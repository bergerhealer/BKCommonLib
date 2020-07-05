package com.bergerkiller.bukkit.common.internal;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.bergerkiller.generated.net.minecraft.server.ChunkSectionHandle;
import com.bergerkiller.generated.net.minecraft.server.DamageSourceHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockStateHandle;

public class CommonMethods {

    public static BlockState CraftBlockState_new(Block block) {
        return CraftBlockStateHandle.createNew(block);
    }

    public static ChunkSectionHandle ChunkSection_new(org.bukkit.World world, int y) {
        return ChunkSectionHandle.createNew(world, y >> 4 << 4);
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

}
