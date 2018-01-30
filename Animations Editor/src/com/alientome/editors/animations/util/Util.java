package com.alientome.editors.animations.util;

import com.alientome.editors.animations.SpritesLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;

public class Util {

    public static AnimationID parse(TreePath path) {

        StringBuilder classFullName = new StringBuilder();

        Object[] objects = path.getPath();

        for (int i = 1; i < objects.length; i++) {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) objects[i];

            TreeView view = (TreeView) node.getUserObject();

            if (view.getType() == TreeView.ANIMATION)
                return new AnimationID(classFullName.toString(), view.toString());

            classFullName.append(view.getValue());

            if (i < objects.length - 2)
                classFullName.append('.');
        }

        throw new RuntimeException("Unexpected state");
    }

    public static void drawString(Graphics g, String string, TextAlign horizontalAlign, TextAlign verticalAlign) {

        FontMetrics metrics = g.getFontMetrics();

        Rectangle rect = g.getClipBounds();

        int x = 0;
        int y = 0;

        switch (horizontalAlign) {
            case LEFT:
                break;

            case CENTER:
                x = (rect.width - metrics.stringWidth(string)) / 2;
                break;

            case RIGHT:
                x = (rect.width - metrics.stringWidth(string));
                break;
        }

        switch (verticalAlign) {
            case TOP:
                break;

            case CENTER:
                y = ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
                break;

            case BOTTOM:
                y = rect.height - metrics.getHeight() + metrics.getAscent();
                break;
        }

        g.drawString(string, x, y);
    }

    public static Dimension parseDimension(String string) {

        String[] split = string.split("x");

        int width = Integer.parseInt(split[0]);
        int height = Integer.parseInt(split[1]);

        return new Dimension(width, height);
    }

    public static Element getSubNodeByAttribute(Element parent, String subNodeName, String attributeName, String attributeValue) {

        NodeList children = parent.getElementsByTagName(subNodeName);

        for (int i = 0; i < children.getLength(); i++) {

            Element subNode = (Element) children.item(i);

            if (subNode.getAttribute(attributeName).equals(attributeValue))
                return subNode;
        }

        return null;
    }

    public static void saveXML(Document document, File output) throws TransformerException {

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(output);

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        transformer.transform(source, result);
    }

    public static Component createRigidArea(int width, int height) {
        return Box.createRigidArea(new Dimension(width, height));
    }

    public static JPanel createBoxLayoutPanel(int axis, Component... components) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, axis));

        for (Component component : components)
            panel.add(component);

        return panel;
    }

    public static <T extends Component> T setFontSize(T component, float size) {

        component.setFont(component.getFont().deriveFont(size));

        return component;
    }

    public static boolean isAnimationMissing(DefaultMutableTreeNode treeNode) {

        AnimationID id = parse(new TreePath(treeNode.getPath()));

        return SpritesLoader.getAnimation(id) == null;
    }

    public static File promptChooseAnimationsXML(String dialogTitle, String currentDirectory) {

        JFileChooser animationsChooser = new JFileChooser(currentDirectory);

        animationsChooser.setDialogTitle(dialogTitle);

        animationsChooser.setFileFilter(new FileNameExtensionFilter("XML Animations Files", "xml"));

        int result = animationsChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION)
            return animationsChooser.getSelectedFile();

        return null;
    }
}
