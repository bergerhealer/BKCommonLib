package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.ClassDeclarationResolver;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Map;

/**
 * Handles conversion from Bukkit DisplaySlot &lt;&gt; id (1.8 - 1.20.1) or
 * DisplaySlot &lt;&gt; NMS DisplaySlot (1.20.2+).
 */
public class ScoreboardDisplaySlotConversion {
    private static final ConversionLogic LOGIC = Template.Class.create(ConversionLogic.class,
            new ClassDeclarationResolver() {
                @Override
                public ClassDeclaration resolveClassDeclaration(String classPath, Class<?> classType) {
                    return null;
                }

                @Override
                public void resolveClassVariables(String classPath, Class<?> classType, Map<String, String> variables) {
                    variables.put("version", CommonBootstrap.initCommonServer().getMinecraftVersion());
                }
            });

    public static void init() {
        LOGIC.forceInitialization();
    }

    @ConverterMethod()
    public static DisplaySlot idToDisplaySlot(int id) {
        return LOGIC.idToSlot(id);
    }

    @ConverterMethod()
    public static int displaySlotToId(DisplaySlot slot) {
        return LOGIC.slotToId(slot);
    }

    @ConverterMethod(input="net.minecraft.world.scores.DisplaySlot", optional=true)
    public static DisplaySlot handleToDisplaySlot(Object nmsDisplaySlot) {
        return LOGIC.handleToSlot(nmsDisplaySlot);
    }

    @ConverterMethod(output="net.minecraft.world.scores.DisplaySlot", optional=true)
    public static Object displaySlotToHandle(DisplaySlot slot) {
        return LOGIC.slotToHandle(slot);
    }

    @Template.Import("org.bukkit.scoreboard.DisplaySlot")
    @Template.Import("org.bukkit.craftbukkit.scoreboard.CraftScoreboardTranslations")
    public static abstract class ConversionLogic extends Template.Class<Template.Handle> {

        /*
         * <ID_TO_SLOT>
         * public static DisplaySlot idToSlot(int id) {
         * #if version >= 1.20.2
         *     net.minecraft.world.scores.DisplaySlot nmsSlot;
         *     nmsSlot = (net.minecraft.world.scores.DisplaySlot) net.minecraft.world.scores.DisplaySlot.BY_ID.apply(id);
         *     return CraftScoreboardTranslations.toBukkitSlot(nmsSlot);
         * #else
         *     return CraftScoreboardTranslations.toBukkitSlot(id);
         * #endif
         * }
         */
        @Template.Generated("%ID_TO_SLOT%")
        public abstract DisplaySlot idToSlot(int id);

        /*
         * <SLOT_TO_ID>
         * public static int slotToId(DisplaySlot slot) {
         * #if version >= 1.20.2
         *     return CraftScoreboardTranslations.fromBukkitSlot(slot).id();
         * #else
         *     return CraftScoreboardTranslations.fromBukkitSlot(slot);
         * #endif
         * }
         */
        @Template.Generated("%SLOT_TO_ID%")
        public abstract int slotToId(DisplaySlot slot);

        /*
         * <HANDLE_TO_SLOT>
         * public static DisplaySlot handleToSlot(Object handle) {
         * #if version >= 1.20.2
         *     return CraftScoreboardTranslations.toBukkitSlot((net.minecraft.world.scores.DisplaySlot) handle);
         * #else
         *     throw new UnsupportedOperationException("No DisplaySlot handle class exists on this version of Minecraft");
         * #endif
         * }
         */
        @Template.Generated("%HANDLE_TO_SLOT%")
        public abstract DisplaySlot handleToSlot(Object handle);

        /*
         * <SLOT_TO_HANDLE>
         * public static Object slotToHandle(DisplaySlot slot) {
         * #if version >= 1.20.2
         *     return CraftScoreboardTranslations.fromBukkitSlot(slot);
         * #else
         *     throw new UnsupportedOperationException("No DisplaySlot handle class exists on this version of Minecraft");
         * #endif
         * }
         */
        @Template.Generated("%SLOT_TO_HANDLE%")
        public abstract Object slotToHandle(DisplaySlot slot);
    }
}
