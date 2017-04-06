package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.internal.hooks.WorldListenerHook;
import com.bergerkiller.bukkit.common.protocol.PacketType;

import com.bergerkiller.reflection.net.minecraft.server.*;
import com.bergerkiller.reflection.org.bukkit.BHandlerList;
import com.bergerkiller.reflection.org.bukkit.BPluginDescriptionFile;
import com.bergerkiller.reflection.org.bukkit.BSimplePluginManager;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.*;

import static com.bergerkiller.bukkit.common.utils.CommonUtil.loadClass;

import org.junit.Test;

public class ReflectionTest {

    @Test
    public void loadReflectionClasses() {
        try {
            /*
             * UnknownObject obj = new UnknownObject();
             * System.out.println(obj.k);
             * 
             * System.out.println(obj.k); obj.myFunc2(20);
             * System.out.println(obj.k);
             * 
             * // Now hook this object with the callback and do some tests
             * UnknownHook hook = new UnknownHook();
             * 
             * obj = hook.hook(obj);
             * 
             * System.out.println(obj.k); obj.myFunc2(20);
             * System.out.println(obj.k);
             */

            loadClass(NMSBlock.class);
            loadClass(NMSChunk.class);
            loadClass(NMSChunkProviderServer.class);
            loadClass(NMSChunkRegionLoader.class);
            loadClass(NMSChunkSection.class);
            loadClass(NMSEntity.class);
            loadClass(NMSItemStack.class);
            loadClass(NMSWorld.class);
            loadClass(NMSEntityHuman.class);
            loadClass(NMSEntityLiving.class);
            loadClass(NMSEntityInsentient.class);
            loadClass(NMSPlayerConnection.class);
            loadClass(NMSNetworkManager.class);
            loadClass(NMSEntityPlayer.class);
            loadClass(NMSWorldServer.class);
            loadClass(NMSEntityTracker.class);
            loadClass(NMSEntityTrackerEntry.class);
            loadClass(NMSPlayerAbilities.class);
            loadClass(NMSCommandBlockListenerAbstract.class);
            loadClass(NMSIChatBaseComponent.class);
            loadClass(NMSDamageSource.class);
            loadClass(NMSDataWatcher.class);
            loadClass(NMSRegionFile.class);
            loadClass(NMSRegionFileCache.class);
            loadClass(NMSNBT.class);
            loadClass(NMSNibbleArray.class);
            loadClass(NMSTileEntity.class);
            loadClass(NMSEntityTypes.class);
            loadClass(NMSRecipe.class);
            loadClass(NMSMobEffect.class);
            loadClass(NMSPlayerChunk.class);
            loadClass(NMSEnumProtocol.class);
            loadClass(NMSPlayerChunkMap.class);
            loadClass(NMSMinecraftKey.class);
            loadClass(NMSMobSpawnerAbstract.class);
            loadClass(NMSVector.class);
            loadClass(NMSDataWatcherObject.class);
            loadClass(NMSWorldType.class);
            loadClass(NMSEntityMinecart.class);
            loadClass(NMSEnumGamemode.class);
            loadClass(NMSIntHashMap.class);
            loadClass(NMSWeightedRandomChoice.class);
            loadClass(NMSBiomeMeta.class);
            loadClass(NMSPlayerList.class);
            loadClass(PacketType.class);
            loadClass(NMSEntityItem.class);
            loadClass(NMSWorldManager.class);
            loadClass(NMSItem.class);
            loadClass(NMSMaterial.class);

            loadClass(BPluginDescriptionFile.class);
            loadClass(BSimplePluginManager.class);
            loadClass(BHandlerList.class);

            loadClass(CBChunkIOExecutor.class);
            loadClass(CBCraftEntity.class);
            loadClass(CBCraftPlayer.class);
            loadClass(CBCraftMagicNumbers.class);
            loadClass(CBCraftItemStack.class);
            loadClass(CBCraftBlockState.class);
            loadClass(CBCraftServer.class);
            loadClass(CBCraftInventory.class);
            loadClass(CBCraftTask.class);
            loadClass(CBCraftScheduler.class);
            loadClass(CBLongHashSet.class);

            loadClass(WorldListenerHook.class);
            loadClass(CommonEntityType.class);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
