package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.EntityTypingHandler;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandler;
import com.bergerkiller.bukkit.common.protocol.PacketType;

import com.bergerkiller.reflection.net.minecraft.server.*;
import com.bergerkiller.reflection.org.bukkit.BHandlerList;
import com.bergerkiller.reflection.org.bukkit.BPluginDescriptionFile;
import com.bergerkiller.reflection.org.bukkit.BSimplePluginManager;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.*;

import static com.bergerkiller.bukkit.common.utils.CommonUtil.loadClass;

import org.junit.Test;

public class ReflectionTest {

    /*
    @Test
    public void asdf() {
        InputStream is = ReflectionTest.class.getClassLoader().getResourceAsStream("com/bergerkiller/reflection/lookup/MC1_11_2.txt");

        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String text = s.hasNext() ? s.next() : "";

        SourceDeclaration source = new SourceDeclaration(text);

        System.out.println(source);
    }
    */

    @Test
    @SuppressWarnings("deprecation")
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

            loadClass(NMSChunk.class);
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
            loadClass(NMSPlayerAbilities.class);
            loadClass(NMSIChatBaseComponent.class);
            loadClass(NMSDamageSource.class);
            loadClass(NMSDataWatcher.class);
            loadClass(NMSNibbleArray.class);
            loadClass(NMSTileEntity.class);
            loadClass(NMSEntityTypes.class);
            loadClass(NMSRecipe.class);
            loadClass(NMSMobEffect.class);
            loadClass(NMSEnumProtocol.class);
            loadClass(NMSMinecraftKey.class);
            loadClass(NMSMobSpawnerAbstract.class);
            loadClass(NMSVector.class);
            loadClass(NMSEntityMinecart.class);
            loadClass(NMSEnumGamemode.class);
            loadClass(NMSWeightedRandomChoice.class);
            loadClass(NMSPlayerList.class);
            loadClass(PacketType.class);
            loadClass(NMSEntityItem.class);
            loadClass(NMSItem.class);
            loadClass(NMSMaterial.class);
            loadClass(NMSEntityHanging.class);

            loadClass(BPluginDescriptionFile.class);
            loadClass(BSimplePluginManager.class);
            loadClass(BHandlerList.class);

            loadClass(CBCraftEntity.class);
            loadClass(CBCraftPlayer.class);
            loadClass(CBCraftItemStack.class);
            loadClass(CBCraftBlockState.class);
            loadClass(CBCraftServer.class);
            loadClass(CBCraftInventory.class);
            loadClass(CBCraftTask.class);
            loadClass(CBCraftScheduler.class);

            loadClass(EntityAddRemoveHandler.class);
            loadClass(EntityTypingHandler.class);
            loadClass(RegionHandler.class);
            loadClass(CommonEntityType.class);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
