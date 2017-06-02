package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.mojang.authlib.GameProfile;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;

public class EntityHumanHandle extends EntityLivingHandle {
    public static final EntityHumanClass T = new EntityHumanClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityHumanHandle.class, "net.minecraft.server.EntityHuman");

    /* ============================================================================== */

    public static EntityHumanHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityHumanHandle handle = new EntityHumanHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public Object getInventoryRaw() {
        return T.inventoryRaw.get(instance);
    }

    public void setInventoryRaw(Object value) {
        T.inventoryRaw.set(instance, value);
    }

    public Object getEnderChestRaw() {
        return T.enderChestRaw.get(instance);
    }

    public void setEnderChestRaw(Object value) {
        T.enderChestRaw.set(instance, value);
    }

    public Object getFoodDataRaw() {
        return T.foodDataRaw.get(instance);
    }

    public void setFoodDataRaw(Object value) {
        T.foodDataRaw.set(instance, value);
    }

    public boolean isSleeping() {
        return T.sleeping.getBoolean(instance);
    }

    public void setSleeping(boolean value) {
        T.sleeping.setBoolean(instance, value);
    }

    public IntVector3 getBedPosition() {
        return T.bedPosition.get(instance);
    }

    public void setBedPosition(IntVector3 value) {
        T.bedPosition.set(instance, value);
    }

    public int getSleepTicks() {
        return T.sleepTicks.getInteger(instance);
    }

    public void setSleepTicks(int value) {
        T.sleepTicks.setInteger(instance, value);
    }

    public IntVector3 getSpawnCoord() {
        return T.spawnCoord.get(instance);
    }

    public void setSpawnCoord(IntVector3 value) {
        T.spawnCoord.set(instance, value);
    }

    public boolean isSpawnForced() {
        return T.spawnForced.getBoolean(instance);
    }

    public void setSpawnForced(boolean value) {
        T.spawnForced.setBoolean(instance, value);
    }

    public PlayerAbilities getAbilities() {
        return T.abilities.get(instance);
    }

    public void setAbilities(PlayerAbilities value) {
        T.abilities.set(instance, value);
    }

    public int getExpLevel() {
        return T.expLevel.getInteger(instance);
    }

    public void setExpLevel(int value) {
        T.expLevel.setInteger(instance, value);
    }

    public int getExpTotal() {
        return T.expTotal.getInteger(instance);
    }

    public void setExpTotal(int value) {
        T.expTotal.setInteger(instance, value);
    }

    public float getExp() {
        return T.exp.getFloat(instance);
    }

    public void setExp(float value) {
        T.exp.setFloat(instance, value);
    }

    public GameProfile getGameProfile() {
        return T.gameProfile.get(instance);
    }

    public void setGameProfile(GameProfile value) {
        T.gameProfile.set(instance, value);
    }

    public String getSpawnWorld() {
        return T.spawnWorld.get(instance);
    }

    public void setSpawnWorld(String value) {
        T.spawnWorld.set(instance, value);
    }

    public static final class EntityHumanClass extends Template.Class<EntityHumanHandle> {
        public final Template.Field.Converted<Object> inventoryRaw = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<Object> enderChestRaw = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<Object> foodDataRaw = new Template.Field.Converted<Object>();
        public final Template.Field.Boolean sleeping = new Template.Field.Boolean();
        public final Template.Field.Converted<IntVector3> bedPosition = new Template.Field.Converted<IntVector3>();
        public final Template.Field.Integer sleepTicks = new Template.Field.Integer();
        public final Template.Field.Converted<IntVector3> spawnCoord = new Template.Field.Converted<IntVector3>();
        public final Template.Field.Boolean spawnForced = new Template.Field.Boolean();
        public final Template.Field.Converted<PlayerAbilities> abilities = new Template.Field.Converted<PlayerAbilities>();
        public final Template.Field.Integer expLevel = new Template.Field.Integer();
        public final Template.Field.Integer expTotal = new Template.Field.Integer();
        public final Template.Field.Float exp = new Template.Field.Float();
        public final Template.Field<GameProfile> gameProfile = new Template.Field<GameProfile>();
        public final Template.Field<String> spawnWorld = new Template.Field<String>();

    }

}

