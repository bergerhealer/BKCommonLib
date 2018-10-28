package com.bergerkiller.bukkit.common.inventory;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.*;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Can be used to match items against, and to provide amounts. Material AIR is
 * designated for invalid (or failed-to-parse) Item Parsers.
 */
public class ItemParser {

    public static final char METADATA_CHAR = '$';
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
        this.rules = Collections.emptyList();
    }

    /**
     * Constructs a new Item Parser
     *
     * @param type to match, null for any type
     * @param amount to use, -1 for infinite
     * @param data to match, -1 for any data
     * @param metaRules for matching metadata in items
     */
    public ItemParser(Material type, int amount, int data, List<ItemParserMetaRule> metaRules) {
        this.amount = amount;
        this.data = data;
        this.type = type;
        this.rules = metaRules;
    }

    private ItemParser() {
        this.data = -1;
        this.type = null;
        this.amount = 1;
        this.rules = Collections.emptyList();
    }

    private int data;
    private Material type;
    private int amount;
    private List<ItemParserMetaRule> rules;

    /**
     * Supported formats: typedata: [type]:[data] [typeid]:[data] [typeid]
     * <p/>
     * Amount/name relationship: [amount]x[typedata] [amount]*[typedata]
     * [amount] [typedata] [amount]@[typedata] [typedata]
     */
    public static ItemParser parse(String fullname) {
        fullname = fullname.trim();

        // Parse amount (multiplier) part of name. Amount should be a valid number.
        String amountStr = null;
        boolean isStackMultiplier = false;
        int index = StringUtil.firstIndexOf(fullname, MULTIPLIER_SIGNS);
        if (index != -1) {
            amountStr = fullname.substring(0, index);
            try {
                Integer.parseInt(amountStr); // Throws if invalid
                isStackMultiplier = fullname.charAt(index) == STACK_MULTIPLIER;
                fullname = fullname.substring(index + 1);
            } catch (NumberFormatException ex) {
                amountStr = null;
            }
        }

        ItemParser parser = parse(fullname, amountStr);
        if (isStackMultiplier) {
            parser = parser.multiplyAmount(parser.getMaxStackSize());
        }
        return parser;
    }

    public static ItemParser parse(String name, String amount) {
        int index = StringUtil.firstIndexOf(name, ':', METADATA_CHAR);
        if (index == -1) {
            return parse(name, null, amount);
        } else {
            int data_index = index;
            if (name.charAt(index) == ':') {
                data_index++;
            }
            return parse(name.substring(0, index), name.substring(data_index), amount);
        }
    }

    /**
     * Parses from name. If dataname is non-null, this parses legacy materials using
     * legacy data name information
     * 
     * @param name
     * @param dataname
     * @param amount
     * @return ItemParser
     */
    @Deprecated
    public static ItemParser parse(String name, String dataname, String amount) {
        ItemParser parser = new ItemParser();

        // parse amount
        parser.amount = ParseUtil.parseInt(amount, -1);

        // split dataname into data and metadata
        String dataname_data = dataname;
        String dataname_meta = null;
        if (dataname_data != null) {
            int index = dataname_data.indexOf(METADATA_CHAR);
            if (index == 0) {
                dataname_meta = dataname_data.substring(1);
                dataname_data = null;
            } else if (index > 0) {
                dataname_meta = dataname_data.substring(index + 1);
                dataname_data = dataname_data.substring(0, index);
            }
        }

        // parse material from name
        if (LogicUtil.nullOrEmpty(name)) {
            parser.type = null;
        } else if (dataname_data == null) {
            // First try non-legacy. Then try legacy.
            parser.type = ParseUtil.parseMaterial(name, Material.AIR, false);
            if (parser.type == null) {
                parser.type = ParseUtil.parseMaterial(name, Material.AIR, true);
            }
        } else {
            // Data is specified, which isn't used on 1.13 and later
            // Only legacy material names are valid here
            parser.type = ParseUtil.parseMaterial(name, Material.AIR, true);
        }

        // parse material data from name if needed
        if (parser.hasType() && !LogicUtil.nullOrEmpty(dataname_data)) {
            parser.data = ParseUtil.parseMaterialData(dataname_data, parser.type, -1);
        }

        // add metadata rules
        if (!LogicUtil.nullOrEmpty(dataname_meta)) {
            parser.rules = new ArrayList<ItemParserMetaRule>(4);
            int index = 0;
            do {
                String ruleStr;
                int end_index = dataname_meta.indexOf(METADATA_CHAR, index);
                if (end_index == -1) {
                    ruleStr = dataname_meta.substring(index);
                    index = -1;
                } else {
                    ruleStr = dataname_meta.substring(index, end_index);
                    index = end_index + 1;
                }
                parser.rules.add(ItemParserMetaRule.parse(ruleStr));
            } while (index != -1);
        }

        return parser;
    }

    /**
     * Checks whether an ItemStack matches this Item Parser
     * 
     * @param stack
     * @return True if it matches
     */
    public boolean match(ItemStack stack) {
        if (stack == null) {
            return false;
        }

        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            // No data is ever used and ItemStack type will guaranteed never be legacy
            if (!this.match(stack.getType(), 0))
                return false;
        } else {
            // 1.12.2 or before, supply durability also
            if (!this.match(stack.getType(), stack.getDurability()))
                return false;
        }

        // Metadata rules
        if (!this.rules.isEmpty()) {
            CommonTagCompound meta = ItemUtil.getMetaTag(stack, false);
            if (meta == null) {
                meta = new CommonTagCompound();
            }

            for (ItemParserMetaRule rule : this.rules) {
                if (!rule.match(meta)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks whether the BlockData of a Block matches the Item of this Item Parser.
     * 
     * @param blockData
     * @return True if it matches
     */
    public boolean match(BlockData blockData) {
        if (this.hasType() && !blockData.isType(this.getType())) {
            return false;
        }
        if (this.hasData() && blockData.getRawData() != this.getData()) {
            return false;
        }
        return true;
    }

    /**
     * Matches a Material type and matching data. This only supports legacy materials
     * and should not be used anymore, because it is <b>deprecated</b>.
     * 
     * @param type
     * @param data
     * @return True if matching
     */
    @Deprecated
    public boolean match(Material type, int data) {
        Material self = this.type;
        if (self != null) {
            // If legacy differs between input and self, we need to convert it somehow
            boolean typeIsLegacy = MaterialUtil.isLegacyType(type);
            boolean selfIsLegacy = MaterialUtil.isLegacyType(self);
            if (typeIsLegacy != selfIsLegacy) {
                // Convert whichever is not legacy, to legacy
                if (!typeIsLegacy) {
                    // Input type -> legacy. Update data parameter doing so if a Block.
                    if (type.isBlock()) {
                        BlockData block = BlockData.fromMaterial(type);
                        type = block.getLegacyType();
                        data = block.getRawData();
                    } else {
                        type = CraftMagicNumbersHandle.toLegacy(type);
                        data = 0;
                    }
                } else {
                    // Self type -> legacy. This shouldn't really be used, ever.
                    self = CraftMagicNumbersHandle.toLegacy(self);
                }
            }

            // Type must match
            if (type != self) {
                return false;
            }
        }

        // Data must match (if legacy)
        if (this.hasData() && MaterialUtil.isLegacyType(type)) {
            // For legacy leaves, use a 0x3 bitfield mask
            if (this.hasType() && CommonLegacyMaterials.getMaterialName(this.getType()).equals("LEGACY_LEAVES")) {
                data &= 0x3;
            }

            // Data must match
            if (data != this.getData()) {
                return false;
            }
        }

        return true;
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
    @Deprecated
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

    /**
     * Gets a list of metadata rules for matching the items
     * 
     * @return metadata rules
     */
    public List<ItemParserMetaRule> getMetaRules() {
        return Collections.unmodifiableList(this.rules);
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
        if (this.type.isBlock()) {
            // For Block items, use BlockData that supports the legacy Material data API
            BlockData block;
            if (this.hasData()) {
                block = BlockData.fromMaterialData(this.type, this.data);
            } else {
                block = BlockData.fromMaterial(this.type);
            }
            return block.createItem(amount);
        } else {
            // For items, rely on standard constructor. Data is durability.
            if (this.hasData()) {
                return new ItemStack(this.type, amount, (short) this.data);
            } else {
                return new ItemStack(this.type, amount);
            }
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
        ItemParser clone = this.cloneParser();
        this.data = data;
        return clone;
    }

    /**
     * Creates a new ItemParser with the type and data of this parser, but with
     * a new amount
     *
     * @param amount for the new parser, -1 for infinite
     * @return new ItemParser with the new amount
     */
    public ItemParser setAmount(int amount) {
        ItemParser clone = this.cloneParser();
        clone.amount = amount;
        return clone;
    }

    /**
     * Creates a new ItemParser with the type, data and amount of this parser, but with
     * different metadata rules.
     * 
     * @param metadata rules
     */
    public ItemParser setMetaRules(List<ItemParserMetaRule> rules) {
        ItemParser clone = this.cloneParser();
        clone.rules = (rules.isEmpty() ? Collections.emptyList() : new ArrayList<ItemParserMetaRule>(rules));
        return clone;
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

    private ItemParser cloneParser() {
        return new ItemParser(this.type, this.amount, this.data, 
                (this.rules.isEmpty() ? Collections.emptyList() : new ArrayList<ItemParserMetaRule>(this.rules)));
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
        if (!this.rules.isEmpty()) {
            rval.append(METADATA_CHAR);
            for (int i = 0; i < this.rules.size(); i++) {
                rval.append(METADATA_CHAR);
                rval.append(this.rules.get(i).toString());
            }
        }
        return rval.toString();
    }
}
