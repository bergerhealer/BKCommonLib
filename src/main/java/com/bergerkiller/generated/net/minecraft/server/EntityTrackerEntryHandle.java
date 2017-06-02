package com.bergerkiller.generated.net.minecraft.server;

import org.bukkit.entity.Player;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import java.util.Set;
import org.bukkit.entity.Entity;

public class EntityTrackerEntryHandle extends Template.Handle {
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

    public CommonPacket getSpawnPacket() {
        return T.getSpawnPacket.invoke(instance);
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

    public int getPlayerViewDistance() {
        return T.playerViewDistance.getInteger(instance);
    }

    public void setPlayerViewDistance(int value) {
        T.playerViewDistance.setInteger(instance, value);
    }

    public int getUpdateInterval() {
        return T.updateInterval.getInteger(instance);
    }

    public void setUpdateInterval(int value) {
        T.updateInterval.setInteger(instance, value);
    }

    public long getXLoc() {
        return T.xLoc.getLong(instance);
    }

    public void setXLoc(long value) {
        T.xLoc.setLong(instance, value);
    }

    public long getYLoc() {
        return T.yLoc.getLong(instance);
    }

    public void setYLoc(long value) {
        T.yLoc.setLong(instance, value);
    }

    public long getZLoc() {
        return T.zLoc.getLong(instance);
    }

    public void setZLoc(long value) {
        T.zLoc.setLong(instance, value);
    }

    public int getYRot() {
        return T.yRot.getInteger(instance);
    }

    public void setYRot(int value) {
        T.yRot.setInteger(instance, value);
    }

    public int getXRot() {
        return T.xRot.getInteger(instance);
    }

    public void setXRot(int value) {
        T.xRot.setInteger(instance, value);
    }

    public int getHeadYaw() {
        return T.headYaw.getInteger(instance);
    }

    public void setHeadYaw(int value) {
        T.headYaw.setInteger(instance, value);
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

    public List<Entity> getPassengers() {
        return T.passengers.get(instance);
    }

    public void setPassengers(List<Entity> value) {
        T.passengers.set(instance, value);
    }

    public Set<Player> getViewers() {
        return T.viewers.get(instance);
    }

    public void setViewers(Set<Player> value) {
        T.viewers.set(instance, value);
    }

    public static final class EntityTrackerEntryClass extends Template.Class<EntityTrackerEntryHandle> {
        public final Template.Field.Converted<EntityHandle> tracker = new Template.Field.Converted<EntityHandle>();
        public final Template.Field.Integer viewDistance = new Template.Field.Integer();
        public final Template.Field.Integer playerViewDistance = new Template.Field.Integer();
        public final Template.Field.Integer updateInterval = new Template.Field.Integer();
        public final Template.Field.Long xLoc = new Template.Field.Long();
        public final Template.Field.Long yLoc = new Template.Field.Long();
        public final Template.Field.Long zLoc = new Template.Field.Long();
        public final Template.Field.Integer yRot = new Template.Field.Integer();
        public final Template.Field.Integer xRot = new Template.Field.Integer();
        public final Template.Field.Integer headYaw = new Template.Field.Integer();
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
        public final Template.Field.Converted<List<Entity>> passengers = new Template.Field.Converted<List<Entity>>();
        public final Template.Field.Converted<Set<Player>> viewers = new Template.Field.Converted<Set<Player>>();

        public final Template.Method<Void> hideForAll = new Template.Method<Void>();
        public final Template.Method.Converted<Void> removeViewer = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<CommonPacket> getSpawnPacket = new Template.Method.Converted<CommonPacket>();

    }

}

