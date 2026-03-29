package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.AbstractMinecartHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.MinecartCommandBlockHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.MinecartFurnaceHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.MinecartHopperHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.MinecartSpawnerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.MinecartHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.MinecartTNTHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

@Deprecated
public class NMSEntityMinecart {
    public static final ClassTemplate<?> T = ClassTemplate.create(AbstractMinecartHandle.T.getType());

    public static DataWatcher.Key<Integer> DATA_SHAKING_FACTOR = AbstractMinecartHandle.DATA_SHAKING_FACTOR;
    public static DataWatcher.Key<Integer> DATA_SHAKING_DIRECTION = AbstractMinecartHandle.DATA_SHAKING_DIRECTION;
    public static DataWatcher.Key<Float> DATA_SHAKING_DAMAGE = AbstractMinecartHandle.DATA_SHAKING_DAMAGE;
    public static DataWatcher.Key<Integer> DATA_BLOCK_OFFSET = AbstractMinecartHandle.DATA_BLOCK_OFFSET;

    public static class Rideable {
        public static final ClassTemplate<?> T = ClassTemplate.create(MinecartHandle.T.getType());
    }

    public static class Furnace {
        public static final ClassTemplate<?> T = ClassTemplate.create(MinecartFurnaceHandle.T.getType());

        public static DataWatcher.Key<Boolean> DATA_SMOKING = MinecartFurnaceHandle.DATA_SMOKING;

        public static final FieldAccessor<Integer> fuel = MinecartFurnaceHandle.T.fuel.toFieldAccessor();
    }

    public static class MobSpawner {
        public static final ClassTemplate<?> T = ClassTemplate.create(MinecartSpawnerHandle.T.getType());
        public static final FieldAccessor<Object> mobSpawner = MinecartSpawnerHandle.T.mobSpawner.raw.toFieldAccessor();
    }

    public static class Hopper {
        public static final ClassTemplate<?> T = ClassTemplate.create(MinecartHopperHandle.T.getType());
    }

    public static class TNT {
        public static final ClassTemplate<?> T = ClassTemplate.create(MinecartTNTHandle.T.getType());
        public static final FieldAccessor<Integer> fuse = MinecartTNTHandle.T.fuse.toFieldAccessor();
        public static final MethodAccessor<Void> explode = MinecartTNTHandle.T.explode.toMethodAccessor();
        public static final MethodAccessor<Void> prime = MinecartTNTHandle.T.prime.toMethodAccessor();
    }

    public static class CommandBlock {
        public static final ClassTemplate<?> T = ClassTemplate.create(MinecartCommandBlockHandle.T.getType());

        public static final DataWatcher.Key<String> DATA_COMMAND = MinecartCommandBlockHandle.DATA_COMMAND;
        public static final DataWatcher.Key<ChatText> DATA_PREVIOUS_OUTPUT = MinecartCommandBlockHandle.DATA_PREVIOUS_OUTPUT;
    }
}
