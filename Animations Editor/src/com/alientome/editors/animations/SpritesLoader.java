package com.alientome.editors.animations;

import com.alientome.core.util.Logger;
import com.alientome.editors.animations.dto.AnimationDTO;
import com.alientome.editors.animations.dto.ClassDTO;
import com.alientome.editors.animations.dto.PackageDTO;
import com.alientome.editors.animations.io.ExtAnimationReader;
import com.alientome.editors.animations.io.ExtAnimationWriter;
import com.alientome.editors.animations.util.AnimationID;
import com.alientome.editors.animations.util.Settings;
import com.alientome.editors.animations.util.TreeView;
import com.alientome.editors.animations.util.Util;
import com.alientome.visual.animations.Animation;
import com.alientome.visual.animations.AnimationImpl;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.alientome.core.util.Util.parseXML;
import static com.alientome.editors.animations.util.Util.*;

public class SpritesLoader {

    private static final Logger log = Logger.get();
    private static final Map<String, LinkedHashMap<String, ExtAnimation>> animations = new HashMap<>();
    private static Settings settings;

    private static void init(String className, String classDirectory, String[] names, Dimension[] dimensions, int[] scales) {

        LinkedHashMap<String, ExtAnimation> animations = new LinkedHashMap<>(names.length);

        for (int i = 0; i < names.length; i++) {

            try (ExtAnimationReader reader = new ExtAnimationReader(new File(getSpritesRoot(), classDirectory + "/" + names[i]))) {

                animations.put(names[i], reader.readAnimation(dimensions[i], scales[i]));

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println(className + " " + names[i]);
            }
        }

        SpritesLoader.animations.put(className, animations);
    }

    public static ExtAnimation getAnimation(AnimationID id) {

        return animations.get(id.classFullName).get(id.animationName);
    }

    public static DefaultTreeModel load() {

        log.i("Loading animations");
        long start = System.nanoTime();

        File animationsXML = getAnimationsXML();

        XML xml;

        try {
            xml = new XMLDocument(animationsXML);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        String rootName = animationsXML.getName();

        TreeView rootView = new TreeView(rootName, rootName, TreeView.ROOT);

        DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode(rootView);

        for (XML packageXML : xml.nodes("animations/package")) {

            Element packageNode = (Element) packageXML.node();

            String packageName = packageNode.getAttribute("name");
            String packageDirectory = packageNode.getAttribute("directory");

            TreeView packageView = new TreeView(packageName.substring(packageName.lastIndexOf('.') + 1), packageName, TreeView.PACKAGE);

            DefaultMutableTreeNode treePackage = new DefaultMutableTreeNode(packageView);

            for (XML classXML : packageXML.nodes("class")) {

                Element classNode = (Element) classXML.node();

                String className = classNode.getAttribute("name");
                String classFullName = packageName + "." + className;
                String classDirectory = packageDirectory + "/" + classNode.getAttribute("subDirectory");

                TreeView classView = new TreeView(className, className, TreeView.CLASS);

                DefaultMutableTreeNode treeClass = new DefaultMutableTreeNode(classView);

                List<XML> animations = classXML.nodes("animation");
                String[] names = new String[animations.size()];
                Dimension[] dimensions = new Dimension[names.length];
                int[] scales = new int[names.length];

                for (int i = 0; i < animations.size(); i++) {

                    Element animationNode = (Element) animations.get(i).node();

                    String animationName = animationNode.getAttribute("name");

                    TreeView animationView = new TreeView(animationName, animationName, TreeView.ANIMATION);

                    DefaultMutableTreeNode treeAnimation = new DefaultMutableTreeNode(animationView);

                    String dimension = animationNode.getAttribute("dimension");

                    dimensions[i] = parseDimension(dimension);

                    String scale = animationNode.getAttribute("scale");
                    if (scale.length() == 0)
                        scales[i] = 1;
                    else
                        scales[i] = Integer.parseInt(scale);

                    names[i] = animationName;

                    treeClass.add(treeAnimation);
                }

                init(classFullName, classDirectory, names, dimensions, scales);

                treePackage.add(treeClass);
            }

            treeRoot.add(treePackage);
        }

        double elapsed = (System.nanoTime() - start) / 1_000_000d;
        log.i("Loaded animations in " + elapsed + "ms");

        return new DefaultTreeModel(treeRoot);
    }

    public static ExtAnimation reload(TreePath path) {

        if (path == null) return null;

        log.i("Reloading node " + path);
        long start = System.nanoTime();

        File animationsXML = getAnimationsXML();
        File spritesRoot = getSpritesRoot();

        AnimationID id = Util.parse(path);

        String packageName = id.classFullName.substring(0, id.classFullName.lastIndexOf('.'));
        String className = id.classFullName.substring(id.classFullName.lastIndexOf('.') + 1);

        XML xml;

        try {
            xml = new XMLDocument(animationsXML);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        XML packageXML = xml.nodes("animations/package[@name='" + packageName + "']").get(0);

        String packageDirectory = ((Element) packageXML.node()).getAttribute("directory");

        XML classXML = packageXML.nodes("class[@name='" + className + "']").get(0);

        String classDirectory = packageDirectory + "/" + ((Element) classXML.node()).getAttribute("subDirectory");

        XML animationXML = packageXML.nodes("animation[@name='" + id.animationName + "']").get(0);

        Element animationNode = (Element) animationXML.node();

        Dimension dimension = parseDimension(animationNode.getAttribute("dimension"));

        String scaleStr = animationNode.getAttribute("scale");
        int scale;
        if (scaleStr.length() == 0)
            scale = 1;
        else
            scale = Integer.parseInt(scaleStr);

        String animationPath = classDirectory + "/" + id.animationName;

        try (ExtAnimationReader reader = new ExtAnimationReader(new File(spritesRoot, animationPath))) {

            ExtAnimation animation = reader.readAnimation(dimension, scale);

            animations.get(id.classFullName).put(id.animationName, animation);

            double elapsed = (System.nanoTime() - start) / 1_000_000d;
            log.i("Reloaded node in " + elapsed + "ms");
            return animation;

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void addNewPackage(DefaultTreeModel model, DefaultMutableTreeNode treeParent, PackageDTO info) {

        System.out.println("Adding " + info);

        Element root;
        Document document;

        File animationsXML = getAnimationsXML();
        File spritesRoot = getSpritesRoot();

        try {
            root = parseXML(animationsXML);
            document = root.getOwnerDocument();
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException("Unable to add package", e);
        }

        Element packageNode = getSubNodeByAttribute(root, "package", "name", info.packageName);

        if (packageNode == null) {

            packageNode = document.createElement("package");
            packageNode.setAttribute("name", info.packageName);
            packageNode.setAttribute("directory", info.packageDirectory);

            root.appendChild(packageNode);

            TreeView packageView = new TreeView(info.packageName.substring(info.packageName.lastIndexOf('.') + 1), info.packageName, TreeView.PACKAGE);

            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(packageView);

            treeParent.add(treeNode);

            model.insertNodeInto(treeNode, treeParent, treeParent.getChildCount() - 1);

            File directory = new File(spritesRoot, info.packageDirectory);
            if (!directory.mkdir())
                System.err.println("Package directory couldn't be created");

            try {
                saveXML(document, animationsXML);
            } catch (TransformerException e) {
                throw new RuntimeException("Couldn't save xml file", e);
            }

        } else throw new IllegalArgumentException("Package already exists");
    }

    public static void addNewClass(DefaultTreeModel model, DefaultMutableTreeNode treeParent, ClassDTO info) {

        System.out.println("Adding " + info);

        Element root;
        Document document;

        File animationsXML = getAnimationsXML();
        File spritesRoot = getSpritesRoot();

        try {
            root = parseXML(animationsXML);
            document = root.getOwnerDocument();
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException("Unable to add class", e);
        }

        Element packageNode = getSubNodeByAttribute(root, "package", "name", info.packageName);

        assert packageNode != null;

        String packageDirectory = packageNode.getAttribute("directory");

        Element classNode = getSubNodeByAttribute(packageNode, "class", "name", info.className);

        if (classNode == null) {

            classNode = document.createElement("class");
            classNode.setAttribute("name", info.className);
            classNode.setAttribute("subDirectory", info.classSubDirectory);

            packageNode.appendChild(classNode);

            TreeView classView = new TreeView(info.className, info.className, TreeView.CLASS);

            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(classView);

            treeParent.add(treeNode);

            model.insertNodeInto(treeNode, treeParent, treeParent.getChildCount() - 1);

            File directory = new File(spritesRoot, packageDirectory + "/" + info.classSubDirectory);
            if (!directory.mkdir())
                System.err.println("Class directory couldn't be created");

            try {
                saveXML(document, animationsXML);
            } catch (TransformerException e) {
                throw new RuntimeException("Couldn't save xml file", e);
            }

        } else throw new IllegalArgumentException("Class already exists");
    }

    public static void addNewAnimation(DefaultTreeModel model, DefaultMutableTreeNode treeParent, AnimationDTO info) {

        System.out.println("Adding " + info);

        Element root;
        Document document;

        File animationsXML = getAnimationsXML();
        File spritesRoot = getSpritesRoot();

        try {
            root = parseXML(animationsXML);
            document = root.getOwnerDocument();
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException("Unable to add animation", e);
        }

        Element packageNode = getSubNodeByAttribute(root, "package", "name", info.packageName);
        assert packageNode != null;

        String packageDirectory = packageNode.getAttribute("directory");

        Element classNode = getSubNodeByAttribute(packageNode, "class", "name", info.className);
        assert classNode != null;

        String classDirectory = packageDirectory + "/" + classNode.getAttribute("subDirectory");

        NodeList animationNodes = classNode.getElementsByTagName("animation");
        int length = animationNodes.getLength();

        for (int i = 0; i < length; i++) {

            Element animationNode = (Element) animationNodes.item(i);

            if (animationNode.getAttribute("name").equals(info.animationName))
                throw new IllegalArgumentException("Animation already exists");
        }

        Element animationNode = document.createElement("animation");
        animationNode.setAttribute("name", info.animationName);

        String dimension = info.dimension.width + "x" + info.dimension.height;

        animationNode.setAttribute("dimension", dimension);
        animationNode.setAttribute("scale", String.valueOf(info.scale));

        classNode.appendChild(animationNode);

        TreeView animationView = new TreeView(info.animationName, length, TreeView.ANIMATION);

        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(animationView);

        treeParent.add(treeNode);

        model.insertNodeInto(treeNode, treeParent, treeParent.getChildCount() - 1);

        File file = new File(spritesRoot, classDirectory + "/" + info.animationName);

        ExtAnimation animation = newAnimation(info, file);

        String classFullName = info.packageName + '.' + info.className;

        animations.computeIfAbsent(classFullName, s -> new LinkedHashMap<>()).put(info.animationName, animation);

        try (ExtAnimationWriter writer = new ExtAnimationWriter(file)) {

            writer.writeAnimation(animation);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        try {
            saveXML(document, animationsXML);
        } catch (TransformerException e) {
            throw new RuntimeException("Couldn't save xml file", e);
        }
    }

    private static ExtAnimation newAnimation(AnimationDTO info, File source) {

        BufferedImage[] sprites = new BufferedImage[info.length];
        int[] xOffsets = new int[info.length];
        int[] yOffsets = new int[info.length];

        for (int i = 0; i < info.length; i++) {
            sprites[i] = new BufferedImage(info.dimension.width, info.dimension.height, BufferedImage.TYPE_INT_ARGB);
        }

        Animation animation = new AnimationImpl(sprites, info.delay, xOffsets, yOffsets, info.loop);

        return new ExtAnimation(source, animation, info.dimension, info.scale);
    }

    public static void deletePackage(DefaultTreeModel model, DefaultMutableTreeNode selectedNode) {

        TreeView packageView = (TreeView) selectedNode.getUserObject();

        System.out.println("Removing " + packageView.getValue());

        Element root;
        Document document;

        File animationsXML = getAnimationsXML();
        File spritesRoot = getSpritesRoot();

        try {
            root = parseXML(animationsXML);
            document = root.getOwnerDocument();
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException("Unable to add package", e);
        }

        Element packageNode = getSubNodeByAttribute(root, "package", "name", (String) packageView.getValue());

        assert packageNode != null;

        root.removeChild(packageNode);

        model.removeNodeFromParent(selectedNode);

        File directory = new File(spritesRoot, packageNode.getAttribute("directory"));

        if (!directory.delete())
            System.err.println("Couldn't delete directory");

        try {
            saveXML(document, animationsXML);
        } catch (TransformerException e) {
            throw new RuntimeException("Couldn't save xml file", e);
        }
    }

    public static void deleteClass(DefaultTreeModel model, DefaultMutableTreeNode selectedNode) {

        TreeView classView = (TreeView) selectedNode.getUserObject();

        System.out.println("Removing " + classView.getValue());

        Element root;
        Document document;

        File animationsXML = getAnimationsXML();
        File spritesRoot = getSpritesRoot();

        try {
            root = parseXML(animationsXML);
            document = root.getOwnerDocument();
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException("Unable to add package", e);
        }

        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();

        TreeView packageView = (TreeView) parentNode.getUserObject();

        Element packageNode = getSubNodeByAttribute(root, "package", "name", (String) packageView.getValue());

        assert packageNode != null;

        Element classNode = getSubNodeByAttribute(packageNode, "class", "name", (String) classView.getValue());

        assert classNode != null;

        packageNode.removeChild(classNode);

        model.removeNodeFromParent(selectedNode);

        File directory = new File(spritesRoot, packageNode.getAttribute("directory") + "/" + classNode.getAttribute("subDirectory"));

        if (!directory.delete())
            System.err.println("Couldn't delete directory");

        try {
            saveXML(document, animationsXML);
        } catch (TransformerException e) {
            throw new RuntimeException("Couldn't save xml file", e);
        }
    }

    public static void deleteAnimation(DefaultTreeModel model, DefaultMutableTreeNode selectedNode) {

        TreeView animationView = (TreeView) selectedNode.getUserObject();

        System.out.println("Removing " + animationView);

        Element root;
        Document document;

        File animationsXML = getAnimationsXML();
        File spritesRoot = getSpritesRoot();

        try {
            root = parseXML(animationsXML);
            document = root.getOwnerDocument();
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException("Unable to add package", e);
        }

        AnimationID id = Util.parse(new TreePath(selectedNode.getPath()));

        String packageName = id.classFullName.substring(0, id.classFullName.lastIndexOf('.'));
        String className = id.classFullName.substring(id.classFullName.lastIndexOf('.') + 1);

        Element packageNode = getSubNodeByAttribute(root, "package", "name", packageName);

        assert packageNode != null;

        Element classNode = getSubNodeByAttribute(packageNode, "class", "name", className);

        assert classNode != null;

        Element animationNode = getSubNodeByAttribute(classNode, "animation", "name", id.animationName);

        assert animationNode != null;

        classNode.removeChild(animationNode);

        model.removeNodeFromParent(selectedNode);

        animations.get(id.classFullName).remove(id.animationName);

        File file = new File(spritesRoot, packageNode.getAttribute("directory") + "/" + classNode.getAttribute("subDirectory") + "/" + id.animationName);

        if (!file.delete())
            System.err.println("Couldn't delete file");

        try {
            saveXML(document, animationsXML);
        } catch (TransformerException e) {
            throw new RuntimeException("Couldn't save xml file", e);
        }
    }

    private static File getAnimationsXML() {
        return settings.getFile("animationsXMLPath");
    }

    private static File getSpritesRoot() {
        return settings.getFile("spritesRootPath");
    }

    public static void register(Settings settings) {
        SpritesLoader.settings = settings;
    }
}
