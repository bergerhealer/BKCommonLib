package com.bergerkiller.bukkit.common.events;

import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

/**
 * Event fired before progress is made on an advancement. Can be cancelled to disable
 * such progress being recorded, which prevents the {@link PlayerAdvancementDoneEvent}
 * being fired.
 */
public class PlayerAdvancementProgressEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final Advancement advancement;
    private final String criteria;

    public PlayerAdvancementProgressEvent(Player who, Advancement advancement, String criteria) {
        super(who);
        this.advancement = advancement;
        this.criteria = criteria;
    }

    /**
     * Gets the advancement on which a player made progress
     *
     * @return Advancement
     */
    public Advancement getAdvancement() {
        return advancement;
    }

    /**
     * Gets the criteria the player made progress with
     *
     * @return Criteria
     */
    public String getAdvancementCriteria() {
        return criteria;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
