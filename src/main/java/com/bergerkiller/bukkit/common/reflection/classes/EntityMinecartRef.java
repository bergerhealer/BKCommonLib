package com.bergerkiller.bukkit.common.reflection.classes;

import org.bukkit.Material;

import net.minecraft.server.v1_4_R1.EntityMinecart;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;

public class EntityMinecartRef {
	public static final ClassTemplate<EntityMinecart> TEMPLATE = ClassTemplate.create(EntityMinecart.class);
	public static final FieldAccessor<Integer> fuel = TEMPLATE.getField("e");
	public static final TranslatorFieldAccessor<Material> type = TEMPLATE.getField("type").translate(ConversionPairs.minecartType);
}
