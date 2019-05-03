package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.InventoryView;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityPlayer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class EntityPlayerHandle extends EntityHumanHandle {
    /** @See {@link EntityPlayerClass} */
    public static final EntityPlayerClass T = new EntityPlayerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityPlayerHandle.class, "net.minecraft.server.EntityPlayer", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static EntityPlayerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract InventoryView openAnvilWindow();

    public static EntityPlayerHandle fromBukkit(org.bukkit.entity.Player player) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toEntityHandle(player));
    }
    public abstract Object getPlayerConnection();
    public abstract void setPlayerConnection(Object value);
    public abstract int getPing();
    public abstract void setPing(int value);
    /**
     * Stores class members for <b>net.minecraft.server.EntityPlayer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityPlayerClass extends Template.Class<EntityPlayerHandle> {
        public final Template.Field.Converted<Object> playerConnection = new Template.Field.Converted<Object>();
        @Template.Optional
        public final Template.Field<List<Integer>> removeQueue = new Template.Field<List<Integer>>();
        public final Template.Field.Integer ping = new Template.Field.Integer();

        public final Template.Method<InventoryView> openAnvilWindow = new Template.Method<InventoryView>();

    }

}

