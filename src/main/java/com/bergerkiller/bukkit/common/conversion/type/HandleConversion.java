package com.bergerkiller.bukkit.common.conversion.type;

import net.minecraft.server.v1_11_R1.Block;
import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EnumDifficulty;
import net.minecraft.server.v1_11_R1.EnumHand;
import net.minecraft.server.v1_11_R1.EnumItemSlot;
import net.minecraft.server.v1_11_R1.MapIcon;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.craftbukkit.v1_11_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_11_R1.potion.CraftPotionUtil;
import org.bukkit.craftbukkit.v1_11_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.MainHand;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.proxies.InventoryProxy;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.bukkit.common.wrappers.ScoreboardAction;
import com.bergerkiller.bukkit.common.wrappers.UseAction;
import com.bergerkiller.generated.net.minecraft.server.AttributeMapServerHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.reflection.net.minecraft.server.NMSEnumGamemode;
import com.bergerkiller.reflection.net.minecraft.server.NMSItemStack;
import com.bergerkiller.reflection.net.minecraft.server.NMSMobEffect;
import com.bergerkiller.reflection.net.minecraft.server.NMSTileEntity;
import com.bergerkiller.reflection.net.minecraft.server.NMSVector;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorldType;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftBlockState;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftItemStack;

public class HandleConversion {

    @ConverterMethod(output="T extends net.minecraft.server.Entity")
    public static Object toEntityHandle(org.bukkit.entity.Entity entity) {
        if (entity instanceof CraftEntity) {
            return ((CraftEntity) entity).getHandle();
        } else {
            return null;
        }
    }

    @ConverterMethod(output="net.minecraft.server.WorldServer")
    public static Object toWorldHandle(org.bukkit.World world) {
        if (world instanceof CraftWorld) {
            return ((CraftWorld) world).getHandle();
        } else {
            return null;
        }
    }

    @ConverterMethod(output="net.minecraft.server.Chunk")
    public static Object toChunkHandle(org.bukkit.Chunk chunk) {
        if (chunk instanceof CraftChunk) {
            return ((CraftChunk) chunk).getHandle();
        } else {
            return null;
        }
    }

    @ConverterMethod(output="net.minecraft.server.ItemStack")
    public static Object toItemStackHandle(org.bukkit.inventory.ItemStack itemStack) {
        if (itemStack instanceof CraftItemStack) {
            return CBCraftItemStack.handle.get(itemStack);
        } else {
            org.bukkit.inventory.ItemStack stack = (org.bukkit.inventory.ItemStack) itemStack;
            Object rval = Bukkit.getServer() != null ? CraftItemStack.asNMSCopy(stack) : null;
            if (rval == null) {
                rval = NMSItemStack.newInstance(stack.getType(), MaterialUtil.getRawData(stack), stack.getAmount());
            }
            return rval;
        }
    }

    @ConverterMethod(output="T extends net.minecraft.server.TileEntity")
    public static Object toTileEntityHandle(org.bukkit.block.BlockState blockState) {
        return CBCraftBlockState.toTileEntity(blockState);
    }

    @ConverterMethod(output="T extends net.minecraft.server.TileEntity")
    public static Object getTileEntityHandle(org.bukkit.block.Block block) {
        return NMSTileEntity.getFromWorld(block);
    }

    @ConverterMethod(output="net.minecraft.server.IInventory")
    public static Object toIInventoryHandle(InventoryProxy proxy) {
        return proxy.getProxyBase();
    }

    @ConverterMethod(output="net.minecraft.server.IInventory")
    public static Object toIInventoryHandle(org.bukkit.inventory.Inventory inventory) {
        if (inventory instanceof CraftInventory) {
            return ((CraftInventory) inventory).getInventory();
        } else {
            return null;
        }
    }

    @ConverterMethod(output="net.minecraft.server.DataWatcher")
    public static Object toDataWatcherHandle(com.bergerkiller.bukkit.common.wrappers.DataWatcher dataWatcher) {
        return dataWatcher.getHandle();
    }

    @ConverterMethod(input="net.minecraft.server.Entity", output="net.minecraft.server.DataWatcher")
    public static Object toDataWatcherHandle(Object nmsEntityHandle) {
        return ((Entity) nmsEntityHandle).getDataWatcher();
    }

    @ConverterMethod(output="T extends net.minecraft.server.Item")
    public static Object toItemHandle(org.bukkit.Material material) {
        return CraftMagicNumbers.getItem(material);
    }

    @ConverterMethod(output="T extends net.minecraft.server.Block")
    public static Object toBlockHandle(org.bukkit.Material material) {
        return CraftMagicNumbers.getBlock(material);
    }

    @ConverterMethod(output="T extends net.minecraft.server.Block")
    public static Object toBlockHandle(com.bergerkiller.bukkit.common.wrappers.BlockData blockData) {
        return blockData.getBlockRaw();
    }

    @ConverterMethod(output="T extends net.minecraft.server.IBlockData")
    public static Object toIBlockDataHandle(com.bergerkiller.bukkit.common.wrappers.BlockData blockData) {
        return blockData.getData();
    }

    @ConverterMethod(input="net.minecraft.server.Block", output="T extends net.minecraft.server.IBlockData")
    public static Object toIBlockDataHandleFromBlock(Object nmsBlockHandle) {
        return ((Block) nmsBlockHandle).getBlockData();
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod(output="net.minecraft.server.EnumGamemode")
    public static Object toEnumGamemodeHandle(org.bukkit.GameMode gameMode) {
        return NMSEnumGamemode.getFromId.invoke(null, gameMode.getValue());
    }

    @ConverterMethod(output="net.minecraft.server.EnumHand")
    public static Object toEnumHandHandle(org.bukkit.inventory.MainHand mainHand) {
        switch((MainHand) mainHand) {
        case LEFT:
            return EnumHand.OFF_HAND;
        case RIGHT:
            return EnumHand.MAIN_HAND;
        }
        return null;
    }

    @ConverterMethod(output="net.minecraft.server.WorldType")
    public static Object toWorldTypeHandle(org.bukkit.WorldType worldType) {
        return NMSWorldType.getType.invoke(null, worldType.getName());
    }

    @ConverterMethod(output="T extends net.minecraft.server.Packet")
    public static Object toPacketHandle(CommonPacket commonPacket) {
        return commonPacket.getHandle();
    }

    @ConverterMethod(output="net.minecraft.server.ChunkCoordIntPair")
    public static Object toChunkCoordIntPairHandle(IntVector2 intVector2) {
        return NMSVector.newPair(intVector2.x, intVector2.z);
    }

    @ConverterMethod(output="net.minecraft.server.BlockPosition")
    public static Object toBlockPositionHandle(IntVector3 intVector3) {
        return NMSVector.newPosition(intVector3.x, intVector3.y, intVector3.z);
    }

    @ConverterMethod(output="net.minecraft.server.Vec3D")
    public static Object toVec3DHandle(Vector vector) {
        return NMSVector.newVec(vector.getX(), vector.getY(), vector.getZ());
    }

    @ConverterMethod(output="net.minecraft.server.PlayerAbilities")
    public static Object toPlayerAbilitiesHandle(PlayerAbilities playerAbilities) {
        return playerAbilities.getHandle();
    }

    @ConverterMethod(output="net.minecraft.server.EntityTracker")
    public static Object toEntityTrackerHandle(EntityTracker entityTracker) {
        return entityTracker.getHandle();
    }

    @ConverterMethod(output="it.unimi.dsi.fastutil.longs.Long2ObjectMap<T>")
    public static <T> Object toLongObjectMapHandle(LongHashMap<T> longHashMapWrapper) {
        return longHashMapWrapper.getHandle();
    }

    @ConverterMethod(output="org.bukkit.craftbukkit.util.LongHashSet")
    public static Object toLongHashSetHandle(LongHashSet longHashSetWrapper) {
        return longHashSetWrapper.getHandle();
    }

    @ConverterMethod(output="net.minecraft.server.IntHashMap<T>")
    public static <T> Object toIntHashMapHandle(IntHashMap<T> intHashMapWrapper) {
        return intHashMapWrapper.getHandle();
    }

    @ConverterMethod(output="net.minecraft.server.PacketPlayInUseEntity.EnumEntityUseAction")
    public static Object toEnumEntityUseActionHandle(UseAction action) {
        return action.getHandle();
    }

    @ConverterMethod(output="net.minecraft.server.EnumDifficulty")
    public static Object toEnumDifficultyHandle(Number difficultyId) {
        return EnumDifficulty.getById(difficultyId.intValue());
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod(output="net.minecraft.server.EnumDifficulty")
    public static Object toEnumDifficultyHandle(Difficulty difficulty) {
        return EnumDifficulty.getById(difficulty.getValue());
    }

    @ConverterMethod(output="net.minecraft.server.PacketPlayOutScoreboardScore.EnumScoreboardAction")
    public static Object toEnumScoreboardActionHandle(ScoreboardAction action) {
        return action.getHandle();
    }

    @ConverterMethod(output="net.minecraft.server.ChunkSection")
    public static Object toChunkSectionHandle(ChunkSection section) {
        return section.getHandle();
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod(output="net.minecraft.server.MobEffectList")
    public static Object toMobEffectListHandle(PotionEffectType potionEffectType) {
        return NMSMobEffect.List.fromId.invoke(null, potionEffectType.getId());
    }

    @ConverterMethod(output="net.minecraft.server.MobEffect")
    public static Object toMobEffectHandle(PotionEffect potionEffect) {
        return CraftPotionUtil.fromBukkit(potionEffect);
    }

    @ConverterMethod(output="net.minecraft.server.DataWatcherObject<V>")
    public static <V> Object toDataWatcherObjectHandle(DataWatcher.Key<V> keyWrapper) {
        return keyWrapper.getHandle();
    }

    @ConverterMethod(output="net.minecraft.server.DataWatcher.Item<V>")
    public static <V> Object toDataWatcherItemHandle(DataWatcher.Item<V> itemWrapper) {
        return itemWrapper.getHandle();
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod(output="net.minecraft.server.MapIcon")
    public static Object toMapIconHandle(MapCursor cursor) {
        // public MapCursor(byte x, byte y, byte direction, byte type, boolean visible)
        // public MapIcon(Type paramType, byte paramByte1, byte paramByte2, byte paramByte3)
        return new MapIcon(MapIcon.Type.a(cursor.getRawType()),
                cursor.getX(), cursor.getY(), cursor.getDirection());
    }

    @ConverterMethod
    public static String worldTypeToString(org.bukkit.WorldType worldType) {
        return worldType.name();
    }

    @ConverterMethod(output="net.minecraft.server.IChatBaseComponent")
    public static Object toChatBaseComponent(ChatText text) {
        return text.getHandle();
    }

    @ConverterMethod(output="net.minecraft.server.EnumItemSlot")
    public static Object toEnumItemSlotHandle(EquipmentSlot equipmentSlot) {
        switch (equipmentSlot) {
        case CHEST: return EnumItemSlot.CHEST;
        case FEET: return EnumItemSlot.FEET;
        case HAND: return EnumItemSlot.MAINHAND;
        case OFF_HAND: return EnumItemSlot.OFFHAND;
        case HEAD: return EnumItemSlot.HEAD;
        case LEGS: return EnumItemSlot.LEGS;
        }
        return null;
    }

    @ConverterMethod(input="net.minecraft.server.AttributeMapBase")
    public static AttributeMapServerHandle toAttributeMapServer(Object attributeMapBaseHandle) {
        if (AttributeMapServerHandle.T.isAssignableFrom(attributeMapBaseHandle)) {
            return AttributeMapServerHandle.createHandle(attributeMapBaseHandle);
        } else {
            return null;
        }
    }
}
