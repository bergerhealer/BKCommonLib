package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.PlayerDataController;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.server.players.PlayerListHandle;
import com.bergerkiller.generated.net.minecraft.util.ProblemReporterHandle;
import com.bergerkiller.generated.net.minecraft.world.level.storage.ValueInputHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.logging.Level;

/**
 * Handler for Minecraft 1.21.6 and later
 */
class PlayerFileDataHandler_1_21_6 extends PlayerFileDataHandler {
    private final FastMethod<File> getPlayerFolderOfWorld = new FastMethod<File>();
    private final FastMethod<Object> getServerRegistryAccess = new FastMethod<>();
    private final FastField<Object> playerListFileDataField;

    public PlayerFileDataHandler_1_21_6() {
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

        {
            MethodDeclaration getServerRegistryAccessMethod = new MethodDeclaration(resolver, SourceDeclaration.preprocess("" +
                    "public static net.minecraft.core.IRegistryCustom getServerRegistryAccess() {\n" +
                    "    return org.bukkit.craftbukkit.CraftRegistry.getMinecraftRegistry();\n" +
                    "}", resolver));
            getServerRegistryAccess.init(getServerRegistryAccessMethod);
            getServerRegistryAccess.forceInitialization();
        }

        String fieldName = "playerIo";
        Class<?> playerFileDataType = CommonUtil.getClass("net.minecraft.world.level.storage.WorldNBTStorage");
        String realFieldName = Resolver.resolveFieldName(PlayerListHandle.T.getType(), fieldName);
        playerListFileDataField = LogicUtil.unsafeCast(SafeField.create(PlayerListHandle.T.getType(), realFieldName, playerFileDataType).getFastField());
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
    @ClassHook.HookImport("net.minecraft.world.level.storage.ValueInput")
    @ClassHook.HookImport("net.minecraft.world.level.storage.ValueOutput")
    @ClassHook.HookImport("net.minecraft.world.entity.player.EntityHuman")
    @ClassHook.HookImport("net.minecraft.util.ProblemReporter")
    @ClassHook.HookImport("net.minecraft.core.IRegistryCustom")
    @ClassHook.HookImport("net.minecraft.nbt.NBTTagCompound")
    @ClassHook.HookLoadVariables("com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
    protected static class PlayerFileDataHook extends ClassHook<PlayerFileDataHook> implements Hook {
        public PlayerDataController controller = null;
        private PlayerFileDataHandler_1_21_6 handler = null;
        private final ThreadLocal<LocalCallState> activeCallState = new ThreadLocal<>();

        private void setHandler(PlayerFileDataHandler_1_21_6 handler) {
            this.handler = handler;
            base.handler = handler;
        }

        @HookMethodCondition("paper")
        @HookMethod("public Optional<NBTTagCompound> load(String name, String uuid, ProblemReporter problemReporter)")
        public java.util.Optional<Object> loadOfflinePaper(String name, String uuid, Object problemReporter) {
            if (this.controller != null) {
                CommonTagCompound compound = null;
                try {
                    activeCallState.set(new LocalCallState(problemReporter, null));
                    compound = this.controller.onLoadOffline(name, uuid);
                } catch (Throwable t) {
                    Logging.LOGGER.log(Level.SEVERE, "Failed to handle onLoadOffline() on " + this.controller, t);
                } finally {
                    activeCallState.remove();
                }
                return (compound == null) ? java.util.Optional.empty()
                        : java.util.Optional.of(compound.getRawHandle());
            }

            return base.loadOfflinePaper(name, uuid, problemReporter);
        }

        @HookMethodCondition("!paper")
        @HookMethod("public Optional<ValueInput> load(String name, String uuid, ProblemReporter problemreporter, IRegistryCustom registryAccess)")
        public java.util.Optional<Object> loadOfflineSpigot(String name, String uuid, Object problemReporter, Object registryAccess) {
            if (this.controller != null) {
                CommonTagCompound compound = null;
                try {
                    activeCallState.set(new LocalCallState(problemReporter, registryAccess));
                    compound = this.controller.onLoadOffline(name, uuid);
                } catch (Throwable t) {
                    Logging.LOGGER.log(Level.SEVERE, "Failed to handle onLoadOffline() on " + this.controller, t);
                } finally {
                    activeCallState.remove();
                }
                return (compound == null) ? java.util.Optional.empty()
                        : java.util.Optional.of(ValueInputHandle.forNBT(
                                problemReporter, registryAccess, compound).getRaw());
            }

            return base.loadOfflineSpigot(name, uuid, problemReporter, registryAccess);
        }

        @HookMethod("public java.util.Optional<ValueInput> load(EntityHuman entityhuman, ProblemReporter problemreporter)")
        public java.util.Optional<Object> load(Object entityHuman, Object problemReporter) {
            if (this.controller != null) {
                Player player = LogicUtil.tryCast(WrapperConversion.toEntity(entityHuman), Player.class);
                if (player != null) {
                    CommonTagCompound compound = null;
                    try {
                        activeCallState.set(new LocalCallState(problemReporter, null));
                        compound = this.controller.onLoad(player);
                    } catch (Throwable t) {
                        Logging.LOGGER.log(Level.SEVERE, "Failed to handle onLoad() on " + this.controller, t);
                    } finally {
                        activeCallState.remove();
                    }
                    return (compound == null) ? java.util.Optional.empty()
                            : java.util.Optional.of(ValueInputHandle.forNBTOnWorld(
                                    problemReporter, player.getWorld(), compound).getRaw());
                }
            }

            return base.load(entityHuman, problemReporter);
        }

        @HookMethod("public abstract void save(net.minecraft.world.entity.player.EntityHuman paramEntityHuman)")
        public void save(Object entityHuman) {
            if (this.controller != null) {
                Player player = LogicUtil.tryCast(WrapperConversion.toEntity(entityHuman), Player.class);
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
            {
                // Re-use a problem reporter if this is during an active (on-thread) load call
                // This way if a hook onLoad calls super onLoad, it doesn't create a whole new problem reporter
                LocalCallState localCallState = activeCallState.get();
                if (localCallState != null) {
                    return base_load_with_reporter(human, localCallState.problemReporter);
                }
            }

            ProblemReporterHandle problemReporter = ProblemReporterHandle.createScoped();
            try {
                return base_load_with_reporter(human, problemReporter.getRaw());
            } finally {
                problemReporter.close();
            }
        }

        @Override
        public CommonTagCompound base_load_offline(String playerName, String playerUUID) {
            {
                // Re-use a problem reporter / registry access if this is during an active (on-thread) load call
                // This way if a hook onLoad calls super onOfflineLoad, it doesn't create a whole new problem reporter
                // The registryAccess field might be null, then we use the server registry access if needed
                LocalCallState localCallState = activeCallState.get();
                if (localCallState != null) {
                    return base_load_offline_with_reporter(playerName, playerUUID,
                            localCallState.problemReporter, localCallState.registryAccess);
                }
            }

            ProblemReporterHandle problemReporter = ProblemReporterHandle.createScoped();
            try {
                return base_load_offline_with_reporter(playerName, playerUUID, problemReporter.getRaw(), null);
            } finally {
                problemReporter.close();
            }
        }

        @Override
        public void base_save(HumanEntity human) {
            base.save(HandleConversion.toEntityHandle(human));
        }

        private CommonTagCompound base_load_with_reporter(HumanEntity human, Object problemReporter) {
            return base.load(HandleConversion.toEntityHandle(human), problemReporter)
                    .map(ValueInputHandle::createHandle)
                    .map(ValueInputHandle::asNBT)
                    .orElse(null);
        }

        private CommonTagCompound base_load_offline_with_reporter(String name, String uuid, Object problemReporter, Object registryAccess) {
            if (Common.IS_PAPERSPIGOT_SERVER) {
                return base.loadOfflinePaper(name, uuid, problemReporter)
                        .map(ValueInputHandle::createHandle)
                        .map(ValueInputHandle::asNBT)
                        .orElse(null);
            } else {
                if (registryAccess == null) {
                    registryAccess = handler.getServerRegistryAccess.invoke(null);
                }

                return base.loadOfflineSpigot(name, uuid, problemReporter, registryAccess)
                        .map(ValueInputHandle::createHandle)
                        .map(ValueInputHandle::asNBT)
                        .orElse(null);
            }
        }

        private static class LocalCallState {
            public final Object problemReporter;
            public final Object registryAccess;

            public LocalCallState(Object problemReporter, Object registryAccess) {
                this.problemReporter = problemReporter;
                this.registryAccess = registryAccess;
            }
        }
    }

    public static enum HookAction {
        HOOK, UNHOOK, MOCK, GET
    }
}
