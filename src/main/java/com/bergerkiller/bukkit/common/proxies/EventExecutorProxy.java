package com.bergerkiller.bukkit.common.proxies;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

public class EventExecutorProxy extends ProxyBase<EventExecutor> implements EventExecutor {

	public EventExecutorProxy(EventExecutor base) {
		super(base);
	}

	@Override
	public void execute(Listener listener, Event event) throws EventException {
		base.execute(listener, event);
	}
}
