package com.bergerkiller.generated.net.minecraft.world.entity.monster;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityInsentientHandle;
import org.bukkit.block.BlockFace;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.monster.EntityShulker</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.entity.monster.EntityShulker")
public abstract class EntityShulkerHandle extends EntityInsentientHandle {
    /** @see EntityShulkerClass */
    public static final EntityShulkerClass T = Template.Class.create(EntityShulkerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityShulkerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static final Key<org.bukkit.block.BlockFace> DATA_FACE_DIRECTION = Key.Type.DIRECTION.createKey(T.DATA_FACE_DIRECTION, -1);
    public static final Key<Byte> DATA_PEEK = Key.Type.BYTE.createKey(T.DATA_PEEK, -1);
    public static final Key<Byte> DATA_COLOR = Key.Type.BYTE.createKey(T.DATA_COLOR, -1);
    /**
     * Stores class members for <b>net.minecraft.world.entity.monster.EntityShulker</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityShulkerClass extends Template.Class<EntityShulkerHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<BlockFace>> DATA_FACE_DIRECTION = new Template.StaticField.Converted<Key<BlockFace>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Byte>> DATA_PEEK = new Template.StaticField.Converted<Key<Byte>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Byte>> DATA_COLOR = new Template.StaticField.Converted<Key<Byte>>();

    }

}

