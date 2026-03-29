package com.bergerkiller.generated.net.minecraft.world.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.world.ContainerHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.inventory.MerchantContainer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.inventory.MerchantContainer")
public abstract class MerchantContainerHandle extends ContainerHandle {
    /** @see MerchantContainerClass */
    public static final MerchantContainerClass T = Template.Class.create(MerchantContainerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static MerchantContainerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Object getMerchant();
    public abstract void setMerchant(Object value);
    /**
     * Stores class members for <b>net.minecraft.world.inventory.MerchantContainer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MerchantContainerClass extends Template.Class<MerchantContainerHandle> {
        public final Template.Field.Converted<Object> merchant = new Template.Field.Converted<Object>();

    }

}

