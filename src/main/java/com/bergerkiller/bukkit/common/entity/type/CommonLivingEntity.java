package com.bergerkiller.bukkit.common.entity.type;

import net.minecraft.server.v1_8_R1.EntityCreature;
import net.minecraft.server.v1_8_R1.EntityInsentient;
import net.minecraft.server.v1_8_R1.EntityLiving;
import net.minecraft.server.v1_8_R1.GenericAttributes;
import net.minecraft.server.v1_8_R1.Navigation;
import net.minecraft.server.v1_8_R1.PathEntity;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.classes.EntityLivingRef;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import net.minecraft.server.v1_8_R1.PathPoint;

/**
 * A Common Entity implementation for Living Entities
 *
 * @param <T> - type of Living Entity
 */
public class CommonLivingEntity<T extends LivingEntity> extends CommonEntity<T> {

    public CommonLivingEntity(T entity) {
        super(entity);
    }

    /**
     * Gets the height of the living entity's eyes above its Location.
     *
     * @return height of the living entity's eyes above its location
     */
    public double getEyeHeight() {
        return entity.getEyeHeight();
    }

    /**
     * Gets the height of the living entity's eyes above its Location.
     *
     * @param ignoreSneaking if set to true, the effects of sneaking will be
     * ignored
     * @return height of the living entity's eyes above its location
     */
    public double getEyeHeight(boolean ignoreSneaking) {
        return entity.getEyeHeight(ignoreSneaking);
    }

    /**
     * Get a Location detailing the current eye position of the living entity.
     *
     * @return a location at the eyes of the living entity
     */
    public Location getEyeLocation() {
        return entity.getEyeLocation();
    }

    /**
     * Performs ray tracing from the head of this Entity towards where this
     * Entity looks. The maximum interaction distance (5.0) is used. Unlike the
     * default getTargetBlock, this method gets the actual highlighted block by
     * this Living Entity.
     *
     * @return the first Block hit, or null if none was found (AIR)
     */
    public Block getTargetBlock() {
        return getTargetBlock(5.0);
    }

    /**
     * Performs ray tracing from the head of this Entity towards where this
     * Entity looks. Unlike the default getTargetBlock, this method gets the
     * actual highlighted block by this Living Entity.
     *
     * @param maxDistance to ray trace
     * @return the first Block hit, or null if none was found (AIR)
     */
    public Block getTargetBlock(double maxDistance) {
        return WorldUtil.rayTraceBlock(getEyeLocation(), maxDistance);
    }

    /**
     * Gets the inventory with the equipment worn by the living entity.
     *
     * @return the living entity's inventory
     */
    public EntityEquipment getEquipment() {
        return entity.getEquipment();
    }

    /**
     * Gets the maximum possible health level of this Living Entity
     *
     * @return max health
     */
    public double getMaxHealth() {
        return getHandle(EntityLiving.class).getMaxHealth();
    }

    /**
     * Gets the current health level of this Living Entity
     *
     * @return health
     */
    public double getHealth() {
        EntityLiving handle = getHandle(EntityLiving.class);
        return MathUtil.clamp(handle.getHealth(), 0, handle.getMaxHealth());
    }

    /**
     * Sets the current health level of this Living Entity
     *
     * @param health to set to
     */
    public void setHealth(double health) {
        entity.setHealth(health);
    }

    /**
     * Damages this Entity, damage caused by an unknown source
     *
     * @param damage dealt
     */
    public void damage(double damage) {
        entity.damage(damage);
    }

    /**
     * Damages this Entity, damage caused by another Entity
     *
     * @param damage dealt
     * @param damager that caused the damage
     */
    public void damage(double damage, Entity damager) {
        entity.damage(damage, damager);
    }

    /**
     * Gets the movement this Living Entity is performing. It points to the
     * direction the Entity is trying to 'go' to. In the case of Players, this
     * is the WASD movement direction.
     *
     * @return Movement direction
     */
    public Vector getMovement() {
        return MathUtil.getDirection(loc.getYaw(), loc.getPitch()).multiply(getForwardMovement());
    }

    /**
     * Gets the forward movement this Living Entity is performing. This value is
     * negative when moving backwards.
     *
     * @return Forward movement
     */
    public double getForwardMovement() {
        return EntityLivingRef.forwardMovement.get(getHandle(EntityLiving.class));
    }

    /**
     * Sets the path finding radius.
     *
     * @param range of path finding
     */
    public void setPathfindingRange(double range) {
        EntityLiving nmsEntity = CommonNMS.getNative(entity);
        nmsEntity.getAttributeInstance(GenericAttributes.b).setValue(range);
    }

    /**
     * Gets the path finding range
     *
     * @return range of path finding
     */
    public double getPathfindingRange() {
        EntityLiving nmsEntity = CommonNMS.getNative(entity);
        return nmsEntity.getAttributeInstance(GenericAttributes.b).getValue();
    }

    /**
     * Move to a location with pathfinding
     *
     * @param location to move to
     * @param speed to move with
     */
    public void moveTo(Location location) {
        this.moveTo(location, GenericAttributes.d.b());
    }

    /**
     * Move to an entity with pathfinding
     *
     * @param entity to find
     */
    public void moveTo(Entity entity) {
        this.moveTo(entity, GenericAttributes.d.b());
    }

    /**
     * Move to a location with pathfinding
     *
     * @param location to move to
     * @param speed to move with
     */
    public void moveTo(Location location, double speed) {
        EntityLiving nmsEntity = CommonNMS.getNative(this.entity);
        if (nmsEntity instanceof EntityInsentient) {
            Navigation navigation = (Navigation) EntityLivingRef.getNavigation.invoke(nmsEntity);
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();
            if (!navigation.a(x, y, z, speed)) {
                int dx = MathUtil.floor(x);
                int dy = (int) y;
                int dz = MathUtil.floor(z);
                PathEntity path = navigation.a(dx, dy, dz);//nmsEntity.world.a(nmsEntity, dx, dy, dz, (float) this.getPathfindingRange(), true, false, false, true);
                this.moveWithPath(path, speed);
            }
        }
    }

    /**
     * Move to an entity with pathfinding
     *
     * @param entity to find
     * @param speed to move with
     */
    public void moveTo(Entity entity, double speed) {
        EntityLiving nmsEntity = CommonNMS.getNative(this.entity);
        net.minecraft.server.v1_8_R1.Entity nmsTargetEntity = CommonNMS.getNative(entity);
        if (nmsEntity instanceof EntityInsentient) {
            Navigation navigation = (Navigation) EntityLivingRef.getNavigation.invoke(nmsEntity);
            if (!navigation.a(nmsTargetEntity, speed)) {
            	PathEntity path = null;//findPath(nmsEntity, nmsTargetEntity, (float) this.getPathfindingRange(), true, false, false, true);
				//nmsEntity.world.
                this.moveWithPath(path, speed);
            }
        }
    }

    private void moveWithPath(PathEntity path, double speed) {
        EntityLiving nmsEntity = CommonNMS.getNative(entity);
        if (nmsEntity instanceof EntityInsentient) {
            if (nmsEntity instanceof EntityCreature) {
            	EntityCreature creature = ((EntityCreature) nmsEntity);
				creature.move(path.c().a, path.c().b, path.c().c);
            }

            Navigation navigation = (Navigation) EntityLivingRef.getNavigation.invoke(nmsEntity);
            navigation.a(path, speed);
        }
    }
}
