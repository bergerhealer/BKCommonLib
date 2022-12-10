package com.bergerkiller.generated.net.minecraft.world.item;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.item.Item</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.item.Item")
public abstract class ItemHandle extends Template.Handle {
    /** @See {@link ItemClass} */
    public static final ItemClass T = Template.Class.create(ItemClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ItemHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Iterable<?> getRegistry() {
        return T.getRegistry.invoker.invoke(null);
    }

    public abstract void setMaxStackSize(int maxStackSize);
    public abstract int getMaxStackSize();
    public abstract int getMaxDurability();
    public abstract boolean usesDurability();
    public abstract String getInternalName(ItemStack itemstack);
    /**
     * Stores class members for <b>net.minecraft.world.item.Item</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ItemClass extends Template.Class<ItemHandle> {
        public final Template.StaticMethod<Iterable<?>> getRegistry = new Template.StaticMethod<Iterable<?>>();

        public final Template.Method<Void> setMaxStackSize = new Template.Method<Void>();
        public final Template.Method<Integer> getMaxStackSize = new Template.Method<Integer>();
        public final Template.Method<Integer> getMaxDurability = new Template.Method<Integer>();
        public final Template.Method<Boolean> usesDurability = new Template.Method<Boolean>();
        public final Template.Method.Converted<String> getInternalName = new Template.Method.Converted<String>();

    }

}

