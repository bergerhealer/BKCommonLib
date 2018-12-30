package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class EntityRegistryTest {

    @Test
    public void loadCommonEntityTypes() {
        // Initialize the entity lists first
        CommonUtil.loadClass(CommonEntityType.class);
    }

    @Test
    public void testEntityTypesConversion() {
        // Test EntityTypes <> Class<? extends Entity> conversion logic
        // This applies to MC 1.13 and onwards only
        if (!Common.evaluateMCVersion(">=", "1.13")) {
            return;
        }

        Class<?>[] input_types = new Class<?>[] {
                CommonUtil.getNMSClass("EntityCreeper"),
                CommonUtil.getNMSClass("EntityGhast"),
                CommonUtil.getNMSClass("EntityMinecartRideable")
        };
        for (Class<?> input_type : input_types) {
            Object type = HandleConversion.toEntityTypesHandleFromEntityClass(input_type);
            assertNotNull(type);
            Class<?> output_type = WrapperConversion.entityClassFromEntityTypes(type);
            assertNotNull(output_type);
            assertEquals(input_type, output_type);
        }
    }
}
