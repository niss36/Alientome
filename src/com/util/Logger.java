package com.util;

import sun.reflect.CallerSensitive;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private final SimpleDateFormat logDateFormat = new SimpleDateFormat("HH:mm:ss");
    private final String className;
    private final String formatString;

    private Logger(String className, String formatString) {
        this.className = className;
        this.formatString = formatString;
    }

    @CallerSensitive
    public static Logger get() {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        String className = stackTrace[2].getClassName();
        className = className.substring(className.lastIndexOf('.') + 1);

        return get(className);
    }

    public static Logger get(String className) {

        return new Logger(className, "[%s][%s/%s][%s]: %s\n");
    }

    public void i(String message) {

        log(System.out, message, LogLevel.INFO);
    }

    public void w(String message) {

        log(System.out, message, LogLevel.WARN);
    }

    public void e(String message) {

        log(System.err, message, LogLevel.ERROR);
    }

    public void f(String message) {

        log(System.err, message, LogLevel.FATAL);
    }

    private void log(PrintStream stream, String message, LogLevel level) {

        String timeStamp = logDateFormat.format(new Date());
        String threadName = Thread.currentThread().getName();
        String logLevel = level.toString();

        stream.printf(formatString, timeStamp, threadName, logLevel, className, message);
    }

    private enum LogLevel {
        INFO,
        WARN,
        ERROR,
        FATAL
    }
}
