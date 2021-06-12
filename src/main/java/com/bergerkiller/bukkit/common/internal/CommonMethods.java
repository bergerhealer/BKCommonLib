package com.bergerkiller.bukkit.common.internal;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.bergerkiller.generated.net.minecraft.world.damagesource.DamageSourceHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockStateHandle;

public class CommonMethods {

    public static BlockState CraftBlockState_new(Block block) {
        return CraftBlockStateHandle.createNew(block);
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

    // Used by PluginBase.getVersionNumber()
    public static int parseVersionNumber(String version) {
        // Split by dots
        int versionNumber = 0;
        int numDigits = 0;
        for (String part : version.split("\\.")) {
            // Trim non-digits from the start of the version part
            int part_start = 0;
            while (part_start < part.length() && !Character.isDigit(part.charAt(part_start))) {
                part_start++;
            }
            if (part_start >= part.length()) {
                continue;
            }

            // Trim everything after the first non-digit
            int part_end = part_start;
            while (part_end < part.length() && Character.isDigit(part.charAt(part_end))) {
                part_end++;
            }

            // Try and parse it; append to global version value and shift multiplier
            try {
                versionNumber *= 100;
                versionNumber += Integer.parseInt(part.substring(part_start, part_end));
                numDigits++;
            } catch (NumberFormatException ex) {}
        }

        // Guarantee three-digit version format
        while (numDigits < 3) {
            numDigits++;
            versionNumber *= 100;
        }

        return versionNumber;
    }
}
