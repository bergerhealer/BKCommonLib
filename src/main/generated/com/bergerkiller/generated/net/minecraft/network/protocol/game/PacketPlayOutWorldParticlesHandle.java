package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.resources.ParticleType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutWorldParticles</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutWorldParticles")
public abstract class PacketPlayOutWorldParticlesHandle extends PacketHandle {
    /** @see PacketPlayOutWorldParticlesClass */
    public static final PacketPlayOutWorldParticlesClass T = Template.Class.create(PacketPlayOutWorldParticlesClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutWorldParticlesHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutWorldParticlesHandle createNew() {
        return T.createNew.invoke();
    }

    public abstract ParticleType<?> getParticleType();
    public abstract double getPosX();
    public abstract double getPosY();
    public abstract double getPosZ();
    public abstract void setPosX(double x);
    public abstract void setPosY(double y);
    public abstract void setPosZ(double z);

    public void setParticle(com.bergerkiller.bukkit.common.resources.ParticleType<Void> particleType) {
        setParticle(particleType, null);
    }

    public <T> void setParticle(com.bergerkiller.bukkit.common.resources.ParticleType<T> particleType, T value) {
        T.setParticle.invoker.invoke(getRaw(), particleType.getRawHandle(), value);
    }


    public void setPos(double x, double y, double z) {
        setPosX(x);
        setPosY(y);
        setPosZ(z);
    }

    public void setPos(org.bukkit.util.Vector pos) {
        setPos(pos.getX(), pos.getY(), pos.getZ());
    }

    public void setPos(org.bukkit.Location loc) {
        setPos(loc.getX(), loc.getY(), loc.getZ());
    }

    public void setRandom(double rx, double ry, double rz) {
        setRandom((float) rx, (float) ry, (float) rz);
    }

    public void setRandom(float rx, float ry, float rz) {
        setRandomX(rx);
        setRandomY(ry);
        setRandomZ(rz);
    }

    public void setRandom(org.bukkit.util.Vector random) {
        setRandom(random.getX(), random.getY(), random.getZ());
    }
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
    public abstract boolean isOverrideLimiter();
    public abstract void setOverrideLimiter(boolean value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutWorldParticles</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutWorldParticlesClass extends Template.Class<PacketPlayOutWorldParticlesHandle> {
        public final Template.Field.Float randomX = new Template.Field.Float();
        public final Template.Field.Float randomY = new Template.Field.Float();
        public final Template.Field.Float randomZ = new Template.Field.Float();
        public final Template.Field.Float speed = new Template.Field.Float();
        public final Template.Field.Integer count = new Template.Field.Integer();
        public final Template.Field.Boolean overrideLimiter = new Template.Field.Boolean();

        public final Template.StaticMethod.Converted<PacketPlayOutWorldParticlesHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutWorldParticlesHandle>();

        @Template.Optional
        public final Template.Method<Void> setParticle = new Template.Method<Void>();
        public final Template.Method.Converted<ParticleType<?>> getParticleType = new Template.Method.Converted<ParticleType<?>>();
        public final Template.Method<Double> getPosX = new Template.Method<Double>();
        public final Template.Method<Double> getPosY = new Template.Method<Double>();
        public final Template.Method<Double> getPosZ = new Template.Method<Double>();
        public final Template.Method<Void> setPosX = new Template.Method<Void>();
        public final Template.Method<Void> setPosY = new Template.Method<Void>();
        public final Template.Method<Void> setPosZ = new Template.Method<Void>();

    }

}

