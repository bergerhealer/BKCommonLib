package com.bergerkiller.bukkit.common;

import org.junit.Test;

import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class EntityRegistryTest {

    static {
        CommonUtil.bootstrap();
    }

    @Test
    public void loadCommonEntityTypes() {
        // Initialize the entity lists first
        CommonUtil.loadClass(CommonEntityType.class);
    }

}
