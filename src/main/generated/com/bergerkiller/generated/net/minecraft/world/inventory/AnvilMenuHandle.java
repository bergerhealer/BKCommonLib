package com.bergerkiller.generated.net.minecraft.world.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.InventoryView;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.inventory.AnvilMenu</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.inventory.AnvilMenu")
public abstract class AnvilMenuHandle extends AbstractContainerMenuHandle {
    /** @see AnvilMenuClass */
    public static final AnvilMenuClass T = Template.Class.create(AnvilMenuClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static AnvilMenuHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static AnvilMenuHandle fromBukkit(InventoryView bukkitView) {
        return T.fromBukkit.invoker.invoke(null,bukkitView);
    }

    public abstract String getRenameText();
    public abstract void setRenameText(String value);
    /**
     * Stores class members for <b>net.minecraft.world.inventory.AnvilMenu</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class AnvilMenuClass extends Template.Class<AnvilMenuHandle> {
        public final Template.Field<String> renameText = new Template.Field<String>();

        public final Template.StaticMethod<AnvilMenuHandle> fromBukkit = new Template.StaticMethod<AnvilMenuHandle>();

    }

}

