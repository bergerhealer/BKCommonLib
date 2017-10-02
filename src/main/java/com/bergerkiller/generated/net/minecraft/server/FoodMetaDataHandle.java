package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.FoodMetaData</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class FoodMetaDataHandle extends Template.Handle {
    /** @See {@link FoodMetaDataClass} */
    public static final FoodMetaDataClass T = new FoodMetaDataClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(FoodMetaDataHandle.class, "net.minecraft.server.FoodMetaData");

    /* ============================================================================== */

    public static FoodMetaDataHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public void loadFromNBT(CommonTagCompound compound) {
        T.loadFromNBT.invoke(getRaw(), compound);
    }

    public void saveToNBT(CommonTagCompound compound) {
        T.saveToNBT.invoke(getRaw(), compound);
    }

    /**
     * Stores class members for <b>net.minecraft.server.FoodMetaData</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class FoodMetaDataClass extends Template.Class<FoodMetaDataHandle> {
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> saveToNBT = new Template.Method.Converted<Void>();

    }

}

