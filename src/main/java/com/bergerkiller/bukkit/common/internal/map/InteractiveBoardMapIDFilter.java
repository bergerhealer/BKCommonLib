package com.bergerkiller.bukkit.common.internal.map;

import java.util.function.IntPredicate;

import org.bukkit.plugin.Plugin;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

class InteractiveBoardMapIDFilter implements IntPredicate {
    private final Plugin plugin;
    private final InteractiveBoardLogicHandle handle;

    public InteractiveBoardMapIDFilter(Plugin plugin) {
        this.plugin = plugin;
        this.handle = Template.Class.create(InteractiveBoardLogicHandle.class);
        this.handle.forceInitialization();
    }

    @Override
    public boolean test(int mapId) {
        return handle.isMapFiltered(this.plugin, mapId);
    }

    @Template.Optional
    public static abstract class InteractiveBoardLogicHandle extends Template.Class<Template.Handle> {

        /*
         * <IS_MAP_FILTERED>
         * public static boolean isMapFiltered(com.interactiveboard.InteractiveBoard plugin, int mapId) {
         *     return plugin.getBoardDisplayManager().isBoardMap(mapId);
         * }
         */
        @Template.Generated("%IS_MAP_FILTERED%")
        public abstract boolean isMapFiltered(Object plugin, int mapId);
    }
}
