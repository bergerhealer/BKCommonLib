package com.bergerkiller.bukkit.common.controller;

import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;

import net.minecraft.server.v1_8_R1.DedicatedPlayerList;
import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.IPlayerFileData;
import net.minecraft.server.v1_8_R1.NBTTagCompound;

/**
 * A controller for dealing with player data loading and saving. To hook it up
 * to the server, call {@link #assign()}.
 */
public class PlayerDataController {

    private IPlayerFileData base;

    public String[] getSeenPlayers() {
        return base.getSeenPlayers();
    }

    /**
     * Called when the entity data for a human entity has to be loaded By
     * default, this method redirects to the underlying implementation.
     *
     * @param humanEntity to load
     * @return the loaded data
     */
    public CommonTagCompound onLoad(HumanEntity humanEntity) {
        return (CommonTagCompound) CommonTag.create(base.load(CommonNMS.getNative(humanEntity)));
    }

    /**
     * Called when the entity data of a human entity has to be saved. By
     * default, this method redirects to the underlying implementation.
     *
     * @param humanEntity to save
     */
    public void onSave(HumanEntity humanEntity) {
        base.save(CommonNMS.getNative(humanEntity));
    }

    /**
     * Assigns this PlayerDataController to the server
     */
    public void assign() {
        if (this.base != null) {
            // Already assigned - ignore
            return;
        }
        DedicatedPlayerList playerList = CommonNMS.getPlayerList();
        this.base = playerList.playerFileData;
        playerList.playerFileData = new Translator(this);
    }

    /**
     * Obtains the Player Data Controller currently assigned to the server
     *
     * @return the currently assigned Player Data Controller
     */
    public static PlayerDataController get() {
        final IPlayerFileData base = CommonNMS.getPlayerList().playerFileData;
        final PlayerDataController controller;
        if (base instanceof Translator) {
            controller = ((Translator) base).controller;
        } else {
            controller = new PlayerDataController();
            controller.base = base;
        }
        return controller;
    }

    private static final class Translator implements IPlayerFileData {

        private final PlayerDataController controller;

        public Translator(PlayerDataController controller) {
            this.controller = controller;
        }

        @Override
        public String[] getSeenPlayers() {
            return this.controller.getSeenPlayers();
        }

        @Override
        public NBTTagCompound load(EntityHuman arg0) {
            return (NBTTagCompound) this.controller.onLoad((HumanEntity) Conversion.toEntity.convert(arg0)).getHandle();
        }

        @Override
        public void save(EntityHuman arg0) {
            this.controller.onSave((HumanEntity) Conversion.toEntity.convert(arg0));
        }
    }
}
