package com.bergerkiller.bukkit.common.inventory;

import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;

/**
 * A single metadata rule for the ItemParser.
 * Represents, for example, that a particular key should equal a certain value.
 */
public class ItemParserMetaRule {
    private static final String[] OPERATOR_TYPES = {">=", "<=", "!=", "==", "=", ">", "<"};
    private String _name;
    private String _operator;
    private String _value;
    private boolean _valuePatternCheck;
    private Pattern _valuePattern;

    public ItemParserMetaRule(String name, String operator, String value) {
        this._name = name;
        this._operator = operator;
        this._value = value;
        this._valuePatternCheck = false; // not yet checked
        this._valuePattern = null; // lazy
    }

    public String getName() {
        return this._name;
    }

    public String getOperator() {
        return this._operator;
    }

    public String getValue() {
        return this._value;
    }

    /**
     * Checks whether this metadata rule matches the metadata of an Item
     * 
     * @param tag
     * @return True if matches
     */
    public boolean match(CommonTagCompound tag) {
        Object value = tag.getValue(this._name);
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
                switch (this._operator) {
                case "=":
                case "==": return num_value == num_expect;
                case "!=": return num_value != num_expect;
                case ">=": return num_value >= num_expect;
                case "<=": return num_value <= num_expect;
                case ">": return num_value > num_expect;
                case "<": return num_value < num_expect;
                }
            } catch (NumberFormatException ex) {}
            return false;
        }

        // Deals with byte, short, int, long cases (integer)
        if (value instanceof Number) {
            // Deals with byte, short, int, long cases (integer)
            try {
                long num_value = ((Number) value).longValue();
                long num_expect = Long.parseLong(this._value);
                switch (this._operator) {
                case "=":
                case "==": return num_value == num_expect;
                case "!=": return num_value != num_expect;
                case ">=": return num_value >= num_expect;
                case "<=": return num_value <= num_expect;
                case ">": return num_value > num_expect;
                case "<": return num_value < num_expect;
                }
            } catch (NumberFormatException ex) {}
            return false;
        }

        // String
        if (value instanceof String) {
            String str_value = (String) value;

            boolean isNotEquals = this._operator.equals("!=");
            if (isNotEquals || this._operator.equals("=") || this._operator.equals("==")) {
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
            int compare = str_value.compareTo(this._value);
            switch (this._operator) {
            case ">=": return compare >= 0;
            case "<=": return compare <= 0;
            case ">": return compare > 0;
            case "<": return compare < 0;
            }
            return false;
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
        for (String op : OPERATOR_TYPES) {
            int index = text.indexOf(op);
            if (index != -1) {
                return new ItemParserMetaRule(
                        text.substring(0, index), /* name */
                        op, /* operator */
                        text.substring(index + op.length())); /* value */
            }
        }
        if (text.startsWith("!")) {
            return new ItemParserMetaRule(text.substring(1), "!=", null);
        } else {
            return new ItemParserMetaRule(text, "==", null);
        }
    }

}
