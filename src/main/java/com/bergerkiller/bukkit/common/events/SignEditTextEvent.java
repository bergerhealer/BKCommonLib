package com.bergerkiller.bukkit.common.events;

import com.bergerkiller.bukkit.common.block.SignLineAccessor;
import com.bergerkiller.bukkit.common.block.SignSide;
import com.bergerkiller.bukkit.common.block.SignSideLineAccessor;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired when a Player changes text of a sign. This fires when:
 * <ul>
 *     <li>Placing down a new sign, with side FRONT being edited</li>
 *     <li>Right-clicking a sign with a sign to edit that side's text</li>
 *     <li>Placing down a pre-filled sign (with nbt) using an item produced with ctrl + middle-mouse click</li>
 * </ul>
 * If this event is {@link #setCancelled(boolean) cancelled} then the action by the
 * player is rolled back. In the case of placing down a new sign, the text is wiped.
 * In the case of placing down a pre-filled sign, the placement is cancelled.
 * In the case of editing a sign's text, the old text is restored.<br>
 * <br>
 * When placing down a pre-filled sign, the event is always fired for the FRONT side.
 * If the BACK side also contains text, the event is fired again for that side as well.<br>
 * <br>
 * If a Bukkit event such as the SignChangeEvent gets cancelled by another plugin, then
 * this event is not fired at all. This event is proxied at level HIGH.
 */
public class SignEditTextEvent extends BlockEvent implements Cancellable, SignSideLineAccessor {
    private static final HandlerList handlers = new HandlerList();
    private final Sign sign;
    private final Player player;
    private final EditReason editReason;
    private final SignSide signSide;
    private final SignLineAccessor lineAccessor;
    private boolean cancelled = false;

    public SignEditTextEvent(
            final @NotNull Player player,
            final @NotNull Block signBlock,
            final @NotNull Sign sign,
            final @NonNull EditReason editReason,
            final @NotNull SignSide signSide,
            final @NonNull SignLineAccessor lineAccessor
    ) {
        super(signBlock);
        this.sign = sign;
        this.player = player;
        this.editReason = editReason;
        this.signSide = signSide;
        this.lineAccessor = lineAccessor;
    }

    /**
     * Gets the Bukkit Sign this event is for. This API should be avoided, as it is not
     * cross-version compatible.
     *
     * @return Bukkit Sign BlockState
     */
    public Sign getSign() {
        return sign;
    }

    /**
     * Returns the player that built the sign (side)
     *
     * @return Player who built the sign (side)
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the side of the sign that was changed or filled in by the Player
     *
     * @return Side of the sign
     */
    @Override
    public SignSide getSide() {
        return signSide;
    }

    /**
     * Gets the reason this sign edit event was fired
     *
     * @return Edit Reason
     */
    public EditReason getEditReason() {
        return editReason;
    }

    /**
     * Gets reads and write access to the text on both sides of the sign.
     * The side currently being changed by the player is specially handled.
     * The object cannot be used outside of the handling of this event.
     *
     * @return SignLineAccessor
     */
    public SignLineAccessor getAllSideLines() {
        return lineAccessor;
    }

    /**
     * Gets all (four) lines of text of this sign of the side that is being changed
     * by the player.
     *
     * @return Lines of text
     */
    @Override
    public String[] getLines() {
        return lineAccessor.getLines(signSide);
    }

    /**
     * Gets the text of a line on the sign of the side that is being changed
     * by the player.
     *
     * @param index Line index
     * @return Line text
     */
    @Override
    public String getLine(int index) {
        return lineAccessor.getLine(signSide, index);
    }

    /**
     * Changes the text on the sign for the side that is being changed by
     * the player.
     *
     * @param index Line index
     * @param text New text
     */
    @Override
    public void setLine(int index, String text) {
        lineAccessor.setLine(signSide, index, text);
    }

    /**
     * Gets all (four) formatted lines of text of this sign of the side that is being changed
     * by the player.
     *
     * @return Lines of formatted ChatText
     */
    @Override
    public ChatText[] getFormattedLines() {
        return signSide.isFront() ? lineAccessor.getFormattedFrontLines()
                                  : lineAccessor.getFormattedBackLines();
    }

    /**
     * Gets the formatted text of a line on the sign of the side that is being changed
     * by the player.
     *
     * @param index Line index
     * @return Line formatted ChatText
     */
    @Override
    public ChatText getFormattedLine(int index) {
        return signSide.isFront() ? lineAccessor.getFormattedFrontLine(index)
                                  : lineAccessor.getFormattedBackLine(index);
    }

    /**
     * Changes the formatted text on the sign for the side that is being changed by
     * the player.
     *
     * @param index Line index
     * @param text New formatted ChatText
     */
    @Override
    public void setFormattedLine(int index, ChatText text) {
        if (signSide.isFront()) {
            lineAccessor.setFormattedFrontLine(index, text);
        } else {
            lineAccessor.setFormattedBackLine(index, text);
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Reason for editing the text of a sign
     */
    public enum EditReason {
        /** Player placed a new sign using a default sign item */
        PLACE,
        /** Player edits one side of the sign by right-clicking it */
        EDIT,
        /** Player control-middle-mouse picked a sign and placed this sign, with lines already filled */
        CTRL_PICK_PLACE
    }
}
