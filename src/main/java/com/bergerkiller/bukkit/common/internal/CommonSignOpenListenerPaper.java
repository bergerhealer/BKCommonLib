package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.EventExecutor;

import java.util.Arrays;

/**
 * Listener used on 1.20.2+ to detect the PlayerSignOpenEvent event.
 * This event is used to track whether a player has opened an edit dialog
 * for a sign.
 */
class CommonSignOpenListenerPaper implements Listener, EventExecutor {
    private final Enum<?> PLACE_CAUSE;
    private final FastMethod<Sign> getSignMethod;
    private final FastMethod<? extends Enum> getCauseMethod;

    @SuppressWarnings("unchecked")
    private CommonSignOpenListenerPaper(Class<? extends Event> openSignEventType) throws Throwable {
        Class<? extends Enum> causeType = (Class<? extends Enum>) Class.forName("io.papermc.paper.event.player.PlayerOpenSignEvent$Cause");
        PLACE_CAUSE = Arrays.stream(causeType.getEnumConstants())
                .filter(n -> n.name().equals("PLACE"))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Cause enum PLACE not found"));

        getSignMethod = new FastMethod<>(openSignEventType.getMethod("getSign"));
        getSignMethod.forceInitialization();
        getCauseMethod = new FastMethod<>(openSignEventType.getMethod("getCause"));
        getCauseMethod.forceInitialization();
    }

    @SuppressWarnings("unchecked")
    public static void register(CommonPlugin plugin) throws Throwable {
        Class<? extends Event> openSignEventType = (Class<? extends Event>) Class.forName(
                "io.papermc.paper.event.player.PlayerOpenSignEvent");
        CommonSignOpenListenerPaper listener = new CommonSignOpenListenerPaper(openSignEventType);
        Bukkit.getPluginManager().registerEvent(openSignEventType, listener, EventPriority.MONITOR, listener, plugin);
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        if (((Cancellable) event).isCancelled()){
            return;
        }
        if (getCauseMethod.invoke(event) != PLACE_CAUSE) {
            return;
        }

        Player player = ((PlayerEvent) event).getPlayer();
        Sign sign = getSignMethod.invoke(event);
        CommonListener.editedSignBlocks.put(player, sign.getBlock());
    }
}
