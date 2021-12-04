package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.EntityMinecartAbstractHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.EntityMinecartCommandBlockHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.EntityMinecartFurnaceHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.EntityMinecartHopperHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.EntityMinecartMobSpawnerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.EntityMinecartRideableHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.EntityMinecartTNTHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

@Deprecated
public class NMSEntityMinecart {
    public static final ClassTemplate<?> T = ClassTemplate.create(EntityMinecartAbstractHandle.T.getType());

    public static DataWatcher.Key<Integer> DATA_SHAKING_FACTOR = EntityMinecartAbstractHandle.DATA_SHAKING_FACTOR;
    public static DataWatcher.Key<Integer> DATA_SHAKING_DIRECTION = EntityMinecartAbstractHandle.DATA_SHAKING_DIRECTION;
    public static DataWatcher.Key<Float> DATA_SHAKING_DAMAGE = EntityMinecartAbstractHandle.DATA_SHAKING_DAMAGE;
    public static DataWatcher.Key<Integer> DATA_BLOCK_TYPE = EntityMinecartAbstractHandle.DATA_BLOCK_TYPE;
    public static DataWatcher.Key<Integer> DATA_BLOCK_OFFSET = EntityMinecartAbstractHandle.DATA_BLOCK_OFFSET;
    public static DataWatcher.Key<Boolean> DATA_BLOCK_VISIBLE = EntityMinecartAbstractHandle.DATA_BLOCK_VISIBLE;

    public static final MethodAccessor<Void> activate = EntityMinecartAbstractHandle.T.activate.toMethodAccessor();

    public static class Rideable {
        public static final ClassTemplate<?> T = ClassTemplate.create(EntityMinecartRideableHandle.T.getType());
    }

    public static class Furnace {
        public static final ClassTemplate<?> T = ClassTemplate.create(EntityMinecartFurnaceHandle.T.getType());

        public static DataWatcher.Key<Boolean> DATA_SMOKING = EntityMinecartFurnaceHandle.DATA_SMOKING;

        public static final FieldAccessor<Integer> fuel = EntityMinecartFurnaceHandle.T.fuel.toFieldAccessor();
        public static final FieldAccessor<Double> pushForceX = EntityMinecartFurnaceHandle.T.pushForceX.toFieldAccessor();
        public static final FieldAccessor<Double> pushForceZ = EntityMinecartFurnaceHandle.T.pushForceZ.toFieldAccessor();
    }

    public static class MobSpawner {
        public static final ClassTemplate<?> T = ClassTemplate.create(EntityMinecartMobSpawnerHandle.T.getType());
        public static final FieldAccessor<Object> mobSpawner = EntityMinecartMobSpawnerHandle.T.mobSpawner.raw.toFieldAccessor();
    }

    public static class Hopper {
        public static final ClassTemplate<?> T = ClassTemplate.create(EntityMinecartHopperHandle.T.getType());
        public static final FieldAccessor<Integer> suckingCooldown = EntityMinecartHopperHandle.T.suckingCooldown.toFieldAccessor();
    }

    public static class TNT {
        public static final ClassTemplate<?> T = ClassTemplate.create(EntityMinecartTNTHandle.T.getType());
        public static final FieldAccessor<Integer> fuse = EntityMinecartTNTHandle.T.fuse.toFieldAccessor();
        public static final MethodAccessor<Void> explode = EntityMinecartTNTHandle.T.explode.toMethodAccessor();
        public static final MethodAccessor<Void> prime = EntityMinecartTNTHandle.T.prime.toMethodAccessor();
    }

    public static class CommandBlock {
        public static final ClassTemplate<?> T = ClassTemplate.create(EntityMinecartCommandBlockHandle.T.getType());

        public static final DataWatcher.Key<String> DATA_COMMAND = EntityMinecartCommandBlockHandle.DATA_COMMAND;
        public static final DataWatcher.Key<ChatText> DATA_PREVIOUS_OUTPUT = EntityMinecartCommandBlockHandle.DATA_PREVIOUS_OUTPUT;
    }
}
