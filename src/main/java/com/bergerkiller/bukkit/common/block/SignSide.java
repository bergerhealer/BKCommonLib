package com.bergerkiller.bukkit.common.block;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.generated.org.bukkit.block.SignHandle;
import org.bukkit.block.Sign;

import java.util.function.Function;

/**
 * The side of a sign. Can be used as argument. Offers convenient methods to get
 * and set lines of text on a sign.
 */
public enum SignSide {
    /**
     * Front side of the sign. Supported on all versions of Minecraft.
     */
    FRONT(SignHandle::getFrontLines, SignHandle::getFrontLine, SignHandle::setFrontLine, true),
    /**
     * Back side of the sign. Only supported on Minecraft 1.20+.
     */
    BACK(SignHandle::getBackLines, SignHandle::getBackLine, SignHandle::setBackLine, CommonCapabilities.HAS_SIGN_BACK_TEXT);

    private final Function<SignHandle, String[]> allLinesGetter;
    private final LineGetterFunc lineGetter;
    private final LineSetterFunc lineSetter;
    private final boolean supported;

    SignSide(Function<SignHandle, String[]> allLinesGetter,
             LineGetterFunc lineGetter,
             LineSetterFunc lineSetter,
             boolean supported
    ) {
        this.allLinesGetter = allLinesGetter;
        this.lineGetter = lineGetter;
        this.lineSetter = lineSetter;
        this.supported = supported;
    }

    /**
     * Gets whether this side is supported on the current version of the server
     *
     * @return True if supported
     */
    public boolean isSupported() {
        return supported;
    }

    /**
     * Gets all the lines put on this side of the sign.
     * Returns an array of empty lines for the back side on Minecraft versions
     * below 1.20.
     *
     * @param sign Sign to get lines of
     * @return lines
     */
    public String[] getLines(Sign sign) {
        return allLinesGetter.apply(SignHandle.createHandle(sign));
    }

    /**
     * Gets a line put on this side of the sign.
     * Returns an empty line for the back side on Minecraft versions
     * below 1.20.
     *
     * @param sign Sign to get a line of
     * @return line
     */
    public String getLine(Sign sign, int index) {
        return lineGetter.get(SignHandle.createHandle(sign), index);
    }

    /**
     * Sets a line on this side of the sign.
     * Does nothing for the back side on Minecraft versions
     * below 1.20.
     *
     * @param sign Sign to set a line of
     * @param text Line to put
     */
    public void setLine(Sign sign, int index, String text) {
        lineSetter.set(SignHandle.createHandle(sign), index, text);
    }

    /**
     * Gets all the lines put on this side of the sign.
     * Returns an array of empty lines for the back side on Minecraft versions
     * below 1.20.
     *
     * @param sign Sign to get lines of
     * @return lines
     */
    public String[] getLines(SignHandle sign) {
        return allLinesGetter.apply(sign);
    }

    /**
     * Gets a line put on this side of the sign.
     * Returns an empty line for the back side on Minecraft versions
     * below 1.20.
     *
     * @param sign Sign to get a line of
     * @return line
     */
    public String getLine(SignHandle sign, int index) {
        return lineGetter.get(sign, index);
    }

    /**
     * Sets a line on this side of the sign.
     * Does nothing for the back side on Minecraft versions
     * below 1.20.
     *
     * @param sign Sign to set a line of
     * @param text Line to put
     */
    public void setLine(SignHandle sign, int index, String text) {
        lineSetter.set(sign, index, text);
    }

    @FunctionalInterface
    private interface LineGetterFunc {
        String get(SignHandle sign, int index);
    }

    @FunctionalInterface
    private interface LineSetterFunc {
        void set(SignHandle sign, int index, String text);
    }
}
