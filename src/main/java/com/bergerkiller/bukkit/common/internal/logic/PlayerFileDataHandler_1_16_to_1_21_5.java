package com.bergerkiller.bukkit.common.internal.logic;

import java.io.File;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.reflection.ClassHook;
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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

/**
 * Handler for Minecraft 1.16 and later
 */
class PlayerFileDataHandler_1_16_to_1_21_5 extends PlayerFileDataHandler {
    private final FastMethod<File> getPlayerFolderOfWorld = new FastMethod<File>();
    private final FastField<Object> playerListFileDataField;

    public PlayerFileDataHandler_1_16_to_1_21_5() {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClassName("net.minecraft.server.level.WorldServer");
        resolver.setVariable("version", Common.MC_VERSION);
        if (Common.IS_PAPERSPIGOT_SERVER) {
            resolver.setVariable("paper", "true");
        }

        {
            MethodDeclaration getPlayerFolderOfWorldMethod = new MethodDeclaration(resolver, SourceDeclaration.preprocess("" +
                    "public java.io.File getPlayerDir() {\n" +
                    "#if version > 1.21.4 && paper\n" +
                    "    return new java.io.File(instance.levelStorageAccess.getDimensionPath(instance.dimension()).toFile(), \"playerdata\");\n" +
                    "#elseif version == 1.21.4 && paper && exists net.minecraft.server.level.WorldServer public final net.minecraft.world.level.storage.Convertable.ConversionSession levelStorageAccess;\n" +
                    "    return new java.io.File(instance.levelStorageAccess.getDimensionPath(instance.dimension()).toFile(), \"playerdata\");\n" +
                    "#elseif version >= 1.18\n" +
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

    // hooks WorldNBTStorage
    @ClassHook.HookPackage("net.minecraft.server")
    @ClassHook.HookLoadVariables("com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
    protected static class PlayerFileDataHook extends ClassHook<PlayerFileDataHook> implements PlayerFileDataHandler.Hook {
        private static final boolean LOAD_RETURNS_OPTIONAL = CommonBootstrap.evaluateMCVersion(">=", "1.20.5");
        private static final boolean HAS_OFFLINE_LOAD = CommonBootstrap.evaluateMCVersion(">=", "1.20.5");
        public PlayerDataController controller = null;

        @HookMethodCondition("version >= 1.20.5")
        @HookMethod("public java.util.Optional<net.minecraft.nbt.NBTTagCompound> load(String name, String uuid)")
        public java.util.Optional<Object> loadOfflineOpt(String name, String uuid) {
            if (this.controller != null) {
                CommonTagCompound compound = null;
                try {
                    compound = this.controller.onLoadOffline(name, uuid);
                } catch (Throwable t) {
                    Logging.LOGGER.log(Level.SEVERE, "Failed to handle onLoadOffline() on " + this.controller, t);
                }
                return (compound == null) ? java.util.Optional.empty()
                        : java.util.Optional.of(compound.getRawHandle());
            }
            return base.loadOfflineOpt(name, uuid);
        }

        @HookMethodCondition("version >= 1.20.5")
        @HookMethod("public abstract java.util.Optional<net.minecraft.nbt.NBTTagCompound> load(net.minecraft.world.entity.player.EntityHuman paramEntityHuman)")
        public java.util.Optional<Object> loadOpt(Object entityHuman) {
            if (this.controller != null) {
                Player player = LogicUtil.tryCast(WrapperConversion.toEntity(entityHuman), Player.class);
                if (player != null) {
                    CommonTagCompound compound = null;
                    try {
                        compound = this.controller.onLoad(player);
                    } catch (Throwable t) {
                        Logging.LOGGER.log(Level.SEVERE, "Failed to handle onLoad() on " + this.controller, t);
                    }
                    return (compound == null) ? java.util.Optional.empty()
                            : java.util.Optional.of(compound.getRawHandle());
                }
            }
            return base_load_raw(entityHuman);
        }

        @HookMethodCondition("version < 1.20.5")
        @HookMethod("public abstract net.minecraft.nbt.NBTTagCompound load(net.minecraft.world.entity.player.EntityHuman paramEntityHuman)")
        public Object load(Object entityHuman) {
            return this.loadOpt(entityHuman).orElse(null);
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
            return base_load_raw(HandleConversion.toEntityHandle(human))
                    .map(CommonTagCompound::create).orElse(null);
        }

        @Override
        public CommonTagCompound base_load_offline(String playerName, String playerUUID) {
            if (HAS_OFFLINE_LOAD) {
                return base.loadOfflineOpt(playerName, playerUUID)
                        .map(CommonTagCompound::create).orElse(null);
            } else {
                throw new UnsupportedOperationException("Not supported on this version of Minecraft");
            }
        }

        private java.util.Optional<Object> base_load_raw(Object entityHuman) {
            if (LOAD_RETURNS_OPTIONAL) {
                return base.loadOpt(entityHuman);
            } else {
                return java.util.Optional.ofNullable(base.load(entityHuman));
            }
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
