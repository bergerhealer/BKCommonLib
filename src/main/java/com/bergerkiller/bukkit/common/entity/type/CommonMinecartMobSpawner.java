package com.bergerkiller.bukkit.common.entity.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.wrappers.MobSpawner;

/**
 * A Common Entity implementation for Minecarts with a Mob Spawner
 */
public class CommonMinecartMobSpawner extends CommonMinecart<SpawnerMinecart> {
	private static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("EntityMinecartMobSpawner");
	private static final FieldAccessor<Object> mobSpawnerHandle = TEMPLATE.getField("a");

	public CommonMinecartMobSpawner(SpawnerMinecart base) {
		super(base);
	}

	@Override
	public List<ItemStack> getBrokenDrops() {
		return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(Material.MOB_SPAWNER, 1));
	}

	@Override
	public Material getCombinedItem() {
		return Material.MINECART; //TODO: Missing!
	}

	/**
	 * Gets the Mob Spawner used to spawn mobs for this Mob Spawner Minecart
	 * 
	 * @return Mob spawner
	 */
	public MobSpawner getMobSpawner() {
		return new MobSpawner(mobSpawnerHandle.get(getHandle()));
	}
}
