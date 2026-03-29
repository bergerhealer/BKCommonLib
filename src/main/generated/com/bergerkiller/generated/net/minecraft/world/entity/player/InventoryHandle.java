package com.bergerkiller.generated.net.minecraft.world.entity.player;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.generated.net.minecraft.world.ContainerHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.player.Inventory</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.player.Inventory")
public abstract class InventoryHandle extends ContainerHandle {
    /** @see InventoryClass */
    public static final InventoryClass T = Template.Class.create(InventoryClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static InventoryHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static int getHotbarSize() {
        return T.getHotbarSize.invoker.invoke(null);
    }

    public abstract CommonTagList saveToNBT(CommonTagList nbttaglist);
    public abstract void loadFromNBT(CommonTagList nbttaglist);
    /**
     * Stores class members for <b>net.minecraft.world.entity.player.Inventory</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class InventoryClass extends Template.Class<InventoryHandle> {
        public final Template.StaticMethod<Integer> getHotbarSize = new Template.StaticMethod<Integer>();

        public final Template.Method.Converted<CommonTagList> saveToNBT = new Template.Method.Converted<CommonTagList>();
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted<Void>();

    }

}

