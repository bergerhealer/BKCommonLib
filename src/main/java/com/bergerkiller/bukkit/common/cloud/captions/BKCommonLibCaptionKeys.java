package com.bergerkiller.bukkit.common.cloud.captions;

import org.incendo.cloud.caption.Caption;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public final class BKCommonLibCaptionKeys {

    private static final Collection<Caption> RECOGNIZED_CAPTIONS = new LinkedList<>();

    /**
     * Variables: {input}
     */
    public static final Caption ARGUMENT_PARSE_FAILURE_SOUNDEFFECT = of("argument.parse.failure.bkcommonlib.soundeffect");

    private BKCommonLibCaptionKeys() {
    }

    private static Caption of(final String key) {
        final Caption caption = Caption.of(key);
        RECOGNIZED_CAPTIONS.add(caption);
        return caption;
    }

    /**
     * Get an immutable collection containing all standard caption keys
     *
     * @return Immutable collection of keys
     */
    public static Collection<Caption> getBukkitCaptionKeys() {
        return Collections.unmodifiableCollection(RECOGNIZED_CAPTIONS);
    }
}
