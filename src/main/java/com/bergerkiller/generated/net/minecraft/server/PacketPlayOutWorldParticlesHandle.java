package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutWorldParticles</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PacketPlayOutWorldParticlesHandle extends Template.Handle {
    /** @See {@link PacketPlayOutWorldParticlesClass} */
    public static final PacketPlayOutWorldParticlesClass T = new PacketPlayOutWorldParticlesClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutWorldParticlesHandle.class, "net.minecraft.server.PacketPlayOutWorldParticles");

    /* ============================================================================== */

    public static PacketPlayOutWorldParticlesHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract float getX();
    public abstract void setX(float value);
    public abstract float getY();
    public abstract void setY(float value);
    public abstract float getZ();
    public abstract void setZ(float value);
    public abstract float getRandomX();
    public abstract void setRandomX(float value);
    public abstract float getRandomY();
    public abstract void setRandomY(float value);
    public abstract float getRandomZ();
    public abstract void setRandomZ(float value);
    public abstract float getSpeed();
    public abstract void setSpeed(float value);
    public abstract int getCount();
    public abstract void setCount(int value);
    public abstract boolean isLongDistance();
    public abstract void setLongDistance(boolean value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutWorldParticles</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutWorldParticlesClass extends Template.Class<PacketPlayOutWorldParticlesHandle> {
        public final Template.Field.Float x = new Template.Field.Float();
        public final Template.Field.Float y = new Template.Field.Float();
        public final Template.Field.Float z = new Template.Field.Float();
        public final Template.Field.Float randomX = new Template.Field.Float();
        public final Template.Field.Float randomY = new Template.Field.Float();
        public final Template.Field.Float randomZ = new Template.Field.Float();
        public final Template.Field.Float speed = new Template.Field.Float();
        public final Template.Field.Integer count = new Template.Field.Integer();
        public final Template.Field.Boolean longDistance = new Template.Field.Boolean();

    }

}

