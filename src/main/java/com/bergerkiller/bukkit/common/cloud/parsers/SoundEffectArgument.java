package com.bergerkiller.bukkit.common.cloud.parsers;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.brigadier.BrigadierMappingBuilder;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.cloud.captions.BKCommonLibCaptionKeys;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.sounds.SoundEffectHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.LambdaBuilder;
import io.leangen.geantyref.TypeToken;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * cloud argument type that parses <i>ResourceKey&lt;SoundEffect&gt;</i>
 *
 * @param <C> Command sender type
 */
public class SoundEffectArgument<C> extends CommandArgument<C, ResourceKey<SoundEffect>> {

    protected SoundEffectArgument(
            final boolean required,
            final String name,
            final String defaultValue,
            final BiFunction<CommandContext<C>, String,
                                List<String>> suggestionsProvider,
            final ArgumentDescription defaultDescription
    ) {
        super(required, name,
                new SoundEffectParser<>(),
                defaultValue,
                new TypeToken<ResourceKey<SoundEffect>>() {},
                suggestionsProvider,
                defaultDescription);
    }

    /**
     * Create a new {@link Builder}.
     *
     * @param name argument name
     * @param <C>  sender type
     * @return new {@link Builder}
     * @since 1.8.0
     */
    public static <C> Builder<C> builder(final String name) {
        return new Builder<>(name);
    }

    /**
     * Create a new required argument
     *
     * @param name Argument name
     * @param <C>  Command sender type
     * @return Created argument
     */
    public static <C> CommandArgument<C, ResourceKey<SoundEffect>> of(final String name) {
        return SoundEffectArgument.<C>builder(name).asRequired().build();
    }

    /**
     * Create a new optional argument
     *
     * @param name Argument name
     * @param <C>  Command sender type
     * @return Created argument
     */
    public static <C> CommandArgument<C, ResourceKey<SoundEffect>> optional(final String name) {
        return SoundEffectArgument.<C>builder(name).asOptional().build();
    }

    /**
     * Create a new optional argument with a default value
     *
     * @param name        Argument name
     * @param soundEffect Default value
     * @param <C>         Command sender type
     * @return Created argument
     */
    public static <C> CommandArgument<C, ResourceKey<SoundEffect>> optional(
            final String name,
            final ResourceKey<SoundEffect> soundEffect
    ) {
        return SoundEffectArgument.<C>builder(name).asOptionalWithDefault(soundEffect.getPath()).build();
    }

    public static final class Builder<C> extends CommandArgument.Builder<C, ResourceKey<SoundEffect>> {

        private Builder(final String name) {
            super(new TypeToken<ResourceKey<SoundEffect>>() {}, name);
        }

        @Override
        public CommandArgument<C, ResourceKey<SoundEffect>> build() {
            return new SoundEffectArgument<>(
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider(),
                    this.getDefaultDescription()
            );
        }
    }

    public static class SoundEffectParser<C> implements ArgumentParser<C, ResourceKey<SoundEffect>> {
        @Override
        public ArgumentParseResult<ResourceKey<SoundEffect>> parse(
                final CommandContext<C> commandContext,
                final Queue<String> inputQueue
        ) {
            if (inputQueue.isEmpty()) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        this.getClass(),
                        commandContext
                ));
            }

            String input = inputQueue.peek();
            ResourceKey<SoundEffect> result = SoundEffect.fromName(input);
            if (result == null) {
                return ArgumentParseResult.failure(new SoundEffectParseException(input, commandContext));
            }

            inputQueue.remove();
            return ArgumentParseResult.success(result);
        }

        @Override
        public List<String> suggestions(
                final CommandContext<C> commandContext,
                final String input
        ) {
            Collection<MinecraftKeyHandle> keys = SoundEffectHandle.getSoundNames();
            List<String> suggestions = new ArrayList<>(keys.size());
            for (MinecraftKeyHandle key : keys) {
                suggestions.add(key.toString());
            }
            return suggestions;
        }
    }

    public static final class SoundEffectParseException extends ParserException {

        private static final long serialVersionUID = 1615554107385965610L;
        private final String input;

        /**
         * Construct a new SoundEffectParseException
         *
         * @param input   Input
         * @param context Command context
         */
        public SoundEffectParseException(
                final String input,
                final CommandContext<?> context
        ) {
            super(
                    SoundEffectParser.class,
                    context,
                    BKCommonLibCaptionKeys.ARGUMENT_PARSE_FAILURE_SOUNDEFFECT,
                    CaptionVariable.of("input", input)
            );
            this.input = input;
        }

        /**
         * Get the input
         *
         * @return Input
         */
        public String getInput() {
            return this.input;
        }
    }

    /**
     * Registers the vanilla sound effect suggestions in brigadier for the ResourceKey&lt;SoundEffect&gt; type.
     * This is a mess because BKCommonLib does not 'see' internal types that it needs for this integration.
     *
     * @param brig CloudBrigadierManager
     * @throws Exception
     */
    public static void registerBrigadier(CloudBrigadierManager<?, ?> brig) throws Exception {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClassName("net.minecraft.commands.synchronization.CompletionProviders"); // SuggestionProviders
        resolver.addImport("net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered"); // ResourceLocationArgument
        resolver.addImport("com.mojang.brigadier.suggestion.SuggestionProvider");
        resolver.setAllVariables(Common.TEMPLATE_RESOLVER);

        // Get a method that can produce a new ResourceLocationArgument instance
        final FastMethod<Object> createResourceLocationArgument;
        {
            MethodDeclaration createResourceLocationArgumentMethod = new MethodDeclaration(resolver, SourceDeclaration.preprocess("" +
                            "public static ArgumentMinecraftKeyRegistered createArgument() {\n" +
                            "#if version >= 1.18\n" +
                            "    return ArgumentMinecraftKeyRegistered.id();\n" +
                            "#else\n" +
                            "    return ArgumentMinecraftKeyRegistered.a();\n" +
                            "#endif\n" +
                            "}",
                    resolver));
            createResourceLocationArgument = new FastMethod<>(createResourceLocationArgumentMethod);
            createResourceLocationArgument.forceInitialization();
        }

        // Get the AVAILABLE_SOUNDS SuggestionProvider instance
        final Object soundSuggestionProvider;
        {
            MethodDeclaration createSuggestionProviderMethod = new MethodDeclaration(resolver, SourceDeclaration.preprocess("" +
                            "public static SuggestionProvider getSoundSuggestions() {\n" +
                            "#if version >= 1.17\n" +
                            "    return CompletionProviders.AVAILABLE_SOUNDS;\n" +
                            "#else\n" +
                            "    return CompletionProviders.c;\n" +
                            "#endif\n" +
                            "}",
                    resolver));
            FastMethod<Object> createSuggestionProvider = new FastMethod<>(createSuggestionProviderMethod);
            soundSuggestionProvider = createSuggestionProvider.invoke(null);
        }

        final BrigadierMappingBuilder.SuggestionProviderSupplier<?, ?> suggestionProvider =
                LambdaBuilder.of(BrigadierMappingBuilder.SuggestionProviderSupplier.class)
                             .createConstant(soundSuggestionProvider);

        final FastMethod<Object> registerMapping = new FastMethod<>(CloudBrigadierManager.class.getMethod("registerMapping", TypeToken.class, Consumer.class));
        final FastMethod<Object> builderTo = new FastMethod<>(BrigadierMappingBuilder.class.getMethod("to", Function.class));

        registerMapping.forceInitialization();
        builderTo.forceInitialization();

        registerMapping.invoke(brig,
                new TypeToken<SoundEffectArgument.SoundEffectParser<CommandSender>>() {},
                (Consumer<BrigadierMappingBuilder<?, ?>>) builder -> {
                    builderTo.invoke(builder, (Function<Object, Object>) o -> createResourceLocationArgument.invoke(null));
                    builder.suggestedBy(CommonUtil.unsafeCast(suggestionProvider));
                }
        );
    }
}
