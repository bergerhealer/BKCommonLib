package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.cdn.MojangMappings;
import com.bergerkiller.bukkit.common.internal.cdn.MojangRemapper;
import com.bergerkiller.bukkit.common.internal.cdn.SpigotMappings;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests and generates the MojangMap <> Spigot class name mappings. These are serialized and included
 * in the jar resources as class_mappings.dat.<br>
 * <br>
 * BKCommonLib in general tries to use the mojang class names of the latest Minecraft version.
 * The class mappings specifically map these newest mojang class names to the spigot class names for
 * older versions. Beyond version 1.21.11 (26.1) these mappings are not used.
 */
public class SpigotMappingTest {
    private static final File mappingsFile = new File("src/main/resources/com/bergerkiller/bukkit/common/internal/resources/class_mappings.dat");

    /**
     * Visualizes the spigot <> mojang mapping details for debug/dev purposes
     */
    @Test
    @Ignore
    public void testVisualizeMappings() {
        SpigotMappings mappings = loadMappings();

        mappings.visualizeMapping("net.minecraft.network.syncher.SynchedEntityData$DataValue");

        //mappings.visualizeMappingsForSpigotClass("net.minecraft.server.DataPackResources");

        //mappings.visualizeMappingsForMojangClass("net.minecraft.network.Connection$WrappedConsumer");

        //mappings.visualizeMappingsForSpigotClass("net.minecraft.network.protocol.game.PacketPlayOutStatistic");
        //mappings.visualizeMappingsForSpigotClass("net.minecraft.network.protocol.game.PacketPlayOutTabComplete");
    }

    /**
     * Run this test to make changes to the SpigotMappings / class_mappings.dat file.
     * If a particular remapping on a particular version is needed, run this test with the required modification
     * between read and write.
     */
    @Test
    @Ignore
    public void testModifyMojangSpigotClassMapping() {
        SpigotMappings mappings = loadMappings();

        //mappings.renameKey("old.path.to.Class", "new.path.to.Class");

        mappings.storeAppendForVersionRange("1.9","1.15.2",
                "net.minecraft.core.BlockPos$PooledBlockPosition",
                "net.minecraft.core.BlockPosition$PooledBlockPosition");

        saveMappings(mappings);
    }

    @Test
    @Ignore
    public void testGenerateBukkitClassMapping() {
        CommonBootstrap.initCommonServerAssertCompatibility();

        SpigotMappings mappings = loadMappings();

        // Spigot stopped using their own class names with Minecraft 26.1
        // So this logic really isn't useful anymore...
        /*
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
         */

        saveMappings(mappings);
    }

    @Test
    @Ignore
    public void testMojangSpigotRemapper() throws Throwable {
        MojangRemapper remapper = MojangRemapper.load("1.17.1", p -> p);

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
            mappings = mappings.translateClassNames(SpigotMappings.forVersion(versionFrom)::toSpigot);

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
            mappings = mappings.translateClassNames(SpigotMappings.forVersion(versionTo)::toSpigot);
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

    private static SpigotMappings loadMappings() {
        SpigotMappings mappings = new SpigotMappings();
        if (mappingsFile.exists()) {
            try {
                try (InputStream input = new FileInputStream(mappingsFile)) {
                    mappings.read(input);
                }
            } catch (IOException ex) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to read class mappings", ex);
            }
        }
        return mappings;
    }

    private static void saveMappings(SpigotMappings mappings) {
        Logging.LOGGER.warning("Class mappings have changed, writing out new file!");
        try {
            try (OutputStream output = new FileOutputStream(mappingsFile)) {
                mappings.write(output);
            }
        } catch (IOException ex) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to write class mappings", ex);
        }
    }
}
