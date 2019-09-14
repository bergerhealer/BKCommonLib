package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.PlayerDataController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.generated.net.minecraft.server.IPlayerFileDataHandle;
import com.bergerkiller.generated.net.minecraft.server.PlayerListHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftServer;

public class PlayerFileDataHook extends ClassHook<PlayerFileDataHook> {
    public PlayerDataController controller = null;

    public static PlayerFileDataHook update(HookAction action) {
        PlayerListHandle playerList = PlayerListHandle.createHandle(CBCraftServer.getPlayerList.invoke(Bukkit.getServer()));
        Object fileDataHandle = playerList.getPlayerFileData().getRaw();

        // Get the player file data hook or hook a new one
        PlayerFileDataHook hook = PlayerFileDataHook.get(fileDataHandle, PlayerFileDataHook.class);
        if ((hook == null) && (action != HookAction.UNHOOK)) {
            hook = new PlayerFileDataHook();
            if (action == HookAction.MOCK) {
                hook.mock(fileDataHandle);
            } else {
                playerList.setPlayerFileData(IPlayerFileDataHandle.createHandle(hook.hook(fileDataHandle)));
            }
        } else if ((hook != null) && (action == HookAction.UNHOOK)) {
            playerList.setPlayerFileData(IPlayerFileDataHandle.createHandle(PlayerFileDataHook.unhook(fileDataHandle)));
            hook = new PlayerFileDataHook();
            hook.mock(fileDataHandle);
        }
        return hook;
    }

    public static enum HookAction {
        HOOK, UNHOOK, MOCK
    }

    @HookMethod("public abstract String[] getSeenPlayers()")
    public String[] getSeenPlayers() {
        if (this.controller == null) {
            return this.base.getSeenPlayers();
        } else {
            return this.controller.getSeenPlayers();
        }
    }

    @HookMethod("public abstract NBTTagCompound load(EntityHuman paramEntityHuman)")
    public Object load(Object entityHuman) {
        if (this.controller == null) {
            return this.base.load(entityHuman);
        } else {
            CommonTagCompound compound = null;
            try {
                compound = this.controller.onLoad((HumanEntity) Conversion.toEntity.convert(entityHuman));
            } catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to handle onLoad() on " + this.controller, t);
            }
            return (compound == null) ? null : compound.getRawHandle();
        }
    }

    @HookMethod("public abstract void save(EntityHuman paramEntityHuman)")
    public void save(Object entityHuman) {
        if (this.controller == null) {
            this.base.save(entityHuman);
        } else {
            try {
                this.controller.onSave((HumanEntity) Conversion.toEntity.convert(entityHuman));
            } catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to handle onSave() on " + this.controller, t);
            }
        }
    }

}
