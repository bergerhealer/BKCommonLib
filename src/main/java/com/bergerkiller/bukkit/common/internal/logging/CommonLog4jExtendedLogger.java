package com.bergerkiller.bukkit.common.internal.logging;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.spi.AbstractLogger;

import com.bergerkiller.bukkit.common.ModuleLogger;

/**
 * Logger only used during test to reduce initialization time of the server
 */
class CommonLog4jExtendedLogger extends AbstractLogger {
    private static final long serialVersionUID = 2180779091871302549L;
    private final ModuleLogger logger;

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

    public CommonLog4jExtendedLogger(String name) {
        this.logger = new ModuleLogger(name);
    }

    public boolean isEnabled(org.apache.logging.log4j.Level log4jlevel) {
        Level level = log4jLevelMapRev.get(log4jlevel);
        if (level == null) {
            level = Level.ALL;
        }
        return this.logger.isLoggable(level);
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
        level = log4jLevelMap.get(this.logger.getLevel());
        return (level != null) ? level : org.apache.logging.log4j.Level.ALL;
    }

    @Override
    public void logMessage(String fqcn, org.apache.logging.log4j.Level log4jlevel, Marker marker, Message msg, Throwable t) {
        Level level = log4jLevelMapRev.get(log4jlevel);
        if (level == null) {
            level = Level.ALL;
        }
        this.logger.log(level, msg.getFormattedMessage(), t);
    }
}
