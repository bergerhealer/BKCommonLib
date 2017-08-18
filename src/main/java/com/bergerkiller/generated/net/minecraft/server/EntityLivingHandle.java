package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import java.util.Collection;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityLiving</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EntityLivingHandle extends EntityHandle {
    /** @See {@link EntityLivingClass} */
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

    public void resetAttributes() {
        T.resetAttributes.invoke(instance);
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


    public static final Key<Byte> DATA_LIVING_FLAGS = Key.Type.BYTE.createKey(T.DATA_LIVING_FLAGS, -1);
    public static final Key<Float> DATA_HEALTH = Key.Type.FLOAT.createKey(T.DATA_HEALTH, 6);
    public static final Key<Integer> DATA_PARTICLES_TIMER = Key.Type.INTEGER.createKey(T.DATA_PARTICLES_TIMER, 7);
    public static final Key<Boolean> DATA_PARTICLES_HIDDEN = Key.Type.BOOLEAN.createKey(T.DATA_PARTICLES_HIDDEN, 8);
    public static final Key<Integer> DATA_UNKNOWN1 = Key.Type.INTEGER.createKey(T.DATA_ARROWCOUNT, 9);


    public static EntityLivingHandle fromBukkit(org.bukkit.entity.LivingEntity livingEntity) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toEntityHandle(livingEntity));
    }
    public AttributeMapServerHandle getAttributeMapField() {
        return T.attributeMapField.get(instance);
    }

    public void setAttributeMapField(AttributeMapServerHandle value) {
        T.attributeMapField.set(instance, value);
    }

    public Map<MobEffectListHandle, MobEffectHandle> getMobEffects() {
        return T.mobEffects.get(instance);
    }

    public void setMobEffects(Map<MobEffectListHandle, MobEffectHandle> value) {
        T.mobEffects.set(instance, value);
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

    /**
     * Stores class members for <b>net.minecraft.server.EntityLiving</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityLivingClass extends Template.Class<EntityLivingHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Byte>> DATA_LIVING_FLAGS = new Template.StaticField.Converted<Key<Byte>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Float>> DATA_HEALTH = new Template.StaticField.Converted<Key<Float>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_PARTICLES_TIMER = new Template.StaticField.Converted<Key<Integer>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Boolean>> DATA_PARTICLES_HIDDEN = new Template.StaticField.Converted<Key<Boolean>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_ARROWCOUNT = new Template.StaticField.Converted<Key<Integer>>();

        public final Template.Field.Converted<AttributeMapServerHandle> attributeMapField = new Template.Field.Converted<AttributeMapServerHandle>();
        public final Template.Field.Converted<Map<MobEffectListHandle, MobEffectHandle>> mobEffects = new Template.Field.Converted<Map<MobEffectListHandle, MobEffectHandle>>();
        public final Template.Field.Float lastDamage = new Template.Field.Float();
        public final Template.Field.Float forwardMovement = new Template.Field.Float();
        public final Template.Field.Boolean updateEffects = new Template.Field.Boolean();

        public final Template.Method.Converted<Collection<MobEffectHandle>> getEffects = new Template.Method.Converted<Collection<MobEffectHandle>>();
        public final Template.Method.Converted<ItemStack> getEquipment = new Template.Method.Converted<ItemStack>();
        public final Template.Method<Void> resetAttributes = new Template.Method<Void>();
        public final Template.Method.Converted<AttributeMapServerHandle> getAttributeMap = new Template.Method.Converted<AttributeMapServerHandle>();
        public final Template.Method.Converted<AttributeInstanceHandle> getAttributeInstance = new Template.Method.Converted<AttributeInstanceHandle>();
        public final Template.Method<Float> getHealth = new Template.Method<Float>();
        public final Template.Method<Float> getMaxHealth = new Template.Method<Float>();

    }

}

