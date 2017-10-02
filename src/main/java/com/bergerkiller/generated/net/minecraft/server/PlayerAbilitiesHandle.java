package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PlayerAbilities</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PlayerAbilitiesHandle extends Template.Handle {
    /** @See {@link PlayerAbilitiesClass} */
    public static final PlayerAbilitiesClass T = new PlayerAbilitiesClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PlayerAbilitiesHandle.class, "net.minecraft.server.PlayerAbilities");

    /* ============================================================================== */

    public static PlayerAbilitiesHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PlayerAbilitiesHandle createNew() {
        return T.constr.newInstance();
    }

    /* ============================================================================== */

    public boolean isInvulnerable() {
        return T.isInvulnerable.getBoolean(getRaw());
    }

    public void setIsInvulnerable(boolean value) {
        T.isInvulnerable.setBoolean(getRaw(), value);
    }

    public boolean isFlying() {
        return T.isFlying.getBoolean(getRaw());
    }

    public void setIsFlying(boolean value) {
        T.isFlying.setBoolean(getRaw(), value);
    }

    public boolean isCanFly() {
        return T.canFly.getBoolean(getRaw());
    }

    public void setCanFly(boolean value) {
        T.canFly.setBoolean(getRaw(), value);
    }

    public boolean isCanInstantlyBuild() {
        return T.canInstantlyBuild.getBoolean(getRaw());
    }

    public void setCanInstantlyBuild(boolean value) {
        T.canInstantlyBuild.setBoolean(getRaw(), value);
    }

    public boolean isMayBuild() {
        return T.mayBuild.getBoolean(getRaw());
    }

    public void setMayBuild(boolean value) {
        T.mayBuild.setBoolean(getRaw(), value);
    }

    public float getFlySpeed() {
        return T.flySpeed.getFloat(getRaw());
    }

    public void setFlySpeed(float value) {
        T.flySpeed.setFloat(getRaw(), value);
    }

    public float getWalkSpeed() {
        return T.walkSpeed.getFloat(getRaw());
    }

    public void setWalkSpeed(float value) {
        T.walkSpeed.setFloat(getRaw(), value);
    }

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
        public final Template.Field.Float flySpeed = new Template.Field.Float();
        public final Template.Field.Float walkSpeed = new Template.Field.Float();

    }

}

