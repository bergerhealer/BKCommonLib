package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.MethodAccessor;
import com.bergerkiller.reflection.SafeDirectField;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftMagicNumbers;

public class NMSBlock {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("Block");

    public static final FieldAccessor<String> name = T.selectField("private String name");
    public static final FieldAccessor<Object> material = T.selectField("protected final Material material");

    /*
     * Method signature:
     * 
     # public final void ##METHODNAME##(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
     *     this.dropNaturally(world, blockposition, iblockdata, 1.0F, i);
     * }
     */
    public static final MethodAccessor<Void> dropNaturally = T.selectMethod("public final void b(World, BlockPosition, IBlockData, int)");
    public static final MethodAccessor<Void> ignite = T.selectMethod("public void wasExploded(World, BlockPosition, Explosion)");

    @Deprecated
    public static final FieldAccessor<Integer> id = new SafeDirectField<Integer>() {
        @Override
        public Integer get(Object instance) {
        	return CBCraftMagicNumbers.getId.invoke(null, instance);
        }

        @Override
        public boolean set(Object instance, Integer value) {
            return false;
        }
    };

    @Deprecated
    public static int getBlockId(Object instance) {
        return id.get(instance);
    }
}
