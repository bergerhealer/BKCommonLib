package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.mountiplex.conversion.Converter;
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
public abstract class PropertyConverter<T> extends Converter<Object, T> {

    private static final EnumDirection[] paintingFaces = {EnumDirection.DOWN, EnumDirection.UP, EnumDirection.NORTH, EnumDirection.SOUTH, EnumDirection.WEST, EnumDirection.EAST};

    @Deprecated
    public static final PropertyConverter<Integer> toItemId = new PropertyConverter<Integer>(Integer.class) {
        @Override
        public Integer convertInput(Object value) {
            Material mat = toItemMaterial.convert(value);
            if (mat == null) {
                return null;
            } else {
                return mat.getId();
            }
        }
    };

    public static final PropertyConverter<Material> toItemMaterial = new PropertyConverter<Material>(Material.class) {
        @Override
        public Material convertInput(Object value) {
            // Note: this conversion is a cascade. Order matters!
            if (value instanceof EntityItem) value = ((EntityItem) value).getItemStack();
            if (value instanceof ItemStack) value = ((ItemStack) value).getItem();

            // First convert to a material directly
            Material mat = Conversion.toMaterial.convert(value);
            if (mat != null) {
                return mat;
            }

            // Ask additional getters
            if (value instanceof org.bukkit.block.Block) {
                return ((org.bukkit.block.Block) value).getType();
            } else if (value instanceof org.bukkit.inventory.ItemStack) {
                return ((org.bukkit.inventory.ItemStack) value).getType();
            } else {
                return null;
            }
        }
    };
    public static final PropertyConverter<Integer> toPaintingFacingId = new PropertyConverter<Integer>(Integer.class) {
        @Override
        public Integer convertInput(Object value) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else {
                EnumDirection face = com.bergerkiller.mountiplex.conversion.Conversion.find(EnumDirection.class).convert(value);
                if (face != null) {
                    for (int i = 0; i < paintingFaces.length; i++) {
                        if (paintingFaces[i] == face) {
                            return i;
                        }
                    }
                }
                return null;
            }
        }
    };
    public static final PropertyConverter<EnumDirection> toPaintingFacing = new PropertyConverter<EnumDirection>(EnumDirection.class) {
        @Override
        public EnumDirection convertInput(Object value) {
            Integer id = toPaintingFacingId.convert(value);
            if (id != null) {
                final int idInt = id.intValue();
                if (LogicUtil.isInBounds(paintingFaces, idInt)) {
                    return paintingFaces[idInt];
                }
            }
            return null;
        }
    };
    public static final PropertyConverter<EntityType> toMinecartType = new PropertyConverter<EntityType>(EntityType.class) {
        @Override
        public EntityType convertInput(Object value) {
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
                    return null;
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
                        return null;
                }
            }
        }
    };
    public static final PropertyConverter<UUID> toGameProfileId = new PropertyConverter<UUID>(UUID.class) {
        @Override
        public UUID convertInput(Object value) {
            if (value instanceof GameProfile) {
                return ((GameProfile) value).getId();
            } else {
                return null;
            }
        }
    };
    public static final PropertyConverter<Object> toGameProfileFromId = new PropertyConverter<Object>(GameProfile.class) {
        @Override
        public Object convertInput(Object value) {
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

                return null;
            } else {
                return null;
            }
        }
    };

    @SuppressWarnings("unchecked")
    public PropertyConverter(Class<?> outputType) {
        super(Object.class, (Class<T>) outputType);
    }

}
