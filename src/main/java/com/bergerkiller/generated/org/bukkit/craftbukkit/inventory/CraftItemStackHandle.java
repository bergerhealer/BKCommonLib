package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.inventory.CraftItemStack</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.craftbukkit.inventory.CraftItemStack")
public abstract class CraftItemStackHandle extends Template.Handle {
    /** @See {@link CraftItemStackClass} */
    public static final CraftItemStackClass T = Template.Class.create(CraftItemStackClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
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

    public static ItemMeta deserializeItemMeta(Map<String, Object> values) {
        return T.deserializeItemMeta.invoker.invoke(null,values);
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
        public final Template.StaticMethod<ItemMeta> deserializeItemMeta = new Template.StaticMethod<ItemMeta>();

    }

}

