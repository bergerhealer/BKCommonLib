# Cloud Command Framework Integration

BKCommonLib provides [`CloudSimpleHandler`](../src/main/java/com/bergerkiller/bukkit/common/cloud/CloudSimpleHandler.java) as a Bukkit-oriented setup and registration layer for Incendo Cloud. This guide covers BKCommonLib's integration and helpers; for dependency setup, shading/relocation, and Cloud's general command syntax, see [`CLOUD_HOWTO.md`](../CLOUD_HOWTO.md) and the [Cloud documentation](https://cloud.incendo.org/).

## Lifecycle and command registration

Create one handler for the plugin and call `enable(plugin)` before using its parser, manager, or registration helpers. Enabling creates the Cloud manager and annotation parser, installs BKCommonLib's captions and sound-effect parsing support, registers default suggestions and exception handling, and makes the owning plugin available for command parameter injection.

Use `annotations(instance)` to register methods annotated with Cloud's `@Command`, `@Argument`, and related annotations. Use `getManager()` when a command needs Cloud's lower-level builder API or a feature not wrapped by `CloudSimpleHandler`. The manager can also register named suggestion providers directly through its parser registry.

```java
private final CloudSimpleHandler commands = new CloudSimpleHandler();

public void enable(MyPlugin plugin) {
    commands.enable(plugin);
    commands.annotations(new ExampleCommands());
}

public final class ExampleCommands {
    @Command("example")
    private void example(MyPlugin plugin, CommandSender sender) {
        // The plugin passed to enable() is injectable by its concrete type.
    }
}
```

`isEnabled()` can be used to check whether setup has completed. `getParser()` exposes the annotation parser, while `getManager()` exposes the underlying Cloud `CommandManager<CommandSender>`.

## `CloudSimpleHandler` conveniences

| API | Use |
|---|---|
| `parse(...)` | Register a parser by result class, `TypeToken`, `ParserDescriptor`, or name. Supplier overloads can use Cloud parser parameters when constructing a parser. |
| `suggest(name, ...)` | Register a named suggestion source for use by command annotations. Sources can be fixed values or context/input-aware suggestion providers. |
| `inject(type, value)` / `injector(type, ...)` | Make a fixed value or context-dependent value available to command parameters and `CommandContext.inject(...)`. |
| `captionFromLocalization(...)` / `caption(...)` | Connect Cloud captions to a BKCommonLib localization enum or register custom caption values/providers. |
| `handleMessage(exceptionType, captionKey)` / `handle(exceptionType, handler)` | Turn command exceptions into a Cloud caption or custom sender-specific handling. |
| `helpCommand(prefix, description)` / `help(...)` | Register or create help output for a command prefix, optionally filtered or customized. |

Use `getParser().registerBuilderModifier(annotationType, modifier)` to make a custom method annotation modify the Cloud command builder. This is suitable for cross-cutting annotations such as permissions, checks, or extra command metadata. The modifier receives the annotation instance and builder, so it can derive behavior from annotation values.

Use `getManager()` directly for full Cloud access, for example to register a command built with `commandBuilder(...)` or a suggestion provider through `parserRegistry()`. Keep commands registered through the same enabled manager so they share BKCommonLib's integrations.

## Argument parsers and suggestions

Register a parser descriptor directly when its type and parameters are fixed:

```java
commands.parse(MyDirectionParser.parser());
```

Register a parser factory by result type when parser behavior depends on parameter annotations, such as Cloud's `greedy` parameter:

```java
commands.parse(FormattedSpeed.class, parameters ->
        new FormattedSpeedParser(parameters.get(StandardParameters.GREEDY, false)));
```

Named parsers are useful when the same parser configuration is selected from an annotation:

```java
commands.parse("attachmentNames", parameters -> new AttachmentNameParser());
commands.annotations(new ExampleCommands());
```

Then reference the parser name with the relevant Cloud argument metadata. For context-aware completions, register a named suggestion with `suggest(name, provider)` or use the underlying manager's `parserRegistry().registerSuggestionProvider(...)`. Named suggestions receive the command context and current input, which allows them to filter values by sender, permissions, or other injected command state.

BKCommonLib also registers a `playername` suggestion provider. For example, annotate a player-name argument with `suggestions = "playername"`. It suggests matching online player names first and only falls back to offline player names when there are no online matches.

### Quoted arguments

[`QuotedArgumentParser<C, T>`](../src/main/java/com/bergerkiller/bukkit/common/cloud/parsers/QuotedArgumentParser.java) adapts a parser that consumes a decoded string. Implement `parseQuotedString(context, input)` and create a parser descriptor with `createDescriptor(ResultType.class)` (or a parser with `createParser()`). The adapter handles quoted/unquoted input and keeps completion suggestions compatible with the selected quoting rules.

```java
final class TrainNameParser implements QuotedArgumentParser<CommandSender, TrainName> {
    @Override
    public ArgumentParseResult<TrainName> parseQuotedString(
            CommandContext<CommandSender> context, String input
    ) {
        TrainName name = parseName(input);
        if (name == null) {
            return ArgumentParseResult.failure(
                    new CloudLocalizedException(context, Messages.INVALID_NAME, input));
        }
        return ArgumentParseResult.success(name);
    }
}
```

The default quote mode is permissive: values containing spaces or double quotes need quoting, while other characters can remain unquoted. Override `isStrictQuoteEscaping()` for Brigadier-style strict unquoted values, which are limited to letters, digits, `_`, `-`, `.`, and `+`. When implementing a suggestion provider, return raw values and let the adapter quote or filter them. The `permissiveQuotedSuggestions(...)` and `strictQuotedSuggestions(...)` helpers are also available when adapting suggestions without implementing the parser.

Use `QuotedArgumentParser.getFromParser(parser)` when code needs to inspect a Cloud parser and recover its BKCommonLib quoted-parser adapter. It returns `null` when the parser is not such an adapter; prefer this helper over depending on the proxy implementation class.

[`UnquotedCharacterFilter`](../src/main/java/com/bergerkiller/bukkit/common/cloud/parsers/UnquotedCharacterFilter.java) exposes the permissive and strict policies and helpers for checking, escaping, and unescaping values. [`QuotedLinesParser`](../src/main/java/com/bergerkiller/bukkit/common/cloud/parsers/QuotedLinesParser.java) is a separate utility for splitting greedy text into space-separated values while preserving quoted phrases. It splits on literal spaces, recognizes quotes at the beginning of a token, and falls back to ordinary word parsing if a closing quote is missing. It is mutable and accumulates results on each `parse()` call; create a fresh instance when parsing a new input.

## Localized errors and captions

[`CloudLocalizedException`](../src/main/java/com/bergerkiller/bukkit/common/cloud/CloudLocalizedException.java) carries the Cloud command context, a BKCommonLib `ILocalizationEnum` value, and formatting arguments. `CloudSimpleHandler.enable()` installs handling for this exception, so a custom parser can return it as a parse failure and the localized message is sent to the command sender. Use `handleMessage(...)` for exceptions that map directly to a Cloud caption key, or `handle(...)` when handling requires custom formatting or sender-specific behavior.

`captionFromLocalization(MyLocalization.class)` registers captions from a localization enum. Use `caption(key, value)` or the provider overload when a caption is not backed by a localization enum.

## Sound-effect arguments

Enabling `CloudSimpleHandler` registers parsing for `ResourceKey<SoundEffect>`. Use that result type in annotation-based commands to accept a Minecraft sound name and receive a typed resource key. Suggestions come from the registered sound effects. For a command built directly with Cloud's manager, use [`SoundEffectParser.soundEffectParser()`](../src/main/java/com/bergerkiller/bukkit/common/cloud/parsers/SoundEffectParser.java) as its parser descriptor. When native Brigadier integration is active, BKCommonLib also maps this parser to Minecraft's identifier argument and sound suggestions.

## Compatibility notes

- BKCommonLib may disable native Brigadier integration based on server capability, compatibility, or configuration. Do not rely on Brigadier-only behavior for command execution or suggestions.
- Cloud and its dependencies are relocated inside BKCommonLib. A dependent plugin must apply the matching relocations when shading, as documented in [`CLOUD_HOWTO.md`](../CLOUD_HOWTO.md).
- Avoid retaining or reusing mutable parser helpers such as `QuotedLinesParser` across independent command inputs.
