package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.TileEntityChest;
import net.minecraft.server.TileEntityDispenser;
import net.minecraft.server.TileEntityFurnace;
import net.minecraft.server.TileEntitySign;

import org.bukkit.craftbukkit.block.CraftChest;
import org.bukkit.craftbukkit.block.CraftDispenser;
import org.bukkit.craftbukkit.block.CraftFurnace;
import org.bukkit.craftbukkit.block.CraftSign;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class BlockStateRef {
	public static final FieldAccessor<TileEntitySign> SIGN = new SafeField<TileEntitySign>(CraftSign.class, "sign");
	public static final FieldAccessor<TileEntityFurnace> FURNACE = new SafeField<TileEntityFurnace>(CraftFurnace.class, "furnace");
	public static final FieldAccessor<TileEntityDispenser> DISPENSER = new SafeField<TileEntityDispenser>(CraftDispenser.class, "dispenser");
	public static final FieldAccessor<TileEntityChest> CHEST = new SafeField<TileEntityChest>(CraftChest.class, "chest");
}
