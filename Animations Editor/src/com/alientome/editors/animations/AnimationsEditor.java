package com.alientome.editors.animations;

import com.alientome.editors.animations.gui.EFrame;
import com.alientome.editors.animations.util.Settings;

import javax.swing.tree.DefaultTreeModel;
import java.io.File;

import static com.alientome.editors.animations.util.Util.promptChooseAnimationsXML;

public class AnimationsEditor {

    public static void main(String[] args) throws Exception {

        Thread.currentThread().setName("Thread-Main");

        Settings settings = new Settings(new File(System.getProperty("user.home"), "Alientome/editors/animations/settings.properties"));

        File animationsXMLFile = settings.computeIfAbsent("animationsXMLPath", s -> promptChooseAnimationsXML("Select Animations XML File", null));

        if (animationsXMLFile == null) System.exit(0);

        settings.computeIfAbsent("spritesRootPath", s -> new File(animationsXMLFile.getParent(), "Sprites"));

        SpritesLoader.register(settings);

        DefaultTreeModel model = null;

        while (model == null) {

            try {
                model = SpritesLoader.load();
            } catch (Exception e) {
                e.printStackTrace();
                prompt(settings);
            }
        }

        new EFrame(model, settings).setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread(settings::save, "Thread-Shutdown"));
    }

    private static void prompt(Settings settings) {

        File animationsXMLFile = promptChooseAnimationsXML("Invalid file selected. Select a valid Animations XML File", settings.getString("animationsXMLPath"));

        if (animationsXMLFile == null) System.exit(0);

        settings.setString("animationsXMLPath", animationsXMLFile.getAbsolutePath());

        File spritesRootFile = new File(animationsXMLFile.getParent(), "Sprites");

        settings.setString("spritesRootPath", spritesRootFile.getAbsolutePath());
    }
}
