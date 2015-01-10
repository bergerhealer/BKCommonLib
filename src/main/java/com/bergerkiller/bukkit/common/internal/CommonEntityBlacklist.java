package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.snowleo.bleedingmobs.BleedingMobs;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.Plugin;

import regalowl.hyperconomy.HyperConomy;

import com.bergerkiller.bukkit.common.filtering.Filter;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.narrowtux.showcase.Showcase;

public class CommonEntityBlacklist implements Filter<Entity> {
	private final List<BlacklistFilter> availableFilters = new ArrayList<BlacklistFilter>();

	public CommonEntityBlacklist() {
		// ===============================================
		// ==== Initialize all available filters here ====
		// ===============================================

		// Showcase
		availableFilters.add(new BlacklistFilter("Showcase") {
			@Override
			public boolean isFiltered(Entity element) {
				if (element instanceof Item) {
					return Showcase.instance.getItemByDrop((Item) element) != null;
				}
				return false;
			}
		});
		// Showcase Standalone
		availableFilters.add(new BlacklistFilter("ShowCaseStandalone") {
			@Override
			public boolean isFiltered(Entity element) {
				if (element instanceof Item) {
					ShowCaseStandalone plugin = (ShowCaseStandalone) getPlugin();
					return plugin.getShopHandler().isShopItem((Item) element);
				}
				return false;
			}
		});
		// HyperConomy
		availableFilters.add(new BlacklistFilter("HyperConomy") {
			@Override
			public boolean isFiltered(Entity element) {
				if (element instanceof Item) {
					return HyperConomy.hyperAPI.isItemDisplay((Item) element);
				}
				return false;
			}
		});
		// Bleeding Mobs
		availableFilters.add(new BlacklistFilter("BleedingMobs") {
			@Override
			@SuppressWarnings("deprecation")
			public boolean isFiltered(Entity element) {
				if (element instanceof Item) {
					BleedingMobs bm = (BleedingMobs) this.getPlugin();
					if (bm.isSpawning() || (bm.isWorldEnabled(element.getWorld()) && bm.isParticleItem(element.getUniqueId()))) {
						return true;
					}
				}
				return false;
			}
		});
	}

	public void updateDependency(Plugin plugin, String pluginName, boolean enabled) {
		for (BlacklistFilter filter : this.availableFilters) {
			if (filter.matchPlugin(pluginName)) {
				filter.trySetEnabled(plugin, enabled);
			}
		}
	}

	@Override
	public boolean isFiltered(Entity element) {
		for (BlacklistFilter filter : this.availableFilters) {
			try {
				if (filter.isEnabled() && filter.isFiltered(element)) {
					return true;
				}
			} catch (Throwable t) {
				CommonPlugin.LOGGER.log(Level.SEVERE, filter.getPluginName() + 
						" entity filter verification failed (update needed?), contact the authors!");
				filter.trySetEnabled(filter.getPlugin(), false);
			}
		}
		return false;
	}

	private static abstract class BlacklistFilter implements Filter<Entity> {
		private final String pluginName;
		private boolean enabled = false;
		private Plugin pluginInstance = null;

		public BlacklistFilter(String pluginName) {
			this.pluginName = pluginName;
		}

		public void trySetEnabled(Plugin plugin, boolean enabled) {
			this.enabled = enabled;
			try {
				onEnabledChange(plugin, enabled);
				if (enabled) {
					CommonPlugin.LOGGER.log(Level.INFO, pluginName + " detected: Items belonging to this plugin will be ignored");
				}
			} catch (Throwable t) {
				CommonPlugin.LOGGER.log(Level.SEVERE, "Error while " + (enabled ? "adding" : "removing") + 
						" item ignore support for " + pluginName, t);
			}
			this.pluginInstance = enabled ? plugin : null;
		}

		public void onEnabledChange(Plugin plugin, boolean enabled) {
		}

		public boolean isEnabled() {
			return this.enabled;
		}

		public String getPluginName() {
			return pluginName;
		}

		public Plugin getPlugin() {
			return pluginInstance;
		}

		public boolean matchPlugin(String pluginName) {
			return pluginName.equals(this.pluginName);
		}
	}
}
