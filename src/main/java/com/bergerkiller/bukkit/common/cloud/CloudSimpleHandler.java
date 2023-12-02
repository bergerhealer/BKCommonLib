package com.bergerkiller.bukkit.common.cloud;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cloud.commandframework.bukkit.BukkitCaptionRegistryFactory;
import com.bergerkiller.bukkit.common.cloud.parsers.SoundEffectArgument;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.localization.LocalizationEnum;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.injection.ParameterInjector;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.arguments.parser.ParserParameter;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.BukkitCommandManager.BrigadierFailureException;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.SimpleCaptionRegistry;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.CommandExecutionException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.execution.postprocessor.CommandPostprocessor;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import cloud.commandframework.services.PipelineException;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

/**
 * Configures the Cloud Command Framework for basic use inside a Bukkit Paper or
 * Spigot server environment. Initializes the command manager itself as synchronously
 * executing (on main thread), registers annotations and help system, and also
 * registers some useful preprocessing logic.
 *
 * From annotation-registered commands you can obtain your plugin instance easily,
 * as it is made available through an injector by default.
 */
public class CloudSimpleHandler {
    private BukkitCommandManager<CommandSender> manager;
    private AnnotationParser<CommandSender> annotationParser;
    private BukkitAudiences bukkitAudiences;

    /**
     * Whether this handler for the Cloud Command Framework has been enabled
     *
     * @return True if enabled
     */
    public boolean isEnabled() {
        return this.manager != null;
    }

    /**
     * Enables and initializes the Cloud Command Framework. After this is
     * called, commands and other things can be registered.
     *
     * @param plugin Owning Bukkit Plugin for this handler
     */
    @SuppressWarnings("unchecked")
    public void enable(Plugin plugin) {
        try {
            this.manager = new PaperCommandManager<>(
                    /* Owning plugin */ plugin,
                    /* Coordinator function */ CommandExecutionCoordinator.simpleCoordinator(),
                    /* Command Sender -> C */ Function.identity(),
                    /* C -> Command Sender */ Function.identity()
            );
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to initialize the command manager", e);
        }

        // BKCommonLib captions
        this.manager.captionRegistry(new BukkitCaptionRegistryFactory<CommandSender>().create());

        // BKCommonLib argument types
        this.manager.parserRegistry().registerParserSupplier(new TypeToken<ResourceKey<SoundEffect>>() {},
                parserParameters -> new SoundEffectArgument.SoundEffectParser<>());

        // Register Brigadier mappings
        // Only do this on PaperSpigot. On base Spigot, this breaks command blocks
        boolean brigDisabled = CommonPlugin.hasInstance() && CommonPlugin.getInstance().isCloudBrigadierDisabled();
        if (!brigDisabled && manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            // Bugfix for 1.19.1+ where brigadier functionality was severely broken
            // We only use it when legacy command support (Paper) is present
            boolean isStable;
            if (Common.evaluateMCVersion(">=", "1.19.1")) {
                try {
                    // Paper API added later on to restore legacy behavior. Not present on all versions.
                    Class<?> eventClass = Class.forName("com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent");
                    eventClass.getMethod("isRawCommand");
                    isStable = true;
                } catch (Throwable t) {
                    isStable = false;
                }
            } else {
                isStable = true;
            }
            if (isStable) {
                try {
                    manager.registerBrigadier();
                    CloudBrigadierManager<?, ?> brig = manager.brigadierManager();

                    brig.setNativeNumberSuggestions(false);

                    // Register a 'sound effect name' auto-completion provider for brigadier
                    // This is automatically used for ResourceKey<SoundEffect> types so clients see their client-side sound names.
                    // As this involves a bunch of at-runtime field and class types, do this stuff in a
                    // function generated at runtime. Messy :(
                    if (CommonBootstrap.evaluateMCVersion(">=", "1.13")) {
                        try {
                            SoundEffectArgument.registerBrigadier(brig);
                        } catch (Throwable t) {
                            plugin.getLogger().log(Level.WARNING, "Failed to register sound effect auto-completion for brigadier", t);
                        }
                    }
                } catch (BrigadierFailureException ex) {
                    plugin.getLogger().log(Level.WARNING, "Failed to register commands using brigadier, " +
                            "using fallback instead. Error:", ex);
                }
            }
        }

        // Registers a custom command preprocessor that handles quote-escaping
        this.manager.registerCommandPreProcessor(new CloudCommandPreprocessor());

        // Create the annotation parser. This allows you to define commands using methods annotated with
        // @CommandMethod
        final Function<ParserParameters, CommandMeta> commandMetaFunction = p ->
                CommandMeta.simple()
                        .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description"))
                        .build();
        this.annotationParser = new AnnotationParser<>(
                /* Manager */ this.manager,
                /* Command sender type */ CommandSender.class,
                /* Mapper for command meta instances */ commandMetaFunction
        );

        // Shows the argname as <argname> as a suggestion
        // Fix for numeric arguments on the broken brigadier system
        suggest("argname", (context,b) -> Collections.singletonList("<" + context.getCurrentArgument().getName() + ">"));

        // Fixes incorrect exception handling in Cloud, so that user-specified
        // exception types can be used instead.
        handle(CommandExecutionException.class, this::handleException);
        handle(PipelineException.class, this::handleException);

        // Makes LocalizedParserException functional
        handle(CloudLocalizedException.class, (sender, ex) -> {
            sender.sendMessage(ex.getMessage());
        });

        // Suggests a player name, either of a player that is online right now,
        // or of a player that was online in the past.
        suggest("playername", (context, input) -> {
            // Try online players first
            List<String> names = Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(p -> p.startsWith(input))
                .collect(Collectors.toList());

            if (!names.isEmpty()) {
                return names;
            }

            // Try offline players second, to reduce clutter
            // TODO: Doesnt work? Weird.
            return Stream.of(Bukkit.getOfflinePlayers())
                .map(OfflinePlayer::getName)
                .filter(p -> p.startsWith(input))
                .collect(Collectors.toList());
        });

        // Make the plugin instance available through the injection system
        inject((Class<Plugin>) plugin.getClass(), plugin);

        // Used by the help system
        this.bukkitAudiences = BukkitAudiences.create(plugin);
    }

    private void handleException(CommandSender sender, Throwable exception) {
        Throwable cause = exception.getCause();

        // Find handler for this exception, if registered, execute that handler
        // If the handler throws, handle it as an internal error
        @SuppressWarnings({"unchecked", "rawtypes"})
        BiConsumer<CommandSender, Throwable> handler = manager.getExceptionHandler((Class) cause.getClass());
        if (handler != null) {
            try {
                handler.accept(sender, exception.getCause());
                return;
            } catch (Throwable t2) {
                cause = t2;
            }
        }

        // Default fallback
        this.manager.getOwningPlugin().getLogger().log(Level.SEVERE,
                "Exception executing command handler", cause);
        sender.sendMessage(ChatColor.RED + "An internal error occurred while attempting to perform this command.");
    }

    /**
     * Gets the parser instance used to parse annotated commands
     * 
     * @return parser
     */
    public AnnotationParser<CommandSender> getParser() {
        return this.annotationParser;
    }

    /**
     * Gets the command manager
     * 
     * @return manager
     */
    public CommandManager<CommandSender> getManager() {
        return this.manager;
    }

    /**
     * Register a new command postprocessor. The order they are registered in is respected, and they
     * are called in FIFO order. This is called before the command handler is executed.
     *
     * @param processor Processor to register
     */
    public void postProcess(final CommandPostprocessor<CommandSender> processor) {
        this.manager.registerCommandPostProcessor(processor);
    }

    /**
     * Register a parser supplier
     *
     * @param type     The type that is parsed by the parser
     * @param supplier The function that generates the parser. The map supplied may contain parameters used
     *                 to configure the parser, many of which are documented in {@link StandardParameters}
     * @param <T>      Generic type specifying what is produced by the parser
     */
    public <T> void parse(
            Class<T> type,
            Function<ParserParameters, ArgumentParser<CommandSender, ?>> supplier
    ) {
        this.manager.parserRegistry().registerParserSupplier(TypeToken.get(type), supplier);
    }

    /**
     * Register a parser supplier
     *
     * @param type     The type that is parsed by the parser
     * @param supplier The function that generates the parser. The map supplied my contain parameters used
     *                 to configure the parser, many of which are documented in {@link StandardParameters}
     * @param <T>      Generic type specifying what is produced by the parser
     */
    public <T> void parse(
            TypeToken<T> type,
            Function<ParserParameters, ArgumentParser<CommandSender, ?>> supplier
    ) {
        this.manager.parserRegistry().registerParserSupplier(type, supplier);
    }

    /**
     * Register a named parser supplier
     *
     * @param name     Parser name
     * @param supplier The function that generates the parser. The map supplied my contain parameters used
     *                 to configure the parser, many of which are documented in {@link StandardParameters}
     */
    public void parse(
            String name,
            Function<ParserParameters, ArgumentParser<CommandSender, ?>> supplier
    ) {
        this.manager.parserRegistry().registerNamedParserSupplier(name, supplier);
    }

    /**
     * Register a new named suggestion provider with a constant list of suggestions.
     * 
     * @param name Name of the suggestions provider. The name is case independent.
     * @param suggestions List of suggestions
     */
    public void suggest(String name, List<String> suggestions) {
        suggest(name, (sender, arg) -> suggestions);
    }

    /**
     * Register a new named suggestion provider with a no-input supplier for a list of suggestions.
     * 
     * @param name Name of the suggestions provider. The name is case independent.
     * @param suggestionsProvider The suggestions provider
     */
    public void suggest(String name, Supplier<List<String>> suggestionsProvider) {
        suggest(name, (sender, arg) -> suggestionsProvider.get());
    }

    /**
     * Register a new named suggestion provider.
     * When an argument suggestion is configured with this name, calls the function
     * to produce suggestions for that argument.
     *
     * @param name Name of the suggestions provider. The name is case independent.
     * @param suggestionsProvider The suggestions provider
     */
    public void suggest(
            String name,
            BiFunction<CommandContext<CommandSender>, String, List<String>> suggestionsProvider
    ) {
        manager.parserRegistry().registerSuggestionProvider(name, suggestionsProvider);
    }

    /**
     * Register an injector for a particular type.
     * Will automatically inject this type of object when provided in method signatures.
     *
     * @param clazz    Type that the injector should inject for. This type will matched using
     *                 {@link Class#isAssignableFrom(Class)}
     * @param value    The value to inject where clazz is used
     * @param <T>      Injected type
     */
    public <T> void inject(
            final Class<T> clazz,
            final T value
    ) {
        injector(clazz, (context, annotations) -> value);
    }

    /**
     * Register an injector for a particular type.
     * Will automatically inject this type of object when provided in method signatures.
     *
     * @param clazz    Type that the injector should inject for. This type will matched using
     *                 {@link Class#isAssignableFrom(Class)}
     * @param injector The injector that should inject the value into the command method
     * @param <T>      Injected type
     */
    public <T> void injector(
            final Class<T> clazz,
            final ParameterInjector<CommandSender, T> injector
    ) {
        this.manager.parameterInjectorRegistry().registerInjector(clazz, injector);
    }

    /**
     * Register an annotation mapper with a constant parameter value
     *
     * @param annotation Annotation class
     * @param parameter  Parameter
     * @param value      Parameter value
     * @param <A>        Annotation type
     * @param <T>        Parameter value type
     */
    public <A extends Annotation, T> void annotationParameter(
            final Class<A> annotation,
            final ParserParameter<T> parameter,
            final T value
    ) {
        manager.parserRegistry().registerAnnotationMapper(annotation, (a, typeToken) -> {
            return ParserParameters.single(parameter, value);
        });
    }

    /**
     * Register an annotation mapper with a function to read the parameter value
     * from an annotation
     *
     * @param annotation   Annotation class
     * @param parameter    Parameter
     * @param valueMapper  Mapper from annotation to parameter value
     * @param <A>          Annotation type
     * @param <T>          Parameter value type
     */
    public <A extends Annotation, T> void annotationParameter(
            final Class<A> annotation,
            final ParserParameter<T> parameter,
            final Function<A, T> valueMapper
    ) {
        manager.parserRegistry().registerAnnotationMapper(annotation, (a, typeToken) -> {
            return ParserParameters.single(parameter, valueMapper.apply(a));
        });
    }

    /**
     * Register a preprocessor mapper for an annotation.
     * The preprocessor can be created based on properties of the annotation.
     *
     * @param annotation         Annotation class
     * @param preprocessorMapper Preprocessor mapper
     * @param <A>                Annotation type
     */
    public <A extends Annotation> void preprocessAnnotation(
            final Class<A> annotation,
            final Function<A, BiFunction<CommandContext<CommandSender>, Queue<String>,
                    ArgumentParseResult<Boolean>>> preprocessorMapper
    ) {
        this.annotationParser.registerPreprocessorMapper(annotation, preprocessorMapper);
    }

    /**
     * Register a preprocessor mapper for an annotation.
     * This assumes the annotation does not store any properties, and the
     * remapper is always constant.
     *
     * @param annotation         Annotation class
     * @param preprocessorMapper Preprocessor mapper
     * @param <A>                Annotation type
     */
    public <A extends Annotation> void preprocessAnnotation(
            final Class<A> annotation,
            final BiFunction<CommandContext<CommandSender>, Queue<String>,
                    ArgumentParseResult<Boolean>> preprocessorMapper
    ) {
        preprocessAnnotation(annotation, a -> preprocessorMapper);
    }

    /**
     * Registers an exception handler for a given exception class type. This handler will be called
     * when this type of exception is thrown during command handling.
     * 
     * @param <T> Exception class type
     * @param exceptionType Type of exception to handle
     * @param handler Handler for the exception type
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T extends Throwable> void handle(Class<T> exceptionType, BiConsumer<CommandSender, T> handler) {
        this.manager.registerExceptionHandler((Class) exceptionType, (BiConsumer) handler);
    }

    /**
     * Registers a message-sending exception handler for a given exception class type. When the exception
     * is handled, the message as specified is sent to the player. If the message matches a caption
     * regex, then the message is first translated.
     * 
     * @param <T>
     * @param exceptionType
     * @param message
     */
    public <T extends Throwable> void handleMessage(Class<T> exceptionType, String message) {
        final Caption caption = Caption.of(message);
        handle(exceptionType, (sender, exception) -> {
            String translated = manager.captionRegistry().getCaption(caption, sender);
            sender.sendMessage(translated);
        });
    }

    /**
     * Registers a class instance containing command annotations
     * 
     * @param <T> Type of annotation holding Object
     * @param annotationsClassInstance Object with command annotations
     */
    public <T> void annotations(T annotationsClassInstance) {
        this.annotationParser.parse(annotationsClassInstance);
    }

    /**
     * Registers all the Localization enum constants declared in a Class as captions
     * 
     * @param localizationDefaults Enum or Class with static LocalizationEnum constants
     */
    public void captionFromLocalization(Class<? extends LocalizationEnum> localizationDefaults) {
        for (LocalizationEnum locale : CommonUtil.getClassConstants(localizationDefaults)) {
            caption(locale.getName(), locale.get().replace("%0%", "{input}"));
        }
    }

    /**
     * Registers a caption with a factory for producing the value for matched captions
     * 
     * @param regex The regex to match
     * @param messageFactory Factory for producing the desired value for a caption
     */
    public void caption(String regex, BiFunction<Caption, CommandSender, String> messageFactory) {
        if (manager.captionRegistry() instanceof SimpleCaptionRegistry) {
            final Caption caption = Caption.of(regex);
            ((SimpleCaptionRegistry<CommandSender>) manager.captionRegistry()).registerMessageFactory(
                    caption, messageFactory
            );
        }
    }

    /**
     * Registers a caption with a fixed value String
     * 
     * @param regex The regex to match
     * @param value The String value to use when the regex is matched
     */
    public void caption(String regex, String value) {
        caption(regex, (caption, sender) -> value);
    }

    /**
     * Registers a new help command for all the commands under a filter prefix
     * 
     * @param filterPrefix Command filter prefix, for commands shown in the menu
     * @param helpDescription Description of the help command
     * @return minecraft help command
     */
    public Command<CommandSender> helpCommand(
            List<String> filterPrefix,
            String helpDescription
    ) {
        return helpCommand(filterPrefix, helpDescription, builder -> builder);
    }

    /**
     * Registers a new help command for all the commands under a filter prefix
     * 
     * @param filterPrefix Command filter prefix, for commands shown in the menu
     * @param helpDescription Description of the help command
     * @param modifier Modifier for the command applied before registering
     * @return minecraft help command
     */
    public Command<CommandSender> helpCommand(
            List<String> filterPrefix,
            String helpDescription,
            Function<Command.Builder<CommandSender>, Command.Builder<CommandSender>> modifier
    ) {
        String helpCmd = "/" + String.join(" ", filterPrefix) + " help";
        final MinecraftHelp<CommandSender> help = this.help(helpCmd, filterPrefix);

        // Start a builder
        Command.Builder<CommandSender> command = Command.newBuilder(
                filterPrefix.get(0),
                CommandMeta.simple()
                    .with(CommandMeta.DESCRIPTION, helpDescription)
                    .build());

        // Add literals, then 'help'
        for (int i = 1; i < filterPrefix.size(); i++) {
            command = command.literal(filterPrefix.get(i));
        }
        command = command.literal("help");
        command = command.argument(StringArgument.<CommandSender>newBuilder("query")
                .greedy()
                .asOptional());
        command = command.handler(context -> {
            String query = context.getOrDefault("query", "");
            help.queryCommands(query, context.getSender());
        });
        command = modifier.apply(command);

        // Build & return
        Command<CommandSender> builtCommand = command.build();
        this.manager.command(builtCommand);
        return builtCommand;
    }

    /**
     * Creates a help menu
     * 
     * @param commandPrefix Help command prefix
     * @param filterPrefix Command filter prefix, for commands shown in the menu
     * @return minecraft help
     */
    public MinecraftHelp<CommandSender> help(String commandPrefix, final List<String> filterPrefix) {
        MinecraftHelp<CommandSender> help = new MinecraftHelp<>(
                commandPrefix, /* Help Prefix */
                this.bukkitAudiences::sender, /* Audience mapper */
                this.manager /* Manager */
        );

        help.commandFilter(command -> {
            List<CommandArgument<CommandSender, ?>> args = command.getArguments();
            if (args.size() < filterPrefix.size()) {
                return false;
            }
            for (int i = 0; i < filterPrefix.size(); i++) {
                if (!args.get(i).getName().equals(filterPrefix.get(i))) {
                    return false;
                }
            }
            return true;
        });

        return help;
    }
}
