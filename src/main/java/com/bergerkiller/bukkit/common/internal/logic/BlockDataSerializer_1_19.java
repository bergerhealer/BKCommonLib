package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * BlockData serializer/deserializer for Minecraft 1.19 and later.
 * Makes use of the then-introduced ArgumentBlock class (and brigadier).
 */
class BlockDataSerializer_1_19 extends BlockDataSerializer {
    private FastMethod<String> serializeMethod = new FastMethod<String>();
    private FastMethod<BlockData> deserializeMethod = new FastMethod<BlockData>();

    @Override
    public void enable() {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClassName("net.minecraft.commands.arguments.blocks.BlockStateParser");
        resolver.addImport("net.minecraft.commands.arguments.blocks.BlockStateParser.BlockResult");
        resolver.addImport("net.minecraft.world.level.block.state.BlockState");
        resolver.addImport("net.minecraft.core.registries.BuiltInRegistries");
        resolver.addImport("net.minecraft.core.Registry");
        resolver.setAllVariables(Common.TEMPLATE_RESOLVER);
        serializeMethod.init(new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                "public static String serialize(BlockState iblockdata) {\n" +
                "    return BlockStateParser.serialize(iblockdata);\n" +
                "}", resolver)));
        deserializeMethod.init(new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                "public static BlockState deserialize(String text) {\n" +
                "    BlockStateParser$BlockResult block;\n" +
                "    try {\n" +
                "#if version >= 1.21.2\n" +
                "        block = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK, text, true);\n" +
                "#elseif version >= 1.19.3\n" +
                "        block = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), text, true);\n" +
                "#else\n" +
                "        block = BlockStateParser.parseForBlock((Registry) BuiltInRegistries.BLOCK, text, true);\n" +
                "#endif\n" +
                "    } catch (com.mojang.brigadier.exceptions.CommandSyntaxException ex) {\n" +
                "        return null;\n" +
                "    }\n" +
                "    return block.blockState();\n" +
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
