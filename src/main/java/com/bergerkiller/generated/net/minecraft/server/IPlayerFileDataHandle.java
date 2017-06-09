package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import org.bukkit.entity.HumanEntity;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.IPlayerFileData</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class IPlayerFileDataHandle extends Template.Handle {
    /** @See {@link IPlayerFileDataClass} */
    public static final IPlayerFileDataClass T = new IPlayerFileDataClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(IPlayerFileDataHandle.class, "net.minecraft.server.IPlayerFileData");

    /* ============================================================================== */

    public static IPlayerFileDataHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        IPlayerFileDataHandle handle = new IPlayerFileDataHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public void save(HumanEntity human) {
        T.save.invoke(instance, human);
    }

    public CommonTagCompound load(HumanEntity human) {
        return T.load.invoke(instance, human);
    }

    public String[] getSeenPlayers() {
        return T.getSeenPlayers.invoke(instance);
    }

    /**
     * Stores class members for <b>net.minecraft.server.IPlayerFileData</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IPlayerFileDataClass extends Template.Class<IPlayerFileDataHandle> {
        public final Template.Method.Converted<Void> save = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<CommonTagCompound> load = new Template.Method.Converted<CommonTagCompound>();
        public final Template.Method<String[]> getSeenPlayers = new Template.Method<String[]>();

    }

}

