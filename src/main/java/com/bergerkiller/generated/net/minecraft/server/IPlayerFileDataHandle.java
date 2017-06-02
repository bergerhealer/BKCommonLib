package com.bergerkiller.generated.net.minecraft.server;

import org.bukkit.entity.HumanEntity;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class IPlayerFileDataHandle extends Template.Handle {
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

    public static final class IPlayerFileDataClass extends Template.Class<IPlayerFileDataHandle> {
        public final Template.Method.Converted<Void> save = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<CommonTagCompound> load = new Template.Method.Converted<CommonTagCompound>();
        public final Template.Method<String[]> getSeenPlayers = new Template.Method<String[]>();

    }

}

