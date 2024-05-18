package com.bergerkiller.bukkit.common.cloud.parsers;

import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.CommandExecutor;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.SuggestionFactory;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.incendo.cloud.suggestion.SuggestionProviderHolder;

/**
 * Helper interface for an Argument Parser that processes the input with the quoted string parser
 * first. This allows quoted name arguments to be further parsed. Makes use of the argument parser
 * mapper feature.
 *
 * @param <C> Command sender type
 * @param <T> Parser result value type
 */
public interface QuotedArgumentParser<C, T> extends SuggestionProviderHolder<C> {

    /**
     * Creates a Parser Descriptor that uses this parser to parse the quoted string
     *
     * @param outputType Output TypeToken type of this parser
     * @return Parser Descriptor
     */
    default ParserDescriptor<C, T> createDescriptor(TypeToken<T> outputType) {
        return ParserDescriptor.of(new QuotedArgumentParserProxy<>(this), outputType);
    }

    /**
     * Creates a Parser Descriptor that uses this parser to parse the quoted string
     *
     * @param outputType Output Class type of this parser
     * @return Parser Descriptor
     */
    default ParserDescriptor<C, T> createDescriptor(Class<T> outputType) {
        return ParserDescriptor.of(new QuotedArgumentParserProxy<>(this), outputType);
    }

    /**
     * Attempts to parse the quoted {@code input} string into an object of type {@link T}.
     *
     * <p>This method may be called when a command chain is being parsed for execution
     * (using {@link CommandExecutor#executeCommand(Object, String)})
     * or when a command is being parsed to provide context for suggestions
     * (using {@link SuggestionFactory#suggest(Object, String)}).
     * It is possible to use {@link CommandContext#isSuggestions()}} to see what the purpose of the
     * parsing is. Particular care should be taken when parsing for suggestions, as the parsing
     * method is then likely to be called once for every character written by the command sender.</p>
     *
     * <p>This method should never throw any exceptions under normal circumstances. Instead, if the
     * parsing for some reason cannot be done successfully {@link ArgumentParseResult#failure(Throwable)}
     * should be returned. This then wraps any exception that should be forwarded to the command sender.</p>
     *
     * <p>The parser is assumed to be completely stateless and should not store any information about
     * the command sender or the command context. Instead, information should be stored in the
     * {@link CommandContext}.</p>
     *
     * @param commandContext Command context
     * @param inputString   Command Input Quoted String
     * @return Parsed command result
     */
    ArgumentParseResult<T> parseQuotedString(CommandContext<C> commandContext, String inputString);

    @Override
    @SuppressWarnings("unchecked")
    default @NonNull SuggestionProvider<C> suggestionProvider() {
        return this instanceof SuggestionProvider ? (SuggestionProvider<C>) this : SuggestionProvider.noSuggestions();
    }
}
