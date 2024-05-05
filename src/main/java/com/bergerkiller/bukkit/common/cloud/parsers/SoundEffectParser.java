package com.bergerkiller.bukkit.common.cloud.parsers;

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
import org.incendo.cloud.brigadier.CloudBrigadierManager;
import org.incendo.cloud.brigadier.argument.BrigadierMappingBuilder;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * cloud argument type that parses <i>ResourceKey&lt;SoundEffect&gt;</i>
 *
 * @param <C> Command sender type
 */
public class SoundEffectParser<C> implements ArgumentParser<C, ResourceKey<SoundEffect>>, BlockingSuggestionProvider.Strings<C> {
    public static <C> @NonNull ParserDescriptor<C, ResourceKey<SoundEffect>> soundEffectParser() {
        return ParserDescriptor.of(new SoundEffectParser<>(), new TypeToken<ResourceKey<SoundEffect>>() {
        });
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull ResourceKey<SoundEffect>> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        String input = commandInput.peekString();
        ResourceKey<SoundEffect> result = SoundEffect.fromName(input);
        if (result == null) {
            return ArgumentParseResult.failure(new SoundEffectParseException(input, commandContext));
        }

        commandInput.readString();
        return ArgumentParseResult.success(result);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        Collection<MinecraftKeyHandle> keys = SoundEffectHandle.getSoundNames();
        List<String> suggestions = new ArrayList<>(keys.size());
        for (MinecraftKeyHandle key : keys) {
            suggestions.add(key.toString());
        }
        return suggestions;
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
                new TypeToken<com.bergerkiller.bukkit.common.cloud.parsers.SoundEffectParser<CommandSender>>() {
                },
                (Consumer<BrigadierMappingBuilder<?, ?>>) builder -> {
                    builderTo.invoke(builder, (Function<Object, Object>) o -> createResourceLocationArgument.invoke(null));
                    builder.suggestedBy(CommonUtil.unsafeCast(suggestionProvider));
                }
        );
    }
}
