package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.bukkit.ChatColor;
import org.junit.Test;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.cdn.SpigotMappings;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler_1_13;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler_1_14;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandler;
import com.bergerkiller.bukkit.common.map.markers.MapDisplayMarkers;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.generated.net.minecraft.core.EnumDirectionHandle.EnumAxisHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityHandle;
import com.bergerkiller.generated.net.minecraft.network.syncher.DataWatcherHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.EntityMinecartRideableHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

public class TemplateTest {

    @Test
    public void testTemplate() {
        boolean fullySuccessful = true;
        ArrayList<Template.Class<?>> classes = new ArrayList<Template.Class<?>>();
        for (ClassDeclaration dec : Common.TEMPLATE_RESOLVER.all()) {
            String genClassPath = "com.bergerkiller.generated";

            for (String part : dec.type.typePath.split("\\.")) {
                if (Character.isUpperCase(part.charAt(0))) {
                    genClassPath += "." + part + "Handle";
                } else {
                    genClassPath += "." + part;
                }
            }

            genClassPath = trimAfter(genClassPath, "org.bukkit.craftbukkit.");
            genClassPath = trimAfter(genClassPath, "net.minecraft.server.");

            // NBT classes have been moved
            {
                String prefix = "com.bergerkiller.generated.net.minecraft.nbt.NBTTag";
                if (genClassPath.startsWith(prefix)
                        && !genClassPath.equals("com.bergerkiller.generated.net.minecraft.nbt.NBTTagCompoundHandle")
                        && !genClassPath.equals("com.bergerkiller.generated.net.minecraft.nbt.NBTTagListHandle"))
                {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.nbt.NBTBaseHandle.NBTTag" + genClassPath.substring(prefix.length());
                }
            }

            // MC 1.8 class translation fixes
            {
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.core.EnumAxisHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.core.EnumDirectionHandle.EnumAxisHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.world.level.block.entity.TileEntityMobSpawnerDataHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.world.level.MobSpawnerDataHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.network.syncher.WatchableObjectHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.network.syncher.DataWatcherHandle.ItemHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.world.level.block.StepSoundHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.world.level.block.SoundEffectTypeHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.world.level.biome.SpawnRateHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.world.level.biome.BiomeSettingsMobsHandle.SpawnRateHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.util.WeightedRandomChoiceHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.util.WeightedRandomHandle.WeightedRandomChoiceHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.network.chat.ChatSerializerHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.network.chat.IChatBaseComponentHandle.ChatSerializerHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.EnumScoreboardActionHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutScoreboardScoreHandle.EnumScoreboardActionHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityLookHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityHandle.PacketPlayOutEntityLookHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutRelEntityMoveLookHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityHandle.PacketPlayOutRelEntityMoveLookHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutRelEntityMoveHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityHandle.PacketPlayOutRelEntityMoveHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.network.protocol.game.EnumTitleActionHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutTitleHandle.EnumTitleActionHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.network.protocol.game.ChunkMapHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutMapChunkHandle.ChunkMapHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.network.protocol.game.EnumPlayerDigTypeHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInBlockDigHandle.EnumPlayerDigTypeHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.network.protocol.game.EnumEntityUseActionHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInUseEntityHandle.EnumEntityUseActionHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.network.protocol.game.PlayerInfoDataHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutPlayerInfoHandle.PlayerInfoDataHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.network.protocol.game.EnumPlayerInfoActionHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutPlayerInfoHandle.EnumPlayerInfoActionHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.com.bergerkiller.bukkit.common.internal.logic.LongHashSet_pre_1_13_2Handle")) {
                    genClassPath = "com.bergerkiller.generated.com.bergerkiller.bukkit.common.internal.LongHashSetHandle";
                }
            }

            // <= MC 1.9 class translation fixes
            {
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.WorldSettingsHandle.EnumGamemodeHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.EnumGamemodeHandle";
                }
            }

            // Internal proxy classes that are named <NMSType>Proxy
            if (genClassPath.startsWith("com.bergerkiller.generated.com.bergerkiller.bukkit.common.internal.proxy")) {
                genClassPath = "com.bergerkiller.generated.net.minecraft.server." + genClassPath.substring(73);
            }

            // MC 1.8.8 class translation fixes
            {
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.MobSpawnerAbstractHandle.a")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.MobSpawnerDataHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.DataWatcherHandle.WatchableObjectHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.DataWatcherHandle.ItemHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.PlayerChunkMapHandle.PlayerChunkHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.PlayerChunkHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.BlockHandle.StepSoundHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.SoundEffectTypeHandle";
                }
            }

            // MC <= 1.12.2 uses HeightMap proxy
            {
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.HeightMapProxy_1_12_2Handle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.HeightMapHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.HeightMapProxy_1_12_2Handle.TypeHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.HeightMapHandle.TypeHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.VoxelShapeProxyHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.VoxelShapeHandle";
                }
            }

            // MC 1.13 class translation fixes
            {
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.ScoreboardServerHandle.ActionHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.PacketPlayOutScoreboardScoreHandle.EnumScoreboardActionHandle";
                }
            }

            // MC 1.14 class translation fixes
            {
                if (genClassPath.equals("com.bergerkiller.generated.org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectMapHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.IntHashMapHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectMapHandle.EntryHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.IntHashMapHandle.IntHashMapEntryHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.longs.LongSetHandle")) {
                    genClassPath = "com.bergerkiller.generated.com.bergerkiller.bukkit.common.internal.LongHashSetHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.longs.Long2ObjectMapHandle")) {
                    genClassPath = "com.bergerkiller.generated.org.bukkit.craftbukkit.util.LongObjectHashMapHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.PlayerChunkMapHandle.EntityTrackerHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryHandle";
                }
            }

            Class<?> genClass = CommonUtil.getClass(genClassPath, true);
            if (genClass == null) {
                System.out.println("Error occurred testing handle for " + dec.type.typePath);
                fail("Failed to find generated class at " + genClassPath);
            }
            try {
                java.lang.reflect.Field f = genClass.getField("T");
                if (f == null) {
                    fail("Failed to find template field in type " + genClassPath);
                }
                Template.Class<?> c = (Template.Class<?>) f.get(null);
                classes.add(c);
                if (c == null) {
                    fail("Failed to initialize template class " + genClassPath);
                }
                if (!c.isValid() && !c.isOptional()) {
                    System.err.println("Failed to fully load template class " + genClassPath);
                    fullySuccessful = false;
                }
            } catch (Throwable t) {
                throw new RuntimeException("Failed to test class "+  genClassPath, t);
            }
        }
        if (!fullySuccessful) {
            fail("Some generated reflection template classes could not be loaded");
        }

        // Attempt to fully initialize all declarations
        for (Template.Class<?> templateClass : classes) {
            try {
                templateClass.forceInitialization();
            } catch (Throwable t) {
                Logging.LOGGER_REFLECTION.severe("Failed to initialize " + templateClass.getHandleType().getName());
                throw MountiplexUtil.uncheckedRethrow(t);
            }
        }

        // These are optional, but they must work at runtime depending on version
        if (Common.evaluateMCVersion(">=", "1.14")) {
            PacketPlayOutSpawnEntityHandle.T.opt_entityType.forceInitialization();
        } else {
            PacketPlayOutSpawnEntityHandle.T.opt_entityTypeId.forceInitialization();
        }
    }

    @Test
    public void testEnum() {
        assertNotNull(EnumAxisHandle.T.X.raw.get());
        assertNotNull(EnumAxisHandle.T.X.get());
        assertNotNull(EnumAxisHandle.X);
    }

    // Tests optional fields/methods to make sure they exist when needed
    @Test
    public void testOptionalMembers() {
        assertAvailable(EntityTrackerEntryStateHandle.T.opt_passengers, EntityTrackerEntryStateHandle.T.opt_vehicle);
        if (CommonCapabilities.DATAWATCHER_OBJECTS) {
            assertAvailable(EntityHandle.T.DATA_FLAGS);
            assertAvailable(EntityHandle.T.DATA_CUSTOM_NAME);
            assertAvailable(EntityHandle.T.DATA_CUSTOM_NAME_VISIBLE);
            assertAvailable(DataWatcherHandle.ItemHandle.T.key);
        } else {
            assertAvailable(DataWatcherHandle.ItemHandle.T.keyId);
        }
    }

    @Test
    public void testRegionHandler() {
        RegionHandler.INSTANCE.forceInitialization();
    }

    @Test
    public void testPortalHandler() {
        PortalHandler.INSTANCE.forceInitialization();
    }

    @Test
    public void testChatColorConversion() {
        testChatColor(ChatColor.BLACK, 0);
        testChatColor(ChatColor.RED, 12);
        testChatColor(ChatColor.RESET, -1);
    }

    @Test
    public void testEntityInteractFunction() {
        assertTrue(EntityHandle.T.onInteractBy_1_16.isAvailable() ||
                   EntityHandle.T.onInteractBy_1_11_2.isAvailable() ||
                   EntityHandle.T.onInteractBy_1_9.isAvailable() ||
                   EntityHandle.T.onInteractBy_1_8_8.isAvailable());
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
    public void testEntityMoveHandlerInitialization() {
        if (Common.evaluateMCVersion(">=", "1.14")) {
            assertTrue("EntityMoveHandler Block Collision method failed to initialize", EntityMoveHandler_1_14.isBlockCollisionsMethodInitialized());
        } else if (Common.evaluateMCVersion(">=", "1.13")) {
            assertTrue("EntityMoveHandler Block Collision method failed to initialize", EntityMoveHandler_1_13.isBlockCollisionsMethodInitialized());
        }
    }

    @Test
    public void testMapDisplayMarkerApplier() {
        MapDisplayMarkers.APPLIER.forceInitialization();
    }

    @Test
    public void testGenerateBukkitClassMapping() {
        SpigotMappings mappings = new SpigotMappings();
        File mappingsFile = new File("src/main/resources/com/bergerkiller/bukkit/common/internal/resources/class_mappings.dat");
        if (mappingsFile.exists()) {
            try {
                try (InputStream input = new FileInputStream(mappingsFile)) {
                    mappings.read(input);
                }
            } catch (IOException ex) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to read class mappings", ex);
            }
        }

        // Make sure all mappings for versions we need are present
        try {
            if (!mappings.assertMappings(Stream.of(Common.TEMPLATE_RESOLVER.getSupportedVersions())
                    .filter(s -> MountiplexUtil.evaluateText(s, ">=", "1.17"))
                    .toArray(String[]::new))
            ) {
                return; // No changes
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to download some mappings", ex);
        }

        Logging.LOGGER.warning("Class mappings have changed, writing out new file!");
        try {
            try (OutputStream output = new FileOutputStream(mappingsFile)) {
                mappings.write(output);
            }
        } catch (IOException ex) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to write class mappings", ex);
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
        Object entity = EntityMinecartRideableHandle.T.newInstanceNull();
        EntityHook hook = new EntityHook();
        assertNotNull(hook.hook(entity));
    }
}
