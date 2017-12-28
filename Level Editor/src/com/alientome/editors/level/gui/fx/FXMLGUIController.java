package com.alientome.editors.level.gui.fx;

import com.alientome.core.SharedInstances;
import com.alientome.core.events.GameEventDispatcher;
import com.alientome.core.events.QuitRequestEvent;
import com.alientome.editors.level.Level;
import com.alientome.editors.level.LevelEditor;
import com.alientome.editors.level.SpritesLoader;
import com.alientome.editors.level.background.Background;
import com.alientome.editors.level.background.Layer;
import com.alientome.editors.level.gui.fx.dialogs.DialogsUtil;
import com.alientome.editors.level.registry.EditorRegistry;
import com.alientome.editors.level.state.*;
import com.alientome.editors.level.util.Colors;
import com.alientome.editors.level.util.StateStack;
import com.alientome.game.collisions.StaticBoundingBox;
import com.alientome.game.events.GameExitEvent;
import com.alientome.game.events.GameStartEvent;
import com.alientome.game.level.LevelLoader;
import com.alientome.game.level.LevelManager;
import com.alientome.gui.fx.controllers.FXMLController;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.alientome.core.SharedNames.DISPATCHER;
import static com.alientome.core.SharedNames.LOADER;
import static com.alientome.core.events.GameEventType.QUIT_REQUEST;
import static com.alientome.editors.level.gui.fx.dialogs.DialogsUtil.*;
import static com.alientome.editors.level.util.Util.invokeAndWait;
import static com.alientome.gui.fx.DialogsUtil.showErrorDialog;

public class FXMLGUIController extends FXMLController {

    private static final int BLOCKS = 0, ENTITIES = 1, SCRIPTS = 2;
    private final EditorRegistry registry = new EditorRegistry();
    private final int[] tempScriptCoordinates = {-1, -1, -1, -1};
    private StateStack undoStack, redoStack;
    private Tool tool = Tool.MOUSE;
    private boolean muteListeners = false;
    private boolean needsSave = false;
    private File lastDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
    private File lastDirectoryImage = FileSystemView.getFileSystemView().getHomeDirectory();
    private Level level;
    private Entity selectedEntity;
    private ScriptObject selectedScript;
    private Layer selectedLayer;
    private Rectangle selection;
    private boolean showGrid = true;
    private BufferedImage minimapImage;
    private BufferedImage buffer;
    private WritableImage image;
    private BufferedImage backgroundBuffer;
    private WritableImage backgroundImage;

    public FXMLGUIController() {

        SpritesLoader.load();

        LevelEditor.registerBlocks(registry.getBlocksRegistry());
        LevelEditor.registerEntities(registry.getEntitiesRegistry());
    }

    private void beforeModification(String name) {

        LevelState state = level.copyState(name);

        undoStack.push(state);
        redoStack.clear();

        needsSave = true;
    }

    private void clearStacks() {

        undoStack.clear();
        redoStack.clear();
    }

    private void paintLevel() {

        if (buffer != null) {

            Graphics2D g = buffer.createGraphics();

            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

            if (level != null)
                level.draw(g, showGrid);

            if (selection != null) {

                g.setColor(Colors.SELECTION);
                int x = selection.x, y = selection.y, width = selection.width, height = selection.height;
                if (width <= 0) {
                    x = x + width;
                    width = -width + 1;
                }

                if (height <= 0) {
                    y = y + height;
                    height = -height + 1;
                }

                g.fillRect(x * BlockState.WIDTH, y * BlockState.WIDTH, width * BlockState.WIDTH, height * BlockState.WIDTH);
            }

            int[] t = tempScriptCoordinates;

            boolean b = true;
            for (int i = 0; i < 4; i++)
                if (t[i] < 0)
                    b = false;

            if (b) {
                g.setColor(Colors.SCRIPT_TEMP);
                int x = t[0], y = t[1], width = t[2] - x, height = t[3] - y;

                if (width <= 0) {
                    x = x + width;
                    width = -width;
                }

                if (height <= 0) {
                    y = y + height;
                    height = -height;
                }

                g.drawRect(x, y, width, height);
            }

            image = SwingFXUtils.toFXImage(buffer, image);

            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.getGraphicsContext2D().drawImage(image, 0, 0);

            minimap.setImage(SwingFXUtils.toFXImage(minimapImage, null));
        }
    }

    private void actualizeView() {

        int pxWidth;
        int pxHeight;

        if (level != null) {
            pxWidth = level.getWidth() * BlockState.WIDTH + 1;
            pxHeight = level.getHeight() * BlockState.WIDTH + 1;

            buffer = new BufferedImage(pxWidth, pxHeight, BufferedImage.TYPE_INT_ARGB);
            minimapImage = level.getMinimap();
        } else {
            pxWidth = pxHeight = 0;
            buffer = null;
            minimapImage = null;
            minimap.setImage(null);
        }

        canvas.setWidth(pxWidth);
        canvas.setHeight(pxHeight);

        canvas.getGraphicsContext2D().clearRect(0, 0, pxWidth, pxHeight);

        image = null;

        paintLevel();
    }

    private void setAllDisable(boolean b) {

        tabPane.setDisable(b);
        backgroundTab.setDisable(b);

        close.setDisable(b);
        save.setDisable(b);
        saveAs.setDisable(b);

        resize.setDisable(b);
        play.setDisable(b);
    }

    private boolean promptUnsaved() {

        boolean shouldAct = false;

        if (needsSave) {

            int action = showUnsavedChangesDialog();

            if (action == SAVE)
                save();
            else if (action == DISCARD)
                shouldAct = true;
        }

        return shouldAct || !needsSave;
    }

    private void cleanup() {

        needsSave = false;

        selection = null;
        Arrays.fill(tempScriptCoordinates, -1);

        selectEntity(null);
        selectScript(null);
        selectLayer(null);

        clearStacks();
    }

    private void setLevel(Level level) {

        this.level = level;

        cleanup();

        setAllDisable(false);

        actualizeView();

        Background lvlBg = level.getBackground();
        backgroundScale.setText(String.valueOf(lvlBg.scale));
        backgroundYOffset.setText(String.valueOf(lvlBg.yOffset));
    }

    private void newLevel() {

        if (promptUnsaved()) {

            int[] result = showNewLevelDialog();

            if (result != null)
                setLevel(new Level(registry, scripts.getItems(), layers.getItems(), result[0], result[1]));
        }
    }

    private void open() {

        if (promptUnsaved()) {

            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(lastDirectory);
            chooser.setTitle("Open Level");
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Alientome Level Files", "*.lvl"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            File selected = chooser.showOpenDialog(manager.getStage());
            if (selected != null) {

                lastDirectory = selected.getParentFile();

                try {
                    setLevel(new Level(registry, scripts.getItems(), layers.getItems(), selected));
                } catch (IOException e) {
                    e.printStackTrace();
                    showErrorDialog(e);
                }
            }
        }
    }

    private void close() {

        if (promptUnsaved())
            closeInternal();
    }

    private void closeInternal() {

        level = null;

        cleanup();

        setAllDisable(true);

        actualizeView();

        scripts.getItems().clear();
        layers.getItems().clear();

        backgroundScale.setText("");
        backgroundYOffset.setText("");
    }

    private void save() {

        if (level.canSave()) {
            try {
                level.save();
                needsSave = false;
            } catch (IOException e) {
                e.printStackTrace();
                showErrorDialog(e);
            }
        } else saveAs();
    }

    private void saveAs() {

        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(lastDirectory);
        chooser.setTitle("Save Level as");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Alientome Level Files", "*.lvl"));
        if (level.canSave())
            chooser.setInitialFileName(level.getSource().getName());
        File selected = chooser.showSaveDialog(manager.getStage());
        if (selected != null) {

            lastDirectory = selected.getParentFile();

            try {
                level.saveTo(selected);
                level.setSource(selected);
                needsSave = false;
            } catch (IOException e) {
                e.printStackTrace();
                showErrorDialog(e);
            }
        }
    }

    private void exit() {

        if (promptUnsaved()) {
            closeInternal();
            manager.exit();
        }
    }

    private void stateOp(StateStack from, StateStack to) {

        LevelState state = from.pop();

        to.push(level.copyState(state.name));

        int index = scripts.getSelectionModel().getSelectedIndex();

        level.setState(state);

        scripts.getSelectionModel().select(index);

        if (selectedEntity != null) {
            int[] pos = selectedEntity.getScreenCoordinates();
            selectEntity(level.getEntityAt(pos[0], pos[1]));
        }

        actualizeView();
    }

    private void undo() {

        stateOp(undoStack, redoStack);
    }

    private void redo() {

        stateOp(redoStack, undoStack);
    }

    private void grid() {

        showGrid = grid.isSelected();

        paintLevel();
    }

    private void resize() {

        int[] result = showResizeDialog(level);

        if (result != null) {

            beforeModification("resize");

            level.resizeTo(result[0], result[1], result[2], result[3]);

            actualizeView();
        }
    }

    private void play() {

        if (level.canPlay()) {

            LevelLoader loader = SharedInstances.get(LOADER);
            GameEventDispatcher dispatcher = SharedInstances.get(DISPATCHER);

            try {

                File tmp = level.getTempDirectory().toFile();

                level.saveToTemp();

                dispatcher.submit(new GameExitEvent());

                LevelManager manager = level.canSave() ? loader.loadFrom(tmp, level.getSource()) : loader.loadFrom(tmp);

                dispatcher.submit(new GameStartEvent(manager));

            } catch (IOException e) {
                e.printStackTrace();
                showErrorDialog(e);
            }
        } else {
            //TODO show error
        }
    }

    private void selectEntity(Entity e) {

        if (e != null)
            tabPane.getSelectionModel().select(ENTITIES);

        if (e == selectedEntity) return;

        selectedEntity = e;

        muteListeners = true;

        int maxRow = getMaxIndex(tags);
        if (maxRow > 3)
            deleteRows(tags, 3, maxRow);

        tagName.setText("");
        tagValue.setText("");

        if (e == null) {

            tagsLabel.setText("Tags :");
            tags.setDisable(true);

            orientation.getSelectionModel().select(0);
            offsets.setText("0;0");
        } else {

            tagsLabel.setText("Tags : " + e.state.name);
            tags.setDisable(false);

            orientation.getSelectionModel().select(e.tags.get("orientation", "RIGHT"));
            offsets.setText(e.tags.get("offsets", "0;0"));

            for (Map.Entry<String, String> entry : e.tagsMap.entrySet()) {

                if (entry.getKey().equals("orientation") || entry.getKey().equals("offsets"))
                    continue;

                createTagRow(entry.getKey(), entry.getValue());
            }
        }

        muteListeners = false;
    }

    private void selectScript(ScriptObject s) {

        if (s != null)
            tabPane.getSelectionModel().select(SCRIPTS);

        if (s == selectedScript) return;

        selectedScript = s;

        muteListeners = true;

        scripts.getSelectionModel().select(s);

        if (s == null) {

            editScript.setDisable(true);
            deleteScript.setDisable(true);
            toggleEnabledScript.setDisable(true);
            scriptContent.setDisable(true);

            toggleEnabledScript.setText("Disable");

            scriptID.setText("");
            scriptBounds.setText("0; 0 -> 0; 0");
            scriptAffected.setText("");
            scriptContent.setText("");

        } else {

            editScript.setDisable(false);
            deleteScript.setDisable(false);
            toggleEnabledScript.setDisable(false);
            scriptContent.setDisable(false);

            toggleEnabledScript.setText(s.enabled ? "Disable" : "Enable");

            scriptID.setText(s.id);
            scriptBounds.setText(s.getBounds());
            scriptAffected.setText(s.affected);
            scriptContent.setText(s.content);
        }

        muteListeners = false;
    }

    private void selectLayer(Layer l) {

        if (l == selectedLayer) return;

        selectedLayer = l;

        muteListeners = true;

        layers.getSelectionModel().select(l);

        if (l == null) {

            editLayer.setDisable(true);

            layerName.setText("");
            layerXCoef.setText("");
            layerYCoef.setText("");
        } else {

            editLayer.setDisable(false);

            layerName.setText(l.name);
            layerXCoef.setText(String.valueOf(l.xCoef));
            layerYCoef.setText(String.valueOf(l.yCoef));
        }

        muteListeners = false;
    }

    @Override
    public void init(Scene scene) {

        GameEventDispatcher dispatcher = SharedInstances.get(DISPATCHER);
        dispatcher.register(QUIT_REQUEST, e ->
                invokeAndWait(() -> {
                    if (!promptUnsaved())
                        ((QuitRequestEvent) e).cancel();
        }));

        blocks.setCellFactory(param -> new BlockStateListCell());
        blocks.getItems().addAll(registry.getBlocksRegistry().values());

        entities.setCellFactory(param -> new EntityStateListCell());
        entities.getItems().addAll(registry.getEntitiesRegistry().values());

        scripts.setCellFactory(param -> new ScriptObjectListCell());
        scripts.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onScriptActionInternal(scripts));

        layers.setCellFactory(param -> new LayerListCell());
        layers.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onBackgroundActionInternal(layers));

        toolToggle.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                toolToggle.selectToggle(oldValue);
        });

        backgroundBuffer = new BufferedImage((int) background.getWidth(), (int) background.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Transition transition = new Transition() {

            {
                setCycleDuration(Duration.seconds(5));
            }

            @Override
            protected void interpolate(double frac) {

                if (level == null)
                    return;

                Background bg = level.getBackground();

                if (bg == null)
                    return;

                Graphics2D g = backgroundBuffer.createGraphics();

                g.setClip(0, 0, backgroundBuffer.getWidth(), backgroundBuffer.getHeight());

                g.setComposite(AlphaComposite.Clear);
                g.fillRect(0, 0, backgroundBuffer.getWidth(), backgroundBuffer.getHeight());

                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

                bg.draw(g, frac);

                backgroundImage = SwingFXUtils.toFXImage(backgroundBuffer, backgroundImage);

                background.getGraphicsContext2D().clearRect(0, 0, background.getWidth(), background.getHeight());
                background.getGraphicsContext2D().drawImage(backgroundImage, 0, 0);
            }
        };

        transition.setInterpolator(Interpolator.EASE_BOTH);
        transition.setAutoReverse(true);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.play();


        undoStack = new StateStack(undo, "Undo");
        redoStack = new StateStack(redo, "Redo");
    }

    @FXML
    private MenuItem newLevel, open, close, save, saveAs, exit;

    @FXML
    private MenuItem undo, redo;

    @FXML
    private CheckMenuItem grid;

    @FXML
    private MenuItem resize, play;

    @FXML
    private TabPane tabPane;

    @FXML
    private ListView<BlockState> blocks;

    @FXML
    private ListView<EntityState> entities;

    @FXML
    private ListView<ScriptObject> scripts;

    @FXML
    private ImageView minimap;

    @FXML
    private Canvas canvas;

    @FXML
    private ToggleButton mouse, pencil, eraser;

    @FXML
    private ToggleGroup toolToggle;

    @FXML
    private GridPane tags;

    @FXML
    private Label tagsLabel;

    @FXML
    private ComboBox<String> orientation;

    @FXML
    private TextField offsets;

    @FXML
    private TextField tagName, tagValue;

    @FXML
    private Button addTag;

    @FXML
    private GridPane editScript;

    @FXML
    private Button newScript, deleteScript, toggleEnabledScript;

    @FXML
    private Button scriptBounds;

    @FXML
    private TextField scriptID, scriptAffected;

    @FXML
    private TextArea scriptContent;

    @FXML
    private HBox backgroundTab;

    @FXML
    private Canvas background;

    @FXML
    private TextField backgroundScale, backgroundYOffset;

    @FXML
    private Button addLayer, deleteLayer;

    @FXML
    private ListView<Layer> layers;

    @FXML
    private GridPane editLayer;

    @FXML
    private TextField layerName, layerXCoef, layerYCoef;

    @FXML
    private void onMenuItemAction(ActionEvent e) {

        Object s = e.getSource();

        if (s == newLevel) newLevel();
        else if (s == open) open();
        else if (s == close) close();
        else if (s == save) save();
        else if (s == saveAs) saveAs();
        else if (s == exit) exit();
        else if (s == undo) undo();
        else if (s == redo) redo();
        else if (s == grid) grid();
        else if (s == resize) resize();
        else if (s == play) play();
    }

    @FXML
    private void onToolAction(ActionEvent e) {

        Object s = e.getSource();

        if (s == mouse) tool = Tool.MOUSE;
        else if (s == pencil) tool = Tool.PENCIL;
        else if (s == eraser) tool = Tool.ERASER;
    }

    @FXML
    private void onTagAction(ActionEvent e) {

        if (selectedEntity == null || muteListeners)
            return;

        Object s = e.getSource();

        if (s == orientation) {
            beforeModification("set tag");
            selectedEntity.tagsMap.put("orientation", orientation.getSelectionModel().getSelectedItem());
        } else if (s == offsets) {
            beforeModification("set tag");
            selectedEntity.tagsMap.put("offsets", offsets.getText());
        } else if (s == addTag) {

            String name = tagName.getText();
            String val = tagValue.getText();

            if (name.isEmpty() || val.isEmpty())
                return;

            if (selectedEntity.tagsMap.containsKey(name))
                return;

            beforeModification("add tag");

            selectedEntity.tagsMap.put(name, val);

            createTagRow(name, val);

        } else return;

        paintLevel();
    }

    @FXML
    private void onScriptAction(ActionEvent e) {

        onScriptActionInternal(e.getSource());
    }

    @FXML
    private void onScriptTextInputKeyReleased(KeyEvent e) {

        onScriptActionInternal(e.getSource());
    }

    private void onScriptActionInternal(Object s) {

        if (muteListeners)
            return;

        if (s == scripts)
            selectScript(scripts.getSelectionModel().getSelectedItem());
        else if (s == newScript) {
            beforeModification("create script");
            ScriptObject newScript = new ScriptObject(null, true, new StaticBoundingBox(0, 0, 0, 0), "", "");
            scripts.getItems().add(newScript);
            selectScript(newScript);
        } else if (selectedScript != null) {
            if (s == deleteScript) {
                beforeModification("delete script");
                scripts.getItems().remove(selectedScript);
            } else if (s == toggleEnabledScript) {
                beforeModification("toggle script enabled");
                selectedScript.enabled = !selectedScript.enabled;
                toggleEnabledScript.setText(selectedScript.enabled ? "Disable" : "Enable");
                scripts.refresh();
            } else if (s == scriptID) {
                beforeModification("typing");
                selectedScript.id = scriptID.getText();
                scripts.refresh();
            } else if (s == scriptBounds) {
                mouse.setSelected(true);
                tool = Tool.PICK_BOUNDS;
            } else if (s == scriptAffected) {
                beforeModification("typing");
                selectedScript.affected = scriptAffected.getText();
                scripts.refresh();
            } else if (s == scriptContent) {
                beforeModification("typing");
                selectedScript.content = scriptContent.getText();
                scripts.refresh();
            } else return;
        } else return;

        paintLevel();
    }

    @FXML
    private void onBackgroundAction(ActionEvent e) {

        onBackgroundActionInternal(e.getSource());
    }

    private void onBackgroundActionInternal(Object s) {

        if (muteListeners)
            return;

        if (s == layers)
            selectLayer(layers.getSelectionModel().getSelectedItem());
        else if (s == addLayer) {
            File img = DialogsUtil.showNewLayerDialog(manager.getStage(), lastDirectoryImage);
            if (img != null) {
                beforeModification("add layer");
                String name = img.getName();
                try {
                    Files.copy(img.toPath(), level.getTempDirectory().resolve(name), StandardCopyOption.REPLACE_EXISTING);
                    Layer l = new Layer(name, ImageIO.read(img), 0, 0);
                    layers.getItems().add(l);
                    selectLayer(l);
                    lastDirectoryImage = img.getParentFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    showErrorDialog(e);
                }
            }
        } else if (s == backgroundScale) {
            beforeModification("set background scale");
            level.getBackground().scale = Integer.parseInt(backgroundScale.getText());
        } else if (s == backgroundYOffset) {
            beforeModification("set background y offset");
            level.getBackground().yOffset = Integer.parseInt(backgroundYOffset.getText());
        } else if (selectedLayer != null) {
            if (s == deleteLayer) {
                beforeModification("delete layer");
                try {
                    Files.delete(level.getTempDirectory().resolve(selectedLayer.name));
                    layers.getItems().remove(selectedLayer);
                } catch (IOException e) {
                    e.printStackTrace();
                    showErrorDialog(e);
                }
            } else if (s == layerName) {
                beforeModification("set layer name");
                try {
                    String newName = layerName.getText();
                    Path currentPath = level.getTempDirectory().resolve(selectedLayer.name);
                    Files.move(currentPath, currentPath.resolveSibling(newName));
                    selectedLayer.name = newName;
                    layers.refresh();
                } catch (IOException e) {
                    e.printStackTrace();
                    showErrorDialog(e);
                }
            } else if (s == layerXCoef) {
                beforeModification("set layer x coefficient");
                selectedLayer.xCoef = Double.parseDouble(layerXCoef.getText());
                layers.refresh();
            } else if (s == layerYCoef) {
                beforeModification("set layer y coefficient");
                selectedLayer.yCoef = Double.parseDouble(layerYCoef.getText());
                layers.refresh();
            }
        }
    }

    private static void deleteRow(GridPane grid, int row) {

        Set<Node> deleteNodes = new HashSet<>();

        for (Node child : grid.getChildren()) {

            Integer rowIndex = GridPane.getRowIndex(child);

            int r = rowIndex == null ? 0 : rowIndex;

            if (r > row)
                GridPane.setRowIndex(child, r - 1);
            else if (r == row)
                deleteNodes.add(child);
        }

        grid.getChildren().removeAll(deleteNodes);
    }

    private static void deleteRows(GridPane grid, int start, int end) {

        int numDelete = end - start;

        Set<Node> deleteNodes = new HashSet<>();

        for (Node child : grid.getChildren()) {

            Integer rowIndex = GridPane.getRowIndex(child);

            int r = rowIndex == null ? 0 : rowIndex;

            if (r >= end)
                GridPane.setRowIndex(child, r - numDelete);
            else if (start <= r && r < end)
                deleteNodes.add(child);
        }

        grid.getChildren().removeAll(deleteNodes);
    }

    private static int getMaxIndex(GridPane grid) {

        return grid.getChildren().stream().mapToInt(n -> {
            Integer row = GridPane.getRowIndex(n);
            Integer rowSpan = GridPane.getRowSpan(n);

            return (row == null ? 0 : row) + (rowSpan == null ? 0 : rowSpan - 1);
        }).max().orElse(-1);
    }

    private void createTagRow(String name, String val) {

        Label label = new Label(name);
        TextField field = new TextField(val);
        field.setId(name);
        field.setOnAction(event -> {
            beforeModification("set tag");
            selectedEntity.tagsMap.put(name, field.getText());
            paintLevel();
        });
        field.setPrefWidth(150);
        field.setMaxWidth(Double.NEGATIVE_INFINITY);
        Button buttonRemove = new Button("-");
        buttonRemove.setPrefWidth(25);

        int maxIndex = getMaxIndex(tags);

        buttonRemove.setOnAction(event -> {
            deleteRow(tags, GridPane.getRowIndex(label));
            beforeModification("remove tag");
            selectedEntity.tagsMap.remove(name);
        });

        GridPane.setRowIndex(tagName, maxIndex + 1);
        GridPane.setRowIndex(tagValue, maxIndex + 1);
        GridPane.setRowIndex(addTag, maxIndex + 1);

        tags.addRow(maxIndex, label, field, buttonRemove);
    }

    private void onEvent(MouseEvent e, boolean dragged) {

        int blockX = (int) e.getX() / BlockState.WIDTH;
        int blockY = (int) e.getY() / BlockState.WIDTH;

        if (level != null && level.checkBounds(blockX, blockY)) {

            if (e.isShiftDown()) {

                if (selection == null)
                    selection = new Rectangle(blockX, blockY, 1, 1);
                else {
                    if (blockX > selection.x) {
                        selection.width = blockX + 1 - selection.x;
                    } else {
                        selection.width = blockX - selection.x;
                    }

                    if (blockY > selection.y) {
                        selection.height = blockY + 1 - selection.y;
                    } else {
                        selection.height = blockY - selection.y;
                    }
                }
            } else {

                if (selection == null) {

                    switch (tool) {

                        case MOUSE:
                            Entity entity = level.getEntityAt((int) e.getX(), (int) e.getY());
                            if (entity != null)
                                selectEntity(entity);
                            else {
                                ScriptObject script = level.getScriptAt((int) e.getX(), (int) e.getY());
                                if (script != null)
                                    selectScript(script);
                                else {
                                    selectEntity(null);
                                    selectScript(null);
                                }
                            }
                            break;

                        case PENCIL:
                            switch (tabPane.getSelectionModel().getSelectedIndex()) {

                                case BLOCKS:
                                    BlockState blockState = blocks.getSelectionModel().getSelectedItem();
                                    if (blockState != null) {
                                        if (!dragged)
                                            beforeModification("set tile");
                                        level.setTile(blockX, blockY, blockState);
                                    }
                                    break;

                                case ENTITIES:
                                    EntityState entityState = entities.getSelectionModel().getSelectedItem();
                                    if (entityState != null) {
                                        if (!dragged)
                                            beforeModification("set entity");
                                        level.addEntity(blockX, blockY, entityState);
                                    }
                                    break;
                            }
                            break;

                        case ERASER:
                            switch (tabPane.getSelectionModel().getSelectedIndex()) {

                                case BLOCKS:
                                    if (!dragged)
                                        beforeModification("erase tile");
                                    level.setTile(blockX, blockY, null);
                                    break;

                                case ENTITIES:
                                    if (!dragged)
                                        beforeModification("erase entity");
                                    level.removeEntity(blockX, blockY);
                                    break;
                            }
                            break;

                        case PICK_BOUNDS:
                            if (dragged) {
                                tempScriptCoordinates[2] = (int) e.getX();
                                tempScriptCoordinates[3] = (int) e.getY();
                                paintLevel();
                            } else {
                                tempScriptCoordinates[0] = (int) e.getX();
                                tempScriptCoordinates[1] = (int) e.getY();
                            }
                            break;
                    }
                }
            }
            paintLevel();
        }
    }

    @FXML
    private void onMousePressed(MouseEvent e) {

        selection = null;

        onEvent(e, false);
    }

    @FXML
    private void onMouseReleased(MouseEvent e) {

        if (tool == Tool.PICK_BOUNDS) {

            beforeModification("set script bounds");
            selectedScript.aabb = new StaticBoundingBox(tempScriptCoordinates[0], tempScriptCoordinates[1], (int) e.getX(), (int) e.getY());

            scripts.refresh();

            scriptBounds.setText(selectedScript.getBounds());

            Arrays.fill(tempScriptCoordinates, -1);
            paintLevel();
            return;
        }

        if (selection != null) {

            int startX = Math.min(selection.x, selection.x + selection.width);
            int startY = Math.min(selection.y, selection.y + selection.height);

            int width = selection.width <= 0 ? -selection.width + 1 : selection.width;
            int height = selection.height <= 0 ? -selection.height + 1 : selection.height;

            switch (tabPane.getSelectionModel().getSelectedIndex()) {

                case BLOCKS:
                    switch (tool) {
                        case PENCIL:
                            BlockState blockState = blocks.getSelectionModel().getSelectedItem();
                            if (blockState != null) {
                                beforeModification("set tiles");
                                for (int x = startX; x < startX + width; x++)
                                    for (int y = startY; y < startY + height; y++)
                                        level.setTile(x, y, blockState);
                            }
                            break;

                        case ERASER:
                            beforeModification("erase tiles");
                            for (int x = startX; x < startX + width; x++)
                                for (int y = startY; y < startY + height; y++)
                                    level.setTile(x, y, null);
                            break;
                    }
                    break;

                case ENTITIES:
                    switch (tool) {
                        case PENCIL:
                            EntityState entityState = entities.getSelectionModel().getSelectedItem();
                            if (entityState != null) {
                                beforeModification("set entities");
                                for (int x = startX; x < startX + width; x++)
                                    for (int y = startY; y < startY + height; y++)
                                        level.addEntity(x, y, entityState);
                            }
                            break;

                        case ERASER:
                            beforeModification("erase entities");
                            for (int x = startX; x < startX + width; x++)
                                for (int y = startY; y < startY + height; y++)
                                    level.removeEntity(x, y);
                            break;
                    }
                    break;
            }

            selection = null;
            paintLevel();
        }
    }

    @FXML
    private void onMouseDragged(MouseEvent e) {
        onEvent(e, true);
    }

    private enum Tool {

        MOUSE,
        PENCIL,
        ERASER,
        PICK_BOUNDS
    }
}