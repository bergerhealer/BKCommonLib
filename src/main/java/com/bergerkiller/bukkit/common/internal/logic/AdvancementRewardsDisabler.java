package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import org.bukkit.advancement.Advancement;

/**
 * Temporary sets the advancement reward experience, loot and recipes
 * to none until the advancement is granted. This disables giving an advancement
 * to a Player when handling the AdvancementDone event.
 *
 * @deprecated Use {@link com.bergerkiller.bukkit.common.events.PlayerAdvancementProgressEvent} to disable
 *             advancements instead
 */
@Deprecated
public abstract class AdvancementRewardsDisabler implements LibraryComponent {
    public static final AdvancementRewardsDisabler INSTANCE = LibraryComponentSelector.forModule(AdvancementRewardsDisabler.class)
            .setDefaultComponent(AdvancementRewardsDisablerDisabled::new)
            .addVersionOption("1.12", "1.20.2", AdvancementRewardsDisabler_1_12_to_1_20_2::new)
            .update();

    /**
     * Disables granting all rewards for the very next grant invocation
     *
     * @param advancement
     */
    public abstract void disableNextGrant(Advancement advancement);
}
