package com.bergerkiller.bukkit.common.cloud.parsers;

/**
 * Filters what characters are permitted unquoted in a quoted string argument.
 * Based on Mojang brigadier's rules.
 */
public interface UnquotedCharacterFilter {
    /** Permissive filter: only spaces and " characters are disallowed */
    UnquotedCharacterFilter PERMISSIVE = c -> c != ' ' && c != '"';
    /** Strict filter: only alpha-numeric characters are allowed */
    UnquotedCharacterFilter STRICT = c -> c >= '0' && c <= '9'
            || c >= 'A' && c <= 'Z'
            || c >= 'a' && c <= 'z'
            || c == '_' || c == '-'
            || c == '.' || c == '+';

    /**
     * Gets whether a character is allowed in an unquoted-quoted string argument
     *
     * @param c Character
     * @return True if allowed
     */
    boolean isAllowed(char c);

    /**
     * Gets whether all characters in a String are allowed in an unquoted-quoted string argument
     *
     * @param str String
     * @return True if allowed
     */
    default boolean isStringAllowed(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (!isAllowed(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Quote-escapes a String if any of the characters inside are disallowed
     *
     * @param str String
     * @return Input string if allowed, otherwise a quote-escaped string
     */
    default String escapeStringIfNeeded(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++ ) {
            char c = str.charAt(i);
            if (!isAllowed(c)) {
                StringBuilder newStr = new StringBuilder(len + 10);
                newStr.append('"');
                newStr.append(str, 0, i);
                QuotedArgumentParserProxy.appendEscapedCharToBuilder(newStr, c);
                QuotedArgumentParserProxy.appendEscapedStringToBuilder(newStr, str, i + 1, len);
                newStr.append('"');
                return newStr.toString();
            }
        }
        return str;
    }

    /**
     * Quote-escapes a String
     *
     * @param str String
     * @return String quote-escaped, surrounded by quotes
     */
    static String escapeString(String str) {
        StringBuilder newStr = new StringBuilder(str.length() + 10);
        newStr.append('"');
        QuotedArgumentParserProxy.appendEscapedStringToBuilder(newStr, str, 0, str.length());
        newStr.append('"');
        return newStr.toString();
    }

    /**
     * Un-escapes a previously escaped String
     *
     * @param str Input String
     * @return Unescaped string if the string starts with a quote, otherwise the input string
     */
    static String unescapeString(String str) {
        // First character must be a " or its not escaped at all. Probably an error.
        int len = str.length();
        if (len == 0 || str.charAt(0) != '"') {
            return str;
        }

        StringBuilder newStr = new StringBuilder(len - 1);
        boolean escaped = false;
        for (int i = 1; i < len; i++) {
            char c = str.charAt(i);
            if (escaped) {
                escaped = false;
                newStr.append(c);
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                break;
            } else {
                newStr.append(c);
            }
        }
        return newStr.toString();
    }
}
