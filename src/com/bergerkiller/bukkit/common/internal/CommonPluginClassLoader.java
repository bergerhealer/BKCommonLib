package com.bergerkiller.bukkit.common.internal;

import java.net.URL;

import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.PluginClassLoader;

/**
 * Redirects package paths to the correct version to fix Class Not Found exceptions
 */
public class CommonPluginClassLoader extends PluginClassLoader {

	public CommonPluginClassLoader(JavaPluginLoader loader, URL[] urls) {
		super(loader, urls, CommonPluginClassLoader.class.getClassLoader());
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		System.out.println("Loading a class: " + name);
		// TODO Auto-generated method stub
		return super.loadClass(name, resolve);
	}
}
