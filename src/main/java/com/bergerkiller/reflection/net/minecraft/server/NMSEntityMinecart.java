package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.MethodAccessor;

public class NMSEntityMinecart {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartAbstract");

    public static Object DATA_SHAKING_FACTOR = T.getStaticFieldValue("a", NMSDataWatcherObject.T.getType());
    public static Object DATA_SHAKING_DIRECTION = T.getStaticFieldValue("b", NMSDataWatcherObject.T.getType());
    public static Object DATA_SHAKING_DAMAGE = T.getStaticFieldValue("c", NMSDataWatcherObject.T.getType());
    public static Object DATA_BLOCK_TYPE = T.getStaticFieldValue("d", NMSDataWatcherObject.T.getType());
    public static Object DATA_BLOCK_OFFSET = T.getStaticFieldValue("e", NMSDataWatcherObject.T.getType());
    public static Object DATA_BLOCK_VISIBLE = T.getStaticFieldValue("f", NMSDataWatcherObject.T.getType());

	public static class Furnace {
		public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityMinecartFurnace");
		public static final FieldAccessor<Integer> fuel = T.nextField("private int d");
		public static final FieldAccessor<Double> pushForceX = T.nextFieldSignature("public double a");
	    public static final FieldAccessor<Double> pushForceZ = T.nextFieldSignature("public double b");
	    
	    public static final MethodAccessor<Boolean> isSmokingMethod = T.selectMethod("protected boolean j()");
	    public static final MethodAccessor<Void> setSmokingMethod = T.selectMethod("protected void l(boolean paramBoolean)");
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
}
