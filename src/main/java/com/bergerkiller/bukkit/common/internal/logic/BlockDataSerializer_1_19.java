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
        resolver.setDeclaredClassName("net.minecraft.commands.arguments.blocks.ArgumentBlock");
        resolver.addImport("net.minecraft.commands.arguments.blocks.ArgumentBlock.a");
        resolver.addImport("net.minecraft.world.level.block.state.IBlockData");
        resolver.addImport("net.minecraft.core.registries.BuiltInRegistries");
        resolver.setAllVariables(Common.TEMPLATE_RESOLVER);
        serializeMethod.init(new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                "public static String serialize(IBlockData iblockdata) {\n" +
                "    return ArgumentBlock.serialize(iblockdata);\n" +
                "}", resolver)));
        deserializeMethod.init(new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                "public static IBlockData deserialize(String text) {\n" +
                "    ArgumentBlock$a block;\n" +
                "    try {\n" +
                "        block = ArgumentBlock.parseForBlock((IRegistry) BuiltInRegistries.BLOCK, text, true);\n" +
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
