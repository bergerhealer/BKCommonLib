package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PlayerAbilities</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.PlayerAbilities")
public abstract class PlayerAbilitiesHandle extends Template.Handle {
    /** @See {@link PlayerAbilitiesClass} */
    public static final PlayerAbilitiesClass T = Template.Class.create(PlayerAbilitiesClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PlayerAbilitiesHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PlayerAbilitiesHandle createNew() {
        return T.constr.newInstance();
    }

    /* ============================================================================== */


    public void setFlySpeed(double flySpeed) {
        if (T.flySpeed_double.isAvailable()) {
            T.flySpeed_double.setDouble(getRaw(), flySpeed);
        } else {
            T.flySpeed_float.setFloat(getRaw(), (float) flySpeed);
        }
    }

    public double getFlySpeed() {
        if (T.flySpeed_double.isAvailable()) {
            return T.flySpeed_double.getDouble(getRaw());
        } else {
            return (double) T.flySpeed_float.getFloat(getRaw());
        }
    }
    public abstract boolean isInvulnerable();
    public abstract void setIsInvulnerable(boolean value);
    public abstract boolean isFlying();
    public abstract void setIsFlying(boolean value);
    public abstract boolean isCanFly();
    public abstract void setCanFly(boolean value);
    public abstract boolean isCanInstantlyBuild();
    public abstract void setCanInstantlyBuild(boolean value);
    public abstract boolean isMayBuild();
    public abstract void setMayBuild(boolean value);
    public abstract float getWalkSpeed();
    public abstract void setWalkSpeed(float value);
    /**
     * Stores class members for <b>net.minecraft.server.PlayerAbilities</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PlayerAbilitiesClass extends Template.Class<PlayerAbilitiesHandle> {
        public final Template.Constructor.Converted<PlayerAbilitiesHandle> constr = new Template.Constructor.Converted<PlayerAbilitiesHandle>();

        public final Template.Field.Boolean isInvulnerable = new Template.Field.Boolean();
        public final Template.Field.Boolean isFlying = new Template.Field.Boolean();
        public final Template.Field.Boolean canFly = new Template.Field.Boolean();
        public final Template.Field.Boolean canInstantlyBuild = new Template.Field.Boolean();
        public final Template.Field.Boolean mayBuild = new Template.Field.Boolean();
        @Template.Optional
        public final Template.Field.Float flySpeed_float = new Template.Field.Float();
        @Template.Optional
        public final Template.Field.Double flySpeed_double = new Template.Field.Double();
        public final Template.Field.Float walkSpeed = new Template.Field.Float();

    }

}

