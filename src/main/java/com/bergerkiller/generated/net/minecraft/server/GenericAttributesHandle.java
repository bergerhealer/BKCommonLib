package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.GenericAttributes</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class GenericAttributesHandle extends Template.Handle {
    /** @See {@link GenericAttributesClass} */
    public static final GenericAttributesClass T = new GenericAttributesClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(GenericAttributesHandle.class, "net.minecraft.server.GenericAttributes");

    public static final Object FOLLOW_RANGE = T.FOLLOW_RANGE.getSafe();
    public static final Object MOVEMENT_SPEED = T.MOVEMENT_SPEED.getSafe();
    /* ============================================================================== */

    public static GenericAttributesHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static void loadFromNBT(AttributeMapServerHandle attributemapbase, CommonTagList nbttaglist) {
        T.loadFromNBT.invoke(attributemapbase, nbttaglist);
    }

    public static CommonTagList saveToNBT(AttributeMapServerHandle attributemapbase) {
        return T.saveToNBT.invoke(attributemapbase);
    }

    /**
     * Stores class members for <b>net.minecraft.server.GenericAttributes</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class GenericAttributesClass extends Template.Class<GenericAttributesHandle> {
        public final Template.StaticField.Converted<Object> FOLLOW_RANGE = new Template.StaticField.Converted<Object>();
        public final Template.StaticField.Converted<Object> MOVEMENT_SPEED = new Template.StaticField.Converted<Object>();

        public final Template.StaticMethod.Converted<Void> loadFromNBT = new Template.StaticMethod.Converted<Void>();
        public final Template.StaticMethod.Converted<CommonTagList> saveToNBT = new Template.StaticMethod.Converted<CommonTagList>();

    }

}

