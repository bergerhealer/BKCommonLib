package com.bergerkiller.bukkit.common.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import com.bergerkiller.bukkit.common.collections.EntityMap;
import com.bergerkiller.bukkit.common.collections.FilteredCollectionSelf;
import com.bergerkiller.bukkit.common.collections.UniqueList;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.tab.TabView;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

public class CommonTabController implements PacketListener, Listener {

    public static final int MAX_TEXT_LENGTH = 16;
    private static final char[] RANDOM_STYLE_CHARS;
    private final FieldAccessor<Integer> maxPlayersField;
    private int serverMaxPlayers;
    private int serverListWidth, serverListHeight, serverListCount;
    private int customListWidth, customListHeight, customListCount;
    private final EntityMap<Player, PlayerTabInfo> players = new EntityMap<Player, PlayerTabInfo>();
    private TabView defaultTab;

    static {
        LinkedHashSet<Character> chars = new LinkedHashSet<Character>();
		// Add all chars available that do not conflict with rendering
        // ===========================================================================
        LogicUtil.addArray(chars, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
        LogicUtil.addArray(chars, 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j');
        LogicUtil.addArray(chars, 'p', 'q', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');
        LogicUtil.addArray(chars, '+', '-', '=', '|', '(', ')', '{', '}', '[', ']');
		// ===========================================================================

        // Also add all upper-case versions
        for (Character c : chars.toArray(new Character[0])) {
            chars.add(Character.toUpperCase(c));
        }
        // To char array and check length
        RANDOM_STYLE_CHARS = Conversion.toCharArr.convert(chars);
        if (RANDOM_STYLE_CHARS.length < 60) {
            CommonPlugin.LOGGER.log(Level.WARNING, "Not enough unique characters to use: " + RANDOM_STYLE_CHARS.length);
        }
    }

    protected CommonTabController() {
        // Read server max players
        maxPlayersField = new SafeField<Integer>(CommonNMS.getPlayerList(), "maxPlayers");
        serverMaxPlayers = 0;
        // Default (startup) dimensions
        customListCount = serverListCount = 0;
        customListWidth = serverListWidth = 1;
        customListHeight = serverListHeight = 1;
        // Set the default tab initial
        defaultTab = TabView.DEFAULT;
    }

    private boolean hasChangedMaxPlayers() {
        return customListCount > serverListCount;
    }

    /**
     * Gets the width of the (total) Tab view that is being used right now
     *
     * @return tab width
     */
    public int getWidth() {
        return customListWidth;
    }

    /**
     * Gets the height of the (total) Tab view that is being used right now
     *
     * @return tab height
     */
    public int getHeight() {
        return customListHeight;
    }

    /**
     * Gets the width of the default player list. This is the width that would
     * be used if no tabs of greater size are created.
     *
     * @return default width
     */
    public int getDefaultWidth() {
        return serverListWidth;
    }

    /**
     * Gets the height of the default player list. This is the height that would
     * be used if no tabs of greater size are created.
     *
     * @return default height
     */
    public int getDefaultHeight() {
        return serverListHeight;
    }

    /**
     * Sets the default player list dimensions based on the player maximum.
     * Bukkit.getMaxPlayers() fails when BKCommonLib enabled, and thus this
     * needs to be called the next tick after enabling.
     */
    public void setDefaultSize() {
        // Calculate the default tab view width and height
        serverMaxPlayers = Bukkit.getMaxPlayers();
        int slotCount = Math.min(serverMaxPlayers, 3 * TabView.MAX_HEIGHT);
        // Calculate the current dimensions of the Player List
        serverListWidth = MathUtil.ceil((double) slotCount / (double) TabView.MAX_HEIGHT);
        serverListHeight = MathUtil.floor((double) slotCount / (double) serverListWidth);
        serverListHeight = Math.max(serverListHeight, getMinHeight(serverListWidth));
        requestNewSize(serverListWidth, serverListHeight);
    }

    /**
     * Prepares the internal logic for the addition of a new tab dimension.
     *
     * @param width of the new tab
     * @param height of the new tab
     */
    public void requestNewSize(int width, int height) {
        if (width > customListWidth || height > customListHeight) {
            customListWidth = Math.max(customListWidth, width);
            customListHeight = Math.max(customListHeight, height);
            customListHeight = Math.max(customListHeight, getMinHeight(customListWidth));
            // Calculate the player list player count needed
            customListCount = customListWidth * customListHeight;
        }
    }

    /**
     * Forces all tabs to resend 'dirty' slot information to all their viewers.
     */
    public void refreshAllTabs() {
        for (PlayerTabInfo info : players.values()) {
            info.refresh();
        }
    }

    /**
     * Maps a tab to a player, telling it to start rendering this new Tab.
     *
     * @param player to map to
     * @param tab to map to the player
     */
    public void showTab(Player player, TabView tab) {
        getInfo(player).setCurrentTab(tab);
    }

    /**
     * Maps a tab to all current and future players, showing it's contents to
     * everyone
     *
     * @param tab to show to all players
     */
    public void showTabToAll(TabView tab) {
        defaultTab = tab;
        for (PlayerTabInfo info : players.values()) {
            info.setCurrentTab(tab);
        }
    }

    /**
     * Gets the Tab View that a player is currently viewing
     *
     * @param player to get the Tab View for
     * @return current player Tab View
     */
    public TabView getCurrentTab(Player player) {
        return getInfo(player).getCurrentTab();
    }

    /**
     * Reloads all text and ping values for a tab. This can be used when a large
     * amount of changes occur.
     *
     * @param tab to reload
     */
    public void reloadAll(TabView tab) {
        for (PlayerTabInfo info : getViewers(tab)) {
            info.reloadAll();
        }
    }

    /**
     * Updates the ping value of a slot in a tab
     *
     * @param tab to update
     * @param x - coordinate of the slot
     * @param y - coordinate of the slot
     * @param ping value to set to
     */
    public void setPing(TabView tab, int x, int y, int ping) {
        for (PlayerTabInfo info : getViewers(tab)) {
            info.setPing(x, y, ping);
        }
    }

    /**
     * Updates the text value of a slot in a tab
     *
     * @param tab to update
     * @param x - coordinate of the slot
     * @param y - coordinate of the slot
     * @param text value to set to
     */
    public void setText(TabView tab, int x, int y, String text) {
        for (PlayerTabInfo info : getViewers(tab)) {
            info.setText(x, y, text);
        }
    }

    /**
     * Updates the ping and text values of a slot in a tab
     *
     * @param tab to update
     * @param x - coordinate of the slot
     * @param y - coordinate of the slot
     * @param text value to set to
     * @param ping value to set to
     */
    public void setSlot(TabView tab, int x, int y, String text, int ping) {
        for (PlayerTabInfo info : getViewers(tab)) {
            info.set(x, y, text, ping);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLoginFirst(PlayerLoginEvent event) {
        if (event.getResult() == Result.ALLOWED) {
            // Register a PlayerTabInfo instance
            getInfo(event.getPlayer());
            // Ensure the listeners are registered in the right order
            CommonUtil.queueListenerLast(this, PlayerLoginEvent.class);
            CommonUtil.queueListenerFirst(this, PlayerJoinEvent.class);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult() == Result.ALLOWED) {
            // Temporarily set the max player count to the one as specified here
            if (hasChangedMaxPlayers()) {
                maxPlayersField.set(CommonNMS.getPlayerList(), customListCount);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (hasChangedMaxPlayers()) {
            // Restore server max players (required, otherwise new people can join a full server all of a sudden!)
            maxPlayersField.set(CommonNMS.getPlayerList(), serverMaxPlayers);
        }
        // Send all the elements of the current tab for this player
        getInfo(event.getPlayer()).refresh();
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        /*
         if (event.getType() == PacketType.OUT_PLAYER_INFO && !event.isCancelled()) {
         CommonPacket packet = event.getPacket();
         String name = packet.read(PacketType.OUT_PLAYER_INFO.playerName);
         int ping = packet.read(PacketType.OUT_PLAYER_INFO.ping);
         boolean register = packet.read(PacketType.OUT_PLAYER_INFO.online);
         event.setCancelled(!getInfo(event.getPlayer()).handlePlayerInfoPacket(name, ping, register));
         }
         */
    }

    private Collection<PlayerTabInfo> getViewers(final TabView currentTab) {
        return new FilteredCollectionSelf<PlayerTabInfo>(players.values()) {
            @Override
            public boolean isFiltered(PlayerTabInfo element) {
                return element.getCurrentTab() != currentTab;
            }
        };
    }

    private PlayerTabInfo getInfo(Player player) {
        PlayerTabInfo info = players.get(player);
        if (info == null) {
            info = new PlayerTabInfo(player, customListWidth, customListHeight, defaultTab);
            players.put(player, info);
        }
        return info;
    }

    private static int getMinHeight(int width) {
        if (width == 1) {
            return 1;
        } else if (width == 2) {
            return 11;
        } else if (width == 3) {
            return 14;
        } else {
            return 1;
        }
    }

    /**
     * Stores the information as known to a single Player
     */
    private static class PlayerTabInfo {

        private final Player player;
        private final Map<String, Integer> defaultView = new LinkedHashMap<String, Integer>();
        private final UniqueList<String> names = new UniqueList<String>();
        private final String[] text;
        private final int[] ping;
        private final int width, height, count;
        private TabView currentTab;
        private int dirtyStartIndex;

        public PlayerTabInfo(Player player, int width, int height, TabView currentTab) {
            this.player = player;
            this.width = width;
            this.height = height;
            this.count = width * height;
            this.text = new String[this.count];
            this.ping = new int[this.count];
            this.setCurrentTab(currentTab);
            // No need to refresh if it is the default tab initially
            if (currentTab == TabView.DEFAULT) {
                this.dirtyStartIndex = this.count;
            }
        }

        public TabView getCurrentTab() {
            return this.currentTab;
        }

        public void setCurrentTab(TabView currentTab) {
            // No need to do anything if it's no valid tab change
            if (this.currentTab == currentTab) {
                return;
            }
            // If the current tab was DEFAULT, update the names
            if (this.currentTab == TabView.DEFAULT) {
                this.names.clear();
                this.names.addAll(this.defaultView.keySet());
            }
            this.currentTab = currentTab;
            // Only need to clear (and not send or add names) if EMPTY
            if (currentTab == TabView.EMPTY) {
                Arrays.fill(this.text, TabView.TEXT_DEFAULT);
                Arrays.fill(this.ping, TabView.PING_DEFAULT);
                return;
            }
            // Write all information from the tab to this class
            reloadAll();
        }

        /**
         * Forces all values to be re-set and re-sent to the player
         */
        public void reloadAll() {
            int tabWidth = currentTab.getWidth();
            int tabHeight = currentTab.getHeight();
            int x, y, index;
            String tabText;
            int tabPing;
            for (x = 0; x < this.width; x++) {
                for (y = 0; y < this.height; y++) {
                    index = getIndex(x, y);
                    // Read from the tab if in bounds
                    if (x < tabWidth && y < tabHeight) {
                        tabText = currentTab.getText(x, y);
                        tabPing = currentTab.getPing(x, y);
                    } else {
                        tabText = TabView.TEXT_DEFAULT;
                        tabPing = TabView.PING_DEFAULT;
                    }
                    this.text[index] = tabText;
                    this.ping[index] = tabPing;
                }
            }
            // Refresh from the first element
            this.dirtyStartIndex = 0;
        }

        public void setText(int x, int y, String text) {
            int index = getIndex(x, y);
            set(index, text, ping[index]);
        }

        public void set(int x, int y, String text, int ping) {
            set(getIndex(x, y), text, ping);
        }

        private void set(int index, String text, int ping) {
            if (this.text[index].equals(text)) {
                // Only set ping
                setPing(index, ping);
                return;
            }
            this.ping[index] = ping;
            this.text[index] = text;
            // Resend all text past the index
            if (index < this.dirtyStartIndex) {
                this.dirtyStartIndex = index;
            }
        }

        public void setPing(int x, int y, int ping) {
            setPing(getIndex(x, y), ping);
        }

        private void setPing(int index, int ping) {
            if (this.ping[index] == ping) {
                return;
            }
            this.ping[index] = ping;
            // Can't update ping if out of bounds (and thus not shown!)
            if (index >= names.size()) {
                return;
            }
			// We can (safely) instantly send here
            // If text changes too, oh well, too bad!
            this.showSlot(names.get(index), ping);
        }

        public boolean handlePlayerInfoPacket(String name, int ping, boolean register) {
            // Update the default view
            if (register) {
                defaultView.put(name, ping);
            } else {
                defaultView.remove(name);
            }

            // If the default tab is shown, allow it to go through
            return this.currentTab == TabView.DEFAULT;
        }

        public void refresh() {
            if (this.dirtyStartIndex < this.count) {
                this.refresh(this.dirtyStartIndex);
                this.dirtyStartIndex = this.count;
            }
        }

        private void refresh(int startIndex) {
            // Hide the old contents
            if (startIndex == 0) {
                // Send removal messages for all names and clear the names
                for (String name : names) {
                    hideSlot(name);
                }
                names.clear();
            } else {
                // Send removal messages and remove names past the index
                while ((names.size() - 1) >= startIndex) {
                    hideSlot(names.remove(startIndex));
                }
            }
            // Show the new contents
            if (currentTab == TabView.DEFAULT) {
                Iterator<Entry<String, Integer>> iter = LogicUtil.skipIterator(this.defaultView.entrySet().iterator(), startIndex);
                while (iter.hasNext()) {
                    Entry<String, Integer> entry = iter.next();
                    showSlot(entry.getKey(), entry.getValue());
                }
            } else if (currentTab != TabView.EMPTY) {
				// Find out the end-index to stop showing information at
                // No-slot is better than an empty slot, it reduces network usage
                int endIndex = this.count - 1;
                while (endIndex >= 0 && text[endIndex].equals(TabView.TEXT_DEFAULT)) {
                    endIndex--;
                }
                // Show the slots from start index to end index
                for (int i = startIndex; i <= endIndex; i++) {
                    showSlot(getName(text[i]), ping[i]);
                }
            }
        }

        private String getName(String text) {
            if (text.length() > MAX_TEXT_LENGTH) {
                text = text.substring(0, MAX_TEXT_LENGTH);
            }
            if (names.add(text)) {
                return text;
            }

            // Get rid of 2 chars before the maximum length, since we need to append style chars...
            int textLength = Math.min(text.length(), MAX_TEXT_LENGTH - 2);
            StringBuilder uniqueNameBuilder = new StringBuilder(textLength + 2);

			// Initial name + chat style char
            // Increment text length since we don't want to trim off the chat style char
            uniqueNameBuilder.append(text);
            uniqueNameBuilder.setLength(textLength);
            uniqueNameBuilder.append(StringUtil.CHAT_STYLE_CHAR);
            textLength++;

            // Let's start building!
            String uniqueName;
            for (char styleChar : RANDOM_STYLE_CHARS) {
                uniqueNameBuilder.setLength(textLength);
                uniqueNameBuilder.append(styleChar);
                uniqueName = uniqueNameBuilder.toString();
                if (names.add(uniqueName)) {
                    return uniqueName;
                }
            }
            throw new RuntimeException("Ran out of names to generate... :(");
        }

        private int getIndex(int x, int y) {
            return x + this.width * y;
        }

        private void hideSlot(String text) {
            //PacketUtil.sendPacket(player, PacketType.OUT_PLAYER_INFO.newInstance(text, false, 0), false);
        }

        private void showSlot(String text, int ping) {
            //PacketUtil.sendPacket(player, PacketType.OUT_PLAYER_INFO.newInstance(text, true, ping), false);
        }
    }
}
