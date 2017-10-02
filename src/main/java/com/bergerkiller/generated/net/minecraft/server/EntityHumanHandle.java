package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityHuman</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EntityHumanHandle extends EntityLivingHandle {
    /** @See {@link EntityHumanClass} */
    public static final EntityHumanClass T = new EntityHumanClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityHumanHandle.class, "net.minecraft.server.EntityHuman");

    /* ============================================================================== */

    public static EntityHumanHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public Object getInventoryRaw() {
        return T.inventoryRaw.get(getRaw());
    }

    public void setInventoryRaw(Object value) {
        T.inventoryRaw.set(getRaw(), value);
    }

    public Object getEnderChestRaw() {
        return T.enderChestRaw.get(getRaw());
    }

    public void setEnderChestRaw(Object value) {
        T.enderChestRaw.set(getRaw(), value);
    }

    public ContainerHandle getActiveContainer() {
        return T.activeContainer.get(getRaw());
    }

    public void setActiveContainer(ContainerHandle value) {
        T.activeContainer.set(getRaw(), value);
    }

    public Object getFoodDataRaw() {
        return T.foodDataRaw.get(getRaw());
    }

    public void setFoodDataRaw(Object value) {
        T.foodDataRaw.set(getRaw(), value);
    }

    public boolean isSleeping() {
        return T.sleeping.getBoolean(getRaw());
    }

    public void setSleeping(boolean value) {
        T.sleeping.setBoolean(getRaw(), value);
    }

    public IntVector3 getBedPosition() {
        return T.bedPosition.get(getRaw());
    }

    public void setBedPosition(IntVector3 value) {
        T.bedPosition.set(getRaw(), value);
    }

    public int getSleepTicks() {
        return T.sleepTicks.getInteger(getRaw());
    }

    public void setSleepTicks(int value) {
        T.sleepTicks.setInteger(getRaw(), value);
    }

    public IntVector3 getSpawnCoord() {
        return T.spawnCoord.get(getRaw());
    }

    public void setSpawnCoord(IntVector3 value) {
        T.spawnCoord.set(getRaw(), value);
    }

    public boolean isSpawnForced() {
        return T.spawnForced.getBoolean(getRaw());
    }

    public void setSpawnForced(boolean value) {
        T.spawnForced.setBoolean(getRaw(), value);
    }

    public PlayerAbilities getAbilities() {
        return T.abilities.get(getRaw());
    }

    public void setAbilities(PlayerAbilities value) {
        T.abilities.set(getRaw(), value);
    }

    public int getExpLevel() {
        return T.expLevel.getInteger(getRaw());
    }

    public void setExpLevel(int value) {
        T.expLevel.setInteger(getRaw(), value);
    }

    public int getExpTotal() {
        return T.expTotal.getInteger(getRaw());
    }

    public void setExpTotal(int value) {
        T.expTotal.setInteger(getRaw(), value);
    }

    public float getExp() {
        return T.exp.getFloat(getRaw());
    }

    public void setExp(float value) {
        T.exp.setFloat(getRaw(), value);
    }

    public GameProfileHandle getGameProfile() {
        return T.gameProfile.get(getRaw());
    }

    public void setGameProfile(GameProfileHandle value) {
        T.gameProfile.set(getRaw(), value);
    }

    public String getSpawnWorld() {
        return T.spawnWorld.get(getRaw());
    }

    public void setSpawnWorld(String value) {
        T.spawnWorld.set(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.EntityHuman</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityHumanClass extends Template.Class<EntityHumanHandle> {
        public final Template.Field.Converted<Object> inventoryRaw = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<Object> enderChestRaw = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<ContainerHandle> activeContainer = new Template.Field.Converted<ContainerHandle>();
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
        public final Template.Field.Converted<GameProfileHandle> gameProfile = new Template.Field.Converted<GameProfileHandle>();
        public final Template.Field<String> spawnWorld = new Template.Field<String>();

    }

}

