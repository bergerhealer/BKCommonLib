package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import org.bukkit.GameMode;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutPlayerInfo</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutPlayerInfoHandle extends PacketHandle {
    /** @See {@link PacketPlayOutPlayerInfoClass} */
    public static final PacketPlayOutPlayerInfoClass T = new PacketPlayOutPlayerInfoClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutPlayerInfoHandle.class, "net.minecraft.server.PacketPlayOutPlayerInfo");

    /* ============================================================================== */

    public static PacketPlayOutPlayerInfoHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutPlayerInfoHandle handle = new PacketPlayOutPlayerInfoHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final PacketPlayOutPlayerInfoHandle createNew() {
        return T.constr.newInstance();
    }

    /* ============================================================================== */

    public EnumPlayerInfoActionHandle getAction() {
        return T.action.get(instance);
    }

    public void setAction(EnumPlayerInfoActionHandle value) {
        T.action.set(instance, value);
    }

    public List<PlayerInfoDataHandle> getPlayers() {
        return T.players.get(instance);
    }

    public void setPlayers(List<PlayerInfoDataHandle> value) {
        T.players.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutPlayerInfo</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutPlayerInfoClass extends Template.Class<PacketPlayOutPlayerInfoHandle> {
        public final Template.Constructor.Converted<PacketPlayOutPlayerInfoHandle> constr = new Template.Constructor.Converted<PacketPlayOutPlayerInfoHandle>();

        public final Template.Field.Converted<EnumPlayerInfoActionHandle> action = new Template.Field.Converted<EnumPlayerInfoActionHandle>();
        public final Template.Field.Converted<List<PlayerInfoDataHandle>> players = new Template.Field.Converted<List<PlayerInfoDataHandle>>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutPlayerInfo.PlayerInfoData</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    public static class PlayerInfoDataHandle extends Template.Handle {
        /** @See {@link PlayerInfoDataClass} */
        public static final PlayerInfoDataClass T = new PlayerInfoDataClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(PlayerInfoDataHandle.class, "net.minecraft.server.PacketPlayOutPlayerInfo.PlayerInfoData");

        /* ============================================================================== */

        public static PlayerInfoDataHandle createHandle(Object handleInstance) {
            if (handleInstance == null) return null;
            PlayerInfoDataHandle handle = new PlayerInfoDataHandle();
            handle.instance = handleInstance;
            return handle;
        }

        public static final PlayerInfoDataHandle createNew(PacketPlayOutPlayerInfoHandle packet, GameProfileHandle profile, int ping, GameMode gameMode, ChatText listName) {
            return T.constr_packet_profile_ping_gameMode_listName.newInstance(packet, profile, ping, gameMode, listName);
        }

        /* ============================================================================== */

        public GameProfileHandle getProfile() {
            return T.getProfile.invoke(instance);
        }

        public int getPing() {
            return T.getPing.invoke(instance);
        }

        public GameMode getGameMode() {
            return T.getGameMode.invoke(instance);
        }

        public ChatText getListName() {
            return T.getListName.invoke(instance);
        }

        /**
         * Stores class members for <b>net.minecraft.server.PacketPlayOutPlayerInfo.PlayerInfoData</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class PlayerInfoDataClass extends Template.Class<PlayerInfoDataHandle> {
            public final Template.Constructor.Converted<PlayerInfoDataHandle> constr_packet_profile_ping_gameMode_listName = new Template.Constructor.Converted<PlayerInfoDataHandle>();

            public final Template.Method.Converted<GameProfileHandle> getProfile = new Template.Method.Converted<GameProfileHandle>();
            public final Template.Method<Integer> getPing = new Template.Method<Integer>();
            public final Template.Method.Converted<GameMode> getGameMode = new Template.Method.Converted<GameMode>();
            public final Template.Method.Converted<ChatText> getListName = new Template.Method.Converted<ChatText>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutPlayerInfo.EnumPlayerInfoAction</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    public static class EnumPlayerInfoActionHandle extends Template.Handle {
        /** @See {@link EnumPlayerInfoActionClass} */
        public static final EnumPlayerInfoActionClass T = new EnumPlayerInfoActionClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(EnumPlayerInfoActionHandle.class, "net.minecraft.server.PacketPlayOutPlayerInfo.EnumPlayerInfoAction");

        public static final EnumPlayerInfoActionHandle ADD_PLAYER = T.ADD_PLAYER.getSafe();
        public static final EnumPlayerInfoActionHandle UPDATE_GAME_MODE = T.UPDATE_GAME_MODE.getSafe();
        public static final EnumPlayerInfoActionHandle UPDATE_LATENCY = T.UPDATE_LATENCY.getSafe();
        public static final EnumPlayerInfoActionHandle UPDATE_DISPLAY_NAME = T.UPDATE_DISPLAY_NAME.getSafe();
        public static final EnumPlayerInfoActionHandle REMOVE_PLAYER = T.REMOVE_PLAYER.getSafe();
        /* ============================================================================== */

        public static EnumPlayerInfoActionHandle createHandle(Object handleInstance) {
            if (handleInstance == null) return null;
            EnumPlayerInfoActionHandle handle = new EnumPlayerInfoActionHandle();
            handle.instance = handleInstance;
            return handle;
        }

        /* ============================================================================== */

        /**
         * Stores class members for <b>net.minecraft.server.PacketPlayOutPlayerInfo.EnumPlayerInfoAction</b>.
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

