package com.bergerkiller.bukkit.common.internal.logic;

import java.io.File;

import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.controller.PlayerDataController;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.players.PlayerListHandle;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftServer;

/**
 * Handler for Minecraft 1.16 and later
 */
class PlayerFileDataHandler_1_16 extends PlayerFileDataHandler {
    private final FastMethod<File> getPlayerFolderOfWorld = new FastMethod<File>();
    private final FastField<Object> playerListFileDataField;

    public PlayerFileDataHandler_1_16() {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClassName("net.minecraft.server.level.WorldServer");
        resolver.setVariable("version", Common.MC_VERSION);

        {
            MethodDeclaration getPlayerFolderOfWorldMethod = new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                    "public java.io.File getPlayerDir() {\n" +
                    "#if version >= 1.18\n" +
                    "    return new java.io.File(instance.convertable.getDimensionPath(instance.dimension()).toFile(), \"playerdata\");\n" +
                    "#else\n" +
                    "    return new java.io.File(instance.convertable.a(instance.getDimensionKey()), \"playerdata\");\n" +
                    "#endif\n" +
                    "}", resolver));
            getPlayerFolderOfWorld.init(getPlayerFolderOfWorldMethod);  
        }
        String fieldName = CommonBootstrap.evaluateMCVersion(">=", "1.17") ? "playerIo" : "playerFileData";
        Class<?> playerFileDataType = CommonUtil.getClass("net.minecraft.world.level.storage.WorldNBTStorage");
        String realFieldName = Resolver.resolveFieldName(PlayerListHandle.T.getType(), fieldName);
        playerListFileDataField = CommonUtil.unsafeCast(SafeField.create(PlayerListHandle.T.getType(), realFieldName, playerFileDataType).getFastField());
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public void forceInitialization() {
        getPlayerFolderOfWorld.forceInitialization();
        playerListFileDataField.forceInitialization();
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

    @Override
    public CommonTagCompound migratePlayerData(CommonTagCompound playerProfileData) {
        Object playerList = CBCraftServer.getPlayerList.invoke(Bukkit.getServer());
        return PlayerListHandle.createHandle(playerList).migratePlayerData(playerProfileData);
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
}
