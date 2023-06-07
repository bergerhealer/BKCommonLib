package com.bergerkiller.bukkit.common.config.yaml;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.SafeField;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.config.HeaderBuilder;
import com.bergerkiller.bukkit.common.config.NodeBuilder;
import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializer;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.mountiplex.reflection.SafeMethod;

/**
 * Helper class for deserializing values and headers from YAML-encoded text
 */
public class YamlDeserializer {
    private final Yaml yaml;
    private final PreParser preParser;

    /**
     * A singleton instance of the YamlDeserializer. Is thread-safe.
     */
    public static final YamlDeserializer INSTANCE = new YamlDeserializer();

    /**
     * Creates a new YamlDeserializer
     */
    public YamlDeserializer() {
        // This is used to de-serialize custom types
        YamlConstructorFactory ctorFactory = new YamlConstructorFactory();

        // Check whether the SnakeYAML library currently in use imposes DOS protection
        // We want to turn this off so that de-serialization cannot break horrible with large files
        // DOS isn't our concern, so yeet it.
        Yaml yaml = null;
        Class<?> loaderOptionsType = CommonUtil.getClass("org.yaml.snakeyaml.LoaderOptions");
        if (loaderOptionsType != null && SafeMethod.contains(loaderOptionsType, "setNestingDepthLimit", int.class)) {
            try {
                Constructor<Yaml> constr = Yaml.class.getConstructor(BaseConstructor.class, Representer.class, DumperOptions.class, LoaderOptions.class, Resolver.class);

                Representer representer = new Representer(new DumperOptions());
                DumperOptions dumperOptions = new DumperOptions();
                dumperOptions.setDefaultFlowStyle(representer.getDefaultFlowStyle());
                dumperOptions.setDefaultScalarStyle(representer.getDefaultScalarStyle());
                dumperOptions.setAllowReadOnlyProperties(representer.getPropertyUtils().isAllowReadOnlyProperties());
                dumperOptions.setTimeZone(representer.getTimeZone());
                LoaderOptions loaderOptions = new LoaderOptions();
                LoaderOptions.class.getMethod("setNestingDepthLimit", int.class).invoke(loaderOptions, Integer.MAX_VALUE);
                LoaderOptions.class.getMethod("setCodePointLimit", int.class).invoke(loaderOptions, Integer.MAX_VALUE);
                LoaderOptions.class.getMethod("setMaxAliasesForCollections", int.class).invoke(loaderOptions, Integer.MAX_VALUE);
                Resolver resolver = new Resolver();
                SafeConstructor yamlConstructor = ctorFactory.create(loaderOptions);
                yaml = constr.newInstance(yamlConstructor, representer, dumperOptions, loaderOptions, resolver);
            } catch (Throwable t) {
                Logging.LOGGER_CONFIG.log(Level.SEVERE, "Failed to disable SnakeYAML document size limits", t);

                // Fallback
                SafeConstructor yamlConstructor = ctorFactory.create();
                yaml = new Yaml(yamlConstructor);
            }
        } else {
            // Default constructor
            SafeConstructor yamlConstructor = ctorFactory.create();
            yaml = new Yaml(yamlConstructor);
        }

        this.yaml = yaml;
        this.preParser = new PreParser();
    }

    /**
     * Deserializes a Yaml String
     * 
     * @param yamlString The String containing the YAML-encoded text
     * @return The deserialized YAML data, containing the values and headers
     * @throws YAMLException When the YAML-encoded text is malformed
     */
    public synchronized Output deserialize(String yamlString) throws YAMLException {
        return deserialize(new StringReader(yamlString));
    }

    /**
     * Deserializes a Yaml String read from a Reader.
     * The stream is automatically closed, also when errors occur.
     * 
     * @param reader The reader to read YAML-encoded text from
     * @return The deserialized YAML data, containing the values and headers
     * @throws YAMLException When the YAML-encoded text is malformed or an IO Exception occurs
     */
    public synchronized Output deserialize(Reader reader) throws YAMLException {
        try {
            this.preParser.open(reader);
            Output output = new Output();
            output.root = CommonUtil.tryCast(this.yaml.load(this.preParser), Map.class, Collections.emptyMap());
            output.headers = new HashMap<YamlPath, String>(this.preParser.headers);
            if (this.preParser.mainHeader.length() > 0) {
                output.headers.put(YamlPath.ROOT, this.preParser.mainHeader.toString());
            }
            output.indent = this.preParser.nodeBuilder.getIndent();
            if (output.indent == -1) {
                output.indent = 2;
            }
            return output;
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {}
        }
    }

    /**
     * Data generated by the Yaml Deserializer
     */
    public static class Output {
        /**
         * The amount of spaces of indentation in the document
         */
        public int indent;
        /**
         * Decoded root value map
         */
        public Map<?, ?> root;
        /**
         * All the headers, mapped by YamlPath key
         */
        public Map<YamlPath, String> headers;
    }

    private static class PreParser extends Reader {
        private Reader baseReader;
        private HeaderBuilder headerBuilder = new HeaderBuilder();
        private final NodeBuilder nodeBuilder = new NodeBuilder(-1);
        private StringBuilder mainHeader = new StringBuilder();
        private Map<YamlPath, String> headers = new HashMap<YamlPath, String>();
        private StringBuilder currentLine = new StringBuilder();
        private int currentColumn = 0;

        public void open(Reader reader) {
            this.baseReader = reader;
            this.headerBuilder.clear();
            this.nodeBuilder.reset(-1);
            this.mainHeader.setLength(0);
            this.headers.clear();
            this.currentColumn = 0;
            this.currentLine.setLength(0);
        }

        @Override
        public int read() throws IOException {
            if (currentColumn == currentLine.length()) {
                if (this.readNextLine()) {
                    currentColumn = 0;
                } else {
                    currentColumn = currentLine.length();
                    return -1;
                }
            }
            return currentLine.charAt(currentColumn++);
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            if (len == 0) {
                return 0;
            }

            int startLen = len;
            do {
                int available = (currentLine.length() - currentColumn);
                if (available == 0) {
                    if (this.readNextLine()) {
                        currentColumn = 0;
                        available = currentLine.length();
                    } else {
                        currentColumn = currentLine.length();
                        break;
                    }
                }
                if (len > available) {
                    currentLine.getChars(currentColumn, currentLine.length(), cbuf, off);
                    currentColumn = currentLine.length();
                    off += available;
                    len -= available;
                } else {
                    currentLine.getChars(currentColumn, currentColumn + len, cbuf, off);
                    currentColumn += len;
                    return startLen;
                }
            } while (len > 0);

            return (len == startLen) ? -1 : (startLen - len);
        }

        @Override
        public void close() throws IOException {
            this.baseReader.close();
        }

        // Single attempt at reading a line of text
        // If the line is 'swallowed' then this returns true for another try
        private boolean readNextLine() throws IOException {
            this.currentLine.setLength(0);
            int rawChar;
            int numNewlineChars = 0;
            while ((rawChar = this.baseReader.read()) != -1) {
                char c = (char) rawChar;
                this.currentLine.append(c);
                if (c == '\r') {
                    numNewlineChars++;
                } else if (c == '\n') {
                    numNewlineChars++;
                    break;
                }
            }

            // If nothing was read, abort before doing anything more
            if (this.currentLine.length() == 0) {
                return false;
            }

            // Replace tabs at the beginning with spaces
            // We assume one tab = one indent, and if no spaces found, is turned into 2 spaces
            for (int i = 0; i < this.currentLine.length(); i++) {
                char c = this.currentLine.charAt(i);
                if (c == '\t') {
                    this.currentLine.setCharAt(i, ' ');
                    if (this.nodeBuilder.getIndent() == -1) {
                        this.nodeBuilder.setIndent(2);
                    }
                    for (int n = 1; n < this.nodeBuilder.getIndent(); n++) {
                        this.currentLine.insert(++i, ' ');
                    }
                } else if (c != ' ') {
                    break;
                }
            }

            // Find the start indent offset
            int contentStart = 0;
            while (contentStart < this.currentLine.length() && this.currentLine.charAt(contentStart) == ' ') {
                contentStart++;
            }
            int contentEnd = this.currentLine.length() - numNewlineChars;
            int contentLen = (contentEnd - contentStart);

            // Replace *: with '*':
            if (contentLen == 2
                    && this.currentLine.charAt(contentEnd-2) == '*'
                    && this.currentLine.charAt(contentEnd-1) == ':')
            {
                this.currentLine.replace(contentEnd-2, contentEnd, "'*':");
                contentEnd += 2;
                contentLen += 2;
            }

            // Detect the main header of the file at indent 0
            if (contentStart == 0 && contentLen >= 2
                    && this.currentLine.charAt(0) == '#'
                    && this.currentLine.charAt(1) == '>')
            {
                int headerStart = 2;
                if (headerStart < contentEnd && this.currentLine.charAt(2) == ' ') {
                    headerStart++;
                }
                if (this.mainHeader.length() > 0) {
                    this.mainHeader.append('\n');
                }
                this.mainHeader.append(this.currentLine, headerStart, contentEnd);
                this.currentLine.setLength(1);
                this.currentLine.setCharAt(0, '\n');
                return true;
            }

            // Handle a header line for a node
            if (this.headerBuilder.handle(this.currentLine, contentStart, contentEnd)) {
                this.currentLine.setLength(1);
                this.currentLine.setCharAt(0, '\n');
                return true;
            }

            // Handle a node line
            this.nodeBuilder.handle(this.currentLine, contentStart, contentEnd);

            // Apply a found header to the node if available
            if (this.headerBuilder.hasHeader()) {
                this.headers.put(nodeBuilder.getYamlPath(), headerBuilder.getHeader());
                this.headerBuilder.clear();
            }

            // Decode chat color style characters found in the YAML line
            // For example, this replaces &c with §c
            // To write an ampersand in an awkward place, && can be used
            for (int i = contentStart; i < (contentEnd-1); i++) {
                if (this.currentLine.charAt(i) == '&') {
                    char following = this.currentLine.charAt(i + 1);
                    if (following == '&') {
                        this.currentLine.deleteCharAt(i);
                        contentEnd--;
                    } else if (StringUtil.isChatCode(following)) {
                        this.currentLine.setCharAt(i, StringUtil.CHAT_STYLE_CHAR);
                        i++;
                    }
                }
            }

            // Done, give the line to the parser!
            return true;
        }
    }

    // Custom version of org.bukkit.configuration.file.YamlConstructor
    // This allows us to add our own handlers for certain object types
    private static class YamlConstructorFactory {
        private final Map<String, Function<Map<String, Object>, ? extends Object>> custom_builders;

        public YamlConstructorFactory() {
            this.custom_builders = new HashMap<>();

            this.register("org.bukkit.inventory.ItemStack", ItemStackDeserializer.INSTANCE);

            // On versions 1.12.2 and before we must keep a backup of the original Map that created the
            // ItemMeta as this is important for restoring some properties like Damage that get lost
            // due to CraftItemMeta not storing these.
            if (CommonCapabilities.NEEDS_LEGACY_ITEMMETA_MIGRATION) {
                this.register("org.bukkit.inventory.ItemMeta", ItemStackDeserializer.LegacyItemMeta.DESERIALIZER);
                this.register("ItemMeta", ItemStackDeserializer.LegacyItemMeta.DESERIALIZER);
            }
        }

        private void register(String typeName, Function<Map<String, Object>, ? extends Object> builder) {
            custom_builders.put(typeName, builder);
        }

        public SafeConstructor create(LoaderOptions loaderOptions) {
            SafeConstructor ctor = new SafeConstructor(loaderOptions);
            initConstructor(ctor);
            return ctor;
        }

        public SafeConstructor create() {
            // If empty constructor is available, use that. Otherwise, instantiate
            // new LoaderOptions and use the other method.
            try {
                Constructor<SafeConstructor> ctor_ctor = SafeConstructor.class.getConstructor();
                SafeConstructor ctor = ctor_ctor.newInstance();
                initConstructor(ctor);
                return ctor;
            } catch (Throwable t) {
                return createWithDefaultOptions();
            }
        }

        private SafeConstructor createWithDefaultOptions() {
            return create(new LoaderOptions());
        }

        private void initConstructor(SafeConstructor ctor) {
            try {
                Map<Tag, Construct> yamlConstructors = CommonUtil.unsafeCast(SafeField.get(ctor, "yamlConstructors", Map.class));

                // Replace the 'Map' constructor with one that handles our custom type de-serialization logic
                // The original 'Map' constructor is re-purposed to construct the map itself (super call)
                yamlConstructors.computeIfPresent(Tag.MAP, (u, base) -> new ConstructCustomObject(base));
            } catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow(t);
            }
        }

        private class ConstructCustomObject implements org.yaml.snakeyaml.constructor.Construct {
            private final org.yaml.snakeyaml.constructor.Construct mapConstructor;

            public ConstructCustomObject(org.yaml.snakeyaml.constructor.Construct mapConstructor) {
                this.mapConstructor = mapConstructor;
            }

            @Override
            public Object construct(Node node) {
                if (node.isTwoStepsConstruction()) {
                    throw new YAMLException("Unexpected referential mapping structure. Node: " + node);
                }

                Map<?, ?> raw = (Map<?, ?>) this.mapConstructor.construct(node);

                String serialized_type = LogicUtil.applyIfNotNull(raw.get(ConfigurationSerialization.SERIALIZED_TYPE_KEY), Object::toString, null);
                if (serialized_type != null) {
                    Map<String, Object> typed = CommonUtil.unsafeCast(raw);

                    // If any entries in the mapping uses non-String keys, convert those to a String
                    // Almost all the time this operation is unneeded, so we do a detection step first
                    // If no conversion is needed, we use the input map casted as if it stores String keys.
                    for (Object key : raw.keySet()) {
                        if (!(key instanceof String)) {
                            typed = new LinkedHashMap<String, Object>(raw.size());
                            for (Map.Entry<?, ?> entry : raw.entrySet()) {
                                typed.put(entry.getKey().toString(), entry.getValue());
                            }
                            break;
                        }
                    }

                    try {
                        Function<Map<String, Object>, ? extends Object> builder = custom_builders.get(serialized_type);
                        if (builder != null) {
                            return builder.apply(typed);
                        } else {
                            return ConfigurationSerialization.deserializeObject(typed);
                        }
                    } catch (IllegalArgumentException ex) {
                        throw new YAMLException("Could not deserialize object", ex);
                    }
                }

                return raw;
            }

            @Override
            public void construct2ndStep(Node node, Object object) {
                mapConstructor.construct2ndStep(node, object);
            }
        }
    }
}
