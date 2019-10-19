package com.bergerkiller.bukkit.common.internal.legacy;

import static com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials.getLegacyMaterial;
import static com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials.getBlockDataFromMaterialName;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.IBlockDataHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * Factory specialized in converting from legacy Bukkit MaterialData to
 * IBlockData values that preserve all original legacy MaterialData states.
 */
@SuppressWarnings("deprecation")
public class MaterialDataToIBlockData {
    private static final FastMethod<Object> craftBukkitgetIBlockData = new FastMethod<Object>();
    private static final Map<Material, IBlockDataBuilder<?>> iblockdataBuilders = new EnumMap<Material, IBlockDataBuilder<?>>(Material.class);

    static {
        // Initialize craftBukkitgetIBlockData to a runtime-generated method
        {
            ClassResolver resolver = new ClassResolver();
            resolver.setDeclaredClass(CommonUtil.getCBClass("util.CraftMagicNumbers"));
            if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
                craftBukkitgetIBlockData.init(new MethodDeclaration(resolver, 
                        "public static net.minecraft.server.IBlockData getIBlockData(org.bukkit.material.MaterialData materialdata) {\n" +
                        "    return CraftMagicNumbers.getBlock(materialdata);\n" +
                        "}"
                ));
            } else {
                craftBukkitgetIBlockData.init(new MethodDeclaration(resolver, 
                        "public static net.minecraft.server.IBlockData getIBlockData(org.bukkit.material.MaterialData materialdata) {\n" +
                        "    net.minecraft.server.Block block = CraftMagicNumbers.getBlock(materialdata.getItemType());\n" +
                        "    try {\n" +
                        "        return block.fromLegacyData((int) materialdata.getData());\n" +
                        "    } catch (IllegalArgumentException ex) {\n" +
                        "        return block.getBlockData();\n" +
                        "    }\n" +
                        "}"
                ));
            }
        }

        // Initialize custom builder functions for some material types
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            initBuilders();
        }
    }

    // Only called after MC 1.13, before that everything was fine!
    private static void initBuilders() {
        if (getLegacyMaterial("REDSTONE_COMPARATOR_OFF") != null) {
            iblockdataBuilders.put(getLegacyMaterial("REDSTONE_COMPARATOR_OFF"), (iblockdata, comparator) -> {
                iblockdata = iblockdata.set("powered", false);
                iblockdata = iblockdata.set("facing", comparator.getFacing());
                iblockdata = iblockdata.set("mode", comparator.isSubtractionMode() ? "subtract" : "compare");
                return iblockdata;
            });
        }
        if (getLegacyMaterial("REDSTONE_COMPARATOR_ON") != null) {
            iblockdataBuilders.put(getLegacyMaterial("REDSTONE_COMPARATOR_ON"), (iblockdata, comparator) -> {
                iblockdata = iblockdata.set("powered", true);
                iblockdata = iblockdata.set("facing", comparator.getFacing());
                iblockdata = iblockdata.set("mode", comparator.isSubtractionMode() ? "subtract" : "compare");
                return iblockdata;
            });
        }

        iblockdataBuilders.put(getLegacyMaterial("DOUBLE_STEP"), (iblockdata, step) -> iblockdata.set("type", "double"));

        iblockdataBuilders.put(getLegacyMaterial("MOB_SPAWNER"), (iblockdata, spawner) -> getBlockDataFromMaterialName("SPAWNER"));
        iblockdataBuilders.put(getLegacyMaterial("TORCH"), (iblockdata, torch) -> {
            if (torch.getAttachedFace() == BlockFace.DOWN) {
                iblockdata = getBlockDataFromMaterialName("TORCH");
                return iblockdata;
            } else {
                iblockdata = getBlockDataFromMaterialName("WALL_TORCH");
                return iblockdata.set("facing", torch.getFacing());
            }
        });

        // Name remapping without special MaterialData
        storeLegacyRemap("MELON_BLOCK", "MELON");

        // Signs
        {
            IBlockDataBuilder<org.bukkit.material.Sign> builder = new IBlockDataBuilder<org.bukkit.material.Sign>() {
                final IBlockDataHandle wall_sign_data = getBlockDataFromMaterialName(CommonCapabilities.HAS_MATERIAL_SIGN_TYPES ? "OAK_WALL_SIGN" : "WALL_SIGN");
                final IBlockDataHandle sign_post_data = getBlockDataFromMaterialName(CommonCapabilities.HAS_MATERIAL_SIGN_TYPES ? "OAK_SIGN" : "SIGN");

                @Override
                public IBlockDataHandle create(IBlockDataHandle iblockdata, org.bukkit.material.Sign sign) {
                    if (CommonLegacyMaterials.isLegacy(sign.getItemType())) {
                        iblockdata = sign.isWallSign() ? wall_sign_data : sign_post_data;
                    }
                    if (sign.isWallSign()) {
                        return iblockdata.set("facing", sign.getFacing());
                    } else {
                        return iblockdata.set("rotation", (int) sign.getData());
                    }
                }
            };
            iblockdataBuilders.put(getLegacyMaterial("WALL_SIGN"), builder);
            iblockdataBuilders.put(getLegacyMaterial("SIGN_POST"), builder);
        }

        // Chests
        {
            Material[] legacy_types = CommonLegacyMaterials.getAllByName("LEGACY_CHEST", "LEGACY_ENDER_CHEST", "LEGACY_TRAPPED_CHEST");
            String[] modern_names = new String[] {"CHEST", "ENDER_CHEST", "TRAPPED_CHEST"};
            for (int n = 0; n < legacy_types.length; n++) {
                final Material legacy_type = legacy_types[n];
                final IBlockDataHandle modern_data = CommonLegacyMaterials.getBlockDataFromMaterialName(modern_names[n]);
                iblockdataBuilders.put(legacy_type, (iblockdata, directional) -> modern_data.set("facing", directional.getFacing()));
            }
        }
    }

    private static void storeLegacyRemap(String legacy_name, String modern_name) {
        final IBlockDataHandle modern_data = getBlockDataFromMaterialName(modern_name);
        iblockdataBuilders.put(getLegacyMaterial(legacy_name), (iblockdata, materialData) -> modern_data);
    }

    /**
     * Converts MaterialData to the best appropriate IBlockData value
     * 
     * @param materialdata
     * @return IBlockData
     */
    public static IBlockDataHandle getIBlockData(MaterialData materialdata) {
        if (materialdata == null) {
            throw new IllegalArgumentException("MaterialData == null");
        }
        if (materialdata.getItemType() == null) {
            throw new IllegalArgumentException("MaterialData getItemType() == null");
        }

        Material legacyType = CommonLegacyMaterials.toLegacy(materialdata.getItemType());
        IBlockDataBuilder<MaterialData> builder = CommonUtil.unsafeCast(iblockdataBuilders.get(legacyType));
        IBlockDataHandle blockData = IBlockDataHandle.createHandle(craftBukkitgetIBlockData.invoke(null, materialdata));
        if (builder != null) {
            // Convert using createData to fix up a couple issues with MaterialData Class typing
            materialdata = IBlockDataToMaterialData.createMaterialData(materialdata.getItemType(), legacyType, materialdata.getData());
            blockData = builder.create(blockData, materialdata);
        }
        return blockData;
    }

    private static interface IBlockDataBuilder<M extends MaterialData> {
        IBlockDataHandle create(IBlockDataHandle iblockdata, M materialdata);
    }
}
