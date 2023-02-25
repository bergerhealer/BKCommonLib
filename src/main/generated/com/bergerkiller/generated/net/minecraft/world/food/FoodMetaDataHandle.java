package com.bergerkiller.generated.net.minecraft.world.food;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.food.FoodMetaData</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.food.FoodMetaData")
public abstract class FoodMetaDataHandle extends Template.Handle {
    /** @see FoodMetaDataClass */
    public static final FoodMetaDataClass T = Template.Class.create(FoodMetaDataClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static FoodMetaDataHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void loadFromNBT(CommonTagCompound compound);
    public abstract void saveToNBT(CommonTagCompound compound);
    /**
     * Stores class members for <b>net.minecraft.world.food.FoodMetaData</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class FoodMetaDataClass extends Template.Class<FoodMetaDataHandle> {
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> saveToNBT = new Template.Method.Converted<Void>();

    }

}

