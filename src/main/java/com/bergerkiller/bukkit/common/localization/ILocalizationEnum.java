package com.bergerkiller.bukkit.common.localization;

import org.bukkit.command.CommandSender;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Interface for a LocalizationEnum. Can be implemented by
 * an actual enum to provide localization constants and defaults.
 */
public interface ILocalizationEnum extends ILocalizationDefault {

    /**
     * Sends this Localization message to the sender specified
     *
     * @param sender to send to
     * @param arguments for the node
     */
    default void message(CommandSender sender, String... arguments) {
        String text = get(arguments);
        if (!LogicUtil.nullOrEmpty(text)) {
            sender.sendMessage(text);
        }
    }

    /**
     * Gets the locale value for this Localization node
     *
     * @param arguments for the node
     * @return Locale value
     */
    String get(String... arguments);
}
