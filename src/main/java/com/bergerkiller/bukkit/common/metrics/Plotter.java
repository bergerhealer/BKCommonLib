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

/**
 * Interface used to collect custom data for a plugin
 */
public abstract class Plotter {

	/**
	 * The plot's name
	 */
	private final String name;

	/**
	 * Construct a plotter with the default plot name
	 */
	public Plotter() {
		this("Default");
	}

	/**
	 * Construct a plotter with a specific plot name
	 *
	 * @param name the name of the plotter to use, which will show up on the website
	 */
	public Plotter(final String name) {
		this.name = name;
	}

	/**
	 * Get the current value for the plotted point. Since this function defers to an external function it may or may
	 * not return immediately thus cannot be guaranteed to be thread friendly or safe. This function can be called
	 * from any thread so care should be taken when accessing resources that need to be synchronized.
	 *
	 * @return the current value for the point to be plotted.
	 */
	public abstract int getValue();

	/**
	 * Get the column name for the plotted point
	 *
	 * @return the plotted point's column name
	 */
	public String getColumnName() {
		return name;
	}

	/**
	 * Called after the website graphs have been updated
	 */
	public void reset() {
	}

	@Override
	public int hashCode() {
		return getColumnName().hashCode();
	}

	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof Plotter)) {
			return false;
		}

		final Plotter plotter = (Plotter) object;
		return plotter.name.equals(name) && plotter.getValue() == getValue();
	}
}