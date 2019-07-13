package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.RegionFileHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

import java.io.File;
import java.io.RandomAccessFile;

public class NMSRegionFile {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("RegionFile");

    public static final FieldAccessor<RandomAccessFile> stream = RegionFileHandle.T.stream.toFieldAccessor();

    public static final MethodAccessor<Void> close = RegionFileHandle.T.close.toMethodAccessor();
    public static final MethodAccessor<Boolean> exists = RegionFileHandle.T.chunkExists.toMethodAccessor();

    public static Object create(File file) {
        return RegionFileHandle.T.constr_file.raw.newInstance(file);
    }
}
