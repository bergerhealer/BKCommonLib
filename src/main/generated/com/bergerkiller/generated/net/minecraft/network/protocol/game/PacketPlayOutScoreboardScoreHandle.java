package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore")
public abstract class PacketPlayOutScoreboardScoreHandle extends PacketHandle {
    /** @see PacketPlayOutScoreboardScoreClass */
    public static final PacketPlayOutScoreboardScoreClass T = Template.Class.create(PacketPlayOutScoreboardScoreClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutScoreboardScoreHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutScoreboardScoreHandle createNew(String name, String objectiveName, int score) {
        return T.createNew.invoke(name, objectiveName, score);
    }

    public abstract String getName();
    public abstract void setName(String value);
    public abstract String getObjName();
    public abstract void setObjName(String value);
    public abstract int getValue();
    public abstract void setValue(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutScoreboardScoreClass extends Template.Class<PacketPlayOutScoreboardScoreHandle> {
        public final Template.Field<String> name = new Template.Field<String>();
        public final Template.Field<String> objName = new Template.Field<String>();
        public final Template.Field.Integer value = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<PacketPlayOutScoreboardScoreHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutScoreboardScoreHandle>();

    }

}

