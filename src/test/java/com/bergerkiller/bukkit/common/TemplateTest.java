package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.utils.DebugUtil;
import com.bergerkiller.generated.net.minecraft.core.DirectionHandle;
import com.bergerkiller.generated.net.minecraft.core.RotationsHandle;
import com.bergerkiller.generated.net.minecraft.network.syncher.SynchedEntityDataHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.MinecartHandle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.junit.Ignore;
import org.junit.Test;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.generated.net.minecraft.core.DirectionHandle.AxisHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

public class TemplateTest {

    @Test
    public void testTestServerInitialized() {
        CommonBootstrap.initServer();
        assertNotNull(Bukkit.getServer());
    }

    @Test
    public void testTemplate() throws IOException {
        List<String> handleClassNames = CommonBootstrap.getGeneratedClassNames();

        boolean fullySuccessful = true;
        ArrayList<Template.Class<?>> classes = new ArrayList<Template.Class<?>>();
        for (String className : handleClassNames) {
            Class<?> genClass = CommonUtil.getClass(className, true);
            if (genClass == null) {
                System.out.println("Error occurred loading generated class " + className);
                fail("Failed to load generated class at " + className);
            }
            if (!Template.Handle.class.isAssignableFrom(genClass)) {
                continue; // Skip things that are not handled
            }

            try {
                java.lang.reflect.Field f = genClass.getField("T");
                if (f == null) {
                    fail("Failed to find template field in type " + className);
                }
                Template.Class<?> c = (Template.Class<?>) f.get(null);
                classes.add(c);
                if (c == null) {
                    fail("Failed to initialize template class " + className);
                }
                if (!c.isValid() && !c.isOptional()) {
                    System.err.println("Failed to fully load template class " + className);
                    fullySuccessful = false;
                }
            } catch (Throwable t) {
                throw new RuntimeException("Failed to test class "+  className, t);
            }
        }

        // Attempt to fully initialize all declarations
        for (Template.Class<?> templateClass : classes) {
            try {
                templateClass.forceInitialization();
            } catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to run forceInitialization() for " + templateClass.getType(), t);
                fullySuccessful = false;
            }
        }

        if (!fullySuccessful) {
            fail("Some generated reflection template classes could not be fully loaded or initialized");
        }

        // These are optional, but they must work at runtime depending on version
        if (Common.evaluateMCVersion(">=", "1.14")) {
            ClientboundAddEntityPacketHandle.T.opt_entityType.forceInitialization();
        } else {
            ClientboundAddEntityPacketHandle.T.opt_entityTypeId.forceInitialization();
        }
    }

    /**
     * Performs the {@link CommonBootstrap#preloadTemplateClasses(Random)} and checks whether
     * it deadlocks (fails to complete within 30 seconds). This test can be run
     * on repeat to identify issues at random class initialization orders.
     *
     * @throws Throwable If anything goes wrong
     */
    @Test
    @Ignore
    public void testPreloadTemplatesDeadlock() throws Throwable {
        // Force a particular seed, or generate a random seed to test with each time
        long seed = new java.util.Random().nextLong();
        // seed = 890821943411959027L;
        Logging.LOGGER_DEBUG.info("Class Initialization Seed: " + seed);

        // Timeout
        final long preloadTimeoutMillis = 30000;

        // This is not part of the test so run this right now
        // It's also required to actually initialize the template classes
        CommonBootstrap.initServer();

        // Run initialization asynchronously
        long startTime = System.currentTimeMillis();
        final long seedFinal = seed;
        CompletableFuture<Void> initFuture = CommonUtil.runCheckedAsync(() -> {
            CommonBootstrap.preloadTemplateClasses(new java.util.Random(seedFinal));
        });

        try {
            initFuture.get(preloadTimeoutMillis, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            DebugUtil.logStackTraces(() -> Thread.getAllStackTraces().keySet());
            Thread.sleep(1000); // Give some time for logging to complete...
            throw new RuntimeException("Initialization test hung!");
        } catch (CompletionException ex) {
            throw ex.getCause();
        }

        Logging.LOGGER_DEBUG.info("Finished initializing templates after " + (System.currentTimeMillis() - startTime) + " millis");
    }

    @Test
    public void testEnum() {
        assertNotNull(DirectionHandle.AxisHandle.T.X.raw.get());
        assertNotNull(AxisHandle.T.X.get());
        assertNotNull(DirectionHandle.AxisHandle.X);
    }

    // Tests optional fields/methods to make sure they exist when needed
    @Test
    public void testOptionalMembers() {
        assertAvailable(EntityTrackerEntryStateHandle.T.opt_passengers, EntityTrackerEntryStateHandle.T.opt_vehicle);
        if (CommonCapabilities.DATAWATCHER_OBJECTS) {
            assertAvailable(EntityHandle.T.DATA_FLAGS);
            assertAvailable(EntityHandle.T.DATA_CUSTOM_NAME);
            assertAvailable(EntityHandle.T.DATA_CUSTOM_NAME_VISIBLE);
            assertAvailable(SynchedEntityDataHandle.DataItemHandle.T.key);
        } else {
            assertAvailable(SynchedEntityDataHandle.DataItemHandle.T.keyId);
        }
    }

    @Test
    public void testChatColorConversion() {
        testChatColor(ChatColor.BLACK, 0);
        testChatColor(ChatColor.RED, 12);
        testChatColor(ChatColor.RESET, -1);
    }

    @Test
    public void testEntityInteractFunction() {
        assertTrue(EntityHandle.T.onInteractBy_26_1.isAvailable()
                || EntityHandle.T.onInteractBy_1_16.isAvailable()
                || EntityHandle.T.onInteractBy_1_11_2.isAvailable()
                || EntityHandle.T.onInteractBy_1_9.isAvailable()
                || EntityHandle.T.onInteractBy_1_8_8.isAvailable());
    }

    private static void testChatColor(ChatColor expectedColor, int expectedIndex) {
        String token = Character.toString(StringUtil.CHAT_STYLE_CHAR) + Character.toString(expectedColor.getChar());
        Object nmsEnumChatFormat = HandleConversion.chatColorToEnumChatFormatHandle(expectedColor);
        assertNotNull(nmsEnumChatFormat);
        assertEquals(token, nmsEnumChatFormat.toString());
        ChatColor color = WrapperConversion.chatColorFromEnumChatFormatHandle(nmsEnumChatFormat);
        assertNotNull(color);
        assertEquals(expectedColor, color);

        int index = HandleConversion.chatColorToEnumChatFormatIndex(expectedColor);
        assertEquals(expectedIndex, index);
        color = WrapperConversion.chatColorFromEnumChatFormatIndex(index);
        assertNotNull(color);
        assertEquals(expectedColor, color);
    }

    @Test
    public void testArmorStandDataWatcherFields() {
        if (Common.evaluateMCVersion(">=", "1.9")) {
            assertTrue(ArmorStandHandle.T.DATA_ARMORSTAND_FLAGS.isAvailable());
            assertTrue(ArmorStandHandle.T.DATA_POSE_ARM_LEFT.isAvailable());
            assertTrue(ArmorStandHandle.T.DATA_POSE_ARM_RIGHT.isAvailable());
            assertTrue(ArmorStandHandle.T.DATA_POSE_BODY.isAvailable());
            assertTrue(ArmorStandHandle.T.DATA_POSE_HEAD.isAvailable());
            assertTrue(ArmorStandHandle.T.DATA_POSE_LEG_LEFT.isAvailable());
            assertTrue(ArmorStandHandle.T.DATA_POSE_LEG_RIGHT.isAvailable());
        }
    }

    @Test
    public void testVector3fConversion() {
        {
            RotationsHandle v = RotationsHandle.fromBukkit(new org.bukkit.util.Vector(1, 2, 3));
            assertEquals(1.0f, v.getX(), 1e-5f);
            assertEquals(2.0f, v.getY(), 1e-5f);
            assertEquals(3.0f, v.getZ(), 1e-5f);
        }

        {
            RotationsHandle v = RotationsHandle.createNew(1.0f, 2.0f, 3.0f);
            assertEquals(1.0f, v.getX(), 1e-5f);
            assertEquals(2.0f, v.getY(), 1e-5f);
            assertEquals(3.0f, v.getZ(), 1e-5f);
        }
    }

    private void assertAvailable(Template.TemplateElement<?>... elements) {
        for (Template.TemplateElement<?> e : elements) {
            if (e.isAvailable()) {
                return;
            }
        }
        if (elements.length == 1) {
            throw new IllegalStateException("Optional template member not found: " + elements[0].getElementName());
        }
        throw new IllegalStateException("None of these optional template members could be found");
    }

    private String trimAfter(String path, String prefix) {
        int idx = path.indexOf(prefix);
        if (idx != -1) {
            idx += prefix.length();

            // Must start with a format the likes of v1_11_R1.<anything>
            if (path.substring(idx).matches("v\\d+(_\\d+)+_R\\d+..*")) {
                path = path.substring(0, idx) + path.substring(path.indexOf('.', idx) + 1);
            }
        }
        return path;
    }

    @Test
    public void testEntityHook() {
        Object entity = MinecartHandle.T.newInstanceNull();
        EntityHook hook = new EntityHook();
        assertNotNull(hook.hook(entity));
    }
}
