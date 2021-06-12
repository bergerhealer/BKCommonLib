package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityLivingHandle;
import org.bukkit.entity.Entity;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityHuman</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.EntityHuman")
public abstract class EntityHumanHandle extends EntityLivingHandle {
    /** @See {@link EntityHumanClass} */
    public static final EntityHumanClass T = Template.Class.create(EntityHumanClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityHumanHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void attack(Entity entity);
    public abstract Object getInventoryRaw();
    public abstract void setInventoryRaw(Object value);
    public abstract Object getEnderChestRaw();
    public abstract void setEnderChestRaw(Object value);
    public abstract ContainerHandle getActiveContainer();
    public abstract void setActiveContainer(ContainerHandle value);
    public abstract Object getFoodDataRaw();
    public abstract void setFoodDataRaw(Object value);
    public abstract int getSleepTicks();
    public abstract void setSleepTicks(int value);
    public abstract PlayerAbilities getAbilities();
    public abstract void setAbilities(PlayerAbilities value);
    public abstract int getExpLevel();
    public abstract void setExpLevel(int value);
    public abstract int getExpTotal();
    public abstract void setExpTotal(int value);
    public abstract float getExp();
    public abstract void setExp(float value);
    public abstract GameProfileHandle getGameProfile();
    public abstract void setGameProfile(GameProfileHandle value);
    /**
     * Stores class members for <b>net.minecraft.server.EntityHuman</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityHumanClass extends Template.Class<EntityHumanHandle> {
        public final Template.Field.Converted<Object> inventoryRaw = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<Object> enderChestRaw = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<ContainerHandle> activeContainer = new Template.Field.Converted<ContainerHandle>();
        public final Template.Field.Converted<Object> foodDataRaw = new Template.Field.Converted<Object>();
        public final Template.Field.Integer sleepTicks = new Template.Field.Integer();
        public final Template.Field.Converted<PlayerAbilities> abilities = new Template.Field.Converted<PlayerAbilities>();
        public final Template.Field.Integer expLevel = new Template.Field.Integer();
        public final Template.Field.Integer expTotal = new Template.Field.Integer();
        public final Template.Field.Float exp = new Template.Field.Float();
        public final Template.Field.Converted<GameProfileHandle> gameProfile = new Template.Field.Converted<GameProfileHandle>();

        public final Template.Method.Converted<Void> attack = new Template.Method.Converted<Void>();

    }

}

