package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EnumDifficultyHandle extends Template.Handle {
    public static final EnumDifficultyClass T = new EnumDifficultyClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumDifficultyHandle.class, "net.minecraft.server.EnumDifficulty");

    /* ============================================================================== */

    public static EnumDifficultyHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EnumDifficultyHandle handle = new EnumDifficultyHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static EnumDifficultyHandle getById(int id) {
        return T.getById.invokeVA(id);
    }

    public int getId() {
        return T.getId.invoke(instance);
    }

    public static final class EnumDifficultyClass extends Template.Class<EnumDifficultyHandle> {
        public final Template.StaticMethod.Converted<EnumDifficultyHandle> getById = new Template.StaticMethod.Converted<EnumDifficultyHandle>();

        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

