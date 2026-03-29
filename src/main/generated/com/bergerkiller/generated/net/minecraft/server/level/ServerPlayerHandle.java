package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.server.network.ServerGamePacketListenerImplHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.PlayerHandle;
import org.bukkit.World;
import org.bukkit.inventory.InventoryView;
import java.util.Collection;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.level.ServerPlayer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.level.ServerPlayer")
public abstract class ServerPlayerHandle extends PlayerHandle {
    /** @see ServerPlayerClass */
    public static final ServerPlayerClass T = Template.Class.create(ServerPlayerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerPlayerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean hasDisconnected();
    public abstract RespawnConfigHandle getRespawnConfig();
    public abstract void setRespawnConfigSilent(RespawnConfigHandle respawnConfig);
    public abstract int getPing();
    public abstract boolean hasSeenCredits();
    public abstract void setHasSeenCredits(boolean hasSeen);
    public abstract void sendMessage(ChatText ichatbasecomponent);
    public abstract int getCurrentWindowId();
    public abstract InventoryView openAnvilWindow(ChatText titleText);
    public abstract void openSignEditWindow(IntVector3 signPosition, boolean isFrontText);
    @Deprecated
    public void setSpawnForced(boolean forced) {
    }

    public void closeSignEditWindow() {
        openSignEditWindow(IntVector3.of(Integer.MAX_VALUE, 0, Integer.MAX_VALUE));
    }

    public void openSignEditWindow(IntVector3 signPosition) {
        openSignEditWindow(signPosition, true);
    }

    public static ServerPlayerHandle fromBukkit(org.bukkit.entity.Player player) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toEntityHandle(player));
    }
    public abstract ServerGamePacketListenerImplHandle getPlayerConnection();
    public abstract void setPlayerConnection(ServerGamePacketListenerImplHandle value);
    @Template.Readonly
    public abstract boolean isViewingCredits();
    /**
     * Stores class members for <b>net.minecraft.server.level.ServerPlayer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerPlayerClass extends Template.Class<ServerPlayerHandle> {
        public final Template.Field.Converted<ServerGamePacketListenerImplHandle> playerConnection = new Template.Field.Converted<ServerGamePacketListenerImplHandle>();
        @Template.Readonly
        public final Template.Field.Boolean viewingCredits = new Template.Field.Boolean();

        public final Template.Method<Boolean> hasDisconnected = new Template.Method<Boolean>();
        public final Template.Method.Converted<RespawnConfigHandle> getRespawnConfig = new Template.Method.Converted<RespawnConfigHandle>();
        public final Template.Method.Converted<Void> setRespawnConfigSilent = new Template.Method.Converted<Void>();
        public final Template.Method<Integer> getPing = new Template.Method<Integer>();
        public final Template.Method<Boolean> hasSeenCredits = new Template.Method<Boolean>();
        public final Template.Method<Void> setHasSeenCredits = new Template.Method<Void>();
        public final Template.Method.Converted<Void> sendMessage = new Template.Method.Converted<Void>();
        @Template.Optional
        public final Template.Method<Collection<Integer>> getRemoveQueue = new Template.Method<Collection<Integer>>();
        public final Template.Method<Integer> getCurrentWindowId = new Template.Method<Integer>();
        public final Template.Method<InventoryView> openAnvilWindow = new Template.Method<InventoryView>();
        public final Template.Method.Converted<Void> openSignEditWindow = new Template.Method.Converted<Void>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.server.level.ServerPlayer.RespawnConfig</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.server.level.ServerPlayer.RespawnConfig")
    public abstract static class RespawnConfigHandle extends Template.Handle {
        /** @see RespawnConfigClass */
        public static final RespawnConfigClass T = Template.Class.create(RespawnConfigClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static RespawnConfigHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static RespawnConfigHandle of(ResourceKey<World> dimension, String worldName, IntVector3 position, float yaw, float pitch, boolean forced) {
            return T.of.invokeVA(dimension, worldName, position, yaw, pitch, forced);
        }

        public static RespawnConfigHandle codecFromNBT(CommonTagCompound nbt) {
            return T.codecFromNBT.invoke(nbt);
        }

        public static void codecToNBT(RespawnConfigHandle respawnConfig, CommonTagCompound nbt) {
            T.codecToNBT.invoke(respawnConfig, nbt);
        }

        public abstract ResourceKey<World> dimension();
        public abstract IntVector3 position();
        public abstract String worldName();
        public abstract float yaw();
        public abstract float pitch();
        public abstract boolean forced();
        @Deprecated
        public static ServerPlayerHandle.RespawnConfigHandle of(com.bergerkiller.bukkit.common.resources.ResourceKey<org.bukkit.World> dimension, String worldName, IntVector3 position, float yaw, boolean forced) {
            return of(dimension, worldName, position, yaw, 0.0f, forced);
        }

        public org.bukkit.World world() {
            return com.bergerkiller.bukkit.common.utils.WorldUtil.getWorldByDimensionKey(this.dimension());
        }

        public static ServerPlayerHandle.RespawnConfigHandle of(org.bukkit.World world, IntVector3 position, float angle, boolean forced) {
            return of(com.bergerkiller.bukkit.common.utils.WorldUtil.getDimensionKey(world), world.getName(), position, angle, forced);
        }
        /**
         * Stores class members for <b>net.minecraft.server.level.ServerPlayer.RespawnConfig</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class RespawnConfigClass extends Template.Class<RespawnConfigHandle> {
            public final Template.StaticMethod.Converted<RespawnConfigHandle> of = new Template.StaticMethod.Converted<RespawnConfigHandle>();
            public final Template.StaticMethod.Converted<RespawnConfigHandle> codecFromNBT = new Template.StaticMethod.Converted<RespawnConfigHandle>();
            public final Template.StaticMethod.Converted<Void> codecToNBT = new Template.StaticMethod.Converted<Void>();

            public final Template.Method.Converted<ResourceKey<World>> dimension = new Template.Method.Converted<ResourceKey<World>>();
            public final Template.Method.Converted<IntVector3> position = new Template.Method.Converted<IntVector3>();
            public final Template.Method<String> worldName = new Template.Method<String>();
            public final Template.Method<Float> yaw = new Template.Method<Float>();
            public final Template.Method<Float> pitch = new Template.Method<Float>();
            public final Template.Method<Boolean> forced = new Template.Method<Boolean>();

        }

    }

}

