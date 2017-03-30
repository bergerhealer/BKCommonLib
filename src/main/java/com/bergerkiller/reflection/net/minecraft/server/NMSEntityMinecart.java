package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.MethodAccessor;

public class NMSEntityMinecart {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartAbstract");

    public static DataWatcher.Key<Integer> DATA_SHAKING_FACTOR = T.getDataWatcherKey("a");
    public static DataWatcher.Key<Integer> DATA_SHAKING_DIRECTION = T.getDataWatcherKey("b");
    public static DataWatcher.Key<Float> DATA_SHAKING_DAMAGE = T.getDataWatcherKey("c");
    public static DataWatcher.Key<Integer> DATA_BLOCK_TYPE = T.getDataWatcherKey("d");
    public static DataWatcher.Key<Integer> DATA_BLOCK_OFFSET = T.getDataWatcherKey("e");
    public static DataWatcher.Key<Boolean> DATA_BLOCK_VISIBLE = T.getDataWatcherKey("f");

    public static final MethodAccessor<Void> activate = T.selectMethod("public void a(int x, int y, int z, boolean active)");

    public static class Furnace {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartFurnace");

        public static DataWatcher.Key<Boolean> DATA_SMOKING = T.getDataWatcherKey("c");

        public static final FieldAccessor<Integer> fuel = T.nextField("private int d");
        public static final FieldAccessor<Double> pushForceX = T.nextFieldSignature("public double a");
        public static final FieldAccessor<Double> pushForceZ = T.nextFieldSignature("public double b");
    }

    public static class MobSpawner {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartMobSpawner");
        public static final FieldAccessor<Object> mobSpawner = T.selectField("private final MobSpawnerAbstract a");
    }

    public static class Hopper {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartHopper");
        public static final FieldAccessor<Integer> suckingCooldown = T.selectField("private int b");
    }

    public static class TNT {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartTNT");
        public static final FieldAccessor<Integer> fuse = T.selectField("private int a");
        public static final MethodAccessor<Void> explode = T.selectMethod("protected void c(double damage)");
        public static final MethodAccessor<Void> prime = T.selectMethod("public void j()");
    }

    public static class CommandBlock {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartCommandBlock");

        public static final DataWatcher.Key<String> DATA_COMMAND = T.getDataWatcherKey("COMMAND");
        public static final DataWatcher.Key<Object> DATA_PREVIOUS_OUTPUT = T.getDataWatcherKey("b");
    }
}
