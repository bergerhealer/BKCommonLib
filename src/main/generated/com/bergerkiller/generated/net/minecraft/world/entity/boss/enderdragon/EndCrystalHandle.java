package com.bergerkiller.generated.net.minecraft.world.entity.boss.enderdragon;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.boss.enderdragon.EndCrystal</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.boss.enderdragon.EndCrystal")
public abstract class EndCrystalHandle extends EntityHandle {
    /** @see EndCrystalClass */
    public static final EndCrystalClass T = Template.Class.create(EndCrystalClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EndCrystalHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static final Key<IntVector3> DATA_BEAM_TARGET = Key.Type.BLOCK_POSITION.createKey(T.DATA_BEAM_TARGET, -1);
    /**
     * Stores class members for <b>net.minecraft.world.entity.boss.enderdragon.EndCrystal</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EndCrystalClass extends Template.Class<EndCrystalHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<IntVector3>> DATA_BEAM_TARGET = new Template.StaticField.Converted<Key<IntVector3>>();

    }

}

