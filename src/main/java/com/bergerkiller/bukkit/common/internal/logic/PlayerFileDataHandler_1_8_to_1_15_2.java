package com.bergerkiller.bukkit.common.internal.logic;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.PlayerDataController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.PlayerListHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftServer;

/**
 * Handler for Minecraft 1.8 to Minecraft 1.15.2
 */
public class PlayerFileDataHandler_1_8_to_1_15_2 extends PlayerFileDataHandler {
    private final FastMethod<File> getPlayerFolderOfWorld = new FastMethod<File>();
    private final SafeField<Object> playerListFileDataField;

    public PlayerFileDataHandler_1_8_to_1_15_2() {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(WorldServerHandle.T.getType());

        {
            MethodDeclaration getPlayerFolderOfWorldMethod = new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                    "public static java.io.File getPlayerDir() {\n" +
                    "    return ((WorldNBTStorage) instance.getDataManager()).getPlayerDir();\n" +
                    "}"));
            getPlayerFolderOfWorld.init(getPlayerFolderOfWorldMethod);  
        }

        Class<?> playerFileDataType = CommonUtil.getNMSClass("IPlayerFileData");
        playerListFileDataField = CommonUtil.unsafeCast(SafeField.create(PlayerListHandle.T.getType(), "playerFileData", playerFileDataType));
    }

    @Override
    public PlayerDataController get() {
        PlayerFileDataHook hook = update(HookAction.GET);
        return (hook == null) ? null : hook.controller;
    }

    @Override
    public Hook hook(PlayerDataController controller) {
        PlayerFileDataHook hook = update(HookAction.HOOK);
        if (hook != null) {
            hook.controller = controller;
        }
        return hook;
    }

    @Override
    public Hook mock(PlayerDataController controller) {
        return update(HookAction.MOCK);
    }

    @Override
    public void unhook(Hook hook, PlayerDataController controller) {
        if (hook instanceof PlayerFileDataHook) {
            PlayerFileDataHook p_hook = (PlayerFileDataHook) hook;
            if (p_hook.controller == controller) {
                update(HookAction.UNHOOK);
            }
        }
    }

    @Override
    public File getPlayerDataFolder(World world) {
        return getPlayerFolderOfWorld.invoke(HandleConversion.toWorldHandle(world));
    }

    public PlayerFileDataHook update(HookAction action) {
        Object playerList = CBCraftServer.getPlayerList.invoke(Bukkit.getServer());
        Object playerFileData = playerListFileDataField.get(playerList);

        // Get the player file data hook or hook a new one
        PlayerFileDataHook hook = PlayerFileDataHook.get(playerFileData, PlayerFileDataHook.class);
        if (action == HookAction.GET) {
            return hook;
        } else if ((hook == null) && (action != HookAction.UNHOOK)) {
            hook = new PlayerFileDataHook();
            if (action == HookAction.MOCK) {
                hook.mock(playerFileData);
            } else {
                playerListFileDataField.set(playerList, hook.hook(playerFileData));
            }
        } else if ((hook != null) && (action == HookAction.UNHOOK)) {
            playerListFileDataField.set(playerList, PlayerFileDataHook.unhook(playerFileData));
            hook = new PlayerFileDataHook();
            hook.mock(playerFileData);
        }
        return hook;
    }

    public static enum HookAction {
        HOOK, UNHOOK, MOCK, GET
    }

    private static class PlayerFileDataHook extends ClassHook<PlayerFileDataHook> implements PlayerFileDataHandler.Hook {
        public PlayerDataController controller = null;

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

        @Override
        public String[] base_getSeenPlayers() {
            return base.getSeenPlayers();
        }

        @Override
        public CommonTagCompound base_load(HumanEntity human) {
            return CommonTagCompound.create(base.load(HandleConversion.toEntityHandle(human)));
        }

        @Override
        public void base_save(HumanEntity human) {
            base.save(HandleConversion.toEntityHandle(human));
        }
    }
}
