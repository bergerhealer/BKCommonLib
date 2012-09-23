package com.bergerkiller.bukkit.common.utils;

import java.util.List;
import java.util.UUID;

import net.minecraft.server.Chunk;
import net.minecraft.server.EntityCreature;
import net.minecraft.server.EntityFallingBlock;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EntityMinecart;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntityTrackerEntry;
import net.minecraft.server.IAnimal;
import net.minecraft.server.IMonster;
import net.minecraft.server.IntHashMap;
import net.minecraft.server.MathHelper;
import net.minecraft.server.NPC;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.avaje.ebeaninternal.server.deploy.BeanDescriptor.EntityType;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;

@SuppressWarnings("deprecation")
public class EntityUtil {

	/*
	 * Entity getters
	 */
	public static EntityItem getNative(Item item) {
		return getNative(item, EntityItem.class);
	}

	public static EntityMinecart getNative(Minecart m) {
		return getNative(m, EntityMinecart.class);
	}

	public static EntityPlayer getNative(Player p) {
		return getNative(p, EntityPlayer.class);
	}

	public static <T extends net.minecraft.server.Entity> T getNative(Entity e, Class<T> type) {
		net.minecraft.server.Entity ee = getNative(e);
		if (ee != null) {
			try {
				return type.cast(ee);
			} catch (ClassCastException ex) {
			}
		}
		return null;
	}

	public static net.minecraft.server.Entity getNative(Entity e) {
		return ((CraftEntity) e).getHandle();
	}

	public static <T extends Entity> T getEntity(World world, UUID uid, Class<T> type) {
		Entity e = getEntity(world, uid);
		if (e != null) {
			try {
				return type.cast(e);
			} catch (ClassCastException ex) {
			}
		}
		return null;
	}

	public static Entity getEntity(World world, UUID uid) {
		net.minecraft.server.Entity e = getEntity(WorldUtil.getNative(world), uid);
		return e == null ? null : e.getBukkitEntity();
	}

	@SuppressWarnings("unchecked")
	public static net.minecraft.server.Entity getEntity(net.minecraft.server.World world, UUID uid) {
		for (net.minecraft.server.Entity e : (List<net.minecraft.server.Entity>) world.entityList) {
			if (e.uniqueId.equals(uid))
				return e;
		}
		return null;
	}

	/**
	 * Replaces a given Entity with another Entity<br>
	 * The entity is not respawned to the clients!
	 * 
	 * @param toReplace Entity, which will be removed
	 * @param with Entity, which will be added in its place
	 */
	public static void setEntity(net.minecraft.server.Entity toreplace, net.minecraft.server.Entity with) {
		setEntity(toreplace, with, WorldUtil.getTrackerEntry(toreplace));
	}

	/**
	 * Replaces a given Entity with another Entity<br>
	 * The entity is not respawned to the clients!
	 * 
	 * @param toreplace Entity, which will be removed
	 * @param with Entity, which will be added in its place
	 * @param tracker to use for the new entity
	 */
	@SuppressWarnings("unchecked")
	public static void setEntity(final net.minecraft.server.Entity toreplace, final net.minecraft.server.Entity with, EntityTrackerEntry tracker) {
		// transfer important information
		with.locX = toreplace.locX;
		with.locY = toreplace.locY;
		with.locZ = toreplace.locZ;
		with.ah = toreplace.ah;
		with.ai = toreplace.ai;
		with.aj = toreplace.aj;
		with.world = toreplace.world;
		with.id = toreplace.id;
		toreplace.dead = true;
		with.dead = false;
		// Bukkit entity
		EntityRef.bukkitEntity.transfer(toreplace, with);
		((CraftEntity) with.getBukkitEntity()).setHandle(with);
		// Passenger
		if (toreplace.passenger != null) {
			toreplace.passenger.setPassengerOf(with);
		}

		// make sure the chunk is loaded prior to swapping
		// this may cause the chunk unload to be delayed one tick
		Chunk chunk = toreplace.world.chunkProvider.getChunkAt(toreplace.ah, toreplace.aj);

		// replace the entity in the world
		List<net.minecraft.server.Entity> worldEntities = WorldUtil.getEntities(toreplace.world);
		for (int i = 0; i < worldEntities.size(); i++) {
			if (worldEntities.get(i).id == toreplace.id) {
				toreplace.world.entityList.set(i, with);
				break;
			}
		}

		// replace the entity in the 'entities by id' map
		final IntHashMap entitiesById = WorldServerRef.entitiesById.get(toreplace.world);
		if (entitiesById.d(toreplace.id) == null) {
			CommonUtil.nextTick(new Runnable() {
				public void run() {
					entitiesById.a(toreplace.id, with);
				}
			});
		} else {
			entitiesById.a(toreplace.id, with);
		}

		// replace the entity in the chunk
		int chunkY = toreplace.ai;
		if (!replaceInChunk(chunk, chunkY, toreplace, with)) {
			for (int y = 0; y < chunk.entitySlices.length; y++) {
				if (y != chunkY && replaceInChunk(chunk, y, toreplace, with)) {
					break;
				}
			}
		}

		// put the new entity tracker
		tracker.tracker = with;
		WorldUtil.setTrackerEntry(toreplace, tracker);
	}

	@SuppressWarnings({"unchecked"})
	private static boolean replaceInChunk(Chunk chunk, int chunkY, net.minecraft.server.Entity toreplace, net.minecraft.server.Entity with) {
		List<net.minecraft.server.Entity> list = chunk.entitySlices[chunkY];
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).id == toreplace.id) {
				list.set(i, with);
				chunk.m = true; //set invalid
				return true;
			}
		}
		return false;
	}

	/*
	 * States
	 */
	public static final String[] animalNames = new String[] { "cow", "pig", "sheep", "chicken", "wolf", "squid", "snowman", "mushroomcow", "ocelot", "snowman" };
	public static final String[] monsterNames = new String[] { "creeper", "skeleton", "zombie", "slime", "skeleton", "pigzombie", "spider", "giant", "ghast", "enderman", "cavespider", "enderdragon",
			"blaze", "magmacube", "silverfish" };
	public static final String[] npcNames = new String[] { "villager", "irongolem" };

	public static boolean isMob(Entity entity) {
		return isMob(getNative(entity));
	}

	public static boolean isAnimal(Entity entity) {
		return isAnimal(getNative(entity));
	}

	public static boolean isMonster(Entity entity) {
		return isMonster(getNative(entity));
	}

	public static boolean isNPC(Entity entity) {
		return isNPC(getNative(entity));
	}

	public static boolean isMob(net.minecraft.server.Entity entity) {
		return entity instanceof EntityCreature;
	}

	public static boolean isNPC(net.minecraft.server.Entity entity) {
		return entity instanceof NPC;
	}

	public static boolean isAnimal(net.minecraft.server.Entity entity) {
		return entity instanceof IAnimal;
	}

	public static boolean isMonster(net.minecraft.server.Entity entity) {
		return entity instanceof IMonster;
	}

	public static boolean isMob(EntityType type) {
		return isMob(getName(type));
	}

	public static boolean isMob(CreatureType type) {
		return isMob(getName(type));
	}

	public static boolean isAnimal(CreatureType type) {
		return isAnimal(getName(type));
	}

	public static boolean isMonster(CreatureType type) {
		return isMonster(getName(type));
	}

	public static boolean isMob(String name) {
		return isAnimal(name) || isMonster(name) || isNPC(name);
	}

	public static boolean isAnimal(String name) {
		return StringUtil.isIn(name, animalNames);
	}

	public static boolean isMonster(String name) {
		return StringUtil.isIn(name, monsterNames);
	}

	public static boolean isNPC(String name) {
		return StringUtil.isIn(name, npcNames);
	}

	public static String getName(Entity entity) {
		return getName(getNative(entity));
	}

	public static String getName(EntityType type) {
		return type.toString().toLowerCase().replace("_", "");
	}

	public static String getName(CreatureType type) {
		return type.toString().toLowerCase().replace("_", "");
	}

	public static String getName(net.minecraft.server.Entity entity) {
		if (entity == null)
			return "";
		if (entity instanceof EntityItem) {
			Material mat = Material.getMaterial(((EntityItem) entity).itemStack.id);
			return mat == null ? "item" : "item" + mat.toString().toLowerCase();
		} else if (entity instanceof EntityFallingBlock) {
			Material mat = Material.getMaterial(((EntityFallingBlock) entity).id);
			return mat == null ? "falling" : "falling" + mat.toString().toLowerCase();
		} else if (entity instanceof EntityMinecart) {
			return "minecart";
		} else {
			String name = entity.getClass().getSimpleName().toLowerCase();
			if (name.startsWith("entity"))
				name = name.substring(6);
			if (name.contains("tnt"))
				return "tnt";
			return name;
		}
	}

	/*
	 * Is near something?
	 */
	public static boolean isNearChunk(Entity entity, final int cx, final int cz, final int chunkview) {
		return isNearChunk(getNative(entity), cx, cz, chunkview);
	}

	public static boolean isNearChunk(net.minecraft.server.Entity entity, final int cx, final int cz, final int chunkview) {
		if (Math.abs(MathUtil.locToChunk(entity.locX) - cx) > chunkview)
			return false;
		if (Math.abs(MathUtil.locToChunk(entity.locZ) - cz) > chunkview)
			return false;
		return true;
	}

	public static boolean isNearBlock(Entity entity, final int bx, final int bz, final int blockview) {
		return isNearBlock(getNative(entity), bx, bz, blockview);
	}

	public static boolean isNearBlock(net.minecraft.server.Entity entity, final int bx, final int bz, final int blockview) {
		if (Math.abs(MathHelper.floor(entity.locX) - bx) > blockview)
			return false;
		if (Math.abs(MathHelper.floor(entity.locZ) - bz) > blockview)
			return false;
		return true;
	}

	/*
	 * Teleport
	 */
	@Deprecated
	public static boolean teleport(final Plugin plugin, Entity entity, final Location to) {
		return teleport(getNative(entity), to);
	}

	@Deprecated
	public static boolean teleport(final Plugin plugin, final net.minecraft.server.Entity entity, final Location to) {
		return teleport(entity, to);
	}

	public static boolean teleport(Entity entity, final Location to) {
		return teleport(getNative(entity), to);
	}

	public static boolean teleport(final net.minecraft.server.Entity entity, final Location to) {
		WorldServer newworld = ((CraftWorld) to.getWorld()).getHandle();
		WorldUtil.loadChunks(to, 3);
		if (entity.world != newworld && !(entity instanceof EntityPlayer)) {
			final net.minecraft.server.Entity passenger = entity.passenger;
			if (passenger != null) {
				entity.passenger = null;
				passenger.vehicle = null;
				if (teleport(passenger, to)) {
					CommonUtil.nextTick(new Runnable() {
						public void run() {
							passenger.setPassengerOf(entity);
						}
					});
				}
			}

			// teleport this entity
			entity.world.removeEntity(entity);
			entity.dead = false;
			entity.world = newworld;
			entity.setLocation(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
			entity.world.addEntity(entity);
			return true;
		} else {
			return entity.getBukkitEntity().teleport(to);
		}
	}
}
