package com.bergerkiller.generated.net.minecraft.world.entity.monster;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.generated.net.minecraft.world.entity.MobHandle;
import org.bukkit.block.BlockFace;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.monster.Shulker</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.entity.monster.Shulker")
public abstract class ShulkerHandle extends MobHandle {
    /** @see ShulkerClass */
    public static final ShulkerClass T = Template.Class.create(ShulkerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ShulkerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static final Key<org.bukkit.block.BlockFace> DATA_FACE_DIRECTION = Key.Type.DIRECTION.createKey(T.DATA_FACE_DIRECTION, -1);
    public static final Key<Byte> DATA_PEEK = Key.Type.BYTE.createKey(T.DATA_PEEK, -1);
    public static final Key<Byte> DATA_COLOR = Key.Type.BYTE.createKey(T.DATA_COLOR, -1);
    /**
     * Stores class members for <b>net.minecraft.world.entity.monster.Shulker</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ShulkerClass extends Template.Class<ShulkerHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<BlockFace>> DATA_FACE_DIRECTION = new Template.StaticField.Converted<Key<BlockFace>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Byte>> DATA_PEEK = new Template.StaticField.Converted<Key<Byte>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Byte>> DATA_COLOR = new Template.StaticField.Converted<Key<Byte>>();

    }

}

