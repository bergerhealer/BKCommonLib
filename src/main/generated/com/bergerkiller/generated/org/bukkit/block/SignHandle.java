package com.bergerkiller.generated.org.bukkit.block;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>org.bukkit.block.Sign</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.block.Sign")
public abstract class SignHandle extends BlockStateHandle {
    /** @see SignClass */
    public static final SignClass T = Template.Class.create(SignClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static SignHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract String getFrontLine(int index);
    public abstract void setFrontLine(int index, String text);
    public abstract String[] getFrontLines();
    public abstract String getBackLine(int index);
    public abstract void setBackLine(int index, String text);
    public abstract String[] getBackLines();
    /**
     * Stores class members for <b>org.bukkit.block.Sign</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SignClass extends Template.Class<SignHandle> {
        public final Template.Method<String> getFrontLine = new Template.Method<String>();
        public final Template.Method<Void> setFrontLine = new Template.Method<Void>();
        public final Template.Method<String[]> getFrontLines = new Template.Method<String[]>();
        public final Template.Method<String> getBackLine = new Template.Method<String>();
        public final Template.Method<Void> setBackLine = new Template.Method<Void>();
        public final Template.Method<String[]> getBackLines = new Template.Method<String[]>();

    }

}

