package sistematica.pbutils;

import org.apache.log4j.Logger;

/**
 * Logger con dei metodi per abbreviare l'idioma
 * logger.log(String.format(...))
 */
public class FormatLogger {

    private Logger logger;

    public static FormatLogger getLogger(Class clazz) {
        return new FormatLogger(Logger.getLogger(clazz));
    }

    protected FormatLogger(Logger logger) {
        this.logger = logger;
    }

    public void trace(String message, Object... args) {
        logger.trace(String.format(message, args));
    }

    public void trace(String message, Throwable t, Object... args) {
        logger.trace(String.format(message, args), t);
    }

    public void debug(String message, Object... args) {
        logger.debug(String.format(message, args));
    }

    public void debug(String message, Throwable t, Object... args) {
        logger.debug(String.format(message, args), t);
    }

    public void info(String message, Object... args) {
        logger.info(String.format(message, args));
    }

    public void info(String message, Throwable t, Object... args) {
        logger.info(String.format(message, args), t);
    }

    public void warn(String message, Object... args) {
        logger.warn(String.format(message, args));
    }

    public void warn(String message, Throwable t, Object... args) {
        logger.warn(String.format(message, args), t);
    }

    public void error(String message, Object... args) {
        logger.error(String.format(message, args));
    }

    public void error(String message, Throwable t, Object... args) {
        logger.error(String.format(message, args), t);
    }

    public void fatal(String message, Object... args) {
        logger.fatal(String.format(message, args));
    }

    public void fatal(String message, Throwable t, Object... args) {
        logger.fatal(String.format(message, args), t);
    }
}
