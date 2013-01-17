package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.v1_4_R1.TileEntityChest;
import net.minecraft.server.v1_4_R1.TileEntityDispenser;
import net.minecraft.server.v1_4_R1.TileEntityFurnace;
import net.minecraft.server.v1_4_R1.TileEntitySign;

import org.bukkit.craftbukkit.v1_4_R1.block.CraftChest;
import org.bukkit.craftbukkit.v1_4_R1.block.CraftDispenser;
import org.bukkit.craftbukkit.v1_4_R1.block.CraftFurnace;
import org.bukkit.craftbukkit.v1_4_R1.block.CraftSign;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class BlockStateRef {
	public static final FieldAccessor<TileEntitySign> SIGN = new SafeField<TileEntitySign>(CraftSign.class, "sign");
	public static final FieldAccessor<TileEntityFurnace> FURNACE = new SafeField<TileEntityFurnace>(CraftFurnace.class, "furnace");
	public static final FieldAccessor<TileEntityDispenser> DISPENSER = new SafeField<TileEntityDispenser>(CraftDispenser.class, "dispenser");
	public static final FieldAccessor<TileEntityChest> CHEST = new SafeField<TileEntityChest>(CraftChest.class, "chest");
}
