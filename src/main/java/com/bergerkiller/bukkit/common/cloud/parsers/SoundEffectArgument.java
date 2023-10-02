package com.bergerkiller.bukkit.common.cloud.parsers;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.bergerkiller.bukkit.common.cloud.captions.BKCommonLibCaptionKeys;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import io.leangen.geantyref.TypeToken;

import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

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
}
