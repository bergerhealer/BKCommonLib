package com.bergerkiller.generated.org.bukkit.craftbukkit.util;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.Material;

public class CraftMagicNumbersHandle extends Template.Handle {
    public static final CraftMagicNumbersClass T = new CraftMagicNumbersClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftMagicNumbersHandle.class, "org.bukkit.craftbukkit.util.CraftMagicNumbers");


    /* ============================================================================== */

    public static CraftMagicNumbersHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftMagicNumbersHandle handle = new CraftMagicNumbersHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static Material getMaterialFromBlock(Object nmsBlock) {
        return T.getMaterialFromBlock.invokeVA(nmsBlock);
    }

    public static Material getMaterialFromItem(Object nmsItem) {
        return T.getMaterialFromItem.invokeVA(nmsItem);
    }

    public static Object getItemFromMaterial(Material material) {
        return T.getItemFromMaterial.invokeVA(material);
    }

    public static Object getBlockFromMaterial(Material material) {
        return T.getBlockFromMaterial.invokeVA(material);
    }

    public static final class CraftMagicNumbersClass extends Template.Class<CraftMagicNumbersHandle> {
        public final Template.StaticMethod.Converted<Material> getMaterialFromBlock = new Template.StaticMethod.Converted<Material>();
        public final Template.StaticMethod.Converted<Material> getMaterialFromItem = new Template.StaticMethod.Converted<Material>();
        public final Template.StaticMethod.Converted<Object> getItemFromMaterial = new Template.StaticMethod.Converted<Object>();
        public final Template.StaticMethod.Converted<Object> getBlockFromMaterial = new Template.StaticMethod.Converted<Object>();

    }
}
