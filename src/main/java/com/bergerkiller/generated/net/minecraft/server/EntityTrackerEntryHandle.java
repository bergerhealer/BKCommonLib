package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityTrackerEntry</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EntityTrackerEntryHandle extends Template.Handle {
    /** @See {@link EntityTrackerEntryClass} */
    public static final EntityTrackerEntryClass T = new EntityTrackerEntryClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityTrackerEntryHandle.class, "net.minecraft.server.EntityTrackerEntry");

    /* ============================================================================== */

    public static EntityTrackerEntryHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityTrackerEntryHandle handle = new EntityTrackerEntryHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public void hideForAll() {
        T.hideForAll.invoke(instance);
    }

    public void removeViewer(Player player) {
        T.removeViewer.invoke(instance, player);
    }

    public void scanPlayers(List<Player> playerList) {
        T.scanPlayers.invoke(instance, playerList);
    }

    public void updatePlayer(Player player) {
        T.updatePlayer.invoke(instance, player);
    }

    public CommonPacket getSpawnPacket() {
        return T.getSpawnPacket.invoke(instance);
    }


    public static EntityTrackerEntryHandle createNew(org.bukkit.entity.Entity entity, int viewDistance, int playerViewDistance, int updateInterval, boolean isMobile) {
        if (T.constr_entity_viewDistance_playerViewDistance_updateInterval_isMobile.isAvailable()) {
            return T.constr_entity_viewDistance_playerViewDistance_updateInterval_isMobile.newInstance(
                entity, viewDistance, playerViewDistance, updateInterval, isMobile);
        } else {
            return T.constr_entity_viewDistance_updateInterval_isMobile.newInstance(
                entity, viewDistance, updateInterval, isMobile);
        }
    }


    public static double POSITION_STEP;
    public static float ROTATION_STEP;
    static {
        if (T.long_xLoc.isAvailable()) {
            POSITION_STEP = 1.0 / 4096.0;
        } else {
            POSITION_STEP = 1.0 / 32.0;
        }
        ROTATION_STEP = 360.0f / 256.0f;
    }

    public static final int getProtocolRotation(float angle) {
        return com.bergerkiller.bukkit.common.utils.MathUtil.floor(angle * 256.0F / 360.0F);
    }

    public void setLocX(double x) {
        if (T.long_xLoc.isAvailable()) {
            T.long_xLoc.setLong(instance, com.bergerkiller.bukkit.common.utils.MathUtil.longFloor(x * 4096.0));
        } else {
            T.int_xLoc.setInteger(instance, com.bergerkiller.bukkit.common.utils.MathUtil.floor(x * 32.0));
        }
    }

    public void setLocY(double y) {
        if (T.long_yLoc.isAvailable()) {
            T.long_yLoc.setLong(instance, com.bergerkiller.bukkit.common.utils.MathUtil.longFloor(y * 4096.0));
        } else {
            T.int_yLoc.setInteger(instance, com.bergerkiller.bukkit.common.utils.MathUtil.floor(y * 32.0));
        }
    }

    public void setLocZ(double z) {
        if (T.long_zLoc.isAvailable()) {
            T.long_zLoc.setLong(instance, com.bergerkiller.bukkit.common.utils.MathUtil.longFloor(z * 4096.0));
        } else {
            T.int_zLoc.setInteger(instance, com.bergerkiller.bukkit.common.utils.MathUtil.floor(z * 32.0));
        }
    }

    public void setYaw(float yaw) {
        T.raw_yRot.setInteger(instance, getProtocolRotation(yaw));
    }

    public void setPitch(float pitch) {
        T.raw_xRot.setInteger(instance, getProtocolRotation(pitch));
    }

    public void setHeadYaw(float headYaw) {
        T.raw_headYaw.setInteger(instance, getProtocolRotation(headYaw));
    }

    public double getLocX() {
        if (T.long_xLoc.isAvailable()) {
            return (double) T.long_xLoc.getLong(instance) / 4096.0;
        } else {
            return (double) T.int_xLoc.getInteger(instance) / 32.0;
        }
    }

    public double getLocY() {
        if (T.long_yLoc.isAvailable()) {
            return (double) T.long_yLoc.getLong(instance) / 4096.0;
        } else {
            return (double) T.int_yLoc.getInteger(instance) / 32.0;
        }
    }

    public double getLocZ() {
        if (T.long_zLoc.isAvailable()) {
            return (double) T.long_zLoc.getLong(instance) / 4096.0;
        } else {
            return (double) T.int_zLoc.getInteger(instance) / 32.0;
        }
    }

    public float getYaw() {
        return ((float) T.raw_yRot.getInteger(instance) * 360) / 256.0F;
    }

    public float getPitch() {
        return ((float) T.raw_xRot.getInteger(instance) * 360) / 256.0F;
    }

    public float getHeadYaw() {
        return ((float) T.raw_headYaw.getInteger(instance) * 360) / 256.0F;
    }


    public java.util.Collection<org.bukkit.entity.Player> getViewers() {
        if (T.viewersMap.isAvailable()) {
            return T.viewersMap.get(instance).keySet();
        } else {
            return T.viewersSet.get(instance);
        }
    }

    public boolean addViewerToSet(org.bukkit.entity.Player viewer) {
        if (T.viewersMap.isAvailable()) {
            java.util.Map<org.bukkit.entity.Player, Boolean> map = T.viewersMap.get(instance);
            if (map.containsKey(viewer)) {
                return false;
            } else {
                map.put(viewer, true);
                return true;
            }
        } else {
            return T.viewersSet.get(instance).add(viewer);
        }
    }

    public boolean removeViewerFromSet(org.bukkit.entity.Player viewer) {
        if (T.viewersMap.isAvailable()) {
            return T.viewersMap.get(instance).remove(viewer) != null;
        } else {
            return T.viewersSet.get(instance).remove(viewer);
        }
    }
    public EntityHandle getTracker() {
        return T.tracker.get(instance);
    }

    public void setTracker(EntityHandle value) {
        T.tracker.set(instance, value);
    }

    public int getViewDistance() {
        return T.viewDistance.getInteger(instance);
    }

    public void setViewDistance(int value) {
        T.viewDistance.setInteger(instance, value);
    }

    public int getUpdateInterval() {
        return T.updateInterval.getInteger(instance);
    }

    public void setUpdateInterval(int value) {
        T.updateInterval.setInteger(instance, value);
    }

    public int getRaw_xRot() {
        return T.raw_xRot.getInteger(instance);
    }

    public void setRaw_xRot(int value) {
        T.raw_xRot.setInteger(instance, value);
    }

    public int getRaw_yRot() {
        return T.raw_yRot.getInteger(instance);
    }

    public void setRaw_yRot(int value) {
        T.raw_yRot.setInteger(instance, value);
    }

    public int getRaw_headYaw() {
        return T.raw_headYaw.getInteger(instance);
    }

    public void setRaw_headYaw(int value) {
        T.raw_headYaw.setInteger(instance, value);
    }

    public double getXVel() {
        return T.xVel.getDouble(instance);
    }

    public void setXVel(double value) {
        T.xVel.setDouble(instance, value);
    }

    public double getYVel() {
        return T.yVel.getDouble(instance);
    }

    public void setYVel(double value) {
        T.yVel.setDouble(instance, value);
    }

    public double getZVel() {
        return T.zVel.getDouble(instance);
    }

    public void setZVel(double value) {
        T.zVel.setDouble(instance, value);
    }

    public int getTickCounter() {
        return T.tickCounter.getInteger(instance);
    }

    public void setTickCounter(int value) {
        T.tickCounter.setInteger(instance, value);
    }

    public double getPrevX() {
        return T.prevX.getDouble(instance);
    }

    public void setPrevX(double value) {
        T.prevX.setDouble(instance, value);
    }

    public double getPrevY() {
        return T.prevY.getDouble(instance);
    }

    public void setPrevY(double value) {
        T.prevY.setDouble(instance, value);
    }

    public double getPrevZ() {
        return T.prevZ.getDouble(instance);
    }

    public void setPrevZ(double value) {
        T.prevZ.setDouble(instance, value);
    }

    public boolean isSynched() {
        return T.synched.getBoolean(instance);
    }

    public void setSynched(boolean value) {
        T.synched.setBoolean(instance, value);
    }

    public boolean isMobile() {
        return T.isMobile.getBoolean(instance);
    }

    public void setIsMobile(boolean value) {
        T.isMobile.setBoolean(instance, value);
    }

    public int getTimeSinceLocationSync() {
        return T.timeSinceLocationSync.getInteger(instance);
    }

    public void setTimeSinceLocationSync(int value) {
        T.timeSinceLocationSync.setInteger(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.EntityTrackerEntry</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityTrackerEntryClass extends Template.Class<EntityTrackerEntryHandle> {
        @Template.Optional
        public final Template.Constructor.Converted<EntityTrackerEntryHandle> constr_entity_viewDistance_playerViewDistance_updateInterval_isMobile = new Template.Constructor.Converted<EntityTrackerEntryHandle>();
        @Template.Optional
        public final Template.Constructor.Converted<EntityTrackerEntryHandle> constr_entity_viewDistance_updateInterval_isMobile = new Template.Constructor.Converted<EntityTrackerEntryHandle>();

        public final Template.Field.Converted<EntityHandle> tracker = new Template.Field.Converted<EntityHandle>();
        public final Template.Field.Integer viewDistance = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer playerViewDistance = new Template.Field.Integer();
        public final Template.Field.Integer updateInterval = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Long long_xLoc = new Template.Field.Long();
        @Template.Optional
        public final Template.Field.Long long_yLoc = new Template.Field.Long();
        @Template.Optional
        public final Template.Field.Long long_zLoc = new Template.Field.Long();
        @Template.Optional
        public final Template.Field.Integer int_xLoc = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer int_yLoc = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer int_zLoc = new Template.Field.Integer();
        public final Template.Field.Integer raw_xRot = new Template.Field.Integer();
        public final Template.Field.Integer raw_yRot = new Template.Field.Integer();
        public final Template.Field.Integer raw_headYaw = new Template.Field.Integer();
        public final Template.Field.Double xVel = new Template.Field.Double();
        public final Template.Field.Double yVel = new Template.Field.Double();
        public final Template.Field.Double zVel = new Template.Field.Double();
        public final Template.Field.Integer tickCounter = new Template.Field.Integer();
        public final Template.Field.Double prevX = new Template.Field.Double();
        public final Template.Field.Double prevY = new Template.Field.Double();
        public final Template.Field.Double prevZ = new Template.Field.Double();
        public final Template.Field.Boolean synched = new Template.Field.Boolean();
        public final Template.Field.Boolean isMobile = new Template.Field.Boolean();
        public final Template.Field.Integer timeSinceLocationSync = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Converted<List<Entity>> opt_passengers = new Template.Field.Converted<List<Entity>>();
        @Template.Optional
        public final Template.Field.Converted<Entity> opt_vehicle = new Template.Field.Converted<Entity>();
        @Template.Optional
        public final Template.Field.Converted<Map<Player, Boolean>> viewersMap = new Template.Field.Converted<Map<Player, Boolean>>();
        @Template.Optional
        public final Template.Field.Converted<Set<Player>> viewersSet = new Template.Field.Converted<Set<Player>>();

        public final Template.Method<Void> hideForAll = new Template.Method<Void>();
        public final Template.Method.Converted<Void> removeViewer = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> scanPlayers = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> updatePlayer = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<CommonPacket> getSpawnPacket = new Template.Method.Converted<CommonPacket>();

    }

}

