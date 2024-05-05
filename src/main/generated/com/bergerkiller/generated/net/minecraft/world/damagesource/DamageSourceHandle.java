package com.bergerkiller.generated.net.minecraft.world.damagesource;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.damagesource.DamageSource</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.damagesource.DamageSource")
public abstract class DamageSourceHandle extends Template.Handle {
    /** @see DamageSourceClass */
    public static final DamageSourceClass T = Template.Class.create(DamageSourceClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static DamageSourceHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static DamageSourceHandle generic(World world) {
        return T.generic.invoke(world);
    }

    public static DamageSourceHandle genericForEntity(Entity entity) {
        return T.genericForEntity.invoke(entity);
    }

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

    public static DamageSourceHandle byName(World world, String name) {
        return T.byName.invoke(world, name);
    }

    public static DamageSourceHandle byNameForEntity(Entity entity, String name) {
        return T.byNameForEntity.invoke(entity, name);
    }

    public static void initNameLookup(Map<String, Object> lookup) {
        T.initNameLookup.invoker.invoke(null,lookup);
    }

    public abstract String getTranslationIndex();
    public abstract Entity getEntity();
    public abstract boolean isExplosion();
    public abstract boolean isFireDamage();
    public static final java.util.Map<String, Object> INTERNAL_NAME_TO_KEY = new java.util.HashMap<>();
    static {
        try {
            initNameLookup(INTERNAL_NAME_TO_KEY);
        } catch (Throwable t) {
            com.bergerkiller.bukkit.common.Logging.LOGGER_REGISTRY.log(java.util.logging.Level.SEVERE,
                    "Failed to initialize damage sources by name", t);
        }

        translateLegacyName("inFire", "in_fire");
        translateLegacyName("lightningBolt", "lightning_bolt");
        translateLegacyName("onFire", "on_fire");
        translateLegacyName("hotFloor", "hot_floor");
        translateLegacyName("inWall", "in_wall");
        translateLegacyName("flyIntoWall", "fly_into_wall");
        translateLegacyName("outOfWorld", "out_of_world");
        translateLegacyName("dragonBreath", "dragon_breath");
        translateLegacyName("dryout", "dry_out");
        translateLegacyName("sweetBerryBush", "sweet_berry_bush");
        translateLegacyName("fallingBlock", "falling_block");
        translateLegacyName("anvil", "falling_anvil");
        translateLegacyName("fallingStalactite", "falling_stalactite");
        translateLegacyName("mob", "mob_attack");
        translateLegacyName("mob", "mob_attack_no_aggro");
        translateLegacyName("player", "player_attack");
        translateLegacyName("mob", "mob_projectile");
        translateLegacyName("onFire", "unattributed_fireball");
        translateLegacyName("witherSkull", "wither_skull");
        translateLegacyName("indirectMagic", "indirect_magic");
        translateLegacyName("explosion.player", "player_explosion");
        translateLegacyName("badRespawnPoint", "bad_respawn_point");
    }

    private static void translateLegacyName(String legacyName, String registryName) {
        Object byLegacy = INTERNAL_NAME_TO_KEY.get(legacyName);
        Object byRegistyName = INTERNAL_NAME_TO_KEY.get(registryName);
        if (byLegacy != null && byRegistyName == null) {
            INTERNAL_NAME_TO_KEY.put(registryName, byLegacy);
        } else if (byLegacy == null && byRegistyName != null) {
            INTERNAL_NAME_TO_KEY.put(legacyName, byRegistyName);
        }
    }
    /**
     * Stores class members for <b>net.minecraft.world.damagesource.DamageSource</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DamageSourceClass extends Template.Class<DamageSourceHandle> {
        public final Template.StaticMethod.Converted<DamageSourceHandle> generic = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> genericForEntity = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> mobAttack = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> playerAttack = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> arrowHit = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> thrownHit = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> magicHit = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> thorns = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> byName = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod.Converted<DamageSourceHandle> byNameForEntity = new Template.StaticMethod.Converted<DamageSourceHandle>();
        public final Template.StaticMethod<Void> initNameLookup = new Template.StaticMethod<Void>();

        public final Template.Method<String> getTranslationIndex = new Template.Method<String>();
        public final Template.Method.Converted<Entity> getEntity = new Template.Method.Converted<Entity>();
        public final Template.Method<Boolean> isExplosion = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isFireDamage = new Template.Method<Boolean>();

    }

}

