package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.EntityMinecartCommandBlockHandle;
import org.bukkit.Material;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;

import java.util.Arrays;
import java.util.List;

/**
 * A Common Entity implementation for Minecarts with a Command Block
 */
public class CommonMinecartCommandBlock extends CommonMinecart<CommandMinecart> {
    private static final Material _COMBINED_ITEM = CommonCapabilities.MATERIAL_ENUM_CHANGES ?
            Material.getMaterial("COMMAND_BLOCK_MINECART") : Material.getMaterial("COMMAND_MINECART");
    private static final Material _COMMAND_BLOCK_TYPE = CommonCapabilities.MATERIAL_ENUM_CHANGES ?
            Material.getMaterial("COMMAND_BLOCK") : Material.getMaterial("COMMAND");

    public final DataWatcher.EntityItem<String> metaCommand = getDataItem(EntityMinecartCommandBlockHandle.DATA_COMMAND);
    public final DataWatcher.EntityItem<ChatText> metaPreviousOutput = getDataItem(EntityMinecartCommandBlockHandle.DATA_PREVIOUS_OUTPUT);

    public CommonMinecartCommandBlock(CommandMinecart base) {
        super(base);
    }

    @Override
    public List<ItemStack> getBrokenDrops() {
        return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(_COMMAND_BLOCK_TYPE, 1));
    }

    @Override
    public Material getCombinedItem() {
        return _COMBINED_ITEM;
    }

}
