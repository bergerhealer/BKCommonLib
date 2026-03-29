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
    /** @see ClientboundPlayerInfoUpdatePacketClass */
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

    public abstract Set<ActionHandle> getActions();
    public abstract void setAction(ActionHandle action);
    public abstract void setActions(Set<ActionHandle> actions);
    public abstract List<EntryHandle> getPlayers();
    public abstract void setPlayers(List<EntryHandle> value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundPlayerInfoUpdatePacketClass extends Template.Class<ClientboundPlayerInfoUpdatePacketHandle> {
        public final Template.Field.Converted<List<EntryHandle>> players = new Template.Field.Converted<List<EntryHandle>>();

        public final Template.StaticMethod.Converted<ClientboundPlayerInfoUpdatePacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundPlayerInfoUpdatePacketHandle>();
        public final Template.StaticMethod<Boolean> isPlayerInfoRemovePacket = new Template.StaticMethod<Boolean>();

        public final Template.Method.Converted<Set<ActionHandle>> getActions = new Template.Method.Converted<Set<ActionHandle>>();
        public final Template.Method.Converted<Void> setAction = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> setActions = new Template.Method.Converted<Void>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry")
    public abstract static class EntryHandle extends Template.Handle {
        /** @see EntryClass */
        public static final EntryClass T = Template.Class.create(EntryClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static EntryHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static EntryHandle createNew(ClientboundPlayerInfoUpdatePacketHandle packet, GameProfileHandle profile, int ping, GameMode gameMode, ChatText listName, boolean listed) {
            return T.createNew.invokeVA(packet, profile, ping, gameMode, listName, listed);
        }

        public abstract GameProfileHandle getProfile();
        public abstract int getPing();
        public abstract GameMode getGameMode();
        public abstract ChatText getListName();
        public static com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacketHandle.EntryHandle createNew(ClientboundPlayerInfoUpdatePacketHandle packet, GameProfileHandle profile, int ping, org.bukkit.GameMode gameMode, ChatText listName) {
            return createNew(packet, profile, ping, gameMode, listName, true); // Listed like olden times
        }
        /**
         * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class EntryClass extends Template.Class<EntryHandle> {
            public final Template.StaticMethod.Converted<EntryHandle> createNew = new Template.StaticMethod.Converted<EntryHandle>();

            public final Template.Method.Converted<GameProfileHandle> getProfile = new Template.Method.Converted<GameProfileHandle>();
            public final Template.Method<Integer> getPing = new Template.Method<Integer>();
            public final Template.Method.Converted<GameMode> getGameMode = new Template.Method.Converted<GameMode>();
            public final Template.Method.Converted<ChatText> getListName = new Template.Method.Converted<ChatText>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action")
    public abstract static class ActionHandle extends Template.Handle {
        /** @see ActionClass */
        public static final ActionClass T = Template.Class.create(ActionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        public static final ActionHandle ADD_PLAYER = T.ADD_PLAYER.getSafe();
        public static final ActionHandle UPDATE_GAME_MODE = T.UPDATE_GAME_MODE.getSafe();
        public static final ActionHandle UPDATE_LATENCY = T.UPDATE_LATENCY.getSafe();
        public static final ActionHandle UPDATE_DISPLAY_NAME = T.UPDATE_DISPLAY_NAME.getSafe();
        /* ============================================================================== */

        public static ActionHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        /**
         * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class ActionClass extends Template.Class<ActionHandle> {
            public final Template.EnumConstant.Converted<ActionHandle> ADD_PLAYER = new Template.EnumConstant.Converted<ActionHandle>();
            public final Template.EnumConstant.Converted<ActionHandle> UPDATE_GAME_MODE = new Template.EnumConstant.Converted<ActionHandle>();
            public final Template.EnumConstant.Converted<ActionHandle> UPDATE_LATENCY = new Template.EnumConstant.Converted<ActionHandle>();
            public final Template.EnumConstant.Converted<ActionHandle> UPDATE_DISPLAY_NAME = new Template.EnumConstant.Converted<ActionHandle>();

        }

    }

}

