package com.bergerkiller.bukkit.common.bases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftMinecart;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingList;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.reflection.classes.EntityMinecartRef;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;

import net.minecraft.server.v1_4_R1.AxisAlignedBB;
import net.minecraft.server.v1_4_R1.Block;
import net.minecraft.server.v1_4_R1.DamageSource;
import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityHuman;
import net.minecraft.server.v1_4_R1.EntityItem;
import net.minecraft.server.v1_4_R1.EntityLiving;
import net.minecraft.server.v1_4_R1.EntityMinecart;
import net.minecraft.server.v1_4_R1.ItemStack;
import net.minecraft.server.v1_4_R1.NBTTagCompound;
import net.minecraft.server.v1_4_R1.Vec3D;

public class EntityMinecartBase extends EntityMinecart {
	/**
	 * This field (contained in the super class) should not be used, use getPassenger()/setPassenger() instead
	 */
	@Deprecated
	public final byte passenger = 0;

	/**
	 * This field (contained in the super class) is unsafe, as types can change, use getType/setType instead.
	 * These use conversion systems in BKCommonLib to always support the latest materials.
	 */
	@Deprecated
	public final byte type = 0;

	public EntityMinecartBase(org.bukkit.World world) {
		super(CommonNMS.getNative(world));
	}

	public EntityMinecartBase(org.bukkit.World world, double locX, double locY, double locZ, Material type) {
		super(CommonNMS.getNative(world), locX, locY, locZ, Conversion.toMinecartTypeId.convert(type));
	}

	/**
	 * Gets the type of minecart
	 * 
	 * @return minecart type
	 */
	public Material getType() {
		return EntityMinecartRef.type.get(this);
	}

	/**
	 * Sets the type of minecart this is
	 * 
	 * @param type of minecart
	 */
	public void setType(Material type) {
		EntityMinecartRef.type.set(this, type);
	}

	/**
	 * Checks whether this Minecart can be entered and ridden by living entities.
	 * This is similar to {@link isRegularMinecart()}, but supports new future enterable carts.
	 * 
	 * @return True if it can be ridden, False if not
	 */
	public boolean canBeRidden() {
		return super.type == 0;
	}

	/**
	 * Checks whether this Minecart is the default minecart type
	 * 
	 * @return True if it is the default type, False if not
	 */
	public boolean isRegularMinecart() {
		return super.type == 0;
	}

	/**
	 * Checks whether this Minecart contains a chest with items and can be opened by players
	 * 
	 * @return True if it is a storage minecart, False if not
	 */
	public boolean isStorageCart() {
		return super.type == 1;
	}

	/**
	 * Checks whether this Minecart contains a furnace which powers the train using coal
	 * 
	 * @return True if it is a powered minecart, False if not
	 */
	public boolean isPoweredCart() {
		return super.type == 2;
	}

	/**
	 * Gets the world this Minecart is in
	 * 
	 * @return World
	 */
	public org.bukkit.World getWorld() {
		return CommonNMS.getWorld(world);
	}

	/**
	 * Gets all the drops to spawn when this Minecart is broken.
	 * The default implementation (break up into parts) is executed unless overridden.
	 * 
	 * @return items to spawn
	 */
	public List<org.bukkit.inventory.ItemStack> getDrops() {
		final List<org.bukkit.inventory.ItemStack> drops = new ArrayList<org.bukkit.inventory.ItemStack>(2);
		drops.add(new org.bukkit.inventory.ItemStack(Material.MINECART, 1));
		if (this.isStorageCart()) {
			drops.add(new org.bukkit.inventory.ItemStack(Material.CHEST, 1));
		} else if (this.isPoweredCart()) {
			drops.add(new org.bukkit.inventory.ItemStack(Material.FURNACE, 1));
		}
		return drops;
	}

	/**
	 * @deprecated: use {@link setSmoking()} instead
	 */
	@Override
	@Deprecated
	public final void e(boolean b) {
		this.setSmoking(b);
	}

	/**
	 * @deprecated: use {@link isSmoking()} instead
	 */
	@Override
	@Deprecated
	public final boolean h() {
		return this.isSmoking();
	}

	/**
	 * @deprecated: use {@link onTick()} instead
	 */
	@Override
	@Deprecated
	public final void j_() {
		try {
			this.onTick();
		} catch (Throwable t) {
			Bukkit.getLogger().severe("En error occurred while performing Minecart tick:");
			t.printStackTrace();
		}
	}

	/**
	 * @deprecated: use {@link setShakingDirection()} instead
	 */
	@Override
	@Deprecated
	public final void i(int i) {
		this.setShakingDirection(i);
	}

	/**
	 * @deprecated: use {@link getShakingDirection()} instead
	 */
	@Override
	@Deprecated
	public final int k() {
		return this.getShakingDirection();
	}

	/**
	 * @deprecated: use {@link setShakingFactor()} instead
	 */
	@Override
	@Deprecated
	public final void h(int i) {
		this.setShakingFactor(0);
	}

	/**
	 * @deprecated: use {@link getShakingFactor()} instead
	 */
	@Override
	@Deprecated
	public final int j() {
		return this.getShakingFactor();
	}

	/**
	 * @deprecated: use {@link markVelocityChanged()} instead
	 */
	@Override
	@Deprecated
	public final void K() {
		this.markVelocityChanged();
	}
	
	/**
	 * @deprecated: use {@link onEntityDamage(Entity, int)} instead
	 */
	@Override
	@Deprecated
	public boolean damageEntity(DamageSource source, int i) {
		return this.onEntityDamage(CommonNMS.getEntity(source.getEntity()), i);
	}

	/**
	 * @deprecated: use {@link dropItem()} instead
	 */
	@Override
	@Deprecated
	public final EntityItem a(ItemStack item, float f) {
		return super.a(item, f);
	}

	/**
	 * @deprecated: use {@link onInteract(HumanEntity)} instead
	 */
	@Override
	@Deprecated
	public boolean a(EntityHuman entityhuman) {
		return this.onInteract(CommonNMS.getEntity(entityhuman, HumanEntity.class));
	}

	/**
	 * Called when a human entity interacts (clicks) on this Minecart.
	 * 
	 * @param human that interacted
	 * @return True if interaction logic occurred, False if not
	 */
	public boolean onInteract(HumanEntity human) {
		return super.a(CommonNMS.getNative(human));
	}

	/**
	 * @deprecated: use {@link onLoad(CommonTagCompound)} instead
	 */
	@Override
	@Deprecated
	public void d(NBTTagCompound arg0) {
		this.onLoad((CommonTagCompound) CommonTag.create(arg0));
	}

	/**
	 * Performs the entity loading logic
	 * 
	 * @param data to load from
	 */
	public void onLoad(CommonTagCompound data) {
		super.d((NBTTagCompound) data.getHandle());
	}

	/**
	 * @deprecated: use {@link onSave(tag)} instead
	 */
	@Override
	@Deprecated
	public void b(NBTTagCompound arg0) {
		this.onSave((CommonTagCompound) CommonTag.create(arg0));
	}

	/**
	 * Performs the entity saving logic
	 * 
	 * @param data to save to
	 */
	public void onSave(CommonTagCompound data) {
		super.b((NBTTagCompound) data.getHandle());
	}

	/**
	 * Gets the amount of fuel ticks remaining for this (Powered) Minecart
	 * 
	 * @return fuel ticks
	 */
	public int getFuel() {
		return EntityMinecartRef.fuel.get(this);
	}

	/**
	 * Sets the amount of fuel ticks remaining for this (Powered) Minecart
	 * 
	 * @param fuel
	 *            ticks to set to
	 */
	public void setFuel(int fuel) {
		EntityMinecartRef.fuel.set(this, fuel);
	}

	/**
	 * Performs the tick logic for this Entity
	 */
	public void onTick() {
		super.j_();
	}
	
	/**
	 * Called when this minecart is damaged by something
	 * 
	 * @param entity that damaged this minecart, null if the source is unknown
	 * @param damage dealt to this minecart
	 * @return True if the damage was handled, False if not
	 */
	public boolean onEntityDamage(org.bukkit.entity.Entity damager, int damage) {
		DamageSource source;
		if (damager instanceof Player) {
			source = DamageSource.playerAttack(CommonNMS.getNative(damager, EntityHuman.class));
		} else if (damager instanceof LivingEntity) {
			source = DamageSource.mobAttack(CommonNMS.getNative(damager, EntityLiving.class));
		} else {
			source = DamageSource.GENERIC;
		}
		return super.damageEntity(source, damage);
	}

	/**
	 * Gets the position to align this Minecart on a slope<br>
	 * Note: not called internally, not suitable for overriding unless manually called
	 * 
	 * @param x - coordinate of the old position
	 * @param y - coordinate of the old position
	 * @param z - coordinate of the old position
	 * @return new Vector for the new sloped position, or null if none possible (not a sloped rail)
	 */
	public Vector getSlopedPosition(double x, double y, double z) {
		Vec3D vector = super.a(x, y, z);
		if (vector == null) {
			return null;
		}
		return new Vector(vector.c, vector.d, vector.e);
	}

	public void markVelocityChanged() {
		super.K();
	}

	public boolean isSmoking() {
		return super.h();
	}

	public void setSmoking(boolean smoking) {
		super.e(smoking);
	}

	public void setShakingDirection(int direction) {
		super.i(direction);
	}

	public int getShakingDirection() {
		return super.k();
	}

	public void setShakingFactor(int factor) {
		super.h(factor);
	}

	public int getShakingFactor() {
		return super.j();
	}

	public Item dropItem(Material material, int amount, float force) {
		return CommonNMS.getItem(super.a(material.getId(), amount, force));
	}

	public Item dropItem(org.bukkit.inventory.ItemStack item, float force) {
		return CommonNMS.getItem(super.a(CommonNMS.getNative(item), force));
	}

	/**
	 * Checks whether this Minecart has a passenger
	 * 
	 * @return True if it has a passenger, False if not
	 */
	public boolean hasPassenger() {
		return super.passenger != null;
	}

	/**
	 * Gets the current passenger in this Minecart, null if there is none
	 * 
	 * @return the passenger entity
	 */
	public org.bukkit.entity.Entity getPassenger() {
		return Conversion.toEntity.convert(super.passenger);
	}

	/**
	 * Sets the passenger of this Minecart.
	 * Use null to clear the passenger (eject)
	 * 
	 * @param passenger to set to
	 */
	public void setPassenger(org.bukkit.entity.Entity passenger) {
		this.getEntity().setPassenger(passenger);
	}

	public Inventory getInventory() {
		return Conversion.toInventory.convert(this);
	}

	/**
	 * Obtains the Minecart Bukkit entity
	 * 
	 * @return Minecart Bukkit entity
	 */
	public Minecart getEntity() {
		return (Minecart) super.getBukkitEntity();
	}

	/**
	 * @deprecated: Use getEntity() instead (issues with CraftBukkit type being exposed)
	 */
	@Override
	@Deprecated
	public CraftMinecart getBukkitEntity() {
		return (CraftMinecart) super.getBukkitEntity();
	}

	/**
	 * @deprecated: Use getItems() instead (issues with NMS type being exposed)
	 */
	@Override
	@Deprecated
	public ItemStack[] getContents() {
		return super.getContents();
	}

	/**
	 * @deprecated: Use Bukkit versions or setPassenger instead.
	 * Also, a minecart can not be a passenger of a vehicle.
	 */
	@Override
	@Deprecated
	public void setPassengerOf(Entity entity) {
		super.setPassengerOf(entity);
	}

	/**
	 * Gets all the items contained in this Minecart (for storage minecarts)
	 * 
	 * @return all items contained
	 */
	public List<org.bukkit.inventory.ItemStack> getItems() {
		return new ConvertingList<org.bukkit.inventory.ItemStack>(Arrays.asList(super.getContents()), ConversionPairs.itemStack);
	}

	/**
	 * Handles the collision of this minecart with another Entity
	 * 
	 * @param e entity with which is collided
	 * @return True if collision is allowed, False if it is ignored
	 */
	public boolean onEntityCollision(Entity e) {
		return true;
	}

	/**
	 * Handles the collision of this minecart with a Block
	 * 
	 * @param block with which this minecart collided
	 * @param hitFace of the block that the minecart hit
	 * @return True if collision is allowed, False if it is ignored
	 */
	public boolean onBlockCollision(org.bukkit.block.Block block, BlockFace hitFace) {
		return true;
	}

	/**
	 * Calls onBlockCollision and onEntityCollision on all elements in the list, and filters it
	 */
	@SuppressWarnings("unchecked")
	private void filterCollisionList(List<AxisAlignedBB> list) {
		try {
			// Shortcut to prevent unneeded logic
			if (list.isEmpty()) {
				return;
			}
			List<Entity> entityList = this.world.entityList;

			Iterator<AxisAlignedBB> iter = list.iterator();
			AxisAlignedBB a;
			boolean isBlock;
			double dx, dy, dz;
			BlockFace dir;
			while (iter.hasNext()) {
				a = iter.next();
				isBlock = true;
				for (Entity e : entityList) {
					if (e.boundingBox == a) {
						if (!onEntityCollision(e))
							iter.remove();
						isBlock = false;
						break;
					}
				}
				if (isBlock) {
					org.bukkit.block.Block block = this.world.getWorld().getBlockAt(MathUtil.floor(a.a), MathUtil.floor(a.b), MathUtil.floor(a.c));

					dx = this.locX - block.getX() - 0.5;
					dy = this.locY - block.getY() - 0.5;
					dz = this.locZ - block.getZ() - 0.5;
					if (Math.abs(dx) < 0.1 && Math.abs(dz) < 0.1) {
						dir = dy >= 0.0 ? BlockFace.UP : BlockFace.DOWN;
					} else {
						dir = FaceUtil.getDirection(dx, dz, false);
					}
					if (!this.onBlockCollision(block, dir)) {
						iter.remove();
					}
				}
			}
		} catch (ConcurrentModificationException ex) {
			Bukkit.getLogger().warning("Another plugin is interacting with the world entity list from another thread, please check your plugins!");
		}
	}

	/**
	 * Performs basic collision logic with nearby minecarts, pushing them aside
	 */
	public void handleCollision() {
		for (org.bukkit.entity.Entity entity : WorldUtil.getNearbyEntities(this.getBukkitEntity(), 0.2, 0, 0.2)) {
			if (entity instanceof Minecart && entity != this.getPassenger()) {
				EntityUtil.doCollision(entity, this.getBukkitEntity());
			}
		}
	}

	/**
	 * Cloned move function and updated to include block/entity collision events
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void move(double d0, double d1, double d2) {
		if (this.Y) {
			this.boundingBox.d(d0, d1, d2);
			this.locX = (this.boundingBox.a + this.boundingBox.d) / 2.0D;
			this.locY = (this.boundingBox.b + (double) this.height) - (double) this.W;
			this.locZ = (this.boundingBox.c + this.boundingBox.f) / 2.0D;
		} else {
			this.W *= 0.4F;
			double d3 = this.locX;
			double d4 = this.locY;
			double d5 = this.locZ;
			if (this.J) {
				this.J = false;
				d0 *= 0.25D;
				d1 *= 0.05D;
				d2 *= 0.25D;
				this.motX = 0.0D;
				this.motY = 0.0D;
				this.motZ = 0.0D;
			}
			double d6 = d0;
			double d7 = d1;
			double d8 = d2;
			AxisAlignedBB axisalignedbb = this.boundingBox.clone();
			List<AxisAlignedBB> list = this.world.getCubes(this, boundingBox.a(d0, d1, d2));

			// ================================================
			filterCollisionList(list);
			// ================================================

			// Collision testing using Y
			for (AxisAlignedBB aabb : list) {
				d1 = aabb.b(this.boundingBox, d1);
			}
			this.boundingBox.d(0.0D, d1, 0.0D);
			if (!this.K && d7 != d1) {
				d2 = 0.0D;
				d1 = 0.0D;
				d0 = 0.0D;
			}

			// Collision testing using X
			boolean flag1 = this.onGround || d7 != d1 && d7 < 0.0D;
			for (AxisAlignedBB aabb : list) {
				d0 = aabb.a(this.boundingBox, d0);
			}
			this.boundingBox.d(d0, 0.0D, 0.0D);
			if (!this.K && d6 != d0) {
				d2 = 0.0D;
				d1 = 0.0D;
				d0 = 0.0D;
			}

			// Collision testing using Z
			for (AxisAlignedBB aabb : list) {
				d2 = aabb.c(this.boundingBox, d2);
			}
			this.boundingBox.d(0.0D, 0.0D, d2);
			if (!this.K && d8 != d2) {
				d2 = 0.0D;
				d1 = 0.0D;
				d0 = 0.0D;
			}

			double d10;
			double d11;
			double d12;

			if (this.X > 0.0F && flag1 && this.W < 0.05F && (d6 != d0 || d8 != d2)) {
				d10 = d0;
				d11 = d1;
				d12 = d2;
				d0 = d6;
				d1 = (double) this.X;
				d2 = d8;

				AxisAlignedBB axisalignedbb1 = this.boundingBox.clone();
				this.boundingBox.c(axisalignedbb);

				list = world.getCubes(this, this.boundingBox.a(d6, d1, d8));

				// ================================================
				filterCollisionList(list);
				// ================================================

				for (AxisAlignedBB aabb : list) {
					d1 = aabb.b(this.boundingBox, d1);
				}
				this.boundingBox.d(0.0D, d1, 0.0D);
				if (!this.K && d7 != d1) {
					d2 = 0.0D;
					d1 = 0.0D;
					d0 = 0.0D;
				}

				for (AxisAlignedBB aabb : list) {
					d0 = aabb.a(this.boundingBox, d0);
				}
				this.boundingBox.d(d0, 0.0D, 0.0D);
				if (!this.K && d6 != d0) {
					d2 = 0.0D;
					d1 = 0.0D;
					d0 = 0.0D;
				}

				for (AxisAlignedBB aabb : list) {
					d2 = aabb.c(this.boundingBox, d2);
				}
				this.boundingBox.d(0.0D, 0.0D, d2);
				if (!this.K && d8 != d2) {
					d2 = 0.0D;
					d1 = 0.0D;
					d0 = 0.0D;
				}

				if (!this.K && d7 != d1) {
					d2 = 0.0D;
					d1 = 0.0D;
					d0 = 0.0D;
				} else {
					d1 = (double) -this.X;
					for (int k = 0; k < list.size(); k++) {
						d1 = list.get(k).b(this.boundingBox, d1);
					}
					this.boundingBox.d(0.0D, d1, 0.0D);
				}
				if (d10 * d10 + d12 * d12 >= d0 * d0 + d2 * d2) {
					d0 = d10;
					d1 = d11;
					d2 = d12;
					this.boundingBox.c(axisalignedbb1);
				} else {
					double d13 = this.boundingBox.b - (double) (int) this.boundingBox.b;
					if (d13 > 0.0D) {
						this.W = (float) ((double) this.W + d13 + 0.01D);
					}
				}
			}

			this.locX = (this.boundingBox.a + this.boundingBox.d) / 2D;
			this.locY = (this.boundingBox.b + (double) this.height) - (double) this.W;
			this.locZ = (this.boundingBox.c + this.boundingBox.f) / 2D;
			this.positionChanged = d6 != d0 || d8 != d2;
			this.G = d7 != d1;
			this.onGround = d7 != d1 && d7 < 0.0D;
			this.H = positionChanged || this.G;
			a(d1, this.onGround);

			if (d7 != d1) {
				this.motY = 0.0D;
			}

			// ========TrainCarts edit: Math.abs check to prevent collision
			// slowdown=====
			if (d6 != d0) {
				if (Math.abs(motX) > Math.abs(motZ)) {
					this.motX = 0.0D;
				}
			}
			if (d8 != d2) {
				if (Math.abs(motZ) > Math.abs(motX)) {
					this.motZ = 0.0D;
				}
			}
			// ===========================================================================

			d10 = this.locX - d3;
			d11 = this.locY - d4;
			d12 = this.locZ - d5;
			if (positionChanged) {
				Vehicle vehicle = (Vehicle) CommonNMS.getEntity(this);
				org.bukkit.block.Block block = world.getWorld().getBlockAt(MathUtil.floor(locX), MathUtil.floor(locY - (double) height), MathUtil.floor(locZ));
				if (d6 > d0) {
					block = block.getRelative(BlockFace.EAST);
				} else if (d6 < d0) {
					block = block.getRelative(BlockFace.WEST);
				} else if (d8 > d2) {
					block = block.getRelative(BlockFace.SOUTH);
				} else if (d8 < d2) {
					block = block.getRelative(BlockFace.NORTH);
				}
				VehicleBlockCollisionEvent event = new VehicleBlockCollisionEvent(vehicle, block);
				world.getServer().getPluginManager().callEvent(event);
			}

			if (this.f_() && this.vehicle == null) {
				int i = MathUtil.floor(this.locX);
				int j = MathUtil.floor(this.locY - 0.2D - (double) this.height);
				int k = MathUtil.floor(this.locZ);
				int l = this.world.getTypeId(i, j, k);

				if (l == 0 && this.world.getTypeId(i, j - 1, k) == Material.FENCE.getId()) {
					l = this.world.getTypeId(i, j - 1, k);
				}

				if (l != Material.LADDER.getId()) {
					d11 = 0.0D;
				}

				this.Q = (float) ((double) this.Q + Math.sqrt(d10 * d10 + d12 * d12) * 0.6D);
				this.R = (float) ((double) this.R + Math.sqrt(d10 * d10 + d11 * d11 + d12 * d12) * 0.6D);
				if (this.R > (float) this.c && l > 0) {
					this.c = (int) this.R + 1;
					if (this.H()) {
						float f = (float) Math.sqrt(this.motX * this.motX * 0.20000000298023224D + this.motY * this.motY + this.motZ * this.motZ * 0.20000000298023224D) * 0.35F;

						if (f > 1.0F) {
							f = 1.0F;
						}

						this.world.makeSound(this, "liquid.swim", f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
					}

					this.a(i, j, k, l);
					Block.byId[k].b(this.world, i, j, k, this);
				}
			}

			this.D(); // Handle block collisions

			// Fire tick calculation (check using block collision)
			boolean flag2 = this.G();
			if (this.world.e(boundingBox.shrink(0.001D, 0.001D, 0.001D))) {
				this.burn(1);
				if (!flag2) {
					this.fireTicks++;
					if (this.fireTicks <= 0) {
						EntityCombustEvent event = new EntityCombustEvent(CommonNMS.getEntity(this), 8);
						this.world.getServer().getPluginManager().callEvent(event);
						if (!event.isCancelled()) {
							this.setOnFire(event.getDuration());
						}
					} else {
						this.setOnFire(8);
					}
				}
			} else if (this.fireTicks <= 0) {
				this.fireTicks = -this.maxFireTicks;
			}
			if (flag2 && this.fireTicks > 0) {
				this.world.makeSound(this, "random.fizz", 0.7F, 1.6F + (random.nextFloat() - random.nextFloat()) * 0.4F);
				this.fireTicks = -this.maxFireTicks;
			}
		}
	}
}
