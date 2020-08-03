package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.ShapedRecipes</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.ShapedRecipes")
public abstract class ShapedRecipesHandle extends IRecipeHandle {
    /** @See {@link ShapedRecipesClass} */
    public static final ShapedRecipesClass T = Template.Class.create(ShapedRecipesClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ShapedRecipesHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract List<CraftInputSlot> getInputItems();
    public abstract void setInputItems(List<CraftInputSlot> value);
    /**
     * Stores class members for <b>net.minecraft.server.ShapedRecipes</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ShapedRecipesClass extends Template.Class<ShapedRecipesHandle> {
        public final Template.Field.Converted<List<CraftInputSlot>> inputItems = new Template.Field.Converted<List<CraftInputSlot>>();

    }

}

