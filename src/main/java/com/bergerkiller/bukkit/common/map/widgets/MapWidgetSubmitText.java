package com.bergerkiller.bukkit.common.map.widgets;

import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * Implementation of {@link MapWidgetAnvil} geared towards allowing
 * the player to input text. To display the input, activate the widget.
 * When the player submits text, {@link #onAccept(text)} is called.
 * When the player cancels, {@link #onCancel()} is called.
 */
public class MapWidgetSubmitText extends MapWidgetAnvil {
    private boolean _accepted = false;

    public MapWidgetSubmitText() {
        LEFT_BUTTON.setMaterial(Material.BARRIER);
        LEFT_BUTTON.setDescription(ChatColor.RED + "Cancel");
        RIGHT_BUTTON.setMaterial(Material.EMERALD);
        RIGHT_BUTTON.setDescription(ChatColor.GREEN + "Accept");
    }

    /**
     * Called when the player clicks on the accept button,
     * confirming his choice of text
     * 
     * @param text accepted
     */
    public void onAccept(String text) {
    }

    /**
     * Called when the player closes the window, without pressing
     * the accept button.
     */
    public void onCancel() {
    }

    /**
     * Sets the description displayed to the user, explaining
     * what this dialog is about.
     * 
     * @param text
     */
    public MapWidgetSubmitText setDescription(String text) {
        if (text == null || text.isEmpty()) {
            MIDDLE_BUTTON.setMaterial(null);
        } else {
            MIDDLE_BUTTON.setMaterial(Material.PAPER);
            MIDDLE_BUTTON.setTitle(ChatColor.YELLOW + "About");
            MIDDLE_BUTTON.setDescription(text);
        }
        return this;
    }

    @Override
    public void onClick(Button button) {
        if (button == RIGHT_BUTTON) {
            if (this.getText() != null && !this.getText().isEmpty()) {
                this._accepted = true;
                this.deactivate();
            }
        } else if (button == LEFT_BUTTON) {
            this._accepted = false;
            this.deactivate();
        }
    }

    @Override
    public void onClose() {
        if (this._accepted) {
            this.onAccept(this.getText());
        } else {
            this.onCancel();
        }
    }

    @Override
    public void onActivate() {
        super.onActivate();
        this._accepted = false;
    }
}
