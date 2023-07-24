package com.bergerkiller.bukkit.common.internal.proxy;

/**
 * Provides the EnumItemSlot enumeration for MC 1.8.8
 */
public enum EnumItemSlot {
    MAINHAND(EnumItemSlot.Function.HAND, 0, 0, "mainhand"),
    FEET(EnumItemSlot.Function.ARMOR, 0, 1, "feet"),
    LEGS(EnumItemSlot.Function.ARMOR, 1, 2, "legs"),
    CHEST(EnumItemSlot.Function.ARMOR, 2, 3, "chest"),
    HEAD(EnumItemSlot.Function.ARMOR, 3, 4, "head");

    private final EnumItemSlot.Function g;
    private final int h;
    private final int i;
    private final String j;

    private static final EnumItemSlot[] BY_PLAYER_SLOT_INDEX = new EnumItemSlot[4];
    static {
        BY_PLAYER_SLOT_INDEX[FEET.b()] = FEET;
        BY_PLAYER_SLOT_INDEX[LEGS.b()] = LEGS;
        BY_PLAYER_SLOT_INDEX[CHEST.b()] = CHEST;
        BY_PLAYER_SLOT_INDEX[HEAD.b()] = HEAD;
    }

    private static final EnumItemSlot[] BY_NONPLAYER_SLOT_INDEX = EnumItemSlot.values(); // Same order

    private EnumItemSlot(EnumItemSlot.Function enumitemslot_function, int i, int j, String s) {
        this.g = enumitemslot_function;
        this.h = i;
        this.i = j;
        this.j = s;
    }

    public EnumItemSlot.Function a() {
        return this.g;
    }

    // Slot index when worn by a player ('human')
    public int b() {
        return this.h;
    }

    // Slot index when worn by non-players that lack inventory (armorstands)
    public int c() {
        return this.i;
    }

    public String d() {
        return this.j;
    }

    // Added by BKCL
    public static EnumItemSlot fromPlayerSlotIndex(int index) {
        int len = BY_PLAYER_SLOT_INDEX.length;
        if (index < 0 || index >= len) {
            return HEAD;
        } else {
            return BY_PLAYER_SLOT_INDEX[index];
        }
    }

    // Added by BKCL
    public static EnumItemSlot fromNonPlayerSlotIndex(int index) {
        int len = BY_NONPLAYER_SLOT_INDEX.length;
        if (index < 0 || index >= len) {
            return HEAD;
        } else {
            return BY_NONPLAYER_SLOT_INDEX[index];
        }
    }

    public static EnumItemSlot a(String s) {
        EnumItemSlot[] aenumitemslot = values();
        int i = aenumitemslot.length;

        for (int j = 0; j < i; ++j) {
            EnumItemSlot enumitemslot = aenumitemslot[j];

            if (enumitemslot.d().equals(s)) {
                return enumitemslot;
            }
        }

        throw new IllegalArgumentException("Invalid slot \'" + s + "\'");
    }

    public static enum Function {

        HAND, ARMOR;

        private Function() {}
    }
}
