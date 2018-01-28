package com.alientome.editors.level;

import com.alientome.core.util.Util;
import com.alientome.core.util.WrappedXML;
import com.alientome.editors.level.registry.Registry;
import com.alientome.editors.level.state.BlockState;
import com.alientome.editors.level.state.EntityState;
import com.alientome.game.GameContext;
import com.alientome.gui.fx.StageManager;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import static com.alientome.core.util.Util.parseXMLNew;

public class LevelEditor {

    private static Stage stage;

    public static void start(GameContext context) throws Exception {

        if (stage == null) {

            stage = new Stage(StageStyle.UNDECORATED);

            stage.setTitle("Alientome Level Editor");

            StageManager manager = new StageManager(stage, context) {
                @Override
                protected void exitInternal() {
                    stage.hide();
                }
            };

            manager.loadAndGetController(ClassLoader.getSystemResource("gui.fxml"), "GUI");

            manager.switchToScene("GUI");

            stage.setMaximized(true);
        }

        stage.show();
    }

    public static void exit() {

        if (stage != null)
            Platform.runLater(stage::close);
    }

    public static void registerBlocks(Registry<BlockState> registry) {

        WrappedXML xml;

        try {
            xml = parseXMLNew("blockStates.xml");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        for (WrappedXML state : xml.nodesWrapped("states/state")) {

            BlockState blockState = BlockState.fromXML(state);

            registry.set(blockState.id + ":" + blockState.metadata, blockState);
        }

        for (WrappedXML tileset : xml.nodesWrapped("states/tileset"))
            registerTileset(registry, tileset);
    }

    private static void registerTileset(Registry<BlockState> registry, WrappedXML tilesetXML) {

        BufferedImage source;

        try (InputStream stream = ClassLoader.getSystemResourceAsStream(tilesetXML.getAttr("path"))) {
            source = ImageIO.read(stream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        String name = tilesetXML.getAttr("name");
        String id = tilesetXML.getAttr("id");
        Dimension d = tilesetXML.getAttrAs("dimension", Util::parseDimension);
        int scale = tilesetXML.getAttrInt("scale");
        int firstMeta = tilesetXML.getAttrInt("firstMeta");
        int cols = tilesetXML.getAttrInt("cols");
        int num = tilesetXML.getAttrInt("num");

        for (int i = 0; i < num; i++) {

            int x = i % cols;
            int y = i / cols;

            int srcX = x * d.width + x;
            int srcY = y * d.height + y;

            BufferedImage image = Util.scale(source.getSubimage(srcX, srcY, d.width, d.height), scale);

            Dimension scaled = new Dimension(d.width * scale, d.height * scale);

            BlockState state = new BlockState(i == 0 ? name : "", id, (byte) (firstMeta + i), new Sprite(image, 0, 0, scaled));
            registry.set(id + ":" + state.metadata, state);
        }
    }

    public static void registerEntities(Registry<EntityState> registry) {

        WrappedXML xml;

        try {
            xml = parseXMLNew("entityStates.xml");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        for (WrappedXML state : xml.nodesWrapped("states/state")) {
            EntityState entityState = EntityState.fromXML(state);

            registry.set(entityState.id, entityState);
        }
    }
}
