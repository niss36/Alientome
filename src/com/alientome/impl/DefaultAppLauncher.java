package com.alientome.impl;

import com.alientome.core.AppLauncher;
import com.alientome.core.events.GameEventDispatcher;
import com.alientome.core.internationalization.I18N;
import com.alientome.core.keybindings.InputManager;
import com.alientome.core.settings.Config;
import com.alientome.core.sound.SoundManager;
import com.alientome.core.util.FileManager;
import com.alientome.core.util.Logger;
import com.alientome.game.SpritesLoader;
import com.alientome.game.blocks.Block;
import com.alientome.game.buffs.Buff;
import com.alientome.game.commands.Command;
import com.alientome.game.entities.*;
import com.alientome.game.events.GamePauseEvent;
import com.alientome.game.events.GameStartEvent;
import com.alientome.game.level.LevelLoader;
import com.alientome.game.level.SaveManager;
import com.alientome.game.profiling.ExecutionTimeProfiler;
import com.alientome.game.registry.GameRegistry;
import com.alientome.game.registry.Registry;
import com.alientome.gui.fx.StageManager;
import com.alientome.impl.blocks.*;
import com.alientome.impl.buffs.BuffHeal;
import com.alientome.impl.buffs.BuffShield;
import com.alientome.impl.commands.*;
import com.alientome.impl.entities.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.alientome.core.SharedInstances.get;
import static com.alientome.core.SharedInstances.set;
import static com.alientome.core.SharedNames.*;
import static com.alientome.core.events.GameEventType.GAME_START;

public class DefaultAppLauncher extends AppLauncher {

    protected static final Logger log = Logger.get();

    public DefaultAppLauncher(String[] args) {
        super(args);
    }

    @Override
    public void preInit() {

        Thread.currentThread().setName("Thread-Main");

        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.subpixeltext", "false");

        try (InputStream stream = ClassLoader.getSystemResourceAsStream("Font/Alientome.ttf")) {
            Font.loadFont(stream, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        GameEventDispatcher dispatcher = new DefaultGameEventDispatcher();
        I18N i18N = new DefaultI18N("Lang/lang");
        InputManager inputManager = new DefaultInputManager("keybindings.xml", "defaultKeybindings.txt");
        Config config = new DefaultConfig("config.xml", "defaultConfig.txt");
        FileManager fileManager = new DefaultFileManager(new File(System.getProperty("user.home") + "/Alientome"));
        GameRegistry registry = new GameRegistry();
        SaveManager saveManager = new DefaultSaveManager();
        SoundManager soundManager = new DefaultSoundManager();
        LevelLoader loader = new DefaultLevelLoader();

        set(DISPATCHER, dispatcher);
        set(I18N, i18N);
        set(INPUT_MANAGER, inputManager);
        set(CONFIG, config);
        set(FILE_MANAGER, fileManager);
        set(REGISTRY, registry);
        set(SAVE_MANAGER, saveManager);
        set(SOUND_MANAGER, soundManager);
        set(LOADER, loader);
    }

    @Override
    public void init() {

        I18N i18N = get(I18N);
        InputManager inputManager = get(INPUT_MANAGER);
        Config config = get(CONFIG);
        FileManager fileManager = get(FILE_MANAGER);
        GameRegistry registry = get(REGISTRY);
        SoundManager soundManager = get(SOUND_MANAGER);

        registerBlocks(registry.getBlocksRegistry());
        registerEntities(registry.getEntitiesRegistry());
        registerBuffs(registry.getBuffsRegistry());
        registerCommands(registry.getCommandsRegistry());

        fileManager.checkFiles();
        config.load();
        inputManager.load();
        i18N.load();
        soundManager.load();

        SpritesLoader.register("animations.xml");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            config.save();
            inputManager.save();
            ExecutionTimeProfiler.theProfiler.dumpProfileData();
        }, "Thread-Shutdown"));
    }

    @Override
    public void postInit() {
        SpritesLoader.loadAll();
    }

    @Override
    public void start(Application app, Stage stage) throws Exception {

        stage.setTitle("Alientome");
        stage.initStyle(StageStyle.UNDECORATED);

        StageManager manager = new StageManager(stage);

        manager.loadAndGetController(ClassLoader.getSystemResource("GUI/main.fxml"), "MAIN");
        manager.loadAndGetController(ClassLoader.getSystemResource("GUI/play.fxml"), "PLAY");
        manager.loadAndGetController(ClassLoader.getSystemResource("GUI/options.fxml"), "OPTIONS");
        manager.loadAndGetController(ClassLoader.getSystemResource("GUI/controls.fxml"), "CONTROLS");
        manager.loadAndGetController(ClassLoader.getSystemResource("GUI/game.fxml"), "GAME");

        GameEventDispatcher dispatcher = get(DISPATCHER);
        Config config = get(CONFIG);

        dispatcher.register(GAME_START, e -> Platform.runLater(() -> manager.switchToScene("GAME")));

        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && "GAME".equals(manager.getCurrentScene()) && config.getAsBoolean("pauseOnLostFocus"))
                dispatcher.submit(new GamePauseEvent());
        });

        List<String> unnamed = arguments.getUnnamed();

        if (unnamed.size() > 0) {

            try {

                LevelLoader loader = get(LOADER);

                File levelFile = new File(unnamed.get(0));

                log.i("Loading level from " + levelFile);

                dispatcher.submit(new GameStartEvent(loader.loadFrom(levelFile)));

                manager.switchToScene("GAME");
            } catch (Exception e) {
                log.e("Uncaught exception while loading level from args :");
                e.printStackTrace();
                manager.switchToScene("MAIN");
            }
        } else
            manager.switchToScene("MAIN");

        stage.show();
    }

    protected void registerBlocks(Registry<Class<? extends Block>> registry) {

        registry.set("void", BlockVoid.class);
        registry.set("air", BlockAir.class);
        registry.set("sand", BlockSand.class);
        registry.set("platform_sand", BlockPlatformSand.class);
        registry.set("slope_sand", BlockSlopeSand.class);
        registry.set("spikes", BlockSpikes.class);
    }

    protected void registerEntities(Registry<Class<? extends Entity>> registry) {

        registry.set("player", EntityPlayer.class);

        registry.set("living", EntityLiving.class);

        registry.set("enemy", EntityEnemy.class);
        registry.set("enemyDefault", EntityEnemyDefault.class);
        registry.set("enemyShield", EntityEnemyShield.class);
        registry.set("enemyBow", EntityEnemyBow.class);
        registry.set("enemyWizard", EntityEnemyWizard.class);

        registry.set("projectile", EntityProjectile.class);
        registry.set("ghostBall", EntityGhostBall.class);
        registry.set("arrow", EntityArrow.class);
    }

    protected void registerBuffs(Registry<Class<? extends Buff>> registry) {

        registry.set("heal", BuffHeal.class);
        registry.set("shield", BuffShield.class);
    }

    protected void registerCommands(List<Command> registry) {

        registry.add(new CommandTeleport());
        registry.add(new CommandFly());
        registry.add(new CommandSetCamera());
        registry.add(new CommandSetControlled());
        registry.add(new CommandHeal());
        registry.add(new CommandDamage());
        registry.add(new CommandKill());
    }
}
