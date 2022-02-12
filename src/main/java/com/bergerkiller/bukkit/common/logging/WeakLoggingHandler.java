package com.bergerkiller.bukkit.common.logging;

import java.lang.ref.WeakReference;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Wraps another logging handler with a weak reference, automatically removing the
 * handler when the underlying handler is garbage-collected. Prevents memory leaks
 * when handlers are registered with loggers and never removed.
 */
public final class WeakLoggingHandler extends Handler {
    private static final java.util.logging.Handler NOOP_HANDLER = new java.util.logging.Handler() {
        @Override
        public void publish(LogRecord record) {
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }
    };

    /**
     * Adds a logging handler to a logger, wrapped with a weak logging handler
     *
     * @param logger The logger
     * @param handler The handler to add
     */
    public static void addHandler(Logger logger, Handler handler) {
        logger.addHandler(new WeakLoggingHandler(logger, handler));
    }

    /**
     * Removes a previously registered handler from the logger. Looks for weak logging
     * handlers in which the specified handler is wrapped.
     *
     * @param logger The logger
     * @param handler The handler to remove
     */
    public static void removeHandler(Logger logger, Handler handler) {
        for (Handler existingHandler : logger.getHandlers()) {
            if (!(existingHandler instanceof WeakLoggingHandler)) {
                continue;
            }

            WeakLoggingHandler weakHandler = (WeakLoggingHandler) existingHandler;
            if (weakHandler.handler.get() == handler) {
                weakHandler.handler.clear();
                logger.removeHandler(weakHandler);
                return;
            }
        }
    }

    /**
     * Gets all the handlers of a logger and unwraps any weak logging handlers contained
     * within the result.
     *
     * @param logger The logger
     * @return registered handlers of the logger
     */
    public static Handler[] unwrapHandlers(Logger logger) {
        Handler[] handlers = logger.getHandlers();
        for (int n = handlers.length - 1; n >= 0; n--) {
            Handler h = handlers[n];
            if (h instanceof WeakLoggingHandler) {
                h = ((WeakLoggingHandler) h).handler.get();
                if (h == null) {
                    handlers = LogicUtil.removeArrayElement(handlers, n);
                } else {
                    handlers[n] = h;
                }
            }
        }
        return handlers;
    }

    private final Logger logger;
    private final WeakReference<Handler> handler;

    private WeakLoggingHandler(Logger logger, Handler handler) {
        this.logger = logger;
        this.handler = new WeakReference<>(handler);
    }

    private java.util.logging.Handler access() {
        Handler realHandler = handler.get();
        if (realHandler == null) {
            // De-register itself
            this.logger.removeHandler(this);
            // Does nothing
            return NOOP_HANDLER;
        }
        return realHandler;
    }

    @Override
    public void publish(LogRecord record) {
        access().publish(record);
    }

    @Override
    public void flush() {
        access().flush();
    }

    @Override
    public void close() throws SecurityException {
        access().close();
    }
}
