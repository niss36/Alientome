package com.game.level;

import com.util.FileManager;
import com.util.Util;
import com.util.listeners.SaveListener;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public final class LevelSaveManager {

    private static final LevelSaveManager ourInstance = new LevelSaveManager();
    private final Random random = new Random();
    private final ArrayList<SaveListener> listeners = new ArrayList<>();
    private int saveIndex;

    /**
     * Not instantiable
     */
    private LevelSaveManager() {

    }

    public static LevelSaveManager getInstance() {
        return ourInstance;
    }

    int init(int index) {

        saveIndex = index;

        File saveFile = FileManager.getInstance().getSave(saveIndex);

        if (!saveFile.exists()) {
            createSaveFile(saveFile);
            Util.log("Created save file " + index + ".", 0);
        }

        return read(saveFile);
    }

    public int getIDFromIndex(int index) {

        return read(FileManager.getInstance().getSave(index));
    }

    public boolean deleteSaveFile(int index) {

        boolean ret = FileManager.getInstance().getSave(index).delete();
        notifyListeners(index);
        return ret;
    }

    private void createSaveFile(File file) {
        save(file, 1);
    }

    void save(int levelID) {
        save(FileManager.getInstance().getSave(saveIndex), levelID);
    }

    private void save(File file, int levelID) {

        try (FileWriter writer = new FileWriter(file.getAbsolutePath())) {

            byte[] salt = new byte[16];

            for (int i = 0; i < salt.length; i++) salt[i] = (byte) (random.nextInt(32) - 16);

            String encrypted = encrypt("SomeLongStringAndLevelID=" + levelID, salt);

            String bytes = writeBytes(salt);

            writer.write("Encrypted. Modifying this file is probably going to create unexpected behavior, including loss of progress." + System.lineSeparator() + bytes + System.lineSeparator() + encrypted);

        } catch (IOException e) {
            e.printStackTrace();
        }

        notifyListeners(saveIndex);
    }

    private int read(File file) {

        try (BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()))) {

            byte[] salt = new byte[16];

            reader.readLine();
            String string = reader.readLine();

            readBytes(string, salt);

            String decrypted = decrypt(reader.readLine(), salt);

            return Integer.parseInt(decrypted.substring(decrypted.indexOf('=') + 1));

        } catch (FileNotFoundException e) {
            //No-OP
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private String writeBytes(byte[] bytes) {

        StringBuilder builder = new StringBuilder();

        for (byte b : bytes) builder.append(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1));

        return new String(builder);
    }

    private void readBytes(String string, byte[] bytes) {

        for (int i = 0; i < bytes.length; i++) {

            int b = Integer.parseInt(string.substring(0, 8), 2);
            if (b > 127) b -= 256;

            bytes[i] = (byte) b;
            string = string.substring(8);
        }
    }

    private String encrypt(String string, byte[] salt) {

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {

            int saltIndex = salt.length - 1 - (i % salt.length);

            builder.append((char) ((int) string.charAt(i) + salt[saltIndex]));
        }

        return new String(builder);
    }

    private String decrypt(String string, byte[] salt) {

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {

            int saltIndex = salt.length - 1 - (i % salt.length);

            builder.append((char) ((int) string.charAt(i) - salt[saltIndex]));
        }

        return new String(builder);
    }

    public void addSaveListener(SaveListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(int saveIndex) {

        SwingUtilities.invokeLater(() -> {
            for (SaveListener listener : listeners) listener.saveChanged(saveIndex);
        });
    }
}
