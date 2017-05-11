package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import org.bukkit.World;
import java.util.Random;
import java.util.List;
import org.bukkit.entity.Entity;

public class EntityHandle extends Template.Handle {
    public static final EntityClass T = new EntityClass();

    public static final Key<Byte> DATA_FLAGS = T.DATA_FLAGS.getSafe();
    public static final Key<Integer> DATA_AIR_TICKS = T.DATA_AIR_TICKS.getSafe();
    public static final Key<String> DATA_CUSTOM_NAME = T.DATA_CUSTOM_NAME.getSafe();
    public static final Key<Boolean> DATA_CUSTOM_NAME_VISIBLE = T.DATA_CUSTOM_NAME_VISIBLE.getSafe();
    public static final Key<Boolean> DATA_SILENT = T.DATA_SILENT.getSafe();
    public static final Key<Boolean> DATA_NO_GRAVITY = T.DATA_NO_GRAVITY.getSafe();

    public static int entityCount() {
        return T.entityCount.getInteger();
    }

    public static void entityCount_set(int value) {
        T.entityCount.setInteger(value);
    }

    public void updateFalling(double d0, boolean flag, BlockData blockData, IntVector3 position) {
        T.updateFalling.invoke(instance, d0, flag, blockData, position);
    }

    public void updateBlockCollision() {
        T.updateBlockCollision.invoke(instance);
    }

    public void playStepSound(IntVector3 position, BlockData blockData) {
        T.playStepSound.invoke(instance, position, blockData);
    }

    public void burn(float dmg) {
        T.burn.invoke(instance, dmg);
    }

    public Object getSwimSound() {
        return T.getSwimSound.invoke(instance);
    }

    public void makeSound(Object soundeffect, float volume, float pitch) {
        T.makeSound.invoke(instance, soundeffect, volume, pitch);
    }

    public boolean isInWaterUpdate() {
        return T.isInWaterUpdate.invoke(instance);
    }

    public boolean isInWaterNoUpdate() {
        return T.isInWaterNoUpdate.invoke(instance);
    }

    public boolean hasMovementSound() {
        return T.hasMovementSound.invoke(instance);
    }

    public void doFallUpdate(double d0, boolean flag, BlockData blockData, IntVector3 blockposition) {
        T.doFallUpdate.invoke(instance, d0, flag, blockData, blockposition);
    }

    public void doStepSoundUpdate(IntVector3 blockposition, BlockData blockData) {
        T.doStepSoundUpdate.invoke(instance, blockposition, blockData);
    }

    public void checkBlockCollisions() {
        T.checkBlockCollisions.invoke(instance);
    }

    public double calculateDistance(double x, double y, double z) {
        return T.calculateDistance.invoke(instance, x, y, z);
    }

    public Object getBoundingBox() {
        return T.getBoundingBox.invoke(instance);
    }

    public Entity getBukkitEntity() {
        return T.bukkitEntity.get(instance);
    }

    public void setBukkitEntity(Entity value) {
        T.bukkitEntity.set(instance, value);
    }

    public List<Entity> getPassengers() {
        return T.passengers.get(instance);
    }

    public void setPassengers(List<Entity> value) {
        T.passengers.set(instance, value);
    }

    public Entity getVehicle() {
        return T.vehicle.get(instance);
    }

    public void setVehicle(Entity value) {
        T.vehicle.set(instance, value);
    }

    public boolean getIgnoreChunkCheck() {
        return T.ignoreChunkCheck.getBoolean(instance);
    }

    public void setIgnoreChunkCheck(boolean value) {
        T.ignoreChunkCheck.setBoolean(instance, value);
    }

    public World getWorld() {
        return T.world.get(instance);
    }

    public void setWorld(World value) {
        T.world.set(instance, value);
    }

    public double getLastX() {
        return T.lastX.getDouble(instance);
    }

    public void setLastX(double value) {
        T.lastX.setDouble(instance, value);
    }

    public double getLastY() {
        return T.lastY.getDouble(instance);
    }

    public void setLastY(double value) {
        T.lastY.setDouble(instance, value);
    }

    public double getLastZ() {
        return T.lastZ.getDouble(instance);
    }

    public void setLastZ(double value) {
        T.lastZ.setDouble(instance, value);
    }

    public double getLocX() {
        return T.locX.getDouble(instance);
    }

    public void setLocX(double value) {
        T.locX.setDouble(instance, value);
    }

    public double getLocY() {
        return T.locY.getDouble(instance);
    }

    public void setLocY(double value) {
        T.locY.setDouble(instance, value);
    }

    public double getLocZ() {
        return T.locZ.getDouble(instance);
    }

    public void setLocZ(double value) {
        T.locZ.setDouble(instance, value);
    }

    public double getMotX() {
        return T.motX.getDouble(instance);
    }

    public void setMotX(double value) {
        T.motX.setDouble(instance, value);
    }

    public double getMotY() {
        return T.motY.getDouble(instance);
    }

    public void setMotY(double value) {
        T.motY.setDouble(instance, value);
    }

    public double getMotZ() {
        return T.motZ.getDouble(instance);
    }

    public void setMotZ(double value) {
        T.motZ.setDouble(instance, value);
    }

    public float getYaw() {
        return T.yaw.getFloat(instance);
    }

    public void setYaw(float value) {
        T.yaw.setFloat(instance, value);
    }

    public float getPitch() {
        return T.pitch.getFloat(instance);
    }

    public void setPitch(float value) {
        T.pitch.setFloat(instance, value);
    }

    public float getLastYaw() {
        return T.lastYaw.getFloat(instance);
    }

    public void setLastYaw(float value) {
        T.lastYaw.setFloat(instance, value);
    }

    public float getLastPitch() {
        return T.lastPitch.getFloat(instance);
    }

    public void setLastPitch(float value) {
        T.lastPitch.setFloat(instance, value);
    }

    public Object getBoundingBoxField() {
        return T.boundingBoxField.get(instance);
    }

    public void setBoundingBoxField(Object value) {
        T.boundingBoxField.set(instance, value);
    }

    public boolean getOnGround() {
        return T.onGround.getBoolean(instance);
    }

    public void setOnGround(boolean value) {
        T.onGround.setBoolean(instance, value);
    }

    public boolean getVelocityChanged() {
        return T.velocityChanged.getBoolean(instance);
    }

    public void setVelocityChanged(boolean value) {
        T.velocityChanged.setBoolean(instance, value);
    }

    public boolean getJustLanded() {
        return T.justLanded.getBoolean(instance);
    }

    public void setJustLanded(boolean value) {
        T.justLanded.setBoolean(instance, value);
    }

    public boolean getDead() {
        return T.dead.getBoolean(instance);
    }

    public void setDead(boolean value) {
        T.dead.setBoolean(instance, value);
    }

    public float getWidth() {
        return T.width.getFloat(instance);
    }

    public void setWidth(float value) {
        T.width.setFloat(instance, value);
    }

    public float getLength() {
        return T.length.getFloat(instance);
    }

    public void setLength(float value) {
        T.length.setFloat(instance, value);
    }

    public float getFallDistance() {
        return T.fallDistance.getFloat(instance);
    }

    public void setFallDistance(float value) {
        T.fallDistance.setFloat(instance, value);
    }

    public int getStepCounter() {
        return T.stepCounter.getInteger(instance);
    }

    public void setStepCounter(int value) {
        T.stepCounter.setInteger(instance, value);
    }

    public boolean getNoclip() {
        return T.noclip.getBoolean(instance);
    }

    public void setNoclip(boolean value) {
        T.noclip.setBoolean(instance, value);
    }

    public Random getRandom() {
        return T.random.get(instance);
    }

    public void setRandom(Random value) {
        T.random.set(instance, value);
    }

    public DataWatcher getDatawatcher() {
        return T.datawatcher.get(instance);
    }

    public void setDatawatcher(DataWatcher value) {
        T.datawatcher.set(instance, value);
    }

    public boolean getIsLoaded() {
        return T.isLoaded.getBoolean(instance);
    }

    public void setIsLoaded(boolean value) {
        T.isLoaded.setBoolean(instance, value);
    }

    public int getChunkX() {
        return T.chunkX.getInteger(instance);
    }

    public void setChunkX(int value) {
        T.chunkX.setInteger(instance, value);
    }

    public int getChunkY() {
        return T.chunkY.getInteger(instance);
    }

    public void setChunkY(int value) {
        T.chunkY.setInteger(instance, value);
    }

    public int getChunkZ() {
        return T.chunkZ.getInteger(instance);
    }

    public void setChunkZ(int value) {
        T.chunkZ.setInteger(instance, value);
    }

    public boolean getPositionChanged() {
        return T.positionChanged.getBoolean(instance);
    }

    public void setPositionChanged(boolean value) {
        T.positionChanged.setBoolean(instance, value);
    }

    public int getPortalCooldown() {
        return T.portalCooldown.getInteger(instance);
    }

    public void setPortalCooldown(int value) {
        T.portalCooldown.setInteger(instance, value);
    }

    public boolean getAllowTeleportation() {
        return T.allowTeleportation.getBoolean(instance);
    }

    public void setAllowTeleportation(boolean value) {
        T.allowTeleportation.setBoolean(instance, value);
    }

    public double[] getMove_SomeArray() {
        return T.move_SomeArray.get(instance);
    }

    public void setMove_SomeArray(double[] value) {
        T.move_SomeArray.set(instance, value);
    }

    public long getMove_SomeState() {
        return T.move_SomeState.getLong(instance);
    }

    public void setMove_SomeState(long value) {
        T.move_SomeState.setLong(instance, value);
    }

    public static class EntityClass extends Template.Class {

        protected EntityClass() {
            init(EntityClass.class, "net.minecraft.server.Entity");
        }

        public final Template.StaticField.Integer entityCount = new Template.StaticField.Integer();
        public final Template.StaticField.Converted<Key<Byte>> DATA_FLAGS = new Template.StaticField.Converted<Key<Byte>>();
        public final Template.StaticField.Converted<Key<Integer>> DATA_AIR_TICKS = new Template.StaticField.Converted<Key<Integer>>();
        public final Template.StaticField.Converted<Key<String>> DATA_CUSTOM_NAME = new Template.StaticField.Converted<Key<String>>();
        public final Template.StaticField.Converted<Key<Boolean>> DATA_CUSTOM_NAME_VISIBLE = new Template.StaticField.Converted<Key<Boolean>>();
        public final Template.StaticField.Converted<Key<Boolean>> DATA_SILENT = new Template.StaticField.Converted<Key<Boolean>>();
        public final Template.StaticField.Converted<Key<Boolean>> DATA_NO_GRAVITY = new Template.StaticField.Converted<Key<Boolean>>();

        public final Template.Field.Converted<Entity> bukkitEntity = new Template.Field.Converted<Entity>();
        public final Template.Field.Converted<List<Entity>> passengers = new Template.Field.Converted<List<Entity>>();
        public final Template.Field.Converted<Entity> vehicle = new Template.Field.Converted<Entity>();
        public final Template.Field.Boolean ignoreChunkCheck = new Template.Field.Boolean();
        public final Template.Field.Converted<World> world = new Template.Field.Converted<World>();
        public final Template.Field.Double lastX = new Template.Field.Double();
        public final Template.Field.Double lastY = new Template.Field.Double();
        public final Template.Field.Double lastZ = new Template.Field.Double();
        public final Template.Field.Double locX = new Template.Field.Double();
        public final Template.Field.Double locY = new Template.Field.Double();
        public final Template.Field.Double locZ = new Template.Field.Double();
        public final Template.Field.Double motX = new Template.Field.Double();
        public final Template.Field.Double motY = new Template.Field.Double();
        public final Template.Field.Double motZ = new Template.Field.Double();
        public final Template.Field.Float yaw = new Template.Field.Float();
        public final Template.Field.Float pitch = new Template.Field.Float();
        public final Template.Field.Float lastYaw = new Template.Field.Float();
        public final Template.Field.Float lastPitch = new Template.Field.Float();
        public final Template.Field.Converted<Object> boundingBoxField = new Template.Field.Converted<Object>();
        public final Template.Field.Boolean onGround = new Template.Field.Boolean();
        public final Template.Field.Boolean velocityChanged = new Template.Field.Boolean();
        public final Template.Field.Boolean justLanded = new Template.Field.Boolean();
        public final Template.Field.Boolean dead = new Template.Field.Boolean();
        public final Template.Field.Float width = new Template.Field.Float();
        public final Template.Field.Float length = new Template.Field.Float();
        public final Template.Field.Float fallDistance = new Template.Field.Float();
        public final Template.Field.Integer stepCounter = new Template.Field.Integer();
        public final Template.Field.Boolean noclip = new Template.Field.Boolean();
        public final Template.Field<Random> random = new Template.Field<Random>();
        public final Template.Field.Converted<DataWatcher> datawatcher = new Template.Field.Converted<DataWatcher>();
        public final Template.Field.Boolean isLoaded = new Template.Field.Boolean();
        public final Template.Field.Integer chunkX = new Template.Field.Integer();
        public final Template.Field.Integer chunkY = new Template.Field.Integer();
        public final Template.Field.Integer chunkZ = new Template.Field.Integer();
        public final Template.Field.Boolean positionChanged = new Template.Field.Boolean();
        public final Template.Field.Integer portalCooldown = new Template.Field.Integer();
        public final Template.Field.Boolean allowTeleportation = new Template.Field.Boolean();
        public final Template.Field<double[]> move_SomeArray = new Template.Field<double[]>();
        public final Template.Field.Long move_SomeState = new Template.Field.Long();

        public final Template.Method.Converted<Void> updateFalling = new Template.Method.Converted<Void>();
        public final Template.Method<Void> updateBlockCollision = new Template.Method<Void>();
        public final Template.Method.Converted<Void> playStepSound = new Template.Method.Converted<Void>();
        public final Template.Method<Void> burn = new Template.Method<Void>();
        public final Template.Method.Converted<Object> getSwimSound = new Template.Method.Converted<Object>();
        public final Template.Method.Converted<Void> makeSound = new Template.Method.Converted<Void>();
        public final Template.Method<Boolean> isInWaterUpdate = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isInWaterNoUpdate = new Template.Method<Boolean>();
        public final Template.Method<Boolean> hasMovementSound = new Template.Method<Boolean>();
        public final Template.Method.Converted<Void> doFallUpdate = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> doStepSoundUpdate = new Template.Method.Converted<Void>();
        public final Template.Method<Void> checkBlockCollisions = new Template.Method<Void>();
        public final Template.Method<Double> calculateDistance = new Template.Method<Double>();
        public final Template.Method.Converted<Object> getBoundingBox = new Template.Method.Converted<Object>();

    }
}
