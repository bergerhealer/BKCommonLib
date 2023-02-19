package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.GameMode;
import java.util.List;
import java.util.Set;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket")
public abstract class ClientboundPlayerInfoUpdatePacketHandle extends PacketHandle {
    /** @See {@link ClientboundPlayerInfoUpdatePacketClass} */
    public static final ClientboundPlayerInfoUpdatePacketClass T = Template.Class.create(ClientboundPlayerInfoUpdatePacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundPlayerInfoUpdatePacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundPlayerInfoUpdatePacketHandle createNew() {
        return T.createNew.invoke();
    }

    public static boolean isPlayerInfoRemovePacket(Object nmsPacketHandle) {
        return T.isPlayerInfoRemovePacket.invoker.invoke(null,nmsPacketHandle);
    }

    public abstract Set<EnumPlayerInfoActionHandle> getActions();
    public abstract void setAction(EnumPlayerInfoActionHandle action);
    public abstract void setActions(Set<EnumPlayerInfoActionHandle> actions);
    public abstract List<PlayerInfoDataHandle> getPlayers();
    public abstract void setPlayers(List<PlayerInfoDataHandle> value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundPlayerInfoUpdatePacketClass extends Template.Class<ClientboundPlayerInfoUpdatePacketHandle> {
        public final Template.Field.Converted<List<PlayerInfoDataHandle>> players = new Template.Field.Converted<List<PlayerInfoDataHandle>>();

        public final Template.StaticMethod.Converted<ClientboundPlayerInfoUpdatePacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundPlayerInfoUpdatePacketHandle>();
        public final Template.StaticMethod<Boolean> isPlayerInfoRemovePacket = new Template.StaticMethod<Boolean>();

        public final Template.Method.Converted<Set<EnumPlayerInfoActionHandle>> getActions = new Template.Method.Converted<Set<EnumPlayerInfoActionHandle>>();
        public final Template.Method.Converted<Void> setAction = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> setActions = new Template.Method.Converted<Void>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.PlayerInfoData</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.PlayerInfoData")
    public abstract static class PlayerInfoDataHandle extends Template.Handle {
        /** @See {@link PlayerInfoDataClass} */
        public static final PlayerInfoDataClass T = Template.Class.create(PlayerInfoDataClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static PlayerInfoDataHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static PlayerInfoDataHandle createNew(ClientboundPlayerInfoUpdatePacketHandle packet, GameProfileHandle profile, int ping, GameMode gameMode, ChatText listName, boolean listed) {
            return T.createNew.invokeVA(packet, profile, ping, gameMode, listName, listed);
        }

        public abstract GameProfileHandle getProfile();
        public abstract int getPing();
        public abstract GameMode getGameMode();
        public abstract ChatText getListName();

        public static PlayerInfoDataHandle createNew(ClientboundPlayerInfoUpdatePacketHandle packet, GameProfileHandle profile, int ping, org.bukkit.GameMode gameMode, ChatText listName) {
            return createNew(packet, profile, ping, gameMode, listName, true); // Listed like olden times
        }
        /**
         * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.PlayerInfoData</b>.
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
     * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.EnumPlayerInfoAction</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.EnumPlayerInfoAction")
    public abstract static class EnumPlayerInfoActionHandle extends Template.Handle {
        /** @See {@link EnumPlayerInfoActionClass} */
        public static final EnumPlayerInfoActionClass T = Template.Class.create(EnumPlayerInfoActionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        public static final EnumPlayerInfoActionHandle ADD_PLAYER = T.ADD_PLAYER.getSafe();
        public static final EnumPlayerInfoActionHandle UPDATE_GAME_MODE = T.UPDATE_GAME_MODE.getSafe();
        public static final EnumPlayerInfoActionHandle UPDATE_LATENCY = T.UPDATE_LATENCY.getSafe();
        public static final EnumPlayerInfoActionHandle UPDATE_DISPLAY_NAME = T.UPDATE_DISPLAY_NAME.getSafe();
        /* ============================================================================== */

        public static EnumPlayerInfoActionHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        /**
         * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.EnumPlayerInfoAction</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class EnumPlayerInfoActionClass extends Template.Class<EnumPlayerInfoActionHandle> {
            public final Template.EnumConstant.Converted<EnumPlayerInfoActionHandle> ADD_PLAYER = new Template.EnumConstant.Converted<EnumPlayerInfoActionHandle>();
            public final Template.EnumConstant.Converted<EnumPlayerInfoActionHandle> UPDATE_GAME_MODE = new Template.EnumConstant.Converted<EnumPlayerInfoActionHandle>();
            public final Template.EnumConstant.Converted<EnumPlayerInfoActionHandle> UPDATE_LATENCY = new Template.EnumConstant.Converted<EnumPlayerInfoActionHandle>();
            public final Template.EnumConstant.Converted<EnumPlayerInfoActionHandle> UPDATE_DISPLAY_NAME = new Template.EnumConstant.Converted<EnumPlayerInfoActionHandle>();

        }

    }

}

