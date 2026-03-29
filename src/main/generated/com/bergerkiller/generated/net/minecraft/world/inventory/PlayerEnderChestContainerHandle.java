package com.bergerkiller.generated.net.minecraft.world.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.generated.net.minecraft.world.ContainerHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.inventory.PlayerEnderChestContainer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.inventory.PlayerEnderChestContainer")
public abstract class PlayerEnderChestContainerHandle extends ContainerHandle {
    /** @see PlayerEnderChestContainerClass */
    public static final PlayerEnderChestContainerClass T = Template.Class.create(PlayerEnderChestContainerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PlayerEnderChestContainerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void loadFromNBT(CommonTagList nbttaglist);
    public abstract CommonTagList saveToNBT();
    /**
     * Stores class members for <b>net.minecraft.world.inventory.PlayerEnderChestContainer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PlayerEnderChestContainerClass extends Template.Class<PlayerEnderChestContainerHandle> {
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<CommonTagList> saveToNBT = new Template.Method.Converted<CommonTagList>();

    }

}

