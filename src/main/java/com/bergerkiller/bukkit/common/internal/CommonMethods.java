package com.bergerkiller.bukkit.common.internal;

import java.util.EnumMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.generated.net.minecraft.server.ChunkSectionHandle;
import com.bergerkiller.generated.net.minecraft.server.DamageSourceHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.ExplosionHandle;
import com.bergerkiller.generated.net.minecraft.server.IPlayerFileDataHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockStateHandle;

public class CommonMethods {
    private static final IntHashMap<Material> idToMaterial = new IntHashMap<Material>();
    private static final EnumMap<Material, Integer> materialToId = new EnumMap<Material, Integer>(Material.class);

    static {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            // On MC 1.13 can we store anything at all? No.
            
        } else {
            // Stores all material mapping on 1.12.2 as is
            try {
                java.lang.reflect.Method m = Material.class.getDeclaredMethod("getId");
                for (Material mat : Material.values()) {
                    int id = ((Integer) m.invoke(mat)).intValue();
                    idToMaterial.put(id, mat);
                    materialToId.put(mat, id);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static Block CraftBlock_new(Chunk chunk, int x, int y, int z) {
        return CraftBlockHandle.createNew(chunk, x, y, z);
    }

    public static BlockState CraftBlockState_new(Block block) {
        return CraftBlockStateHandle.createNew(block);
    }

    public static ChunkSectionHandle ChunkSection_new(org.bukkit.World world, int y) {
        return ChunkSectionHandle.createNew(y >> 4 << 4, CommonNMS.getHandle(world).getWorldProvider().hasSkyLight());
    }

    public static ExplosionHandle Explosion_new(org.bukkit.World world, double x, double y, double z) {
        return ExplosionHandle.createNew(world, null, x, y, z, 4.0f, true, true);
    }

    public static void setPlayerFileData(IPlayerFileDataHandle playerFileData) {
        CommonNMS.getPlayerList().setPlayerFileData(playerFileData);
    }

    public static DamageSourceHandle DamageSource_explosion(org.bukkit.entity.Entity entity, DamageCause cause, double damage) {
        Location loc = entity.getLocation();
        ExplosionHandle ex = Explosion_new(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
        return DamageSourceHandle.explosion(ex);
    }

    /**
     * @deprecated use the double damage version instead
     */
    @Deprecated
    public static void damage_explode(org.bukkit.entity.Entity entity, int damage, ExplosionHandle explosion) {
        damage_explode(entity, (double) damage, explosion);
    }

    /**
     * Damages an entity with the reason of an explosion
     *
     * @param entity to be demaged
     * @param damage of the damage
     * @param explosion wich has damaged the player
     */
    public static void damage_explode(org.bukkit.entity.Entity entity, double damage, ExplosionHandle explosion) {
        EntityHandle.fromBukkit(entity).damageEntity(DamageSourceHandle.explosion(explosion), (float) damage);
    }

    public static void damageBy(org.bukkit.entity.Entity entity, org.bukkit.entity.Entity damager, double damage) {
        DamageSourceHandle source;
        if (damager instanceof Player) {
            source = DamageSourceHandle.playerAttack((HumanEntity) damager);
        } else if (damager instanceof LivingEntity) {
            source = DamageSourceHandle.mobAttack((LivingEntity) damager);
        } else {
            source = DamageSourceHandle.byName("generic");
        }
        CommonNMS.getHandle(entity).damageEntity(source, (float) damage);
    }

    public static DamageSourceHandle DamageSource_from_damagecause(DamageCause cause) {
        return DamageSourceHandle.byName(getSourceName(cause));
    }

    private static String getSourceName(DamageCause cause) {
        // Special case >= v1.11.2
        if (cause.name().equals("CRAMMING")) {
            return "cramming";
        }

        // Special case >= v1.10.2
        if (cause.name().equals("HOT_FLOOR")) {
            return "hotFloor";
        }
        if (cause.name().equals("FLY_INTO_WALL")) {
            return "flyIntoWall";
        }
        if (cause.name().equals("DRAGON_BREATH")) {
            return "dragonBreath";
        }

        switch (cause) {
        case FIRE: return "inFire";
        case LIGHTNING: return "lightningBolt";
        case FIRE_TICK: return "onFire";
        case LAVA: return "lava";
        case SUFFOCATION: return "inWall";
        case DROWNING: return "drown";
        case STARVATION: return "starve";
        case CONTACT: return "cactus";
        case FALL: return "fall";
        case VOID: return "outOfWorld";
        case MAGIC: return "magic";
        case WITHER: return "wither";
        case FALLING_BLOCK: return "fallingBlock";
        default: return "generic";
        }
    }

    /**
     * Converts (old) legacy material ids to the Material types. On MC 1.13 and onwards, this
     * method will fail with a runtime exception. DO NOT USE. Backwards compatibility only!
     * 
     * @param id
     * @return material, null if not matched
     */
    @Deprecated
    public static Material getMaterialFromId(int id) {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            throw new UnsupportedOperationException("Material Ids are no longer supported on Minecraft 1.13 and onwards");
        }
        return idToMaterial.get(id);
    }

    /**
     * Converts material enum values to their respective legacy material type Ids. On MC 1.13 and onwards, this
     * method will fail with a runtime exception. DO NOT USE. Backwards compatibility only!
     * 
     * @param type
     * @return material type id, -1 for null
     */
    @Deprecated
    public static int getIdFromMaterial(Material type) {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            throw new UnsupportedOperationException("Material Ids are no longer supported on Minecraft 1.13 and onwards");
        }
        return materialToId.get(type);
    }
}
