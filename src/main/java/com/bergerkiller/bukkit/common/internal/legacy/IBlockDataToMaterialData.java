package com.bergerkiller.bukkit.common.internal.legacy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.IBlockDataHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.reflection.SafeMethod;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * Factory specialized in converting from legacy IBlockData values to
 * Bukkit MaterialData that preserve all original legacy MaterialData states.
 */
@SuppressWarnings("deprecation")
public class IBlockDataToMaterialData {
    public static final Map<Object, MaterialData> INTERNAL_IBLOCKDATA_TO_MATERIALDATA = new HashMap<Object, MaterialData>();
    private static final Map<Material, MaterialDataBuilder> materialdata_builders = new EnumMap<Material, MaterialDataBuilder>(Material.class);
    private static final Map<Material, Byte> materialdata_default_data = new EnumMap<Material, Byte>(Material.class);
    private static final FastMethod<MaterialData> craftbukkitGetMaterialdata = new FastMethod<MaterialData>();
    private static final EnumMap<Material, Material> materialToLegacyCache;

    static {
        // Stores a mapping from IBlockData to MaterialData for some values
        // Used on MC 1.13 and onwards for legacy conversions
        Map<Object, MaterialData> iblockdataToMaterialdata_map = Collections.emptyMap();
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            Class<?> craftLegacyClass = CommonUtil.getClass("org.bukkit.craftbukkit.legacy.CraftLegacy");
            if (craftLegacyClass != null) {
                try {
                    java.lang.reflect.Field f = craftLegacyClass.getDeclaredField("dataToMaterial");
                    f.setAccessible(true);
                    iblockdataToMaterialdata_map = CommonUtil.unsafeCast(f.get(null));
                    f.setAccessible(false);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        INTERNAL_IBLOCKDATA_TO_MATERIALDATA.putAll(iblockdataToMaterialdata_map);
        MaterialDataBuilder default_builder = (material_type, legacy_data_type, legacy_data_value) -> legacy_data_type.getNewData(legacy_data_value);

        // Generated method for generating MaterialData from IBlockData
        {
            ClassResolver resolver = new ClassResolver();
            resolver.setDeclaredClass(CommonUtil.getClass("org.bukkit.craftbukkit.util.CraftMagicNumbers"));
            resolver.setAllVariables(Common.TEMPLATE_RESOLVER);
            if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
                craftbukkitGetMaterialdata.init(new MethodDeclaration(resolver, 
                        "public static org.bukkit.material.MaterialData getMaterialData(net.minecraft.world.level.block.state.IBlockData iblockdata) {\n" +
                        "    Object materialdata_raw = com.bergerkiller.bukkit.common.internal.legacy.IBlockDataToMaterialData.INTERNAL_IBLOCKDATA_TO_MATERIALDATA.get(iblockdata);\n" +
                        "    org.bukkit.material.MaterialData materialdata = (org.bukkit.material.MaterialData) materialdata_raw;\n" +
                        "    org.bukkit.Material type = CraftMagicNumbers.getMaterial(iblockdata.getBlock());\n" +
                        "    org.bukkit.Material data_type;\n" +
                        "    byte data_value;\n" +
                        "    if (materialdata != null) {\n" +
                        "        data_type = materialdata.getItemType();\n" +
                        "        data_value = materialdata.getData();\n" +
                        "    } else {\n" +
                        "        data_type = org.bukkit.craftbukkit.legacy.CraftLegacy.toLegacy(type);\n" +
                        "        data_value = org.bukkit.craftbukkit.legacy.CraftLegacy.toLegacyData(iblockdata);\n" +
                        "    }\n" +
                        "    return com.bergerkiller.bukkit.common.internal.legacy.IBlockDataToMaterialData.createMaterialData(type, data_type, data_value);\n" +
                        "}"
                ));
            } else {
                craftbukkitGetMaterialdata.init(new MethodDeclaration(resolver, 
                        "public static org.bukkit.material.MaterialData getMaterialData(net.minecraft.world.level.block.state.IBlockData iblockdata) {\n" +
                        "    net.minecraft.world.level.block.Block block = iblockdata.getBlock();\n" +
                        "    org.bukkit.Material data_type = CraftMagicNumbers.getMaterial(block);\n" +
                        "    byte data_value = (byte) block.toLegacyData(iblockdata);\n" +
                        "    return com.bergerkiller.bukkit.common.internal.legacy.IBlockDataToMaterialData.createMaterialData(data_type, data_type, data_value);\n" +
                        "}"
                ));
            }
        }

        // Bukkit bugfix.
        storeBuilders((material_type, legacy_data_type, legacy_data_value) -> new org.bukkit.material.PressurePlate(material_type, legacy_data_value), "LEGACY_GOLD_PLATE", "LEGACY_IRON_PLATE");

        // Bukkit bugfix. (<= 1.8.3)
        storeBuilders((material_type, legacy_data_type, legacy_data_value) -> new org.bukkit.material.Door(material_type, legacy_data_value), "LEGACY_JUNGLE_DOOR", "LEGACY_ACACIA_DOOR", "LEGACY_DARK_OAK_DOOR", "LEGACY_SPRUCE_DOOR", "LEGACY_BIRCH_DOOR");

        // Default data values for some common Material types
        // This ensures getMaterialData() works correctly when used from BY_MATERIAL
        storeMaterialDataDefault("FURNACE", 2);
        storeMaterialDataDefault("BURNING_FURNACE", 2);
        storeMaterialDataDefault("REDSTONE_TORCH_OFF", 5);
        storeMaterialDataDefault("REDSTONE_TORCH_ON", 5);

        // Initialize missing defaults
        for (Material type : MaterialsByName.getAllMaterials()) {
            if (!materialdata_builders.containsKey(type)) {
                materialdata_builders.put(type, default_builder);
            }
            if (!materialdata_default_data.containsKey(type)) {
                materialdata_default_data.put(type, Byte.valueOf((byte) 0));
            }
        }

        // Special edge cases: some material types don't work so well. Fix those.
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            initMaterialDataMap();
        }

        // Make absolutely sure that IBlockData AIR stays AIR, because Bukkit sends back AIR when materials cannot be resolved
        // This fixes a rather serious issue of some random material data getting mapped to air.
        storeMaterialData(CraftMagicNumbersHandle.getBlockDataFromMaterial(Material.AIR), new MaterialData(CommonLegacyMaterials.getLegacyMaterial("AIR")));

        // Cache all the Material -> legacy material mappings to make lookup fast
        // Before this point materialToLegacy is null
        EnumMap<Material, Material> initMaterialToLegacy = new EnumMap<>(Material.class);
        for (Material material : MaterialsByName.getAllMaterials()) {
            initMaterialToLegacy.put(material, toLegacy(material));
        }
        materialToLegacyCache = initMaterialToLegacy;
    }

    // Only called on MC >= 1.13
    private static void initMaterialDataMap() {
        {
            MaterialData materialdata = new MaterialData(MaterialsByName.getMaterial("LEGACY_DOUBLE_STEP"));
            for (byte data = 0; data < 8; data++) {
                materialdata.setData(data);
                IBlockDataHandle iblockdata = MaterialDataToIBlockData.getIBlockData(materialdata);
                storeMaterialData(iblockdata.set("waterlogged", true), materialdata);
                storeMaterialData(iblockdata.set("waterlogged", false), materialdata);
            }
        }
        storeMaterialDataGen("LEGACY_REDSTONE_COMPARATOR_OFF", 0, 7);
        storeMaterialDataGen("LEGACY_REDSTONE_COMPARATOR_ON", 0, 7);
        storeMaterialDataGen("LEGACY_PORTAL", 1, 2);

        // Store 5 unique kinds of wood types for some materials that don't exist on MC 1.12.2, and thus have no legacy type
        // A littly hacky, because MaterialData should only use legacy materials, but in this instance it can work

        // Buttons
        {
            new CustomMaterialDataBuilder<org.bukkit.material.Button>() {
                @Override
                public org.bukkit.material.Button create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new org.bukkit.material.Button(material_type, legacy_data_value);
                }

                @Override
                public List<IBlockDataHandle> createStates(IBlockDataHandle iblockdata, org.bukkit.material.Button button) {
                    iblockdata = iblockdata.set("powered", button.isPowered());

                    BlockFace facing = button.getFacing();
                    if (!FaceUtil.isVertical(facing)) {
                        return Arrays.asList(iblockdata
                                .set("face", "WALL")
                                .set("facing", facing));
                    }

                    iblockdata = iblockdata.set("face", (facing == BlockFace.UP) ? "FLOOR" : "CEILING");
                    return Arrays.asList(iblockdata.set("facing", BlockFace.NORTH),
                                         iblockdata.set("facing", BlockFace.EAST),
                                         iblockdata.set("facing", BlockFace.SOUTH),
                                         iblockdata.set("facing", BlockFace.WEST));
                }
            }.setTypes("JUNGLE_BUTTON", "SPRUCE_BUTTON", "ACACIA_BUTTON",
                       "BIRCH_BUTTON", "DARK_OAK_BUTTON", "OAK_BUTTON", "STONE_BUTTON")
             .addTypesIf(Common.evaluateMCVersion(">=", "1.16"),
                       "CRIMSON_BUTTON", "WARPED_BUTTON", "POLISHED_BLACKSTONE_BUTTON")
             .addTypesIf(Common.evaluateMCVersion(">=", "1.17"),
                       "POLISHED_BLACKSTONE_BUTTON")
             .setDataValues(0,1,2,3,4,5, 8,9,10,11,12,13)
             .build();
        }

        // Levers
        {
            new CustomMaterialDataBuilder<org.bukkit.material.Lever>() {
                @Override
                public org.bukkit.material.Lever create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new org.bukkit.material.Lever(material_type, legacy_data_value);
                }

                @Override
                public List<IBlockDataHandle> createStates(IBlockDataHandle iblockdata, org.bukkit.material.Lever button) {
                    iblockdata = iblockdata.set("powered", button.isPowered());

                    BlockFace attached = button.getAttachedFace();
                    if (attached == BlockFace.UP) {
                        // Attached to the ceiling
                        // Facing controls what direction the lever points
                        // However, legacy only supports ambiguous {north/south} and {east/west}
                        BlockFace facing = ((button.getData() & 0x7) == 0x7) ? BlockFace.NORTH : BlockFace.EAST;
                        iblockdata = iblockdata.set("face", "ceiling");
                        return Arrays.asList(iblockdata.set("facing", facing),
                                             iblockdata.set("facing", facing.getOppositeFace()));
                    } else if (attached == BlockFace.DOWN) {
                        // Attached to the floor
                        // Facing controls what direction the lever points
                        // However, legacy only supports ambiguous {north/south} and {east/west}
                        BlockFace facing = ((button.getData() & 0x7) == 5) ? BlockFace.NORTH : BlockFace.EAST;
                        iblockdata = iblockdata.set("face", "floor");
                        return Arrays.asList(iblockdata.set("facing", facing),
                                             iblockdata.set("facing", facing.getOppositeFace()));
                    } else {
                        // Attached to a wall, facing = orientation on block
                        iblockdata = iblockdata.set("face", "wall");
                        iblockdata = iblockdata.set("facing", button.getFacing());
                        return Collections.singletonList(iblockdata);
                    }
                }
            }.setTypes("LEVER")
             .setDataValues(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15)
             .build();
        }

        // Pressureplates
        {
            new CustomMaterialDataBuilder<org.bukkit.material.PressurePlate>() {
                @Override
                public org.bukkit.material.PressurePlate create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new org.bukkit.material.PressurePlate(material_type, legacy_data_value);
                }

                @Override
                public List<IBlockDataHandle> createStates(IBlockDataHandle iblockdata, org.bukkit.material.PressurePlate plate) {
                    return Arrays.asList(iblockdata.set("powered", plate.isPressed()));
                }
            }.setTypes("JUNGLE_PRESSURE_PLATE", "SPRUCE_PRESSURE_PLATE", "ACACIA_PRESSURE_PLATE",
                       "BIRCH_PRESSURE_PLATE", "DARK_OAK_PRESSURE_PLATE")
             .addTypesIf(Common.evaluateMCVersion(">=", "1.16"),
                       "CRIMSON_PRESSURE_PLATE", "WARPED_PRESSURE_PLATE", "POLISHED_BLACKSTONE_PRESSURE_PLATE")
             .addTypesIf(Common.evaluateMCVersion(">=", "1.17"),
                     "POLISHED_BLACKSTONE_PRESSURE_PLATE")
             .setDataValues(0, 1)
             .build();
        }

        // Burning and not burning furnace (now both the same material, was separate legacy materials)
        for (boolean burning : new boolean[] {true, false}) {
            new CustomMaterialDataBuilder<org.bukkit.material.Furnace>() {
                @Override
                public Material toLegacy(Material m) {
                    return CommonLegacyMaterials.getLegacyMaterial(burning ? "BURNING_FURNACE" : "FURNACE");
                }

                @Override
                public org.bukkit.material.Furnace create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new org.bukkit.material.Furnace(toLegacy(material_type), legacy_data_value);
                }

                @Override
                public List<IBlockDataHandle> createStates(IBlockDataHandle iblockdata, org.bukkit.material.Furnace furnace) {
                    return Collections.singletonList(iblockdata.set("facing", furnace.getFacing()).set("lit", burning));
                }
            }.setTypes("FURNACE")
             .setDataValues(2, 3, 4, 5)
             .build();
        }

        // Wall torch
        {
            new CustomMaterialDataBuilder<org.bukkit.material.Torch>() {
                @Override
                public org.bukkit.material.Torch create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new org.bukkit.material.Torch(legacy_data_type, legacy_data_value);
                }

                @Override
                public List<IBlockDataHandle> createStates(IBlockDataHandle iblockdata, org.bukkit.material.Torch torch) {
                    return Collections.singletonList(iblockdata.set("facing", torch.getFacing()));
                }
            }.setTypes("WALL_TORCH")
             .setDataValues(1, 2, 3, 4)
             .build();
        }

        // Wall redstone torch
        for (boolean lit : new boolean[] {true, false}) {
            new CustomMaterialDataBuilder<org.bukkit.material.RedstoneTorch>() {
                @Override
                public Material toLegacy(Material m) {
                    return CommonLegacyMaterials.getLegacyMaterial(lit ? "REDSTONE_TORCH_ON" : "REDSTONE_TORCH_OFF");
                }

                @Override
                public org.bukkit.material.RedstoneTorch create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new org.bukkit.material.RedstoneTorch(toLegacy(material_type), legacy_data_value);
                }

                @Override
                public List<IBlockDataHandle> createStates(IBlockDataHandle iblockdata, org.bukkit.material.RedstoneTorch torch) {
                    return Collections.singletonList(iblockdata.set("facing", torch.getFacing()).set("lit", lit));
                }
            }.setTypes("REDSTONE_WALL_TORCH")
             .setDataValues(1, 2, 3, 4)
             .build();
        }

        // Redstone Wire has north/east/south/west metadata too, which also have to be registered
        // Format: minecraft:redstone_wire[east=side,north=none,power=12,south=none,west=none]
        {
            new CustomMaterialDataBuilder<org.bukkit.material.RedstoneWire>() {
                @Override
                public org.bukkit.material.RedstoneWire create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new org.bukkit.material.RedstoneWire(material_type, legacy_data_value);
                }

                @Override
                public List<IBlockDataHandle> createStates(IBlockDataHandle wire_data, org.bukkit.material.RedstoneWire wire) {
                    final String[] SIDE_VALUES = {"up", "side", "none"};
                    ArrayList<IBlockDataHandle> variants = new ArrayList<IBlockDataHandle>(3*3*3*3);
                    wire_data = wire_data.set("power", wire.getData());
                    for (String side_north : SIDE_VALUES) {
                        wire_data = wire_data.set("north", side_north);
                        for (String side_east : SIDE_VALUES) {
                            wire_data = wire_data.set("east", side_east);
                            for (String side_south : SIDE_VALUES) {
                                wire_data = wire_data.set("south", side_south);
                                for (String side_west : SIDE_VALUES) {
                                    wire_data = wire_data.set("west", side_west);
                                    variants.add(wire_data);
                                }
                            }
                        }
                    }
                    return variants;
                }

                @Override
                public Material fromLegacy(Material legacyMaterial) {
                    return CommonLegacyMaterials.getMaterial("REDSTONE_WIRE");
                }

            }.setTypes("LEGACY_REDSTONE_WIRE")
             .setDataValues(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15)
             .build();
        }

        // Chests
        {
            Material[] legacy_types = CommonLegacyMaterials.getAllByName("LEGACY_CHEST", "LEGACY_ENDER_CHEST", "LEGACY_TRAPPED_CHEST");
            Material[] modern_types = CommonLegacyMaterials.getAllByName("CHEST", "ENDER_CHEST", "TRAPPED_CHEST");
            for (int n = 0; n < legacy_types.length; n++) {
                final Material legacy_type = legacy_types[n];
                final Material modern_type = modern_types[n];
                final boolean isEnderChest = (n==1);

                new CustomMaterialDataBuilder<org.bukkit.material.DirectionalContainer>() {
                    @Override
                    public org.bukkit.material.DirectionalContainer create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                        if (isEnderChest) {
                            org.bukkit.material.EnderChest chest = new org.bukkit.material.EnderChest();
                            chest.setData(legacy_data_value);
                            return chest;
                        } else {
                            return new org.bukkit.material.Chest(material_type, legacy_data_value);
                        }
                    }

                    @Override
                    public List<IBlockDataHandle> createStates(IBlockDataHandle chest_data, org.bukkit.material.DirectionalContainer chest) {
                        chest_data = chest_data.set("facing", chest.getFacing());
                        return Arrays.asList(
                                chest_data.set("waterlogged", true).set("type", "SINGLE"),
                                chest_data.set("waterlogged", true).set("type", "LEFT"),
                                chest_data.set("waterlogged", true).set("type", "RIGHT"),
                                chest_data.set("waterlogged", false).set("type", "SINGLE"),
                                chest_data.set("waterlogged", false).set("type", "LEFT"),
                                chest_data.set("waterlogged", false).set("type", "RIGHT")
                        );
                    }

                    @Override
                    public Material fromLegacy(Material legacyMaterial) {
                        return modern_type;
                    }

                    @Override
                    public Material toLegacy(Material material) {
                        return legacy_type;
                    }

                }.setTypes(legacy_type, modern_type)
                 .setDataValues(2, 3, 4, 5)
                 .build();
            }
        }

        // Ladder can also be waterlogged since MC 1.15
        {
            new CustomMaterialDataBuilder<org.bukkit.material.Ladder>() {
                @Override
                public org.bukkit.material.Ladder create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new org.bukkit.material.Ladder(legacy_data_type, legacy_data_value);
                }

                @Override
                public List<IBlockDataHandle> createStates(IBlockDataHandle iblockdata, org.bukkit.material.Ladder ladder) {
                    IBlockDataHandle base = iblockdata.set("facing", ladder.getFacing());
                    return Arrays.asList(base.set("waterlogged", false), base.set("waterlogged", true));
                }
            }.setTypes("LADDER")
             .setDataValues(2, 3, 4, 5)
             .build();
        }

        if (CommonCapabilities.HAS_MATERIAL_SIGN_TYPES) {
            // LEGACY_WALL_SIGN is broken on 1.14
            {
                new CustomMaterialDataBuilder<org.bukkit.material.Sign>() {
                    @Override
                    public org.bukkit.material.Sign create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                        return new CommonSignDataFix(material_type, legacy_data_value, true);
                    }

                    @Override
                    public List<IBlockDataHandle> createStates(IBlockDataHandle iblockdata, org.bukkit.material.Sign sign) {
                        IBlockDataHandle base = iblockdata.set("facing", sign.getFacing());
                        return Arrays.asList(base.set("waterlogged", false), base.set("waterlogged", true));
                    }

                    @Override
                    public Material fromLegacy(Material legacyMaterial) {
                        return CommonLegacyMaterials.getMaterial("OAK_WALL_SIGN");
                    }

                    @Override
                    public Material toLegacy(Material material) {
                        return CommonLegacyMaterials.getLegacyMaterial("WALL_SIGN");
                    }
                }.setTypes("ACACIA_WALL_SIGN", "BIRCH_WALL_SIGN", "DARK_OAK_WALL_SIGN", "JUNGLE_WALL_SIGN", "OAK_WALL_SIGN", "SPRUCE_WALL_SIGN",
                           "LEGACY_WALL_SIGN")
                 .addTypesIf(CommonBootstrap.evaluateMCVersion(">=", "1.16"),
                           "CRIMSON_WALL_SIGN", "WARPED_WALL_SIGN")
                 .setDataValues(2, 3, 4, 5)
                 .build();
            }

            // Register new SIGN_POST types as well
            {
                new CustomMaterialDataBuilder<org.bukkit.material.Sign>() {
                    @Override
                    public org.bukkit.material.Sign create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                        return new CommonSignDataFix(material_type, legacy_data_value, false);
                    }

                    @Override
                    public List<IBlockDataHandle> createStates(IBlockDataHandle iblockdata, org.bukkit.material.Sign sign) {
                        IBlockDataHandle base = iblockdata.set("rotation", sign.getData());
                        return Arrays.asList(base.set("waterlogged", false), base.set("waterlogged", true));
                    }

                    @Override
                    public Material fromLegacy(Material legacyMaterial) {
                        return CommonLegacyMaterials.getMaterial("OAK_SIGN");
                    }

                    @Override
                    public Material toLegacy(Material material) {
                        return CommonLegacyMaterials.getLegacyMaterial("SIGN_POST");
                    }
                }.setTypes("ACACIA_SIGN", "BIRCH_SIGN", "DARK_OAK_SIGN", "JUNGLE_SIGN", "OAK_SIGN", "SPRUCE_SIGN",
                           "LEGACY_SIGN_POST")
                 .addTypesIf(CommonBootstrap.evaluateMCVersion(">=", "1.16"),
                           "CRIMSON_SIGN", "WARPED_SIGN")
                 .setDataValues(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15)
                 .build();
            }
        } else {
            // Fix LEGACY_WALL_SIGN
            {
                new CustomMaterialDataBuilder<org.bukkit.material.Sign>() {
                    @Override
                    public org.bukkit.material.Sign create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                        return new CommonSignDataFix(material_type, legacy_data_value, true);
                    }

                    @Override
                    public List<IBlockDataHandle> createStates(IBlockDataHandle iblockdata, org.bukkit.material.Sign sign) {
                        IBlockDataHandle base = iblockdata.set("facing", sign.getFacing());
                        return Arrays.asList(base.set("waterlogged", false), base.set("waterlogged", true));
                    }
                }.setTypes("WALL_SIGN",
                           "LEGACY_WALL_SIGN")
                 .setDataValues(2, 3, 4, 5)
                 .build();
            }

            // Fix LEGACY_SIGN_POST
            {
                new CustomMaterialDataBuilder<org.bukkit.material.Sign>() {
                    @Override
                    public org.bukkit.material.Sign create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                        return new CommonSignDataFix(material_type, legacy_data_value, false);
                    }

                    @Override
                    public List<IBlockDataHandle> createStates(IBlockDataHandle iblockdata, org.bukkit.material.Sign sign) {
                        IBlockDataHandle base = iblockdata.set("rotation", sign.getData());
                        return Arrays.asList(base.set("waterlogged", false), base.set("waterlogged", true));
                    }
                }.setTypes("SIGN",
                           "LEGACY_SIGN_POST")
                 .setDataValues(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15)
                 .build();
            }
        }

        // Minecraft 1.16 added a Target block, for which we have added a custom material class to support it
        if (Common.evaluateMCVersion(">=", "1.16")) {
            new CustomMaterialDataBuilder<CommonTargetDataFix>() {
                @Override
                public CommonTargetDataFix create(Material material_type, Material legacy_data_type, byte legacy_data_value) {
                    return new CommonTargetDataFix(material_type, legacy_data_value);
                }

                @Override
                public List<IBlockDataHandle> createStates(IBlockDataHandle iblockdata, CommonTargetDataFix target) {
                    return Collections.singletonList(iblockdata.set("power", Integer.valueOf(target.getData())));
                }
            }.setTypes("TARGET")
             .setDataValues(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15)
             .build();
        }
    }

    private static void storeMaterialDataGen(String legacyTypeName, int data_start, int data_end) {
        MaterialData materialdata = new MaterialData(CommonLegacyMaterials.getMaterial(legacyTypeName));
        for (int data = data_start; data <= data_end; data++) {
            materialdata.setData((byte) data);
            storeMaterialData(MaterialDataToIBlockData.getIBlockData(materialdata), materialdata);
        }
    }

    private static void storeMaterialData(IBlockDataHandle iblockdata, MaterialData materialdata) {
        INTERNAL_IBLOCKDATA_TO_MATERIALDATA.put(iblockdata.getRaw(), materialdata.clone());
    }

    private static void storeMaterialDataDefault(String name, int data) {
        materialdata_default_data.put(MaterialsByName.getLegacyMaterial(name), Byte.valueOf((byte) data));
    }

    /**
     * Obtains the Legacy Material type from a Material that gets closest to what can be represented.
     * For example, materials of wood that did not yet exist will return another type of wood that comes close.
     * By default will return the input material type, unless a custom conversion is required.
     * 
     * @param material
     * @return legacy material type approximation
     */
    public static Material toLegacy(Material material) {
        if (materialToLegacyCache != null) {
            return materialToLegacyCache.get(material);
        } else if (!CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            return material;
        } else if (MaterialsByName.isLegacy(material)) {
            return material;
        } else {
            Material legacy = materialdata_builders.get(material).toLegacy(material);
            if (legacy == material) {
                legacy = CraftBukkitToLegacy.toLegacy(material);
            }
            return legacy;
        }
    }

    /**
     * Converts IBlockData to the best appropriate MaterialData value
     * 
     * @param iblockdata
     * @return MaterialData
     */
    public static MaterialData getMaterialData(IBlockDataHandle iblockdata) {
        return craftbukkitGetMaterialdata.invoke(null, iblockdata.getRaw());
    }

    /**
     * Creates new MaterialData from a legacy Material type with a default legacy data value.
     * This method includes several bugfixes for bugs that exist in the Bukkit API.
     * 
     * @param legacy_data_type
     * @return MaterialData
     */
    public static MaterialData createMaterialData(Material legacy_data_type) {
        return createMaterialData(legacy_data_type, legacy_data_type, materialdata_default_data.get(legacy_data_type).byteValue());
    }

    /**
     * Creates new MaterialData from a legacy Material type and legacy data value.
     * This method includes several bugfixes for bugs that exist in the Bukkit API.
     * 
     * @param material_type (can differ from legacy data type)
     * @param legacy_data_type (legacy of material_type)
     * @param legacy_data_value
     * @return MaterialData
     */
    public static MaterialData createMaterialData(Material material_type, Material legacy_data_type, byte legacy_data_value) {
        MaterialData result = materialdata_builders.get(legacy_data_type).create(material_type, legacy_data_type, legacy_data_value);

        // Fix attachable face returning NULL sometimes
        if (result instanceof Attachable) {
            Attachable att = (Attachable) result;
            if (att.getAttachedFace() == null) {
                att.setFacingDirection(BlockFace.NORTH);
            }
        }

        return result;
    }

    private static void storeBuilders(MaterialDataBuilder builder, String... typeNames) {
        for (String typeName : typeNames) {
            Material type = MaterialsByName.getMaterial(typeName);
            if (type != null) materialdata_builders.put(type, builder);
        }
    }

    private static interface MaterialDataBuilder {
        MaterialData create(Material material_type, Material legacy_data_type, byte legacy_data_value);

        // Creates IBlockData for the default state of a Material
        default IBlockDataHandle getIBlockData(Material material) {
            if (CommonLegacyMaterials.isLegacy(material)) {
                material = fromLegacy(material);
            }
            return CraftMagicNumbersHandle.getBlockDataFromMaterial(material);
        }

        // Turn a legacy material into the new material type that represents it now
        // The input is guaranteed to be a legacy material only
        // Is only called by getIBlockData(material) and makes implementation easier
        default Material fromLegacy(Material legacyMaterial) { return legacyMaterial; }

        // Turn a (new) material into a legacy material close approximate
        default Material toLegacy(Material material) { return material; }
    }

    private static abstract class CustomMaterialDataBuilder<T extends MaterialData> implements MaterialDataBuilder {
        private Material[] types;
        private int[] data_values;

        @Override
        public abstract T create(Material material_type, Material legacy_data_type, byte legacy_data_value);

        /**
         * Creates all possible IBlockDatas state matching with this MaterialData
         * 
         * @param iblockdata of the default Material state
         * @param materialdata input state data
         * @return IBlockData state
         */
        public abstract List<IBlockDataHandle> createStates(IBlockDataHandle iblockdata, T materialdata);

        /**
         * Sets all possible Material types of this MaterialData
         * 
         * @param names
         * @return this
         */
        public CustomMaterialDataBuilder<T> setTypes(String... names) {
            this.types = CommonLegacyMaterials.getAllByName(names);
            return this;
        }

        /**
         * Adds additional Material types if the condition is True. See {@link #setTypes(names...)}.
         * 
         * @param condition Names are added when this is True
         * @param names Names to add
         * @return this
         */
        public CustomMaterialDataBuilder<T> addTypesIf(boolean condition, String... names) {
            if (condition) {
                this.types = LogicUtil.appendArray(this.types, CommonLegacyMaterials.getAllByName(names));
            }
            return this;
        }

        /**
         * Sets all possible Material types of this MaterialData
         * 
         * @param types
         * @return this
         */
        public CustomMaterialDataBuilder<T> setTypes(Material... types) {
            this.types = types;
            return this;
        }

        /**
         * Sets all possible data values of this MaterialData
         * 
         * @param values
         * @return this
         */
        public CustomMaterialDataBuilder<T> setDataValues(int... values) {
            this.data_values = values;
            return this;
        }

        /**
         * Builds all IBlockData values and registers them
         */
        public void build() {
            for (Material type : this.types) {
                materialdata_builders.put(type, this);
                T materialdata = this.create(type, type, (byte) this.data_values[0]);
                IBlockDataHandle baseIBlockData = this.getIBlockData(type);
                for (int data_value : this.data_values) {
                    materialdata.setData((byte) data_value);
                    for (IBlockDataHandle iBlockData : this.createStates(baseIBlockData, materialdata)) {
                        storeMaterialData(iBlockData, materialdata);
                    }
                }
            }
        }
    }

    private static class CraftBukkitToLegacy {
        private static final SafeMethod<Material> craftbukkitToLegacy = new SafeMethod<Material>(CommonUtil.getClass("org.bukkit.craftbukkit.legacy.CraftLegacy"), "toLegacy", Material.class);

        public static Material toLegacy(Material material) {
            return craftbukkitToLegacy.invoke(null, material);
        }
    }
}
