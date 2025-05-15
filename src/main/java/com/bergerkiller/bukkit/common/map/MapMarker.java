package com.bergerkiller.bukkit.common.map;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.bergerkiller.bukkit.common.map.markers.MapDisplayMarkers;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.world.level.saveddata.maps.MapDecorationTypeHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import org.jetbrains.annotations.Debug;

/**
 * A marker icon displayed on the {@link MapDisplay}
 */
public final class MapMarker {
    private final MapDisplayMarkers owner;
    private final String id;
    private Type type;
    private double x, y;
    private double rotation;
    private boolean visible;
    private ChatText caption;

    MapMarker(MapDisplayMarkers owner, String id) {
        this.owner = owner;
        this.id = id;
        this.type = Type.RED_MARKER;
        this.x = 0;
        this.y = 0;
        this.rotation = 0.0;
        this.visible = true;
        this.caption = null;
    }

    /**
     * Gets the unique String identifier of this Map Marker.
     * This identifier can be used to retrieve this Map Marker instance
     * from the map display it was added to.
     *
     * @return id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Removes this marker from the display
     *
     * @return True if this marker was found and removed, False
     * if the marker was already removed
     */
    public boolean remove() {
        return this.owner.remove(this);
    }

    /**
     * Gets the type of marker displayed on the map display
     *
     * @return marker type
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Sets the type of marker displayed on the map display
     *
     * @param type The type of marker to set to
     * @return this
     */
    public MapMarker setType(Type type) {
        if (type == null) {
            throw new IllegalArgumentException("Type can not be null");
        } else if (this.type != type) {
            this.type = type;
            this.owner.update(this);
        }
        return this;
    }

    /**
     * Gets the horizontal X-coordinate of the position of this
     * map marker.
     *
     * @return Position X-coordinate
     */
    public double getPositionX() {
        return this.x;
    }

    /**
     * Gets the vertical Y-coordinate of the position of this
     * map marker.
     *
     * @return Position Y-coordinate
     */
    public double getPositionY() {
        return this.y;
    }

    /**
     * Sets the horizontal X-coordinate of the position of this
     * map marker.
     *
     * @param x X-Coordinate to set to
     * @return this
     */
    public MapMarker setPositionX(double x) {
        return setPosition(x, this.y);
    }

    /**
     * Sets the vertical Y-coordinate of the position of this
     * map marker.
     *
     * @param y Y-Coordinate to set to
     * @return this
     */
    public MapMarker setPositionY(double y) {
        return setPosition(this.x, y);
    }

    /**
     * Sets the position of this marker on the display at the x and y
     * pixel coordinates specified.
     *
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @return this
     */
    public MapMarker setPosition(double x, double y) {
        if (this.x != x || this.y != y) {
            this.owner.move(this, x, y);
            this.x = x;
            this.y = y;
        }
        return this;
    }

    /**
     * Gets the rotation angle of the icon in degrees
     *
     * @return rotation angle
     */
    public double getRotation() {
        return this.rotation;
    }

    /**
     * Sets the rotation angle of the icon in degrees
     *
     * @param rotation angle
     * @return this
     */
    public MapMarker setRotation(double rotation) {
        boolean changed = isRotationChanged(this.rotation, rotation);
        this.rotation = rotation;
        if (changed) {
            this.owner.update(this);
        }
        return this;
    }

    /**
     * Gets whether this map marker is currently visible
     *
     * @return True if visible, False if not
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Sets whether this marker is visible. When false, the marker
     * is removed from the map, but can be re-added cleanly from here,
     * with all the original properties left intact.
     *
     * @param visible True to make visible, False to keep hidden
     * @return this
     */
    public MapMarker setVisible(boolean visible) {
        this.owner.updateVisible(this, visible);
        this.visible = visible;
        return this;
    }

    /**
     * Gets the message caption displayed on the map where this marker is located.
     * Returns null when the caption is hidden.
     *
     * @return caption text message, null when hidden
     */
    public String getCaption() {
        return (this.caption == null) ? null : this.caption.getMessage();
    }

    /**
     * Sets the message caption displayed on the map where this marker is located.
     * Supports standard Bukkit chat formatting characters.
     * For more advanced formats, use {@link #setFormattedCaption(ChatText)}.
     *
     * @param caption Text message caption, use null to hide the caption
     * @return this
     */
    public MapMarker setCaption(String caption) {
        if (this.caption == null) {
            if (caption != null) {
                this.caption = ChatText.fromMessage(caption);
                this.owner.update(this);
            }
        } else if (caption == null) {
            this.caption = null;
            this.owner.update(this);
        } else if (!this.caption.getMessage().equals(caption)) {
            this.caption.setMessage(caption);
            this.owner.update(this);
        }
        return this;
    }

    /**
     * Sets the caption displayed on the map where this marker is located/
     * Returns null when the caption is hidden.<br>
     * <br>
     * <b>Note: </b>the returned ChatText may later change when setCaption() or
     * setFormattedCaption() are called! If you plan to store it, make a clone.
     *
     * @return caption formatted text, null when hidden
     */
    public ChatText getFormattedCaption() {
        return this.caption;
    }

    /**
     * Sets the caption displayed on the map where this marker is located.<br>
     * <br>
     * <b>Note: </b>later changes to the input caption will not change the caption of this
     * map marker. The input ChatText is copied.
     *
     * @param caption Formatted ChatText caption, use null to hide the caption
     * @return this
     */
    public MapMarker setFormattedCaption(ChatText caption) {
        if (this.caption == null) {
            if (caption != null) {
                this.caption = caption.clone();
                this.owner.update(this);
            }
        } else if (caption == null) {
            this.caption = null;
            this.owner.update(this);
        } else if (!this.caption.equals(caption)) {
            this.caption.copy(caption);
            this.owner.update(this);
        }
        return this;
    }

    private static boolean isRotationChanged(double oldAngle, double newAngle) {
        return oldAngle != newAngle; //TODO: Check using 16 rotation steps!
    }

    /**
     * A type of color a Map Marker {@link Type} is displayed as.
     * WHITE, GREEN, RED and BLUE are used for pointers,
     * and all colors are used for banner icons.<br>
     * <br>
     * This enum is for compatibility with older versions of Bukkit
     * where some DyeColor constants had different names.
     */
    public static enum Color {
        WHITE, ORANGE, MAGENTA, LIGHT_BLUE,
        YELLOW, LIME, PINK, GRAY,
        LIGHT_GRAY, CYAN, PURPLE, BLUE,
        BROWN, GREEN, RED, BLACK;

        private final org.bukkit.DyeColor _dyeColor;
        private static final Color[] _cached_values = values();

        private Color() {
            this._dyeColor = org.bukkit.DyeColor.values()[this.ordinal()];
        }

        /**
         * Gets the Bukkit Color RGB this Color constant represents
         *
         * @return color
         */
        public org.bukkit.Color toColor() {
            return this._dyeColor.getColor();
        }

        /**
         * Gets the Bukkit DyeColor matching this Color constant
         *
         * @return dye color
         */
        public org.bukkit.DyeColor toDyeColor() {
            return this._dyeColor;
        }

        /**
         * Gets the Color enum constant by Bukkit Dye Color.
         * If input is null, null is returned.
         *
         * @param dyeColor
         * @return Color
         */
        public static Color byDyeColor(org.bukkit.DyeColor dyeColor) {
            return dyeColor == null ? null : _cached_values[dyeColor.ordinal()];
        }

        /**
         * Gets the Color enum constant by the ordinal value.
         * The ordinal value wraps around when exceeding the maximum number
         * of elements, allowing for counter color wheel functionality.
         *
         * @param ordinal
         * @return values[ordinal % count]
         */
        public static Color byOrdinal(int ordinal) {
            return _cached_values[ordinal & 0xf];
        }
    }

    // We give a fancy display name and color to marker types. This performs the mapping of the registry name to this one.
    private static class TypeInfo {
        private static final Map<MinecraftKeyHandle, TypeInfo> byKey = new HashMap<>();
        public final MinecraftKeyHandle key;
        public final String name;
        public final String displayName;
        public final Color color;

        static {
            register("player", "WHITE_POINTER", "pointer (white)", Color.WHITE);
            register("frame", "GREEN_POINTER", "pointer (green)", Color.GREEN);
            register("red_marker", "RED_POINTER", "pointer (red)", Color.RED);
            register("blue_marker", "BLUE_POINTER", "pointer (blue)", Color.BLUE);
            register("target_x", "WHITE_CROSS", "cross (white)", Color.WHITE);
            register("target_point", "RED_MARKER", "marker (red)", Color.RED);
            register("player_off_map", "WHITE_CIRCLE", "ball (white)", Color.WHITE);

            // Since Minecraft 1.11
            register("player_off_limits", "SMALL_WHITE_CIRCLE", "dot (white)", Color.WHITE);
            register("mansion", "MANSION", "mansion", Color.CYAN);
            register("monument", "TEMPLE", "temple", Color.BROWN);

            // Since Minecraft 1.13
            register("banner_white",  "BANNER_WHITE", "banner (white)", Color.WHITE);
            register("banner_orange", "BANNER_ORANGE", "banner (orange)", Color.ORANGE);
            register("banner_magenta", "BANNER_MAGENTA", "banner (magenta)", Color.MAGENTA);
            register("banner_light_blue", "BANNER_LIGHT_BLUE", "banner (light blue)", Color.LIGHT_BLUE);
            register("banner_yellow", "BANNER_YELLOW", "banner (yellow)", Color.YELLOW);
            register("banner_lime", "BANNER_LIME", "banner (lime)", Color.LIME);
            register("banner_pink", "BANNER_PINK", "banner (pink)", Color.PINK);
            register("banner_gray", "BANNER_GRAY", "banner (gray)", Color.GRAY);
            register("banner_light_gray", "BANNER_LIGHT_GRAY", "banner (light gray)", Color.LIGHT_GRAY);
            register("banner_cyan", "BANNER_CYAN", "banner (cyan)", Color.CYAN);
            register("banner_purple", "BANNER_PURPLE", "banner (purple)", Color.PURPLE);
            register("banner_blue", "BANNER_BLUE", "banner (blue)", Color.BLUE);
            register("banner_brown", "BANNER_BROWN", "banner (brown)", Color.BROWN);
            register("banner_green", "BANNER_GREEN", "banner (green)", Color.GREEN);
            register("banner_red", "BANNER_RED", "banner (red)", Color.RED);
            register("banner_black", "BANNER_BLACK", "banner (black)", Color.BLACK);
            register("red_x", "RED_X", "cross (red)", Color.RED);
        }

        private static void register(String registryName, String name, String displayName, Color color) {
            MinecraftKeyHandle key = MinecraftKeyHandle.createNew(MinecraftKeyHandle.DEFAULT_NAMESPACE, registryName);
            byKey.put(key, new TypeInfo(key, name, displayName, color));
        }

        public static TypeInfo get(MinecraftKeyHandle key) {
            return LogicUtil.getOrComputeDefault(byKey, key, TypeInfo::new);
        }

        private TypeInfo(MinecraftKeyHandle key, String name, String displayName, Color color) {
            this.key = key;
            this.name = name;
            this.displayName = displayName;
            this.color = color;
        }

        // Fallback for unknown types
        private TypeInfo(MinecraftKeyHandle key) {
            this.key = key;
            if (key.getNamespace().equals("minecraft")) {
                this.name = key.getName().toUpperCase(Locale.ENGLISH);
                this.displayName = key.getName().replace('_', ' ');
            } else {
                this.name = key.toString().toUpperCase(Locale.ENGLISH).replace('.', '_');
                this.displayName = key.toString().replace('_', ' ');
            }
            this.color = Color.WHITE;
        }
    }

    /**
     * A type of marker icon displayed on a {@link MapDisplay}
     */
    public static final class Type {
        private final boolean available;
        private final boolean visibleOnItemFrames;
        private final TypeInfo info;
        private final Holder<MapDecorationTypeHandle> handle;

        // Registry by Holder. Is by type value on older versions of Minecraft (< 1.20.5)
        private static final Map<Holder<MapDecorationTypeHandle>, Type> typesByHolder = new HashMap<>();
        private static final Map<MinecraftKeyHandle, Type> existingTypesByKey = new HashMap<>();
        private static final Map<Byte, Type> existingTypesByLegacyId = new HashMap<>();
        static {
            for (Holder<MapDecorationTypeHandle> holder : MapDecorationTypeHandle.getValues()) {
                Type type = new Type(holder);
                typesByHolder.put(holder, type);
                existingTypesByKey.put(type.info.key, type);
                existingTypesByLegacyId.put(type.id(), type);
            }
        }

        // Since oldest version supported by Minecraft
        public static final Type WHITE_POINTER = getTypeOrFail("player");
        public static final Type GREEN_POINTER = getTypeOrFail("frame");
        public static final Type RED_POINTER = getTypeOrFail("red_marker");
        public static final Type BLUE_POINTER = getTypeOrFail("blue_marker");
        public static final Type WHITE_CROSS = getTypeOrFail("target_x");
        public static final Type RED_MARKER = getTypeOrFail("target_point");
        public static final Type WHITE_CIRCLE = getTypeOrFail("player_off_map");

        // Since Minecraft 1.11
        public static final Type SMALL_WHITE_CIRCLE = getTypeOrFallback("player_off_limits", WHITE_CIRCLE);
        public static final Type MANSION = getTypeOrFallback("mansion", RED_MARKER);
        public static final Type TEMPLE = getTypeOrFallback("monument", RED_MARKER);

        // Since Minecraft 1.13
        public static final Type BANNER_WHITE = getTypeOrFallback("banner_white", WHITE_POINTER);
        public static final Type BANNER_ORANGE = getTypeOrFallback("banner_orange", GREEN_POINTER);
        public static final Type BANNER_MAGENTA = getTypeOrFallback("banner_magenta", RED_POINTER);
        public static final Type BANNER_LIGHT_BLUE = getTypeOrFallback("banner_light_blue", BLUE_POINTER);
        public static final Type BANNER_YELLOW = getTypeOrFallback("banner_yellow", GREEN_POINTER);
        public static final Type BANNER_LIME = getTypeOrFallback("banner_lime", GREEN_POINTER);
        public static final Type BANNER_PINK = getTypeOrFallback("banner_pink", RED_POINTER);
        public static final Type BANNER_GRAY = getTypeOrFallback("banner_gray", WHITE_POINTER);
        public static final Type BANNER_LIGHT_GRAY = getTypeOrFallback("banner_light_gray", WHITE_POINTER);
        public static final Type BANNER_CYAN = getTypeOrFallback("banner_cyan", BLUE_POINTER);
        public static final Type BANNER_PURPLE = getTypeOrFallback("banner_purple", RED_POINTER);
        public static final Type BANNER_BLUE = getTypeOrFallback("banner_blue", BLUE_POINTER);
        public static final Type BANNER_BROWN = getTypeOrFallback("banner_brown", BLUE_POINTER);
        public static final Type BANNER_GREEN = getTypeOrFallback("banner_green", GREEN_POINTER);
        public static final Type BANNER_RED = getTypeOrFallback("banner_red", RED_POINTER);
        public static final Type BANNER_BLACK = getTypeOrFallback("banner_black", WHITE_POINTER);
        public static final Type RED_X = getTypeOrFallback("red_x", WHITE_CROSS);

        private static final EnumMap<Color, Type> pointerByColor = byColors(WHITE_POINTER, GREEN_POINTER, RED_POINTER, BLUE_POINTER);
        private static final EnumMap<Color, Type> bannerByColor = byColors(BANNER_WHITE, BANNER_ORANGE, BANNER_MAGENTA, BANNER_LIGHT_BLUE,
                BANNER_YELLOW, BANNER_LIME, BANNER_PINK, BANNER_GRAY,
                BANNER_LIGHT_GRAY, BANNER_CYAN, BANNER_PURPLE, BANNER_BLUE,
                BANNER_BROWN, BANNER_GREEN, BANNER_RED, BANNER_BLACK);

        private static final List<Type> all_values = typesByHolder.values().stream()
                .collect(StreamUtil.toUnmodifiableList());

        private static final List<Type> all_values_including_unavailable = Stream.concat(
                all_values.stream(),
                Stream.of(Type.class.getDeclaredFields())
                        .filter(f -> f.getType() == Type.class && java.lang.reflect.Modifier.isStatic(f.getModifiers()))
                        .map(field -> {
                            try {
                                return (Type) field.get(null);
                            } catch (Throwable t) {
                                throw MountiplexUtil.uncheckedRethrow(t);
                            }
                        }))
                .distinct()
                .collect(StreamUtil.toUnmodifiableList());

        private static final Map<String, Type> by_name = all_values_including_unavailable.stream()
                .collect(Collectors.toMap(Type::name, Function.identity()));

        /**
         * Gets an unmodifiable List of Type constants that are available for the Minecraft version
         * the server is running. Types that are unavailable are not inside this List.
         *
         * @return values
         */
        public static List<Type> values() {
            return all_values;
        }

        /**
         * Gets an unmodifiable List of all possible Type constants, including those on other
         * versions of Minecraft which are currently not supported. Please avoid using this
         * unless you know what you're doing!
         *
         * @return values, including unavailable ones
         */
        public static List<Type> values_including_unavailable() {
            return all_values_including_unavailable;
        }

        /**
         * Gets the Type matching the {@link #name()} value.
         * May return unavailable types matching the name.
         *
         * @param name Name of the Type to find
         * @return Type by this name, null if not found
         */
        public static Type byName(String name) {
            return by_name.get(name);
        }

        /**
         * Gets a Map Marker Type of a pointer by pointer color.
         * Only WHITE, RED, GREEN and BLUE are supported. Other colors return null.
         *
         * @param color
         * @return pointer with the color specified, or null if there is none
         */
        public static Type getPointer(Color color) {
            return pointerByColor.get(color);
        }

        /**
         * Gets a Map Marker Type of a banner by banner color
         *
         * @param color The color of the banner to find
         * @return banner type with the color specified
         */
        public static Type getBanner(Color color) {
            return bannerByColor.get(color);
        }

        /**
         * Retrieves the Marker Type information from a provided internal NMS Handle
         *
         * @param handle Handle
         * @return Marker Type
         */
        public static Type fromHandle(Holder<MapDecorationTypeHandle> handle) {
            return LogicUtil.getOrComputeDefault(typesByHolder, handle, Type::new);
        }

        /**
         * Retrieves the Marker type information by the legacy marker type id. This should
         * never be used after 1.20.5.
         *
         * @param id Legacy Type Id
         * @return Type by this ID, or a default WHITE_POINTER placeholder if unknown
         */
        @Deprecated
        public static Type fromLegacyId(byte id) {
            return existingTypesByLegacyId.getOrDefault(id, WHITE_POINTER);
        }

        private static Type getTypeOrFail(String registryName) {
            return LogicUtil.getOrSupplyDefault(existingTypesByKey, MinecraftKeyHandle.createNew("minecraft", registryName),
                    () -> {
                        throw new IllegalStateException("Marker type does not exist: " + registryName);
                    });
        }

        private static Type getTypeOrFallback(String registryName, Type fallback) {
            return LogicUtil.getOrComputeDefault(existingTypesByKey, MinecraftKeyHandle.createNew("minecraft", registryName),
                    key -> new Type(key, fallback));
        }

        private Type(Holder<MapDecorationTypeHandle> handle) {
            this.available = true;
            this.handle = handle;
            this.info = TypeInfo.get(handle.value().getName());
            this.visibleOnItemFrames = handle.value().isShownOnItemFrame();
        }

        // Used when a particular type does not exist, to use a fallback icon in that case
        private Type(MinecraftKeyHandle key, Type fallback) {
            this.available = false;
            this.handle = fallback.getHandle();
            this.info = TypeInfo.get(key);
            this.visibleOnItemFrames = fallback.isVisibleOnItemFrames();
        }

        /**
         * Gets the name of this marker type. This is a unique all-capitalized
         * name that represents this type of marker, and is the same as used
         * by bukkit's MapCursor.Type.
         *
         * @return marker type name
         */
        public String name() {
            return this.info.name;
        }

        /**
         * Gets a user-friendly all-lowercase display name for this marker type.
         *
         * @return display name
         */
        public String displayName() {
            return this.info.displayName;
        }

        /**
         * Gets the primary color of this type of map marker
         *
         * @return color
         */
        public Color color() {
            return this.info.color;
        }

        /**
         * Gets the id used internally to represent this type of marker on the map.
         * Should not be used by people. May be different from what is expected
         * when not {@link #isAvailable()}, when a fallback type is used for display.
         *
         * @return type id
         */
        @Deprecated
        public byte id() {
            return this.handle.value().getId();
        }

        /**
         * Gets the internal handle this type represents
         *
         * @return Handle Holder
         */
        public Holder<MapDecorationTypeHandle> getHandle() {
            return this.handle;
        }

        /**
         * Gets whether this type of map marker is supported on the version of minecraft
         * the current server is running on.
         *
         * @return True if available
         */
        public boolean isAvailable() {
            return this.available;
        }

        /**
         * Gets whether this type of marker is displayed on maps in item frames, or not.
         *
         * @return True if this type of marker is displayed on item frames
         */
        public boolean isVisibleOnItemFrames() {
            return this.visibleOnItemFrames;
        }

        /**
         * Returns {@link #name()}
         */
        @Override
        public String toString() {
            return this.name();
        }

        private static EnumMap<Color, Type> byColors(Type... types) {
            EnumMap<Color, Type> result = new EnumMap<Color, Type>(Color.class);
            for (Type type : types) {
                result.put(type.color(), type);
            }
            return result;
        }

        private static Type getTypeConstant(java.lang.reflect.Field f) {
            try {
                return (Type) f.get(null);
            } catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow(t);
            }
        }
    }
}
