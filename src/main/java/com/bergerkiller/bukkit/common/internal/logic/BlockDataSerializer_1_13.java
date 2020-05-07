package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * BlockData serializer/deserializer for Minecraft 1.13 and later.
 * Makes use of the then-introduced ArgumentBlock class (and brigadier).
 */
public class BlockDataSerializer_1_13 extends BlockDataSerializer {
    private FastMethod<String> serializeMethod = new FastMethod<String>();
    private FastMethod<BlockData> deserializeMethod = new FastMethod<BlockData>();

    public BlockDataSerializer_1_13() {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(CommonUtil.getNMSClass("ArgumentBlock"));
        resolver.setVariable("version", Common.MC_VERSION);
        serializeMethod.init(new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                "public static String serialize(IBlockData iblockdata) {\n" +
                "#if version >= 1.14\n" +
                "    return ArgumentBlock.a(iblockdata);\n" +
                "#else\n" +
                "    return ArgumentBlock.a(iblockdata, null);\n" +
                "#endif\n" +
                "}", resolver)));
        deserializeMethod.init(new MethodDeclaration(resolver,
                "public static IBlockData deserialize(String text) {\n" +
                "    com.mojang.brigadier.StringReader reader = new com.mojang.brigadier.StringReader(text);\n" +
                "    ArgumentBlock block;\n" +
                "    try {\n" +
                "        block = (new ArgumentBlock(reader, false)).a(true);\n" +
                "    } catch (com.mojang.brigadier.exceptions.CommandSyntaxException ex) {\n" +
                "        return null;\n" +
                "    }\n" +
                "    return block.getBlockData();\n" +
                "}"));
    }

    @Override
    public String serialize(BlockData blockData) {
        return serializeMethod.invoke(null, blockData.getData());
    }

    @Override
    public BlockData deserialize(String text) {
        return BlockData.fromBlockData(deserializeMethod.invoke(null, text));
    }
}
