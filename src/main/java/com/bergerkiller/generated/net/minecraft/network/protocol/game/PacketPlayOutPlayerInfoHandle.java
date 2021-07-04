package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.GameMode;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo")
public abstract class PacketPlayOutPlayerInfoHandle extends PacketHandle {
    /** @See {@link PacketPlayOutPlayerInfoClass} */
    public static final PacketPlayOutPlayerInfoClass T = Template.Class.create(PacketPlayOutPlayerInfoClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutPlayerInfoHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutPlayerInfoHandle createNew() {
        return T.createNew.invoke();
    }

    public abstract EnumPlayerInfoActionHandle getAction();
    public abstract void setAction(EnumPlayerInfoActionHandle value);
    public abstract List<PlayerInfoDataHandle> getPlayers();
    public abstract void setPlayers(List<PlayerInfoDataHandle> value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutPlayerInfoClass extends Template.Class<PacketPlayOutPlayerInfoHandle> {
        public final Template.Field.Converted<EnumPlayerInfoActionHandle> action = new Template.Field.Converted<EnumPlayerInfoActionHandle>();
        public final Template.Field.Converted<List<PlayerInfoDataHandle>> players = new Template.Field.Converted<List<PlayerInfoDataHandle>>();

        public final Template.StaticMethod.Converted<PacketPlayOutPlayerInfoHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutPlayerInfoHandle>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.PlayerInfoData</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.PlayerInfoData")
    public abstract static class PlayerInfoDataHandle extends Template.Handle {
        /** @See {@link PlayerInfoDataClass} */
        public static final PlayerInfoDataClass T = Template.Class.create(PlayerInfoDataClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static PlayerInfoDataHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static PlayerInfoDataHandle createNew(PacketPlayOutPlayerInfoHandle packet, GameProfileHandle profile, int ping, GameMode gameMode, ChatText listName) {
            return T.createNew.invoke(packet, profile, ping, gameMode, listName);
        }

        public abstract GameProfileHandle getProfile();
        public abstract int getPing();
        public abstract GameMode getGameMode();
        public abstract ChatText getListName();
        /**
         * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.PlayerInfoData</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class PlayerInfoDataClass extends Template.Class<PlayerInfoDataHandle> {
            public final Template.StaticMethod.Converted<PlayerInfoDataHandle> createNew = new Template.StaticMethod.Converted<PlayerInfoDataHandle>();

            public final Template.Method.Converted<GameProfileHandle> getProfile = new Template.Method.Converted<GameProfileHandle>();
            public final Template.Method<Integer> getPing = new Template.Method<Integer>();
            public final Template.Method.Converted<GameMode> getGameMode = new Template.Method.Converted<GameMode>();
            public final Template.Method.Converted<ChatText> getListName = new Template.Method.Converted<ChatText>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.EnumPlayerInfoAction</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.EnumPlayerInfoAction")
    public abstract static class EnumPlayerInfoActionHandle extends Template.Handle {
        /** @See {@link EnumPlayerInfoActionClass} */
        public static final EnumPlayerInfoActionClass T = Template.Class.create(EnumPlayerInfoActionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        public static final EnumPlayerInfoActionHandle ADD_PLAYER = T.ADD_PLAYER.getSafe();
        public static final EnumPlayerInfoActionHandle UPDATE_GAME_MODE = T.UPDATE_GAME_MODE.getSafe();
        public static final EnumPlayerInfoActionHandle UPDATE_LATENCY = T.UPDATE_LATENCY.getSafe();
        public static final EnumPlayerInfoActionHandle UPDATE_DISPLAY_NAME = T.UPDATE_DISPLAY_NAME.getSafe();
        public static final EnumPlayerInfoActionHandle REMOVE_PLAYER = T.REMOVE_PLAYER.getSafe();
        /* ============================================================================== */

        public static EnumPlayerInfoActionHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        /**
         * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.EnumPlayerInfoAction</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class EnumPlayerInfoActionClass extends Template.Class<EnumPlayerInfoActionHandle> {
            public final Template.EnumConstant.Converted<EnumPlayerInfoActionHandle> ADD_PLAYER = new Template.EnumConstant.Converted<EnumPlayerInfoActionHandle>();
            public final Template.EnumConstant.Converted<EnumPlayerInfoActionHandle> UPDATE_GAME_MODE = new Template.EnumConstant.Converted<EnumPlayerInfoActionHandle>();
            public final Template.EnumConstant.Converted<EnumPlayerInfoActionHandle> UPDATE_LATENCY = new Template.EnumConstant.Converted<EnumPlayerInfoActionHandle>();
            public final Template.EnumConstant.Converted<EnumPlayerInfoActionHandle> UPDATE_DISPLAY_NAME = new Template.EnumConstant.Converted<EnumPlayerInfoActionHandle>();
            public final Template.EnumConstant.Converted<EnumPlayerInfoActionHandle> REMOVE_PLAYER = new Template.EnumConstant.Converted<EnumPlayerInfoActionHandle>();

        }

    }

}

