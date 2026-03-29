package com.bergerkiller.generated.net.minecraft.world.food;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.food.FoodData</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.food.FoodData")
public abstract class FoodDataHandle extends Template.Handle {
    /** @see FoodDataClass */
    public static final FoodDataClass T = Template.Class.create(FoodDataClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static FoodDataHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void loadFromNBT(CommonTagCompound compound);
    public abstract void saveToNBT(CommonTagCompound compound);
    /**
     * Stores class members for <b>net.minecraft.world.food.FoodData</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class FoodDataClass extends Template.Class<FoodDataHandle> {
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> saveToNBT = new Template.Method.Converted<Void>();

    }

}

