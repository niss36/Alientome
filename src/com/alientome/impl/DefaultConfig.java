package com.alientome.impl;

import com.alientome.core.Context;
import com.alientome.core.internationalization.I18N;
import com.alientome.core.settings.AbstractConfig;
import com.alientome.core.util.*;
import com.alientome.game.GameContext;
import com.alientome.gui.fx.DialogsUtil;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static com.alientome.gui.fx.DialogsUtil.*;

public class DefaultConfig extends AbstractConfig {

    private final String configXML;
    private final String defaultConfig;

    public DefaultConfig(Context context, String configXML, String defaultConfig) {

        super(context);

        this.configXML = configXML;
        this.defaultConfig = defaultConfig;
    }

    @Override
    protected InputStream defaultConfig() {
        return ClassLoader.getSystemResourceAsStream(defaultConfig);
    }

    @Override
    protected Path userConfig() {
        return context.getFileManager().getConfig();
    }

    @Override
    protected WrappedXML getXML() throws IOException {
        return Util.parseXMLNew(configXML);
    }

    @Override
    public void resolveConflict(VersionConflictData data) {

        String gameVersion = data.gameVersion;
        String userVersion = data.userVersion;

        log.w("The game version (" + gameVersion + ") is different from the user's config version (" + userVersion + ").");

        boolean canceled = false;

        try {
            FileManager manager = context.getFileManager();
            I18N i18N = context.getI18N();

            int doBackup = showYesNoCancelDialog(Alert.AlertType.WARNING,
                    i18N.get("version.conflict.backup.title"),
                    null,
                    i18N.get("version.conflict.backup.content", gameVersion, userVersion),
                    i18N.get("version.conflict.backup.accept"), i18N.get("version.conflict.backup.decline"),
                    i18N.get("gui.cancel"));

            switch (doBackup) {

                case YES:
                    Path backupDir = manager.getBackup(userVersion);

                    if (Files.exists(backupDir)) { // A backup already exists for this version; ask the user what to do

                        int doOverwrite = showYesNoCancelDialog(Alert.AlertType.WARNING,
                                i18N.get("version.conflict.backup.overwrite.title"),
                                null,
                                i18N.get("version.conflict.backup.overwrite.content", userVersion),
                                i18N.get("version.conflict.backup.overwrite.accept"),
                                i18N.get("version.conflict.backup.overwrite.decline"),
                                i18N.get("gui.cancel"));

                        switch (doOverwrite) {

                            case YES: // Replace existing backed up files
                                backup(backupDir, StandardCopyOption.REPLACE_EXISTING);
                                break;

                            case NO: // Do nothing : keep existing files
                                break;

                            case CANCEL: // Abort everything
                            default:
                                canceled = true;
                                break;
                        }
                    } else {
                        Files.createDirectory(backupDir);
                        backup(backupDir);
                    }
                    break;

                case NO: // Do nothing
                    break;

                case CANCEL: // Abort everything
                default:
                    canceled = true;
                    break;
            }

            if (!canceled) {

                int doKeep = showYesNoCancelDialog(Alert.AlertType.CONFIRMATION,
                        i18N.get("version.conflict.preferences.title"),
                        null,
                        i18N.get("version.conflict.preferences.content"),
                        i18N.get("version.conflict.preferences.keep"),
                        i18N.get("version.conflict.preferences.reset"),
                        i18N.get("gui.cancel"));

                // Now that files are or aren't backed up, according to user preference :
                // Either keep the current preferences where they are, in case everything can go on normally
                // Or delete them and replace them with default values.

                switch (doKeep) {

                    case YES: // Don't touch preferences, with an exception : update version string.
                        getProperty("version").setValue(gameVersion);
                        save();
                        break;

                    case NO: // Delete preferences and saves, and create default files.

                        Files.delete(manager.getConfig());
                        Files.delete(manager.getKeybindings());
                        FileUtils.deleteDirectory(manager.getSavesRoot());

                        context.getFileManager().checkFiles();

                        reset();
                        context.getInputManager().reset();
                        ((GameContext) context).getSaveManager().actualize();
                        break;

                    case CANCEL:
                        canceled = true;
                        break;
                }
            }

        } catch (IOException e) {
            DialogsUtil.showErrorDialog(context, e);
            throw new UncheckedIOException(e);
        }

        if (canceled) {
            log.w("The version conflict resolution was canceled by the user.");
            System.exit(-1);
        }
    }

    private void backup(Path backupDir, CopyOption... options) throws IOException {

        FileManager manager = context.getFileManager();

        Path mainDir = manager.getRootDirectory();

        Path config = manager.getConfig();
        Path keybindings = manager.getKeybindings();
        Path savesDir = manager.getSavesRoot();

        Files.copy(config, backupDir.resolve(mainDir.relativize(config)), options);
        Files.copy(keybindings, backupDir.resolve(mainDir.relativize(keybindings)), options);
        FileUtils.copyDirectory(savesDir, backupDir.resolve(mainDir.relativize(savesDir)), options);
    }
}
