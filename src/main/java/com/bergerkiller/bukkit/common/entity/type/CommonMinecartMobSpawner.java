package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.bukkit.common.wrappers.MobSpawner;
import com.bergerkiller.generated.net.minecraft.server.EntityMinecartMobSpawnerHandle;

import org.bukkit.Material;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * A Common Entity implementation for Minecarts with a Mob Spawner
 */
public class CommonMinecartMobSpawner extends CommonMinecart<SpawnerMinecart> {

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
        return EntityMinecartMobSpawnerHandle.T.mobSpawner.get(getHandle());
    }
}
