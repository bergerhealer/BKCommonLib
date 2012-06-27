package com.bergerkiller.bukkit.common;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.EnumUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

public class ItemParser {
	
	public ItemParser(Material type) {
		this(null, type);
	}
	public ItemParser(Integer amount, Material type) {
		this(amount, type, null);
	}
	public ItemParser(Integer amount, Material type, Byte data) {
		this.amount = (this.hasamount = amount != null) ? amount : 0;
		this.data = (this.hasdata = data != null) ? data : 0;
		this.type = type;
	}
	private ItemParser(boolean hasdata) {
		this.hasdata = hasdata;
	}
	private byte data = 0;
	private Material type = null;
	private int amount = 1;
	private boolean hasdata, hasamount;
	
	/**
	 * Supported formats:
	 * typedata:
	 *   [type]:[data]
	 *   [typeid]:[data]
	 *   [typeid]
	 *   
	 * Amount/name relationship:
	 * [amount]x[typedata]
	 * [amount]*[typedata]
	 * [amount] [typedata]
	 * [amount]@[typedata]
	 * [typedata]
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
		ItemParser parser = new ItemParser(dataname != null);
		if (amount == null) {
			parser.hasamount = false;
		} else {
			try {
				parser.amount = Integer.parseInt(amount);
				parser.hasamount = true;
			} catch (NumberFormatException ex) {
				parser.hasamount = false;
			}
		}
		//match material from name
		if (name == null || name.isEmpty()) {
			parser.type = null;
			return parser;
		}
		parser.type = EnumUtil.parseMaterial(name, null);
		if (parser.type == null) {
			parser.type = Material.AIR;
		}
		//match data name if needed
		if (parser.hasdata) {
			Byte dat = ItemUtil.getData(parser.type, dataname);
			if (dat == null) {
				parser.hasdata = false;
			} else {
				parser.data = dat;
			}
		}
		return parser;
	}
	
	public boolean match(net.minecraft.server.ItemStack stack) {
		return this.match(stack.id, stack.getData());
	}
	public boolean match(ItemStack stack) {
		return this.match(stack.getTypeId(), stack.getData().getData());
	}
	public boolean match(Material type, int data) {
		return this.match(type.getId(), data);
	}
	public boolean match(int typeid, int data) {
		if (this.hasType() && typeid != this.getTypeId()) return false; 
		if (this.hasData() && data != this.getData()) return false;
		return true;
	}
	
	public boolean hasAmount() {
		return this.hasamount;
	}
	public boolean hasData() {
		return this.hasdata;
	}
	public boolean hasType() {
		return this.type != null;
	}
	public byte getData() {
	    return this.data;
	}
	public int getAmount() {
		return this.amount;
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

	public String toString() {
		StringBuilder rval = new StringBuilder();
		if (this.hasamount) {
			rval.append(this.amount).append(" of ");
		}
		if (this.type == null) {
			rval.append("any type");
		} else {
			rval.append(this.type.toString().toLowerCase());
			if (this.hasdata) {
				rval.append(':').append(this.data);
			}
		}
		return rval.toString();
	}
}
