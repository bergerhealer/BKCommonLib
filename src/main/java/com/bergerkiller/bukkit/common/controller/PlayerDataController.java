package com.bergerkiller.bukkit.common.controller;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.hooks.PlayerFileDataHook;
import com.bergerkiller.bukkit.common.internal.hooks.PlayerFileDataHook.HookAction;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;

import org.bukkit.entity.HumanEntity;

/**
 * A controller for dealing with player data loading and saving. To hook it up
 * to the server, call {@link #assign()}.
 */
public class PlayerDataController {
    private PlayerFileDataHook hook = null;

    public String[] getSeenPlayers() {
        return hook.base.getSeenPlayers();
    }

    /**
     * Called when the entity data for a human entity has to be loaded By
     * default, this method redirects to the underlying implementation.
     *
     * @param humanEntity to load
     * @return the loaded data
     */
    public CommonTagCompound onLoad(HumanEntity humanEntity) {
        return CommonTagCompound.create(hook.base.load(Conversion.toEntityHandle.convert(humanEntity)));
    }

    /**
     * Called when the entity data of a human entity has to be saved. By
     * default, this method redirects to the underlying implementation.
     *
     * @param humanEntity to save
     */
    public void onSave(HumanEntity humanEntity) {
        hook.base.save(Conversion.toEntityHandle.convert(humanEntity));
    }

    /**
     * Assigns this PlayerDataController to the server
     */
    public void assign() {
        this.hook = PlayerFileDataHook.update(HookAction.HOOK);
        this.hook.controller = this;
    }

    /**
     * Detaches this PlayerDataController from the server; it will no longer be used
     */
    public void detach() {
        if (this.hook != null && this.hook.controller == this) {
            this.hook = PlayerFileDataHook.update(HookAction.UNHOOK);
        }
    }

    /**
     * Obtains the Player Data Controller currently assigned to the server.
     * If no custom controller is assigned, a default instance is returned that allows
     * interaction with the default controller.
     *
     * @return the currently assigned Player Data Controller
     */
    public static PlayerDataController get() {
        PlayerFileDataHook hook =  PlayerFileDataHook.update(HookAction.MOCK);
        if (hook.controller == null) {
            PlayerDataController controller = new PlayerDataController();
            controller.hook = hook;
            return controller;
        } else {
            return hook.controller;
        }
    }

}
