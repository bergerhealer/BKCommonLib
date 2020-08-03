package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.DamageSource</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.DamageSource")
public abstract class DamageSourceHandle extends Template.Handle {
    /** @See {@link DamageSourceClass} */
    public static final DamageSourceClass T = Template.Class.create(DamageSourceClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static DamageSourceHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static DamageSourceHandle mobAttack(LivingEntity livingEntity) {
        return T.mobAttack.invoke(livingEntity);
    }

    public static DamageSourceHandle playerAttack(HumanEntity humanEntity) {
        return T.playerAttack.invoke(humanEntity);
    }

    public static DamageSourceHandle arrowHit(Arrow arrow, Entity damager) {
        return T.arrowHit.invoke(arrow, damager);
    }

    public static DamageSourceHandle thrownHit(Entity projectile, Entity damager) {
        return T.thrownHit.invoke(projectile, damager);
    }

    public static DamageSourceHandle magicHit(Entity magicEntity, Entity damager) {
        return T.magicHit.invoke(magicEntity, damager);
    }

    public static DamageSourceHandle thorns(Entity entity) {
        return T.thorns.invoke(entity);
    }

    public abstract boolean isExplosion();
    public abstract boolean isFireDamage();
    public abstract Entity getEntity();

    private static final java.util.HashMap<String, DamageSourceHandle> _values = new java.util.HashMap<String, DamageSourceHandle>();
    public static DamageSourceHandle byName(String name) {
        if (_values.size() == 0) {
            for (Object rawValue : com.bergerkiller.bukkit.common.utils.CommonUtil.getClassConstants(T.getType())) {
                DamageSourceHandle handle = createHandle(rawValue);
                _values.put(handle.getTranslationIndex(), handle);
            }
        }
        DamageSourceHandle result = _values.get(name);
        if (result == null) {
            result = _values.get("generic");
        }
        return result;
    }
    public abstract String getTranslationIndex();
    public abstract void setTranslationIndex(String value);
    /**
     * Stores class members for <b>net.minecraft.server.DamageSource</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DamageSourceClass extends Template.Class<DamageSourceHandle> {
        public final Template.Field<String> translationIndex = new Template.Field<String>();

        public final Template.StaticMethod.Converted<DamageSourceHandle> mobAttack = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> playerAttack = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> arrowHit = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> thrownHit = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> magicHit = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> thorns = new Template.StaticMethod.Converted<DamageSourceHandle>();

        public final Template.Method<Boolean> isExplosion = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isFireDamage = new Template.Method<Boolean>();
        public final Template.Method.Converted<Entity> getEntity = new Template.Method.Converted<Entity>();

    }

}

