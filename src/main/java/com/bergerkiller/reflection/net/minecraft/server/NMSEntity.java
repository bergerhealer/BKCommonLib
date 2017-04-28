package com.bergerkiller.reflection.net.minecraft.server;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.SafeConstructor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftEntity;

import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.IBlockData;

public class NMSEntity {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("Entity")
    		.addImport("org.bukkit.craftbukkit.entity.CraftEntity");

    /* ================================================================================================================ */
    /* ================================================== FIELDS ====================================================== */
    /* ================================================================================================================ */

    public static final FieldAccessor<Entity> bukkitEntity  = T.nextField("protected CraftEntity bukkitEntity");
    public static final FieldAccessor<Integer> globalEntityCount = T.nextField("private static int entityCount");

    public static final TranslatorFieldAccessor<List<Entity>> passengers = T.nextField("public final List<Entity> passengers").translate(ConversionPairs.entityList);

    static {
        T.skipFieldSignature("protected int j");
    }

    public static final TranslatorFieldAccessor<Entity> vehicleField = T.nextFieldSignature("private net.minecraft.server.Entity au").translate(ConversionPairs.entity);
    public static final FieldAccessor<Boolean> ignoreChunkCheck = T.nextField("public boolean attachedToPlayer");
    public static final TranslatorFieldAccessor<World>  world = T.nextField("public World world").translate(ConversionPairs.world);
    public static final FieldAccessor<Double> lastX = T.nextField("public double lastX");
    public static final FieldAccessor<Double> lastY = T.nextField("public double lastY");
    public static final FieldAccessor<Double> lastZ = T.nextField("public double lastZ");
    public static final FieldAccessor<Double> locX  = T.nextField("public double locX");
    public static final FieldAccessor<Double> locY  = T.nextField("public double locY");
    public static final FieldAccessor<Double> locZ  = T.nextField("public double locZ");
    public static final FieldAccessor<Double> motX  = T.nextField("public double motX");
    public static final FieldAccessor<Double> motY  = T.nextField("public double motY");
    public static final FieldAccessor<Double> motZ  = T.nextField("public double motZ");
    public static final FieldAccessor<Float>  yaw   = T.nextField("public float yaw");
    public static final FieldAccessor<Float>  pitch = T.nextField("public float pitch");

    public static final FieldAccessor<Float>   lastYaw     = T.nextField("public float lastYaw");
    public static final FieldAccessor<Float>   lastPitch   = T.nextField("public float lastPitch");
    public static final FieldAccessor<Object>  boundingBox = T.nextField("private AxisAlignedBB boundingBox");
    public static final FieldAccessor<Boolean> onGround    = T.nextField("public boolean onGround");

    public static final FieldAccessor<Boolean> positionChanged_tmp = T.nextField("public boolean positionChanged");

    static { 
    	T.skipFieldSignature("public boolean B");
    	T.skipFieldSignature("public boolean C");
    }

    public static final FieldAccessor<Boolean> velocityChanged = T.nextField("public boolean velocityChanged");
    public static final FieldAccessor<Boolean> justLanded = T.nextFieldSignature("protected boolean E");

    static {
    	T.skipFieldSignature("private boolean aw");
    }
    
    public static final FieldAccessor<Boolean> dead = T.nextField("public boolean dead");
    public static final FieldAccessor<Float> width = T.nextField("public float width");
    public static final FieldAccessor<Float> length = T.nextField("public float length");

    static {
    	T.skipFieldSignature("public float I");
    	T.skipFieldSignature("public float J");
    	T.skipFieldSignature("public float K");
    }
    
    public static final FieldAccessor<Float> fallDistance = T.nextField("public float fallDistance");
    public static final FieldAccessor<Integer> stepCounter = T.nextFieldSignature("private int ax");
    
    static {
    	T.skipFieldSignature("public double M");
    	T.skipFieldSignature("public double N");
    	T.skipFieldSignature("public double O");
    	T.skipFieldSignature("public float P");
    }
    
    public static final FieldAccessor<Boolean> noclip = T.nextField("public boolean noclip");

    static {
    	T.skipFieldSignature("public float R");
    }

    public static final FieldAccessor<Random>  random = T.nextField("protected Random random");

    public static final TranslatorFieldAccessor<DataWatcher> datawatcher = T.nextField("protected DataWatcher datawatcher").translate(ConversionPairs.dataWatcher);

    public static final DataWatcher.Key<Byte> DATA_FLAGS = DataWatcher.Key.fromStaticField(T, "Z");
    public static final DataWatcher.Key<Integer> DATA_AIR_TICKS = DataWatcher.Key.fromStaticField(T, "az");
    public static final DataWatcher.Key<String> DATA_CUSTOM_NAME = DataWatcher.Key.fromStaticField(T, "aA");
    public static final DataWatcher.Key<Boolean> DATA_CUSTOM_NAME_VISIBLE = DataWatcher.Key.fromStaticField(T, "aB");
    public static final DataWatcher.Key<Boolean> DATA_SILENT = DataWatcher.Key.fromStaticField(T, "aC");
    public static final DataWatcher.Key<Boolean> DATA_NO_GRAVITY = DataWatcher.Key.fromStaticField(T, "aD");

    public static final int DATA_FLAG_ON_FIRE = (1 << 0);
    public static final int DATA_FLAG_SNEAKING = (1 << 1);
    public static final int DATA_FLAG_UNKNOWN1 = (1 << 2);
    public static final int DATA_FLAG_SPRINTING = (1 << 3);
    public static final int DATA_FLAG_UNKNOWN2 = (1 << 4);
    public static final int DATA_FLAG_INVISIBLE = (1 << 5);
    public static final int DATA_FLAG_GLOWING = (1 << 6);
    public static final int DATA_FLAG_FLYING = (1 << 7);

    public static final FieldAccessor<Boolean> isLoaded = T.nextField("public boolean aa");
    public static final FieldAccessor<Integer> chunkX = T.nextFieldSignature("public int ab");
    public static final FieldAccessor<Integer> chunkY = T.nextFieldSignature("public int ac");
    public static final FieldAccessor<Integer> chunkZ = T.nextFieldSignature("public int ad");

    static {
        T.skipFieldSignature("public boolean ah");
    }

    public static final FieldAccessor<Boolean> positionChanged = T.nextFieldSignature("public boolean impulse");

    public static final FieldAccessor<Integer> portalCooldown = T.nextField("public int portalCooldown");
    public static final FieldAccessor<Boolean> allowTeleportation = T.nextFieldSignature("protected boolean ak");

    /* Used in the move() function; unknown function. */
    public static final FieldAccessor<double[]> move_SomeArray = T.nextField("private double[] aI");
    public static final FieldAccessor<Long> move_SomeState = T.nextField("private long aJ");

    /* ================================================================================================================ */
    /* ================================================== METHODS ===================================================== */
    /* ================================================================================================================ */

    /*
     * protected void ##METHODNAME##(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
     *     if (flag) {
     *         if (this.fallDistance > 0.0F) {
     *             iblockdata.getBlock().fallOn(this.world, blockposition, this, this.fallDistance);
     *         }
     *         ...
     *     }
     *     ...
     * }
     */
    private static final MethodAccessor<Void> updateFalling        = T.selectMethod("protected void a(double, boolean, IBlockData, BlockPosition)");

    /*
     # protected void ##METHODNAME##() {
     *     AxisAlignedBB axisalignedbb = this.getBoundingBox();
     *     BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.d(axisalignedbb.a + 0.001D, axisalignedbb.b + 0.001D, axisalignedbb.c + 0.001D);
     *     BlockPosition.PooledBlockPosition blockposition_pooledblockposition1 = BlockPosition.PooledBlockPosition.d(axisalignedbb.d - 0.001D, axisalignedbb.e - 0.001D, axisalignedbb.f - 0.001D);
     *     BlockPosition.PooledBlockPosition blockposition_pooledblockposition2 = BlockPosition.PooledBlockPosition.s();
     *     ...
     * }
     */
    private static final MethodAccessor<Void> updateBlockCollision = T.selectMethod("protected void checkBlockCollisions()");

    /*
     # protected void ##METHODNAME##(BlockPosition blockposition, Block block) {
     *     SoundEffectType soundeffecttype = block.getStepSound();
     *     if (this.world.getType(blockposition.up()).getBlock() == Blocks.SNOW_LAYER) {
     *         soundeffecttype = Blocks.SNOW_LAYER.getStepSound();
     *         this.a(soundeffecttype.d(), soundeffecttype.a() * 0.15F, soundeffecttype.b());
     *     } else if (!block.getBlockData().getMaterial().isLiquid()) {
     *         this.a(soundeffecttype.d(), soundeffecttype.a() * 0.15F, soundeffecttype.b());
     *     }
     * }
     */
    private static final MethodAccessor<Void> playStepSound = T.selectMethod("protected void a(BlockPosition, Block)");

    /*
     # protected void ##METHODNAME##(float f, float f1) {
     *     // CraftBukkit start - yaw was sometimes set to NaN, so we need to set it back to 0
     *     if (Float.isNaN(f)) {
     *         f = 0;
     *     }
     *     if (f == Float.POSITIVE_INFINITY || f == Float.NEGATIVE_INFINITY) {
     *         if (this instanceof EntityPlayer) {
     *             this.world.getServer().getLogger().warning(this.getName() + " was caught trying to crash the server with an invalid yaw");
     *             ((CraftPlayer) this.getBukkitEntity()).kickPlayer("Infinite yaw (Hacking?)"); //Spigot "Nope" -> Descriptive reason
     *         }
     *         f = 0;
     *     }
     *     ...
     * }
     */
    private static final MethodAccessor<Void> setRotation = T.selectMethod("protected void setYawPitch(float, float)");
    
    /*
     # protected void ##METHODNAME##(float i) { // CraftBukkit - int -> float
     *     if (!this.fireProof) {
     *         this.damageEntity(DamageSource.FIRE, (float) i);
     *     }
     * }
     */
    public static final MethodAccessor<Void> burn = T.selectMethod("protected void burn(float dmg)");

    /*
     * void move(...) {
     *     ...
     #     this.a(this.##METHODNAME##(), f1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
     * }
     */
    public static final MethodAccessor<Object> getSwimSound = T.selectMethod("protected SoundEffect aa()");

    /*
     * void move(...) {
     *     ...
     *     this.##METHODNAME##(this.aa(), f1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
     * }
     * 
     # public void ##METHODNAME##(SoundEffect soundeffect, float f, float f1) {
     *     if (!this.isSilent()) {
     *         this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, soundeffect, this.bC(), f, f1);
     *     }
     * }
     */
    public static final MethodAccessor<Void> makeSound = T.selectMethod("public void a(SoundEffect soundeffect, float volume, float pitch)");

    /*
     # public boolean ###METHODNAME###() {
     *     if (this.world.a(this.boundingBox.grow(0.0D, -0.4000000059604645D, 0.0D).shrink(0.001D, 0.001D, 0.001D), Material.WATER, this)) {
     *         if (!this.inWater && !this.justCreated) {
     *             ...
     *         }
     *     }
     * }
     */
    private static final MethodAccessor<Boolean> isInWaterUpdate   = T.selectMethod("public boolean ak()");
    
    /*
     # public boolean ###METHODNAME###() {
     *     return this.inWater;
     * }
     */
    private static final MethodAccessor<Boolean> isInWaterNoUpdate = T.selectMethod("public boolean isInWater()");

    /*
     * public void move(double d0, double d1, double d2) {
     *     ....
     *     {
     *         if (bl.getType() != org.bukkit.Material.AIR) {
     *             VehicleBlockCollisionEvent event = new VehicleBlockCollisionEvent(vehicle, bl);
     *             world.getServer().getPluginManager().callEvent(event);
     *         }
     *     }
     *     
     *     // CraftBukkit end
     *
     #     if (this.###METHODNAME###() && (!this.onGround || !this.isSneaking() || !(this instanceof EntityHuman)) && !this.isPassenger()) {
     *         double d22 = this.locX - d4;
     *         double d23 = this.locY - d5;
     *         ...
     *     }
     * }
     */
    private static final MethodAccessor<Boolean> hasMovementSound = T.selectMethod("protected boolean playStepSound()");

    /*
     # protected void ##METHODNAME##(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
     *     if (flag) {
     *         if (this.fallDistance > 0.0F) {
     *             iblockdata.getBlock().fallOn(this.world, blockposition, this, this.fallDistance);
     *         }
     *
     *         this.fallDistance = 0.0F;
     *     } else if (d0 < 0.0D) {
     *         this.fallDistance = ((float)(this.fallDistance - d0));
     *     }
     * }
     */
    public static final MethodAccessor<Void> doFallUpdate = T.selectMethod("protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition)");

    /*
     * protected void ##METHODNAME##(BlockPosition blockposition, Block block) {
     *     SoundEffectType soundeffecttype = block.getStepSound();
     *
     *     if (this.world.getType(blockposition.up()).getBlock() == Blocks.SNOW_LAYER) {
     *         soundeffecttype = Blocks.SNOW_LAYER.getStepSound();
     *         a(soundeffecttype.d(), soundeffecttype.a() * 0.15F, soundeffecttype.b());
     *     } else if (!block.getBlockData().getMaterial().isLiquid()) {
     *         a(soundeffecttype.d(), soundeffecttype.a() * 0.15F, soundeffecttype.b());
     *     }
     * }
     */
    public static final MethodAccessor<Void> doStepSoundUpdate = T.selectMethod("protected void a(BlockPosition blockposition, Block block)");

    /*
     * protected void checkBlockCollisions() {
     *     AxisAlignedBB axisalignedbb = getBoundingBox();
     *     BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.d(axisalignedbb.a + 0.001D, axisalignedbb.b + 0.001D, axisalignedbb.c + 0.001D);
     *     BlockPosition.PooledBlockPosition blockposition_pooledblockposition1 = BlockPosition.PooledBlockPosition.d(axisalignedbb.d - 0.001D, axisalignedbb.e - 0.001D, axisalignedbb.f - 0.001D);
     *     BlockPosition.PooledBlockPosition blockposition_pooledblockposition2 = BlockPosition.PooledBlockPosition.s();
     *     ...
     * }
     */
    public static final MethodAccessor<Void> checkBlockCollisions = T.selectMethod("protected void checkBlockCollisions()");

    /*
     * public double ##METHODNAME##(double d0, double d1, double d2) {
     *     double d3 = this.locX - d0;
     *     double d4 = this.locY - d1;
     *     double d5 = this.locZ - d2;
     *
     *     return MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
     * }
     */
    public static final MethodAccessor<Double> calculateDistance = T.selectMethod("public double e(double d0, double d1, double d2)");

    public static final MethodAccessor<Object> getBoundingBox = T.selectMethod("public AxisAlignedBB getBoundingBox()");

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final SafeConstructor entityItemConstr = new SafeConstructor(CommonUtil.getNMSClass("EntityItem"),
            NMSWorld.T.getType(), double.class, double.class, double.class);

    public static Item createEntityItem(World world, double x, double y, double z) {
        return (Item) Conversion.toEntity.convert(entityItemConstr.newInstance(Conversion.toWorldHandle.convert(world), x, y, z));
    }

    public static boolean isInWater(Object entityHandle, boolean update) {
        return update ? isInWaterUpdate.invoke(entityHandle) : isInWaterNoUpdate.invoke(entityHandle);
    }

    public static void updateFalling(Object entityHandle, double deltaY, boolean hitGround, IBlockData block, BlockPosition bpos) {
        updateFalling.invoke(entityHandle, deltaY, hitGround, block, bpos);
    }

    public static void updateBlockCollision(Object entityHandle) {
        updateBlockCollision.invoke(entityHandle);
    }

    @Deprecated
    public static void playStepSound(Object entityHandle, int x, int y, int z, int typeId) {
        playStepSound(entityHandle, x, y, z, CommonNMS.getBlock(typeId));
    }

    public static void playStepSound(Object entityHandle, int x, int y, int z, Object blockStepped) {
        if (blockStepped != null) {
            playStepSound.invoke(entityHandle, new BlockPosition(x, y, z), blockStepped);
        }
    }

    public static boolean hasMovementSound(Object entityHandle) {
    	return hasMovementSound.invoke(entityHandle);
    }

    public static void setRotation(Object entityHandle, float yaw, float pitch) {
        setRotation.invoke(entityHandle, yaw, pitch);
    }

    public static Entity createEntity(Object entityHandle) {
    	return CBCraftEntity.getEntity.invoke(null, Bukkit.getServer(), entityHandle);
    }
}
