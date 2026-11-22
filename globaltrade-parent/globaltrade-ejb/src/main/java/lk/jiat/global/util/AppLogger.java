package lk.jiat.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppLogger {

    private static final Logger LOGGER = Logger.getLogger(AppLogger.class.getName());
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private AppLogger() {
        // Private constructor to prevent instantiation
    }

    public static void info(String message) {
        LOGGER.info(formatMessage("INFO", message));
    }

    public static void info(String message, Object... args) {
        LOGGER.info(formatMessage("INFO", String.format(message, args)));
    }

    public static void warning(String message) {
        LOGGER.warning(formatMessage("WARN", message));
    }

    public static void warning(String message, Object... args) {
        LOGGER.warning(formatMessage("WARN", String.format(message, args)));
    }

    public static void error(String message) {
        LOGGER.severe(formatMessage("ERROR", message));
    }

    public static void error(String message, Throwable throwable) {
        LOGGER.log(Level.SEVERE, formatMessage("ERROR", message), throwable);
    }

    public static void error(String message, Object... args) {
        LOGGER.severe(formatMessage("ERROR", String.format(message, args)));
    }

    public static void debug(String message) {
        LOGGER.fine(formatMessage("DEBUG", message));
    }

    public static void debug(String message, Object... args) {
        LOGGER.fine(formatMessage("DEBUG", String.format(message, args)));
    }

    private static String formatMessage(String level, String message) {
        return String.format("[%s] [%s] %s",
                FORMATTER.format(LocalDateTime.now()),
                level,
                message
        );
    }
}