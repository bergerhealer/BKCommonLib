package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.PlayerChunkHandle;
import com.bergerkiller.generated.net.minecraft.server.PlayerChunkMapHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.SafeDirectMethod;

import java.util.List;

/**
 * <b>Deprecated: </b>Use {@link PlayerChunkMapHandle} instead
 */
public class NMSPlayerChunkMap {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("PlayerChunkMap");

    public static final FieldAccessor<List<?>> managedPlayers = CommonUtil.unsafeCast(PlayerChunkMapHandle.T.managedPlayers.raw.toFieldAccessor());

    public static final FieldAccessor<Integer> radius = PlayerChunkMapHandle.T.radius.toFieldAccessor();

    // markForUpdate(net.minecraft.server.PlayerChunk chunk)
    public static final MethodAccessor<Void> markForUpdate = new SafeDirectMethod<Void>() {
        @Override
        public Void invoke(Object instance, Object... args) {
            PlayerChunkMapHandle.createHandle(instance).markForUpdate(PlayerChunkHandle.createHandle(args[0]));
            return null;
        }
    };

    public static final MethodAccessor<Boolean> shouldUnload = PlayerChunkMapHandle.T.shouldUnload.toMethodAccessor();

    // getChunk(int x, int z)
    public static final MethodAccessor<Object> getChunk = new SafeDirectMethod<Object>() {
        @Override
        public Object invoke(Object instance, Object... args) {
            if (PlayerChunkMapHandle.T.getChunk_1_10_2.isAvailable()) {
                return PlayerChunkMapHandle.T.getChunk_1_10_2.raw.invokeVA(instance, args);
            } else {
                return PlayerChunkMapHandle.T.getChunk_1_8_8.raw.invoke(instance, args[0], args[1], false);
            }
        }
    };

    public static void flagBlockDirty(Object playerChunkMap, int x, int y, int z) {
        PlayerChunkMapHandle.createHandle(playerChunkMap).flagDirty(x, y, z);
    }
}
