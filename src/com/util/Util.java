package com.util;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    public static void log(String message, int level) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String timestamp = "[" + simpleDateFormat.format(date) + "]";
        String msgLevel = "[" + parseLevel(level) + "]";
        String thread = "[" + Thread.currentThread().getName() + "]";

        if (level >= 2) {
            System.err.println(timestamp + thread + msgLevel + message);
            JOptionPane.showMessageDialog(null, message, msgLevel, JOptionPane.ERROR_MESSAGE);

            if (level == 3) System.exit(-1);

        } else System.out.println(timestamp + thread + msgLevel + message);
    }

    private static String parseLevel(int level) {

        switch (level) {

            case 0:
                return "INFO";

            case 1:
                return "WARN";

            case 2:
                return "ERROR";

            case 3:
                return "FATAL";

            default:
                return null;

        }
    }
}
