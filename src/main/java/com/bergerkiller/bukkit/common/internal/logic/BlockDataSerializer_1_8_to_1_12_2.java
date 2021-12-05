package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * BlockData serializer/deserializer for Minecraft 1.8 and later, up until 1.12.2.
 * Base implementation simply deserializes a block name (NameSpace) and either the
 * data value, or the map of key:value states.
 */
class BlockDataSerializer_1_8_to_1_12_2 extends BlockDataSerializer {
    private FastMethod<Object> findBlockByNameMethod = new FastMethod<Object>();
    private FastMethod<Object> createLegacyBlockDataMethod = new FastMethod<Object>();
    private FastMethod<Object> setBlockDataKeyValueMethod = new FastMethod<Object>();

    @Override
    public void enable() {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClassName("net.minecraft.world.level.block.Block");
        resolver.setAllVariables(Common.TEMPLATE_RESOLVER);
        findBlockByNameMethod.init(new MethodDeclaration(resolver,
                "public static Object findBlockByName(net.minecraft.resources.MinecraftKey minecraftKey) {\n" +
                "    return Block.REGISTRY.get((Object)minecraftKey);\n" +
                "}"));
        createLegacyBlockDataMethod.init(new MethodDeclaration(resolver,
                "public static IBlockData createLegacyBlockData(Block block, int legacyData) {\n" +
                "    return block.fromLegacyData(legacyData);\n" +
                "}"));
        setBlockDataKeyValueMethod.init(new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                "public static IBlockData setBlockDataKeyValue(IBlockData iblockdata, String keyText, String valueText) {\n" +
                "    BlockStateList blockstatelist;\n" +
                "#if version >= 1.11\n" +
                "    blockstatelist = iblockdata.getBlock().s();\n" +
                "#elseif version >= 1.9\n" +
                "    blockstatelist = iblockdata.getBlock().t();\n" +
                "#elseif version > 1.8\n" +
                "    blockstatelist = iblockdata.getBlock().P();\n" +
                "#else\n" +
                "    blockstatelist = iblockdata.getBlock().O();\n" +
                "#endif\n" +
                "    IBlockState iblockstate;\n" +
                "    Comparable value;\n" +
                "#if version >= 1.10.2\n" +
                "    if ((iblockstate = blockstatelist.a(keyText)) == null) return null;\n" +
                "    value = (Comparable) iblockstate.b(valueText).orNull();\n" +
                "#else\n" +
                "    {\n" +
                "        java.util.Iterator state_iter = blockstatelist.d().iterator();\n" +
                "        while (true) {\n" +
                "            if (!state_iter.hasNext()) {\n" +
                "                return null; //not found\n" +
                "            }\n" +
                "            iblockstate = (IBlockState) state_iter.next();\n" +
                "            if (iblockstate.a().equals(keyText)) {\n" +
                "                break;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    if (iblockstate instanceof BlockStateBoolean) {\n" +
                "        if (valueText.equals(\"true\")) {\n" +
                "            value = Boolean.TRUE;\n" +
                "        } else if (valueText.equals(\"false\")) {\n" +
                "            value = Boolean.FALSE;\n" +
                "        } else {\n" +
                "            value = null;\n" +
                "        }\n" +
                "    } else if (iblockstate instanceof BlockStateInteger) {\n" +
                "        try {\n" +
                "            Integer intValue = Integer.valueOf(Integer.parseInt(valueText));\n" +
                "            if (iblockstate.c().contains(intValue)) {\n" +
                "                value = intValue;\n" +
                "            } else {\n" +
                "                value = null;\n" +
                "            }\n" +
                "        } catch (NumberFormatException ex) {\n" +
                "            value = null;\n" +
                "        }\n" +
                "    } else if (iblockstate instanceof BlockStateEnum) {\n" +
                "        java.util.Iterator iter = iblockstate.c().iterator();\n" +
                "        while (true) {\n" +
                "            if (!iter.hasNext()) {\n" +
                "                value = null; //not found\n" +
                "                break;\n" +
                "            }\n" +
                "            INamable option = (INamable) iter.next();\n" +
                "            if (option.getName().equals(valueText)) {\n" +
                "                value = (Comparable) option;\n" +
                "                break;\n" +
                "            }\n" +
                "        }\n" +
                "    } else {\n" +
                "        value = null; //no idea\n" +
                "    }\n" +
                "#endif\n" +
                "    if (value == null) return null;\n" +
                "    return iblockdata.set(iblockstate, value);\n" +
                "}", resolver)));
    }

    @Override
    public void disable() throws Throwable {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void forceInitialization() {
        this.findBlockByNameMethod.forceInitialization();
        this.createLegacyBlockDataMethod.forceInitialization();
        this.setBlockDataKeyValueMethod.forceInitialization();
    }

    @Override
    public String serialize(BlockData blockData) {
        return blockData.getData().toString();
    }

    @Override
    public BlockData deserialize(String text) {
        // Read block name and (optional) block states (or legacy data)
        String blockName;
        String stateText = null;
        int legacyDataValue = -1;
        {
            int index;
            if ((index = text.indexOf('[')) != -1) {
                blockName = text.substring(0, index);
                int endIndex = text.indexOf(']', index+1);
                if (endIndex != -1) {
                    stateText = text.substring(index+1, endIndex).trim();
                } else {
                    stateText = text.substring(index+1).trim();
                }
            } else {
                blockName = text;

                // If last element after : is a data value, parse legacy data
                if ((index = text.lastIndexOf(':')) != -1) {
                    String dataText = text.substring(index+1).trim();
                    if (ParseUtil.isNumeric(dataText)) {
                        try {
                            legacyDataValue = Integer.parseInt(stateText);
                            if (legacyDataValue < 0 || legacyDataValue > 15) {
                                return null; // invalid data value
                            }
                            blockName = text.substring(0, index);
                        } catch (NumberFormatException ex) {
                            // Ignore
                        }
                    }
                }
            }
        }

        // Parse Block name to MinecraftKey, and find the Block with this name
        MinecraftKeyHandle key = MinecraftKeyHandle.createNew(blockName);
        if (key == null) {
            return null; // invalid key name format
        }
        Object nmsBlock = findBlockByNameMethod.invoke(null, key.getRaw());
        if (nmsBlock == null) {
            return null; // block not found
        }

        // If state text can be parsed as a number, parse from legacy data
        if (legacyDataValue != -1) {
            return BlockData.fromBlockData(createLegacyBlockDataMethod.invoke(null, nmsBlock, legacyDataValue));
        }

        // Create default BlockData from Block
        Object nmsIBlockData = BlockHandle.T.getBlockData.raw.invoke(nmsBlock);

        // Deserialize state text, if specified
        // Split the state text by , and each element by =
        if (stateText != null && !stateText.isEmpty()) {
            int index = 0;
            do {
                int nextElement = stateText.indexOf(',', index);
                int nextValue = stateText.indexOf('=', index);
                if (nextValue == -1 || (nextElement != -1 && nextValue > nextElement)) {
                    return null; // Invalid syntax (missing =)
                }

                // Retrieve key and value text
                String keyText = stateText.substring(index, nextValue);
                String valueText;
                if (nextElement == -1) {
                    valueText = stateText.substring(nextValue+1);
                } else {
                    valueText = stateText.substring(nextValue+1, nextElement);
                }

                // Attempt setting the key-value. If key or value cannot be parsed, returns null.
                if ((nmsIBlockData = setBlockDataKeyValueMethod.invoke(null, nmsIBlockData, keyText, valueText)) == null) {
                    return null; // Could not be parsed
                }

                // Next element
                index = nextElement+1;
            } while (index != 0);
        }

        // To wrapper and done!
        return BlockData.fromBlockData(nmsIBlockData);
    }
}
