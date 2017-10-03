package com.bergerkiller.reflection.net.minecraft.server;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

@Deprecated
public class NMSWorldManager {
	public static final ClassTemplate<?> T = ClassTemplate.createNMS("WorldManager");
	public static final TranslatorFieldAccessor<World> world = T.selectField("private final WorldServer world").translate(DuplexConversion.world);
}
