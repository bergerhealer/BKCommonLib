package com.bergerkiller.generated.net.minecraft.world.item;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.IBlockDataHandle;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;

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
    public abstract ChatText getName();
    public abstract void setName(ChatText name);
    public abstract int getDurability();
    public abstract void setDurability(int durability);
    public abstract int getRepairCost();
    public abstract boolean hasName();
    public abstract ItemStackHandle cloneItemStack();
    public abstract void setRepairCost(int cost);
    public abstract boolean hasTag();
    public abstract CommonTagCompound getTag();
    public abstract CommonTagCompound saveToNBT(CommonTagCompound compound);
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
    public abstract CommonTagCompound getTagField();
    public abstract void setTagField(CommonTagCompound value);
    /**
     * Stores class members for <b>net.minecraft.world.item.ItemStack</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ItemStackClass extends Template.Class<ItemStackHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<ItemStackHandle> OPT_EMPTY_ITEM = new Template.StaticField.Converted<ItemStackHandle>();

        public final Template.Field.Integer amountField = new Template.Field.Integer();
        public final Template.Field.Converted<CommonTagCompound> tagField = new Template.Field.Converted<CommonTagCompound>();

        public final Template.StaticMethod.Converted<ItemStackHandle> newInstance = new Template.StaticMethod.Converted<ItemStackHandle>();
        public final Template.StaticMethod.Converted<ItemStackHandle> fromBlockData = new Template.StaticMethod.Converted<ItemStackHandle>();

        @Template.Optional
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();
        public final Template.Method<Object> getItem = new Template.Method<Object>();
        public final Template.Method.Converted<Material> getTypeField = new Template.Method.Converted<Material>();
        public final Template.Method.Converted<ChatText> getName = new Template.Method.Converted<ChatText>();
        public final Template.Method.Converted<Void> setName = new Template.Method.Converted<Void>();
        public final Template.Method<Integer> getDurability = new Template.Method<Integer>();
        public final Template.Method<Void> setDurability = new Template.Method<Void>();
        public final Template.Method<Integer> getRepairCost = new Template.Method<Integer>();
        public final Template.Method<Boolean> hasName = new Template.Method<Boolean>();
        public final Template.Method.Converted<ItemStackHandle> cloneItemStack = new Template.Method.Converted<ItemStackHandle>();
        public final Template.Method<Void> setRepairCost = new Template.Method<Void>();
        public final Template.Method<Boolean> hasTag = new Template.Method<Boolean>();
        public final Template.Method.Converted<CommonTagCompound> getTag = new Template.Method.Converted<CommonTagCompound>();
        public final Template.Method.Converted<CommonTagCompound> saveToNBT = new Template.Method.Converted<CommonTagCompound>();
        public final Template.Method.Converted<ItemStackHandle> cloneAndSubtract = new Template.Method.Converted<ItemStackHandle>();
        public final Template.Method<ItemStack> toBukkit = new Template.Method<ItemStack>();
        public final Template.Method<Boolean> isMapItem = new Template.Method<Boolean>();
        public final Template.Method<Integer> getMapId = new Template.Method<Integer>();
        public final Template.Method<Void> setMapId = new Template.Method<Void>();
        public final Template.Method<UUID> getMapDisplayDynamicOnlyUUID = new Template.Method<UUID>();
        public final Template.Method<UUID> getMapDisplayUUID = new Template.Method<UUID>();

    }

}

