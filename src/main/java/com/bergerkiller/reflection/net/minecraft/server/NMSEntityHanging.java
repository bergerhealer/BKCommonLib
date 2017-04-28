package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

public class NMSEntityHanging extends NMSEntity {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityHanging");
    public static final TranslatorFieldAccessor<IntVector3> blockPosition = T.selectField("public BlockPosition blockPosition").translate(ConversionPairs.blockPosition);
}
