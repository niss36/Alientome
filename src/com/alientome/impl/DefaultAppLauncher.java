package com.alientome.impl;

import com.alientome.core.AppLauncher;
import com.alientome.core.util.Logger;
import com.alientome.core.util.VersionConflictData;
import com.alientome.game.GameContext;
import com.alientome.game.SpritesLoader;
import com.alientome.game.blocks.Block;
import com.alientome.game.buffs.Buff;
import com.alientome.game.commands.Command;
import com.alientome.game.entities.Entity;
import com.alientome.game.entities.EntityEnemy;
import com.alientome.game.entities.EntityLiving;
import com.alientome.game.entities.EntityProjectile;
import com.alientome.game.events.GamePauseEvent;
import com.alientome.game.events.GameStartEvent;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.alientome.core.events.GameEventType.GAME_START;

public class DefaultAppLauncher extends AppLauncher {

    protected static final Logger log = Logger.get();
    private final GameContext context = new GameContext();
    private VersionConflictData conflictData;

    public DefaultAppLauncher(String[] args) {
        super(args);
    }

    @Override
    public void preInit() throws Exception {

        Thread.currentThread().setName("Thread-Main");

        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.subpixeltext", "false");

        try (InputStream stream = ClassLoader.getSystemResourceAsStream("Font/Alientome.ttf")) {
            Font.loadFont(stream, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        context.setConfig(new DefaultConfig(context,"config.xml", "defaultConfig.txt"));
        context.setDispatcher(new DefaultGameEventDispatcher());
        context.setFileManager(new DefaultFileManager(context, Paths.get(System.getProperty("user.home"), "Alientome")));
        context.setI18N(new DefaultI18N(context,"Lang/lang"));
        context.setInputManager(new DefaultInputManager(context, "keybindings.xml", "defaultKeybindings.txt"));
        context.setSoundManager(new DefaultSoundManager(context));
        context.setLoader(new DefaultLevelLoader(context));
        context.setRegistry(new GameRegistry());
        context.setSaveManager(new DefaultSaveManager(context));
    }

    @Override
    public void init() throws Exception {

        GameRegistry registry = context.getRegistry();

        registerBlocks(registry.getBlocksRegistry());
        registerEntities(registry.getEntitiesRegistry());
        registerBuffs(registry.getBuffsRegistry());
        registerCommands(registry.getCommandsRegistry());

        context.getFileManager().checkFiles();
        conflictData = context.getConfig().load();
        context.getInputManager().load();
        context.getI18N().load();
        context.getSoundManager().load();

        SpritesLoader.register("animations.xml");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (context.getConfig().needsSave()) {
                log.w("Unsaved changes to config");
                context.getConfig().save();
            }
            if (context.getInputManager().needsSave()) {
                log.w("Unsaved changes to key bindings");
                context.getInputManager().save();
            }
            ExecutionTimeProfiler.theProfiler.dumpProfileData();
        }, "Thread-Shutdown"));
    }

    @Override
    public void postInit() throws Exception {
        SpritesLoader.loadAll();
    }

    @Override
    public void start(Application app, Stage stage) throws Exception {

        stage.setTitle("Alientome");
        stage.initStyle(StageStyle.UNDECORATED);

        StageManager manager = new StageManager(stage, context);

        manager.loadAndGetController(ClassLoader.getSystemResource("GUI/main.fxml"), "MAIN");
        manager.loadAndGetController(ClassLoader.getSystemResource("GUI/play.fxml"), "PLAY");
        manager.loadAndGetController(ClassLoader.getSystemResource("GUI/options.fxml"), "OPTIONS");
        manager.loadAndGetController(ClassLoader.getSystemResource("GUI/controls.fxml"), "CONTROLS");
        manager.loadAndGetController(ClassLoader.getSystemResource("GUI/game.fxml"), "GAME");

        if (conflictData != null)
            context.getConfig().resolveConflict(conflictData);

        context.getDispatcher().register(GAME_START, e -> manager.switchToScene("GAME"));

        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && "GAME".equals(manager.getCurrentScene()) && context.getConfig().getAsBoolean("pauseOnLostFocus"))
                context.getDispatcher().submit(new GamePauseEvent());
        });

        List<String> unnamed = arguments.getUnnamed();

        if (unnamed.size() > 0) {

            try {
                Path level = Paths.get(unnamed.get(0));

                log.i("Loading level from " + level);

                context.getDispatcher().submit(new GameStartEvent(context.getLoader().loadFrom(level)));

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
        registry.set("bush", BlockBush.class);
    }

    protected void registerEntities(Registry<Class<? extends Entity>> registry) {

        registry.set("player", EntityAlientome.class);

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
