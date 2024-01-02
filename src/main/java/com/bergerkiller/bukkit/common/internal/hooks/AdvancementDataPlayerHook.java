package com.bergerkiller.bukkit.common.internal.hooks;

import com.bergerkiller.bukkit.common.events.PlayerAdvancementProgressEvent;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.advancements.AdvancementHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

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
    private Player player;

    static {
        PlayerAdvancementProgressEvent.getHandlerList(); // Must be loaded
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
        if (!CommonCapabilities.HAS_ADVANCEMENTS) {
            return;
        }
        if (!CommonUtil.hasHandlers(PlayerAdvancementProgressEvent.getHandlerList())) {
            return;
        }

        EntityPlayerHandle handle = EntityPlayerHandle.fromBukkit(player);
        Object currAdvancements = handle.getAdvancements();
        if (ClassHook.get(currAdvancements, AdvancementDataPlayerHook.class) == null) {
            AdvancementDataPlayerHook hook = new AdvancementDataPlayerHook();
            hook.player = player;
            handle.setAdvancements(hook.hook(currAdvancements));
        }
    }

    public static void unhook(Player player) {
        if (!CommonCapabilities.HAS_ADVANCEMENTS) {
            return;
        }

        EntityPlayerHandle handle = EntityPlayerHandle.fromBukkit(player);
        Object currAdvancements = handle.getAdvancements();
        if (ClassHook.get(currAdvancements, AdvancementDataPlayerHook.class) != null) {
            handle.setAdvancements(ClassHook.unhook(currAdvancements));
        }
    }
}
