package com.bergerkiller.generated.org.bukkit.craftbukkit.util;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Material;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.util.CraftMagicNumbers</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.craftbukkit.util.CraftMagicNumbers")
public abstract class CraftMagicNumbersHandle extends Template.Handle {
    /** @see CraftMagicNumbersClass */
    public static final CraftMagicNumbersClass T = Template.Class.create(CraftMagicNumbersClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CraftMagicNumbersHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Material getMaterialFromBlock(Object nmsBlock) {
        return T.getMaterialFromBlock.invoke(nmsBlock);
    }

    public static Material getMaterialFromItem(Object nmsItem) {
        return T.getMaterialFromItem.invoke(nmsItem);
    }

    public static Object getItemFromMaterial(Material material) {
        return T.getItemFromMaterial.invoker.invoke(null,material);
    }

    public static Object getBlockFromMaterial(Material material) {
        return T.getBlockFromMaterial.invoker.invoke(null,material);
    }

    public static int getDataVersion() {
        return T.getDataVersion.invoker.invoke(null);
    }


    public static com.bergerkiller.generated.net.minecraft.world.level.block.state.IBlockDataHandle getBlockDataFromMaterial(org.bukkit.Material material) {
        return com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle.T.getBlockData.invoke(getBlockFromMaterial(material));
    }
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.util.CraftMagicNumbers</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftMagicNumbersClass extends Template.Class<CraftMagicNumbersHandle> {
        public final Template.StaticMethod.Converted<Material> getMaterialFromBlock = new Template.StaticMethod.Converted<Material>();
        public final Template.StaticMethod.Converted<Material> getMaterialFromItem = new Template.StaticMethod.Converted<Material>();
        public final Template.StaticMethod<Object> getItemFromMaterial = new Template.StaticMethod<Object>();
        public final Template.StaticMethod<Object> getBlockFromMaterial = new Template.StaticMethod<Object>();
        public final Template.StaticMethod<Integer> getDataVersion = new Template.StaticMethod<Integer>();

    }

}

