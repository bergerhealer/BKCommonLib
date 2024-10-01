package com.bergerkiller.bukkit.common.block;

import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Accesses the lines of a single side of a sign
 */
public interface SignSideLineAccessor {
    /**
     * Gets the side of the sign that these lines are for
     *
     * @return Side of the sign
     */
    SignSide getSide();

    /**
     * Gets all (four) lines of text of this side of the sign
     *
     * @return Lines of text
     */
    String[] getLines();

    /**
     * Gets the text of a line on this side of a sign
     *
     * @param index Line index
     * @return Line text
     */
    String getLine(int index);

    /**
     * Changes the text of a line on this side of a sign
     *
     * @param index Line index
     * @param text New text
     */
    void setLine(int index, String text);

    /**
     * Gets all (four) formatted lines of text on this side of the sign
     *
     * @return Lines of formatted ChatText
     */
    ChatText[] getFormattedLines();

    /**
     * Gets the formatted text of a line on this side of the sign
     *
     * @param index Line index
     * @return Line formatted ChatText
     */
    ChatText getFormattedLine(int index);

    /**
     * Changes the formatted text on this side of the sign
     *
     * @param index Line index
     * @param text New formatted ChatText
     */
    void setFormattedLine(int index, ChatText text);

    /**
     * Creates a sign side line accessor for a Bukkit sign change event.
     * Supports special APIs on Paper servers for formatted text.
     *
     * @param event SignChangeEvent
     * @return SignSideLineAccessor
     */
    static SignSideLineAccessor ofChangeEvent(SignChangeEvent event) {
        final SignSide side = SignSide.byFront(BlockUtil.isChangingFrontLines(event));
        //TODO: Formatted text on paper server! Adventure!
        return new SignSideLineAccessor() {
            @Override
            public SignSide getSide() {
                return side;
            }

            @Override
            public String[] getLines() {
                return event.getLines();
            }

            @Override
            public String getLine(int index) {
                return event.getLine(index);
            }

            @Override
            public void setLine(int index, String text) {
                event.setLine(index, text);
            }

            @Override
            public ChatText[] getFormattedLines() {
                return LogicUtil.mapArray(event.getLines(), ChatText.class, ChatText::fromMessage);
            }

            @Override
            public ChatText getFormattedLine(int index) {
                return ChatText.fromMessage(event.getLine(index));
            }

            @Override
            public void setFormattedLine(int index, ChatText text) {
                event.setLine(index, text.getMessage());
            }
        };
    }
}
