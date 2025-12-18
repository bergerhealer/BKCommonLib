package com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.vehicle.minecart.EntityMinecartCommandBlock</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.vehicle.minecart.EntityMinecartCommandBlock")
public abstract class EntityMinecartCommandBlockHandle extends EntityMinecartAbstractHandle {
    /** @see EntityMinecartCommandBlockClass */
    public static final EntityMinecartCommandBlockClass T = Template.Class.create(EntityMinecartCommandBlockClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityMinecartCommandBlockHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static final Key<String> DATA_COMMAND = Key.Type.STRING.createKey(T.DATA_COMMAND, 23);
    public static final Key<com.bergerkiller.bukkit.common.wrappers.ChatText> DATA_PREVIOUS_OUTPUT = Key.Type.CHAT_TEXT.createKey(T.DATA_PREVIOUS_OUTPUT, 24);
    /**
     * Stores class members for <b>net.minecraft.world.entity.vehicle.minecart.EntityMinecartCommandBlock</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityMinecartCommandBlockClass extends Template.Class<EntityMinecartCommandBlockHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<String>> DATA_COMMAND = new Template.StaticField.Converted<Key<String>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Object>> DATA_PREVIOUS_OUTPUT = new Template.StaticField.Converted<Key<Object>>();

    }

}

