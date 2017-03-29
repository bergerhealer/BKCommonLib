package com.bergerkiller.bukkit.common._unused;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.proxies.ProxyBase;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EntityProxy_unused<T extends Entity> extends ProxyBase<T> implements Entity {

    public EntityProxy_unused(T base) {
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

    @Override
    public String getName() {
        return base.getName();
    }

    @Override
    public void sendMessage(String arg0) {
        base.sendMessage(arg0);
    }

    @Override
    public void sendMessage(String[] arg0) {
        base.sendMessage(arg0);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0) {
        return base.addAttachment(arg0);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
        return base.addAttachment(arg0, arg1);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, String arg1,
            boolean arg2) {
        return base.addAttachment(arg0, arg1, arg2);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, String arg1,
            boolean arg2, int arg3) {
        return base.addAttachment(arg0, arg1, arg2, arg3);
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return base.getEffectivePermissions();
    }

    @Override
    public boolean hasPermission(String arg0) {
        return base.hasPermission(arg0);
    }

    @Override
    public boolean hasPermission(Permission arg0) {
        return base.hasPermission(arg0);
    }

    @Override
    public boolean isPermissionSet(String arg0) {
        return base.isPermissionSet(arg0);
    }

    @Override
    public boolean isPermissionSet(Permission arg0) {
        return base.isPermissionSet(arg0);
    }

    @Override
    public void recalculatePermissions() {
        base.recalculatePermissions();
    }

    @Override
    public void removeAttachment(PermissionAttachment arg0) {
        base.removeAttachment(arg0);
    }

    @Override
    public boolean isOp() {
        return base.isOp();
    }

    @Override
    public void setOp(boolean arg0) {
        base.setOp(arg0);
    }

    @Override
    public String getCustomName() {
        return base.getCustomName();
    }

    @Override
    public boolean isCustomNameVisible() {
        return base.isCustomNameVisible();
    }

    @Override
    public void setCustomName(String arg0) {
        base.setCustomName(arg0);
    }

    @Override
    public void setCustomNameVisible(boolean arg0) {
        base.setCustomNameVisible(arg0);
    }

	@Override
	public Spigot spigot() {
		return base.spigot();
	}

	@Override
	public boolean isGlowing() {
		return base.isGlowing();
	}

	@Override
	public void setGlowing(boolean arg0) {
		base.setGlowing(arg0);
	}
	
	@Override
	public boolean isSilent() {
		return base.isSilent();
	}
    
    public boolean isInvulnerable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setInvulnerable(boolean invulnerable) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	@Override
	public boolean addPassenger(Entity entity) {
		return base.addPassenger(entity);
	}

	@Override
	public boolean addScoreboardTag(String tag) {
		return base.addScoreboardTag(tag);
	}

	@Override
	public List<Entity> getPassengers() {
		return base.getPassengers();
	}

	@Override
	public int getPortalCooldown() {
		return base.getPortalCooldown();
	}

	@Override
	public Set<String> getScoreboardTags() {
		return base.getScoreboardTags();
	}

	@Override
	public boolean hasGravity() {
		return base.hasGravity();
	}

	@Override
	public boolean removePassenger(Entity arg0) {
		return base.removePassenger(arg0);
	}

	@Override
	public boolean removeScoreboardTag(String arg0) {
		return base.removeScoreboardTag(arg0);
	}

	@Override
	public void setGravity(boolean arg0) {
		base.setGravity(arg0);
	}

	@Override
	public void setPortalCooldown(int arg0) {
		base.setPortalCooldown(arg0);
	}

	@Override
	public void setSilent(boolean arg0) {
		base.setSilent(arg0);
	}

	@Override
	public double getWidth() {
	    return base.getWidth();
	}

    @Override
    public double getHeight() {
        return base.getHeight();
    }
}
