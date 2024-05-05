package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.Interaction</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.entity.Interaction")
public abstract class InteractionHandle extends EntityHandle {
    /** @see InteractionClass */
    public static final InteractionClass T = Template.Class.create(InteractionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static InteractionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static final Key<Float> DATA_WIDTH = Key.Type.FLOAT.createKey(T.DATA_WIDTH_ID, -1);
    public static final Key<Float> DATA_HEIGHT = Key.Type.FLOAT.createKey(T.DATA_HEIGHT_ID, -1);
    public static final Key<Boolean> DATA_RESPONSE = Key.Type.BOOLEAN.createKey(T.DATA_RESPONSE_ID, -1);
    /**
     * Stores class members for <b>net.minecraft.world.entity.Interaction</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class InteractionClass extends Template.Class<InteractionHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Float>> DATA_WIDTH_ID = new Template.StaticField.Converted<Key<Float>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Float>> DATA_HEIGHT_ID = new Template.StaticField.Converted<Key<Float>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Boolean>> DATA_RESPONSE_ID = new Template.StaticField.Converted<Key<Boolean>>();

    }

}

