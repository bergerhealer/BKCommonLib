package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.server.UnknownServer;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A logger that is meant to log a given module of the server, for example that
 * of a Plugin
 */
public class ModuleLogger extends Logger {
    private final String[] modulePath;
    private final String prefix;
    private final HashSet<String> logOnceSet = new HashSet<String>();

    private static Logger createDefaultLogger() {
        if (Bukkit.getServer() != null && Bukkit.getLogger() != MountiplexUtil.LOGGER)
            return Bukkit.getLogger();

    	Logger log = Logger.getLogger("");
    	log.setUseParentHandlers(false);
    	CustomRecordFormatter formatter = new CustomRecordFormatter();
    	ConsoleHandler consoleHandler = new ConsoleHandler();
    	consoleHandler.setFormatter(formatter);

        for(Handler iHandler:log.getHandlers()) {
            log.removeHandler(iHandler);
        }
    	log.addHandler(consoleHandler);

    	return log;
    }
    
    private static class CustomRecordFormatter extends Formatter {
        @Override
        public String format(final LogRecord r) {
            StringBuilder sb = new StringBuilder();
            sb.append("[" + r.getLevel().getName() + "] ");
            sb.append(formatMessage(r)).append(System.getProperty("line.separator"));
            if (null != r.getThrown()) {
                sb.append("Throwable occurred: "); //$NON-NLS-1$
                Throwable t = r.getThrown();
                PrintWriter pw = null;
                try {
                    StringWriter sw = new StringWriter();
                    pw = new PrintWriter(sw);
                    t.printStackTrace(pw);
                    sb.append(sw.toString());
                } finally {
                    if (pw != null) {
                        try {
                            pw.close();
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                }
            }
            return sb.toString();
        }
    }
    
    
    public ModuleLogger(Plugin plugin, String... modulePath) {
        this(LogicUtil.appendArray(new String[]{getPrefix(plugin)}, modulePath));
    }

    public ModuleLogger(String... modulePath) {
        this(createDefaultLogger(), modulePath);
    }

    public ModuleLogger(Logger parent, String... modulePath) {
        super(StringUtil.join(".", modulePath), null);
        this.setParent(parent);
        this.setLevel(Level.ALL);
        this.modulePath = modulePath;
        StringBuilder builder = new StringBuilder();
        for (String module : modulePath) {
            builder.append("[").append(module).append("] ");
        }
        this.prefix = builder.toString();
    }

    private static String getPrefix(Plugin plugin) {
        return LogicUtil.fixNull(plugin.getDescription().getPrefix(), plugin.getDescription().getName());
    }

    /**
     * Obtains a Module Logger for the path specified
     *
     * @param path to get the Module Logger for
     * @return new Module Logger pointing to the path relative to this Module
     * Logger
     */
    public ModuleLogger getModule(String... path) {
        return new ModuleLogger(this.getParent(), LogicUtil.appendArray(this.modulePath, path));
    }

    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage(this.prefix + logRecord.getMessage());

        // Filter stack trace. Only do so when the server was properly initialized
        if (!(CommonBootstrap.initCommonServer() instanceof UnknownServer) && logRecord.getThrown() != null) {
            logRecord.setThrown(StackTraceFilter.SERVER.filter(logRecord.getThrown()));
        }

        super.log(logRecord);
    }

    /**
     * Prints a quick trace line of the method that preceded trace() with a message
     * 
     * @param message to print
     */
    public void trace(String message) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack.length >= 3) {
            log(Level.INFO, stack[2].getMethodName() + " " + message + " (" + stack[2].getFileName() + ":" + stack[2].getLineNumber() + ")");
        }
    }

    /**
     * Prints a quick trace line of the method that preceded trace()
     */
    public void trace() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack.length >= 3) {
            log(Level.INFO, stack[2].getMethodName() + " (" + stack[2].getFileName() + ":" + stack[2].getLineNumber() + ")");
        }
    }

    /**
     * Logs a simple warning message to the server console, but only does this upon the first invocation.
     * This enables easier bugtracking to identify longstanding issues that need resolving.
     * The place in the code where this is called is printed, along with the message.
     * <br><br>
     * Prints with WARNING level.
     * 
     * @param message to log
     */
    public void warnOnce(String message) {
        once(Level.WARNING, message);
    }

    /**
     * Logs a simple warning message to the server console, but only does this upon the first invocation.
     * This enables easier bugtracking to identify longstanding issues that need resolving.
     * The place in the code where this is called is printed, along with the message.
     * 
     * @param level of the message
     * @param message to log
     */
    public void once(Level level, String message) {
        once(level, message, null);
    }

    /**
     * Logs a simple warning message with exception information to the server console, but only does this upon the first invocation.
     * This enables easier bugtracking to identify longstanding issues that need resolving.
     * The place in the code where this is called is printed, along with the message.
     * 
     * @param level of the message
     * @param message to log
     * @param t throwable to log
     */
    public void once(Level level, String message, Throwable t) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String token = message; // fallback
        for (int i = 1; i < stack.length; i++) {
            if (stack[i].getClassName().equals(ModuleLogger.class.getName())) {
                continue;
            }

            token = message + "  -  " + stack[i].toString();
            break;
        }
        String key = token;
        if (t != null) {
            key += t.toString();
        }
        if (logOnceSet.add(key)) {
            if (t != null) {
                Logging.LOGGER_DEBUG.log(level, token, t);
            } else {
                Logging.LOGGER_DEBUG.log(level, token);
            }
        }
    }

    /**
     * Handles the message and/or stack trace logging when something related to
     * reflection is missing
     *
     * @param type of thing that is missing
     * @param name of the thing that is missing
     * @param source class in which it is missing
     */
    public void handleReflectionMissing(String type, String name, Class<?> source) {
        String msg = type + " '" + name + "' does not exist in class file " + source.getName();
        Exception ex = new Exception(msg);
        for (StackTraceElement elem : ex.getStackTrace()) {
            if (elem.getClassName().startsWith("com.bergerkiller.reflection")) {
            	msg += " (Update BKCommonLib?)";
            	break;
            }
        }
        log(Level.WARNING, msg, ex);
    }
}
