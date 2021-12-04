package com.bergerkiller.generated.net.minecraft.world.entity.decoration;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.decoration.EntityItemFrame</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.decoration.EntityItemFrame")
public abstract class EntityItemFrameHandle extends EntityHangingHandle {
    /** @See {@link EntityItemFrameClass} */
    public static final EntityItemFrameClass T = Template.Class.create(EntityItemFrameClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityItemFrameHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean getItemIsMap();
    public abstract UUID getItemMapDisplayUUID();
    public abstract ItemStack getItem();
    public abstract void setItem(ItemStack newItemStack);
    public abstract void refreshItem();
    public abstract int getRotationOrdinal();

    public static final Key<org.bukkit.inventory.ItemStack> DATA_ITEM = Key.Type.ITEMSTACK.createKey(T.DATA_ITEM, 8);


    public static EntityItemFrameHandle fromBukkit(org.bukkit.entity.ItemFrame itemFrame) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toEntityHandle(itemFrame));
    }
    /**
     * Stores class members for <b>net.minecraft.world.entity.decoration.EntityItemFrame</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityItemFrameClass extends Template.Class<EntityItemFrameHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Object>> DATA_ITEM = new Template.StaticField.Converted<Key<Object>>();

        public final Template.Method<Boolean> getItemIsMap = new Template.Method<Boolean>();
        public final Template.Method<UUID> getItemMapDisplayUUID = new Template.Method<UUID>();
        public final Template.Method.Converted<ItemStack> getItem = new Template.Method.Converted<ItemStack>();
        public final Template.Method.Converted<Void> setItem = new Template.Method.Converted<Void>();
        public final Template.Method<Void> refreshItem = new Template.Method<Void>();
        public final Template.Method<Integer> getRotationOrdinal = new Template.Method<Integer>();

    }

}

