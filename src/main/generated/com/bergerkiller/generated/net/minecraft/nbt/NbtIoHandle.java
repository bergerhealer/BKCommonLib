package com.bergerkiller.generated.net.minecraft.nbt;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Instance wrapper handle for type <b>net.minecraft.nbt.NbtIo</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.nbt.NbtIo")
public abstract class NbtIoHandle extends Template.Handle {
    /** @see NbtIoClass */
    public static final NbtIoClass T = Template.Class.create(NbtIoClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static NbtIoHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static CompoundTagHandle parseTagCompoundFromSNBT(String snbtContent) {
        return T.parseTagCompoundFromSNBT.invoke(snbtContent);
    }

    public static TagHandle parseTagFromSNBT(String snbtContent) {
        return T.parseTagFromSNBT.invoke(snbtContent);
    }

    public static String handleSNBTParseError(String snbtContent, Throwable exception) {
        return T.handleSNBTParseError.invoker.invoke(null,snbtContent, exception);
    }

    public static void uncompressed_writeTag(TagHandle nbtbase, DataOutput dataoutput) {
        T.uncompressed_writeTag.invoke(nbtbase, dataoutput);
    }

    public static TagHandle uncompressed_readTag(DataInput datainput) {
        return T.uncompressed_readTag.invoke(datainput);
    }

    public static void uncompressed_writeTagCompound(CompoundTagHandle nbttagcompound, DataOutput dataoutput) {
        T.uncompressed_writeTagCompound.invoke(nbttagcompound, dataoutput);
    }

    public static CompoundTagHandle uncompressed_readTagCompound(DataInput datainput) {
        return T.uncompressed_readTagCompound.invoke(datainput);
    }

    public static CompoundTagHandle compressed_readTagCompound(InputStream inputstream) {
        return T.compressed_readTagCompound.invoke(inputstream);
    }

    public static void compressed_writeTagCompound(CompoundTagHandle nbttagcompound, OutputStream outputstream) {
        T.compressed_writeTagCompound.invoke(nbttagcompound, outputstream);
    }

    /**
     * Stores class members for <b>net.minecraft.nbt.NbtIo</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NbtIoClass extends Template.Class<NbtIoHandle> {
        public final Template.StaticMethod.Converted<CompoundTagHandle> parseTagCompoundFromSNBT = new Template.StaticMethod.Converted<CompoundTagHandle>();
        public final Template.StaticMethod.Converted<TagHandle> parseTagFromSNBT = new Template.StaticMethod.Converted<TagHandle>();
        public final Template.StaticMethod<String> handleSNBTParseError = new Template.StaticMethod<String>();
        public final Template.StaticMethod.Converted<Void> uncompressed_writeTag = new Template.StaticMethod.Converted<Void>();
        public final Template.StaticMethod.Converted<TagHandle> uncompressed_readTag = new Template.StaticMethod.Converted<TagHandle>();
        public final Template.StaticMethod.Converted<Void> uncompressed_writeTagCompound = new Template.StaticMethod.Converted<Void>();
        public final Template.StaticMethod.Converted<CompoundTagHandle> uncompressed_readTagCompound = new Template.StaticMethod.Converted<CompoundTagHandle>();
        public final Template.StaticMethod.Converted<CompoundTagHandle> compressed_readTagCompound = new Template.StaticMethod.Converted<CompoundTagHandle>();
        public final Template.StaticMethod.Converted<Void> compressed_writeTagCompound = new Template.StaticMethod.Converted<Void>();

    }

}

