package com.bergerkiller.bukkit.common.internal.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.controller.PlayerDataController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.generated.net.minecraft.server.PlayerListHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftServer;

public class PlayerFileDataHook extends ClassHook<PlayerFileDataHook> {
    public PlayerDataController controller = null;

    public static PlayerFileDataHook update(HookAction action) {
        PlayerListHandle playerList = PlayerListHandle.createHandle(CBCraftServer.getPlayerList.invoke(Bukkit.getServer()));

        // Get the player file data hook or hook a new one
        PlayerFileDataHook hook = PlayerFileDataHook.get(playerList.getPlayerFileData(), PlayerFileDataHook.class);
        if ((hook == null) && (action != HookAction.UNHOOK)) {
            hook = new PlayerFileDataHook();
            if (action == HookAction.MOCK) {
                hook.mock(playerList.getPlayerFileData());
            } else {
                playerList.setPlayerFileData(hook.hook(playerList.getPlayerFileData()));
            }
        } else if ((hook != null) && (action == HookAction.UNHOOK)) {
            playerList.setPlayerFileData(PlayerFileDataHook.unhook(playerList.getPlayerFileData()));
            hook = new PlayerFileDataHook();
            hook.mock(playerList.getPlayerFileData());
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
            return this.controller.onLoad((HumanEntity) Conversion.toEntity.convert(entityHuman)).getHandle();
        }
    }

    @HookMethod("public abstract void save(EntityHuman paramEntityHuman)")
    public void save(Object entityHuman) {
        if (this.controller == null) {
            this.base.save(entityHuman);
        } else {
            this.controller.onSave((HumanEntity) Conversion.toEntity.convert(entityHuman));
        }
    }

}
