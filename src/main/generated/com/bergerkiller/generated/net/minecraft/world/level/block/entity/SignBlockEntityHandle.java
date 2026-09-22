package com.bergerkiller.generated.net.minecraft.world.level.block.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.entity.SignBlockEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.entity.SignBlockEntity")
public abstract class SignBlockEntityHandle extends BlockEntityHandle {
    /** @see SignBlockEntityClass */
    public static final SignBlockEntityClass T = Template.Class.create(SignBlockEntityClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static SignBlockEntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract List<Object> getRawFrontLines();
    public abstract String[] getMessageFrontLines();
    public abstract List<Object> getRawBackLines();
    public abstract String[] getMessageBackLines();
    public abstract void setFormattedFrontLine(int index, ChatText text);
    public abstract void setFormattedBackLine(int index, ChatText text);
    public static final java.util.List<Object> ALL_EMPTY_RAW_LINES;
    public static final String[] ALL_EMPTY_STRING_LINES;
    static {
        Object raw_empty = ChatText.empty().getRawHandle();
        ALL_EMPTY_RAW_LINES = java.util.Arrays.asList(raw_empty, raw_empty, raw_empty, raw_empty);
        ALL_EMPTY_STRING_LINES = new String[] { "", "", "", "" };
    }

    @Override
    public org.bukkit.block.Sign toBukkit() {
        return (org.bukkit.block.Sign) super.toBukkit();
    }

    public static SignBlockEntityHandle fromBukkit(org.bukkit.block.Sign sign) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion.INSTANCE.blockStateToTileEntity(sign));
    }
    /**
     * Stores class members for <b>net.minecraft.world.level.block.entity.SignBlockEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SignBlockEntityClass extends Template.Class<SignBlockEntityHandle> {
        public final Template.Method<List<Object>> getRawFrontLines = new Template.Method<List<Object>>();
        public final Template.Method<String[]> getMessageFrontLines = new Template.Method<String[]>();
        public final Template.Method<List<Object>> getRawBackLines = new Template.Method<List<Object>>();
        public final Template.Method<String[]> getMessageBackLines = new Template.Method<String[]>();
        public final Template.Method.Converted<Void> setFormattedFrontLine = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> setFormattedBackLine = new Template.Method.Converted<Void>();

    }

}

