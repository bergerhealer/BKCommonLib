package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

/**
 * HumanHand is a mirror of Bukkit's {@link org.bukkit.inventory.MainHand}.
 * It is here for backwards compatibility with MC 1.8.9.
 * This defines a player-relative left or right hand, and provides utility methods for using it
 * to update/query equipment or to convert it to other API/internal types.
 */
public enum HumanHand {
    LEFT, RIGHT;

    private final Object bukkitMainHand;
    private final Object nmsHumanoidArm;

    HumanHand() {
        if (CommonCapabilities.PLAYER_OFF_HAND) {
            try {
                Class<?> mainHandType = CommonUtil.getClass("org.bukkit.inventory.MainHand");
                this.bukkitMainHand = mainHandType.getField(this.name()).get(null);
            } catch (Throwable t) {
                throw new UnsupportedOperationException("Failed to initialize bukkit main hand for HumanHand enum constant " + name(), t);
            }
            try {
                Class<?> humanoidArmType = CommonUtil.getClass("net.minecraft.world.entity.HumanoidArm");
                this.nmsHumanoidArm = humanoidArmType.getField(this.name()).get(null);
            } catch (Throwable t) {
                throw new UnsupportedOperationException("Failed to initialize nms humanoid arm for HumanHand enum constant " + name(), t);
            }
        } else {
            this.bukkitMainHand = null;
            this.nmsHumanoidArm = null;
        }
    }

    private static final FastMethod<Object> getBukkitMainHandMethod = LogicUtil.tryCreate(() -> {
        if (CommonCapabilities.PLAYER_OFF_HAND) {
            FastMethod<Object> method = new FastMethod<>(HumanEntity.class.getMethod("getMainHand"));
            method.forceInitialization();
            return method;
        } else {
            return null;
        }
    }, t -> {
        throw new UnsupportedOperationException("Failed to initialize the HumanEntity getMainHand method", t);
    });

    /**
     * Gets the opposite hand (LEFT -> RIGHT, RIGHT->LEFT)
     * 
     * @return opposite hand
     */
    public final HumanHand opposite() {
        return (this == LEFT) ? RIGHT : LEFT;
    }

    /**
     * Converts this HumanHand into a <i>net.minecraft.server.EnumHand</i>, keeping in mind
     * whether LEFT/RIGHT is the MAIN or OFF hand. When <i>null</i> is used for {@code humanEntity}
     * it is assumed the player's main hand is RIGHT.<br>
     * <br>
     * On versions before and including MC 1.8.9 this method always returns <i>null</i>.
     * 
     * @param humanEntity to query for main hand information
     * @return <i>net.minecraft.server.EnumHand</i>
     */
    public Object toNMSInteractionHand(HumanEntity humanEntity) {
        return toNMSInteractionHand(humanEntity, this);
    }

    /**
     * Converts Bukkit's MainHand property to the HumanHand LEFT/RIGHT constants.
     * This wrapper is required for backwards compatibility with MC 1.8.9.
     * On versions before and including MC 1.8.9 this method always returns RIGHT.
     *
     * @param mainHand The Bukkit {@link org.bukkit.inventory.MainHand} MainHand) of the human
     * @return HumanHand
     */
    @ConverterMethod(input = "org.bukkit.inventory.MainHand")
    public static HumanHand fromBukkitMainHand(Object mainHand) {
        if (!CommonCapabilities.PLAYER_OFF_HAND) {
            return RIGHT;
        } else if (mainHand != null) {
            if (RIGHT.bukkitMainHand == mainHand) {
                return RIGHT;
            } else if (LEFT.bukkitMainHand == mainHand) {
                return LEFT;
            }
        }
        return null;
    }

    /**
     * Converts a HumanHand to Bukkit's MainHand. This method always returns null
     * on versions before and including MC 1.8.9.
     *
     * @param hand to convert
     * @return {@link org.bukkit.inventory.MainHand} as a safe Object
     */
    @ConverterMethod(output = "org.bukkit.inventory.MainHand")
    public static Object toBukkitMainHand(HumanHand hand) {
        return hand == null ? null : hand.bukkitMainHand;
    }

    /**
     * Converts a <i>net.minecraft.world.entity.HumanoidArm</i> into a HumanHand.
     * This wrapper is required for backwards compatibility with MC 1.8.9,
     * and will always return RIGHT on versions before and including MC 1.8.9.
     *
     * @param nmsHumanoidArm to convert into a HumanHand
     * @return HumanHand
     */
    @ConverterMethod(input = "net.minecraft.world.entity.HumanoidArm")
    public static HumanHand fromNMSHumanoidArm(Object nmsHumanoidArm) {
        if (!CommonCapabilities.PLAYER_OFF_HAND) {
            return RIGHT;
        } else if (nmsHumanoidArm != null) {
            if (RIGHT.nmsHumanoidArm == nmsHumanoidArm) {
                return RIGHT;
            } else if (LEFT.nmsHumanoidArm == nmsHumanoidArm) {
                return LEFT;
            }
        }
        return null;
    }

    /**
     * Converts a HumanHand into a <i>net.minecraft.world.entity.HumanoidArm</i>.
     * Only works since MC 1.9, and will return <i>null</i> on versions before and including MC 1.8.9.
     *
     * @param hand to convert
     * @return Humanoid arm as a safe Object
     */
    @ConverterMethod(output = "net.minecraft.world.entity.HumanoidArm")
    public static Object toNMSHumanoidArm(HumanHand hand) {
        return hand == null ? null : hand.nmsHumanoidArm;
    }

    /**
     * Gets the HumanHandRole of a given hand for a human entity. This is determined by the player's main hand setting.
     * On versions before and including MC 1.8.9, this method will always return MAIN for RIGHT and OFF for LEFT,
     * regardless of the player's actual main hand setting.
     *
     * @param humanEntity to get the hand role for
     * @return HumanHandRole of the given hand for the given human entity
     */
    public HumanHandRole getRoleOf(HumanEntity humanEntity) {
        if (this == getMainHand(humanEntity)) {
            return HumanHandRole.MAIN;
        } else {
            return HumanHandRole.OFF;
        }
    }

    /**
     * Gets the main hand in use by a human. This will always return RIGHT on versions before
     * and including MC 1.8.9. When <i>null</i> is used for the {@code humanEntity} parameter,
     * this method will always return RIGHT as well.
     *
     * @param humanEntity to get the main hand for
     * @return main human hand
     */
    public static HumanHand getMainHand(HumanEntity humanEntity) {
        if (humanEntity != null && getBukkitMainHandMethod != null) {
            return fromBukkitMainHand(getBukkitMainHandMethod.invoke(humanEntity));
        } else {
            return RIGHT;
        }
    }

    /**
     * Gets the off hand in use by a human. This will always return LEFT on versions before
     * and including MC 1.8.9. When <i>null</i> is used for the {@code humanEntity} parameter,
     * this method will always return LEFT as well.
     * 
     * @param humanEntity to get the off hand for
     * @return off human hand
     */
    public static HumanHand getOffHand(HumanEntity humanEntity) {
        return getMainHand(humanEntity).opposite();
    }

    /**
     * Converts a <i>net.minecraft.server.InteractionHand</i> into a HumanHand, keeping in mind
     * whether LEFT/RIGHT is the MAIN or OFF hand. When <i>null</i> is used for {@code humanEntity}
     * it is assumed the player's main hand is RIGHT.<br>
     * <br>
     * On versions before and including MC 1.8.9 this method always returns <i>null</i>.
     * 
     * @param humanEntity to query for main hand information
     * @param nmsInteractionHand to convert into a HumanHand
     * @return HumanHand
     * @see HumanHandRole#fromNMSInteractionHand
     */
    public static HumanHand fromNMSInteractionHand(HumanEntity humanEntity, Object nmsInteractionHand) {
        HumanHandRole role = HumanHandRole.fromNMSInteractionHand(nmsInteractionHand);
        return role == null ? null : role.getHandOf(humanEntity);
    }

    /**
     * Converts a HumanHand into a <i>net.minecraft.server.EnumHand</i>, keeping in mind
     * whether LEFT/RIGHT is the MAIN or OFF hand. When <i>null</i> is used for {@code humanEntity}
     * it is assumed the player's main hand is RIGHT.<br>
     * <br>
     * On versions before and including MC 1.8.9 this method always returns <i>null</i>.
     * 
     * @param humanEntity to query for main hand information
     * @param humanHand to convert
     * @return <i>net.minecraft.server.EnumHand</i>
     * @see #getRoleOf(HumanEntity)
     * @see HumanHandRole#toNMSInteractionHand(HumanHandRole) 
     */
    public static Object toNMSInteractionHand(HumanEntity humanEntity, HumanHand humanHand) {
        return HumanHandRole.toNMSInteractionHand(humanHand.getRoleOf(humanEntity));
    }

    /**
     * Gets the item held in a given hand for a human entity.<br>
     * <br>
     * On versions before and including MC 1.8.9 this method always returns <i>null</i> for the LEFT hand,
     * and will return the held item for the RIGHT hand.
     * 
     * @param humanEntity to query the held item
     * @param humanHand where the item is to be found
     * @return held item
     */
    public static ItemStack getHeldItem(HumanEntity humanEntity, HumanHand humanHand) {
        if (humanEntity == null) {
            throw new IllegalArgumentException("humanEntity can not be null");
        }
        return humanHand.getRoleOf(humanEntity).getHeldItem(humanEntity);
    }

    /**
     * Sets the item held in a given hand for a human entity.<br>
     * <br>
     * On versions before and including MC 1.8.9 this method silently fails for the LEFT hand,
     * and will instead set the held item only for the RIGHT hand.
     * 
     * @param humanEntity to set the held item for
     * @param humanHand where the held item must be changed
     * @param item to set for the hand
     */
    public static void setHeldItem(HumanEntity humanEntity, HumanHand humanHand, ItemStack item) {
        if (humanEntity == null) {
            throw new IllegalArgumentException("humanEntity can not be null");
        }
        humanHand.getRoleOf(humanEntity).setHeldItem(humanEntity, item);
    }

    /**
     * Gets the item held in the main hand of the human
     * 
     * @param humanEntity to get the main hand held item
     * @return held item
     * @see HumanHandRole#getHeldItem(HumanEntity)
     */
    public static ItemStack getItemInMainHand(HumanEntity humanEntity) {
        return HumanHandRole.MAIN.getHeldItem(humanEntity);
    }

    /**
     * Gets the item held in the off hand of the human. This method will always return
     * <i>null</i> on versions before and including MC 1.8.9.
     * 
     * @param humanEntity to get the off hand held item
     * @return held item
     * @see HumanHandRole#getHeldItem(HumanEntity)
     */
    public static ItemStack getItemInOffHand(HumanEntity humanEntity) {
        return HumanHandRole.OFF.getHeldItem(humanEntity);
    }

    /**
     * Sets the item held in the main hand of the human.
     * 
     * @param humanEntity to set the main hand held item
     * @param item to set to
     * @see HumanHandRole#setHeldItem(HumanEntity, ItemStack)
     */
    public static void setItemInMainHand(HumanEntity humanEntity, ItemStack item) {
        HumanHandRole.MAIN.setHeldItem(humanEntity, item);
    }

    /**
     * Sets the item held in the off hand of the human. This method will silently fail
     * on versions before and including MC 1.8.9.
     * 
     * @param humanEntity to set the off hand held item
     * @param item to set to
     * @see HumanHandRole#setHeldItem(HumanEntity, ItemStack)
     */
    public static void setItemInOffHand(HumanEntity humanEntity, ItemStack item) {
        HumanHandRole.OFF.setHeldItem(humanEntity, item);
    }
}
