package com.bergerkiller.bukkit.common.inventory;

import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.StringMapCaseInsensitive;

/**
 * A single metadata rule for the ItemParser.
 * Represents, for example, that a particular key should equal a certain value.
 */
public final class ItemParserMetaRule {
    private static final Map<String, Function<CommonItemStack, Object>> valueExtractors = new StringMapCaseInsensitive<>();
    static {
        valueExtractors.put("damage", item -> item.isDamageSupported() ? item.getDamage() : null);
    }

    private final Function<CommonItemStack, Object> _valueExtractor;
    private String _name;
    private Operator _operator;
    private String _value;
    private boolean _valuePatternCheck;
    private Pattern _valuePattern;

    private ItemParserMetaRule(Function<CommonItemStack, Object> valueExtractor, String name, Operator operator, String value) {
        this._valueExtractor = valueExtractor;
        this._name = name;
        this._operator = operator;
        this._value = value;
        this._valuePatternCheck = false; // not yet checked
        this._valuePattern = null; // lazy
    }

    public String getName() {
        return this._name;
    }

    public Operator getOperator() {
        return this._operator;
    }

    public String getValue() {
        return this._value;
    }

    /**
     * Checks whether this metadata rule matches an item
     * 
     * @param item
     * @return True if matches
     */
    public final boolean match(CommonItemStack item) {
        Object value = this._valueExtractor.apply(item);
        if (value == null) {
            // 'not exists' match
            return "!=".equals(this._operator);
        } else if (this._value == null) {
            // 'exists' check
            return (this._operator == null) ||
                    this._operator.equals("==") ||
                    this._operator.equals("=");
        }

        // Deals with float and double cases (decimal)
        // Note: parse as value type to avoid precision mismatch errors
        if (value instanceof Double || value instanceof Float) {
            try {
                double num_value, num_expect;
                num_value = ((Number) value).doubleValue();
                if (value instanceof Double) {
                    num_expect = Double.parseDouble(this._value);
                } else {
                    num_expect = (double) Float.parseFloat(this._value);
                }
                return this._operator.compare(num_value, num_expect);
            } catch (NumberFormatException ex) {
                return false;
            }
        }

        // Deals with byte, short, int, long cases (integer)
        if (value instanceof Number) {
            // Deals with byte, short, int, long cases (integer)
            try {
                long num_value = ((Number) value).longValue();
                long num_expect = Long.parseLong(this._value);
                return this._operator.compare(num_value, num_expect);
            } catch (NumberFormatException ex) {
                return false;
            }
        }

        // String
        if (value instanceof String) {
            String str_value = (String) value;

            boolean isNotEquals = this._operator == Operator.NOT_EQUAL;
            if (isNotEquals || this._operator == Operator.EQUAL || this._operator == Operator.EQUAL_ALT) {
                // Check if this._value contains wildcard character
                // If so, we must use regex to do all this
                if (!this._valuePatternCheck) {
                    this._valuePatternCheck = true;
                    this._valuePattern = null;
                    int wildcardIndex = this._value.indexOf('*');
                    if (wildcardIndex != -1) {
                        // Turn this._value into a regex pattern (and cache it)
                        StringBuilder regexStr = new StringBuilder();
                        int startIndex = 0;
                        while (true) {
                            // Add all text prior as a quoted string
                            if (wildcardIndex > startIndex) {
                                regexStr.append(Pattern.quote(this._value.substring(startIndex, wildcardIndex)));
                            }

                            // Add wildcard, or if double-*, as an escaped *
                            if (wildcardIndex < (this._value.length()-1) && this._value.charAt(wildcardIndex+1) == '*') {
                                regexStr.append("\\*\\*");
                                wildcardIndex += 2;
                            } else {
                                regexStr.append(".*");
                                wildcardIndex++;
                            }

                            // Look from past startIndex
                            startIndex = wildcardIndex;

                            // Find next wildcard
                            wildcardIndex = this._value.indexOf('*', startIndex);
                            if (wildcardIndex == -1) {
                                // Stop. Add what remains as quoted string
                                regexStr.append(Pattern.quote(this._value.substring(startIndex)));
                                break;
                            }
                        }

                        // This should never fail to compile, but just in case, try-catch it.
                        try {
                            this._valuePattern = Pattern.compile(regexStr.toString());
                        } catch (PatternSyntaxException ex) {
                            Logging.LOGGER.log(Level.WARNING, "Failed to compile item parser meta regex pattern", ex);
                        }
                    }
                }

                // Handling of regex patterns (or not)
                boolean isValueEqual;
                if (this._valuePattern != null) {
                    isValueEqual = this._valuePattern.matcher(str_value).matches();
                } else {
                    isValueEqual = this._value.equals(str_value);
                }

                return isValueEqual != isNotEquals;
            }

            // Other cases
            return this._operator.compare(str_value, this._value);
        }

        // List, Map, byte[], int[]
        // TODO: Don't even know of a syntax for these :/
        return false;
    }

    @Override
    public String toString() {
        if (this._value == null) {
            return this._name;
        } else {
            return this._name + this._operator + this._value;
        }
    }

    /**
     * Parses a meta rule from a String
     * 
     * @param text
     * @return meta rule
     */
    public static ItemParserMetaRule parse(String text) {
        // ! prefixes means opposite
        boolean opposite = false;
        while (text.startsWith("!")) {
            opposite = !opposite;
            text = text.substring(1);
        }

        // Extract the operator and operator value from the expression
        Operator operator = Operator.EQUAL;
        String value = null; // null means 'any' non-null value
        for (Operator operatorCandidate : Operator.values()) {
            int index = text.indexOf(operatorCandidate.getOperatorName());
            if (index != -1) {
                operator = operatorCandidate;
                value = text.substring(index + operatorCandidate.getOperatorName().length());
                text = text.substring(0, index); /* name */
                break;
            }
        }

        // Inverted?
        if (opposite) {
            operator = operator.opposite();
        }

        Function<CommonItemStack, Object> valueExtractor = null;
        synchronized (valueExtractors) {
            valueExtractor = valueExtractors.get(text);
        }
        if (valueExtractor == null) {
            final String name = text;
            valueExtractor = item -> {
                if (item.hasCustomData()) {
                    return item.getCustomData().getValue(name);
                } else {
                    return null;
                }
            };
        }
        return new ItemParserMetaRule(valueExtractor, text, operator, value);
    }

    public enum Operator {
        GREATER_EQUAL_THAN(">=", c -> c >= 0),
        LESS_EQUAL_THAN("<=", c -> c <= 0),
        NOT_EQUAL("!=", c -> c != 0),
        EQUAL("==", c -> c == 0),
        EQUAL_ALT("=", c -> c == 0),
        GREATER_THAN(">", c -> c > 0),
        LESS_THAN("<", c -> c < 0);

        static {
            GREATER_EQUAL_THAN.opposite = LESS_THAN;
            LESS_EQUAL_THAN.opposite = GREATER_THAN;
            NOT_EQUAL.opposite = EQUAL;
            EQUAL.opposite = NOT_EQUAL;
            EQUAL_ALT.opposite = NOT_EQUAL;
            GREATER_THAN.opposite = LESS_EQUAL_THAN;
            LESS_THAN.opposite = GREATER_EQUAL_THAN;
        }

        private final String name;
        private final ComparatorRule comparatorRule;
        private Operator opposite;

        Operator(String name, ComparatorRule comparatorRule) {
            this.name = name;
            this.comparatorRule = comparatorRule;
        }

        public String getOperatorName() {
            return name;
        }

        public Operator opposite() {
            return opposite;
        }

        public boolean compare(double lhs, double rhs) {
            return comparatorRule.test(Double.compare(lhs, rhs));
        }

        public boolean compare(long lhs, long rhs) {
            return comparatorRule.test(Long.compare(lhs, rhs));
        }

        public boolean compare(String lhs, String rhs) {
            return comparatorRule.test(lhs.compareTo(rhs));
        }

        @FunctionalInterface
        private interface ComparatorRule {
            boolean test(int comparator);
        }
    }
}
