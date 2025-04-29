package com.failover.router.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {

    // Bind logger properly using the LoggerUtil class
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);

    /**
     * Log an informational message.
     * @param message The message to log
     */
    public static void logInfo(String message) {
        logger.info(message);
    }

    /**
     * Log an error message.
     * @param message The message to log
     */
    public static void logError(String message) {
        logger.error(message);
    }

    public static void logError(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    /**
     * Log a debug message (for detailed troubleshooting).
     * @param message The message to log
     */
    public static void logDebug(String message) {
        logger.debug(message);
    }
}
