package com.bergerkiller.bukkit.common.config.yaml;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.representer.BaseRepresenter;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.serializer.Serializer;

import com.bergerkiller.bukkit.common.io.StringBuilderWriter;
import com.bergerkiller.bukkit.common.utils.StringUtil;

/**
 * Helper class for serializing data to YAML-encoded Strings
 */
public class YamlSerializer {
    private String indentStr;
    private String[] headerPrefixes;
    private final DumperOptions dumperOptions;
    private final Resolver resolver;
    private final YamlRepresenter representer;
    private final StringBuilderWriter output;
    private Emitter emitter;
    private Serializer serializer;
    private Field emitterStateField;
    private Field emitterColumnField;
    private Field emitterIndentField;
    private Object emitterStateStartValue;
    private boolean reuseEmitter;

    public static final YamlSerializer INSTANCE = new YamlSerializer();

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
        reuseEmitter = false;
        reset(0);

        // Use Reflection to reset the state of the Emitter
        // If this stops working in the future (library changes), a slower fallback will be used
        try {
            // Instantiate an instance of ExpectFirstDocumentStart to reset the Emitter with
            Class<?>[] emitterSubclasses = Emitter.class.getDeclaredClasses();
            for (int i = 0;;i++) {
                if (i >= emitterSubclasses.length) {
                    throw new IllegalStateException("SnakeYAML Emitter class has no ExpectFirstDocumentStart subclass");
                } else if (emitterSubclasses[i].getSimpleName().equals("ExpectFirstDocumentStart")) {
                    Constructor<?> streamStartCtor = emitterSubclasses[i].getDeclaredConstructor(Emitter.class);
                    streamStartCtor.setAccessible(true);
                    emitterStateStartValue = streamStartCtor.newInstance(emitter);
                    streamStartCtor.setAccessible(false);
                    break;
                }
            }

            // Obtain the Emitter state field
            emitterStateField = Emitter.class.getDeclaredField("state");
            emitterStateField.setAccessible(true);

            // Obtain the Emitter column field
            emitterColumnField = Emitter.class.getDeclaredField("column");
            emitterColumnField.setAccessible(true);

            // Obtain the Emitter indent field
            emitterIndentField = Emitter.class.getDeclaredField("indent");
            emitterIndentField.setAccessible(true);

            // All good!
            reuseEmitter = true;
        } catch (Throwable t) {
            t.printStackTrace();
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
            t.printStackTrace();
        }
    }

    private void reset(int indent) {
        // Reset StringBuilder to empty state
        output.getBuilder().setLength(0);

        // Reset the emitter to its initial state of expecting a new document
        // If this for whatever reason fails (library changes in the future?), use a slower fallback
        if (reuseEmitter) {
            try {
                emitter.emit(new StreamEndEvent(null, null));
                emitterStateField.set(emitter, emitterStateStartValue);
                if (indent > 1) {
                    int indentSpaces = dumperOptions.getIndent() * (indent - 1);
                    emitterColumnField.setInt(emitter, indentSpaces);
                    emitterIndentField.set(emitter, Integer.valueOf(indentSpaces - dumperOptions.getIndent()));
                } else {
                    emitterColumnField.setInt(emitter, 0);
                    emitterIndentField.set(emitter, null);
                }
                return;
            } catch (Throwable t) {
                t.printStackTrace();
                reuseEmitter = false;
            }
        }

        // Slower fallback, also used during initialization
        // The emitter will not do any indentation for us
        emitter = new Emitter(output, dumperOptions);
        serializer = new Serializer(emitter, resolver, dumperOptions, null);
        try {
            serializer.open();
        } catch (IOException ex) {
            // never happens, writer writes to a StringBuilder, what can go wrong?
        }
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

            // Detect newlines in the beginning
            // Prefix those without a # left of it
            while (i < header.length() && header.charAt(i) == '\n') {
                i++;
            }

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
     * Serializes a value to a YAML-encoded String.
     * If the specified header is not empty, every line is prefixed to
     * the output starting with a #-character.
     * 
     * @param value   The value to serialize
     * @param header  The text to put in front prefixed with #-characters, empty String or null for no header
     * @param indent  The number of indentation levels to add
     * @return YAML-encoded String
     */
    public synchronized String serialize(Object value, String header, int indent) {
        // Reset the buffer and serializer
        reset(indent);

        // When our reflection hacks work, indentation is correct while writing
        // When it is not, we must perform indentation as a post-processing step
        if (this.reuseEmitter) {
            appendHeader(output.getBuilder(), header, indent);
            for (int i = 1; i < indent; i++) {
                output.getBuilder().append(indentStr);
            }
        } else {
            appendHeader(output.getBuilder(), header, (indent == 0) ? 0 : 1);
        }

        // Serialize it using SnakeYaml
        try {
            serializer.serialize(representer.represent(value));
        } catch (IOException e) {
            // never happens, writer writes to a StringBuilder, what can go wrong?
        }

        // Post-serialization operations
        if (this.reuseEmitter) {
            // Erase trailing spaces from indent
            if (indent > 1) {
                output.getBuilder().setLength(output.getBuilder().length() - 2*(indent-2));
            }
        } else if (indent > 1) {
            // Add indents after the fact
            StringBuilder builder = output.getBuilder();
            String fullIndentStr = StringUtil.getFilledString(indentStr, indent - 1);
            int indentStart = 0;
            for (int i = 0; i < builder.length(); i++) {
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

        return output.toString();
    }
}
