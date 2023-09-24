package com.bergerkiller.bukkit.common.internal.logic;

import org.bukkit.advancement.Advancement;

/**
 * Used when the disabler cannot be initialized (error)
 */
class AdvancementRewardsDisablerDisabled extends AdvancementRewardsDisabler {

    @Override
    public void enable() throws Throwable {
    }

    @Override
    public void disable() throws Throwable {
    }

    @Override
    public void disableNextGrant(Advancement advancement) {
    }
}
