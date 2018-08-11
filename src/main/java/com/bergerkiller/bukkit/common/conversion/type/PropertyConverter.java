package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityItemHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumDirectionHandle;
import com.bergerkiller.generated.net.minecraft.server.ItemHandle;
import com.bergerkiller.generated.net.minecraft.server.ItemStackHandle;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.reflection.declarations.Template.Handle;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

import java.util.EnumMap;
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

    private static final EnumDirectionHandle[] paintingFaces = {EnumDirectionHandle.DOWN, EnumDirectionHandle.UP, EnumDirectionHandle.NORTH, EnumDirectionHandle.SOUTH, EnumDirectionHandle.WEST, EnumDirectionHandle.EAST};

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
            if (EntityItemHandle.T.isAssignableFrom(value)) {
                value = EntityItemHandle.T.getItemStack.raw.invoke(value);
            }
            if (ItemStackHandle.T.isAssignableFrom(value)) {
                value = ItemStackHandle.T.getItem.invoke(value);
            }
            if (ItemHandle.T.isAssignableFrom(value)) {
                return WrapperConversion.toMaterialFromItemHandle(value);
            }
            if (value instanceof org.bukkit.inventory.ItemStack) {
                return ((org.bukkit.inventory.ItemStack) value).getType();
            } else if (value instanceof org.bukkit.block.Block) {
                return ((org.bukkit.block.Block) value).getType();
            } else {
                return Conversion.toMaterial.convert(value);
            }
        }
    };
    public static final PropertyConverter<Integer> toPaintingFacingId = new PropertyConverter<Integer>(Integer.class) {
        @Override
        public Integer convertInput(Object value) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else if (EnumDirectionHandle.T.isAssignableFrom(value)) {
                EnumDirectionHandle face = EnumDirectionHandle.createHandle(value);
                for (int i = 0; i < paintingFaces.length; i++) {
                    if (paintingFaces[i].equals(face)) {
                        return i;
                    }
                }
                return null;
            } else {
                return null;
            }
        }
    };
    public static final PropertyConverter<Object> toPaintingFacing = new PropertyConverter<Object>(EnumDirectionHandle.T.getType()) {
        @Override
        public Object convertInput(Object value) {
            Integer id = toPaintingFacingId.convert(value);
            if (id != null) {
                final int idInt = id.intValue();
                if (LogicUtil.isInBounds(paintingFaces, idInt)) {
                    return paintingFaces[idInt].getRaw();
                }
            }
            return null;
        }
    };

    // ======================= Material -> EntityType for Minecarts =============================
    private static final EnumMap<Material, EntityType> matToMinecartType = new EnumMap<Material, EntityType>(Material.class);
    private static void storeMinecartTypes(EntityType type, String... materialNames) {
        for (String materialName : materialNames) {
            Material mat = CommonLegacyMaterials.getMaterial(materialName);
            if (mat != null) {
                matToMinecartType.put(mat, type);
            }
        }
    }
    static {
        storeMinecartTypes(EntityType.MINECART, "MINECART", "LEGACY_MINECART");
        storeMinecartTypes(EntityType.MINECART_HOPPER, "HOPPER", "HOPPER_MINECART", "LEGACY_HOPPER_MINECART");
        storeMinecartTypes(EntityType.MINECART_CHEST, "CHEST", "CHEST_MINECART", "LEGACY_CHEST", "LEGACY_STORAGE_MINECART");
        storeMinecartTypes(EntityType.MINECART_COMMAND, "COMMAND_BLOCK", "COMMAND_BLOCK_MINECART", "LEGACY_COMMAND", "LEGACY_COMMAND_MINECART");
        storeMinecartTypes(EntityType.MINECART_FURNACE, "FURNACE", "FURNACE_MINECART", "LEGACY_FURNACE", "LEGACY_POWERED_MINECART");
        storeMinecartTypes(EntityType.MINECART_TNT, "TNT", "TNT_MINECART", "LEGACY_TNT", "LEGACY_EXPLOSIVE_MINECART");
    }
    // ============================================================================================

    public static final PropertyConverter<EntityType> toMinecartType = new PropertyConverter<EntityType>(EntityType.class) {
        @Override
        public EntityType convertInput(Object value) {
            if (EntityHandle.T.isAssignableFrom(value)) {
                value = WrapperConversion.toEntity(value);
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
                return matToMinecartType.get(material);
            }
        }
    };
    public static final PropertyConverter<UUID> toGameProfileId = new PropertyConverter<UUID>(UUID.class) {
        @Override
        public UUID convertInput(Object value) {
            if (GameProfileHandle.T.isAssignableFrom(value)) {
                return GameProfileHandle.T.getId.invoke(value);
            } else {
                return null;
            }
        }
    };
    public static final PropertyConverter<Object> toGameProfileFromId = new PropertyConverter<Object>(GameProfileHandle.T.getType()) {
        @Override
        public Object convertInput(Object value) {
            if (value instanceof String) {
                String name = (String) value;
                return Handle.getRaw(CommonUtil.getGameProfile(name));
            } else if (value instanceof UUID) {
                UUID uuid = (UUID) value;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getUniqueId().equals(uuid)) {
                        return Handle.getRaw(PlayerUtil.getGameProfile(player));
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
