package com.bergerkiller.bukkit.common.internal.logging;

import java.net.URI;

import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.LoggerContextFactory;

/**
 * Initializes a log4j logger for use during test.
 * Avoids the default server logger initialization.
 */
public class CommonLog4jTestLogging {

    /**
     * Initializes the Logger Context Factory of log4j to log to our own Module Logger.
     * This prevents very slow initialization of the default logger under test.
     */
    public static void initLog4jLegacy() {
        org.apache.logging.log4j.LogManager.setFactory(new LoggerContextFactory() {
            @Override
            public org.apache.logging.log4j.spi.LoggerContext getContext(String arg0, ClassLoader arg1, Object arg2, boolean arg3) {
                return new org.apache.logging.log4j.spi.LoggerContext() {
                    @Override
                    public Object getExternalContext() {
                        return null;
                    }

                    @Override
                    public org.apache.logging.log4j.spi.ExtendedLogger getLogger(String name) {
                        return new CommonLog4jExtendedLogger(name);
                    }

                    @Override
                    public org.apache.logging.log4j.spi.ExtendedLogger getLogger(String arg0, MessageFactory arg1) {
                        return getLogger(arg0);
                    }

                    @Override
                    public boolean hasLogger(String arg0) {
                        return true;
                    }

                    @Override
                    public boolean hasLogger(String arg0, MessageFactory arg1) {
                        return true;
                    }

                    @Override
                    public boolean hasLogger(String arg0, Class<? extends MessageFactory> arg1) {
                        return true;
                    }
                };
            }

            @Override
            public org.apache.logging.log4j.spi.LoggerContext getContext(String arg0, ClassLoader arg1, Object arg2, boolean arg3, URI arg4, String arg5) {
                return getContext(arg0, arg1, arg2, arg3);
            }

            @Override
            public void removeContext(org.apache.logging.log4j.spi.LoggerContext arg0) {
            }
        });
    }

    /**
     * Initializes the Logger Context Factory of log4j to log to our own Module Logger.
     * This prevents very slow initialization of the default logger under test.
     */
    public static void initLog4j() {
        org.apache.logging.log4j.LogManager.setFactory(new LoggerContextFactory() {
            @Override
            public org.apache.logging.log4j.core.LoggerContext getContext(String arg0, ClassLoader arg1, Object arg2, boolean arg3) {
                return new org.apache.logging.log4j.core.LoggerContext("Test") {
                    @Override
                    public Object getExternalContext() {
                        return null;
                    }

                    @Override
                    public org.apache.logging.log4j.core.Logger getLogger(String name) {
                        return new CommonLog4jCoreLogger(this, name);
                    }

                    @Override
                    public org.apache.logging.log4j.core.Logger getLogger(String arg0, MessageFactory arg1) {
                        return getLogger(arg0);
                    }

                    @Override
                    public boolean hasLogger(String arg0) {
                        return true;
                    }

                    @Override
                    public boolean hasLogger(String arg0, MessageFactory arg1) {
                        return true;
                    }

                    @Override
                    public boolean hasLogger(String arg0, Class<? extends MessageFactory> arg1) {
                        return true;
                    }
                };
            }

            @Override
            public org.apache.logging.log4j.core.LoggerContext getContext(String arg0, ClassLoader arg1, Object arg2, boolean arg3, URI arg4, String arg5) {
                return getContext(arg0, arg1, arg2, arg3);
            }

            @Override
            public void removeContext(org.apache.logging.log4j.spi.LoggerContext context) {
            }
        });
    }
}
