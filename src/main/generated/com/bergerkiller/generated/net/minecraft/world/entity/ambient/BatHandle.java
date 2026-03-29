package com.bergerkiller.generated.net.minecraft.world.entity.ambient;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.generated.net.minecraft.world.entity.MobHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.ambient.Bat</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.ambient.Bat")
public abstract class BatHandle extends MobHandle {
    /** @see BatClass */
    public static final BatClass T = Template.Class.create(BatClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static BatHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static final Key<Byte> DATA_BAT_FLAGS = Key.Type.BYTE.createKey(T.DATA_BAT_FLAGS, 16);
    public static final int DATA_BAT_FLAG_HANGING = (1 << 0);
    /**
     * Stores class members for <b>net.minecraft.world.entity.ambient.Bat</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BatClass extends Template.Class<BatHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Byte>> DATA_BAT_FLAGS = new Template.StaticField.Converted<Key<Byte>>();

    }

}

