package com.bergerkiller.bukkit.common.internal.logic;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.PlayerDataController;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.reflection.ClassHook;

/**
 * Handles the registration and de-registration of player file data handlers
 */
public abstract class PlayerFileDataHandler {
    public static final PlayerFileDataHandler INSTANCE;

    static {
        if (CommonBootstrap.evaluateMCVersion(">=", "1.16")) {
            INSTANCE = new PlayerFileDataHandler_1_16();
        } else {
            INSTANCE = new PlayerFileDataHandler_1_8_to_1_15_2();
        }
    }

    public abstract PlayerDataController get();
    public abstract Hook hook(PlayerDataController controller);
    public abstract Hook mock(PlayerDataController controller);
    public abstract void unhook(Hook hook, PlayerDataController controller);

    public abstract File getPlayerDataFolder(World world);

    public static interface Hook {
        String[] base_getSeenPlayers();
        CommonTagCompound base_load(HumanEntity human);
        void base_save(HumanEntity human);
    }

    protected static class PlayerFileDataHook extends ClassHook<PlayerFileDataHook> implements PlayerFileDataHandler.Hook {
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
            if (this.controller != null) {
                Player player = CommonUtil.tryCast(WrapperConversion.toEntity(entityHuman), Player.class);
                if (player != null) {
                    CommonTagCompound compound = null;
                    try {
                        compound = this.controller.onLoad(player);
                    } catch (Throwable t) {
                        Logging.LOGGER.log(Level.SEVERE, "Failed to handle onLoad() on " + this.controller, t);
                    }
                    return (compound == null) ? null : compound.getRawHandle();
                }
            }
            return this.base.load(entityHuman);
        }

        @HookMethod("public abstract void save(EntityHuman paramEntityHuman)")
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
