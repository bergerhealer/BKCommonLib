package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.MethodAccessor;
import com.bergerkiller.reflection.SafeConstructor;

import java.io.File;
import java.io.RandomAccessFile;

public class NMSRegionFile {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("RegionFile")
    		.addImport("java.io.File")
    		.addImport("java.io.RandomAccessFile");
    
    public static final FieldAccessor<File> file = T.selectField("private final File b");
    public static final FieldAccessor<RandomAccessFile> stream = T.selectField("private RandomAccessFile c");
    
    public static final MethodAccessor<Void> close = T.getMethod("c");
    public static final MethodAccessor<Boolean> exists = T.getMethod("c", int.class, int.class);
    private static final SafeConstructor<?> constructor1 = T.getConstructor(File.class);

    public static Object create(File file) {
    	
        return constructor1.newInstance(file);
    }
}
