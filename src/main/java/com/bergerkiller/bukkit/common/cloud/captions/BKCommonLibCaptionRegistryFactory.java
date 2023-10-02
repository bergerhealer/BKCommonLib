package com.bergerkiller.bukkit.common.cloud.captions;

/**
 * Factory creating {@link BKCommonLibCaptionRegistry} instances
 *
 * @param <C> Command sender type
 */
public final class BKCommonLibCaptionRegistryFactory<C> {

    /**
     * Create a new BKCommonLib caption registry instance
     *
     * @return Created instance
     */
    public BKCommonLibCaptionRegistry<C> create() {
        return new BKCommonLibCaptionRegistry<>();
    }
}
