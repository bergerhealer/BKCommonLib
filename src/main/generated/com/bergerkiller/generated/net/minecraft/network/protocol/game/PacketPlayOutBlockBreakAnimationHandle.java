package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation")
public abstract class PacketPlayOutBlockBreakAnimationHandle extends PacketHandle {
    /** @See {@link PacketPlayOutBlockBreakAnimationClass} */
    public static final PacketPlayOutBlockBreakAnimationClass T = Template.Class.create(PacketPlayOutBlockBreakAnimationClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutBlockBreakAnimationHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getId();
    public abstract void setId(int value);
    public abstract IntVector3 getPosition();
    public abstract void setPosition(IntVector3 value);
    public abstract int getProgress();
    public abstract void setProgress(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutBlockBreakAnimationClass extends Template.Class<PacketPlayOutBlockBreakAnimationHandle> {
        public final Template.Field.Integer id = new Template.Field.Integer();
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted<IntVector3>();
        public final Template.Field.Integer progress = new Template.Field.Integer();

    }

}

