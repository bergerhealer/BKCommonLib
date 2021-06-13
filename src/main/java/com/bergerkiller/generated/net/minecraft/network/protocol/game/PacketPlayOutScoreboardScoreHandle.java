package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ScoreboardAction;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore")
public abstract class PacketPlayOutScoreboardScoreHandle extends PacketHandle {
    /** @See {@link PacketPlayOutScoreboardScoreClass} */
    public static final PacketPlayOutScoreboardScoreClass T = Template.Class.create(PacketPlayOutScoreboardScoreClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutScoreboardScoreHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract String getName();
    public abstract void setName(String value);
    public abstract String getObjName();
    public abstract void setObjName(String value);
    public abstract int getValue();
    public abstract void setValue(int value);
    public abstract ScoreboardAction getAction();
    public abstract void setAction(ScoreboardAction value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutScoreboardScoreClass extends Template.Class<PacketPlayOutScoreboardScoreHandle> {
        public final Template.Field<String> name = new Template.Field<String>();
        public final Template.Field<String> objName = new Template.Field<String>();
        public final Template.Field.Integer value = new Template.Field.Integer();
        public final Template.Field.Converted<ScoreboardAction> action = new Template.Field.Converted<ScoreboardAction>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore.EnumScoreboardAction</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore.EnumScoreboardAction")
    public abstract static class EnumScoreboardActionHandle extends Template.Handle {
        /** @See {@link EnumScoreboardActionClass} */
        public static final EnumScoreboardActionClass T = Template.Class.create(EnumScoreboardActionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        public static final EnumScoreboardActionHandle CHANGE = T.CHANGE.getSafe();
        public static final EnumScoreboardActionHandle REMOVE = T.REMOVE.getSafe();
        /* ============================================================================== */

        public static EnumScoreboardActionHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        /**
         * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore.EnumScoreboardAction</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class EnumScoreboardActionClass extends Template.Class<EnumScoreboardActionHandle> {
            public final Template.EnumConstant.Converted<EnumScoreboardActionHandle> CHANGE = new Template.EnumConstant.Converted<EnumScoreboardActionHandle>();
            public final Template.EnumConstant.Converted<EnumScoreboardActionHandle> REMOVE = new Template.EnumConstant.Converted<EnumScoreboardActionHandle>();

        }

    }

}

