/*
 * Copyright 2011 Tyler Blair. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *	1. Redistributions of source code must retain the above copyright notice, this list of
 *	   conditions and the following disclaimer.
 *
 *	2. Redistributions in binary form must reproduce the above copyright notice, this list
 *	   of conditions and the following disclaimer in the documentation and/or other materials
 *	   provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */
package com.bergerkiller.bukkit.common.metrics;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.plugin.Plugin;

/**
 * Represents a custom graph on the website
 */
public class Graph {
	/**
	 * The graph's name, alphanumeric and spaces only :) If it does not comply to the above when submitted, it is
	 * rejected
	 */
	private final String name;
	/**
	 * The set of plotters that are contained within this graph
	 */
	private final Map<String, Object> plotters = new LinkedHashMap<String, Object>();

	/**
	 * Constructs a new default Graph for the plugin.
	 * The name of this new Graph is "Default".
	 */
	public Graph() {
		this("Default");
	}

	/**
	 * Constructs a new Graph with the name specified
	 * 
	 * @param name of the Graph
	 */
	public Graph(final String name) {
		this.name = name;
	}

	/**
	 * Gets the graph's name
	 *
	 * @return the Graph's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Removes all currently stored plotters from this graph
	 */
	public void clearPlotters() {
		plotters.clear();
	}

	/**
	 * Remove a plotter from the graph
	 *
	 * @param name of the plotter to remove from the graph
	 */
	public void removePlotter(String name) {
		plotters.remove(name);
	}

	/**
	 * Toggles a plotter value on or off. If the state is set to True, the
	 * number '1' is mapped to the name. If the state is set to False, the
	 * entire plotter entry is removed, resulting in '0' being mapped.
	 * 
	 * @param name of the toggled plotter
	 * @param state to set the toggled plotter to
	 */
	public void togglePlotter(String name, boolean state) {
		if (state) {
			plotters.put(name, 1);
		} else {
			plotters.remove(name);
		}
	}

	/**
	 * Add a plotter to the graph, which will be used to plot entries.
	 * The {@link #toString()} method of the value should produce a valid numeric number.
	 * It is allowed to dynamically produce values this way, but keep in mind that
	 * the method is called from another thread.
	 *
	 * @param name of the plotter
	 * @param value of the plotter
	 */
	public void addPlotter(String name, Object value) {
		if (name == null) {
			throw new IllegalArgumentException("Can not add a plotter with a null name");
		}
		plotters.put(name, value);
	}

	/**
	 * Gets an <b>unmodifiable</b> mapping of the plotter objects in the graph
	 *
	 * @return an unmodifiable {@link java.util.Map} of the plotter objects
	 */
	public Map<String, Object> getPlotters() {
		return Collections.unmodifiableMap(plotters);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof Graph)) {
			return false;
		}

		final Graph graph = (Graph) object;
		return graph.name.equals(name);
	}

	/**
	 * Called right before this Graph is being sent to the server.
	 * This is called on the main thread (sync), it is allowed to call Bukkit methods.
	 */
	protected void onUpdate(Plugin plugin) {
	}

	/**
	 * Called right after the graph has been updated on the server.
	 * This is called on the main thread (sync), it is allowed to call Bukkit methods.
	 */
	protected void onReset(Plugin plugin) {
	}
}