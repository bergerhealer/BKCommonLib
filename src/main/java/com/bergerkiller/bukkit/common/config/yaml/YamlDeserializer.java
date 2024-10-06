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

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
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
    private final MappingConstructorFactory mappingFactory;

    /**
     * A singleton instance of the YamlDeserializer. Is thread-safe.
     */
    public static final YamlDeserializer INSTANCE = new YamlDeserializer();

    /**
     * Creates a new YamlDeserializer
     */
    public YamlDeserializer() {
        // This is used to de-serialize custom types
        mappingFactory = new MappingConstructorFactory();
        YamlConstructorFactory ctorFactory = new YamlConstructorFactory(mappingFactory);

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
     * Deserializes the Map into a known registered ConfigurationSerializable object. If possible.
     *
     * @param mapping Mapping
     * @return Deserialized object, or the input Mapping
     */
    public Object deserializeMapping(Map<?, ?> mapping) {
        Map<Object, Object> mappingUnsafe = LogicUtil.unsafeCast(mapping);

        // Recursively deserialize all entries that are mapping themselves, first
        boolean operatingOnCopy = false;
        for (Map.Entry<Object, Object> entry : mappingUnsafe.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                Object newValue = deserializeMapping((Map<?, ?>) value);
                if (value != newValue) {
                    if (!operatingOnCopy) {
                        if (!(mapping instanceof com.google.common.collect.ImmutableMap)) {
                            // try to use entry.set
                            try {
                                entry.setValue(newValue);
                                continue; // Skip alt
                            } catch (UnsupportedOperationException ex) { /* Immutable */ }
                        }

                        mappingUnsafe = LogicUtil.unsafeCast((mapping = new LinkedHashMap<>(mapping)));
                        operatingOnCopy = true;
                    }

                    mappingUnsafe.put(entry.getKey(), newValue);
                }
            }
        }

        return mappingFactory.construct(mapping);
    }

    private static Map<String, Object> safePutInMap(Map<String, Object> map, String key, Object value) {
        if (!(map instanceof com.google.common.collect.ImmutableMap)) {
            try {
                map.put(key, value);
                return map;
            } catch (UnsupportedOperationException ex) { /* Immutable */ }
        }

        map = new LinkedHashMap<>(map);
        map.put(key, value);
        return map;
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

    // Decodes a Map type into custom (registered) types
    private static class MappingConstructorFactory {
        private final Map<String, Function<Map<String, Object>, ? extends Object>> custom_builders;

        public MappingConstructorFactory() {
            this.custom_builders = new HashMap<>();

            ItemStackDeserializer itemStackDeserializer = ItemStackDeserializer.INSTANCE;
            this.register("org.bukkit.inventory.ItemStack", itemStackDeserializer);
            this.register("org.bukkit.inventory.ItemMeta", itemStackDeserializer.getItemMetaDeserializer());
            this.register("ItemMeta", itemStackDeserializer.getItemMetaDeserializer());

            // Fix deserializing profiles with invalid name causing errors to be logged
            if (CommonBootstrap.evaluateMCVersion(">=", "1.18.1")) {
                try {
                    Class<?> craftPlayerProfileType = CommonUtil.getClass("org.bukkit.craftbukkit.profile.CraftPlayerProfile");
                    final FastMethod<Object> deserializeMethod = new FastMethod<>(
                            craftPlayerProfileType.getMethod("deserialize", Map.class));
                    deserializeMethod.forceInitialization();

                    Function<Map<String, Object>, Object> deserializePlayerProfile = map -> {
                        // Fix name
                        Object name = map.get("name");
                        if (name instanceof String) {
                            String newName = fixProfileName((String) name);
                            if (newName != name) {
                                map = safePutInMap(map, "name", newName);
                            }
                        }

                        return deserializeMethod.invoke(null, map);
                    };

                    this.register("org.bukkit.profile.PlayerProfile", deserializePlayerProfile);
                    this.register("PlayerProfile", deserializePlayerProfile);
                } catch (Throwable t) {
                    Logging.LOGGER_CONFIG.log(Level.SEVERE, "Failed to register player profile deserializer", t);
                }
            }
        }

        private void register(String typeName, Function<Map<String, Object>, ? extends Object> builder) {
            custom_builders.put(typeName, builder);
        }

        private static String fixProfileName(String name) {
            // See constructor of CraftPlayerProfile on Paper:
            //   Preconditions.checkArgument((uniqueId != null) || !StringUtils.isBlank(name), "uniqueId is null or name is blank");
            //   Preconditions.checkArgument(name == null || name.length() <= 16, "The name of the profile is longer than 16 characters"); // Paper - Validate
            //   Preconditions.checkArgument(name == null || net.minecraft.util.StringUtil.isValidPlayerName(name), "The name of the profile contains invalid characters: %s", name); // Paper - Validate
            //
            // With isValidPlayerName:
            //   public static boolean isValidPlayerName(String name) {
            //       return name.length() <= 16 && name.chars().filter(c -> c <= 32 || c >= 127).findAny().isEmpty();
            //   }

            int len = name.length();
            StringBuilder result = null;
            for (int i = 0; i < len; i++) {
                char c = name.charAt(i);
                if (c <= 32 || c >= 127) {
                    if (result == null) {
                        result = new StringBuilder(len);
                        result.append(name, 0, i);
                    }
                    result.append('_');
                } else if (result != null) {
                    result.append(c);
                }
            }
            if (result == null) {
                if (len > 16) {
                    result = new StringBuilder(name);
                } else {
                    return name; // All good! Return the original name, as it is valid.
                }
            }

            // If too long, strip characters off from the end
            len = result.length();
            if (len > 16) {
                result.delete(16, len);
            }

            return result.toString();
        }

        /**
         * Constructs a ConfigurationSerializable Object from a specified mapping
         *
         * @param mapping Mapping
         * @return Serialized object, or the input Mapping if not known
         */
        public Object construct(Map<?, ?> mapping) {
            String serialized_type = LogicUtil.applyIfNotNull(mapping.get(ConfigurationSerialization.SERIALIZED_TYPE_KEY), Object::toString, null);
            if (serialized_type != null) {
                Map<String, Object> typed = CommonUtil.unsafeCast(mapping);

                // If any entries in the mapping uses non-String keys, convert those to a String
                // Almost all the time this operation is unneeded, so we do a detection step first
                // If no conversion is needed, we use the input map casted as if it stores String keys.
                for (Object key : mapping.keySet()) {
                    if (!(key instanceof String)) {
                        typed = new LinkedHashMap<String, Object>(mapping.size());
                        for (Map.Entry<?, ?> entry : mapping.entrySet()) {
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

            return mapping;
        }
    }

    // Custom version of org.bukkit.configuration.file.YamlConstructor
    // This allows us to add our own handlers for certain object types
    private static class YamlConstructorFactory {
        private final MappingConstructorFactory mappingFactory;

        public YamlConstructorFactory(MappingConstructorFactory mappingFactory) {
            this.mappingFactory = mappingFactory;
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

                return mappingFactory.construct((Map<?, ?>) this.mapConstructor.construct(node));
            }

            @Override
            public void construct2ndStep(Node node, Object object) {
                mapConstructor.construct2ndStep(node, object);
            }
        }
    }
}
