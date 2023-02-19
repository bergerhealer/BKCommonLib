package com.bergerkiller.generated.net.minecraft.world.entity.boss.enderdragon;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal")
public abstract class EntityEnderCrystalHandle extends EntityHandle {
    /** @See {@link EntityEnderCrystalClass} */
    public static final EntityEnderCrystalClass T = Template.Class.create(EntityEnderCrystalClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityEnderCrystalHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public static final Key<IntVector3> DATA_BEAM_TARGET = Key.Type.BLOCK_POSITION.createKey(T.DATA_BEAM_TARGET, -1);
    /**
     * Stores class members for <b>net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityEnderCrystalClass extends Template.Class<EntityEnderCrystalHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<IntVector3>> DATA_BEAM_TARGET = new Template.StaticField.Converted<Key<IntVector3>>();

    }

}

