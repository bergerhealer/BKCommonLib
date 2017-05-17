package com.bergerkiller.generated.net.minecraft.server;

import java.util.Collection;
import org.bukkit.inventory.ItemStack;
import com.bergerkiller.generated.net.minecraft.server.AttributeInstanceHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.server.AttributeMapServerHandle;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.inventory.EquipmentSlot;
import com.bergerkiller.generated.net.minecraft.server.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;

public class EntityLivingHandle extends EntityHandle {
    public static final EntityLivingClass T = new EntityLivingClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityLivingHandle.class, "net.minecraft.server.EntityLiving");


    /* ============================================================================== */

    public static EntityLivingHandle createHandle(Object handleInstance) {
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

    public AttributeInstanceHandle getAttributeInstance(Object iattribute) {
        return T.getAttributeInstance.invoke(instance, iattribute);
    }

    public float getHealth() {
        return T.getHealth.invoke(instance);
    }

    public float getMaxHealth() {
        return T.getMaxHealth.invoke(instance);
    }

    public float getLastDamage() {
        return T.lastDamage.getFloat(instance);
    }

    public void setLastDamage(float value) {
        T.lastDamage.setFloat(instance, value);
    }

    public float getForwardMovement() {
        return T.forwardMovement.getFloat(instance);
    }

    public void setForwardMovement(float value) {
        T.forwardMovement.setFloat(instance, value);
    }

    public boolean isUpdateEffects() {
        return T.updateEffects.getBoolean(instance);
    }

    public void setUpdateEffects(boolean value) {
        T.updateEffects.setBoolean(instance, value);
    }

    public static final class EntityLivingClass extends Template.Class<EntityLivingHandle> {
        public final Template.Field.Float lastDamage = new Template.Field.Float();
        public final Template.Field.Float forwardMovement = new Template.Field.Float();
        public final Template.Field.Boolean updateEffects = new Template.Field.Boolean();

        public final Template.Method.Converted<Collection<MobEffectHandle>> getEffects = new Template.Method.Converted<Collection<MobEffectHandle>>();
        public final Template.Method.Converted<ItemStack> getEquipment = new Template.Method.Converted<ItemStack>();
        public final Template.Method.Converted<AttributeMapServerHandle> getAttributeMap = new Template.Method.Converted<AttributeMapServerHandle>();
        public final Template.Method.Converted<AttributeInstanceHandle> getAttributeInstance = new Template.Method.Converted<AttributeInstanceHandle>();
        public final Template.Method<Float> getHealth = new Template.Method<Float>();
        public final Template.Method<Float> getMaxHealth = new Template.Method<Float>();

    }
}
