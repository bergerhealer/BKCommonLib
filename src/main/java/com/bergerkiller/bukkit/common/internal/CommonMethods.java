package com.bergerkiller.bukkit.common.internal;

import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.ChunkSection;
import net.minecraft.server.v1_11_R1.DamageSource;
import net.minecraft.server.v1_11_R1.Explosion;
import net.minecraft.server.v1_11_R1.IBlockData;
import net.minecraft.server.v1_11_R1.WorldServer;
import net.minecraft.server.v1_11_R1.IPlayerFileData;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_11_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftBlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.reflection.net.minecraft.server.NMSChunk;

public class CommonMethods {

    public static Block CraftBlock_new(Chunk chunk, int x, int y, int z) {
        return new CraftBlock((CraftChunk) chunk, x, y, z);
    }

    public static BlockState CraftBlockState_new(Block block) {
        return new CraftBlockState(block);
    }

    public static ChunkSection ChunkSection_new(org.bukkit.World world, int y) {
        return new ChunkSection(y >> 4 << 4, !CommonNMS.getNative(world).worldProvider.m());
    }

    public static Explosion Explosion_new(org.bukkit.World world, double x, double y, double z) {
        return new Explosion(CommonNMS.getNative(world), null, x, y, z, (float) 4.0, true, true);
    }

    public static CraftServer CraftServer_instance() {
        return (CraftServer) Bukkit.getServer();
    }

    public static void setPlayerFileData(Object playerFileData) {
        CommonNMS.getPlayerList().playerFileData = (IPlayerFileData) playerFileData;
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
        DamageSource source;
        if (damager instanceof Player) {
            source = DamageSource.playerAttack(CommonNMS.getNative((Player) damager));
        } else if (damager instanceof LivingEntity) {
            source = DamageSource.mobAttack(CommonNMS.getNative((LivingEntity) damager));
        } else {
            source = DamageSource.GENERIC;
        }
        CommonNMS.getNative(entity).damageEntity(source, (float) damage);
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

    @Deprecated
    public static boolean setBlock(org.bukkit.Chunk chunk, int x, int y, int z, Material type, int data) {
        if (y < 0 || y > chunk.getWorld().getMaxHeight())
            return false;

        WorldServer world = CommonNMS.getNative(chunk.getWorld());
        net.minecraft.server.v1_11_R1.Block typeBlock = CommonNMS.getBlock(type);

        boolean result = NMSChunk.setBlock(Conversion.toChunkHandle.convert(chunk), x, y, z, typeBlock, data);
        world.methodProfiler.a("checkLight");
        world.A(new BlockPosition(x, y, z));
        world.methodProfiler.b();

        if (result) {
//            world.notifyAndUpdatePhysics(pos, chunk, typeBlock);
            return chunk.getBlock(x, y, z).setTypeIdAndData(type.getId(), (byte) data, true);
        }
        return result;
    }

    /**
     * Gets the block data
     *
     * @param chunk the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @return block data
     */
    public static IBlockData getBlockData(org.bukkit.Chunk chunk, int x, int y, int z) {
        return NMSChunk.getData(Conversion.toChunkHandle.convert(chunk), x, y, z);
    }
}
