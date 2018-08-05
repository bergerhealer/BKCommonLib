package com.bergerkiller.bukkit.common.internal.legacy;

import static com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials.getLegacyMaterial;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
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
                        "    return CraftMagicNumbers.getBlock(materialdata.getItemType()).fromLegacyData((int) materialdata.getData());\n" +
                        "}"
                ));
            }
        }

        // Initialize custom builder functions for some material types
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            initBuilders();
        }
    }

    // Only called on MC 1.13, before that everything was fine!
    private static void initBuilders() {
        iblockdataBuilders.put(getLegacyMaterial("REDSTONE_COMPARATOR_OFF"), new IBlockDataBuilder<org.bukkit.material.Comparator>() {
            @Override
            public IBlockDataHandle create(IBlockDataHandle iblockdata, org.bukkit.material.Comparator comparator) {
                iblockdata = iblockdata.set("powered", false);
                iblockdata = iblockdata.set("facing", comparator.getFacing());
                iblockdata = iblockdata.set("mode", comparator.isSubtractionMode() ? "subtract" : "compare");
                return iblockdata;
            }
        });
        iblockdataBuilders.put(getLegacyMaterial("REDSTONE_COMPARATOR_ON"), new IBlockDataBuilder<org.bukkit.material.Comparator>() {
            @Override
            public IBlockDataHandle create(IBlockDataHandle iblockdata, org.bukkit.material.Comparator comparator) {
                iblockdata = iblockdata.set("powered", true);
                iblockdata = iblockdata.set("facing", comparator.getFacing());
                iblockdata = iblockdata.set("mode", comparator.isSubtractionMode() ? "subtract" : "compare");
                return iblockdata;
            }
        });
        iblockdataBuilders.put(getLegacyMaterial("DOUBLE_STEP"), new IBlockDataBuilder<org.bukkit.material.Step>() {
            @Override
            public IBlockDataHandle create(IBlockDataHandle iblockdata, org.bukkit.material.Step step) {
                return iblockdata.set("type", "double");
            }
        });
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
        IBlockDataHandle blockData = IBlockDataHandle.createHandle(craftBukkitgetIBlockData.invoke(null, materialdata));
        IBlockDataBuilder<MaterialData> builder = CommonUtil.unsafeCast(iblockdataBuilders.get(materialdata.getItemType()));
        if (builder != null) {
            // Convert using createData to fix up a couple issues with MaterialData Class typing
            materialdata = IBlockDataToMaterialData.createMaterialData(materialdata.getItemType(), materialdata.getData());
            blockData = builder.create(blockData, materialdata);
        }
        return blockData;
    }

    private static interface IBlockDataBuilder<M extends MaterialData> {
        IBlockDataHandle create(IBlockDataHandle iblockdata, M materialdata);
    }
}
