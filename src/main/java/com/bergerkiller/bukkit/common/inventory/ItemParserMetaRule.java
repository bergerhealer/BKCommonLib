package com.bergerkiller.bukkit.common.inventory;

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

    public ItemParserMetaRule(String name, String operator, String value) {
        this._name = name;
        this._operator = operator;
        this._value = value;
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
            System.out.println("KEY BY " + this._name + " NOT FOUND");
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
            if (this._operator.equals("=") || this._operator.equals("==")) {
                return this._value.equals(str_value);
            } else if (this._operator.equals("!=")) {
                return !this._value.equals(str_value);
            } else {
                int compare = str_value.compareTo(this._value);
                switch (this._operator) {
                case ">=": return compare >= 0;
                case "<=": return compare <= 0;
                case ">": return compare > 0;
                case "<": return compare < 0;
                }
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
