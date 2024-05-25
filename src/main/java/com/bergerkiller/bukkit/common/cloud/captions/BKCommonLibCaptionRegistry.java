package com.bergerkiller.bukkit.common.cloud.captions;

import org.incendo.cloud.caption.CaptionProvider;

/**
 * Caption registry that uses bi-functions to produce messages
 */
public class BKCommonLibCaptionRegistry {
    public static <C> CaptionProvider<C> provider() {
        return CaptionProvider.forCaption(BKCommonLibCaptionKeys.ARGUMENT_PARSE_FAILURE_SOUNDEFFECT, r -> ARGUMENT_PARSE_FAILURE_SOUNDEFFECT);
    }

    /**
     * Default caption for {@link BKCommonLibCaptionKeys#ARGUMENT_PARSE_FAILURE_SOUNDEFFECT}
     */
    public static final String ARGUMENT_PARSE_FAILURE_SOUNDEFFECT = "'{input}' is not a valid sound effect name";
}
