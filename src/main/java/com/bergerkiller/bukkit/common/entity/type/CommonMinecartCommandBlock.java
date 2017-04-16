package com.bergerkiller.bukkit.common.entity.type;

import org.bukkit.Material;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityMinecart;

import java.util.Arrays;
import java.util.List;

/**
 * A Common Entity implementation for Minecarts with a Command Block
 */
public class CommonMinecartCommandBlock extends CommonMinecart<CommandMinecart> {

    public final DataWatcher.EntityItem<String> metaCommand = getDataItem(NMSEntityMinecart.CommandBlock.DATA_COMMAND);
    public final DataWatcher.EntityItem<String> metaPreviousOutput = getDataItem(NMSEntityMinecart.CommandBlock.DATA_PREVIOUS_OUTPUT).translate(ConversionPairs.textChatComponent);

    public CommonMinecartCommandBlock(CommandMinecart base) {
        super(base);
    }

    @Override
    public List<ItemStack> getBrokenDrops() {
        return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(Material.COMMAND, 1));
    }

    @Override
    public Material getCombinedItem() {
        return Material.COMMAND_MINECART;
    }

}
