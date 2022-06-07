package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * BlockData serializer/deserializer for Minecraft 1.13 to 1.18.2.
 * Makes use of the then-introduced ArgumentBlock class (and brigadier).
 */
class BlockDataSerializer_1_13_to_1_18_2 extends BlockDataSerializer {
    private FastMethod<String> serializeMethod = new FastMethod<String>();
    private FastMethod<BlockData> deserializeMethod = new FastMethod<BlockData>();

    @Override
    public void enable() {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClassName("net.minecraft.commands.arguments.blocks.ArgumentBlock");
        resolver.addImport("net.minecraft.world.level.block.state.IBlockData");
        resolver.setAllVariables(Common.TEMPLATE_RESOLVER);
        serializeMethod.init(new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                "public static String serialize(IBlockData iblockdata) {\n" +
                "#if version >= 1.18\n" +
                "    return ArgumentBlock.serialize(iblockdata);\n" +
                "#elseif version >= 1.14\n" +
                "    return ArgumentBlock.a(iblockdata);\n" +
                "#else\n" +
                "    return ArgumentBlock.a(iblockdata, null);\n" +
                "#endif\n" +
                "}", resolver)));
        deserializeMethod.init(new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                "public static IBlockData deserialize(String text) {\n" +
                "    com.mojang.brigadier.StringReader reader = new com.mojang.brigadier.StringReader(text);\n" +
                "    ArgumentBlock block;\n" +
                "    try {\n" +
                "#if version >= 1.18\n" +
                "        block = (new ArgumentBlock(reader, false)).parse(true);\n" +
                "#else\n" +
                "        block = (new ArgumentBlock(reader, false)).a(true);\n" +
                "#endif\n" +
                "    } catch (com.mojang.brigadier.exceptions.CommandSyntaxException ex) {\n" +
                "        return null;\n" +
                "    }\n" +
                "#if version >= 1.18\n" +
                "    return block.getState();\n" +
                "#elseif version >= 1.13.2\n" +
                "    return block.getBlockData();\n" +
                "#else\n" +
                "    return block.b();\n" +
                "#endif\n" +
                "}", resolver)));
    }

    @Override
    public void disable() throws Throwable {
    }

    @Override
    public void forceInitialization() {
        this.serializeMethod.forceInitialization();
        this.deserializeMethod.forceInitialization();
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
