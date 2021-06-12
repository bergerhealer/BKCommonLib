package com.bergerkiller.generated.net.minecraft.nbt;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Instance wrapper handle for type <b>net.minecraft.nbt.NBTCompressedStreamTools</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.nbt.NBTCompressedStreamTools")
public abstract class NBTCompressedStreamToolsHandle extends Template.Handle {
    /** @See {@link NBTCompressedStreamToolsClass} */
    public static final NBTCompressedStreamToolsClass T = Template.Class.create(NBTCompressedStreamToolsClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static NBTCompressedStreamToolsHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static void uncompressed_writeTag(NBTBaseHandle nbtbase, DataOutput dataoutput) {
        T.uncompressed_writeTag.invoke(nbtbase, dataoutput);
    }

    public static NBTBaseHandle uncompressed_readTag(DataInput datainput) {
        return T.uncompressed_readTag.invoke(datainput);
    }

    public static void uncompressed_writeTagCompound(NBTTagCompoundHandle nbttagcompound, DataOutput dataoutput) {
        T.uncompressed_writeTagCompound.invoke(nbttagcompound, dataoutput);
    }

    public static NBTTagCompoundHandle uncompressed_readTagCompound(DataInput datainput) {
        return T.uncompressed_readTagCompound.invoke(datainput);
    }

    public static NBTTagCompoundHandle compressed_readTagCompound(InputStream inputstream) {
        return T.compressed_readTagCompound.invoke(inputstream);
    }

    public static void compressed_writeTagCompound(NBTTagCompoundHandle nbttagcompound, OutputStream outputstream) {
        T.compressed_writeTagCompound.invoke(nbttagcompound, outputstream);
    }

    /**
     * Stores class members for <b>net.minecraft.nbt.NBTCompressedStreamTools</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NBTCompressedStreamToolsClass extends Template.Class<NBTCompressedStreamToolsHandle> {
        public final Template.StaticMethod.Converted<Void> uncompressed_writeTag = new Template.StaticMethod.Converted<Void>();
        public final Template.StaticMethod.Converted<NBTBaseHandle> uncompressed_readTag = new Template.StaticMethod.Converted<NBTBaseHandle>();
        public final Template.StaticMethod.Converted<Void> uncompressed_writeTagCompound = new Template.StaticMethod.Converted<Void>();
        public final Template.StaticMethod.Converted<NBTTagCompoundHandle> uncompressed_readTagCompound = new Template.StaticMethod.Converted<NBTTagCompoundHandle>();
        public final Template.StaticMethod.Converted<NBTTagCompoundHandle> compressed_readTagCompound = new Template.StaticMethod.Converted<NBTTagCompoundHandle>();
        public final Template.StaticMethod.Converted<Void> compressed_writeTagCompound = new Template.StaticMethod.Converted<Void>();

    }

}

