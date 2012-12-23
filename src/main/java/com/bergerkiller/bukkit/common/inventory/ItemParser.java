package com.bergerkiller.bukkit.common.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

public class ItemParser {

	public ItemParser(Material type) {
		this(type, null);
	}

	public ItemParser(Material type, Integer amount) {
		this(type, amount, null);
	}

	public ItemParser(Material type, Integer amount, Byte data) {
		this.amount = amount;
		this.data = data;
		this.type = type;
	}

	private ItemParser() {
	}

	private Byte data = 0;
	private Material type = null;
	private Integer amount = 1;

	/**
	 * Supported formats: typedata: [type]:[data] [typeid]:[data] [typeid]
	 * 
	 * Amount/name relationship: [amount]x[typedata] [amount]*[typedata]
	 * [amount] [typedata] [amount]@[typedata] [typedata]
	 */
	public static ItemParser parse(String fullname) {
		fullname = fullname.trim();
		int index = StringUtil.firstIndexOf(fullname, "x", "X", "*", " ", "@");
		if (index == -1) {
			return parse(fullname, null);
		} else {
			return parse(fullname.substring(index + 1), fullname.substring(0, index));
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
		parser.amount = ParseUtil.parseInt(amount, null);
		// parse material from name
		parser.type = ParseUtil.parseMaterial(name, null);
		// parse material data from name if needed
		if (parser.type != null && !LogicUtil.nullOrEmpty(dataname)) {
			parser.data = ParseUtil.parseMaterialData(dataname, parser.type, null);
		}
		return parser;
	}

	public boolean match(ItemStack stack) {
		return this.match(stack.getTypeId(), stack.getData().getData());
	}

	public boolean match(Material type, int data) {
		return this.match(type.getId(), data);
	}

	public boolean match(int typeid, int data) {
		if (this.hasType() && typeid != this.getTypeId()) {
			return false;
		}
		if (this.hasData() && data != this.getData()) {
			return false;
		}
		return true;
	}

	public boolean hasAmount() {
		return this.amount != null;
	}

	public boolean hasData() {
		return this.data != null;
	}

	public boolean hasType() {
		return this.type != null;
	}

	public byte getData() {
		return this.data == null ? 0 : this.data.byteValue();
	}

	public int getAmount() {
		return this.amount == null ? -1 : this.amount.intValue();
	}

	public Material getType() {
		return this.type;
	}

	public int getTypeId() {
		return this.type.getId();
	}

	public ItemStack getItemStack() {
		return this.getItemStack(this.amount);
	}

	public ItemStack getItemStack(int amount) {
		return new ItemStack(this.type, this.amount, this.data);
	}

	public int getMaxStackSize() {
		if (this.hasData()) {
			return this.getItemStack(1).getMaxStackSize();
		} else {
			return this.getType().getMaxStackSize();
		}
	}

	/**
	 * Creates a new ItemParser with the type and amount of this parser, but with a new data
	 * 
	 * @param data for the new parser
	 * @return new ItemParser with the new data
	 */
	public ItemParser setData(Byte data) {
		return new ItemParser(this.type, this.amount, data);
	}

	/**
	 * Creates a new ItemParser with the type and data of this parser, but with a new amount
	 * 
	 * @param amount for the new parser
	 * @return new ItemParser with the new amount
	 */
	public ItemParser setAmount(Integer amount) {
		return new ItemParser(this.type, amount, this.data);
	}

	/**
	 * Multiplies the amount of this item parser with the amount specified<br>
	 * If this parser has no amount, an amount of 1 is assumed, 
	 * resulting in a new item parser with the specified amount
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
			rval.append("any type");
		} else {
			rval.append(this.type.toString().toLowerCase());
			if (this.hasData()) {
				rval.append(':').append(this.data);
			}
		}
		return rval.toString();
	}
}
