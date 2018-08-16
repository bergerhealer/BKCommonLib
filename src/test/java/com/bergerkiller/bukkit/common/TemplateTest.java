package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.bukkit.Material;
import org.junit.Test;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler_1_13;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.HeightMap;
import com.bergerkiller.bukkit.common.wrappers.ResourceKey;
import com.bergerkiller.generated.net.minecraft.server.PlayerChunkMapHandle;
import com.bergerkiller.generated.net.minecraft.server.SoundEffectTypeHandle;
import com.bergerkiller.generated.net.minecraft.server.ChunkProviderServerHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumDirectionHandle.EnumAxisHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

public class TemplateTest {

    static {
        CommonUtil.bootstrap();
    }

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

            // MC 1.8 class translation fixes
            {
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.EnumAxisHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.EnumDirectionHandle.EnumAxisHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.TileEntityMobSpawnerDataHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.MobSpawnerDataHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.WatchableObjectHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.DataWatcherHandle.ItemHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.StepSoundHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.SoundEffectTypeHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.BiomeMetaHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.BiomeBaseHandle.BiomeMetaHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.WeightedRandomChoiceHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.WeightedRandomHandle.WeightedRandomChoiceHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.ChatSerializerHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.IChatBaseComponentHandle.ChatSerializerHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.EnumScoreboardActionHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.PacketPlayOutScoreboardScoreHandle.EnumScoreboardActionHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.PacketPlayOutEntityLookHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.PacketPlayOutEntityHandle.PacketPlayOutEntityLookHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.PacketPlayOutRelEntityMoveLookHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.PacketPlayOutEntityHandle.PacketPlayOutRelEntityMoveLookHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.PacketPlayOutRelEntityMoveHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.PacketPlayOutEntityHandle.PacketPlayOutRelEntityMoveHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.EnumTitleActionHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.PacketPlayOutTitleHandle.EnumTitleActionHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.ChunkMapHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.PacketPlayOutMapChunkHandle.ChunkMapHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.EnumPlayerDigTypeHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.PacketPlayInBlockDigHandle.EnumPlayerDigTypeHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.EnumEntityUseActionHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.PacketPlayInUseEntityHandle.EnumEntityUseActionHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.PlayerInfoDataHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.PacketPlayOutPlayerInfoHandle.PlayerInfoDataHandle";
                }
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.EnumPlayerInfoActionHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.PacketPlayOutPlayerInfoHandle.EnumPlayerInfoActionHandle";
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

            // MC 1.13 class translation fixed
            {
                if (genClassPath.equals("com.bergerkiller.generated.net.minecraft.server.ScoreboardServerHandle.ActionHandle")) {
                    genClassPath = "com.bergerkiller.generated.net.minecraft.server.PacketPlayOutScoreboardScoreHandle.EnumScoreboardActionHandle";
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
                Logging.LOGGER_REFLECTION.severe("Failed to initialize " + templateClass.getType());
                throw MountiplexUtil.uncheckedRethrow(t);
            }
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
        assertAvailable(PlayerChunkMapHandle.T.getChunk_1_8_8, PlayerChunkMapHandle.T.getChunk_1_9);
        assertAvailable(EntityTrackerEntryHandle.T.opt_passengers, EntityTrackerEntryHandle.T.opt_vehicle);
        assertAvailable(ChunkProviderServerHandle.T.saveChunk_old, ChunkProviderServerHandle.T.saveChunk_new);
        if (CommonCapabilities.DATAWATCHER_OBJECTS) {
            assertAvailable(EntityHandle.T.DATA_FLAGS);
            assertAvailable(EntityHandle.T.DATA_CUSTOM_NAME);
            assertAvailable(EntityHandle.T.DATA_CUSTOM_NAME_VISIBLE);
        }
    }

    @Test
    public void testBlockPlaceSound() {
        assertTrue(SoundEffectTypeHandle.T.isAvailable());
        ResourceKey stepName = BlockData.fromMaterial(Material.GRASS).getPlaceSound();
        if (CommonCapabilities.KEYED_EFFECTS) {
            assertEquals("minecraft:block.grass.place", stepName.getPath());
        } else {
            assertEquals("minecraft:dig.grass", stepName.getPath());
        }
    }

    @Test
    public void testHeightMapTypeConversion() {
        HeightMap.Type input = HeightMap.Type.LIGHT_BLOCKING;
        Class<?> nmstype = CommonUtil.getNMSClass("HeightMap.Type");
        assertNotNull(nmstype);
        Converter<?, ?> converter = Conversion.find(HeightMap.Type.class, nmstype);
        assertNotNull(converter);

        Enum<?> output = (Enum<?>) converter.convert(input);
        assertNotNull(output);
        assertEquals(nmstype, output.getClass());
        assertEquals("LIGHT_BLOCKING", output.name());
    }

    @Test
    public void testEntityMoveHandlerInitialization() {
        if (Common.evaluateMCVersion(">=", "1.13")) {
            assertTrue("EntityMoveHandler Block Collision method failed to initialize", EntityMoveHandler_1_13.isBlockCollisionsMethodInitialized());
        }
    }

    private void assertAvailable(Template.TemplateElement<?>... elements) {
        for (Template.TemplateElement<?> e : elements) {
            if (e.isAvailable()) {
                return;
            }
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
}
