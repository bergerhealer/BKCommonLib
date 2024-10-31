package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

import java.util.Collections;
import java.util.Set;

/**
 * Controls what coordinates in a position update are relative or absolute.
 * A relative flag will add the coordinate to the existing coordinate on the
 * client side, an absolute flag sets the coordinates to this new value.<br>
 * <br>
 * This is used to shift a player's position without having to know the current
 * position of the player.
 */
public final class RelativeFlags {
    private static final int FLAG_X = 1;
    private static final int FLAG_Y = (1 << 1);
    private static final int FLAG_Z = (1 << 2);
    private static final int FLAG_YAW = (1 << 3);
    private static final int FLAG_PITCH = (1 << 4);
    private static final int FLAG_DELTA_X = (1 << 5);
    private static final int FLAG_DELTA_Y = (1 << 6);
    private static final int FLAG_DELTA_Z = (1 << 7);
    private static final int FLAG_DELTA_ROTATION = (1 << 8);
    private static final RelativeFlags[] cache = new RelativeFlags[1 << 9];

    // Used for converting between these RelativeFlags and internally-used Set of movement relative flags
    public static final FastMethod<Set<?>> unpackMethod = new FastMethod<>(m -> {
        Class<?> relativeMovementType = CommonUtil.getClass("net.minecraft.world.entity.RelativeMovement");
        if (CommonBootstrap.evaluateMCVersion(">=", "1.18")) {
            m.init(relativeMovementType.getDeclaredMethod("unpack", int.class));
        } else {
            m.init(relativeMovementType.getDeclaredMethod("a", int.class));
        }
    });
    public static final FastMethod<Integer> packMethod = new FastMethod<>(m -> {
        Class<?> relativeMovementType = CommonUtil.getClass("net.minecraft.world.entity.RelativeMovement");
        if (CommonBootstrap.evaluateMCVersion(">=", "1.18")) {
            m.init(relativeMovementType.getDeclaredMethod("pack", Set.class));
        } else {
            m.init(relativeMovementType.getDeclaredMethod("a", Set.class));
        }
    });

    // Frequently used constants
    public static final RelativeFlags ABSOLUTE_POSITION = fromFlags(0);
    public static final RelativeFlags RELATIVE_ROTATION = fromFlags(FLAG_YAW | FLAG_PITCH);
    public static final RelativeFlags RELATIVE_POSITION_ROTATION = fromFlags(FLAG_X | FLAG_Y | FLAG_Z | FLAG_YAW | FLAG_PITCH);

    private final int flags;
    private final Set<?> relativeFlags;

    private RelativeFlags(int flags) {
        this.flags = flags;
        this.relativeFlags = Collections.unmodifiableSet(unpackMethod.invoke(flags));
    }

    public boolean isRelativeX() {
        return (flags & FLAG_X) != 0;
    }

    public boolean isRelativeY() {
        return (flags & FLAG_Y) != 0;
    }

    public boolean isRelativeZ() {
        return (flags & FLAG_Z) != 0;
    }

    public boolean isRelativeYaw() {
        return (flags & FLAG_YAW) != 0;
    }

    public boolean isRelativePitch() {
        return (flags & FLAG_PITCH) != 0;
    }

    public boolean isRelativeDeltaX() {
        return (flags & FLAG_DELTA_X) != 0;
    }

    public boolean isRelativeDeltaY() {
        return (flags & FLAG_DELTA_Y) != 0;
    }

    public boolean isRelativeDeltaZ() {
        return (flags & FLAG_DELTA_Z) != 0;
    }

    public boolean isRelativeDeltaRotation() {
        return (flags & FLAG_DELTA_ROTATION) != 0;
    }

    public RelativeFlags withRelativeX() {
        return fromFlags(flags | FLAG_X);
    }

    public RelativeFlags withAbsoluteX() {
        return fromFlags(flags & ~FLAG_X);
    }

    public RelativeFlags withRelativeY() {
        return fromFlags(flags | FLAG_Y);
    }

    public RelativeFlags withAbsoluteY() {
        return fromFlags(flags & ~FLAG_Y);
    }

    public RelativeFlags withRelativeZ() {
        return fromFlags(flags | FLAG_Z);
    }

    public RelativeFlags withAbsoluteZ() {
        return fromFlags(flags & ~FLAG_Z);
    }

    public RelativeFlags withRelativeDeltaX() {
        return fromFlags(flags | FLAG_DELTA_X);
    }

    public RelativeFlags withAbsoluteDeltaX() {
        return fromFlags(flags & ~FLAG_DELTA_X);
    }

    public RelativeFlags withRelativeDeltaY() {
        return fromFlags(flags | FLAG_DELTA_Y);
    }

    public RelativeFlags withAbsoluteDeltaY() {
        return fromFlags(flags & ~FLAG_DELTA_Y);
    }

    public RelativeFlags withRelativeDeltaZ() {
        return fromFlags(flags | FLAG_DELTA_Z);
    }

    public RelativeFlags withAbsoluteDeltaZ() {
        return fromFlags(flags & ~FLAG_DELTA_Z);
    }

    public RelativeFlags withRelativeDeltaRotation() {
        return fromFlags(flags | FLAG_DELTA_ROTATION);
    }

    public RelativeFlags withAbsoluteDeltaRotation() {
        return fromFlags(flags & ~FLAG_DELTA_ROTATION);
    }

    public RelativeFlags withRelativePosition() {
        return fromFlags(flags | (FLAG_X | FLAG_Y | FLAG_Z));
    }

    public RelativeFlags withAbsolutePosition() {
        return fromFlags(flags & ~(FLAG_X | FLAG_Y | FLAG_Z));
    }

    public RelativeFlags withRelativeRotation() {
        return fromFlags(flags | (FLAG_YAW | FLAG_PITCH));
    }

    public RelativeFlags withAbsoluteRotation() {
        return fromFlags(flags & ~(FLAG_YAW | FLAG_PITCH));
    }

    public RelativeFlags withRelativeDelta() {
        return fromFlags(flags | (FLAG_DELTA_X | FLAG_DELTA_Y | FLAG_DELTA_Z | FLAG_DELTA_ROTATION));
    }

    public RelativeFlags withAbsoluteDelta() {
        return fromFlags(flags & ~(FLAG_DELTA_X | FLAG_DELTA_Y | FLAG_DELTA_Z | FLAG_DELTA_ROTATION));
    }

    @Override
    public int hashCode() {
        return flags;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("RelativeFlags{flags=").append(flags).append(", relative=[");
        if (isRelativeX())
            str.append(" X");
        if (isRelativeY())
            str.append(" Y");
        if (isRelativeZ())
            str.append(" Z");
        if (isRelativeYaw())
            str.append(" Yaw");
        if (isRelativePitch())
            str.append(" Pitch");
        if (isRelativeDeltaX())
            str.append(" Delta-X");
        if (isRelativeDeltaY())
            str.append(" Delta-Y");
        if (isRelativeDeltaZ())
            str.append(" Delta-Z");
        if (isRelativeDeltaRotation())
            str.append(" Delta-Rotation");
        str.append(" ]}");
        return str.toString();
    }

    /**
     * Decodes the Set of the flags used internally by the server into RelativeFlags.
     *
     * @param rawRelativeFlags Raw relative flags for use internally
     * @return RelativeFlags value
     */
    @ConverterMethod(input="java.util.Set<net.minecraft.world.entity.RelativeMovement>")
    public static RelativeFlags fromRawRelativeFlags(Set<?> rawRelativeFlags) {
        return fromFlags(packMethod.invoke(rawRelativeFlags));
    }

    /**
     * Converts relative flags to the flags as used internally by the server. This returned set
     * is read-only.
     *
     * @param flags RelativeFlags
     * @return Set of raw flags
     */
    @ConverterMethod(output="java.util.Set<net.minecraft.world.entity.RelativeMovement>")
    public static Set<?> toRawRelativeFlags(RelativeFlags flags) {
        return flags.relativeFlags;
    }

    private static RelativeFlags fromFlags(int flags) {
        RelativeFlags cachedFlags = cache[flags];
        if (cachedFlags == null) {
            synchronized (cache) {
                cachedFlags = cache[flags];
                if (cachedFlags == null) {
                    cache[flags] = cachedFlags = new RelativeFlags(flags);
                }
            }
        }

        return cachedFlags;
    }
}
