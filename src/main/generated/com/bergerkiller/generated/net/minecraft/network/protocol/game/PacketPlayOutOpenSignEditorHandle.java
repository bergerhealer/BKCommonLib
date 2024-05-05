package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor")
public abstract class PacketPlayOutOpenSignEditorHandle extends PacketHandle {
    /** @see PacketPlayOutOpenSignEditorClass */
    public static final PacketPlayOutOpenSignEditorClass T = Template.Class.create(PacketPlayOutOpenSignEditorClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutOpenSignEditorHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutOpenSignEditorHandle createNew(IntVector3 signPosition, boolean isFrontText) {
        return T.createNew.invoke(signPosition, isFrontText);
    }

    public abstract boolean isFrontText();
    public abstract void setFrontText(boolean front);
    public static PacketPlayOutOpenSignEditorHandle createNew(IntVector3 signPosition) {
        return createNew(signPosition, true);
    }
    public abstract IntVector3 getSignPosition();
    public abstract void setSignPosition(IntVector3 value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutOpenSignEditorClass extends Template.Class<PacketPlayOutOpenSignEditorHandle> {
        public final Template.Field.Converted<IntVector3> signPosition = new Template.Field.Converted<IntVector3>();

        public final Template.StaticMethod.Converted<PacketPlayOutOpenSignEditorHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutOpenSignEditorHandle>();

        public final Template.Method<Boolean> isFrontText = new Template.Method<Boolean>();
        public final Template.Method<Void> setFrontText = new Template.Method<Void>();

    }

}

