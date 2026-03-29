package com.bergerkiller.generated.net.minecraft.world.entity.player;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.inventory.AbstractContainerMenuHandle;
import org.bukkit.entity.Entity;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.player.Player</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.player.Player")
public abstract class PlayerHandle extends LivingEntityHandle {
    /** @see PlayerClass */
    public static final PlayerClass T = Template.Class.create(PlayerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PlayerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void attack(Entity entity);
    public static final Key<Byte> DATA_PLAYER_MODE_CUSTOMISATION = Key.Type.BYTE.createKey(T.DATA_PLAYER_MODE_CUSTOMISATION, -1);
    public static final int DATA_CUSTOMISATION_FLAG_CAPE = (1 << 0);
    public static final int DATA_CUSTOMISATION_FLAG_JACKET = (1 << 1);
    public static final int DATA_CUSTOMISATION_FLAG_LEFT_SLEEVE = (1 << 2);
    public static final int DATA_CUSTOMISATION_FLAG_RIGHT_SLEEVE = (1 << 3);
    public static final int DATA_CUSTOMISATION_FLAG_LEFT_PANTS_LEG = (1 << 4);
    public static final int DATA_CUSTOMISATION_FLAG_RIGHT_PANTS_LEG = (1 << 5);
    public static final int DATA_CUSTOMISATION_FLAG_HAT = (1 << 6);
    public static final byte DATA_CUSTOMISATION_FLAG_ALL = 0x7f;
    public abstract Object getInventoryRaw();
    public abstract void setInventoryRaw(Object value);
    public abstract Object getEnderChestRaw();
    public abstract void setEnderChestRaw(Object value);
    public abstract AbstractContainerMenuHandle getActiveContainer();
    public abstract void setActiveContainer(AbstractContainerMenuHandle value);
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
     * Stores class members for <b>net.minecraft.world.entity.player.Player</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PlayerClass extends Template.Class<PlayerHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Byte>> DATA_PLAYER_MODE_CUSTOMISATION = new Template.StaticField.Converted<Key<Byte>>();

        public final Template.Field.Converted<Object> inventoryRaw = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<Object> enderChestRaw = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<AbstractContainerMenuHandle> activeContainer = new Template.Field.Converted<AbstractContainerMenuHandle>();
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

