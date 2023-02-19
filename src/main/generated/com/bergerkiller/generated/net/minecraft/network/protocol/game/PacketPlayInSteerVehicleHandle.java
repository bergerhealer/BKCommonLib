package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInSteerVehicle</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInSteerVehicle")
public abstract class PacketPlayInSteerVehicleHandle extends PacketHandle {
    /** @See {@link PacketPlayInSteerVehicleClass} */
    public static final PacketPlayInSteerVehicleClass T = Template.Class.create(PacketPlayInSteerVehicleClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInSteerVehicleHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract float getSideways();
    public abstract void setSideways(float value);
    public abstract float getForwards();
    public abstract void setForwards(float value);
    public abstract boolean isJump();
    public abstract void setJump(boolean value);
    public abstract boolean isUnmount();
    public abstract void setUnmount(boolean value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInSteerVehicle</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInSteerVehicleClass extends Template.Class<PacketPlayInSteerVehicleHandle> {
        public final Template.Field.Float sideways = new Template.Field.Float();
        public final Template.Field.Float forwards = new Template.Field.Float();
        public final Template.Field.Boolean jump = new Template.Field.Boolean();
        public final Template.Field.Boolean unmount = new Template.Field.Boolean();

    }

}

