package com.bergerkiller.generated.net.minecraft.server;

import java.util.Collection;
import org.bukkit.inventory.ItemStack;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.server.AttributeMapServerHandle;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.inventory.EquipmentSlot;
import com.bergerkiller.generated.net.minecraft.server.MobEffectHandle;

public class EntityLivingHandle extends Template.Handle {
    public static final EntityLivingClass T = new EntityLivingClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityLivingHandle.class, "net.minecraft.server.EntityLiving");


    /* ============================================================================== */

    public static final EntityLivingHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityLivingHandle handle = new EntityLivingHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public Collection<MobEffectHandle> getEffects() {
        return T.getEffects.invoke(instance);
    }

    public ItemStack getEquipment(EquipmentSlot paramEnumItemSlot) {
        return T.getEquipment.invoke(instance, paramEnumItemSlot);
    }

    public AttributeMapServerHandle getAttributeMap() {
        return T.getAttributeMap.invoke(instance);
    }

    public static final class EntityLivingClass extends Template.Class {
        public final Template.Method.Converted<Collection<MobEffectHandle>> getEffects = new Template.Method.Converted<Collection<MobEffectHandle>>();
        public final Template.Method.Converted<ItemStack> getEquipment = new Template.Method.Converted<ItemStack>();
        public final Template.Method.Converted<AttributeMapServerHandle> getAttributeMap = new Template.Method.Converted<AttributeMapServerHandle>();

    }
}
