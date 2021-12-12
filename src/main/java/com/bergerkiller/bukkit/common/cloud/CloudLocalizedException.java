package com.bergerkiller.bukkit.common.cloud;

import com.bergerkiller.bukkit.common.localization.ILocalizationEnum;

import cloud.commandframework.context.CommandContext;

/**
 * An exception that, when thrown, will result in a localized message
 * being sent to the sender.
 */
public class CloudLocalizedException extends IllegalArgumentException {
    private static final long serialVersionUID = -5070196948773497905L;
    private final CommandContext<?> context;
    private final ILocalizationEnum message;
    private final String[] input;

    public CloudLocalizedException(
            final CommandContext<?> context,
            final ILocalizationEnum message,
            final String... input
    ) {
        this.context = context;
        this.message = message;
        this.input = input;
    }

    @Override
    public final String getMessage() {
        return this.message.get(this.input);
    }

    /**
     * Get the command context
     *
     * @return Command context
     */
    public final CommandContext<?> getContext() {
        return this.context;
    }
}
