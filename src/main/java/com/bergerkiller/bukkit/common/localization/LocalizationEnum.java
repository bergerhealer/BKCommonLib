package com.bergerkiller.bukkit.common.localization;

import org.bukkit.command.CommandSender;

/**
 * Basic implementation of ILocationDefault that supplies additional function
 * routines<br>
 * The get routine has to be implemented to link to the localization source
 * Plugin
 */
public abstract class LocalizationEnum implements ILocalizationEnum {

    private final String name;
    private final String defValue;

    public LocalizationEnum(String name, String defValue) {
        this.name = name;
        this.defValue = defValue;
    }

    @Override
    public String getDefault() {
        return this.defValue;
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Sends this Localization message to the sender specified
     *
     * @param sender to send to
     * @param arguments for the node
     */
    @Override
    public void message(CommandSender sender, String... arguments) {
        ILocalizationEnum.super.message(sender, arguments);
    }

    /**
     * Gets the locale value for this Localization node
     *
     * @param arguments for the node
     * @return Locale value
     */
    @Override
    public abstract String get(String... arguments);
}
