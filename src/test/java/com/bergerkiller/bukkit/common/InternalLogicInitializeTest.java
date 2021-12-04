package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.lang.reflect.Modifier;

import org.junit.Test;

import com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataSerializer;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.EntityTypingHandler;
import com.bergerkiller.bukkit.common.internal.logic.LightingHandlerDisabled;
import com.bergerkiller.bukkit.common.internal.logic.NullPacketDataSerializerInit;
import com.bergerkiller.bukkit.common.internal.logic.PlayerFileDataHandler;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandler;
import com.bergerkiller.bukkit.common.lighting.LightingHandler;
import com.bergerkiller.bukkit.common.map.markers.MapDisplayMarkers;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;

/**
 * Test the initialization of internally used logic helpers
 */
public class InternalLogicInitializeTest {

    @Test
    public void testBlockStateConversion() {
        assertNotNull(BlockStateConversion.INSTANCE);
    }

    @Test
    public void testEntityAddRemoveHandler() {
        assertNotNull(EntityAddRemoveHandler.INSTANCE);
    }

    @Test
    public void testEntityTypingHandler() {
        assertNotNull(EntityTypingHandler.INSTANCE);
    }

    @Test
    public void testLightingHandler() {
        assertFalse(LightingHandler.instance() instanceof LightingHandlerDisabled);
    }

    @Test
    public void testRegionHandler() {
        assertNotNull(RegionHandler.INSTANCE);
        RegionHandler.INSTANCE.forceInitialization();
    }

    @Test
    public void testPlayerFileDataHandler() {
        assertNotNull(PlayerFileDataHandler.INSTANCE);
        PlayerFileDataHandler.INSTANCE.forceInitialization();
    }

    @Test
    public void testDimensionWrapper() {
        assertNotNull(DimensionType.OVERWORLD);
    }

    @Test
    public void testPortalHandler() {
        PortalHandler.INSTANCE.forceInitialization();
    }

    @Test
    public void testBlockDataSerializer() {
        BlockDataSerializer.INSTANCE.forceInitialization();
    }

    @Test
    public void testEntityMoveHandler() {
        EntityMoveHandler handler = EntityMoveHandler.create(null);
        if (!handler.isBlockCollisionsMethodInitialized()) {
            fail("Failed to initialize " + handler.getClass().getName() + " block collision handler");
        }
    }

    @Test
    public void testMapDisplayMarkerApplier() {
        MapDisplayMarkers.APPLIER.forceInitialization();
    }

    @Test
    public void testNullPacketDataSerializer() {
        NullPacketDataSerializerInit.initialize();
        assertTrue(NullPacketDataSerializerInit.is_initialized);

        Class<?> nullPacketDataSerializerType = null;
        try {
            nullPacketDataSerializerType = Resolver.getClassByExactName(NullPacketDataSerializerInit.CLASS_NAME);
        } catch (ClassNotFoundException e) {
            fail("Runtime-generated NullPacketDataSerializer not found: " + NullPacketDataSerializerInit.CLASS_NAME);
        }

        Class<?> serializerType = Resolver.loadClass("net.minecraft.network.PacketDataSerializer", false);
        assertNotNull(serializerType);

        java.lang.reflect.Field instanceField = null;
        Object nullPacketDataSerializer = null;
        try {
            instanceField = nullPacketDataSerializerType.getField("INSTANCE");
            assertTrue(Modifier.isStatic(instanceField.getModifiers()));
            nullPacketDataSerializer = instanceField.get(null);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Runtime-generated NullPacketDataSerializer class has no valid INSTANCE field");
        }

        assertTrue(serializerType.isAssignableFrom(instanceField.getType()));
        assertNotNull(nullPacketDataSerializer);
        assertTrue(serializerType.isAssignableFrom(nullPacketDataSerializer.getClass()));
    }
}
