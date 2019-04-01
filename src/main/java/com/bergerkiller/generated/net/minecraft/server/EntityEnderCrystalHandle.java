package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityEnderCrystal</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class EntityEnderCrystalHandle extends EntityHandle {
    /** @See {@link EntityEnderCrystalClass} */
    public static final EntityEnderCrystalClass T = new EntityEnderCrystalClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityEnderCrystalHandle.class, "net.minecraft.server.EntityEnderCrystal", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    public static final Key<IntVector3> DATA_BEAM_TARGET_LOC = T.DATA_BEAM_TARGET_LOC.getSafe();
    /* ============================================================================== */

    public static EntityEnderCrystalHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public static final Key<IntVector3> DATA_BEAM_TARGET = Key.Type.BLOCK_POSITION.createKey(T.DATA_BEAM_TARGET_LOC, -1);
    /**
     * Stores class members for <b>net.minecraft.server.EntityEnderCrystal</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityEnderCrystalClass extends Template.Class<EntityEnderCrystalHandle> {
        public final Template.StaticField.Converted<Key<IntVector3>> DATA_BEAM_TARGET_LOC = new Template.StaticField.Converted<Key<IntVector3>>();

    }

}

