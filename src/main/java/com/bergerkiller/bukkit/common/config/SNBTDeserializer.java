package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * De-serializes SNBT strings into a Map/List/Primitive type structure that
 * can then be programmatically processed.
 * <a href="https://minecraft.wiki/w/NBT_format#SNBT_format">SNBT Format overview</a><br>
 * <br>
 * For example:
 * <pre>{colors:[1,2,3],flags:[1b,0b,1b],floats:[1.0f,2.0f,4.0f],strings:["a","b","c"]}</pre>
 */
public class SNBTDeserializer<TO> {
    private final Factory<TO, ?, ?> factory;
    private final String contents;
    private final int length;
    private int position = 0;

    public SNBTDeserializer(String contents, Factory<TO, ?, ?> factory) {
        this.contents = contents;
        this.length = contents.length();
        this.factory = factory;
    }

    /**
     * Parses the SNBT-formatted String into the value it represents.
     * Returned value can be a number, string, boolean, array, list, map or null.
     *
     * @param snbtContent Content to parse
     * @return Parsed content
     */
    public static Object parse(String snbtContent) {
        return new SNBTDeserializer<>(snbtContent, Factory.JAVA).next();
    }

    /**
     * Parses the SNBT-formatted String into the value it represents.
     * Returned value can be a number, string, boolean, array, list, map or null.
     *
     * @param snbtContent Content to parse
     * @param factory To deserialize the data into (pass NBT for CommonTag)
     * @return Parsed content
     */
    public static <T> T parse(String snbtContent, Factory<T, ?, ?> factory) {
        return new SNBTDeserializer<>(snbtContent, factory).next();
    }

    public TO next() {
        consumeWhitespace();
        return nextValue(this.factory);
    }

    private <T, L extends T, M extends T> T nextValue(Factory<T, L, M> factory) {
        final int length = this.length;
        if (tryConsume('{')) {
            // Decode a map of key=value pairs
            M result = factory.createMap();
            while (true) {
                consumeWhitespace();

                // Entry delimiter. Ignore a weird [,a:1,b:2,c:3,] style list
                if (tryConsume(',')) {
                    consumeWhitespace();
                    continue;
                }

                if (position >= length || tryConsume('}')) {
                    break;
                }

                // Consume the key String
                String key = consumeCompoundKey();

                // Consume the : delimiter
                // Value-less key entries? Oh well, handle it I guess.
                consumeWhitespace();
                if (!tryConsume(':')) {
                    factory.addToMap(result, key, factory.wrap(null));
                    continue;
                }

                // Consume the value
                consumeWhitespace();
                T value = nextValue(factory);
                factory.addToMap(result, key, value);
            }
            return result;
        } else if (tryConsume('[')) {
            // Array?
            consumeWhitespace();
            NBTArrayFormat format = tryConsumeArrayFormat();
            if (format != null) {
                return factory.wrap(format.build(consumeList(Factory.JAVA)));
            }

            // Normal list
            return consumeList(factory);
        } else if (tryConsume('\"')) {
            return factory.wrap(consumeString('\"'));
        } else if (tryConsume('\'')) {
            return factory.wrap(consumeString('\''));
        } else if (tryConsume("true")) {
            return factory.wrap((byte) 1);
        } else if (tryConsume("false")) {
            return factory.wrap((byte) 0);
        } else {
            return factory.wrap(consumeNumber());
        }
    }

    private <T, L extends T, M extends T> L consumeList(Factory<T, L, M> factory) {
        L list = factory.createList();
        while (position < length && !tryConsume(']')) {
            // List delimiter. Ignore a weird [,a,b,c,] style list
            if (tryConsume(',')) {
                consumeWhitespace();
                continue;
            }

            factory.addToList(list, nextValue(factory));
            consumeWhitespace();
        }
        return list;
    }

    private Object consumeNumber() {
        // Primitive types (numbers)
        // Look for an end-delimiter for the surrounding structure
        // This can be: , ] }
        // When we encounter an exponent or a dot, switch to double format by default
        NBTNumberFormat format = NBTNumberFormat.INTEGER;
        int numberStart = this.position;
        int numberEnd = numberStart;
        while (true) {
            if (numberEnd >= length) {
                this.position = length;
                break;
            }

            char c = contents.charAt(numberEnd);
            if (c == ',' || c == ']' || c == '}' || c == ' ') {
                this.position = numberEnd;
                break;
            } else if (c == '.' || c == 'E') {
                // If it has a dot or exponent, assume double format
                format = NBTNumberFormat.DOUBLE;
                numberEnd++;
            } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                // End-format delimiter. Find it and resume
                NBTNumberFormat matchedFormat = NBTNumberFormat.find(c);
                if (matchedFormat != null) {
                    format = matchedFormat;
                    this.position = numberEnd + 1;
                } else {
                    this.position = numberEnd;
                }
                break;
            } else {
                numberEnd++;
            }
        }
        if (numberStart == numberEnd) {
            return null; // Weird?
        }

        // Parse the number body
        return format.parse(contents.substring(numberStart, numberEnd));
    }

    private String consumeCompoundKey() {
        if (tryConsume('\"')) {
            return consumeString('\"');
        } else if (tryConsume('\'')) {
            return consumeString('\'');
        } else {
            // Only these characters are allowed (no spaces):
            // a-zA-Z0-9_-.+
            String contents = this.contents;
            int position = this.position;
            int length = this.length;
            while (position < length) {
                char c = contents.charAt(position);
                if ((c >= 'a' && c <= 'z')
                        || (c >= 'A' && c <= 'Z')
                        || (c >= '0' && c <= '9')
                        || c == '_'
                        || c == '-'
                        || c == '.'
                        || c == '+'
                ) {
                    position++;
                } else {
                    break;
                }
            }

            // We must consume something, unless the next character is a :
            // When this happens, consume everything up until the first encountered :
            if (this.position == position) {
                while (position < length && contents.charAt(position) != ':') {
                    position++;
                }
            }

            String key = contents.substring(this.position, position);
            this.position = position;
            return key;
        }
    }

    private String consumeString(char delimiterQuoteChar) {
        // Decode strings. Try optimized (no escaping) first. Must not contain \ characters.
        {
            int stringEnd = contents.indexOf(delimiterQuoteChar, position);
            if (stringEnd != -1 && (stringEnd == position || contents.charAt(stringEnd - 1) != '\\')) {
                String result = contents.substring(position, stringEnd);
                if (result.indexOf('\\') == -1) {
                    position = stringEnd + 1;
                    return result;
                }
            }
        }

        // Decode strings. Handle un-escaping. An unescaped double-quote marks the end of the String.
        StringBuilder str = new StringBuilder();
        boolean escaped = false;
        int position = this.position;
        while (position < length) {
            char c = contents.charAt(position++);
            if (escaped) {
                escaped = false;
                str.append(c);
            } else if (c == '\\') {
                escaped = true;
            } else if (c != delimiterQuoteChar) {
                str.append(c);
            } else {
                break; // String end
            }
        }
        this.position = position;
        return str.toString();
    }

    private NBTArrayFormat tryConsumeArrayFormat() {
        int position = this.position;
        int length = this.length;
        String contents = this.contents;
        if ((length - position) >= 2 && contents.charAt(position + 1) == ';') {
            NBTArrayFormat format = NBTArrayFormat.parse(contents.charAt(position));
            if (format != null) {
                this.position = position + 2;
                return format;
            }
        }
        return null;
    }

    private void consumeWhitespace() {
        int position = this.position;
        int length = this.length;
        while (position < length && contents.charAt(position) == ' ') {
            position++;
        }
        this.position = position;
    }

    private boolean tryConsume(char c) {
        int position = this.position;
        if (position < length && contents.charAt(position) == c) {
            this.position = position + 1;
            return true;
        } else {
            return false;
        }
    }

    private boolean tryConsume(String s) {
        int position = this.position;
        int s_len = s.length();
        if (s_len > (length - position)) {
            return false;
        }
        for (int i = 0; i < s_len; i++) {
            if (contents.charAt(position + i) != s.charAt(i)) {
                return false;
            }
        }
        this.position = position + s_len;
        return true;
    }

    private enum NBTNumberFormat {
        FLOAT('f', Float::parseFloat),
        DOUBLE('d', Double::parseDouble),
        BYTE('b', Byte::parseByte),
        SHORT('s', Short::parseShort),
        LONG('l', Long::parseLong),
        INTEGER('i', Integer::parseInt);

        private final char c1, c2;
        private final Function<String, Object> parser;

        private static final NBTNumberFormat[] FORMATS = NBTNumberFormat.values();

        NBTNumberFormat(char c, Function<String, Object> parser) {
            this.c1 = c;
            this.c2 = Character.toUpperCase(c);
            this.parser = parser;
        }

        public static NBTNumberFormat find(char c) {
            for (NBTNumberFormat format : FORMATS) {
                if (format.c1 == c || format.c2 == c) {
                    return format;
                }
            }
            return null;
        }

        public Object parse(String input) {
            try {
                return parser.apply(input);
            } catch (NumberFormatException ex) {
                return input; // Treat as string I guess?
            }
        }
    }

    /**
     * Type of NBT Array with identifier
     */
    private enum NBTArrayFormat {
        BYTE_ARRAY('B', byte[]::new, (array, index, value) -> {
            if (value instanceof Number) {
                array[index] = ((Number) value).byteValue();
            }
        }),
        INT_ARRAY('I', int[]::new, (array, index, value) -> {
            if (value instanceof Number) {
                array[index] = ((Number) value).intValue();
            }
        }),
        LONG_ARRAY('L', long[]::new, (array, index, value) -> {
            if (value instanceof Number) {
                array[index] = ((Number) value).longValue();
            }
        });

        private static final NBTArrayFormat[] FORMATS = NBTArrayFormat.values();

        private final char c;
        private final Function<List<?>, Object> arrayBuilder;

        <T> NBTArrayFormat(char c, IntFunction<T> create, ArraySetter<T> setter) {
            this.c = c;
            this.arrayBuilder = list -> {
                int length = list.size();
                T array = create.apply(length);
                for (int i = 0; i < length; i++) {
                    setter.set(array, i, list.get(i));
                }
                return array;
            };
        }

        public static NBTArrayFormat parse(char identifier) {
            for (NBTArrayFormat f : FORMATS) {
                if (f.c == identifier) {
                    return f;
                }
            }
            return null;
        }

        public Object build(List<?> values) {
            return arrayBuilder.apply(values);
        }

        private interface ArraySetter<T> {
            void set(T array, int index, Object value);
        }
    }

    /**
     * Factory that produces the deserialized result. Is here so that data can be
     * deserialized into Map or with the CommonTag NBT API.
     *
     * @param <T> Base tag type
     * @param <L> List container type
     * @param <M> Map container type
     */
    public interface Factory<T, L extends T, M extends T> {
        /** Decodes into Java Object, Map and List */
        Factory<Object, List<Object>, Map<String, Object>> JAVA = new Factory<Object, List<Object>, Map<String, Object>>() {
            @Override
            public Object wrap(Object data) {
                return data;
            }

            @Override
            public List<Object> createList() {
                return new ArrayList<>();
            }

            @Override
            public Map<String, Object> createMap() {
                return new LinkedHashMap<>();
            }

            @Override
            public void addToList(List<Object> list, Object item) {
                list.add(item);
            }

            @Override
            public void addToMap(Map<String, Object> map, String key, Object value) {
                map.put(key, value);
            }
        };
        /** Decodes into NBT CommonTag, CommonTagList and CommonTagCompound */
        Factory<CommonTag, CommonTagList, CommonTagCompound> NBT = new Factory<CommonTag, CommonTagList, CommonTagCompound>() {
            @Override
            public CommonTag wrap(Object data) {
                if (data == null) {
                    return CommonTag.createForData("");
                }
                return CommonTag.createForData(data);
            }

            @Override
            public CommonTagList createList() {
                return new CommonTagList();
            }

            @Override
            public CommonTagCompound createMap() {
                return new CommonTagCompound();
            }

            @Override
            public void addToList(CommonTagList list, CommonTag item) {
                list.add(item);
            }

            @Override
            public void addToMap(CommonTagCompound map, String key, CommonTag value) {
                map.put(key, value);
            }
        };

        T wrap(Object data);
        L createList();
        M createMap();
        void addToList(L list, T item);
        void addToMap(M map, String key, T value);
    }
}
