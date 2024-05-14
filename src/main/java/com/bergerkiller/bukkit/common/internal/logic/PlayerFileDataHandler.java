package com.bergerkiller.bukkit.common.internal.logic;

import java.io.File;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.controller.PlayerDataController;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;

/**
 * Handles the registration and de-registration of player file data handlers
 */
public abstract class PlayerFileDataHandler implements LazyInitializedObject, LibraryComponent {
    public static final PlayerFileDataHandler INSTANCE = LibraryComponentSelector.forModule(PlayerFileDataHandler.class)
            .addVersionOption(null, "1.15.2", PlayerFileDataHandler_1_8_to_1_15_2::new)
            .addVersionOption("1.16", null, PlayerFileDataHandler_1_16::new)
            .update();

    public abstract PlayerDataController get();
    public abstract Hook hook(PlayerDataController controller);
    public abstract Hook mock(PlayerDataController controller);
    public abstract void unhook(Hook hook, PlayerDataController controller);

    public abstract File getPlayerDataFolder(World world);

    public static interface Hook {
        CommonTagCompound base_load(HumanEntity human);
        void base_save(HumanEntity human);
    }

    @ClassHook.HookPackage("net.minecraft.server")
    @ClassHook.HookLoadVariables("com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
    protected static class PlayerFileDataHook extends ClassHook<PlayerFileDataHook> implements PlayerFileDataHandler.Hook {
        private static final boolean LOAD_RETURNS_OPTIONAL = CommonBootstrap.evaluateMCVersion(">=", "1.20.5");
        public PlayerDataController controller = null;

        @HookMethodCondition("version >= 1.20.5")
        @HookMethod("public abstract java.util.Optional<net.minecraft.nbt.NBTTagCompound> load(net.minecraft.world.entity.player.EntityHuman paramEntityHuman)")
        public java.util.Optional<Object> loadOpt(Object entityHuman) {
            if (this.controller != null) {
                Player player = CommonUtil.tryCast(WrapperConversion.toEntity(entityHuman), Player.class);
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
            return base_load_raw(HandleConversion.toEntityHandle(human))
                    .map(CommonTagCompound::create).orElse(null);
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
}
