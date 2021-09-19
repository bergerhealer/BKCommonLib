package com.bergerkiller.bukkit.common.internal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Hastebin;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.mountiplex.MountiplexUtil;

/**
 * Can be hooked to a plugin's logger so that all written log records
 * can be tracked and later retrieved that are part of the plugin's startup log.
 * Logs written by dependencies of a plugin are also read, and combined together
 * as one log report.
 * 
 * The log handler is automatically removed after startup has finished (one tick later)
 */
public class CommonDependencyStartupLogHandler extends java.util.logging.Handler {
    public static final String PERMISSION = "bkcommonlib.command.startuplog";
    private static final Object LOCK = new Object(); // A global lock avoids a shitton of problems
    private final Plugin plugin;
    private final List<StartupLogRecord> startupLogRecords = new ArrayList<>();
    private final StringBuilder startupLog = new StringBuilder(); // Includes records of dependencies!
    private final Set<CommonDependencyStartupLogHandler> startupLogSources = new HashSet<>();
    private final Set<CommonDependencyStartupLogHandler> startupLogSinks = new HashSet<>();
    protected boolean enabled = true;

    protected CommonDependencyStartupLogHandler(Plugin plugin) {
        this.plugin = plugin;
        this.startupLogSources.add(this);
        this.startupLogSinks.add(this);
        ServerLogRecorder.bind(this); // Make sure bound on first use!
    }

    @Override
    public void publish(LogRecord j_record) {
        if (!enabled) {
            return;
        }

        StartupLogRecord record = new StartupLogRecord(j_record);

        synchronized (LOCK) {
            startupLogRecords.add(record);
            startupLogSinks.forEach(sink -> record.appendTo(sink.startupLog));
        }
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public String getFullStartupLog() {
        StringBuilder fullStartupLog = new StringBuilder();

        // Add a header at the top with some helpful server environment details
        {
            fullStartupLog.append("====================================================\n");
            {
                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy O", Locale.ENGLISH);
                fullStartupLog.append("Date: ");
                fullStartupLog.append(ZonedDateTime.now().format(formatter));
                fullStartupLog.append('\n');
            }
            {
                fullStartupLog.append("Server: " + Bukkit.getServer().getVersion());
                fullStartupLog.append(" (JDK ");
                try {
                    fullStartupLog.append(Runtime.version().toString());
                } catch (Throwable t) {
                    /* Fallback for older JDK */
                    fullStartupLog.append(System.getProperty("java.version"));
                }
                fullStartupLog.append(")\n");
            }
            fullStartupLog.append("Relevant plugins:\n");
            startupLogSources.stream()
                .map(CommonDependencyStartupLogHandler::getPlugin)
                .forEachOrdered(plugin -> {
                    fullStartupLog.append("  - ");
                    fullStartupLog.append(plugin.getName());
                    if (!plugin.isEnabled()) {
                        fullStartupLog.append(" [DISABLED]");
                    }
                    fullStartupLog.append(' ');
                    if (plugin instanceof PluginBase) {
                        fullStartupLog.append(((PluginBase) plugin).getDebugVersion());
                    } else {
                        fullStartupLog.append(plugin.getDescription().getVersion());
                    }
                    fullStartupLog.append('\n');
                });
            fullStartupLog.append("====================================================\n\n");
        }

        fullStartupLog.append("---- Plugin logs ----\n");
        synchronized (LOCK) {
            fullStartupLog.append(startupLog.toString());
        }

        fullStartupLog.append("\n---- Miscellaneous server logs ----\n");
        synchronized (ServerLogRecorder.INSTANCE) {
            ServerLogRecorder.INSTANCE.records.forEach(r -> r.appendTo(fullStartupLog));
        }

        return fullStartupLog.toString();
    }

    private void handleAddDependency(CommonDependencyStartupLogHandler dependency) {
        synchronized (LOCK) {
            // For all plugins depending on this plugin, including this plugin itself.
            for (CommonDependencyStartupLogHandler sink : new ArrayList<>(this.startupLogSinks)) {
                boolean changed = false;
                for (CommonDependencyStartupLogHandler source : dependency.startupLogSources) {
                    if (sink.startupLogSources.add(source)) {
                        source.startupLogSinks.add(sink);
                        changed = true;
                    }
                }
                if (changed) {
                    sink.rebuildStartupLog();
                }
            }
        }
    }

    private void rebuildStartupLog() {
        // Add all log records, sorted by timestamp, in one full log
        // Filter by just the plugins depended on
        startupLog.setLength(0);
        startupLogSources.stream()
             .flatMap(source -> source.startupLogRecords.stream())
             .sorted()
             .forEachOrdered(record -> record.appendTo(startupLog));
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }

    public void bindDependency(Plugin plugin) {
        // Bind an existing handler
        for (java.util.logging.Handler handler : plugin.getLogger().getHandlers()) {
            if (handler instanceof CommonDependencyStartupLogHandler) {
                handleAddDependency((CommonDependencyStartupLogHandler) handler);
                return;
            }
        }

        // Bind a new one - make sure to de-register it the next tick!
        // No need to do this if the plugin never enabled - there is nothing to log.
        if (plugin.isEnabled()) {
            final CommonDependencyStartupLogHandler new_dep_handler = new CommonDependencyStartupLogHandler(plugin);
            plugin.getLogger().addHandler(new_dep_handler);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                    () -> plugin.getLogger().removeHandler(new_dep_handler));
            handleAddDependency(new_dep_handler);
        }
    }

    public static CommonDependencyStartupLogHandler.PluginBaseHandler bindSelf(PluginBase plugin) {
        PluginBaseHandler handler = new PluginBaseHandler(plugin);
        plugin.getLogger().addHandler(handler);
        return handler;
    }

    public static class PluginBaseHandler extends CommonDependencyStartupLogHandler {
        private JavaPlugin pluginDelegate;
        private CompletableFuture<Hastebin.UploadResult> startupLogUpload = null; // Once uploaded to Hastebin is set
        private String hastebinServer = "https://hastebin.com";
        private String mainCommand = null;
        private String criticalStartupFailure = null;

        protected PluginBaseHandler(PluginBase pluginBase) {
            super(pluginBase);
            this.pluginDelegate = pluginBase;
        }

        @Override
        public PluginBase getPlugin() {
            return (PluginBase) super.getPlugin();
        }

        public void setHastebinServer(String hastebinServer) {
            this.hastebinServer = hastebinServer;
        }

        public String getCriticalStartupFailure() {
            return this.criticalStartupFailure;
        }

        public boolean hasCriticalStartupFailure() {
            return this.criticalStartupFailure != null;
        }

        public void stopReadingLogNextTick() {
            Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), this::stopReadingLogNow);
        }

        public void stopReadingLogNow() {
            enabled = false;
            getPlugin().getLogger().removeHandler(this);
            ServerLogRecorder.unbind();
        }

        /**
         * Activates critical failure mode. This will register listeners and command handlers
         * in place of the plugin's normal commands telling people what went wrong. In addition, they
         * can use a special command to retrieve a full startup log.
         *
         * @param commonPlugin CommonPlugin instance that will hold commands/listeners for us
         * @param reason Reason plugin failed to enable at startup
         * @param commands Commands registered by the plugin
         */
        @SuppressWarnings("unchecked")
        public void criticalStartupFailure(CommonPlugin commonPlugin, String reason, Collection<CriticalAltCommand> commands) {
            this.pluginDelegate = commonPlugin;
            this.criticalStartupFailure = reason;

            // Stop tracking startup logs beyond this point, but keep the logger hooked for one more tick
            // Depending plugins will still want to see the startup log
            enabled = false;
            Bukkit.getScheduler().scheduleSyncDelayedTask(commonPlugin, this::stopReadingLogNow);

            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onPlayerJoin(PlayerJoinEvent event) {
                    // Only advertise to operators / those that can view the issue
                    if (mainCommand == null || !checkHasStartupLogPermission(event.getPlayer())) {
                        return;
                    }

                    // Don't show any details, just let the player know. We don't want to be too spammy
                    String msg = ChatColor.RED + "Plugin " + getPlugin().getName() + " failed to enable - Check " +
                            ChatColor.BOLD + ChatColor.UNDERLINE + "/" + mainCommand;
                    try {
                        ChatText formatted = ChatText.fromMessage(msg);
                        formatted = formatted.setClickableRunCommand("/" + mainCommand);
                        formatted.sendTo(event.getPlayer());
                    } catch (Throwable t) {
                        // Fallback in case stuff broke
                        event.getPlayer().sendMessage(msg);
                    }
                }
            }, this.pluginDelegate);

            // Executor for handling the /<command> startuplog command
            // Without (other) args, it will show that the plugin is disabled, similar to the join message
            CommandExecutor executor = new CommandExecutor() {
                @Override
                public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
                    if (args.length >= 1 && args[0].equalsIgnoreCase("startuplog")) {
                        handleStartupLogCommand(sender);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Plugin " + getPlugin().getName() + " failed to enable!");
                        if (checkHasStartupLogPermission(sender)) {
                            tellAboutServerLog(sender, label);
                        } else {
                            sender.sendMessage(ChatColor.RED + "Tell a server operator to fix this issue");
                        }
                    }
                    return true;
                }
            };

            // Register commands for this plugin that serves solely to provide a startup log
            try {
                Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getPluginManager());

                // Un-register all commands before we register again
                // This is important because BKCommonLib will be handling commands of other plugins!
                {
                    List<String> conflicts = commands.stream()
                            .flatMap(c -> Stream.concat(MountiplexUtil.toStream(c.name), c.aliases.stream()))
                            .filter(name -> commandMap.getCommand(name) != null)
                            .collect(Collectors.toList());

                    if (!conflicts.isEmpty()) {
                        Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                        knownCommandsField.setAccessible(true);
                        Map<String, ?> knownCommands = (Map<String, ?>) knownCommandsField.get(commandMap);
                        conflicts.forEach(knownCommands::remove);
                    }
                }

                // Register all the commands
                for (CriticalAltCommand command : commands) {
                    // Pick first command to advertise when operators join
                    if (mainCommand == null) {
                        mainCommand = command.name;
                    }

                    PluginCommand pluginCommand;
                    {
                        Constructor<PluginCommand> constr = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                        constr.setAccessible(true);
                        pluginCommand = constr.newInstance(command.name, this.pluginDelegate);
                        pluginCommand.setAliases(command.aliases);
                        pluginCommand.setDescription("Plugin " + getPlugin().getName() + " failed to enable!");
                        pluginCommand.setUsage("/" + command.name + " startuplog - Get plugin startup log");
                        pluginCommand.setExecutor(executor);
                    }

                    commandMap.register(this.pluginDelegate.getName(), pluginCommand);
                }
            } catch (Throwable t) {
                getPlugin().getLogger().log(Level.SEVERE, "Failed to register fallabck startup log handler commands", t);
            }
        }

        /**
         * Command handler for the /<command> startuplog command
         *
         * @param sender
         */
        public void handleStartupLogCommand(final CommandSender sender) {
            if (!checkHasStartupLogPermission(sender)) {
                sender.sendMessage(ChatColor.RED + "You have no permission! Ask an admin.");
                return;
            }

            // If upload finishes quickly, don't show an uploading... message
            final Task delayedNotifyTask = new Task(this.pluginDelegate) {
                @Override
                public void run() {
                    sender.sendMessage(ChatColor.YELLOW + "Uploading startup log to paste server...");
                }
            }.start(10);

            this.uploadStartupLog().thenAccept(result -> {
                delayedNotifyTask.stop();
                if (result.success()) {
                    sender.sendMessage(ChatColor.YELLOW + "Need help? Share this startup log URL with the developers!");
                    sender.sendMessage(ChatColor.YELLOW + "View the " + getPlugin().getName() + " startup log:");
                    tellAboutLogURL(sender, result.url());
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to upload startup log: " + result.error());
                }
            });
        }

        /**
         * Tells a sender about the server log and the startuplog command
         *
         * @param sender Command sender
         * @param label Command label
         */
        public void tellAboutServerLog(CommandSender sender, String label) {
            {
                String[] failureList = this.criticalStartupFailure.split("\n");
                if (failureList.length == 1) {
                    sender.sendMessage(ChatColor.RED + "Cause: " + this.criticalStartupFailure);
                } else {
                    sender.sendMessage(ChatColor.RED + "Cause:");
                    for (String failure : failureList) {
                        sender.sendMessage(ChatColor.RED + "  - " + failure);
                    }
                }
            }

            String logURL = getStartupLogURLIfAvailable();
            if (logURL != null) {
                sender.sendMessage(ChatColor.RED + "Check the server log, or view the plugin startup log:");
                tellAboutLogURL(sender, logURL);
            } else {
                sender.sendMessage(ChatColor.RED + "Check the server log, or use the following command:");

                String cmdText = ChatColor.WHITE.toString() + ChatColor.UNDERLINE + "/" + label + " startuplog";
                try {
                    ChatText formatted = ChatText.fromMessage(cmdText);
                    formatted = formatted.setClickableRunCommand("/" + label + " startuplog");
                    formatted.sendTo(sender);
                } catch (Throwable t) {
                    // Fallback - maybe ChatText failed to initialize or something!
                    sender.sendMessage(cmdText);
                }
            }
        }

        public void tellAboutLogURL(CommandSender sender, String logURL) {
            String urlText = ChatColor.WHITE + "> " + ChatColor.UNDERLINE + logURL + ".txt";
            try {
                ChatText formatted = ChatText.fromMessage(urlText);
                formatted = formatted.setClickableURL(logURL + ".txt");
                formatted.sendTo(sender);
            } catch (Throwable t) {
                // Fallback - maybe ChatText failed to initialize or something!
                sender.sendMessage(urlText);
            }
        }

        /**
         * Uploads the startup log to Hastebin if not already uploaded.
         * 
         * @return Upload result future
         */
        public CompletableFuture<Hastebin.UploadResult> uploadStartupLog() {
            if (this.startupLogUpload == null || (this.startupLogUpload.isDone() && !hasStartupLog())) {
                Hastebin hastebin = new Hastebin(this.pluginDelegate, this.hastebinServer);
                this.startupLogUpload = hastebin.upload(this.getFullStartupLog());
            }
            return this.startupLogUpload;
        }

        public String getStartupLogURLIfAvailable() {
            try {
                return hasStartupLog() ? this.startupLogUpload.get().url() : null;
            } catch (InterruptedException | ExecutionException e) {
                return null;
            }
        }

        private boolean hasStartupLog() {
            CompletableFuture<Hastebin.UploadResult> c = this.startupLogUpload;
            try {
                return c != null && c.isDone() && !c.isCompletedExceptionally() && c.get().success();
            } catch (InterruptedException | ExecutionException e) {
                return false;
            }
        }

        private static boolean checkHasStartupLogPermission(CommandSender sender) {
            if (!(sender instanceof Player) || ((Player) sender).isOp()) {
                return true;
            }

            // Fallback that can be used so non-ops can access it
            return sender.hasPermission(PERMISSION);
        }
    }

    public static class CriticalAltCommand {
        public final String name;
        public final List<String> aliases;

        public CriticalAltCommand(PluginCommand command) {
            this.name = command.getName();
            this.aliases = command.getAliases();
        }

        public CriticalAltCommand(String name) {
            this.name = name;
            this.aliases = Collections.emptyList();
        }
    }

    private static final class ServerLogRecorder extends java.util.logging.Handler {
        public static final ServerLogRecorder INSTANCE;
        public final List<StartupLogRecord> records = new ArrayList<>();
        private final List<java.util.logging.Logger> hookedLoggers = new ArrayList<>();
        private final List<CommonDependencyStartupLogHandler> upstream = new ArrayList<>();
        static {
            INSTANCE = new ServerLogRecorder();
            INSTANCE.hookedLoggers.add(Bukkit.getLogger());
            INSTANCE.hookedLoggers.add(Logger.getLogger(Bukkit.getServer().getClass().getName()));
            INSTANCE.hookedLoggers.forEach(l -> l.addHandler(INSTANCE));
        }

        public static void bind(CommonDependencyStartupLogHandler handler) {
            synchronized (INSTANCE) {
                INSTANCE.upstream.add(handler);
            }
        }

        public static void unbind() {
            synchronized (INSTANCE) {
                INSTANCE.hookedLoggers.forEach(l -> l.removeHandler(INSTANCE));
                INSTANCE.hookedLoggers.clear();
            }
        }

        @Override
        public void publish(LogRecord j_record) {
            StartupLogRecord record = new StartupLogRecord(j_record);

            synchronized (this) {
                records.add(record);
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }
    }

    private static final class StartupLogRecord implements Comparable<StartupLogRecord> {
        public final long timestamp;
        public final String content;

        public StartupLogRecord(LogRecord record) {
            timestamp = record.getMillis();
            content = StartupLogFormatter.INSTANCE.format(record);
        }

        @Override
        public int compareTo(StartupLogRecord o) {
            return Long.compare(this.timestamp, o.timestamp);
        }

        public void appendTo(StringBuilder builder) {
            builder.append(content).append('\n');
        }
    }

    private static final class StartupLogFormatter extends java.util.logging.Formatter {
        public static final StartupLogFormatter INSTANCE = new StartupLogFormatter();

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
