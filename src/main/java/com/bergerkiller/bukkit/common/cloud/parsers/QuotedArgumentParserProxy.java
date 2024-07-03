package com.bergerkiller.bukkit.common.cloud.parsers;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.brigadier.CloudBrigadierManager;
import org.incendo.cloud.brigadier.argument.BrigadierMapping;
import org.incendo.cloud.brigadier.argument.BrigadierMappingBuilder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * This argument parser wraps the {@link QuotedArgumentParser} in such a way that brigadier can pick
 * it up and correctly register the argument with the quoted string() type.
 *
 * @param <C> Command sender type
 * @param <T> Output value type
 */
final class QuotedArgumentParserProxy<C, T> implements ArgumentParser<C, T>, SuggestionProvider<C> {
    private final ArgumentParser<C, String> baseParser = new StringParser<>(StringParser.StringMode.QUOTED);
    private final QuotedArgumentParser<C, T> parser;

    public QuotedArgumentParserProxy(QuotedArgumentParser<C, T> parser) {
        this.parser = parser;
    }

    public UnquotedCharacterFilter getCharFilter() {
        return parser.isStrictQuoteEscaping() ? UnquotedCharacterFilter.STRICT : UnquotedCharacterFilter.PERMISSIVE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull ArgumentParseResult<@NonNull T> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        ArgumentParseResult<String> baseResult = baseParser.parse(commandContext, commandInput);
        Optional<String> baseParsed = baseResult.parsedValue();
        if (baseParsed.isPresent()) {
            return parser.parseQuotedString(commandContext, baseParsed.get());
        } else {
            // No value so I guess this is fine?
            return (ArgumentParseResult<T>) baseResult;
        }
    }

    @NonNull
    public SuggestionProvider<C> suggestionProvider() {
        return this;
    }

    @Override
    public CompletableFuture<? extends Iterable<? extends Suggestion>> suggestionsFuture(
            final CommandContext<C> context,
            final CommandInput commandInput
    ) {
        String input = commandInput.lastRemainingToken();
        if (input.startsWith("\"")) {
            // Rewrite the input as if it was typed without quotes
            final CommandInput unescapedInput = CommandInput.of(UnquotedCharacterFilter.unescapeString(input));

            // Rewrite all underlying suggestions as "-escaped
            return createSuggestions(context, unescapedInput, (suggestions, newSuggestions) -> {
                for (Suggestion suggestion : suggestions) {
                    newSuggestions.add(suggestion.withSuggestion(UnquotedCharacterFilter.escapeString(suggestion.suggestion())));
                }
            });
        } else if (input.isEmpty()) {
            // Simply show all suggestions, quote-escape those suggestions that require it
            final UnquotedCharacterFilter filter = getCharFilter();
            return createSuggestions(context, commandInput, (suggestions, newSuggestions) -> {
                for (Suggestion suggestion : suggestions) {
                    newSuggestions.add(suggestion.withSuggestion(filter.escapeStringIfNeeded(suggestion.suggestion())));
                }
            });
        } else {
            // Show all suggestions that do not require quote-escaping
            final UnquotedCharacterFilter filter = getCharFilter();
            return createSuggestions(context, commandInput, (suggestions, newSuggestions) -> {
                for (Suggestion suggestion : suggestions) {
                    if (filter.isStringAllowed(suggestion.suggestion())) {
                        newSuggestions.add(suggestion);
                    }
                }
            });
        }
    }

    private CompletableFuture<? extends Iterable<? extends Suggestion>> createSuggestions(
            final CommandContext<C> commandContext,
            final CommandInput commandInput,
            final BiConsumer<Iterable<? extends Suggestion>, List<Suggestion>> mapperAndFilter
    ) {
        return parser.suggestionProvider().suggestionsFuture(commandContext, commandInput)
                .thenApplyAsync(suggestions -> {
                    List<Suggestion> newSuggestions;
                    if (suggestions instanceof Collection) {
                        newSuggestions = new ArrayList<>(((Collection<?>) suggestions).size());
                    } else {
                        newSuggestions = new ArrayList<>();
                    }
                    mapperAndFilter.accept(suggestions, newSuggestions);
                    return newSuggestions;
                });
    }

    public static void appendEscapedStringToBuilder(StringBuilder builder, String str, int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; i++) {
            appendEscapedCharToBuilder(builder, str.charAt(i));
        }
    }

    public static void appendEscapedCharToBuilder(StringBuilder builder, char c) {
        if (c == '\\' || c == '"') {
            builder.append('\\');
        }
        builder.append(c);
    }

    /**
     * Registers the quoted argument parser into brigadier.
     *
     * @param brig CloudBrigadierManager
     * @throws Exception Anything can go wrong
     */
    @SuppressWarnings({"rawtypes", "unchecked", "JavaReflectionInvocation", "JavaReflectionMemberAccess"})
    public static void registerBrigadier(CloudBrigadierManager<?, ?> brig) throws Exception {
        // This horrible fucking code makes the "QuotedArgumentParser" work correctly.
        // Cloud does not support registering a mapper for "all classes that are an instance of", so
        // we use a proxy final class that is always the same class type to make this work.
        // In brigadier we register this to use the StringArgumentType quoted string().
        // This makes it all work properly.
        // Brig isn't on our class path so there is this mess of a code. Oh well.
        //
        // Hopefully someday there is a nicer API in Cloud to get this job done, like a mapper
        // that allows setting a suggestion provider on an existing parser.
        //
        // There is no other way currently. The only type allowing this to work must be the Mapping argument parser.
        // That one cannot be inherited.
        // See:
        // https://github.com/Incendo/cloud-minecraft/blob/master/cloud-brigadier/src/main/java/org/incendo/cloud/brigadier/node/LiteralBrigadierNodeFactory.java#L251

        Class<?> stringArgumentTypeClass = Class.forName("com.mojang.brigadier.arguments.StringArgumentType");
        Class<?> argumentTypeClass = Class.forName("com.mojang.brigadier.arguments.ArgumentType");

        Object quotedStringArgumentType = stringArgumentTypeClass.getMethod("string").invoke(null);

        BrigadierMappingBuilder builder = BrigadierMapping.builder();
        builder = builder.cloudSuggestions();
        builder = (BrigadierMappingBuilder) BrigadierMappingBuilder.class.getMethod("toConstant", argumentTypeClass)
                .invoke(builder, quotedStringArgumentType);
        brig.mappings().registerMapping(QuotedArgumentParserProxy.class, builder.build());
    }
}
