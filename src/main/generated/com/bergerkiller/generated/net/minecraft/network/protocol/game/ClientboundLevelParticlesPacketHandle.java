package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.resources.ParticleType;
import com.bergerkiller.bukkit.common.wrappers.ParticleRandomization;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket")
public abstract class ClientboundLevelParticlesPacketHandle extends PacketHandle {
    /** @see ClientboundLevelParticlesPacketClass */
    public static final ClientboundLevelParticlesPacketClass T = Template.Class.create(ClientboundLevelParticlesPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundLevelParticlesPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract ParticleType<?> getParticleType();
    public abstract boolean isOverrideLimiter();
    public abstract double getPosX();
    public abstract double getPosY();
    public abstract double getPosZ();
    public abstract float getRandomX();
    public abstract float getRandomY();
    public abstract float getRandomZ();
    public abstract float getMaxSpeedX();
    public abstract float getMaxSpeedY();
    public abstract float getMaxSpeedZ();
    public abstract int getCount();
    public abstract boolean isAlwaysShow();
    public abstract ParticleRandomization getRandomization();
    public abstract ClientboundLevelParticlesPacketHandle withConfiguration(double posX, double posY, double posZ, float randomX, float randomY, float randomZ, float maxSpeedX, float maxSpeedY, float maxSpeedZ, int count, boolean overrideLimiter, boolean alwaysShow, ParticleRandomization randomization);
    public static <T> ClientboundLevelParticlesPacketHandle createNew(com.bergerkiller.bukkit.common.resources.ParticleType<T> particleType, T value, double posX, double posY, double posZ, float randomX, float randomY, float randomZ, float maxSpeedX, float maxSpeedY, float maxSpeedZ, int count, boolean overrideLimiter, boolean alwaysShow, ParticleRandomization randomization) {
        return T.createNew.invokeVA(particleType.getRawHandle(), value, posX, posY, posZ, randomX, randomY, randomZ, maxSpeedX, maxSpeedY, maxSpeedZ, count, overrideLimiter, alwaysShow, randomization);
    }

    public static <T> ClientboundLevelParticlesPacketHandle createNew(com.bergerkiller.bukkit.common.resources.ParticleType<T> particleType, T value, double posX, double posY, double posZ, float randomX, float randomY, float randomZ, float maxSpeedX, float maxSpeedY, float maxSpeedZ, int count) {
        return createNew(particleType, value, posX, posY, posZ, randomX, randomY, randomZ, maxSpeedX, maxSpeedY, maxSpeedZ, count, false, false, ParticleRandomization.DEFAULT);
    }

    public static <T> ClientboundLevelParticlesPacketHandle createNew(com.bergerkiller.bukkit.common.resources.ParticleType<T> particleType, T value, double posX, double posY, double posZ) {
        return createNew(particleType, value, posX, posY, posZ, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1, false, false, ParticleRandomization.DEFAULT);
    }

    public static ClientboundLevelParticlesPacketHandle createNew(com.bergerkiller.bukkit.common.resources.ParticleType<Void> particleType, double posX, double posY, double posZ, float randomX, float randomY, float randomZ, float maxSpeedX, float maxSpeedY, float maxSpeedZ, int count, boolean overrideLimiter, boolean alwaysShow, ParticleRandomization randomization) {
        return createNew(particleType, null, posX, posY, posZ, randomX, randomY, randomZ, maxSpeedX, maxSpeedY, maxSpeedZ, count, overrideLimiter, alwaysShow, randomization);
    }

    public static ClientboundLevelParticlesPacketHandle createNew(com.bergerkiller.bukkit.common.resources.ParticleType<Void> particleType, double posX, double posY, double posZ, float randomX, float randomY, float randomZ, float maxSpeedX, float maxSpeedY, float maxSpeedZ, int count) {
        return createNew(particleType, posX, posY, posZ, randomX, randomY, randomZ, maxSpeedX, maxSpeedY, maxSpeedZ, count, false, false, ParticleRandomization.DEFAULT);
    }

    public static ClientboundLevelParticlesPacketHandle createNew(com.bergerkiller.bukkit.common.resources.ParticleType<Void> particleType, double posX, double posY, double posZ) {
        return createNew(particleType, posX, posY, posZ, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1, false, false, ParticleRandomization.DEFAULT);
    }

    public ClientboundLevelParticlesPacketHandle withRandom(float uniformRandom) {
        return withRandom(uniformRandom, uniformRandom, uniformRandom);
    }

    public ClientboundLevelParticlesPacketHandle withRandom(float randomX, float randomY, float randomZ) {
        return withConfiguration(getPosX(), getPosY(), getPosZ(), randomX, randomY, randomZ, getMaxSpeedX(), getMaxSpeedY(), getMaxSpeedZ(), getCount(), isOverrideLimiter(), isAlwaysShow(), getRandomization());
    }

    public ClientboundLevelParticlesPacketHandle withMaxSpeed(float maxUniformSpeed) {
        return withMaxSpeed(maxUniformSpeed, maxUniformSpeed, maxUniformSpeed);
    }

    public ClientboundLevelParticlesPacketHandle withMaxSpeed(float maxSpeedX, float maxSpeedY, float maxSpeedZ) {
        return withConfiguration(getPosX(), getPosY(), getPosZ(), getRandomX(), getRandomY(), getRandomZ(), maxSpeedX, maxSpeedY, maxSpeedZ, getCount(), isOverrideLimiter(), isAlwaysShow(), getRandomization());
    }

    public ClientboundLevelParticlesPacketHandle withOverrideLimiter() {
        return withConfiguration(getPosX(), getPosY(), getPosZ(), getRandomX(), getRandomY(), getRandomZ(), getMaxSpeedX(), getMaxSpeedY(), getMaxSpeedZ(), getCount(), true, isAlwaysShow(), getRandomization());
    }

    public ClientboundLevelParticlesPacketHandle withAlwaysShow() {
        return withConfiguration(getPosX(), getPosY(), getPosZ(), getRandomX(), getRandomY(), getRandomZ(), getMaxSpeedX(), getMaxSpeedY(), getMaxSpeedZ(), getCount(), isOverrideLimiter(), true, getRandomization());
    }

    public ClientboundLevelParticlesPacketHandle withRandomization(ParticleRandomization randomization) {
        return withConfiguration(getPosX(), getPosY(), getPosZ(), getRandomX(), getRandomY(), getRandomZ(), getMaxSpeedX(), getMaxSpeedY(), getMaxSpeedZ(), getCount(), isOverrideLimiter(), isAlwaysShow(), randomization);
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundLevelParticlesPacketClass extends Template.Class<ClientboundLevelParticlesPacketHandle> {
        @Template.Optional
        public final Template.StaticMethod.Converted<ClientboundLevelParticlesPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundLevelParticlesPacketHandle>();

        public final Template.Method.Converted<ParticleType<?>> getParticleType = new Template.Method.Converted<ParticleType<?>>();
        public final Template.Method<Boolean> isOverrideLimiter = new Template.Method<Boolean>();
        public final Template.Method<Double> getPosX = new Template.Method<Double>();
        public final Template.Method<Double> getPosY = new Template.Method<Double>();
        public final Template.Method<Double> getPosZ = new Template.Method<Double>();
        public final Template.Method<Float> getRandomX = new Template.Method<Float>();
        public final Template.Method<Float> getRandomY = new Template.Method<Float>();
        public final Template.Method<Float> getRandomZ = new Template.Method<Float>();
        public final Template.Method<Float> getMaxSpeedX = new Template.Method<Float>();
        public final Template.Method<Float> getMaxSpeedY = new Template.Method<Float>();
        public final Template.Method<Float> getMaxSpeedZ = new Template.Method<Float>();
        public final Template.Method<Integer> getCount = new Template.Method<Integer>();
        public final Template.Method<Boolean> isAlwaysShow = new Template.Method<Boolean>();
        public final Template.Method.Converted<ParticleRandomization> getRandomization = new Template.Method.Converted<ParticleRandomization>();
        public final Template.Method.Converted<ClientboundLevelParticlesPacketHandle> withConfiguration = new Template.Method.Converted<ClientboundLevelParticlesPacketHandle>();

    }

}

