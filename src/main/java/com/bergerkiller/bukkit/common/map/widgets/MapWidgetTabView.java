package com.bergerkiller.bukkit.common.map.widgets;

import java.util.ArrayList;
import java.util.List;

/**
 * Switches between displaying different pages ('tabs'). For each tab
 * widgets can be added to be displayed.
 */
public class MapWidgetTabView extends MapWidget {
    private final List<Tab> tabs = new ArrayList<Tab>();

    /**
     * Adds a new tab to this tab view. Custom tabs can be implemented
     * and added here. It is recommended to use {@link #addTab()}
     * for normal tabs instead. A tab can only be added to a single tab view.
     * Trying to add it to another will remove it from the previous.
     * 
     * @param tab to add
     * @return added tab
     */
    public <T extends Tab> T addTab(T tab) {
        {
            Tab t = (Tab) tab;
            if (t.tabView != null) {
                t.tabView.removeTab(t);
                t.tabView = null;
            }
            t.tabView = this;
        }

        this.tabs.add(tab);
        if (this.getWidgetCount() == 0) {
            this.addWidget(tab);
            this.updateBounds();
        }
        return tab;
    }

    /**
     * Adds a new tab to this tab view
     * 
     * @return tab added
     */
    public Tab addTab() {
        return addTab(new Tab());
    }

    /**
     * Gets the total number of tabs
     * 
     * @return tab count
     */
    public int getTabCount() {
        return this.tabs.size();
    }

    /**
     * Gets an added tab by index
     * 
     * @param index of the tab to get
     * @return tab at this index
     */
    public Tab getTab(int index) {
        return this.tabs.get(index);
    }

    /**
     * Gets the index of the currently selected tab
     * 
     * @return tab index
     */
    public int getSelectedIndex() {
        return (this.getWidgetCount() == 0) ? -1 : this.tabs.indexOf(this.getWidget(0));
    }

    /**
     * Sets the tab that is currently selected and displayed by index
     * 
     * @param index to select, -1 to deselect all tabs
     * @return this tab view
     */
    public MapWidgetTabView setSelectedIndex(int index) {
        if (index >= 0) {
            return this.setSelectedTab(this.tabs.get(index));
        } else {
            return this.setSelectedTab(null);
        }
    }

    /**
     * Gets the tab that is currently selected and displayed
     * 
     * @return selected tab, null if no tabs exist
     */
    public Tab getSelectedTab() {
        return this.getWidgetCount() == 0 ? null : (Tab) this.getWidget(0);
    }

    /**
     * Sets the tab that is currently selected and displayed
     * 
     * @param tab to select
     * @return this tab view
     */
    public MapWidgetTabView setSelectedTab(Tab tab) {
        if (!this.tabs.contains(tab)) {
            throw new IllegalArgumentException("Tab is not part of this tab view");
        }
        if (this.getWidgetCount() == 0 || this.getWidget(0) != tab) {
            this.clearWidgets();
            this.addWidget(tab);
            this.updateBounds();
        }
        return this;
    }

    /**
     * Removes a tab from this tab view. If the tab was selected, a different one is selected
     * instead.
     * 
     * @param tab to remove
     */
    public void removeTab(Tab tab) {
        int index = this.tabs.indexOf(tab);
        if (index == -1) {
            return;
        }
        this.tabs.remove(index);
        if (this.getWidgetCount() > 0 && this.getWidget(0) == tab) {
            this.clearWidgets();
            if (this.tabs.size() > 0) {
                this.addWidget(this.tabs.get(Math.min(index, this.tabs.size() - 1)));
                this.updateBounds();
            }
        }
    }

    /**
     * Removes all tabs that are added to this tab view
     */
    public void clearTabs() {
        this.tabs.clear();
        this.clearWidgets();
    }

    @Override
    public void onBoundsChanged() {
        updateBounds();
    }

    private void updateBounds() {
        if (this.getWidgetCount() > 0) {
            this.getWidget(0).setBounds(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * A single page in the tab view
     */
    public static class Tab extends MapWidget {
        private MapWidgetTabView tabView = null;

        public Tab() {
            this.setRetainChildWidgets(true);
        }

        /**
         * Gets the index of this tab in the tab view
         * 
         * @return tab index
         */
        public int getIndex() {
            return (this.tabView == null) ? -1 : this.tabView.tabs.indexOf(this);
        }

        /**
         * Gets whether or not this tab is currently selected in the tab view
         * 
         * @return True if selected
         */
        public boolean isSelected() {
            return this.tabView != null && this.tabView.getSelectedTab() == this;
        }

        /**
         * Switches the tab view to select this tab
         */
        public void select() {
            if (this.tabView != null) {
                this.tabView.setSelectedTab(this);
            }
        }

        /**
         * Removes this tab from the tab view
         */
        public void remove() {
            if (this.tabView != null) {
                this.tabView.removeTab(this);
            }
        }
    }
}
