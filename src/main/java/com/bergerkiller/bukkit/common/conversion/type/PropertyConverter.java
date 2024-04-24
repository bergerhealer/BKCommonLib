package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.core.EnumDirectionHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.item.EntityItemHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.reflection.declarations.Template.Handle;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
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
    static {
        CommonBootstrap.initCommonServer();
    }

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
                for (int i = 0; i < PaintingFacingHelper.faces.length; i++) {
                    if (PaintingFacingHelper.faces[i].equals(face)) {
                        return i;
                    }
                }
                return null;
            } else {
                return null;
            }
        }
    };
    public static final PropertyConverter<Object> toPaintingFacing = new PropertyConverter<Object>(CommonUtil.getClass("net.minecraft.core.EnumDirection")) {
        @Override
        public Object convertInput(Object value) {
            Integer id = toPaintingFacingId.convert(value);
            if (id != null) {
                final int idInt = id.intValue();
                if (LogicUtil.isInBounds(PaintingFacingHelper.faces, idInt)) {
                    return PaintingFacingHelper.faces[idInt].getRaw();
                }
            }
            return null;
        }
    };

    public static final PropertyConverter<EntityType> toMinecartType = new PropertyConverter<EntityType>(EntityType.class) {
        @Override
        public EntityType convertInput(Object value) {
            if (value instanceof String) {
                String key = ((String) value).toUpperCase(Locale.ENGLISH);
                return MinecartMaterialHelper.matNameToMinecartType.getOrDefault(key, null);
            }
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
                return MinecartMaterialHelper.matToMinecartType.get(material);
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
    public static final PropertyConverter<Object> toGameProfileFromId = new PropertyConverter<Object>(CommonUtil.getClass("com.mojang.authlib.GameProfile")) {
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

    private static class PaintingFacingHelper {
        private static final EnumDirectionHandle[] faces = {EnumDirectionHandle.DOWN, EnumDirectionHandle.UP, EnumDirectionHandle.NORTH, EnumDirectionHandle.SOUTH, EnumDirectionHandle.WEST, EnumDirectionHandle.EAST};
    }

    // Material -> EntityType for Minecarts
    private static class MinecartMaterialHelper {
        private static final EnumMap<Material, EntityType> matToMinecartType = new EnumMap<Material, EntityType>(Material.class);
        private static final HashMap<String, EntityType> matNameToMinecartType = new HashMap<>();
        private static void storeMinecartTypes(CommonEntityType type, String... materialNames) {
            if (type == CommonEntityType.UNKNOWN) {
                throw new IllegalArgumentException("Unknown entity type registered!");
            }
            for (String materialName : materialNames) {
                matNameToMinecartType.put(materialName, type.entityType);
                matNameToMinecartType.put(materialName.replace("_", ""), type.entityType);
                Material mat = MaterialsByName.getMaterial(materialName);
                if (mat != null) {
                    matToMinecartType.put(mat, type.entityType);
                }
            }
        }
        static {
            storeMinecartTypes(CommonEntityType.MINECART, "MINECART", "LEGACY_MINECART");
            storeMinecartTypes(CommonEntityType.HOPPER_MINECART, "HOPPER", "HOPPER_MINECART", "LEGACY_HOPPER_MINECART");
            storeMinecartTypes(CommonEntityType.CHEST_MINECART, "CHEST", "STORAGE", "STORAGE_MINECART", "CHEST_MINECART", "LEGACY_CHEST", "LEGACY_STORAGE_MINECART");
            storeMinecartTypes(CommonEntityType.COMMAND_BLOCK_MINECART, "COMMAND_BLOCK", "COMMAND_BLOCK_MINECART", "LEGACY_COMMAND", "LEGACY_COMMAND_MINECART");
            storeMinecartTypes(CommonEntityType.FURNACE_MINECART, "FURNACE", "FURNACE_MINECART", "LEGACY_FURNACE", "LEGACY_POWERED_MINECART");
            storeMinecartTypes(CommonEntityType.TNT_MINECART, "TNT", "TNT_MINECART", "LEGACY_TNT", "LEGACY_EXPLOSIVE_MINECART");
        }
    }
}
