package com.bergerkiller.bukkit.common.map.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import com.bergerkiller.bukkit.common.block.InputDialogAnvil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.wrappers.ChatText;

/**
 * Add this widget to open an interactive anvil window, where the player
 * can input text. When this widget is deactivated, or the player
 * closes the window, the window is automatically closed
 * and focus returned to the parent.<br>
 * <br>
 * Unlike the other widgets, no contents are drawn to the map itself, and the
 * widget will have a default size and position of 0,0.<br>
 * <br>
 * To display the anvil GUI, activate the widget. The window can be closed
 * again by de-activating, or when the player closes the window some other way.
 * When the window is closed, focus is returned to the underlying map widget.
 */
public class MapWidgetAnvil extends MapWidget {
    public final Button LEFT_BUTTON = new Button(this, 0, i -> i.LEFT_BUTTON);
    public final Button MIDDLE_BUTTON = new Button(this, 1, i -> i.MIDDLE_BUTTON);
    public final Button RIGHT_BUTTON = new Button(this, 2, i -> i.RIGHT_BUTTON);
    private final Set<Player> _openFor = new HashSet<>();
    private final Map<Player, InputDialogAnvil> _openDialogs = new HashMap<>();
    private boolean _isOpen = false; // Avoids multiple onClose() being fired
    private String _text = "";

    public MapWidgetAnvil() {
        this.setBounds(0, 0, 0, 0);
        this.setFocusable(true);
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
     * Gets the title to display at the top of the Anvil dialog window. Can be overrided
     * to change the title. Will default to just showing 'Anvil' if null is returned.
     *
     * @return Title text
     */
    public ChatText getTitle() {
        return null;
    }

    /**
     * Instead of opening this anvil menu for players controlling the map with steering
     * controls, opens it for the players specified. If an Empty collection is
     * specified, opens it for controlling players instead.
     *
     * @param players Players to open the menu for
     * @return this
     */
    public MapWidgetAnvil openFor(Collection<Player> players) {
        this._openFor.clear();
        this._openFor.addAll(players);
        return this;
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
     * Called when the anvil menu is closed by a viewing player, or by the server
     */
    public void onClose() {
    }

    @Override
    public void onActivate() {
        super.onActivate();

        // Reset any previous open dialogs
        closeAllDialogs();

        // Reset text
        this._text = "";

        // Show to everyone viewing this display
        openDialogForAll();
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        closeAllDialogs();
    }

    private void openDialogForAll() {
        // Open windows for all viewing players
        for (Player player : (_openFor.isEmpty() ? this.display.getViewers() : _openFor)) {
            if (_openFor.isEmpty() && !this.display.isControlling(player)) {
                continue;
            }

            WidgetInputDialogAnvil dialog = new WidgetInputDialogAnvil(player);
            LEFT_BUTTON.applyAll(dialog);
            MIDDLE_BUTTON.applyAll(dialog);
            RIGHT_BUTTON.applyAll(dialog);
            this._openDialogs.put(player, dialog);
            dialog.open();
        }

        // If it couldn't be opened for anyone, close itself
        if (this._openDialogs.isEmpty()) {
            closeAllDialogs();
            this.deactivate();
            return;
        }

        _isOpen = true;
    }

    private void closeAllDialogs() {
        List<InputDialogAnvil> dialogs = new ArrayList<>(_openDialogs.values());
        dialogs.forEach(InputDialogAnvil::close);
        _openDialogs.clear(); // For good measure

        // Event
        if (_isOpen) {
            _isOpen = false;
            onClose();
        }
    }

    private Button buttonFromAnvilDialog(InputDialogAnvil dialog, InputDialogAnvil.Button button) {
        if (LEFT_BUTTON._buttonAccessor.apply(dialog) == button) {
            return LEFT_BUTTON;
        } else if (MIDDLE_BUTTON._buttonAccessor.apply(dialog) == button) {
            return MIDDLE_BUTTON;
        } else if (RIGHT_BUTTON._buttonAccessor.apply(dialog) == button) {
            return RIGHT_BUTTON;
        } else {
            return null; // Error?
        }
    }

    /**
     * A single clickable button in the anvil menu, represented as an ItemStack
     * in the inventory
     */
    public static class Button {
        private final MapWidgetAnvil _owner;
        private final int _index;
        private final Function<InputDialogAnvil, InputDialogAnvil.Button> _buttonAccessor;

        // These are only stored 'offline' if no input dialogs are open yet
        // The values are applied when the dialog is first activated
        private Material _material = Material.AIR;
        private String _title = "";
        private String _description = "";

        public Button(MapWidgetAnvil owner, int index, Function<InputDialogAnvil, InputDialogAnvil.Button> buttonAccessor) {
            this._owner = owner;
            this._index = index;
            this._buttonAccessor = buttonAccessor;
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
            this._title = title;
            forAllDialogs(b -> b.setTitle(title));
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
            this._description = description;
            forAllDialogs(b -> b.setDescription(description));
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
            this._material = material;
            forAllDialogs(b -> b.setMaterial(material));
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

        private void forAllDialogs(Consumer<InputDialogAnvil.Button> action) {
            for (InputDialogAnvil dialog : _owner._openDialogs.values()) {
                action.accept(_buttonAccessor.apply(dialog));
            }
        }

        private void applyAll(InputDialogAnvil dialog) {
            InputDialogAnvil.Button dialogButton = _buttonAccessor.apply(dialog);
            dialogButton.setTitle(getTitle());
            dialogButton.setDescription(getDescription());
            dialogButton.setMaterial(getMaterial());
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

    private class WidgetInputDialogAnvil extends InputDialogAnvil {

        public WidgetInputDialogAnvil(Player player) {
            super(MapWidgetAnvil.this.getDisplay().getPlugin(), player);
        }

        @Override
        public ChatText getTitle() {
            return MapWidgetAnvil.this.getTitle();
        }

        @Override
        public void onTextChanged() {
            MapWidgetAnvil.this._text = this.getText();
            MapWidgetAnvil.this.LEFT_BUTTON._title = this.getText();
            MapWidgetAnvil.this.onTextChanged();
        }

        @Override
        public void onClick(InputDialogAnvil.Button button) {
            // Which widget button is this?
            MapWidgetAnvil.Button widgetButton = buttonFromAnvilDialog(this, button);
            if (widgetButton != null) {
                MapWidgetAnvil.this.onClick(widgetButton);
            }
        }

        @Override
        public void onClose() {
            MapWidgetAnvil.this._openDialogs.remove(this.getPlayer());

            // Also close the dialog for everyone else, fires onClose
            MapWidgetAnvil.this.closeAllDialogs();
        }
    }
}
