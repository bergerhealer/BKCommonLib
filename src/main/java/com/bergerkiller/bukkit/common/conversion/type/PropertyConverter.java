package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.mountiplex.conversion.BasicConverter;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntity;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_11_R1.EntityItem;
import net.minecraft.server.v1_11_R1.EnumDirection;
import net.minecraft.server.v1_11_R1.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Converter to convert to a certain property obtained from various kinds of
 * objects<br>
 * These converters are not registered, as they can potentially overwrite data
 * conversions
 *
 * @param <T> - type of output
 */
public abstract class PropertyConverter<T> extends BasicConverter<T> {

    private static final EnumDirection[] paintingFaces = {EnumDirection.DOWN, EnumDirection.UP, EnumDirection.NORTH, EnumDirection.SOUTH, EnumDirection.WEST, EnumDirection.EAST};

    @Deprecated
    public static final PropertyConverter<Integer> toItemId = new PropertyConverter<Integer>(Integer.class) {
        @Override
        public Integer convertSpecial(Object value, Class<?> valueType, Integer def) {
            Material mat = toItemMaterial.convert(value);
            if (mat == null) {
                return def;
            } else {
                return mat.getId();
            }
        }
    };

    public static final PropertyConverter<Material> toItemMaterial = new PropertyConverter<Material>(Material.class) {
        @Override
        public Material convertSpecial(Object value, Class<?> valueType, Material def) {
            // Note: this conversion is a cascade. Order matters!
            if (value instanceof EntityItem) value = ((EntityItem) value).getItemStack();
            if (value instanceof ItemStack) value = ((ItemStack) value).getItem();

            // First convert to a material directly
            Material mat = ConversionTypes.toMaterial.convert(value);
            if (mat != null) {
                return mat;
            }

            // Ask additional getters
            if (value instanceof org.bukkit.block.Block) {
                return ((org.bukkit.block.Block) value).getType();
            } else if (value instanceof org.bukkit.inventory.ItemStack) {
                return ((org.bukkit.inventory.ItemStack) value).getType();
            } else {
                return def;
            }
        }
    };
    public static final PropertyConverter<Integer> toPaintingFacingId = new PropertyConverter<Integer>(Integer.class) {
        @Override
        public Integer convertSpecial(Object value, Class<?> valueType, Integer def) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else {
                EnumDirection face = Conversion.convert(value, EnumDirection.class);
                if (face != null) {
                    for (int i = 0; i < paintingFaces.length; i++) {
                        if (paintingFaces[i] == face) {
                            return i;
                        }
                    }
                }
                return def;
            }
        }
    };
    public static final PropertyConverter<EnumDirection> toPaintingFacing = new PropertyConverter<EnumDirection>(EnumDirection.class) {
        @Override
        public EnumDirection convertSpecial(Object value, Class<?> valueType, EnumDirection def) {
            Integer id = toPaintingFacingId.convert(value);
            if (id != null) {
                final int idInt = id.intValue();
                if (LogicUtil.isInBounds(paintingFaces, idInt)) {
                    return paintingFaces[idInt];
                }
            }
            return def;
        }
    };
    public static final PropertyConverter<EntityType> toMinecartType = new PropertyConverter<EntityType>(EntityType.class) {
        @Override
        protected EntityType convertSpecial(Object value, Class<?> valueType, EntityType def) {
            if (NMSEntity.T.isInstance(value)) {
                value = Conversion.toEntity.convert(value);
            }
            if (value instanceof Minecart) {
                return ((Minecart) value).getType();
            } else if (value instanceof CommonMinecart) {
                return ((CommonMinecart<?>) value).getType();
            } else {
                Material material = Conversion.toMaterial.convert(value);
                if (material == null) {
                    return def;
                }
                switch (material) {
                    case FURNACE:
                    case POWERED_MINECART:
                        return EntityType.MINECART_FURNACE;
                    case CHEST:
                    case STORAGE_MINECART:
                        return EntityType.MINECART_CHEST;
                    case HOPPER:
                    case HOPPER_MINECART:
                        return EntityType.MINECART_HOPPER;
                    //case MOB_SPAWNER :
                    //case MOB_SPAWNER_MINECART : return EntityType.MINECART_MOB_SPAWNER; (TODO: missing!)
                    case TNT:
                    case EXPLOSIVE_MINECART:
                        return EntityType.MINECART_TNT;
                    case MINECART:
                        return EntityType.MINECART;
                    default:
                        return def;
                }
            }
        }
    };
    public static final PropertyConverter<UUID> toGameProfileId = new PropertyConverter<UUID>(UUID.class) {
        @Override
        protected UUID convertSpecial(Object value, Class<?> valueType, UUID def) {
            if (value instanceof GameProfile) {
                return ((GameProfile) value).getId();
            } else {
                return def;
            }
        }
    };
    public static final PropertyConverter<Object> toGameProfileFromId = new PropertyConverter<Object>(GameProfile.class) {
        @Override
        protected Object convertSpecial(Object value, Class<?> valueType, Object def) {
            if (value instanceof String) {
                String name = (String) value;
                return CommonUtil.getGameProfile(name);
            } else if (value instanceof UUID) {
                UUID uuid = (UUID) value;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getUniqueId().equals(uuid)) {
                        return PlayerUtil.getGameProfile(player);
                    }
                }

                return def;
            } else {
                return def;
            }
        }
    };

    @SuppressWarnings("unchecked")
    public PropertyConverter(Class<?> outputType) {
        super((Class<T>) outputType);
    }

    @Override
    public boolean isRegisterSupported() {
        return false;
    }
}
