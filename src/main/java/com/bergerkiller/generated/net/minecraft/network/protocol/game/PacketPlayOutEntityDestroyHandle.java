package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy")
public abstract class PacketPlayOutEntityDestroyHandle extends PacketHandle {
    /** @See {@link PacketPlayOutEntityDestroyClass} */
    public static final PacketPlayOutEntityDestroyClass T = Template.Class.create(PacketPlayOutEntityDestroyClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutEntityDestroyHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutEntityDestroyHandle createNewSingle(int entityId) {
        return T.createNewSingle.invoke(entityId);
    }

    public static PacketPlayOutEntityDestroyHandle createNewMultiple(int[] multipleEntityIds) {
        return T.createNewMultiple.invoke(multipleEntityIds);
    }

    public abstract boolean canSupportMultipleEntityIds();
    public abstract boolean hasMultipleEntityIds();
    public abstract int getSingleEntityId();
    public abstract int[] getEntityIds();
    public abstract void setSingleEntityId(int entityId);
    public abstract void setMultpleEntityIds(int[] multipleEntityIds);

    public static boolean canDestroyMultiple() {
        return com.bergerkiller.bukkit.common.internal.CommonCapabilities.PACKET_DESTROY_MULTIPLE;
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutEntityDestroyClass extends Template.Class<PacketPlayOutEntityDestroyHandle> {
        public final Template.StaticMethod.Converted<PacketPlayOutEntityDestroyHandle> createNewSingle = new Template.StaticMethod.Converted<PacketPlayOutEntityDestroyHandle>();
        public final Template.StaticMethod.Converted<PacketPlayOutEntityDestroyHandle> createNewMultiple = new Template.StaticMethod.Converted<PacketPlayOutEntityDestroyHandle>();

        public final Template.Method<Boolean> canSupportMultipleEntityIds = new Template.Method<Boolean>();
        public final Template.Method<Boolean> hasMultipleEntityIds = new Template.Method<Boolean>();
        public final Template.Method<Integer> getSingleEntityId = new Template.Method<Integer>();
        public final Template.Method<int[]> getEntityIds = new Template.Method<int[]>();
        public final Template.Method<Void> setSingleEntityId = new Template.Method<Void>();
        public final Template.Method<Void> setMultpleEntityIds = new Template.Method<Void>();

    }

}

