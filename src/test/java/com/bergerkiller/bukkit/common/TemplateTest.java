package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.stream.Stream;

import com.bergerkiller.bukkit.common.utils.DebugUtil;
import com.bergerkiller.generated.net.minecraft.core.Vector3fHandle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.junit.Ignore;
import org.junit.Test;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.cdn.MojangMappings;
import com.bergerkiller.bukkit.common.internal.cdn.MojangSpigotRemapper;
import com.bergerkiller.bukkit.common.internal.cdn.SpigotMappings;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.generated.net.minecraft.core.EnumDirectionHandle.EnumAxisHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityHandle;
import com.bergerkiller.generated.net.minecraft.network.syncher.DataWatcherHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.EntityArmorStandHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.EntityMinecartRideableHandle;
import com.bergerkiller.mountiplex.logic.TextValueSequence;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

public class TemplateTest {

    @Test
    @Ignore
    public void testVersionDiff() {
        //String className = "net.minecraft.world.level.block.state.BlockBase$BlockData"; // IBlockData
        //String className = "net.minecraft.world.level.block.state.BlockBase";
        String className = "net.minecraft.world.phys.shapes.VoxelShape";
        String methodName = "c";
        int methodParamCount = 0;
        String versionFrom = "1.17.1";
        String versionTo = "1.18";

        // Collect matches on previous version
        List<MojangMappings.MethodSignature> matches = new ArrayList<>();
        {
            MojangMappings mappings = MojangMappings.fromCacheOrDownload(versionFrom);
            mappings = mappings.translateClassNames(SpigotMappings.fromCacheOrDownload(versionFrom)::toSpigot);

            if (methodName.isEmpty()) {
                // Just list them all
                System.out.println("All methods of " + className + ":");
                for (MojangMappings.MethodSignature sig : mappings.forClassIfExists(className).methods) {
                    System.out.println("- " + sig);
                }
                return;
            } else {
                for (MojangMappings.MethodSignature sig : mappings.forClassIfExists(className).methods) {
                    if (sig.name.equals(methodName) || sig.name_obfuscated.equals(methodName)) {
                        if (sig.parameterTypes.size() == methodParamCount) {
                            matches.add(sig);
                        }
                    }
                }
            }
        }
        if (matches.isEmpty()) {
            System.out.println("No matches!");
            return;
        }

        // Find them again on the new version
        System.out.println("Method matches for '" + methodName + "' on MC " + versionFrom + ", for MC " + versionTo + ":");
        {
            MojangMappings mappings = MojangMappings.fromCacheOrDownload(versionTo);
            mappings = mappings.translateClassNames(SpigotMappings.fromCacheOrDownload(versionTo)::toSpigot);
            List<MojangMappings.MethodSignature> newSignatures = mappings.forClassIfExists(className).methods;

            for (MojangMappings.MethodSignature sig : matches) {
                System.out.println("Similar matches for:");
                System.out.println("  [" + versionFrom + "]: " + sig);

                for (MojangMappings.MethodSignature newSig : newSignatures) {
                    if (newSig.name.equals(sig.name)) {
                        System.out.println("  [" + versionTo + "]: " + newSig);
                    }
                }
            }
        }
    }

    @Test
    public void testTestServerInitialized() {
        CommonBootstrap.initServer();
        assertNotNull(Bukkit.getServer());
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

            // MC 1.8.9 class translation fixes
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

        // Attempt to fully initialize all declarations
        for (Template.Class<?> templateClass : classes) {
            try {
                templateClass.forceInitialization();
            } catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to run forceInitialization()", t);
                fullySuccessful = false;
            }
        }

        if (!fullySuccessful) {
            fail("Some generated reflection template classes could not be fully loaded or initialized");
        }

        // These are optional, but they must work at runtime depending on version
        if (Common.evaluateMCVersion(">=", "1.14")) {
            PacketPlayOutSpawnEntityHandle.T.opt_entityType.forceInitialization();
        } else {
            PacketPlayOutSpawnEntityHandle.T.opt_entityTypeId.forceInitialization();
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
    public void testArmorStandDataWatcherFields() {
        if (Common.evaluateMCVersion(">=", "1.9")) {
            assertTrue(EntityArmorStandHandle.T.DATA_ARMORSTAND_FLAGS.isAvailable());
            assertTrue(EntityArmorStandHandle.T.DATA_POSE_ARM_LEFT.isAvailable());
            assertTrue(EntityArmorStandHandle.T.DATA_POSE_ARM_RIGHT.isAvailable());
            assertTrue(EntityArmorStandHandle.T.DATA_POSE_BODY.isAvailable());
            assertTrue(EntityArmorStandHandle.T.DATA_POSE_HEAD.isAvailable());
            assertTrue(EntityArmorStandHandle.T.DATA_POSE_LEG_LEFT.isAvailable());
            assertTrue(EntityArmorStandHandle.T.DATA_POSE_LEG_RIGHT.isAvailable());
        }
    }

    @Test
    public void testVector3fConversion() {
        {
            Vector3fHandle v = Vector3fHandle.fromBukkit(new org.bukkit.util.Vector(1, 2, 3));
            assertEquals(1.0f, v.getX(), 1e-5f);
            assertEquals(2.0f, v.getY(), 1e-5f);
            assertEquals(3.0f, v.getZ(), 1e-5f);
        }

        {
            Vector3fHandle v = Vector3fHandle.createNew(1.0f, 2.0f, 3.0f);
            assertEquals(1.0f, v.getX(), 1e-5f);
            assertEquals(2.0f, v.getY(), 1e-5f);
            assertEquals(3.0f, v.getZ(), 1e-5f);
        }
    }

    @Test
    @Ignore
    public void testMojangSpigotRemapper() throws Throwable {
        MojangSpigotRemapper remapper = MojangSpigotRemapper.load("1.17.1", p -> p);
 
        Class<?> pathFinderTargetCondition = Class.forName("net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition",
                false, TemplateTest.class.getClassLoader());

        // Base
        {
            Class<?> entityLivingType = Class.forName("net.minecraft.world.entity.EntityLiving",
                    false, TemplateTest.class.getClassLoader());

            String remapped = remapper.remapMethodName(entityLivingType, "canAttack", new Class<?>[] {
                entityLivingType, pathFinderTargetCondition
            }, "BAD");

            System.out.println("LivingEntity canAttack -> " + remapped);

            // Reverse it
            String original = remapper.remapMethodNameReverse(entityLivingType, remapped, new Class<?>[] {
                entityLivingType, pathFinderTargetCondition
            }, "BAD");

            assertEquals("canAttack", original);
        }

        // Test works recursive as well
        {
            Class<?> entityCreeperType = Class.forName("net.minecraft.world.entity.monster.EntityCreeper",
                    false, TemplateTest.class.getClassLoader());

            String remapped = remapper.remapMethodName(entityCreeperType, "canAttack", new Class<?>[] {
                entityCreeperType, pathFinderTargetCondition
            }, "BAD");

            System.out.println("Creeper canAttack -> " + remapped);

            // Reverse it
            String original = remapper.remapMethodNameReverse(entityCreeperType, remapped, new Class<?>[] {
                entityCreeperType, pathFinderTargetCondition
            }, "BAD");

            assertEquals("canAttack", original);
        }

        // Field check
        {
            Class<?> entityLivingType = Class.forName("net.minecraft.world.entity.EntityLiving",
                    false, TemplateTest.class.getClassLoader());

            String remapped = remapper.remapFieldName(entityLivingType, "SPEED_MODIFIER_POWDER_SNOW_UUID", "BAD");

            System.out.println("LivingEntity SPEED_MOD -> " + remapped);

            // Reverse it
            String original = remapper.remapFieldNameReverse(entityLivingType, remapped, "BAD");

            assertEquals("SPEED_MODIFIER_POWDER_SNOW_UUID", original);
        }

        // Test that methods with names similar to java interface methods are remapped properly
        // This here tests NBTTagList, which has a get(int) method that remaps, but List.get() also exists.
        {
            Class<?> nbtTagListType = Class.forName("net.minecraft.nbt.NBTTagList");

            String remapped = remapper.remapMethodName(nbtTagListType, "get", new Class<?>[] {
                int.class
            }, "BAD");

            assertNotEquals("get", remapped);
            System.out.println("NBTTagList get(int) -> " + remapped);

            // Reverse it
            String original = remapper.remapMethodNameReverse(nbtTagListType, remapped, new Class<?>[] {
                int.class
            }, "BAD");

            assertEquals("get", original);
        }
    }

    @Test
    public void testGenerateBukkitClassMapping() {
        CommonBootstrap.initCommonServerAssertCompatibility();

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
                    .filter(s -> TextValueSequence.evaluateText(s, ">=", "1.17"))
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
