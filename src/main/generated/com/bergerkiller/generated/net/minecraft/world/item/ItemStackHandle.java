package com.bergerkiller.generated.net.minecraft.world.item;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.IBlockDataHandle;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.item.ItemStack</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.item.ItemStack")
public abstract class ItemStackHandle extends Template.Handle {
    /** @see ItemStackClass */
    public static final ItemStackClass T = Template.Class.create(ItemStackClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ItemStackHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ItemStackHandle newInstance(Material type) {
        return T.newInstance.invoke(type);
    }

    public static ItemStackHandle fromBlockData(IBlockDataHandle data, int amount) {
        return T.fromBlockData.invoke(data, amount);
    }

    public abstract Object getItem();
    public abstract Material getTypeField();
    public abstract ChatText getCustomName();
    public abstract ChatText getDisplayName();
    public abstract boolean hasCustomName();
    public abstract void setCustomName(ChatText name);
    public abstract void hideTooltip();
    public abstract void hideAllAttributes();
    public abstract List<ChatText> getLores();
    public abstract void addLore(ChatText line);
    public abstract void clearLores();
    public abstract int getDamageValue();
    public abstract void setDamageValue(int damage);
    public abstract boolean isUnbreakable();
    public abstract void setUnbreakable(boolean unbreakable);
    public abstract int getRepairCost();
    public abstract void setRepairCost(int cost);
    public abstract int getMapColor();
    public abstract void setMapColor(int rgb);
    public abstract int getLeatherArmorColor();
    public abstract int getPotionColor();
    public abstract int getFireworksFlightDuration();
    public abstract void setFireworksFlightDuration(int duration);
    public abstract GameProfileHandle getSkullProfile();
    public abstract void setSkullProfile(GameProfileHandle profile);
    public abstract boolean hasCustomModelDataValue();
    public abstract int getCustomModelDataValue();
    public abstract void setCustomModelDataValue(int value);
    public abstract void clearCustomModelData();
    public abstract boolean hasCustomData();
    public abstract CommonTagCompound getCustomDataCopy();
    public abstract CommonTagCompound getCustomData();
    public abstract void setCustomData(CommonTagCompound tag);
    public abstract void updateCustomData(Consumer<CommonTagCompound> consumer);
    public abstract ItemStackHandle cloneItemStack();
    public abstract ItemStackHandle cloneAndSubtract(int n);
    public abstract ItemStack toBukkit();
    public abstract boolean isMapItem();
    public abstract int getMapId();
    public abstract void setMapId(int mapId);
    public abstract UUID getMapDisplayDynamicOnlyUUID();
    public abstract UUID getMapDisplayUUID();
    public static final ItemStackHandle EMPTY_ITEM;
    static {
        if (T.OPT_EMPTY_ITEM.isAvailable()) {
            EMPTY_ITEM = T.OPT_EMPTY_ITEM.get();
        } else {
            EMPTY_ITEM = T.createHandle(null, true);
        }
    }

    public static ItemStackHandle fromBukkit(org.bukkit.inventory.ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        } else {
            return createHandle(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toItemStackHandle(itemStack));
        }
    }
    public abstract int getAmountField();
    public abstract void setAmountField(int value);
    /**
     * Stores class members for <b>net.minecraft.world.item.ItemStack</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ItemStackClass extends Template.Class<ItemStackHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<ItemStackHandle> OPT_EMPTY_ITEM = new Template.StaticField.Converted<ItemStackHandle>();

        public final Template.Field.Integer amountField = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<ItemStackHandle> newInstance = new Template.StaticMethod.Converted<ItemStackHandle>();
        public final Template.StaticMethod.Converted<ItemStackHandle> fromBlockData = new Template.StaticMethod.Converted<ItemStackHandle>();

        @Template.Optional
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();
        public final Template.Method<Object> getItem = new Template.Method<Object>();
        public final Template.Method.Converted<Material> getTypeField = new Template.Method.Converted<Material>();
        public final Template.Method.Converted<ChatText> getCustomName = new Template.Method.Converted<ChatText>();
        public final Template.Method.Converted<ChatText> getDisplayName = new Template.Method.Converted<ChatText>();
        public final Template.Method<Boolean> hasCustomName = new Template.Method<Boolean>();
        public final Template.Method.Converted<Void> setCustomName = new Template.Method.Converted<Void>();
        public final Template.Method<Void> hideTooltip = new Template.Method<Void>();
        public final Template.Method<Void> hideAllAttributes = new Template.Method<Void>();
        public final Template.Method.Converted<List<ChatText>> getLores = new Template.Method.Converted<List<ChatText>>();
        public final Template.Method.Converted<Void> addLore = new Template.Method.Converted<Void>();
        public final Template.Method<Void> clearLores = new Template.Method<Void>();
        public final Template.Method<Integer> getDamageValue = new Template.Method<Integer>();
        public final Template.Method<Void> setDamageValue = new Template.Method<Void>();
        public final Template.Method<Boolean> isUnbreakable = new Template.Method<Boolean>();
        public final Template.Method<Void> setUnbreakable = new Template.Method<Void>();
        public final Template.Method<Integer> getRepairCost = new Template.Method<Integer>();
        public final Template.Method<Void> setRepairCost = new Template.Method<Void>();
        public final Template.Method<Integer> getMapColor = new Template.Method<Integer>();
        public final Template.Method<Void> setMapColor = new Template.Method<Void>();
        public final Template.Method<Integer> getLeatherArmorColor = new Template.Method<Integer>();
        public final Template.Method<Integer> getPotionColor = new Template.Method<Integer>();
        public final Template.Method<Integer> getFireworksFlightDuration = new Template.Method<Integer>();
        public final Template.Method<Void> setFireworksFlightDuration = new Template.Method<Void>();
        public final Template.Method.Converted<GameProfileHandle> getSkullProfile = new Template.Method.Converted<GameProfileHandle>();
        public final Template.Method.Converted<Void> setSkullProfile = new Template.Method.Converted<Void>();
        public final Template.Method<Boolean> hasCustomModelDataValue = new Template.Method<Boolean>();
        public final Template.Method<Integer> getCustomModelDataValue = new Template.Method<Integer>();
        public final Template.Method<Void> setCustomModelDataValue = new Template.Method<Void>();
        public final Template.Method<Void> clearCustomModelData = new Template.Method<Void>();
        public final Template.Method<Boolean> hasCustomData = new Template.Method<Boolean>();
        public final Template.Method.Converted<CommonTagCompound> getCustomDataCopy = new Template.Method.Converted<CommonTagCompound>();
        public final Template.Method<CommonTagCompound> getCustomData = new Template.Method<CommonTagCompound>();
        public final Template.Method.Converted<Void> setCustomData = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> updateCustomData = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<ItemStackHandle> cloneItemStack = new Template.Method.Converted<ItemStackHandle>();
        public final Template.Method.Converted<ItemStackHandle> cloneAndSubtract = new Template.Method.Converted<ItemStackHandle>();
        public final Template.Method<ItemStack> toBukkit = new Template.Method<ItemStack>();
        public final Template.Method<Boolean> isMapItem = new Template.Method<Boolean>();
        public final Template.Method<Integer> getMapId = new Template.Method<Integer>();
        public final Template.Method<Void> setMapId = new Template.Method<Void>();
        public final Template.Method<UUID> getMapDisplayDynamicOnlyUUID = new Template.Method<UUID>();
        public final Template.Method<UUID> getMapDisplayUUID = new Template.Method<UUID>();

    }

}

