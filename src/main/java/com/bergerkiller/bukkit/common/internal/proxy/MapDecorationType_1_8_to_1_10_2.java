package com.bergerkiller.bukkit.common.internal.proxy;

/**
 * Between MC 1.8 and 1.10.2 the MapDecorationType (MapIcon.Type) enum did not exist.
 * This is a simple implementation to cover that gap and keep everything consistent.
 * Only some of the marker icons existed at that time, as some were also introduced
 * on MC 1.11.
 */
public enum MapDecorationType_1_8_to_1_10_2 {
    PLAYER(false),
    FRAME(true),
    RED_MARKER(false),
    BLUE_MARKER(false),
    TARGET_X(true),
    TARGET_POINT(true),
    PLAYER_OFF_MAP(false);

    private final byte k;
    private final boolean l;
    private final int m;

    private MapDecorationType_1_8_to_1_10_2(boolean flag) {
        this(flag, -1);
    }

    private MapDecorationType_1_8_to_1_10_2(boolean flag, int i) {
        this.k = (byte) this.ordinal();
        this.l = flag;
        this.m = i;
    }

    public byte a() {
        return this.k;
    }

    public boolean c() {
        return this.m >= 0;
    }

    public int d() {
        return this.m;
    }

    public static MapDecorationType_1_8_to_1_10_2 a(byte b0) {
        return values()[b0];
    }
}
