package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class PlayerAbilitiesHandle extends Template.Handle {
    public static final PlayerAbilitiesClass T = new PlayerAbilitiesClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PlayerAbilitiesHandle.class, "net.minecraft.server.PlayerAbilities");


    /* ============================================================================== */

    public static PlayerAbilitiesHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PlayerAbilitiesHandle handle = new PlayerAbilitiesHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final PlayerAbilitiesHandle createNew() {
        return T.constr.newInstance();
    }

    /* ============================================================================== */

    public boolean isInvulnerable() {
        return T.isInvulnerable.getBoolean(instance);
    }

    public void setIsInvulnerable(boolean value) {
        T.isInvulnerable.setBoolean(instance, value);
    }

    public boolean isFlying() {
        return T.isFlying.getBoolean(instance);
    }

    public void setIsFlying(boolean value) {
        T.isFlying.setBoolean(instance, value);
    }

    public boolean isCanFly() {
        return T.canFly.getBoolean(instance);
    }

    public void setCanFly(boolean value) {
        T.canFly.setBoolean(instance, value);
    }

    public boolean isCanInstantlyBuild() {
        return T.canInstantlyBuild.getBoolean(instance);
    }

    public void setCanInstantlyBuild(boolean value) {
        T.canInstantlyBuild.setBoolean(instance, value);
    }

    public boolean isMayBuild() {
        return T.mayBuild.getBoolean(instance);
    }

    public void setMayBuild(boolean value) {
        T.mayBuild.setBoolean(instance, value);
    }

    public float getFlySpeed() {
        return T.flySpeed.getFloat(instance);
    }

    public void setFlySpeed(float value) {
        T.flySpeed.setFloat(instance, value);
    }

    public float getWalkSpeed() {
        return T.walkSpeed.getFloat(instance);
    }

    public void setWalkSpeed(float value) {
        T.walkSpeed.setFloat(instance, value);
    }

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
