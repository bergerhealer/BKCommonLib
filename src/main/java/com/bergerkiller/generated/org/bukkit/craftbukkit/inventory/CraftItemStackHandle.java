package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.inventory.CraftItemStack</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class CraftItemStackHandle extends Template.Handle {
    /** @See {@link CraftItemStackClass} */
    public static final CraftItemStackClass T = new CraftItemStackClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftItemStackHandle.class, "org.bukkit.craftbukkit.inventory.CraftItemStack", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static CraftItemStackHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Object asNMSCopy(ItemStack original) {
        return T.asNMSCopy.invoker.invoke(null,original);
    }

    public static ItemStack asCraftCopy(ItemStack original) {
        return T.asCraftCopy.invoke(original);
    }

    public static ItemStack asCraftMirror(Object nmsItemStack) {
        return T.asCraftMirror.invoke(nmsItemStack);
    }

    public static Map<String, Object> serialize(ItemStack item) {
        return T.serialize.invoker.invoke(null,item);
    }

    public static ItemStack deserialize(Map<String, Object> values) {
        return T.deserialize.invoker.invoke(null,values);
    }

    public abstract Object getHandle();
    public abstract void setHandle(Object value);
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.inventory.CraftItemStack</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftItemStackClass extends Template.Class<CraftItemStackHandle> {
        public final Template.Field.Converted<Object> handle = new Template.Field.Converted<Object>();

        public final Template.StaticMethod<Object> asNMSCopy = new Template.StaticMethod<Object>();
        public final Template.StaticMethod.Converted<ItemStack> asCraftCopy = new Template.StaticMethod.Converted<ItemStack>();
        public final Template.StaticMethod.Converted<ItemStack> asCraftMirror = new Template.StaticMethod.Converted<ItemStack>();
        public final Template.StaticMethod<Map<String, Object>> serialize = new Template.StaticMethod<Map<String, Object>>();
        public final Template.StaticMethod<ItemStack> deserialize = new Template.StaticMethod<ItemStack>();

    }

}

