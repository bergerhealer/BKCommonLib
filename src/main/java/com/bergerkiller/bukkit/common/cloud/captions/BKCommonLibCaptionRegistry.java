package com.bergerkiller.bukkit.common.cloud.captions;

import cloud.commandframework.captions.SimpleCaptionRegistry;

/**
 * Caption registry that uses bi-functions to produce messages
 *
 * @param <C> Command sender type
 */
public class BKCommonLibCaptionRegistry<C> extends SimpleCaptionRegistry<C> {

    /**
     * Default caption for {@link BKCommonLibCaptionKeys#ARGUMENT_PARSE_FAILURE_SOUNDEFFECT}
     */
    public static final String ARGUMENT_PARSE_FAILURE_SOUNDEFFECT = "'{input}' is not a valid sound effect name";

    @SuppressWarnings("deprecation")
    protected BKCommonLibCaptionRegistry() {
        super();
        this.registerMessageFactory(
                BKCommonLibCaptionKeys.ARGUMENT_PARSE_FAILURE_SOUNDEFFECT,
                (caption, sender) -> ARGUMENT_PARSE_FAILURE_SOUNDEFFECT
        );
    }
}
