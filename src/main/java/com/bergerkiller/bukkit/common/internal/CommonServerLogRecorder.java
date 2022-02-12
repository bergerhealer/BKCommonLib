package com.bergerkiller.bukkit.common.internal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.OverwritingCircularBuffer;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.logging.WeakLoggingHandler;

/**
 * Records all that is logged to the server log in a temporary buffer, so it can later
 * be used for generating reports. The recorder is automatically hooked when
 * BKCommonLib initializes, and disabled again when BKCommonLib disables.
 *
 * No logging is available when BKCommonLib failed to enable in the first place.
 */
public class CommonServerLogRecorder extends java.util.logging.Handler implements LibraryComponent {
    private final Task startupDoneTask;
    private final List<FormattedLogRecord> startupLogRecords = new ArrayList<>();
    private final OverwritingCircularBuffer<LogRecord> lastLogRecords = OverwritingCircularBuffer.create(32);
    private final Set<java.util.logging.Logger> hookedLoggers = new HashSet<>();
    private boolean isStartingUp = true;

    public CommonServerLogRecorder(CommonPlugin plugin) {
        this.startupDoneTask = new Task(plugin) {
            @Override
            public void run() {
                isStartingUp = false;
            }
        };

        // Register this recorder as handler for all server loggers that matter
        hookedLoggers.add(Bukkit.getLogger());
        hookedLoggers.add(Logger.getLogger(Bukkit.getServer().getClass().getName()));
        hookedLoggers.forEach(l -> WeakLoggingHandler.addHandler(l, this));
    }

    /**
     * Gets everything that was logged during the startup of the server
     *
     * @return startup-logged records
     */
    public List<FormattedLogRecord> getStartupLogRecords() {
        synchronized (this) {
            return new ArrayList<>(this.startupLogRecords);
        }
    }

    /**
     * Gets the last 32 messages that were logged
     *
     * @return last 32 server messages
     */
    public List<FormattedLogRecord> getRuntimeLogRecords() {
        return lastLogRecords.values().stream()
                .map(FormattedLogRecord::format)
                .collect(Collectors.toList());
    }

    @Override
    public void enable() {
        this.startupDoneTask.start();
    }

    @Override
    public void disable() {
        this.startupDoneTask.stop();
        this.hookedLoggers.forEach(l -> WeakLoggingHandler.removeHandler(l, this));
        this.hookedLoggers.clear();
    }

    @Override
    public void publish(LogRecord record) {
        if (isStartingUp) {
            FormattedLogRecord formatted = FormattedLogRecord.format(record);
            synchronized (this) {
                this.startupLogRecords.add(formatted);
            }
        } else {
            this.lastLogRecords.add(record);
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    /**
     * A log record formatted to a String, with an easy comparison function. This is used
     * to format a final document with all the logged records.
     */
    public static final class FormattedLogRecord implements Comparable<FormattedLogRecord> {
        public final long timestamp;
        public final String content;

        private FormattedLogRecord(LogRecord record) {
            timestamp = record.getMillis();
            content = ServerLogFormatter.INSTANCE.format(record);
        }

        @Override
        public int compareTo(FormattedLogRecord o) {
            return Long.compare(this.timestamp, o.timestamp);
        }

        public void appendTo(StringBuilder builder) {
            builder.append(content).append('\n');
        }

        public static FormattedLogRecord format(LogRecord record) {
            return new FormattedLogRecord(record);
        }
    }

    private static final class ServerLogFormatter extends java.util.logging.Formatter {
        public static final ServerLogFormatter INSTANCE = new ServerLogFormatter();

        @Override
        public String format(LogRecord record) {
            ZonedDateTime zdt = ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(record.getMillis()), ZoneId.systemDefault());
            String source;
            if (record.getSourceClassName() != null) {
                source = record.getSourceClassName();
                if (record.getSourceMethodName() != null) {
                   source += " " + record.getSourceMethodName();
                }
            } else {
                source = record.getLoggerName();
            }
            String message = formatMessage(record);
            String throwable = "";
            if (record.getThrown() != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                pw.println();
                record.getThrown().printStackTrace(pw);
                pw.close();
                throwable = sw.toString();

                // Get rid of double-newline issues
                if (throwable.endsWith("\n")) {
                    throwable = throwable.substring(0, throwable.length() - 1);
                }
            }
            return String.format("[%tT] [%4$s] [%3$s] %5$s%6$s",
                                 zdt,
                                 source,
                                 record.getLoggerName(),
                                 record.getLevel().getName(),
                                 message,
                                 throwable);
        }
    }
}
