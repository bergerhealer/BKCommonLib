package com.bergerkiller.bukkit.common.block;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.org.bukkit.block.SignHandle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import java.util.function.Function;

/**
 * The side of a sign. Can be used as argument. Offers convenient methods to get
 * and set lines of text on a sign.
 */
public enum SignSide {
    /**
     * Front side of the sign. Supported on all versions of Minecraft.
     */
    FRONT(SignHandle::getFrontLines, SignHandle::getFrontLine, SignHandle::setFrontLine,
            SignChangeTracker::getFrontLines, SignChangeTracker::getFrontLine, SignChangeTracker::setFrontLine,
            true),
    /**
     * Back side of the sign. Only supported on Minecraft 1.20+.
     */
    BACK(SignHandle::getBackLines, SignHandle::getBackLine, SignHandle::setBackLine,
            SignChangeTracker::getBackLines, SignChangeTracker::getBackLine, SignChangeTracker::setBackLine,
            CommonCapabilities.HAS_SIGN_BACK_TEXT);

    static {
        FRONT.opposite = BACK;
        BACK.opposite = FRONT;
    }

    // Bukkit (Deprecated. Slow. Bad.)
    private final Function<SignHandle, String[]> bukkitAllLinesGetter;
    private final BukkitLineGetterFunc bukkitLineGetter;
    private final BukkitLineSetterFunc bukkitLineSetter;
    // BKCommonLib SignChangeTracker (Good. Fast. Awesome.)
    private final Function<SignChangeTracker, String[]> trackerAllLinesGetter;
    private final SignTrackerLineGetterFunc trackerLineGetter;
    private final SignTrackerLineSetterFunc trackerLineSetter;
    private final boolean supported;
    private SignSide opposite;

    SignSide(
            final Function<SignHandle, String[]> bukkitAllLinesGetter,
            final BukkitLineGetterFunc bukkitLineGetter,
            final BukkitLineSetterFunc bukkitLineSetter,
            final Function<SignChangeTracker, String[]> trackerAllLinesGetter,
            final SignTrackerLineGetterFunc trackerLineGetter,
            final SignTrackerLineSetterFunc trackerLineSetter,
            final boolean supported
    ) {
        this.bukkitAllLinesGetter = bukkitAllLinesGetter;
        this.bukkitLineGetter = bukkitLineGetter;
        this.bukkitLineSetter = bukkitLineSetter;
        this.trackerAllLinesGetter = trackerAllLinesGetter;
        this.trackerLineGetter = trackerLineGetter;
        this.trackerLineSetter = trackerLineSetter;
        this.supported = supported;
    }

    /**
     * Gets the opposite side
     *
     * @return opposite side
     */
    public SignSide opposite() {
        return opposite;
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
     * Gets whether this is the front side. Can be used in places where the side
     * of text is represented using a boolean.
     *
     * @return True if this side is the front side
     */
    public boolean isFront() {
        return this == FRONT;
    }

    /**
     * Gets the facing of text on a side. If this is the BACK side, returns the
     * opposite face.
     *
     * @param signBlock Sign Block
     * @return Facing
     */
    public BlockFace getFacing(Block signBlock) {
        BlockFace facing = BlockUtil.getFacing(signBlock);
        if (this == BACK) {
            facing = facing.getOppositeFace();
        }
        return facing;
    }

    /**
     * Gets the facing of text on a side. If this is the BACK side, returns the
     * opposite face.
     *
     * @param signBlockData Sign BlockData
     * @return Facing
     */
    public BlockFace getFacing(BlockData signBlockData) {
        BlockFace facing = signBlockData.getFacingDirection();
        if (this == BACK) {
            facing = facing.getOppositeFace();
        }
        return facing;
    }

    /**
     * Selects the FRONT-assigned value out of two values which are this side,
     * or the opposite of this side.
     *
     * @param selfSideValue Value assigned to this side
     * @param oppositeSideValue Value assigned to the {@link #opposite()} side
     * @return Front value
     * @param <T> Value Type
     */
    public <T> T selectFront(T selfSideValue, T oppositeSideValue) {
        return (this == FRONT) ? selfSideValue : oppositeSideValue;
    }

    /**
     * Selects the BACK-assigned value out of two values which are this side,
     * or the opposite of this side.
     *
     * @param selfSideValue Value assigned to this side
     * @param oppositeSideValue Value assigned to the {@link #opposite()} side
     * @return Back value
     * @param <T> Value Type
     */
    public <T> T selectBack(T selfSideValue, T oppositeSideValue) {
        return (this == BACK) ? selfSideValue : oppositeSideValue;
    }

    /**
     * Gets all the lines put on this side of the sign.
     * Returns an array of empty lines for the back side on Minecraft versions
     * below 1.20.
     *
     * @param signChangeTracker SignChangeTracker to get lines of
     * @return lines
     */
    public String[] getLines(SignChangeTracker signChangeTracker) {
        return trackerAllLinesGetter.apply(signChangeTracker);
    }

    /**
     * Gets a line put on this side of the sign.
     * Returns an empty line for the back side on Minecraft versions
     * below 1.20.
     *
     * @param signChangeTracker SignChangeTracker to get a line of
     * @return line
     */
    public String getLine(SignChangeTracker signChangeTracker, int index) {
        return trackerLineGetter.get(signChangeTracker, index);
    }

    /**
     * Sets a line on this side of the sign.
     * Does nothing for the back side on Minecraft versions
     * below 1.20.
     *
     * @param signChangeTracker SignChangeTracker to set a line of
     * @param text Line to put
     */
    public void setLine(SignChangeTracker signChangeTracker, int index, String text) {
        trackerLineSetter.set(signChangeTracker, index, text);
    }

    /**
     * Gets all the lines put on this side of the sign.
     * Returns an array of empty lines for the back side on Minecraft versions
     * below 1.20.
     *
     * @param sign Sign to get lines of
     * @return lines
     * @deprecated Bukkit Sign legacy lines are very slow. Use {@link SignChangeTracker} instead.
     */
    @Deprecated
    public String[] getLines(Sign sign) {
        return bukkitAllLinesGetter.apply(SignHandle.createHandle(sign));
    }

    /**
     * Gets a line put on this side of the sign.
     * Returns an empty line for the back side on Minecraft versions
     * below 1.20.
     *
     * @param sign Sign to get a line of
     * @return line
     * @deprecated Bukkit Sign legacy lines are very slow. Use {@link SignChangeTracker} instead.
     */
    @Deprecated
    public String getLine(Sign sign, int index) {
        return bukkitLineGetter.get(SignHandle.createHandle(sign), index);
    }

    /**
     * Sets a line on this side of the sign.
     * Does nothing for the back side on Minecraft versions
     * below 1.20.
     *
     * @param sign Sign to set a line of
     * @param text Line to put
     * @deprecated Bukkit Sign legacy lines are very slow. Use {@link SignChangeTracker} instead.
     */
    @Deprecated
    public void setLine(Sign sign, int index, String text) {
        bukkitLineSetter.set(SignHandle.createHandle(sign), index, text);
    }

    /**
     * Gets all the lines put on this side of the sign.
     * Returns an array of empty lines for the back side on Minecraft versions
     * below 1.20.
     *
     * @param sign Sign to get lines of
     * @return lines
     * @deprecated Bukkit Sign legacy lines are very slow. Use {@link SignChangeTracker} instead.
     */
    @Deprecated
    public String[] getLines(SignHandle sign) {
        return bukkitAllLinesGetter.apply(sign);
    }

    /**
     * Gets a line put on this side of the sign.
     * Returns an empty line for the back side on Minecraft versions
     * below 1.20.
     *
     * @param sign Sign to get a line of
     * @return line
     * @deprecated Bukkit Sign legacy lines are very slow. Use {@link SignChangeTracker} instead.
     */
    @Deprecated
    public String getLine(SignHandle sign, int index) {
        return bukkitLineGetter.get(sign, index);
    }

    /**
     * Sets a line on this side of the sign.
     * Does nothing for the back side on Minecraft versions
     * below 1.20.
     *
     * @param sign Sign to set a line of
     * @param text Line to put
     * @deprecated Bukkit Sign legacy lines are very slow. Use {@link SignChangeTracker} instead.
     */
    @Deprecated
    public void setLine(SignHandle sign, int index, String text) {
        bukkitLineSetter.set(sign, index, text);
    }

    /**
     * Gets the side that was changed by a Player that caused a sign change event.
     * Always returns FRONT on Minecraft versions &lt; 1.20.
     *
     * @param event SignChangeEvent
     * @return Side that was changed
     */
    public static SignSide sideChanged(SignChangeEvent event) {
        return SignHandle.isChangingFrontLines(event) ? FRONT : BACK;
    }

    /**
     * Gets either FRONT or BACK depending on the input boolean
     *
     * @param front True for FRONT, False for BACK
     * @return SignSide
     */
    public static SignSide byFront(boolean front) {
        return front ? FRONT : BACK;
    }

    @FunctionalInterface
    private interface SignTrackerLineGetterFunc {
        String get(SignChangeTracker tracker, int index);
    }

    @FunctionalInterface
    private interface SignTrackerLineSetterFunc {
        void set(SignChangeTracker tracker, int index, String text);
    }

    @FunctionalInterface
    private interface BukkitLineGetterFunc {
        String get(SignHandle sign, int index);
    }

    @FunctionalInterface
    private interface BukkitLineSetterFunc {
        void set(SignHandle sign, int index, String text);
    }
}
