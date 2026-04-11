package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.org.bukkit.inventory.PlayerInventoryHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

/**
 * Defines what type of hand a hand is. This can be the player's 'main' hand, or 'off' hand.
 * Often in packets and elsewhere not the left/right hand is set, but main or off. Then the
 * server determines left/right based on what hand was set to be the main hand.<br>
 * <br>
 * This class has methods to convert between the LEFT/RIGHT and MAIN/OFF hand types.
 * This provides utility methods for using it to update/query equipment or to convert
 * it to other API/internal types.<br>
 * <br>
 * Typically this role is used when handling player interactions.
 */
public enum HumanHandRole {
    /** Main dominant hand. When used it uses the tool. */
    MAIN("MAIN_HAND"),
    /** Off non-dominant hand. Holds an alternative item not actively used with actions. */
    OFF("OFF_HAND");

    private final Object nmsInteractionHand;

    HumanHandRole(String enumName) {
        if (CommonBootstrap.evaluateMCVersion(">=", "1.9")) {
            try {
                Class<?> interactionHandType = CommonUtil.getClass("net.minecraft.world.InteractionHand");
                this.nmsInteractionHand = interactionHandType.getField(enumName).get(null);
            } catch (Throwable t) {
                throw new UnsupportedOperationException("Failed to initialize HumanHandRole enum constants for " + name(), t);
            }
        } else {
            this.nmsInteractionHand = null;
        }
    }

    /**
     * Gets the opposite hand (LEFT -> RIGHT, RIGHT->LEFT)
     * 
     * @return opposite hand
     */
    public final HumanHandRole opposite() {
        return (this == MAIN) ? OFF : MAIN;
    }

    /**
     * Converts a <i>net.minecraft.world.InteractionHand</i> into a HumanHandRole. This method
     * returns null if the input is not a valid InteractionHand, or if the InteractionHand class is not available (MC 1.8.9 and below).
     *
     * @param nmsInteractionHand to convert into a HumanHandRole
     * @return HumanHandRole, or null if the input is invalid or the InteractionHand class is not available
     */
    @ConverterMethod(input = "net.minecraft.world.InteractionHand")
    public static HumanHandRole fromNMSInteractionHand(Object nmsInteractionHand) {
        if (nmsInteractionHand != null) {
            if (nmsInteractionHand == MAIN.nmsInteractionHand) {
                return MAIN;
            } else if (nmsInteractionHand == OFF.nmsInteractionHand) {
                return OFF;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Converts a HumanHandRole into a <i>net.minecraft.world.InteractionHand</i>.
     * <br>
     * On versions before and including MC 1.8.9 this method always returns <i>null</i>.
     *
     * @param handRole to convert
     * @return <i>net.minecraft.world.InteractionHand</i>
     */
    @ConverterMethod(output = "net.minecraft.world.InteractionHand")
    public static Object toNMSInteractionHand(HumanHandRole handRole) {
        if (handRole != null) {
            return handRole.nmsInteractionHand;
        } else {
            return null;
        }
    }

    /**
     * Gets the hand that has this role for a given human entity. This is determined by the player's main hand setting.
     * On versions before and including MC 1.8.9, this method will always return RIGHT for MAIN and LEFT for OFF,
     * regardless of the player's actual main hand setting. If the input human entity is null,
     * assumes the main hand is the right one.
     *
     * @param humanEntity to get the hand for, null to assume main=right
     * @return hand that has this role for the given human entity
     */
    public HumanHand getHandOf(HumanEntity humanEntity) {
        HumanHand hand = HumanHand.getMainHand(humanEntity);
        return (this == MAIN) ? hand : hand.opposite();
    }

    /**
     * Gets the HumanHandRole of a given hand for a human entity. This is determined by the player's main hand setting.
     * On versions before and including MC 1.8.9, this method will always return MAIN for RIGHT and OFF for LEFT,
     * regardless of the player's actual main hand setting. If the input hand is null, this method will also return null.
     * If the input human entity is null, assumes the main hand is the right one.
     *
     * @param humanEntity to get the hand role for, null to assume main=right
     * @param hand to get the role of
     * @return HumanHandRole of the given hand for the given human entity, or null if the hand is null
     */
    public static HumanHandRole fromHandOf(HumanEntity humanEntity, HumanHand hand) {
        if (hand == null) {
            return null;
        }

        HumanHand mainHand = HumanHand.getMainHand(humanEntity);
        return hand == mainHand ? MAIN : OFF;
    }

    /**
     * Gets the item held in this hand for a human entity if this role is MAIN, or the off hand
     * if this role is OFF. On versions before and including MC 1.8.9, this method will
     * always return null for the OFF hand role.<br>
     *
     * @param humanEntity to query the held item
     * @return held item
     */
    public ItemStack getHeldItem(HumanEntity humanEntity) {
        if (humanEntity == null) {
            throw new IllegalArgumentException("humanEntity cannot be null");
        }

        if (this == MAIN) {
            return PlayerInventoryHandle.T.getItemInMainHand.invoke(humanEntity.getInventory());
        } else {
            return PlayerInventoryHandle.T.getItemInOffHand.invoke(humanEntity.getInventory());
        }
    }

    /**
     * Sets the item held in this hand for a human entity if this role is MAIN, or the off hand
     * if this role is OFF. On versions before and including MC 1.8.9, this method will silently fail
     * and not set anything for the OFF hand role.<br>
     *
     * @param humanEntity to set the held item for
     * @param item to set for the hand
     */
    public void setHeldItem(HumanEntity humanEntity, ItemStack item) {
        if (humanEntity == null) {
            throw new IllegalArgumentException("humanEntity can not be null");
        }

        if (this == MAIN) {
            PlayerInventoryHandle.T.setItemInMainHand.invoke(humanEntity.getInventory(), item);
        } else {
            PlayerInventoryHandle.T.setItemInOffHand.invoke(humanEntity.getInventory(), item);
        }
    }
}
