package com.bergerkiller.bukkit.common.block;

import com.bergerkiller.bukkit.common.wrappers.ChatText;

/**
 * An object that represents accessing the front and back lines of a Sign.
 * Both formatted and legacy String message formatted text can be get and set.
 */
public interface SignLineAccessor {

    /**
     * Gets a line at one side of the sign
     *
     * @param index Line index
     * @return Line at this index
     */
    String getLine(SignSide side, int index);

    /**
     * Sets a line of one side of the sign, updating the sign in the world.
     *
     * @param index Line index
     * @param text New text to put at this index
     */
    void setLine(SignSide side, int index, String text);

    /**
     * Gets all the lines put at a side of the sign. 4 lines.
     *
     * @return Sign lines
     */
    String[] getLines(SignSide side);

    /**
     * Gets a line of the front of the sign
     *
     * @param index Line index
     * @return Line at this index at the front of the sign
     */
    String getFrontLine(int index);

    /**
     * Sets a line of the front of the sign, updating the sign in the world.
     *
     * @param index Line index
     * @param text New text to put at this index at the front of the sign
     */
    void setFrontLine(int index, String text);

    /**
     * Gets all the lines put at the front of the sign. 4 lines.
     *
     * @return Sign front lines
     */
    String[] getFrontLines();

    /**
     * Gets a line as a formatted component of the front of the sign.
     *
     * @param index Line index
     * @return Line at this index at the front of the sign
     */
    ChatText getFormattedFrontLine(int index);

    /**
     * Sets a line as a formatted component of the front of the sign, updating the sign in the world.
     *
     * @param index Line index
     * @param text New text to put at this index at the front of the sign
     */
    void setFormattedFrontLine(int index, ChatText text);

    /**
     * Gets all the lines put at the front of the sign as formatted components. 4 lines.
     *
     * @return Sign front lines
     */
    ChatText[] getFormattedFrontLines();

    /**
     * Gets a line of the back of the sign.
     * Always returns an empty String on Minecraft versions below 1.20.
     *
     * @param index Line index
     * @return Line at this index at the back of the sign
     */
    String getBackLine(int index);

    /**
     * Sets a line of the back of the sign, updating the sign in the world.
     * Does nothing on Minecraft versions below 1.20.
     *
     * @param index Line index
     * @param text New text to put at this index at the back of the sign
     */
    void setBackLine(int index, String text);

    /**
     * Gets all the lines put at the back of the sign. 4 lines.
     * Always returns an array of empty Strings on Minecraft versions below 1.20.
     *
     * @return Sign front lines
     */
    String[] getBackLines();

    /**
     * Gets a line as a formatted component of the back of the sign.
     * Always returns an empty String on Minecraft versions below 1.20.
     *
     * @param index Line index
     * @return Line at this index at the back of the sign
     */
    ChatText getFormattedBackLine(int index);

    /**
     * Sets a line as a formatted component of the back of the sign, updating the sign in the world.
     * Does nothing on Minecraft versions below 1.20.
     *
     * @param index Line index
     * @param text New text to put at this index at the back of the sign
     */
    void setFormattedBackLine(int index, ChatText text);

    /**
     * Gets all the lines put at the back of the sign as formatted components. 4 lines.
     * Always returns an array of empty Strings on Minecraft versions below 1.20.
     *
     * @return Sign front lines
     */
    ChatText[] getFormattedBackLines();

    /**
     * Gets an accessor of the lines of a single side of the sign
     *
     * @param side SignSide
     * @return SignSideLineAccessor for the specified side
     */
    default SignSideLineAccessor accessLinesOfSide(final SignSide side) {
        return new SignSideLineAccessor() {
            @Override
            public SignSide getSide() {
                return side;
            }

            @Override
            public String[] getLines() {
                return SignLineAccessor.this.getLines(side);
            }

            @Override
            public String getLine(int index) {
                return SignLineAccessor.this.getLine(side, index);
            }

            @Override
            public void setLine(int index, String text) {
                SignLineAccessor.this.setLine(side, index, text);
            }

            @Override
            public ChatText[] getFormattedLines() {
                return side.isFront() ? SignLineAccessor.this.getFormattedFrontLines()
                                      : SignLineAccessor.this.getFormattedBackLines();
            }

            @Override
            public ChatText getFormattedLine(int index) {
                return side.isFront() ? SignLineAccessor.this.getFormattedFrontLine(index)
                                      : SignLineAccessor.this.getFormattedBackLine(index);
            }

            @Override
            public void setFormattedLine(int index, ChatText text) {
                if (side.isFront()) {
                    SignLineAccessor.this.setFormattedFrontLine(index, text);
                } else {
                    SignLineAccessor.this.setFormattedBackLine(index, text);
                }
            }
        };
    }

    /**
     * Composes a SignLineAccessor using separate front and back lines
     *
     * @param frontLines Front lines SignSideLineAccessor
     * @param backLines Back lines SignSideLineAccessor
     * @return SignLineAccessor
     */
    static SignLineAccessor compose(final SignSideLineAccessor frontLines, final SignSideLineAccessor backLines) {
        return new SignLineAccessor() {
            @Override
            public String getLine(SignSide side, int index) {
                return side.selectFront(frontLines, backLines).getLine(index);
            }

            @Override
            public void setLine(SignSide side, int index, String text) {
                side.selectFront(frontLines, backLines).setLine(index, text);
            }

            @Override
            public String[] getLines(SignSide side) {
                return side.selectFront(frontLines, backLines).getLines();
            }

            @Override
            public String getFrontLine(int index) {
                return frontLines.getLine(index);
            }

            @Override
            public void setFrontLine(int index, String text) {
                frontLines.setLine(index, text);
            }

            @Override
            public String[] getFrontLines() {
                return frontLines.getLines();
            }

            @Override
            public ChatText getFormattedFrontLine(int index) {
                return frontLines.getFormattedLine(index);
            }

            @Override
            public void setFormattedFrontLine(int index, ChatText text) {
                frontLines.setFormattedLine(index, text);
            }

            @Override
            public ChatText[] getFormattedFrontLines() {
                return frontLines.getFormattedLines();
            }

            @Override
            public String getBackLine(int index) {
                return backLines.getLine(index);
            }

            @Override
            public void setBackLine(int index, String text) {
                backLines.setLine(index, text);
            }

            @Override
            public String[] getBackLines() {
                return backLines.getLines();
            }

            @Override
            public ChatText getFormattedBackLine(int index) {
                return backLines.getFormattedLine(index);
            }

            @Override
            public void setFormattedBackLine(int index, ChatText text) {
                backLines.setFormattedLine(index, text);
            }

            @Override
            public ChatText[] getFormattedBackLines() {
                return backLines.getFormattedLines();
            }
        };
    }
}
