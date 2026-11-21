package me.zombie_striker.qg.utils;

import me.zombie_striker.qg.QAMain;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reports unexpected errors instead of swallowing them. Each place that can fail is reported once
 * (with the stack trace when {@code debug.log-exceptions} is enabled); repeats only go to the debug
 * channel, so code running every tick can't flood the console.
 */
public final class ErrorReporter {
    private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();

    private ErrorReporter() {
    }

    /**
     * @param context What was being done, for example "applying armor protection".
     * @param error   The error.
     */
    public static void report(String context, Throwable error) {
        QAMain plugin = QAMain.getInstance();
        Logger logger = plugin != null ? plugin.getLogger() : Logger.getLogger("QualityArmory");
        String key = context + '|' + error.getClass().getName();

        if (!REPORTED.add(key)) {
            if (plugin != null && QAMain.getConfiguration() != null)
                QAMain.DEBUG("Error while " + context + ": " + error);
            return;
        }

        boolean details = plugin == null || QAMain.getConfiguration() == null || QAMain.getConfiguration().debug.logExceptions;
        if (details)
            logger.log(Level.WARNING, "Error while " + context + ". Further occurrences are hidden.", error);
        else
            logger.warning("Error while " + context + ": " + error + ". Further occurrences are hidden. Enable debug.log-exceptions for details.");
    }

    /**
     * Forgets reported errors, so they are reported again after a reload.
     */
    public static void reset() {
        REPORTED.clear();
    }
}
