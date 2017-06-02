package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class FoodMetaDataHandle extends Template.Handle {
    public static final FoodMetaDataClass T = new FoodMetaDataClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(FoodMetaDataHandle.class, "net.minecraft.server.FoodMetaData");

    /* ============================================================================== */

    public static FoodMetaDataHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        FoodMetaDataHandle handle = new FoodMetaDataHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public void loadFromNBT(CommonTagCompound compound) {
        T.loadFromNBT.invoke(instance, compound);
    }

    public void saveToNBT(CommonTagCompound compound) {
        T.saveToNBT.invoke(instance, compound);
    }

    public static final class FoodMetaDataClass extends Template.Class<FoodMetaDataHandle> {
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> saveToNBT = new Template.Method.Converted<Void>();

    }

}

