package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Random;

import net.minecraft.server.v1_8_R1.Block;
import net.minecraft.server.v1_8_R1.BlockPosition;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.CBClassTemplate;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class EntityRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("Entity");

    /* Fields */
    public static final FieldAccessor<org.bukkit.entity.Entity> bukkitEntity = TEMPLATE.getField("bukkitEntity");
    public static final FieldAccessor<Integer> chunkX = TEMPLATE.getField("ae");
    public static final FieldAccessor<Integer> chunkY = TEMPLATE.getField("af");
    public static final FieldAccessor<Integer> chunkZ = TEMPLATE.getField("ag");
    public static final FieldAccessor<Boolean> positionChanged = TEMPLATE.getField("al");
    public static final FieldAccessor<Boolean> velocityChanged = TEMPLATE.getField("velocityChanged");
    public static final FieldAccessor<Boolean> justLanded = TEMPLATE.getField("g");
    public static final FieldAccessor<Double> locX = TEMPLATE.getField("locX");
    public static final FieldAccessor<Double> locY = TEMPLATE.getField("locY");
    public static final FieldAccessor<Double> locZ = TEMPLATE.getField("locZ");
    public static final FieldAccessor<Double> motX = TEMPLATE.getField("motX");
    public static final FieldAccessor<Double> motY = TEMPLATE.getField("motY");
    public static final FieldAccessor<Double> motZ = TEMPLATE.getField("motZ");
    public static final FieldAccessor<Float> yaw = TEMPLATE.getField("yaw");
    public static final FieldAccessor<Float> pitch = TEMPLATE.getField("pitch");
    public static final FieldAccessor<Random> random = TEMPLATE.getField("random");
    public static final FieldAccessor<Integer> stepCounter = TEMPLATE.getField("d");
    public static final FieldAccessor<Boolean> ignoreChunkCheck = TEMPLATE.getField("k"); //Note: Not sure if the name is correct!
    public static final FieldAccessor<Boolean> isLoaded = TEMPLATE.getField("ag");
    public static final FieldAccessor<Boolean> allowTeleportation = TEMPLATE.getField("an");

    /* Methods */
    private static final MethodAccessor<Void> updateFalling = TEMPLATE.getMethod("a", double.class, boolean.class, Block.class, BlockPosition.class);//double.class,boolean.class
    private static final MethodAccessor<Void> updateBlockCollision = TEMPLATE.getMethod("I");
    private static final MethodAccessor<Void> playStepSound = TEMPLATE.getMethod("a", int.class, int.class, int.class, BlockRef.TEMPLATE.getType());
    private static final MethodAccessor<Boolean> hasMovementSound = TEMPLATE.getMethod("g_");
    private static final MethodAccessor<Void> setRotation = TEMPLATE.getMethod("setYawPitch", float.class, float.class);
    private static final MethodAccessor<Void> burn = TEMPLATE.getMethod("burn", float.class);
    private static final MethodAccessor<Boolean> isInWaterUpdate = TEMPLATE.getMethod("N");
    private static final MethodAccessor<Boolean> isInWaterNoUpdate = TEMPLATE.getMethod("M");
    public static final MethodAccessor<String> getSwimSound = TEMPLATE.getMethod("H");

    /* External */
    public static final TranslatorFieldAccessor<World> world = TEMPLATE.getField("world").translate(ConversionPairs.world);
    private static final ClassTemplate<?> CRAFT_TEMPLATE = CBClassTemplate.create("entity.CraftEntity");
    private static final MethodAccessor<Entity> getCraftEntity = CRAFT_TEMPLATE.getMethod("getEntity", CraftServerRef.TEMPLATE.getType(), TEMPLATE.getType());

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final SafeConstructor entityItemConstr = new SafeConstructor(CommonUtil.getNMSClass("EntityItem"),
            WorldRef.TEMPLATE.getType(), double.class, double.class, double.class);

    public static Item createEntityItem(World world, double x, double y, double z) {
        return (Item) Conversion.toEntity.convert(entityItemConstr.newInstance(Conversion.toWorldHandle.convert(world), x, y, z));
    }

    public static boolean isInWater(Object entityHandle, boolean update) {
        return update ? isInWaterUpdate.invoke(entityHandle) : isInWaterNoUpdate.invoke(entityHandle);
    }

    public static void updateFalling(Object entityHandle, double deltaY, boolean hitGround) {
        updateFalling.invoke(entityHandle, deltaY, hitGround);
    }

    public static void updateBlockCollision(Object entityHandle) {
        updateBlockCollision.invoke(entityHandle);
    }

    @Deprecated
    public static void playStepSound(Object entityHandle, int x, int y, int z, int typeId) {
        playStepSound(entityHandle, x, y, z, CommonNMS.getBlock(typeId));
    }

    public static void playStepSound(Object entityHandle, int x, int y, int z, Object blockStepped) {
        playStepSound.invoke(entityHandle, x, y, z, blockStepped);
    }

    public static boolean hasMovementSound(Object entityHandle) {
        return hasMovementSound.invoke(entityHandle);
    }

    public static void setRotation(Object entityHandle, float yaw, float pitch) {
        setRotation.invoke(entityHandle, yaw, pitch);
    }

    public static void burn(Object entityHandle, float damage) {
        burn.invoke(entityHandle, damage);
    }

    public static Entity createEntity(Object entityHandle) {
        return getCraftEntity.invoke(null, Bukkit.getServer(), entityHandle);
    }
}
