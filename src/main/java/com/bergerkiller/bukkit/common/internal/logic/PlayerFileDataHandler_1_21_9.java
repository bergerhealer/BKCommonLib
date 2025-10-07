package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.PlayerDataController;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.players.PlayerListHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastConstructor;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Handler for Minecraft 1.21.9 and later
 */
class PlayerFileDataHandler_1_21_9 extends PlayerFileDataHandler {
    private final FastMethod<File> getPlayerFolderOfWorld = new FastMethod<File>();
    private final FastField<Object> playerListFileDataField;
    private final FastMethod<UUID> nameAndId_getId = new FastMethod<>();
    private final FastMethod<String> nameAndId_getName = new FastMethod<>();
    private final FastConstructor<Object> nameAndId_ctor_id_name = new FastConstructor<>();

    public PlayerFileDataHandler_1_21_9() throws Exception {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClassName("net.minecraft.server.level.WorldServer");
        resolver.setVariable("version", Common.MC_VERSION);
        if (Common.IS_PAPERSPIGOT_SERVER) {
            resolver.setVariable("paper", "true");
        }

        {
            MethodDeclaration getPlayerFolderOfWorldMethod = new MethodDeclaration(resolver, SourceDeclaration.preprocess("" +
                    "public java.io.File getPlayerDir() {\n" +
                    "#if paper\n" +
                    "    return new java.io.File(instance.levelStorageAccess.getDimensionPath(instance.dimension()).toFile(), \"playerdata\");\n" +
                    "#else\n" +
                    "    return new java.io.File(instance.convertable.getDimensionPath(instance.dimension()).toFile(), \"playerdata\");\n" +
                    "#endif\n" +
                    "}", resolver));
            getPlayerFolderOfWorld.init(getPlayerFolderOfWorldMethod);
            getPlayerFolderOfWorld.forceInitialization();
        }

        String fieldName = "playerIo";
        Class<?> playerFileDataType = CommonUtil.getClass("net.minecraft.world.level.storage.WorldNBTStorage");
        String realFieldName = Resolver.resolveFieldName(PlayerListHandle.T.getType(), fieldName);
        playerListFileDataField = CommonUtil.unsafeCast(SafeField.create(PlayerListHandle.T.getType(), realFieldName, playerFileDataType).getFastField());

        // For accessing NameAndId input into load/save/etc.
        Class<?> nameAndIdType = CommonUtil.getClass("net.minecraft.server.players.NameAndId");
        if (nameAndIdType == null) {
            throw new UnsupportedOperationException("NameAndId class not found");
        }
        nameAndId_getId.init(Resolver.resolveAndGetDeclaredMethod(nameAndIdType, "id"));
        nameAndId_getName.init(Resolver.resolveAndGetDeclaredMethod(nameAndIdType, "name"));
        nameAndId_ctor_id_name.init(nameAndIdType.getConstructor(UUID.class, String.class));
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
            hook.setHandler(this);
            if (action == HookAction.MOCK) {
                hook.mock(playerFileData);
            } else {
                playerListFileDataField.set(playerList, hook.hook(playerFileData));
            }
        } else if ((hook != null) && (action == HookAction.UNHOOK)) {
            playerListFileDataField.set(playerList, PlayerFileDataHook.unhook(playerFileData));
            hook = new PlayerFileDataHook();
            hook.setHandler(this);
            hook.mock(playerFileData);
        }
        return hook;
    }

    // hooks WorldNBTStorage
    @ClassHook.HookPackage("net.minecraft.server")
    @ClassHook.HookImport("net.minecraft.world.entity.player.EntityHuman")
    @ClassHook.HookImport("net.minecraft.server.players.NameAndId")
    @ClassHook.HookImport("net.minecraft.nbt.NBTTagCompound")
    @ClassHook.HookLoadVariables("com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
    protected static class PlayerFileDataHook extends ClassHook<PlayerFileDataHook> implements Hook {
        public PlayerDataController controller = null;
        private PlayerFileDataHandler_1_21_9 handler = null;

        private void setHandler(PlayerFileDataHandler_1_21_9 handler) {
            this.handler = handler;
            base.handler = handler;
        }

        @HookMethod("public Optional<NBTTagCompound> load(NameAndId nameandid)")
        public java.util.Optional<Object> loadOffline(Object nameAndId) {
            if (this.controller != null) {
                CommonTagCompound compound = null;
                UUID uuid = handler.nameAndId_getId.invoke(nameAndId);
                String name = handler.nameAndId_getName.invoke(nameAndId);

                try {
                    compound = this.controller.onLoadOffline(name, uuid.toString());
                } catch (Throwable t) {
                    Logging.LOGGER.log(Level.SEVERE, "Failed to handle onLoadOffline() on " + this.controller, t);
                }
                return (compound == null) ? java.util.Optional.empty()
                        : java.util.Optional.of(compound.getRawHandle());
            }

            return base.loadOffline(nameAndId);
        }

        @HookMethod("public abstract void save(net.minecraft.world.entity.player.EntityHuman paramEntityHuman)")
        public void save(Object entityHuman) {
            if (this.controller != null) {
                Player player = CommonUtil.tryCast(WrapperConversion.toEntity(entityHuman), Player.class);
                if (player != null) {
                    try {
                        this.controller.onSave(player);
                    } catch (Throwable t) {
                        Logging.LOGGER.log(Level.SEVERE, "Failed to handle onSave() on " + this.controller, t);
                    }
                    return;
                }
            }
            this.base.save(entityHuman);
        }

        @Override
        public CommonTagCompound base_load(HumanEntity human) {
            throw new UnsupportedOperationException("Not supported on 1.21.9+");
        }

        @Override
        public CommonTagCompound base_load_offline(String playerName, String playerUUIDStr) {
            UUID playerUUID;
            try {
                playerUUID = UUID.fromString(playerUUIDStr);
            } catch (IllegalArgumentException ex) {
                return null;
            }

            Object nameAndId = this.handler.nameAndId_ctor_id_name.newInstance(playerUUID, playerName);
            return base.loadOffline(nameAndId)
                    .map(CommonTagCompound::create)
                    .orElse(null);
        }

        @Override
        public void base_save(HumanEntity human) {
            base.save(HandleConversion.toEntityHandle(human));
        }
    }

    public static enum HookAction {
        HOOK, UNHOOK, MOCK, GET
    }
}
