package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.world.item.ItemHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeDirectField;

public class NMSItem {
    public static final ClassTemplate<?> T = ClassTemplate.create(ItemHandle.T.getType());
    public static final FieldAccessor<Integer> maxStackSize = new SafeDirectField<Integer>() {
        @Override
        public Integer get(Object instance) {
            return ItemHandle.T.getMaxStackSize.invoke(instance);
        }

        @Override
        public boolean set(Object instance, Integer value) {
            ItemHandle.T.setMaxStackSize.invoker.invoke(instance, value);
            return true;
        }
    };
}
