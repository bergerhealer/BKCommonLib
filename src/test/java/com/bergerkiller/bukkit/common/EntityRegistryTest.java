package com.bergerkiller.bukkit.common;

import org.junit.Test;

import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityTypes;

public class EntityRegistryTest {

    @Test
    public void loadCommonEntityTypes() {
        // Initialize the entity lists first
        NMSEntityTypes.registerAllEntities();
        CommonUtil.loadClass(CommonEntityType.class);
    }

    @Test
    public void verifyNetworkSettings() {
       // AsyncCatcher.enabled = false; // otherwise it fails horribly with a NPE

        //NMSEntityTracker.NetworkSettings settings = NMSEntityTracker.NetworkSettings.get(CommonUtil.getNMSClass("EntityPlayer"));
       // System.out.println(settings.toString());
        
    }
}
