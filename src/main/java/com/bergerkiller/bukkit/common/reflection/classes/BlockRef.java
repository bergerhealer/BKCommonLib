package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.*;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Explosion;
import net.minecraft.server.v1_8_R3.IBlockData;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;

public class BlockRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("Block");
    public static final FieldAccessor<String> name = TEMPLATE.getField("name");
    public static final MethodAccessor<Void> dropNaturally = TEMPLATE.getMethod("b", WorldRef.TEMPLATE.getType(), BlockPosition.class, IBlockData.class, int.class);
    public static final MethodAccessor<Void> ignite = TEMPLATE.getMethod("wasExploded", WorldRef.TEMPLATE.getType(), BlockPosition.class, Explosion.class);

    @Deprecated
    public static final FieldAccessor<Integer> id = new SafeDirectField<Integer>() {
        @Override
        public Integer get(Object instance) {
            return getBlockId(instance);
        }

        @Override
        public boolean set(Object instance, Integer value) {
            return false;
        }
    };

    @Deprecated
    public static int getBlockId(Object instance) {
        return CraftMagicNumbers.getId((Block) instance);
    }
}
