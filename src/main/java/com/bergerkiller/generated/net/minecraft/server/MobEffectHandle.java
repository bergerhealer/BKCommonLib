package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.MobEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class MobEffectHandle extends Template.Handle {
    /** @See {@link MobEffectClass} */
    public static final MobEffectClass T = new MobEffectClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MobEffectHandle.class, "net.minecraft.server.MobEffect");

    /* ============================================================================== */

    public static MobEffectHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        MobEffectHandle handle = new MobEffectHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static MobEffectHandle fromNBT(CommonTagCompound compound) {
        return T.fromNBT.invokeVA(compound);
    }

    public MobEffectListHandle getEffectList() {
        return T.effectList.get(instance);
    }

    public void setEffectList(MobEffectListHandle value) {
        T.effectList.set(instance, value);
    }

    public int getDuration() {
        return T.duration.getInteger(instance);
    }

    public void setDuration(int value) {
        T.duration.setInteger(instance, value);
    }

    public int getAmplification() {
        return T.amplification.getInteger(instance);
    }

    public void setAmplification(int value) {
        T.amplification.setInteger(instance, value);
    }

    public boolean isSplash() {
        return T.splash.getBoolean(instance);
    }

    public void setSplash(boolean value) {
        T.splash.setBoolean(instance, value);
    }

    public boolean isAmbient() {
        return T.ambient.getBoolean(instance);
    }

    public void setAmbient(boolean value) {
        T.ambient.setBoolean(instance, value);
    }

    public boolean isParticles() {
        return T.particles.getBoolean(instance);
    }

    public void setParticles(boolean value) {
        T.particles.setBoolean(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.MobEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MobEffectClass extends Template.Class<MobEffectHandle> {
        public final Template.Field.Converted<MobEffectListHandle> effectList = new Template.Field.Converted<MobEffectListHandle>();
        public final Template.Field.Integer duration = new Template.Field.Integer();
        public final Template.Field.Integer amplification = new Template.Field.Integer();
        public final Template.Field.Boolean splash = new Template.Field.Boolean();
        public final Template.Field.Boolean ambient = new Template.Field.Boolean();
        public final Template.Field.Boolean particles = new Template.Field.Boolean();

        public final Template.StaticMethod.Converted<MobEffectHandle> fromNBT = new Template.StaticMethod.Converted<MobEffectHandle>();

    }

}

