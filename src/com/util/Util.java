package com.util;

import javax.swing.*;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
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

        PrintStream stream = level >= 2 ? System.err : System.out;

        stream.println(timestamp + thread + msgLevel + message);

        if (level >= 2) {
            JOptionPane.showMessageDialog(null, message, msgLevel, JOptionPane.ERROR_MESSAGE);

            if (level == 3) System.exit(-1);

        }
    }

    public static void closeSilently(Closeable closeable) {

        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to decrease velocity, that is, make it closer to zero.
     *
     * @param toDecrease the number to be decreased.
     * @param value      the amount to decrease.
     * @return If <code>toDecrease==0</code> 0 else <code>toDecrease</code> closer to 0 by <code>value</code>.
     */
    public static double decrease(double toDecrease, double value) {
        return toDecrease < 0 ? toDecrease + value : toDecrease > 0 ? toDecrease - value : 0;
    }

    /**
     * Method used to decrease a vector representing velocity.
     *
     * @param vec   the <code>Vec2</code> to be decreased.
     * @param value the amount to decrease.
     */
    public static void decrease(Vec2 vec, double value) {

        vec.x = decrease(vec.x, value);
        vec.y = decrease(vec.y, value);
    }

    public static int center(double length1, double length2) {

        return (int) (length1 / 2 - length2 / 2);
    }

    public static double clamp(double value, double minVal, double maxVal) {

        return value < minVal ? minVal : value > maxVal ? maxVal : value;
    }
}
