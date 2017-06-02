package com.bergerkiller.generated.net.minecraft.server;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Arrow;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Entity;

public class DamageSourceHandle extends Template.Handle {
    public static final DamageSourceClass T = new DamageSourceClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(DamageSourceHandle.class, "net.minecraft.server.DamageSource");

    /* ============================================================================== */

    public static DamageSourceHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        DamageSourceHandle handle = new DamageSourceHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static DamageSourceHandle mobAttack(LivingEntity livingEntity) {
        return T.mobAttack.invokeVA(livingEntity);
    }

    public static DamageSourceHandle playerAttack(HumanEntity humanEntity) {
        return T.playerAttack.invokeVA(humanEntity);
    }

    public static DamageSourceHandle arrowHit(Arrow arrow, Entity damager) {
        return T.arrowHit.invokeVA(arrow, damager);
    }

    public static DamageSourceHandle fireballHit(Fireball fireball, Entity damager) {
        return T.fireballHit.invokeVA(fireball, damager);
    }

    public static DamageSourceHandle thrownHit(Entity projectile, Entity damager) {
        return T.thrownHit.invokeVA(projectile, damager);
    }

    public static DamageSourceHandle magicHit(Entity magicEntity, Entity damager) {
        return T.magicHit.invokeVA(magicEntity, damager);
    }

    public static DamageSourceHandle thorns(Entity entity) {
        return T.thorns.invokeVA(entity);
    }

    public static DamageSourceHandle explosion(ExplosionHandle explosion) {
        return T.explosion.invokeVA(explosion);
    }

    public boolean isExplosion() {
        return T.isExplosion.invoke(instance);
    }

    public boolean isFireDamage() {
        return T.isFireDamage.invoke(instance);
    }

    public Entity getEntity() {
        return T.getEntity.invoke(instance);
    }


    public static DamageSourceHandle entityExplosion(org.bukkit.entity.Entity explodableEntity) {
        ExplosionHandle explosionHandle = null;
        if (explodableEntity != null) {
            org.bukkit.Location loc = explodableEntity.getLocation();
            explosionHandle = ExplosionHandle.createNew(explodableEntity.getWorld(), explodableEntity, loc.getX(), loc.getY(), loc.getZ(), 0.0f, false, true);
        }
        return explosion(explosionHandle);
    }


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
    public String getTranslationIndex() {
        return T.translationIndex.get(instance);
    }

    public void setTranslationIndex(String value) {
        T.translationIndex.set(instance, value);
    }

    public static final class DamageSourceClass extends Template.Class<DamageSourceHandle> {
        public final Template.Field<String> translationIndex = new Template.Field<String>();

        public final Template.StaticMethod.Converted<DamageSourceHandle> mobAttack = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> playerAttack = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> arrowHit = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> fireballHit = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> thrownHit = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> magicHit = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> thorns = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> explosion = new Template.StaticMethod.Converted<DamageSourceHandle>();

        public final Template.Method<Boolean> isExplosion = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isFireDamage = new Template.Method<Boolean>();
        public final Template.Method.Converted<Entity> getEntity = new Template.Method.Converted<Entity>();

    }

}

