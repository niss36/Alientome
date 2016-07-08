package com.util;

import javax.swing.*;
import java.io.Closeable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Util {

    private static final String[] messageLevel = {"INFO", "WARN", "ERROR", "FATAL"};

    /**
     * Not instantiable
     */
    private Util() {

    }

    public static void log(String message, int level) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String timestamp = "[" + simpleDateFormat.format(date) + "]";
        String msgLevel = "[" + messageLevel[level] + "]";
        String thread = "[" + Thread.currentThread().getName() + "]";

        if (level >= 2) {
            System.err.println(timestamp + thread + msgLevel + message);
            JOptionPane.showMessageDialog(null, message, msgLevel, JOptionPane.ERROR_MESSAGE);

            if (level == 3) System.exit(-1);

        } else System.out.println(timestamp + thread + msgLevel + message);
    }

    public static void closeSilently(Closeable stream) {

        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double combineVelocities(double velocityA, double velocityB) {

        int sigA = (int) Math.signum(velocityA);
        int sigB = (int) Math.signum(velocityB);

        if (sigA != 0 && sigB != 0 && sigA != sigB) return velocityA + velocityB;

        double absA = Math.abs(velocityA);
        double absB = Math.abs(velocityB);

        return Math.max(absA, absB);
    }
}
