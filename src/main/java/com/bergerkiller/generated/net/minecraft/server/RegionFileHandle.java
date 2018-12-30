package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.io.File;
import java.io.RandomAccessFile;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.RegionFile</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class RegionFileHandle extends Template.Handle {
    /** @See {@link RegionFileClass} */
    public static final RegionFileClass T = new RegionFileClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(RegionFileHandle.class, "net.minecraft.server.RegionFile", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static RegionFileHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final RegionFileHandle createNew(File file) {
        return T.constr_file.newInstance(file);
    }

    /* ============================================================================== */

    public abstract void close();
    public abstract boolean chunkExists(int cx, int cz);
    public abstract File getFile();
    public abstract void setFile(File value);
    public abstract RandomAccessFile getStream();
    public abstract void setStream(RandomAccessFile value);
    /**
     * Stores class members for <b>net.minecraft.server.RegionFile</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RegionFileClass extends Template.Class<RegionFileHandle> {
        public final Template.Constructor.Converted<RegionFileHandle> constr_file = new Template.Constructor.Converted<RegionFileHandle>();

        public final Template.Field<File> file = new Template.Field<File>();
        public final Template.Field<RandomAccessFile> stream = new Template.Field<RandomAccessFile>();

        public final Template.Method<Void> close = new Template.Method<Void>();
        public final Template.Method<Boolean> chunkExists = new Template.Method<Boolean>();

    }

}

