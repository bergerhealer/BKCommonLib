package com.bergerkiller.bukkit.common.config.yaml;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.BaseRepresenter;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.serializer.Serializer;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.io.StringBuilderWriter;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * Helper class for serializing data to YAML-encoded text
 */
public class YamlSerializer {
    private String indentStr;
    private String[] headerPrefixes;
    private final DumperOptions dumperOptions;
    private final Resolver resolver;
    private final YamlRepresenter representer;
    private final StringBuilder builder;
    private final StringBuilderWriter output;
    private final FastMethod<Void> resetMethod;
    private Emitter emitter;
    private Serializer serializer;
    private Object emitterStateStartValue;

    /**
     * A singleton instance of the YamlSerializer. Is thread-safe.
     */
    public static final YamlSerializer INSTANCE = new YamlSerializer();

    /**
     * Creates a new YamlSerializer
     */
    public YamlSerializer() {
        headerPrefixes = new String[] { "#> ", "# " };
        dumperOptions = new DumperOptions();
        dumperOptions.setIndent(2);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        indentStr = StringUtil.getFilledString(" ", dumperOptions.getIndent());
        resolver = new Resolver();
        representer = new YamlRepresenter();
        representer.setDefaultFlowStyle(dumperOptions.getDefaultFlowStyle());
        representer.setDefaultScalarStyle(dumperOptions.getDefaultScalarStyle());
        representer.getPropertyUtils().setAllowReadOnlyProperties(dumperOptions.isAllowReadOnlyProperties());
        representer.setTimeZone(dumperOptions.getTimeZone());
        output = new StringBuilderWriter();
        resetMethod = new FastMethod<Void>();
        builder = output.getBuilder();

        // Initialize serializer for the first time.
        emitter = new Emitter(output, dumperOptions);
        serializer = new Serializer(emitter, resolver, dumperOptions, null);

        // The open() method differs in implementation between SnakeYaml versions,
        // and using it causes unpredictable behavior. We do our own predictable one.
        // This makes sure the current state is set to "ExpectFirstDocumentStart"
        try {
            Field emitterEventField = Emitter.class.getDeclaredField("event");
            Field emitterStateField = Emitter.class.getDeclaredField("state");
            Method expectMethod = emitterStateField.getType().getDeclaredMethod("expect");
            Field serializerClosedField = Serializer.class.getDeclaredField("closed");
            emitterEventField.setAccessible(true);
            emitterStateField.setAccessible(true);
            expectMethod.setAccessible(true);
            serializerClosedField.setAccessible(true);

            // emitter.event = new StreamStartEvent(null, null)
            emitterEventField.set(emitter, new StreamStartEvent(null, null));

            // emitter.stat.expect();
            Object startState = emitterStateField.get(emitter);
            expectMethod.invoke(startState);

            // emitter.event = null;
            emitterEventField.set(emitter, null);

            // serializer.closed = Boolean.FALSE;
            serializerClosedField.set(serializer, Boolean.FALSE);

            // get current state, and verify it is ExpectFirstDocumentStart
            emitterStateStartValue = emitterStateField.get(emitter);
            if (emitterStateStartValue == null) {
                throw new IllegalStateException("Emitter state is null");
            } else if (!emitterStateStartValue.getClass().getSimpleName().equals("ExpectFirstDocumentStart")) {
                throw new IllegalStateException("Emitter state after initialization is not ExpectFirstDocumentStart");
            }

            // generate a method that will reset the emitter, so that no full re-initialization is needed
            ClassResolver resolver = new ClassResolver();
            resolver.addImport(Emitter.class.getName() + "State");
            resolver.addImport(StreamEndEvent.class.getName());
            resolver.setDeclaredClass(Emitter.class);
            MethodDeclaration resetMethodDec = new MethodDeclaration(resolver,
                    "public void reset(Object startState) {\n" +
                    "  #require Emitter private (Object) EmitterState state;\n" +
                    "  #require Emitter private int column;\n" +
                    "  #require Emitter private Integer indent;\n" +
                    "  instance.emit(new StreamEndEvent(null, null));\n" +
                    "  instance#state = startState;\n" +
                    "  instance#column = 0;\n" +
                    "  instance#indent = null;\n" +
                    "}");
            if (!resetMethodDec.isResolved()) {
                throw new IllegalStateException("Failed to resolve reset method: " + resetMethodDec);
            }
            resetMethod.init(resetMethodDec);
            resetMethod.forceInitialization();
        } catch (Throwable t) {
            throw new UnsupportedOperationException("This version of SnakeYAML is not supported", t);
        }

        // No-Op map. Always empty, never puts.
        Map<Object, Object> noop_map = new AbstractMap<Object, Object>() {
            @Override
            public Set<java.util.Map.Entry<Object, Object>> entrySet() {
                return Collections.emptySet();
            }

            @Override
            public boolean containsKey(Object o) {
                return false;
            }

            @Override
            public Object put(Object key, Object value) {
                return null;
            }

            @Override
            public void clear() {
            }
        };

        // No-Op set. Always empty, never adds.
        Set<Object> noop_set = new AbstractSet<Object>() {
            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public boolean add(Object o) {
                return true;
            }

            @Override
            public Iterator<Object> iterator() {
                return Collections.emptyIterator();
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public void clear() {
            }
        };

        // Use reflection to disable the maps and sets used for turning Object references into anchors
        // This way no anchor nodes are created (and wasted) while serializing values into nodes
        // If this for some reason fails to be done, it's only an added performance cost.
        try {
            Field representedObjectsField = BaseRepresenter.class.getDeclaredField("representedObjects");
            representedObjectsField.setAccessible(true);
            representedObjectsField.set(representer, noop_map);
            representedObjectsField.setAccessible(false);

            Field serializerAnchorsField = Serializer.class.getDeclaredField("anchors");
            serializerAnchorsField.setAccessible(true);
            serializerAnchorsField.set(serializer, noop_map);
            serializerAnchorsField.setAccessible(false);

            Field serializerSerializedNodesField = Serializer.class.getDeclaredField("serializedNodes");
            serializerSerializedNodesField.setAccessible(true);
            serializerSerializedNodesField.set(serializer, noop_set);
            serializerSerializedNodesField.setAccessible(false);
        } catch (Throwable t) {
            Logging.LOGGER_CONFIG.log(Level.SEVERE, "Unhandled error disabling YAML anchors", t);
        }
    }

    private String build() {
        String str = this.output.toString();
        builder.setLength(0);
        return str;
    }

    /**
     * Appends a header in standard #-encoded header format to a StringBuilder.
     * This header will use #> instead of # at indent level 0. Indent level 1
     * is on the same indentation but uses # instead.
     * 
     * @param builder
     * @param header
     * @param indent
     */
    public void appendHeader(StringBuilder builder, String header, int indent) {
        if (header != null && !header.isEmpty()) {
            int i = 0;

            // Detect newlines in the beginning
            // Prefix those without a # left of it
            while (header.charAt(i) == '\n') {
                if (++i == header.length()) {
                    // There are only newlines, nothing else
                    // This means no # are written at all
                    builder.append(header);
                    return;
                }
            }

            // Obtain the header prefix String for this indent level that we use
            // Generate more of them if the indent is higher than what we have cached
            String[] headerPrefixes = this.headerPrefixes;
            if (indent >= headerPrefixes.length) {
                String[] new_prefixes = new String[indent+1];
                System.arraycopy(headerPrefixes, 0, new_prefixes, 0, headerPrefixes.length);
                StringBuilder headerBuilder = new StringBuilder(headerPrefixes[headerPrefixes.length-1]);
                for (int h = headerPrefixes.length; h < new_prefixes.length; h++) {
                    headerBuilder.insert(0, indentStr);
                    new_prefixes[h] = headerBuilder.toString();
                }
                this.headerPrefixes = headerPrefixes = new_prefixes;
            }
            String headerPrefix = headerPrefixes[indent];

            if (i > 0) {
                builder.append(header).insert(i, headerPrefix);
            } else {
                builder.append(headerPrefix).append(header);
            }
            for (i += 2; i < builder.length(); i++) {
                if (builder.charAt(i) == '\n') {
                    builder.insert(i + 1, headerPrefix);
                    i += 2;
                }
            }
            builder.append('\n');
        }
    }

    /**
     * Serializes a value to a YAML-encoded String.
     * No header is serialized.
     * 
     * @param value to serialize
     * @return YAML-encoded String
     */
    public synchronized String serialize(Object value) {
        return serialize(value, "", 1);
    }

    /**
     * Serializes a key to a YAML-encoded String.
     * If the specified header is not empty, every line is prefixed to
     * the output starting with a #-character.
     * 
     * @param key     The key to write
     * @param header  The text to put in front prefixed with #-characters, empty String or null for no header
     * @param indent  The number of indentation levels to add
     * @return YAML-encoded String
     */
    public synchronized String serializeKey(String key, String header, int indent) {
        // Append the header, if one exists
        appendHeader(header, indent);

        if (key.length() == 1 && key.charAt(0) == '*') {
            // If key is '*' write without quotes around it
            appendIndent(indent);
            builder.append("*:\n");
        } else {
            // Append the key: by writing key: 0
            appendKeyValue(key, 0, indent, true);

            // Replace the ' 0\n' portion with a newline
            builder.setLength(builder.length() - 3);
            builder.append('\n');
        }

        return build();
    }

    /**
     * Serializes a value to a YAML-encoded String.
     * If the specified header is not empty, every line is prefixed to
     * the output starting with a #-character.
     * 
     * @param value   The value to serialize
     * @param header  The text to put in front prefixed with #-characters, empty String or null for no header
     * @param indent  The number of indentation levels to add
     * @return YAML-encoded String
     */
    @SuppressWarnings("unchecked")
    public synchronized String serialize(Object value, String header, int indent) {
        // Append the header, if one exists
        appendHeader(header, indent);

        // Optimization for simple key: value pairs
        // SnakeYAML is awfully slow with these
        if (value instanceof Map) {
            Map<String, Object> m = (Map<String, Object>) value;
            if (m.size() == 1) {
                Map.Entry<String, Object> entry = m.entrySet().iterator().next();
                appendKeyValue(entry.getKey(), entry.getValue(), indent, true);
                return build();
            }
        }

        // Fallback
        appendValue(value, indent, true);
        return build();
    }

    private void appendHeader(String header, int indent) {
        appendHeader(this.builder, header, indent);
    }

    private void appendKeyValue(String key, Object value, int indent, boolean indentFirstLine) {
        // If key is not a String literal, really strange formatting rules may happen
        // For example, a multiline string will write ? |-\n  text\n  text\n: for the key
        // Just don't bother and let Snakeyaml deal with those rare cases
        if (!canWriteStringLiteral(key, true)) {
            appendValue(Collections.singletonMap(key, value), indent, indentFirstLine);
            return;
        }

        // Append first line indent now
        if (indentFirstLine) {
            appendIndent(indent);
        }

        // Append the value of the key where some have been optimized
        if (value == null) {
            builder.append(key);
            builder.append(": ~\n");
            return;
        } else if (value instanceof Number) {
            builder.append(key);
            builder.append(": ");

            // Avoid toString() for common value types (int, double)
            Number valueNum = (Number) value;
            if (value instanceof Integer) {
                builder.append(valueNum.intValue());
            } else if (value instanceof Double) {
                builder.append(valueNum.doubleValue());
            } else {
                builder.append(value.toString());
            }

            builder.append('\n');
            return;
        } else if (value instanceof String) {
            String valueStr = value.toString();
            if (canWriteStringLiteral(valueStr, false)) {
                builder.append(key);
                builder.append(": ");
                builder.append(valueStr);
                builder.append('\n');
                return;
            }
        } else if (value instanceof Boolean) {
            builder.append(key);
            if (((Boolean) value).booleanValue()) {
                builder.append(": true\n");
            } else {
                builder.append(": false\n");
            }
            return;
        }

        // Other value types may consist of a block of key: value pairs or weird indent rules
        // In that case, a newline is put right after the key
        // But it may also be a simple value that is stored on the same line
        // We cannot know this, so let Snakeyaml solve that
        appendValue(Collections.singletonMap(key, value), indent, false);
    }

    private void appendValue(Object value, int indent, boolean indentFirstLine) {
        appendNode(this.representer.represent(value), indent, indentFirstLine);
    }

    private void appendIndent(int indent) {
        for (int i = 1; i < indent; i++) {
            builder.append(indentStr);
        }
    }

    private void appendNode(Node node, int indent, boolean indentFirstLine) {
        // Reset the serializer, make sure quench anything written out to the builder
        {
            int oldTrailingLength = builder.length();
            resetMethod.invoke(emitter, emitterStateStartValue);
            builder.setLength(oldTrailingLength);
        }

        // Serialize it using SnakeYaml
        int valueInitialOffset = builder.length();
        try {
            if (indentFirstLine) {
                for (int i = 1; i < indent; i++) {
                    builder.append(indentStr);
                }
            }
            serializer.serialize(node);
        } catch (IOException e) {
            // never happens, writer writes to a StringBuilder, what can go wrong?
        }

        // Ensure a single trailing newline
        // This behavior differs per version of SnakeYaml
        int newLength = builder.length();
        if (newLength == 0 || builder.charAt(newLength - 1) != '\n') {
            builder.append('\n');
        }

        // Replace chat color codes (ยงc) with ampersand codes (&c) in the value YAML
        // Also check whether an ampersand is going to accidentally turn into one
        // In that case, we must escape it (&&)
        for (int i = valueInitialOffset; i < builder.length()-1; i++) {
            char c = builder.charAt(i);
            if (c == StringUtil.CHAT_STYLE_CHAR && StringUtil.isChatCode(builder.charAt(i + 1))) {
                builder.setCharAt(i, '&');
                i++;
            } else if (c == '&' && StringUtil.isChatCode(builder.charAt(i + 1))) {
                builder.insert(i, '&');
                i++;
            }
        }

        // Further post-serialization operations
        if (indent > 1) {
            // Add indents after the fact for all but the first line
            String fullIndentStr = StringUtil.getFilledString(indentStr, indent - 1);
            int indentStart = Integer.MAX_VALUE; // skip first line
            for (int i = valueInitialOffset; i < builder.length(); i++) {
                char c = builder.charAt(i);
                if (c == '\n') {
                    if (i > indentStart) {
                        builder.insert(indentStart, fullIndentStr);
                        i += fullIndentStr.length();
                    }
                    indentStart = i+1;
                }
            }
        }
    }

    // Checks whether a String can be written as a String literal, without ' or "
    // This is a fast check for the most common case of only a-zA-Z key names
    private boolean canWriteStringLiteral(String str, boolean isKey) {
        int len = str.length();
        if (len == 0) {
            return false; // ''
        }

        int i = 0;
        char c = str.charAt(i);
        if (len == 3 && (c == 'i' || c == 'I')) {
            // Check for 'inf'
            c = str.charAt(++i);
            if (c == 'n' || c == 'N') {
                c = str.charAt(++i);
                if (c == 'f' || c == 'F') {
                    return false; // 'inf'
                }
            }
        } else if (len == 3 && (c == 'n' || c == 'N')) {
            // Check for 'nan'
            c = str.charAt(++i);
            if (c == 'a' || c == 'A') {
                c = str.charAt(++i);
                if (c == 'n' || c == 'N') {
                    return false; // 'nan'
                }
            }
        } else if (len == 4 && (c == 'n' || c == 'N')) {
            // Check for 'null'
            c = str.charAt(++i);
            if (c == 'u' || c == 'U') {
                c = str.charAt(++i);
                if (c == 'l' || c == 'L') {
                    c = str.charAt(++i);
                    if (c == 'l' || c == 'L') {
                        return false; // 'null'
                    }
                }
            }
        }

        // Only alphanumeric characters permitted
        boolean hasDigits = false;
        boolean hasSpecial = false;
        boolean hasLetters = false;
        for (; i != len; c = str.charAt(i++)) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                hasLetters = true;
                continue;
            }
            if (c == '_' || c == '-') {
                hasSpecial = true;
                continue;
            }
            if (isKey && c >= '0' && c <= '9') {
                hasDigits = true;
                continue;
            }
            return false;
        }

        // Prevent 1_000_000 being written as-is, as YAML decodes it as an int number
        // This caused problems when someone had a '1_18_2' name. It must be escaped
        if (hasSpecial && !hasLetters && hasDigits) {
            return false;
        }

        // Check starts or ends with a space
        if (Character.isWhitespace(str.charAt(0)) || Character.isWhitespace(str.charAt(len-1))) {
            return false;
        }

        return true;
    }
}
