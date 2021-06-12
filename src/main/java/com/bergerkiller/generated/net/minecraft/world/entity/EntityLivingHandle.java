package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.generated.net.minecraft.server.AttributeMapBaseHandle;
import com.bergerkiller.generated.net.minecraft.server.AttributeModifiableHandle;
import com.bergerkiller.generated.net.minecraft.server.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.server.MobEffectListHandle;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import java.util.Collection;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.EntityLiving</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.EntityLiving")
public abstract class EntityLivingHandle extends EntityHandle {
    /** @See {@link EntityLivingClass} */
    public static final EntityLivingClass T = Template.Class.create(EntityLivingClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityLivingHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void resetAttributes();
    public abstract AttributeMapBaseHandle getAttributeMap();
    public abstract AttributeModifiableHandle getAttribute(Object iattribute);
    public abstract Collection<MobEffectHandle> getEffects();
    public abstract ItemStack getEquipment(EquipmentSlot paramEnumItemSlot);
    public abstract float getHealth();
    public abstract float getMaxHealth();

    public static final Key<Byte> DATA_LIVING_FLAGS = Key.Type.BYTE.createKey(T.DATA_LIVING_FLAGS, -1);
    public static final Key<Float> DATA_HEALTH = Key.Type.FLOAT.createKey(T.DATA_HEALTH, 6);
    public static final Key<Integer> DATA_PARTICLES_TIMER = Key.Type.INTEGER.createKey(T.DATA_PARTICLES_TIMER, 7);
    public static final Key<Boolean> DATA_PARTICLES_HIDDEN = Key.Type.BOOLEAN.createKey(T.DATA_PARTICLES_HIDDEN, 8);
    public static final Key<Integer> DATA_UNKNOWN1 = Key.Type.INTEGER.createKey(T.DATA_ARROWCOUNT, 9);
    public static final Key<IntVector3> DATA_BEDPOSITION = Key.Type.BLOCK_POSITION.createKey(T.DATA_BEDPOSITION, -1);


    public static EntityLivingHandle fromBukkit(org.bukkit.entity.LivingEntity livingEntity) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toEntityHandle(livingEntity));
    }
    public abstract Map<MobEffectListHandle, MobEffectHandle> getMobEffects();
    public abstract void setMobEffects(Map<MobEffectListHandle, MobEffectHandle> value);
    public abstract float getLastDamage();
    public abstract void setLastDamage(float value);
    public abstract float getSideMovement();
    public abstract void setSideMovement(float value);
    public abstract float getForwardMovement();
    public abstract void setForwardMovement(float value);
    public abstract boolean isUpdateEffects();
    public abstract void setUpdateEffects(boolean value);
    /**
     * Stores class members for <b>net.minecraft.world.entity.EntityLiving</b>.
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
        @Template.Optional
        public final Template.StaticField.Converted<Key<IntVector3>> DATA_BEDPOSITION = new Template.StaticField.Converted<Key<IntVector3>>();

        public final Template.Field.Converted<Map<MobEffectListHandle, MobEffectHandle>> mobEffects = new Template.Field.Converted<Map<MobEffectListHandle, MobEffectHandle>>();
        public final Template.Field.Float lastDamage = new Template.Field.Float();
        public final Template.Field.Float sideMovement = new Template.Field.Float();
        public final Template.Field.Float forwardMovement = new Template.Field.Float();
        public final Template.Field.Boolean updateEffects = new Template.Field.Boolean();

        public final Template.Method<Void> resetAttributes = new Template.Method<Void>();
        public final Template.Method.Converted<AttributeMapBaseHandle> getAttributeMap = new Template.Method.Converted<AttributeMapBaseHandle>();
        public final Template.Method.Converted<AttributeModifiableHandle> getAttribute = new Template.Method.Converted<AttributeModifiableHandle>();
        public final Template.Method.Converted<Collection<MobEffectHandle>> getEffects = new Template.Method.Converted<Collection<MobEffectHandle>>();
        public final Template.Method.Converted<ItemStack> getEquipment = new Template.Method.Converted<ItemStack>();
        public final Template.Method<Float> getHealth = new Template.Method<Float>();
        public final Template.Method<Float> getMaxHealth = new Template.Method<Float>();

    }

}

