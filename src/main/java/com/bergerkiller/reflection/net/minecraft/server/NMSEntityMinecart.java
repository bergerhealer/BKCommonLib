package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.server.EntityMinecartAbstractHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityMinecartCommandBlockHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityMinecartFurnaceHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityMinecartHopperHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityMinecartTNTHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

public class NMSEntityMinecart {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartAbstract");

    public static DataWatcher.Key<Integer> DATA_SHAKING_FACTOR = EntityMinecartAbstractHandle.DATA_SHAKING_FACTOR;
    public static DataWatcher.Key<Integer> DATA_SHAKING_DIRECTION = EntityMinecartAbstractHandle.DATA_SHAKING_DIRECTION;
    public static DataWatcher.Key<Float> DATA_SHAKING_DAMAGE = EntityMinecartAbstractHandle.DATA_SHAKING_DAMAGE;
    public static DataWatcher.Key<Integer> DATA_BLOCK_TYPE = EntityMinecartAbstractHandle.DATA_BLOCK_TYPE;
    public static DataWatcher.Key<Integer> DATA_BLOCK_OFFSET = EntityMinecartAbstractHandle.DATA_BLOCK_OFFSET;
    public static DataWatcher.Key<Boolean> DATA_BLOCK_VISIBLE = EntityMinecartAbstractHandle.DATA_BLOCK_VISIBLE;

    public static final MethodAccessor<Void> activate = EntityMinecartAbstractHandle.T.activate.toMethodAccessor();

    public static class Rideable {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartRideable");
    }

    public static class Furnace {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartFurnace");

        public static DataWatcher.Key<Boolean> DATA_SMOKING = EntityMinecartFurnaceHandle.DATA_SMOKING;

        public static final FieldAccessor<Integer> fuel = EntityMinecartFurnaceHandle.T.fuel.toFieldAccessor();
        public static final FieldAccessor<Double> pushForceX = EntityMinecartFurnaceHandle.T.pushForceX.toFieldAccessor();
        public static final FieldAccessor<Double> pushForceZ = EntityMinecartFurnaceHandle.T.pushForceZ.toFieldAccessor();
    }

    public static class MobSpawner {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartMobSpawner");
        public static final FieldAccessor<Object> mobSpawner = T.selectField("private final MobSpawnerAbstract a");
    }

    public static class Hopper {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartHopper");
        public static final FieldAccessor<Integer> suckingCooldown = EntityMinecartHopperHandle.T.suckingCooldown.toFieldAccessor();
    }

    public static class TNT {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartTNT");
        public static final FieldAccessor<Integer> fuse = EntityMinecartTNTHandle.T.fuse.toFieldAccessor();
        public static final MethodAccessor<Void> explode = EntityMinecartTNTHandle.T.explode.toMethodAccessor();
        public static final MethodAccessor<Void> prime = EntityMinecartTNTHandle.T.prime.toMethodAccessor();
    }

    public static class CommandBlock {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartCommandBlock");

        public static final DataWatcher.Key<String> DATA_COMMAND = EntityMinecartCommandBlockHandle.DATA_COMMAND;
        public static final DataWatcher.Key<ChatText> DATA_PREVIOUS_OUTPUT = EntityMinecartCommandBlockHandle.DATA_PREVIOUS_OUTPUT;
    }
}
