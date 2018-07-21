package com.bergerkiller.bukkit.common.inventory;

import com.bergerkiller.bukkit.common.utils.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Can be used to match items against, and to provide amounts. Material AIR is
 * designated for invalid (or failed-to-parse) Item Parsers.
 */
public class ItemParser {

    public static final char STACK_MULTIPLIER = '^';
    public static final char[] MULTIPLIER_SIGNS = {'x', 'X', '*', ' ', '@', STACK_MULTIPLIER};

    /**
     * Constructs a new Item Parser for any data and infinite amount
     *
     * @param type to match, null for any type
     */
    public ItemParser(Material type) {
        this(type, -1);
    }

    /**
     * Constructs a new Item Parser for any data
     *
     * @param type to match, null for any type
     * @param amount to use, -1 for infinite
     */
    public ItemParser(Material type, int amount) {
        this(type, amount, -1);
    }

    /**
     * Constructs a new Item Parser
     *
     * @param type to match, null for any type
     * @param amount to use, -1 for infinite
     * @param data to match, -1 for any data
     */
    public ItemParser(Material type, int amount, int data) {
        this.amount = amount;
        this.data = data;
        this.type = type;
    }

    private ItemParser() {
        this.data = -1;
        this.type = null;
        this.amount = 1;
    }

    private int data;
    private Material type;
    private int amount;

    /**
     * Supported formats: typedata: [type]:[data] [typeid]:[data] [typeid]
     * <p/>
     * Amount/name relationship: [amount]x[typedata] [amount]*[typedata]
     * [amount] [typedata] [amount]@[typedata] [typedata]
     */
    public static ItemParser parse(String fullname) {
        fullname = fullname.trim();
        int index = StringUtil.firstIndexOf(fullname, MULTIPLIER_SIGNS);
        if (index == -1) {
            return parse(fullname, null);
        } else {
            ItemParser parser = parse(fullname.substring(index + 1), fullname.substring(0, index));
            if (fullname.charAt(index) == STACK_MULTIPLIER) {
                parser = parser.multiplyAmount(parser.getMaxStackSize());
            }
            return parser;
        }
    }

    public static ItemParser parse(String name, String amount) {
        int index = name.indexOf(':');

        if (index == -1) {
            return parse(name, null, amount);
        } else {
            return parse(name.substring(0, index), name.substring(index + 1), amount);
        }
    }

    public static ItemParser parse(String name, String dataname, String amount) {
        ItemParser parser = new ItemParser();
        // parse amount
        parser.amount = ParseUtil.parseInt(amount, -1);
        // parse material from name
        if (LogicUtil.nullOrEmpty(name)) {
            parser.type = null;
        } else {
            parser.type = ParseUtil.parseMaterial(name, Material.AIR);
        }
        // parse material data from name if needed
        if (parser.hasType() && !LogicUtil.nullOrEmpty(dataname)) {
            parser.data = ParseUtil.parseMaterialData(dataname, parser.type, -1);
        }

        return parser;
    }

    public boolean match(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        return this.match(stack.getType(), MaterialUtil.getRawData(stack));
    }

    public boolean match(Material type, int data) {
        if (this.hasType() && type != this.getType()) {
            return false;
        }
        if (!this.hasData() || data == this.getData()) {
            return true;
        }

        // Take in account certain items/blocks with bit fields (<= 1.12.2)
        if (this.getType() != null && this.getType().name().equals("LEAVES")) {
            return (data & 0x3) == this.getData();
        }

        return false;
    }

    /**
     * Checks whether an amount is being used
     *
     * @return True if there is an amount, False if not
     */
    public boolean hasAmount() {
        return this.amount >= 0;
    }

    /**
     * Checks whether data is being used
     *
     * @return True if there is data, False if not
     */
    public boolean hasData() {
        return this.data >= 0;
    }

    /**
     * Checks whether a type is being used
     *
     * @return True if there is a type, False if not
     */
    public boolean hasType() {
        return this.type != null;
    }

    /**
     * Gets the data to match against, -1 for any data
     *
     * @return Matched data
     */
    public int getData() {
        return this.data;
    }

    /**
     * Gets the amount, -1 for infinite amount
     *
     * @return Amount
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Gets the type to match against, null for any item
     *
     * @return Matched type
     */
    public Material getType() {
        return this.type;
    }

    public ItemStack getItemStack() {
        return this.getItemStack(this.amount);
    }

    public ItemStack getItemStack(int amount) {
        if (!this.hasType()) {
            return null;
        }
        if (amount <= 0) {
            amount = this.getMaxStackSize();
        }
        if (this.hasData()) {
            return new ItemStack(this.type, amount, (short) this.data);
        } else {
            return new ItemStack(this.type, amount);
        }
    }

    public int getMaxStackSize() {
        return ItemUtil.getMaxSize(this.getType(), 64);
    }

    /**
     * Creates a new ItemParser with the type and amount of this parser, but
     * with a new data
     *
     * @param data for the new parser (-1 for no data)
     * @return new ItemParser with the new data
     */
    public ItemParser setData(int data) {
        return new ItemParser(this.type, this.amount, data);
    }

    /**
     * Creates a new ItemParser with the type and data of this parser, but with
     * a new amount
     *
     * @param amount for the new parser, -1 for infinite
     * @return new ItemParser with the new amount
     */
    public ItemParser setAmount(int amount) {
        return new ItemParser(this.type, amount, this.data);
    }

    /**
     * Multiplies the amount of this item parser with the amount specified<br>
     * If this parser has no amount, an amount of 1 is assumed, resulting in a
     * new item parser with the specified amount
     *
     * @param amount to multiply with
     * @return new Item Parser with the multiplied amount
     */
    public ItemParser multiplyAmount(int amount) {
        if (this.hasAmount()) {
            amount *= this.getAmount();
        }
        return this.setAmount(amount);
    }

    @Override
    public String toString() {
        StringBuilder rval = new StringBuilder();
        if (this.hasAmount()) {
            rval.append(this.amount).append(" of ");
        }
        if (this.hasType()) {
            rval.append(this.type.toString().toLowerCase());
            if (this.hasData()) {
                rval.append(':').append(this.data);
            }
        } else {
            rval.append("any type");
        }
        return rval.toString();
    }
}
