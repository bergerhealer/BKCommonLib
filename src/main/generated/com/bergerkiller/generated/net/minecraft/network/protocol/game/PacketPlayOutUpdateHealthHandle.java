package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutUpdateHealth</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutUpdateHealth")
public abstract class PacketPlayOutUpdateHealthHandle extends PacketHandle {
    /** @see PacketPlayOutUpdateHealthClass */
    public static final PacketPlayOutUpdateHealthClass T = Template.Class.create(PacketPlayOutUpdateHealthClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutUpdateHealthHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract float getHealth();
    public abstract void setHealth(float value);
    public abstract int getFood();
    public abstract void setFood(int value);
    public abstract float getFoodSaturation();
    public abstract void setFoodSaturation(float value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutUpdateHealth</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutUpdateHealthClass extends Template.Class<PacketPlayOutUpdateHealthHandle> {
        public final Template.Field.Float health = new Template.Field.Float();
        public final Template.Field.Integer food = new Template.Field.Integer();
        public final Template.Field.Float foodSaturation = new Template.Field.Float();

    }

}

