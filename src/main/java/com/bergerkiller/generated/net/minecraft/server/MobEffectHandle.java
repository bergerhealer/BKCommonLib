package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.MobEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class MobEffectHandle extends Template.Handle {
    /** @See {@link MobEffectClass} */
    public static final MobEffectClass T = new MobEffectClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MobEffectHandle.class, "net.minecraft.server.MobEffect");

    /* ============================================================================== */

    public static MobEffectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static MobEffectHandle fromNBT(CommonTagCompound compound) {
        return T.fromNBT.invoke(compound);
    }

    public abstract MobEffectListHandle getEffectList();
    public abstract void setEffectList(MobEffectListHandle value);
    public abstract int getDuration();
    public abstract void setDuration(int value);
    public abstract int getAmplification();
    public abstract void setAmplification(int value);
    public abstract boolean isSplash();
    public abstract void setSplash(boolean value);
    public abstract boolean isAmbient();
    public abstract void setAmbient(boolean value);
    public abstract boolean isParticles();
    public abstract void setParticles(boolean value);
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

