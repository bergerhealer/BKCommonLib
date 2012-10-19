package com.bergerkiller.bukkit.common.utils;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import me.snowleo.bleedingmobs.BleedingMobs;
import net.minecraft.server.Chunk;
import net.minecraft.server.EntityFallingBlock;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EntityMinecart;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntityTrackerEntry;
import net.minecraft.server.IntHashMap;
import net.minecraft.server.MathHelper;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Squid;
import org.bukkit.plugin.Plugin;

import com.avaje.ebeaninternal.server.deploy.BeanDescriptor.EntityType;
import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.narrowtux.showcase.Showcase;

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
		return CommonUtil.tryCast(getNative(e), type);
	}

	public static net.minecraft.server.Entity getNative(Entity e) {
		return e == null ? null : ((CraftEntity) e).getHandle();
	}

	public static <T extends Entity> T getEntity(World world, UUID uid, Class<T> type) {
		return CommonUtil.tryCast(getEntity(world, uid), type);
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

	public static boolean isMob(net.minecraft.server.Entity entity) {
		return isMob(entity.getBukkitEntity());
	}

	public static boolean isMob(Entity entity) {
		return entity instanceof Creature || entity instanceof Slime;
	}

	public static boolean isAnimal(net.minecraft.server.Entity entity) {
		return isAnimal(entity.getBukkitEntity());
	}

	public static boolean isAnimal(Entity entity) {
		return entity instanceof Animals || entity instanceof Squid;
	}

	public static boolean isMonster(net.minecraft.server.Entity entity) {
		return isMonster(entity.getBukkitEntity());
	}

	public static boolean isMonster(Entity entity) {
		return entity instanceof Monster || entity instanceof Slime;
	}

	public static boolean isNPC(net.minecraft.server.Entity entity) {
		return isNPC(entity.getBukkitEntity());
	}

	public static boolean isNPC(Entity entity) {
		return entity instanceof NPC;
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
		return LogicUtil.contains(name.toLowerCase(), animalNames);
	}

	public static boolean isMonster(String name) {
		return LogicUtil.contains(name.toLowerCase(), monsterNames);
	}

	public static boolean isNPC(String name) {
		return LogicUtil.contains(name.toLowerCase(), npcNames);
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

	/**
	 * Checks if a given Entity should be ignored when working with it<br>
	 * This could be because another plugin is operating on it, or for Virtual items
	 * 
	 * @param entity to check
	 * @return True if the entity should be ignored, False if not
	 */
	public static boolean isIgnored(Entity entity) {
		if (entity instanceof Item) {
			Item item = (Item) entity;
			if (Common.isShowcaseEnabled) {
				try {
					if (Showcase.instance.getItemByDrop(item) != null)
						return true;
				} catch (Throwable t) {
					Bukkit.getLogger().log(Level.SEVERE, "Showcase item verification failed (update needed?), contact the authors!");
					t.printStackTrace();
					Common.isShowcaseEnabled = false;
				}
			}
			if (Common.isSCSEnabled) {
				try {
					if (ShowCaseStandalone.get().isShowCaseItem(item))
						return true;
				} catch (Throwable t) {
					Bukkit.getLogger().log(Level.SEVERE, "ShowcaseStandalone item verification failed (update needed?), contact the authors!");
					t.printStackTrace();
					Common.isSCSEnabled = false;
				}
			}
			if (Common.bleedingMobsInstance != null) {
				try {
					BleedingMobs bm = (BleedingMobs) Common.bleedingMobsInstance;
					if (bm.isSpawning())
						return true;
					if (bm.isWorldEnabled(item.getWorld())) {
						if (bm.isParticleItem(((CraftItem) item).getUniqueId())) {
							return true;
						}
					}
				} catch (Throwable t) {
					Bukkit.getLogger().log(Level.SEVERE, "Bleeding Mobs item verification failed (update needed?), contact the authors!");
					t.printStackTrace();
					Common.bleedingMobsInstance = null;
				}
			}
		}
		return false;
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

	/**
	 * Teleports an entity in the next tick
	 * 
	 * @param entity to teleport
	 * @param to location to teleport to
	 */
	public static void teleportNextTick(final Entity entity, final Location to) {
		CommonUtil.nextTick(new Runnable() {
			public void run() {
				teleport(entity, to);
			}
		});
	}

	/**
	 * Teleports an entity
	 * 
	 * @param entity to teleport
	 * @param to location to teleport to
	 */
	public static boolean teleport(Entity entity, final Location to) {
		return teleport(getNative(entity), to);
	}

	/**
	 * Teleports an entity
	 * 
	 * @param entity to teleport
	 * @param to location to teleport to
	 */
	public static boolean teleport(final net.minecraft.server.Entity entity, final Location to) {
		WorldServer newworld = WorldUtil.getNative(to.getWorld());
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
