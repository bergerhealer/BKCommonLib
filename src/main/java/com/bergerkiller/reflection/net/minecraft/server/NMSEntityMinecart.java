package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

public class NMSEntityMinecart {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartAbstract");

    public static DataWatcher.Key<Integer> DATA_SHAKING_FACTOR = DataWatcher.Key.fromStaticField(T, "a");
    public static DataWatcher.Key<Integer> DATA_SHAKING_DIRECTION = DataWatcher.Key.fromStaticField(T, "b");
    public static DataWatcher.Key<Float> DATA_SHAKING_DAMAGE = DataWatcher.Key.fromStaticField(T, "c");
    public static DataWatcher.Key<Integer> DATA_BLOCK_TYPE = DataWatcher.Key.fromStaticField(T, "d");
    public static DataWatcher.Key<Integer> DATA_BLOCK_OFFSET = DataWatcher.Key.fromStaticField(T, "e");
    public static DataWatcher.Key<Boolean> DATA_BLOCK_VISIBLE = DataWatcher.Key.fromStaticField(T, "f");

    public static final MethodAccessor<Void> activate = T.selectMethod("public void a(int x, int y, int z, boolean active)");

    public static class Rideable {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartRideable");
    }

    public static class Furnace {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartFurnace");

        public static DataWatcher.Key<Boolean> DATA_SMOKING = DataWatcher.Key.fromStaticField(T, "c");

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

        public static final DataWatcher.Key<String> DATA_COMMAND = DataWatcher.Key.fromStaticField(T, "COMMAND");
        public static final DataWatcher.Key<Object> DATA_PREVIOUS_OUTPUT = DataWatcher.Key.fromStaticField(T, "b");
    }
}
