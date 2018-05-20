package com.bergerkiller.bukkit.common.wrappers;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.generated.net.minecraft.server.EnumHandHandle;
import com.bergerkiller.generated.org.bukkit.entity.HumanEntityHandle;
import com.bergerkiller.generated.org.bukkit.inventory.MainHandHandle;
import com.bergerkiller.generated.org.bukkit.inventory.PlayerInventoryHandle;

/**
 * HumanHand is a mirror of Bukkit's {@link org.bukkit.inventory.MainHand}.
 * It is here for backwards compatibility with MC 1.8.8.
 */
public enum HumanHand {
    LEFT, RIGHT;

    /**
     * Gets the opposite hand (LEFT -> RIGHT, RIGHT->LEFT)
     * 
     * @return opposite hand
     */
    public final HumanHand opposite() {
        return (this == LEFT) ? RIGHT : LEFT;
    }

    /**
     * Converts this HumanHand to Bukkit's MainHand. This method always returns null
     * on versions before and including 1.8.8.
     * 
     * @param hand to convert
     * @return {@link org.bukkit.inventory.MainHand}
     */
    public Object toMainHand() {
        return toMainHand(this);
    }

    /**
     * Converts this HumanHand into a <i>net.minecraft.server.EnumHand</i>, keeping in mind
     * whether LEFT/RIGHT is the MAIN or OFF hand. When <i>null</i> is used for {@code humanEntity}
     * it is assumed the player's main hand is RIGHT.<br>
     * <br>
     * On versions before and including MC 1.8.8 this method always returns <i>null</i>.
     * 
     * @param humanEntity to query for main hand information
     * @param humanHand to convert
     * @return <i>net.minecraft.server.EnumHand</i>
     */
    public Object toNMSEnumHand(HumanEntity humanEntity) {
        return toNMSEnumHand(humanEntity, this);
    }

    /**
     * Converts Bukkit's MainHand property to the HumanHand LEFT/RIGHT constants.
     * This wrapper is required for backwards compatibility with MC 1.8.8.
     * On versions before and including MC 1.8.8 this method always returns RIGHT.
     * 
     * @param human the main hand is for
     * @param mainHand of the human ({@link org.bukkit.inventory.MainHand})
     * @return HumanHand
     */
    public static HumanHand fromMainHand(Object mainHand) {
        if (!MainHandHandle.T.isAvailable()) {
            return RIGHT;
        } else if (MainHandHandle.T.isAssignableFrom(mainHand)) {
            if (MainHandHandle.LEFT.getRaw() == mainHand) {
                return LEFT;
            } else {
                return RIGHT;
            }
        } else {
            return null;
        }
    }

    /**
     * Gets the main hand in use by a human. This will always return RIGHT on versions before
     * and including MC 1.8.8. When <i>null</i> is used for the {@code humanEntity} parameter,
     * this method will always return RIGHT as well.
     * 
     * @param humanEntity to get the main hand for
     * @return main human hand
     */
    public static HumanHand getMainHand(HumanEntity humanEntity) {
        if (humanEntity != null && HumanEntityHandle.T.getMainHand.isAvailable()) {
            return fromMainHand(HumanEntityHandle.T.getMainHand.invoke(humanEntity));
        } else {
            return RIGHT;
        }
    }

    /**
     * Gets the off hand in use by a human. This will always return LEFT on versions before
     * and including MC 1.8.8. When <i>null</i> is used for the {@code humanEntity} parameter,
     * this method will always return LEFT as well.
     * 
     * @param humanEntity to get the off hand for
     * @return off human hand
     */
    public static HumanHand getOffHand(HumanEntity humanEntity) {
        HumanHand mainHand = getMainHand(humanEntity);
        return (mainHand == LEFT) ? RIGHT : LEFT;
    }

    /**
     * Converts a HumanHand to Bukkit's MainHand. This method always returns null
     * on versions before and including MC 1.8.8.
     * 
     * @param hand to convert
     * @return {@link org.bukkit.inventory.MainHand}
     */
    public static Object toMainHand(HumanHand hand) {
        if (hand == null) {
            return null;
        }
        if (MainHandHandle.T.isAvailable()) {
            return (hand == LEFT) ? MainHandHandle.LEFT.getRaw() : MainHandHandle.RIGHT.getRaw();
        } else {
            return null;
        }
    }

    /**
     * Converts a <i>net.minecraft.server.EnumHand</i> into a HumanHand, keeping in mind
     * whether LEFT/RIGHT is the MAIN or OFF hand. When <i>null</i> is used for {@code humanEntity}
     * it is assumed the player's main hand is RIGHT.<br>
     * <br>
     * On versions before and including MC 1.8.8 this method always returns <i>null</i>.
     * 
     * @param humanEntity to query for main hand information
     * @param nmsEnumHand to convert into a HumanHand
     * @return HumanHand
     */
    public static HumanHand fromNMSEnumHand(HumanEntity humanEntity, Object nmsEnumHand) {
        if (EnumHandHandle.T.isAvailable()) {
            HumanHand hand = (nmsEnumHand == EnumHandHandle.OFF_HAND.getRaw()) ? LEFT : RIGHT;
            if (getMainHand(humanEntity) == LEFT) {
                hand = (hand == RIGHT) ? LEFT : RIGHT;
            }
            return hand;
        } else {
            return null;
        }
    }

    /**
     * Converts a HumanHand into a <i>net.minecraft.server.EnumHand</i>, keeping in mind
     * whether LEFT/RIGHT is the MAIN or OFF hand. When <i>null</i> is used for {@code humanEntity}
     * it is assumed the player's main hand is RIGHT.<br>
     * <br>
     * On versions before and including MC 1.8.8 this method always returns <i>null</i>.
     * 
     * @param humanEntity to query for main hand information
     * @param humanHand to convert
     * @return <i>net.minecraft.server.EnumHand</i>
     */
    public static Object toNMSEnumHand(HumanEntity humanEntity, HumanHand humanHand) {
        if (EnumHandHandle.T.isAvailable()) {
            if (getMainHand(humanEntity) == humanHand) {
                return EnumHandHandle.MAIN_HAND.getRaw();
            } else {
                return EnumHandHandle.OFF_HAND.getRaw();
            }
        } else {
            return null;
        }
    }

    /**
     * Gets the item held in a given hand for a human entity.<br>
     * <br>
     * On versions before and including MC 1.8.8 this method always returns <i>null</i> for the LEFT hand,
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
        if (humanHand == getMainHand(humanEntity)) {
            return getItemInMainHand(humanEntity);
        } else {
            return getItemInOffHand(humanEntity);
        }
    }

    /**
     * Sets the item held in a given hand for a human entity.<br>
     * <br>
     * On versions before and including MC 1.8.8 this method silently fails for the LEFT hand,
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
        if (humanHand == getMainHand(humanEntity)) {
            setItemInMainHand(humanEntity, item);
        } else {
            setItemInOffHand(humanEntity, item);
        }
    }

    /**
     * Gets the item held in the main hand of the human
     * 
     * @param humanEntity to get the main hand held item
     * @return held item
     */
    public static ItemStack getItemInMainHand(HumanEntity humanEntity) {
        if (humanEntity == null) {
            throw new IllegalArgumentException("humanEntity can not be null");
        }
        if (PlayerInventoryHandle.T.getItemInMainHand.isAvailable()) {
            return PlayerInventoryHandle.T.getItemInMainHand.invoke(humanEntity.getInventory());
        } else if (PlayerInventoryHandle.T.getItemInHand.isAvailable()) {
            return PlayerInventoryHandle.T.getItemInHand.invoke(humanEntity.getInventory());
        } else {
            return null;
        }
    }

    /**
     * Gets the item held in the off hand of the human. This method will always return
     * <i>null</i> on versions before and including MC 1.8.8.
     * 
     * @param humanEntity to get the off hand held item
     * @return held item
     */
    public static ItemStack getItemInOffHand(HumanEntity humanEntity) {
        if (humanEntity == null) {
            throw new IllegalArgumentException("humanEntity can not be null");
        }
        if (PlayerInventoryHandle.T.getItemInOffHand.isAvailable()) {
            return PlayerInventoryHandle.T.getItemInOffHand.invoke(humanEntity.getInventory());
        } else {
            return null; // No OFF hand item holding <= MC 1.8.8
        }
    }

    /**
     * Sets the item held in the main hand of the human.
     * 
     * @param humanEntity to set the main hand held item
     * @param item to set to
     */
    public static void setItemInMainHand(HumanEntity humanEntity, ItemStack item) {
        if (humanEntity == null) {
            throw new IllegalArgumentException("humanEntity can not be null");
        }
        if (PlayerInventoryHandle.T.setItemInMainHand.isAvailable()) {
            PlayerInventoryHandle.T.setItemInMainHand.invoke(humanEntity.getInventory(), item);
        } else if (PlayerInventoryHandle.T.setItemInHand.isAvailable()) {
            PlayerInventoryHandle.T.setItemInHand.invoke(humanEntity.getInventory(), item);
        } else {
            // Silently fail
        }
    }

    /**
     * Sets the item held in the off hand of the human. This method will silently fail
     * on versions before and including MC 1.8.8.
     * 
     * @param humanEntity to set the off hand held item
     * @param item to set to
     */
    public static void setItemInOffHand(HumanEntity humanEntity, ItemStack item) {
        if (humanEntity == null) {
            throw new IllegalArgumentException("humanEntity can not be null");
        }
        if (PlayerInventoryHandle.T.setItemInOffHand.isAvailable()) {
            PlayerInventoryHandle.T.setItemInOffHand.invoke(humanEntity.getInventory(), item);
        } else {
            // Silent fail, No OFF hand item holding <= MC 1.8.8
        }
    }
}
