package com.bergerkiller.bukkit.common.wrappers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * The pose an Entity can have. Will include pose values from Minecraft versions
 * not supported by the server and/or client. Keep that in mind.<br>
 * <br>
 * Only supported on Minecraft 1.14 and later.
 */
public enum EntityPose {
    STANDING(0),
    FALL_FLYING(1),
    SLEEPING(2),
    SWIMMING(3),
    SPIN_ATTACK(4),
    CROUCHING(5),
    LONG_JUMPING(6),
    DYING(7),
    /** Player entity is crouching (formerly: sneaking) */
    CROAKING(8),
    USING_TONGUE(9),
    SITTING(10),
    ROARING(11),
    SNIFFING(12),
    EMERGING(13),
    DIGGING(14),
    SLIDING(15),
    SHOOTING(16),
    INHALING(17),

    /**
     * Value reserved for poses BKCommonLib does not know about. Do not use.
     * Value is ignored when set as a datawatcher value.
     */
    UNKNOWN(-1);

    private final int id;
    private static final EntityPose[] BY_ID;
    private static final Map<String, EntityPose> BY_NAME = new HashMap<>();
    static {
        int max = Stream.of(EntityPose.values())
                .filter(p -> p != UNKNOWN)
                .mapToInt(EntityPose::getId).max().orElse(0);
        BY_ID = new EntityPose[max + 1];
        for (EntityPose pose : EntityPose.values()) {
            if (pose == UNKNOWN) {
                continue;
            }
            BY_ID[pose.getId()] = pose;
            BY_NAME.put(pose.name(), pose);
        }
        BY_NAME.put("SNEAKING", CROUCHING);
    }

    EntityPose(int id) {
        this.id = id;
    }

    /**
     * Gets the internal id of this pose. Internal use only.
     *
     * @return Id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the pose value by id. Returns {@link #UNKNOWN} if the id is invalid.
     *
     * @param id Id ({@link #getId()})
     * @return Pose by this id, or {@link #UNKNOWN} if the id is invalid
     */
    public static EntityPose fromId(int id) {
        EntityPose[] byIdLocal = BY_ID;
        if (id >= 0 && id < byIdLocal.length) {
            return byIdLocal[id];
        } else {
            return UNKNOWN;
        }
    }

    /**
     * Gets the pose value by enum name. Returns {@link #UNKNOWN} if the name is invalid.
     *
     * @param name Enum name. Also supports older names (SNEAKING -> CROUCHING)
     * @return EntityPose by this exact name
     */
    public static EntityPose fromName(String name) {
        return BY_NAME.getOrDefault(name, UNKNOWN);
    }
}
