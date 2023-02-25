package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.Material;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutBlockAction</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutBlockAction")
public abstract class PacketPlayOutBlockActionHandle extends PacketHandle {
    /** @see PacketPlayOutBlockActionClass */
    public static final PacketPlayOutBlockActionClass T = Template.Class.create(PacketPlayOutBlockActionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutBlockActionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract IntVector3 getPosition();
    public abstract void setPosition(IntVector3 value);
    public abstract int getB0();
    public abstract void setB0(int value);
    public abstract int getB1();
    public abstract void setB1(int value);
    public abstract Material getBlock();
    public abstract void setBlock(Material value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutBlockAction</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutBlockActionClass extends Template.Class<PacketPlayOutBlockActionHandle> {
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted<IntVector3>();
        public final Template.Field.Integer b0 = new Template.Field.Integer();
        public final Template.Field.Integer b1 = new Template.Field.Integer();
        public final Template.Field.Converted<Material> block = new Template.Field.Converted<Material>();

    }

}

