package com.bergerkiller.bukkit.common.proxies;

import java.util.List;
import java.util.UUID;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class EntityProxy<T extends Entity> extends ProxyBase<T> implements Entity {

    public EntityProxy(T base) {
        super(base);
    }

    @Override
    public List<MetadataValue> getMetadata(String arg0) {
        return base.getMetadata(arg0);
    }

    @Override
    public boolean hasMetadata(String arg0) {
        return base.hasMetadata(arg0);
    }

    @Override
    public void removeMetadata(String arg0, Plugin arg1) {
        base.removeMetadata(arg0, arg1);
    }

    @Override
    public void setMetadata(String arg0, MetadataValue arg1) {
        base.setMetadata(arg0, arg1);
    }

    @Override
    public boolean eject() {
        return base.eject();
    }

    @Override
    public int getEntityId() {
        return base.getEntityId();
    }

    @Override
    public float getFallDistance() {
        return base.getFallDistance();
    }

    @Override
    public int getFireTicks() {
        return base.getFireTicks();
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return base.getLastDamageCause();
    }

    @Override
    public Location getLocation() {
        return base.getLocation();
    }

    @Override
    public Location getLocation(Location arg0) {
        return base.getLocation(arg0);
    }

    @Override
    public int getMaxFireTicks() {
        return base.getMaxFireTicks();
    }

    @Override
    public List<Entity> getNearbyEntities(double arg0, double arg1, double arg2) {
        return base.getNearbyEntities(arg0, arg1, arg2);
    }

    @Override
    public Entity getPassenger() {
        return base.getPassenger();
    }

    @Override
    public Server getServer() {
        return base.getServer();
    }

    @Override
    public int getTicksLived() {
        return base.getTicksLived();
    }

    @Override
    public EntityType getType() {
        return base.getType();
    }

    @Override
    public UUID getUniqueId() {
        return base.getUniqueId();
    }

    @Override
    public Entity getVehicle() {
        return base.getVehicle();
    }

    @Override
    public Vector getVelocity() {
        return base.getVelocity();
    }

    @Override
    public World getWorld() {
        return base.getWorld();
    }

    @Override
    public boolean isDead() {
        return base.isDead();
    }

    @Override
    public boolean isEmpty() {
        return base.isEmpty();
    }

    @Override
    public boolean isInsideVehicle() {
        return base.isInsideVehicle();
    }

    @Override
    public boolean isValid() {
        return base.isValid();
    }

    @Override
    public boolean leaveVehicle() {
        return base.leaveVehicle();
    }

    @Override
    public void playEffect(EntityEffect arg0) {
        base.playEffect(arg0);
    }

    @Override
    public void remove() {
        base.remove();
    }

    @Override
    public void setFallDistance(float arg0) {
        base.setFallDistance(arg0);
    }

    @Override
    public void setFireTicks(int arg0) {
        base.setFireTicks(arg0);
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent arg0) {
        base.setLastDamageCause(arg0);
    }

    @Override
    public boolean setPassenger(Entity arg0) {
        return base.setPassenger(arg0);
    }

    @Override
    public void setTicksLived(int arg0) {
        base.setTicksLived(arg0);
    }

    @Override
    public void setVelocity(Vector arg0) {
        base.setVelocity(arg0);
    }

    @Override
    public boolean teleport(Location arg0) {
        return base.teleport(arg0);
    }

    @Override
    public boolean teleport(Entity arg0) {
        return base.teleport(arg0);
    }

    @Override
    public boolean teleport(Location arg0, TeleportCause arg1) {
        return base.teleport(arg0, arg1);
    }

    @Override
    public boolean teleport(Entity arg0, TeleportCause arg1) {
        return base.teleport(arg0, arg1);
    }

    @Override
    public boolean isOnGround() {
        return base.isOnGround();
    }

    public org.bukkit.entity.Entity.Spigot spigot() {
        return base.spigot();
    }

    @Override
    public void setCustomName(String string) {
        base.setCustomName(string);
    }

    @Override
    public String getCustomName() {
        return base.getCustomName();
    }

    @Override
    public void setCustomNameVisible(boolean bln) {
        base.setCustomNameVisible(bln);
    }

    @Override
    public boolean isCustomNameVisible() {
        return base.isCustomNameVisible();
    }
}
