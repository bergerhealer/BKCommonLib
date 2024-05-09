package com.bergerkiller.bukkit.common.resources;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.core.particles.ParticleHandle;

/**
 * Stores types of particles supported by Minecraft.
 */
public class ParticleType<T> extends BasicWrapper<ParticleHandle> {
    /**
     * Particle Type returned when the particle type cannot be identified, or where
     * the particle type is not supported on the current version of the server.
     */
    public static final ParticleType<?> UNKNOWN = new ParticleTypeUnknown();

    private static Map<Object, ParticleType<?>> byNMSHandle;
    static {
        IdentityHashMap<Object, ParticleType<?>> byNMSHandleInit = new IdentityHashMap<>();
        for (Object nmsHandle : ParticleHandle.values()) {
            byNMSHandleInit.put(nmsHandle, new ParticleType<Object>(nmsHandle));
        }
        byNMSHandle = byNMSHandleInit;
    }

    /*
     * Constants referring to types of particles that exist in the server
     */

    /* Since Minecraft 1.8 */
    public static final ParticleType<Void> ANGRY_VILLAGER = byName_1_13("angryVillager", "angry_villager");
    public static final ParticleType<Void> HAPPY_VILLAGER = byName_1_13("happyVillager", "happy_villager");
    public static final ParticleType<Void> FLAME = byName("flame");
    public static final ParticleType<Void> HEART = byName("heart");
    public static final ParticleType<Void> WITCH = byName_1_13("witchMagic", "witch");
    public static final ParticleType<Void> NOTE = byName("note");
    public static final ParticleType<Void> SMOKE = byName("smoke");
    public static final ParticleType<Void> SPLASH = byName("splash");
    public static final ParticleType<Void> LAVA = byName("lava");
    public static final ParticleType<Void> BUBBLE = byName("bubble");
    public static final ParticleType<Void> CLOUD = byName("cloud");
    public static final ParticleType<Void> CRIT = byName("crit");
    public static final ParticleType<Void> DRIPPING_LAVA = byName_1_13("dripLava", "dripping_lava");
    public static final ParticleType<Void> DRIPPING_WATER = byName_1_13("dripWater", "dripping_water");
    public static final ParticleType<Void> FIREWORK = byName_1_13("fireworksSpark", "firework");
    public static final ParticleType<Void> LARGE_SMOKE = byName_1_13("largesmoke", "large_smoke");
    public static final ParticleType<Void> PORTAL = byName("portal");
    public static final ParticleType<Void> ENCHANT = byName_1_13("enchantmenttable", "enchant");
    public static final ParticleType<Void> ITEM_SLIME = byName_1_13("slime", "item_slime");
    public static final ParticleType<Void> ITEM_SNOWBALL = byName_1_13("snowballpoof", "item_snowball");
    public static final ParticleType<ColorOptions> ENTITY_EFFECT = byName_1_13("mobSpell", "entity_effect");
    public static final ParticleType<ColorOptions> AMBIENT_ENTITY_EFFECT = CommonBootstrap.evaluateMCVersion(">=", "1.20.5")
            ? ENTITY_EFFECT : byName_1_13("mobSpellAmbient", "ambient_entity_effect");
    public static final ParticleType<DustOptions> DUST = byName_1_13("reddust", "dust");
    public static final ParticleType<Void> POOF = byName_1_13("explode", "poof");
    public static final ParticleType<Void> EXPLOSION = byName_1_13("largeexplode", "explosion");
    public static final ParticleType<Void> EXPLOSION_EMITTER = byName_1_13("hugeexplosion", "explosion_emitter");
    public static final ParticleType<Void> INSTANT_EFFECT = byName_1_13("instantSpell", "instant_effect");
    public static final ParticleType<Void> FISHING = byName_1_13("wake", "fishing");
    public static final ParticleType<ItemStack> ITEM = byName_1_13("iconcrack", "item");
    // Note: was also "blockdust" on 1.12.2 and before
    public static final ParticleType<BlockData> BLOCK = byName_1_13("blockcrack", "block");
    public static final ParticleType<Void> EFFECT = byName_1_13("spell", "effect");
    public static final ParticleType<Void> ENCHANTED_HIT = byName_1_13("magicCrit", "enchanted_hit");
    public static final ParticleType<Void> RAIN = byName_1_13("droplet", "rain");

    /* Since Minecraft 1.9 */
    public static final ParticleType<Void> DRAGON_BREATH = byName_1_13("dragonbreath", "dragon_breath");
    public static final ParticleType<Void> END_ROD = byName_1_13("endRod", "end_rod");
    public static final ParticleType<Void> DAMAGE_INDICATOR = byName_1_13("damageIndicator", "damage_indicator");
    public static final ParticleType<Void> SWEEP_ATTACK = byName_1_13("sweepAttack", "sweep_attack");

    /* Since Minecraft 1.10 */
    public static final ParticleType<BlockData> FALLING_DUST = byName_1_13("fallingdust", "falling_dust");

    /* Since Minecraft 1.11 */
    public static final ParticleType<Void> SPIT = byName("spit");
    public static final ParticleType<Void> TOTEM_OF_UNDYING = byName_1_13("totem", "totem_of_undying");

    /* Up until Minecraft 1.12.2 */
    public static final ParticleType<Void> FOOTSTEP = byName("footstep");
    public static final ParticleType<Void> MOB_APPEARANCE = byName("mobappearance");
    public static final ParticleType<Void> DEPTH_SUSPEND = byName("depthsuspend");
    public static final ParticleType<Void> SUSPENDED = byName("suspended");
    public static final ParticleType<Void> ITEM_TAKE = byName("take");
    public static final ParticleType<Void> TOWN_AURA = byName("townaura");
    // Trigger 2001 block effect instead since 1.13
    public static final ParticleType<Void> SNOW_SHOVEL = byName("snowshovel");

    /* Since Minecraft 1.13 */
    public static final ParticleType<Void> UNDERWATER = byName("underwater");
    public static final ParticleType<Void> MYCELIUM = byName("mycelium");
    public static final ParticleType<Void> SQUID_INK = byName("squid_ink");
    public static final ParticleType<Void> ELDER_GUARDIAN = byName("elder_guardian");
    public static final ParticleType<Void> BUBBLE_POP = byName("bubble_pop");
    public static final ParticleType<Void> CURRENT_DOWN = byName("current_down");
    public static final ParticleType<Void> BUBBLE_COLUMN_UP = byName("bubble_column_up");
    public static final ParticleType<Void> NAUTILUS = byName("nautilus");
    public static final ParticleType<Void> DOLPHIN = byName("dolphin");

    /* Since Minecraft 1.14 */
    public static final ParticleType<Void> FALLING_LAVA = byName("falling_lava");
    public static final ParticleType<Void> LANDING_LAVA = byName("landing_lava");
    public static final ParticleType<Void> FALLING_WATER = byName("falling_water");
    public static final ParticleType<Void> FLASH = byName("flash");
    public static final ParticleType<Void> COMPOSTER = byName("composter");
    public static final ParticleType<Void> SNEEZE = byName("sneeze");
    public static final ParticleType<Void> CAMPFIRE_COSY_SMOKE = byName("campfire_cosy_smoke");
    public static final ParticleType<Void> CAMPFIRE_SIGNAL_SMOKE = byName("campfire_signal_smoke");

    /* Since Minecraft 1.15 */
    public static final ParticleType<Void> DRIPPING_HONEY = byName("dripping_honey");
    public static final ParticleType<Void> FALLING_HONEY = byName("falling_honey");
    public static final ParticleType<Void> LANDING_HONEY = byName("landing_honey");
    public static final ParticleType<Void> FALLING_NECTAR = byName("falling_nectar");

    /* Since Minecraft 1.16 */
    public static final ParticleType<Void> SOUL_FIRE_FLAME = byName("soul_fire_flame");
    public static final ParticleType<Void> SOUL = byName("soul");
    public static final ParticleType<Void> ASH = byName("ash");
    public static final ParticleType<Void> CRIMSON_SPORE = byName("crimson_spore");
    public static final ParticleType<Void> WARPED_SPORE = byName("warped_spore");
    public static final ParticleType<Void> DRIPPING_OBSIDIAN_TEAR = byName("dripping_obsidian_tear");
    public static final ParticleType<Void> FALLING_OBSIDIAN_TEAR = byName("falling_obsidian_tear");
    public static final ParticleType<Void> LANDING_OBSIDIAN_TEAR = byName("landing_obsidian_tear");
    public static final ParticleType<Void> REVERSE_PORTAL = byName("reverse_portal");
    public static final ParticleType<Void> WHITE_ASH = byName("white_ash");

    /* Since Minecraft 1.17 */
    public static final ParticleType<VibrationOptions> VIBRATION = byName("vibration");
    public static final ParticleType<DustColorTransitionOptions> DUST_COLOR_TRANSITION = byName("dust_color_transition");
    public static final ParticleType<Void> FALLING_SPORE_BLOSSOM = byName("falling_spore_blossom");
    public static final ParticleType<Void> SPORE_BLOSSOM_AIR = byName("spore_blossom_air");
    public static final ParticleType<Void> SMALL_FLAME = byName("small_flame");
    public static final ParticleType<Void> SNOWFLAKE = byName("snowflake");
    public static final ParticleType<Void> DRIPPING_DRIPSTONE_LAVA = byName("dripping_dripstone_lava");
    public static final ParticleType<Void> FALLING_DRIPSTONE_LAVA = byName("falling_dripstone_lava");
    public static final ParticleType<Void> DRIPPING_DRIPSTONE_WATER = byName("dripping_dripstone_water");
    public static final ParticleType<Void> FALLING_DRIPSTONE_WATER = byName("falling_dripstone_water");
    public static final ParticleType<Void> GLOW_SQUID_INK = byName("glow_squid_ink");
    public static final ParticleType<Void> GLOW = byName("glow");
    public static final ParticleType<Void> SCRAPE = byName("scrape");
    public static final ParticleType<Void> WAX_ON = byName("wax_on");
    public static final ParticleType<Void> WAX_OFF = byName("wax_off");
    public static final ParticleType<Void> ELECTRIC_SPARK = byName("electric_spark");

    /* Minecraft 1.17 only */
    public static final ParticleType<Void> LIGHT = byName("light");

    /* Up to Minecraft 1.17.1 */
    public static final ParticleType<Void> BARRIER = byName("barrier");

    /*
     * Since Minecraft 1.18
     */
    public static final ParticleType<BlockData> BLOCK_MARKER = byName("block_marker");

    /*
     * Since Minecraft 1.19
     */
    public static final ParticleType<Void> SONIC_BOOM = byName("sonic_boom");
    public static final ParticleType<Void> SCULK_SOUL = byName("sculk_soul");
    public static final ParticleType<SculkChargeOptions> SCULK_CHARGE = byName("sculk_charge");
    public static final ParticleType<Void> SCULK_CHARGE_POP = byName("sculk_charge_pop");
    public static final ParticleType<ShriekOptions> SHRIEK = byName("shriek");

    protected ParticleType() {
        this.setHandle(ParticleHandle.T.createHandle(null, true));
    }

    private ParticleType(Object nmsHandle) {
        this.setHandle(ParticleHandle.createHandle(nmsHandle));
    }

    /**
     * Gets the unique identifier of this type of Particle
     *
     * @return particle type name
     */
    public String getName() {
        return this.handle.getName();
    }

    /**
     * Gets whether this type of particles requires additional options to be specified
     *
     * @return True if this particle type requires options
     */
    public boolean hasOptions() {
        return this.handle.hasOptions();
    }

    /**
     * Gets whether this type of particle exists for the current server version
     *
     * @return True if the particle type exists
     */
    public boolean exists() {
        return true;
    }

    @Override
    public String toString() {
        return "ParticleType{name=" + getName() + "}";
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    /**
     * Finds a ParticleType by its internal handle
     *
     * @param nmsParticleHandle
     * @return Particle Type, or {@link #UNKNOWN} if not found
     */
    public static ParticleType<?> byNMSParticleHandle(Object nmsParticleHandle) {
        if (nmsParticleHandle == null) {
            return UNKNOWN;
        }

        return LogicUtil.synchronizeCopyOnWrite(ParticleType.class, () -> byNMSHandle, nmsParticleHandle, Map::get, (map, key) -> {
            ParticleType<Object> value = new ParticleType<Object>(key);
            Map<Object, ParticleType<?>> copy = new IdentityHashMap<>(map);
            copy.put(key, value);
            byNMSHandle = copy;
            return value;
        });
    }

    /**
     * Finds a registered ParticleType by name
     *
     * @param <T> Type of options required for this particle type
     * @param name Name of the particle
     * @return Particle Type, or {@link #UNKNOWN} if not found
     */
    @SuppressWarnings("unchecked")
    public static <T> ParticleType<T> byName(String name) {
        return (ParticleType<T>) byNMSParticleHandle(ParticleHandle.byName(name));
    }

    private static <T> ParticleType<T> byName_1_13(String name_1_12, String name_1_13) {
        return byName(CommonCapabilities.PARTICLE_OPTIONS ? name_1_13 : name_1_12);
    }

    /**
     * Gets all possible particle types that exist on the server. Particle types that aren't supported
     * are not included.
     *
     * @return Collection of supported particle types
     */
    public static Collection<ParticleType<?>> values() {
        return byNMSHandle.values();
    }

    private static final class ParticleTypeUnknown extends ParticleType<Object> {
        @Override
        public String getName() {
            return "";
        }

        @Override
        public boolean exists() {
            return false;
        }

        @Override
        public boolean hasOptions() {
            return false;
        }

        @Override
        public String toString() {
            return "ParticleType{UNKNOWN}";
        }
    }

    public static class DustOptions {
        public Color color = Color.WHITE;
        public float scale = 1.0f;

        public static DustOptions create(int red, int green, int blue, float scale) {
            return create(Color.fromRGB(red, green, blue), scale);
        }

        public static DustOptions create(Color color, float scale) {
            DustOptions opt = new DustOptions();
            opt.color = color;
            opt.scale = scale;
            return opt;
        }
    }

    public static final class DustColorTransitionOptions extends DustOptions {
        public Color endColor = Color.WHITE;

        public static DustColorTransitionOptions create(Color startColor, Color endColor, float scale) {
            DustColorTransitionOptions opt = new DustColorTransitionOptions();
            opt.color = startColor;
            opt.endColor = endColor;
            opt.scale = scale;
            return opt;
        }
    }

    public static final class SculkChargeOptions {
        public float roll = 0.0f;

        public static SculkChargeOptions create(float roll) {
            SculkChargeOptions opt = new SculkChargeOptions();
            opt.roll = roll;
            return opt;
        }
    }

    public static final class ColorOptions {
        public Color color = Color.WHITE;

        public static ColorOptions create(int r, int g, int b) {
            return create(Color.fromRGB(r, g, b));
        }

        public static ColorOptions create(Color color) {
            ColorOptions opt = new ColorOptions();
            opt.color = color;
            return opt;
        }
    }

    public static interface PositionOption {
    }

    public static class BlockPositionOption implements PositionOption {
        public int x = 0;
        public int y = 0;
        public int z = 0;

        public static BlockPositionOption create(org.bukkit.block.Block block) {
            return create(block.getX(), block.getY(), block.getZ());
        }

        public static BlockPositionOption create(IntVector3 blockCoordinates) {
            return create(blockCoordinates.x, blockCoordinates.y, blockCoordinates.z);
        }

        public static BlockPositionOption create(int x, int y, int z) {
            BlockPositionOption opt = new BlockPositionOption();
            opt.x = x;
            opt.y = y;
            opt.z = z;
            return opt;
        }
    }

    /**
     * Targets an Entity by its Id. Works on all version of Minecraft where a
     * position option is specified.
     */
    public static class EntityByIdPositionOption implements PositionOption {
        public int entityId;
        public float yOffset = 0.0f;

        public static EntityByIdPositionOption create(int entityId, float yOffset) {
            EntityByIdPositionOption opt = new EntityByIdPositionOption();
            opt.entityId = entityId;
            opt.yOffset = yOffset;
            return opt;
        }
    }

    /**
     * Targets an Entity by its Unique Id. Only works on MC 1.19 and later.
     */
    public static class EntityByUUIDPositionOption implements PositionOption {
        public UUID entityUUID;
        public float yOffset = 0.0f;

        public static EntityByUUIDPositionOption create(UUID entityUUID, float yOffset) {
            EntityByUUIDPositionOption opt = new EntityByUUIDPositionOption();
            opt.entityUUID = entityUUID;
            opt.yOffset = yOffset;
            return opt;
        }
    }

    public static final class VibrationOptions {
        /** Used on 1.18, on 1.19 it uses the particle position */
        public BlockPositionOption origin;

        public PositionOption destination;
        public int arrivalInTicks;

        public static VibrationOptions create(BlockPositionOption origin, PositionOption destination, int arrivalInTicks) {
            VibrationOptions opt = new VibrationOptions();
            opt.origin = origin;
            opt.destination = destination;
            opt.arrivalInTicks = arrivalInTicks;
            return opt;
        }
    }

    public static final class ShriekOptions {
        public int delay = 0;

        public static ShriekOptions create(int delay) {
            ShriekOptions opt = new ShriekOptions();
            opt.delay = delay;
            return opt;
        }
    }
}
