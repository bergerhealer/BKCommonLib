package com.bergerkiller.bukkit.common.internal;

import net.minecraft.server.v1_11_R1.DamageSource;
import net.minecraft.server.v1_11_R1.Explosion;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.bergerkiller.generated.net.minecraft.server.ChunkSectionHandle;
import com.bergerkiller.generated.net.minecraft.server.DamageSourceHandle;
import com.bergerkiller.generated.net.minecraft.server.ExplosionHandle;
import com.bergerkiller.generated.net.minecraft.server.IPlayerFileDataHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockStateHandle;

public class CommonMethods {

    public static Block CraftBlock_new(Chunk chunk, int x, int y, int z) {
        return CraftBlockHandle.createNew(chunk, x, y, z);
    }

    public static BlockState CraftBlockState_new(Block block) {
        return CraftBlockStateHandle.createNew(block);
    }

    public static ChunkSectionHandle ChunkSection_new(org.bukkit.World world, int y) {
        return ChunkSectionHandle.createNew(y >> 4 << 4, !CommonNMS.getHandle(world).getWorldProvider().isDarkWorld());
    }

    public static Explosion Explosion_new(org.bukkit.World world, double x, double y, double z) {
        return (Explosion) ExplosionHandle.createNew(world, null, x, y, z, 4.0f, true, true).getRaw();
    }

    public static void setPlayerFileData(IPlayerFileDataHandle playerFileData) {
        CommonNMS.getPlayerList().setPlayerFileData(playerFileData);
    }

    public static DamageSource DamageSource_explosion(org.bukkit.entity.Entity entity, DamageCause cause, double damage) {
        Location loc = entity.getLocation();
        Explosion ex = Explosion_new(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
        return DamageSource.explosion(ex);
    }

    /**
     * @deprecated use the double damage version instead
     */
    @Deprecated
    public static void damage_explode(org.bukkit.entity.Entity entity, int damage, Explosion explosion) {
        damage_explode(entity, (double) damage, explosion);
    }

    /**
     * Damages an entity with the reason of an explosion
     *
     * @param entity to be demaged
     * @param damage of the damage
     * @param explosion wich has damaged the player
     */
    public static void damage_explode(org.bukkit.entity.Entity entity, double damage, Explosion explosion) {
        CommonNMS.getNative(entity).damageEntity(DamageSource.explosion(explosion), (float) damage);
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

    public static DamageSource DamageSource_from_damagecause(DamageCause cause) {
        DamageSource source;
        if (cause == DamageCause.CONTACT) {
            source = DamageSource.CACTUS;
        } else if (cause == DamageCause.DROWNING) {
            source = DamageSource.DROWN;
        } else if (cause == DamageCause.FALL) {
            source = DamageSource.FALL;
        } else if (cause == DamageCause.FALLING_BLOCK) {
            source = DamageSource.FALLING_BLOCK;
        } else if (cause == DamageCause.FIRE) {
            source = DamageSource.FIRE;
        } else if (cause == DamageCause.LAVA) {
            source = DamageSource.LAVA;
        } else if (cause == DamageCause.MAGIC) {
            source = DamageSource.MAGIC;
        } else if (cause == DamageCause.VOID) {
            source = DamageSource.OUT_OF_WORLD;
        } else if (cause == DamageCause.STARVATION) {
            source = DamageSource.STARVE;
        } else if (cause == DamageCause.SUFFOCATION) {
            source = DamageSource.STUCK;
        } else if (cause == DamageCause.WITHER) {
            source = DamageSource.WITHER;
        } else {
            source = DamageSource.GENERIC;
        }
        return source;
    }

}
