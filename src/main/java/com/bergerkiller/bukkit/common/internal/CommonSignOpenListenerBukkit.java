package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

/**
 * Listener used on 1.20.2+ to detect the PlayerSignOpenEvent event.
 * This event is used to track whether a player has opened an edit dialog
 * for a sign.
 */
class CommonSignOpenListenerBukkit implements Listener, EventExecutor {
    public final FastMethod<Void> callback;

    @SuppressWarnings("unchecked")
    public static void register(CommonPlugin plugin) throws Throwable {
        Class<? extends Event> openSignEventType = (Class<? extends Event>) Class.forName(
                "org.bukkit.event.player.PlayerSignOpenEvent");
        CommonSignOpenListenerBukkit listener = new CommonSignOpenListenerBukkit(openSignEventType);
        Bukkit.getPluginManager().registerEvent(openSignEventType, listener, EventPriority.MONITOR, listener, plugin);
    }

    private CommonSignOpenListenerBukkit(Class<? extends Event> openSignEventType) {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(openSignEventType);
        resolver.addImport(CommonListener.class.getName());
        MethodDeclaration callbackMethod = new MethodDeclaration(resolver, "" +
                "public static void callback(PlayerSignOpenEvent event) {\n" +
                "    if (event.getCause() != PlayerSignOpenEvent$Cause.PLACE) {\n" +
                "        CommonListener.storeEditedSign(event.getPlayer(), event.getSign());\n" +
                "    }\n" +
                "}"
        );
        this.callback = new FastMethod<>(callbackMethod);
        this.callback.forceInitialization();
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        callback.invoke(null, event);
    }
}
