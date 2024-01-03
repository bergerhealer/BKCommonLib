package com.bergerkiller.bukkit.common.internal.hooks;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.events.PlayerAdvancementProgressEvent;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.advancements.AdvancementHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.logging.Level;

/**
 * This is hooked when players join the server and the advancement pre event
 * has registered listeners, to hook award() and cancel this awarding if the
 * event is cancelled.<br>
 * <br>
 * Note: This is used by MyWorlds to disable advancements on particular worlds.
 */
@ClassHook.HookPackage("net.minecraft.server")
@ClassHook.HookImport("net.minecraft.advancements.AdvancementHolder")
@ClassHook.HookImport("net.minecraft.advancements.Advancement")
@ClassHook.HookLoadVariables("com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
public class AdvancementDataPlayerHook extends ClassHook<AdvancementDataPlayerHook> {
    private static final FastField<Object> advancementsField = new FastField<>();
    private Player player;

    static {
        PlayerAdvancementProgressEvent.getHandlerList(); // Must be loaded

        if (CommonCapabilities.HAS_ADVANCEMENTS) {
            try {
                final String name;
                if (CommonBootstrap.evaluateMCVersion(">=", "1.17")) {
                    name = "advancements";
                } else if (CommonBootstrap.evaluateMCVersion(">=", "1.14")) {
                    name = "advancementDataPlayer";
                } else if (CommonBootstrap.evaluateMCVersion(">=", "1.13.1")) {
                    name = "cf";
                } else if (CommonBootstrap.evaluateMCVersion(">=", "1.13")) {
                    name = "cg";
                } else {
                    name = "bY";
                }

                Class<?> advancementsDataPlayerClass = CommonUtil.getClass("net.minecraft.server.AdvancementDataPlayer");
                if (advancementsDataPlayerClass == null) {
                    throw new UnsupportedOperationException("AdvancementDataPlayer not found");
                }

                Field f = Resolver.resolveAndGetDeclaredField(EntityPlayerHandle.T.getType(), name);
                if (f.getType() != advancementsDataPlayerClass) {
                    throw new UnsupportedOperationException("Player advancements field incompatible: " + f.getType().getName());
                }

                advancementsField.init(f);
            } catch (Throwable t) {
                Logging.LOGGER.log(Level.WARNING, "Failed to identify player advancements", t);
                advancementsField.initUnavailable("Error: " + t.toString());
            }
        }
    }

    public static Optional<String> getAdvancementsInitFailure() {
        return (CommonCapabilities.HAS_ADVANCEMENTS && !advancementsField.isAvailable())
                ? Optional.of(advancementsField.getDescription()) : Optional.empty();
    }

    @HookMethodCondition("version < 1.18")
    @HookMethod("public boolean grantCriteria(Advancement advancement, String s)")
    public boolean award_pre_1_18(Object rawAdvancement, String s) {
        Advancement advancement = AdvancementHandle.toBukkit(rawAdvancement);
        return fireEvent(advancement, s) && base.award_pre_1_18(rawAdvancement, s);
    }

    @HookMethodCondition("version >= 1.18 && version < 1.20.2")
    @HookMethod("public boolean award(Advancement advancement, String s)")
    public boolean award_1_18_to_1_20_1(Object rawAdvancement, String s) {
        Advancement advancement = AdvancementHandle.toBukkit(rawAdvancement);
        return fireEvent(advancement, s) && base.award_1_18_to_1_20_1(rawAdvancement, s);
    }

    @HookMethodCondition("version >= 1.20.2")
    @HookMethod("public boolean award(AdvancementHolder advancement, String s)")
    public boolean award_1_20_2(Object rawAdvancementHolder, String s) {
        Advancement advancement = AdvancementHandle.toBukkit(rawAdvancementHolder);
        return fireEvent(advancement, s) && base.award_1_20_2(rawAdvancementHolder, s);
    }

    private boolean fireEvent(Advancement advancement, String criteria) {
        PlayerAdvancementProgressEvent event = new PlayerAdvancementProgressEvent(player, advancement, criteria);
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled();
    }

    public static void hook(Player player) {
        if (!advancementsField.isAvailable()) {
            return;
        }
        if (!CommonUtil.hasHandlers(PlayerAdvancementProgressEvent.getHandlerList())) {
            return;
        }

        Object playerHandle = HandleConversion.toEntityHandle(player);
        Object currAdvancements = advancementsField.get(playerHandle);
        if (ClassHook.get(currAdvancements, AdvancementDataPlayerHook.class) == null) {
            AdvancementDataPlayerHook hook = new AdvancementDataPlayerHook();
            hook.player = player;
            advancementsField.set(playerHandle, hook.hook(currAdvancements));
        }
    }

    public static void unhook(Player player) {
        if (!advancementsField.isAvailable()) {
            return;
        }

        Object playerHandle = HandleConversion.toEntityHandle(player);
        Object currAdvancements = advancementsField.get(playerHandle);
        if (ClassHook.get(currAdvancements, AdvancementDataPlayerHook.class) != null) {
            advancementsField.set(playerHandle, ClassHook.unhook(currAdvancements));
        }
    }
}
