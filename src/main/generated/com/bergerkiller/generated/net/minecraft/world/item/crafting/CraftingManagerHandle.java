package com.bergerkiller.generated.net.minecraft.world.item.crafting;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.item.crafting.CraftingManager</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.item.crafting.CraftingManager")
public abstract class CraftingManagerHandle extends Template.Handle {
    /** @see CraftingManagerClass */
    public static final CraftingManagerClass T = Template.Class.create(CraftingManagerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CraftingManagerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Iterable<IRecipeHandle> getRecipes() {
        return T.getRecipes.invoke();
    }

    /**
     * Stores class members for <b>net.minecraft.world.item.crafting.CraftingManager</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftingManagerClass extends Template.Class<CraftingManagerHandle> {
        public final Template.StaticMethod.Converted<Iterable<IRecipeHandle>> getRecipes = new Template.StaticMethod.Converted<Iterable<IRecipeHandle>>();

    }

}

