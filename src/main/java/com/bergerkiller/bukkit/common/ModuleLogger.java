package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
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
    private ExtendedLogger log4j_extendedLogger = null;

    // Map for converting from java.util.logging.Level to org.apache.logging.log4j.Level
    private static Map<Level, org.apache.logging.log4j.Level> log4jLevelMap = new IdentityHashMap<>();
    private static Map<org.apache.logging.log4j.Level, Level> log4jLevelMapRev = new IdentityHashMap<>();
    static {
        log4jLevelMap.put(Level.FINEST, org.apache.logging.log4j.Level.TRACE);
        log4jLevelMap.put(Level.FINER, org.apache.logging.log4j.Level.DEBUG);
        log4jLevelMap.put(Level.FINE, org.apache.logging.log4j.Level.DEBUG);
        log4jLevelMap.put(Level.CONFIG, org.apache.logging.log4j.Level.INFO);
        log4jLevelMap.put(Level.INFO, org.apache.logging.log4j.Level.INFO);
        log4jLevelMap.put(Level.WARNING, org.apache.logging.log4j.Level.WARN);
        log4jLevelMap.put(Level.SEVERE, org.apache.logging.log4j.Level.ERROR);
        log4jLevelMap.put(Level.ALL, org.apache.logging.log4j.Level.ALL);
        log4jLevelMap.put(Level.OFF, org.apache.logging.log4j.Level.OFF);
        for (Map.Entry<Level, org.apache.logging.log4j.Level> entry : log4jLevelMap.entrySet()) {
            log4jLevelMapRev.put(entry.getValue(), entry.getKey());
        }
    }

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
        logRecord.setThrown(StackTraceFilter.SERVER.filter(logRecord.getThrown()));
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

    /**
     * Obtains an Apache log4j ExtendedLogger instance that logs to this module logger.
     * Is only used during debug to avoid the slow initialization of the default log4j.
     * 
     * @return Apache log4j extended logger
     */
    public ExtendedLogger toLog4jExtendedLogger() {
        if (this.log4j_extendedLogger == null) {
            this.log4j_extendedLogger = new AbstractLogger() {
                private static final long serialVersionUID = 2180779091871302549L;

                public boolean isEnabled(org.apache.logging.log4j.Level log4jlevel) {
                    Level level = log4jLevelMapRev.get(log4jlevel);
                    if (level == null) {
                        level = Level.ALL;
                    }
                    return ModuleLogger.this.isLoggable(level);
                }

                @Override
                public boolean isEnabled(org.apache.logging.log4j.Level arg0, Marker arg1, String arg2) {
                    return isEnabled(arg0);
                }

                @Override
                public boolean isEnabled(org.apache.logging.log4j.Level arg0, Marker arg1, Message arg2, Throwable arg3) {
                    return isEnabled(arg0);
                }

                @Override
                public boolean isEnabled(org.apache.logging.log4j.Level arg0, Marker arg1, CharSequence arg2, Throwable arg3) {
                    return isEnabled(arg0);
                }

                @Override
                public boolean isEnabled(org.apache.logging.log4j.Level arg0, Marker arg1, Object arg2, Throwable arg3) {
                    return isEnabled(arg0);
                }

                @Override
                public boolean isEnabled(org.apache.logging.log4j.Level arg0, Marker arg1, String arg2, Throwable arg3) {
                    return isEnabled(arg0);
                }

                @Override
                public boolean isEnabled(org.apache.logging.log4j.Level arg0, Marker arg1, String arg2, Object... arg3) {
                    return isEnabled(arg0);
                }

                @Override
                public boolean isEnabled(org.apache.logging.log4j.Level arg0, Marker arg1, String arg2, Object arg3) {
                    return isEnabled(arg0);
                }

                @Override
                public boolean isEnabled(org.apache.logging.log4j.Level arg0, Marker arg1, String arg2, Object arg3, Object arg4) {
                    return isEnabled(arg0);
                }

                @Override
                public boolean isEnabled(org.apache.logging.log4j.Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5) {
                    return isEnabled(arg0);
                }

                @Override
                public boolean isEnabled(org.apache.logging.log4j.Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
                    return isEnabled(arg0);
                }

                @Override
                public boolean isEnabled(org.apache.logging.log4j.Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7) {
                    return isEnabled(arg0);
                }

                @Override
                public boolean isEnabled(org.apache.logging.log4j.Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8) {
                    return isEnabled(arg0);
                }

                @Override
                public boolean isEnabled(org.apache.logging.log4j.Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
                    return isEnabled(arg0);
                }

                @Override
                public boolean isEnabled(org.apache.logging.log4j.Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10) {
                    return isEnabled(arg0);
                }

                @Override
                public boolean isEnabled(org.apache.logging.log4j.Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
                    return isEnabled(arg0);
                }

                @Override
                public boolean isEnabled(org.apache.logging.log4j.Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12) {
                    return isEnabled(arg0);
                }

                @Override
                public org.apache.logging.log4j.Level getLevel() {
                    org.apache.logging.log4j.Level level;
                    level = log4jLevelMap.get(ModuleLogger.this.getLevel());
                    return (level != null) ? level : org.apache.logging.log4j.Level.ALL;
                }

                @Override
                public void logMessage(String fqcn, org.apache.logging.log4j.Level log4jlevel, Marker marker, Message msg, Throwable t) {
                    Level level = log4jLevelMapRev.get(log4jlevel);
                    if (level == null) {
                        level = Level.ALL;
                    }
                    ModuleLogger.this.log(level, msg.getFormattedMessage(), t);
                }
            };
        }
        return this.log4j_extendedLogger;
    }
}
