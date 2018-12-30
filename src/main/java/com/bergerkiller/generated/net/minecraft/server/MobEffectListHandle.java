package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.MobEffectList</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class MobEffectListHandle extends Template.Handle {
    /** @See {@link MobEffectListClass} */
    public static final MobEffectListClass T = new MobEffectListClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MobEffectListHandle.class, "net.minecraft.server.MobEffectList", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static MobEffectListHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static int getId(MobEffectListHandle mobeffectlist) {
        return T.getId.invoke(mobeffectlist);
    }

    public static MobEffectListHandle fromId(int id) {
        return T.fromId.invoke(id);
    }

    /**
     * Stores class members for <b>net.minecraft.server.MobEffectList</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MobEffectListClass extends Template.Class<MobEffectListHandle> {
        public final Template.StaticMethod.Converted<Integer> getId = new Template.StaticMethod.Converted<Integer>();
        public final Template.StaticMethod.Converted<MobEffectListHandle> fromId = new Template.StaticMethod.Converted<MobEffectListHandle>();

    }

}

