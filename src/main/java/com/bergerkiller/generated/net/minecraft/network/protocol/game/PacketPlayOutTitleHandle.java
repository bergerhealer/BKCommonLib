package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutTitle</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutTitle")
public abstract class PacketPlayOutTitleHandle extends PacketHandle {
    /** @See {@link PacketPlayOutTitleClass} */
    public static final PacketPlayOutTitleClass T = Template.Class.create(PacketPlayOutTitleClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutTitleHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract EnumTitleActionHandle getAction();
    public abstract void setAction(EnumTitleActionHandle value);
    public abstract ChatText getTitle();
    public abstract void setTitle(ChatText value);
    public abstract int getFadeIn();
    public abstract void setFadeIn(int value);
    public abstract int getStay();
    public abstract void setStay(int value);
    public abstract int getFadeOut();
    public abstract void setFadeOut(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutTitle</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutTitleClass extends Template.Class<PacketPlayOutTitleHandle> {
        public final Template.Field.Converted<EnumTitleActionHandle> action = new Template.Field.Converted<EnumTitleActionHandle>();
        public final Template.Field.Converted<ChatText> title = new Template.Field.Converted<ChatText>();
        public final Template.Field.Integer fadeIn = new Template.Field.Integer();
        public final Template.Field.Integer stay = new Template.Field.Integer();
        public final Template.Field.Integer fadeOut = new Template.Field.Integer();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutTitle.EnumTitleAction</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutTitle.EnumTitleAction")
    public abstract static class EnumTitleActionHandle extends Template.Handle {
        /** @See {@link EnumTitleActionClass} */
        public static final EnumTitleActionClass T = Template.Class.create(EnumTitleActionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        public static final EnumTitleActionHandle TITLE = T.TITLE.getSafe();
        public static final EnumTitleActionHandle SUBTITLE = T.SUBTITLE.getSafe();
        public static final EnumTitleActionHandle TIMES = T.TIMES.getSafe();
        public static final EnumTitleActionHandle CLEAR = T.CLEAR.getSafe();
        public static final EnumTitleActionHandle RESET = T.RESET.getSafe();
        /* ============================================================================== */

        public static EnumTitleActionHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        /**
         * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutTitle.EnumTitleAction</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class EnumTitleActionClass extends Template.Class<EnumTitleActionHandle> {
            public final Template.EnumConstant.Converted<EnumTitleActionHandle> TITLE = new Template.EnumConstant.Converted<EnumTitleActionHandle>();
            public final Template.EnumConstant.Converted<EnumTitleActionHandle> SUBTITLE = new Template.EnumConstant.Converted<EnumTitleActionHandle>();
            @Template.Optional
            public final Template.EnumConstant.Converted<EnumTitleActionHandle> ACTIONBAR = new Template.EnumConstant.Converted<EnumTitleActionHandle>();
            public final Template.EnumConstant.Converted<EnumTitleActionHandle> TIMES = new Template.EnumConstant.Converted<EnumTitleActionHandle>();
            public final Template.EnumConstant.Converted<EnumTitleActionHandle> CLEAR = new Template.EnumConstant.Converted<EnumTitleActionHandle>();
            public final Template.EnumConstant.Converted<EnumTitleActionHandle> RESET = new Template.EnumConstant.Converted<EnumTitleActionHandle>();

        }

    }

}

