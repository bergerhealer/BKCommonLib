package com.bergerkiller.bukkit.common.block;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.hooks.LegacyContainerAnvilHook;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle;
import com.bergerkiller.generated.net.minecraft.world.inventory.ContainerAnvilHandle;
import com.bergerkiller.generated.net.minecraft.world.inventory.ContainerHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;

/**
 * Based on MapWidgetAnvil<br>
 * <br> 
 * Construct a custom implementation and call {@link #open()} to open the dialog
 */
public class InputDialogAnvil {
    private final Plugin plugin;
    private final Player player;
    public final Button LEFT_BUTTON = new Button(this, 0);
    public final Button MIDDLE_BUTTON = new Button(this, 1);
    public final Button RIGHT_BUTTON = new Button(this, 2);
    private final Set<InventoryView> _openInventories;
    private final Listener _listener;
    private boolean _isWindowOpen = false;
    private String _text = "";

    public InputDialogAnvil(Plugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this._openInventories = Collections.newSetFromMap(new WeakHashMap<InventoryView, Boolean>());
        if (CommonCapabilities.HAS_PREPARE_ANVIL_EVENT) {
            this._listener = new EventListenerFull();
        } else {
            this._listener = new EventListenerBase();
        }
    }

    /**
     * Gets the current text input by the user
     * 
     * @return text
     */
    public String getText() {
        return this._text;
    }

    /**
     * Gets the title to display for the window
     *
     * @return title
     */
    public ChatText getTitle() {
        return null;
    }

    /**
     * Called when the player clicks on one of the three buttons in the menu
     */
    public void onClick(Button button) {
    }

    /**
     * Called when the name text field is changed by the player
     */
    public void onTextChanged() {
    }

    /**
     * Called before the menu is opened. The dialog can be setup here.
     */
    public void onOpen() {
    }

    /**
     * Called when the anvil menu is closed by a viewing player, or by the server
     */
    public void onClose() {
    }

    public void open() {
        this.setWindowOpen(true);
    }

    public void close() {
        this.setWindowOpen(false);
    }

    private void handleTextChange(InventoryView view) {
        // String new_text = view.getInventory().getRenameText(); // Not backwards-compatible
        String new_text = ContainerAnvilHandle.fromBukkit(view).getRenameText();
        new_text = LogicUtil.fixNull(new_text, "");
        if (!CommonCapabilities.EMPTY_ITEM_NAME) {
            new_text = new_text.replace("\0", "");
        }
        if (!_text.equals(new_text)) {
            _text = new_text;
            onTextChanged();
        }

        // force resend the buttons
        refreshButtons(view);
    }

    private void refreshButtons(InventoryView view) {
        LEFT_BUTTON.refresh(view);
        MIDDLE_BUTTON.refresh(view);
        RIGHT_BUTTON.refresh(view);
    }

    private void setWindowOpen(boolean enabled) {
        if (this._isWindowOpen == enabled) {
            return;
        }
        this._isWindowOpen = enabled;

        // Close all old views first (to be sure), both when enabling and disabling
        for (InventoryView view : this._openInventories) {
            view.close();
        }
        this._openInventories.clear();

        if (enabled) {
            // Callback
            onOpen();

            // Start listening
            Bukkit.getPluginManager().registerEvents(this._listener, plugin);

            // Reset text
            this._text = "";

            // Open windows for all viewing players
            final InventoryView view = EntityPlayerHandle.fromBukkit(player).openAnvilWindow(getTitle());

            // Required for handling text changes < MC 1.9
            if (!CommonCapabilities.HAS_PREPARE_ANVIL_EVENT) {
                ContainerAnvilHandle container = ContainerAnvilHandle.fromBukkit(view);
                LegacyContainerAnvilHook hook = ClassHook.get(container.getRaw(), LegacyContainerAnvilHook.class);
                hook.textChangeCallback = () -> handleTextChange(view);
            }

            this._openInventories.add(view);
            this.refreshButtons(view);

            // If it couldn't be opened for anyone, close itself
            if (this._openInventories.isEmpty()) {
                setWindowOpen(false);
            }

        } else {
            // Unregister event listener
            CommonUtil.unregisterListener(this._listener);

            // Event
            onClose();
        }
    }

    /**
     * A single clickable button in the anvil menu, represented as an ItemStack
     * in the inventory
     */
    public static class Button {
        private final InputDialogAnvil _owner;
        private final int _index;
        private String _title;
        private String _description;
        private Material _material;

        public Button(InputDialogAnvil owner, int index) {
            this._owner = owner;
            this._index = index;
            this._title = "";
            this._description = "";
            this._material = Material.AIR;
        }

        /**
         * Gets the index of this button in the menu
         * 
         * @return menu item slot index
         */
        public int getIndex() {
            return this._index;
        }

        /**
         * Gets the title displayed when hovering over the button.
         * For the left-most slot, this also sets the initial text field value.
         * 
         * @return title
         */
        public String getTitle() {
            return this._title;
        }

        /**
         * Sets the title displayed when hovering over the button.
         * For the left-most slot, this also sets the initial text field value.
         * 
         * @param title to set
         */
        public void setTitle(String title) {
            if (!LogicUtil.bothNullOrEqual(this._title, title)) {
                this._title = title;
                this.refresh();
            }
        }

        /**
         * Gets the description displayed when hovering over the button
         * 
         * @return description
         */
        public String getDescription() {
            return this._description;
        }

        /**
         * Sets the description displayed when hovering over the button
         * 
         * @param description to set
         */
        public void setDescription(String description) {
            if (!LogicUtil.bothNullOrEqual(this._description, description)) {
                this._description = description;
                this.refresh();
            }
        }

        /**
         * Gets the material of the item that makes up the button
         * 
         * @return material
         */
        public Material getMaterial() {
            return this._material;
        }

        /**
         * Sets the material of the item that makes up the button
         * 
         * @param material to set
         */
        public void setMaterial(Material material) {
            if (this._material != material) {
                this._material = material;
                this.refresh();
            }
        }

        /**
         * Sets the material of the item that makes up the button
         * from an ItemStack
         * 
         * @param material item
         */
        public void setItemMaterial(ItemStack material) {
            setMaterial(material == null ? null : material.getType());
        }

        protected ItemStack createItem() {
            if (getMaterial() == null || BlockData.AIR.isType(getMaterial())) {
                return null;
            } else {
                ItemStack item = ItemUtil.createItem(getMaterial(), 1);
                if (this.getTitle() != null && !this.getTitle().isEmpty()) {
                    ItemUtil.setDisplayName(item, this.getTitle());
                } else if (CommonCapabilities.EMPTY_ITEM_NAME) {
                    ItemUtil.setDisplayName(item, "");
                } else {
                    ItemUtil.setDisplayName(item, "\0");
                }
                ItemUtil.getMetaTag(item).putValue("RepairCost", 0);
                if (this.getDescription() != null && !this.getDescription().isEmpty()) {
                    for (String line : this.getDescription().split("\n")) {
                        ItemUtil.addLoreName(item, ChatColor.RESET.toString() + line);
                    }
                }
                return item;
            }
        }

        protected void refresh() {
            for (InventoryView view : this._owner._openInventories) {
                this.refresh(view);
            }
        }

        protected void refresh(InventoryView view) {
            if (!this._owner._isWindowOpen) {
                return;
            }
            int windowId = ContainerHandle.fromBukkit(view).getWindowId();
            CommonPacket set_output_packet = PacketType.OUT_WINDOW_SET_SLOT.newInstance();
            set_output_packet.write(PacketType.OUT_WINDOW_SET_SLOT.windowId, windowId);
            set_output_packet.write(PacketType.OUT_WINDOW_SET_SLOT.slot, getIndex());
            set_output_packet.write(PacketType.OUT_WINDOW_SET_SLOT.item, createItem());
            PacketUtil.sendPacket((Player) view.getPlayer(), set_output_packet);
        }

        @Override
        public String toString() {
            if (this.getDescription() == null || this.getDescription().isEmpty()) {
                return "Button[" + this.getIndex() + "]";
            } else {
                return this.getDescription();
            }
        }
    }

    // Listens to Bukkit events for refreshing and handling the map widget anvil
    private class EventListenerBase implements Listener {

        protected boolean mustHandle(InventoryEvent event) {
            // Check whether we even have windows open
            // If not, deactivate the widget again
            if (_openInventories.isEmpty()) {
                setWindowOpen(false);
                return false;
            }

            // If not interesting to us, ignore event
            if (!(event.getInventory() instanceof AnvilInventory) ||
                !_openInventories.contains(event.getView()))
            {
                return false;
            }

            return true; // yay!
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerQuit(PlayerQuitEvent event) {
            if (event.getPlayer() == player) {
                setWindowOpen(false);
            }
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerTeleport(PlayerTeleportEvent event) {
            if (event.getPlayer() == player) {
                setWindowOpen(false);
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onInventoryClose(InventoryCloseEvent event) {
            if (!mustHandle(event)) return;

            setWindowOpen(false);
        }

        @SuppressWarnings("deprecation")
        @EventHandler(priority = EventPriority.LOWEST)
        public void onInventoryClick(InventoryClickEvent event) {
            if (!mustHandle(event)) return;

            final InventoryView view = event.getView();

            // Use raw slot
            Button button;
            if (event.getRawSlot() == 0) {
                button = LEFT_BUTTON;
            } else if (event.getRawSlot() == 1) {
                button = MIDDLE_BUTTON;
            } else if (event.getRawSlot() == 2) {
                button = RIGHT_BUTTON;
            } else {
                return;
            }

            ItemStack itemOnCursor = event.getCursor();
            event.setCursor(null);
            event.setResult(Result.DENY);

            if (!ItemUtil.isEmpty(itemOnCursor)) {
                view.getBottomInventory().addItem(itemOnCursor);
            }

            onClick(button);

            if (_isWindowOpen) {
                CommonUtil.nextTick(() -> refreshButtons(view));
            }
        }
    }

    // usable since MC 1.9
    private class EventListenerFull extends EventListenerBase {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onPrepareAnvil(PrepareAnvilEvent event) {
            if (!mustHandle(event)) return;

            handleTextChange(event.getView());
        }
    }
}
