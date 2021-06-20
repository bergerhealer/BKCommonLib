package com.bergerkiller.bukkit.common.internal.logic;

import java.util.logging.Level;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.Logging;

public abstract class CreaturePreSpawnHandler {
    public static final CreaturePreSpawnHandler INSTANCE;

    static {
        // Detect presence of Paper's PreCreatureSpawnEvent
        boolean hasPaperPreSpawnEvent = false;
        try {
            Class.forName("com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent");
            hasPaperPreSpawnEvent = true;
        } catch (ClassNotFoundException ex) {}

        CreaturePreSpawnHandler handler;
        try {
            if (hasPaperPreSpawnEvent) {
                handler = new CreaturePreSpawnHandler_Paper();
            } else {
                handler = new CreaturePreSpawnHandler_Spigot();
            }
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize creature pre spawn handler, event disabled", t);
            handler = new DisabledHandler();
        }
        INSTANCE = handler;
    }

    /**
     * Called when the BKCommonLib CreaturePreSpawnEvent has a new handler registered,
     * when before the event had none. If the handling of the event was disabled because
     * it wasn't used, it is here that handling should be enabled.
     */
    public abstract void onEventHasHandlers();

    /**
     * Called when a new world is initialized, or on startup for all worlds
     * that were already initialized.
     *
     * @param world World that was enabled
     */
    public abstract void onWorldEnabled(World world);

    /**
     * Called when the plugin is disabled and existing worlds need any hooks
     * un-registered.
     *
     * @param world World that was disabled (to this plugin)
     */
    public abstract void onWorldDisabled(World world);

    private static final class DisabledHandler extends CreaturePreSpawnHandler {
        @Override
        public void onWorldEnabled(World world) {
        }

        @Override
        public void onWorldDisabled(World world) {
        }

        @Override
        public void onEventHasHandlers() {
        }
    }
}
