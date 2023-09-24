package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import org.bukkit.advancement.Advancement;

/**
 * Temporary sets the advancement reward experience, loot and recipes
 * to none until the advancement is granted. This disables giving an advancement
 * to a Player when handling the AdvancementDone event.
 */
public abstract class AdvancementRewardsDisabler implements LibraryComponent {
    public static final AdvancementRewardsDisabler INSTANCE = LibraryComponentSelector.forModule(AdvancementRewardsDisabler.class)
            .setDefaultComponent(AdvancementRewardsDisablerDisabled::new)
            .addVersionOption("1.12", null, AdvancementRewardsDisablerImpl::new)
            .update();

    /**
     * Disables granting all rewards for the very next grant invocation
     *
     * @param advancement
     */
    public abstract void disableNextGrant(Advancement advancement);
}
