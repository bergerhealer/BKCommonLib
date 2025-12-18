package com.bergerkiller.bukkit.common.internal.hooks;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.events.PlayerAdvancementProgressEvent;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.advancements.AdvancementHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.InputTypeMap;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private static Optional<String> LOGIC_FAILURE = Optional.empty();
    private static final Handler HANDLER = LogicUtil.tryCreate(() -> {
        if (!CommonCapabilities.HAS_ADVANCEMENTS) {
            return null; // Not supported
        }
        return new Handler();
    }, err -> {
        Logging.LOGGER.log(Level.WARNING, "Failed to hook advancement updates", err);
        LOGIC_FAILURE = Optional.of(err.getMessage());
        return null;
    });

    private Player player;

    public static Optional<String> getAdvancementsInitFailure() {
        return LOGIC_FAILURE;
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
        if (HANDLER == null) {
            return;
        }
        if (!CommonUtil.hasHandlers(PlayerAdvancementProgressEvent.getHandlerList())) {
            return;
        }

        HANDLER.hook(player);
    }

    public static void unhook(Player player) {
        if (HANDLER == null) {
            return;
        }

        HANDLER.unhook(player);
    }

    private static class Handler {
        private final HandlerLogic logic = Template.Class.create(HandlerLogic.class, Common.TEMPLATE_RESOLVER);
        private final InputTypeMap<CriterionDataSwapper> criterionTriggerPlayerDataFields = new InputTypeMap<>();

        public Handler() throws Throwable {
            logic.forceInitialization();
            registerCritereonFields();
        }

        private void registerCritereonFields() throws Throwable {
            // Paper made a patch to store this data in the AdvancementDataPlayer class instead
            // Detect presence of this patch and if it exists, don't register these triggers
            try {
                CommonUtil.getClass("net.minecraft.server.AdvancementDataPlayer").getDeclaredField("criterionData");
                return; // Present. Don't bother.
            } catch (Throwable t) { /* ignore */ }

            // On modern versions all 'critereons' use a main abstract class to track per-player progress in
            if (CommonBootstrap.evaluateMCVersion(">=", "1.15")) {
                if (CommonBootstrap.evaluateMCVersion(">=", "1.17")) {
                    registerCritereonField("CriterionTriggerAbstract", "players");
                } else {
                    registerCritereonField("CriterionTriggerAbstract", "a");
                }
                return;
            }

            // On this old version someone (???) did something horrible involving a map stored in each individual class
            // Such a huge pain in the ass!
            // These are OG 1.12 advancement criterion triggers
            registerCritereonField("CriterionTriggerBredAnimals", "b");
            registerCritereonField("CriterionTriggerBrewedPotion", "b");
            registerCritereonField("CriterionTriggerChangedDimension", "b");
            registerCritereonField("CriterionTriggerConstructBeacon", "b");
            registerCritereonField("CriterionTriggerConsumeItem", "b");
            registerCritereonField("CriterionTriggerCuredZombieVillager", "b");
            registerCritereonField("CriterionTriggerEffectsChanged", "b");
            registerCritereonField("CriterionTriggerEnchantedItem", "b");
            registerCritereonField("CriterionTriggerEnterBlock", "b");
            registerCritereonField("CriterionTriggerEntityHurtPlayer", "b");
            registerCritereonField("CriterionTriggerInventoryChanged", "b");
            registerCritereonField("CriterionTriggerItemDurabilityChanged", "b");
            registerCritereonField("CriterionTriggerKilled", "a");
            registerCritereonField("CriterionTriggerLevitation", "b");
            registerCritereonField("CriterionTriggerLocation", "b");
            registerCritereonField("CriterionTriggerNetherTravel", "b");
            registerCritereonField("CriterionTriggerPlacedBlock", "b");
            registerCritereonField("CriterionTriggerPlayerHurtEntity", "b");
            registerCritereonField("CriterionTriggerRecipeUnlocked", "b");
            registerCritereonField("CriterionTriggerSummonedEntity", "b");
            registerCritereonField("CriterionTriggerTamedAnimal", "b");
            registerCritereonField("CriterionTriggerTick", "b");
            registerCritereonField("CriterionTriggerUsedEnderEye", "b");
            registerCritereonField("CriterionTriggerUsedTotem", "b");
            registerCritereonField("CriterionTriggerVillagerTrade", "b");

            // 1.13 added ones
            if (CommonBootstrap.evaluateMCVersion(">=", "1.13")) {
                registerCritereonField("CriterionTriggerChanneledLightning", "b");
                registerCritereonField("CriterionTriggerFilledBucket", "b");
                registerCritereonField("CriterionTriggerFishingRodHooked", "b");
            }

            // 1.14 added ones
            if (CommonBootstrap.evaluateMCVersion(">=", "1.14")) {
                registerCritereonField("CriterionTriggerShotCrossbow", "b");
                registerCritereonField("CriterionTriggerKilledByCrossbow", "b");
            }
        }

        private void registerCritereonField(String criterionClassName, String fieldName) throws Throwable {

            String fullCriterionClassName;
            if (CommonBootstrap.evaluateMCVersion(">=", "1.21.11")) {
                fullCriterionClassName = "net.minecraft.advancements.criterion." + criterionClassName;
            } else {
                fullCriterionClassName = "net.minecraft.advancements.critereon." + criterionClassName;
            }
            Class<?> type = CommonUtil.getClass(fullCriterionClassName);
            if (type == null) {
                throw new IllegalStateException("Failed to find criterion: " + criterionClassName);
            }

            java.lang.reflect.Field refMapField = Resolver.resolveAndGetDeclaredField(type, fieldName);
            if (!Map.class.isAssignableFrom(refMapField.getType())) {
                throw new IllegalStateException("Criterion field of " + criterionClassName + " field " + fieldName +
                        " is invalid: " + refMapField.getType());
            }
            final FastField<Map<Object, Object>> mapField = new FastField<>(refMapField);

            // Try to see if a sub-class (.a) exists of this same criterion class
            // And if it does, whether this class also contains a field storing AdvancementPlayerData
            // If it does, we also have to modify the values when swapping
            Class<?> valueClassType = CommonUtil.getClass(fullCriterionClassName + "$a");
            final List<BiConsumer<Object, Object>> criterionPlayerDataModifiers;
            if (valueClassType != null) {
                final Class<?> adpType = CommonUtil.getClass("net.minecraft.server.AdvancementDataPlayer");
                criterionPlayerDataModifiers = Stream.of(valueClassType.getDeclaredFields())
                        .filter(f -> !Modifier.isStatic(f.getModifiers()))
                        .filter(f -> f.getType() == adpType)
                        .map(FastField::new)
                        .map(ff -> (BiConsumer<Object, Object>) ff::set)
                        .collect(Collectors.toList());
            } else {
                criterionPlayerDataModifiers = Collections.emptyList();
            }

            // Package it up nicely
            criterionTriggerPlayerDataFields.put(type, (criterion, oldPlayerAdvancements, newPlayerAdvancements) -> {
                Map<Object, Object> map = mapField.get(criterion);
                Object currPlayerData = map.remove(oldPlayerAdvancements);
                if (currPlayerData != null) {
                    for (BiConsumer<Object, Object> modifier : criterionPlayerDataModifiers) {
                        modifier.accept(currPlayerData, newPlayerAdvancements);
                    }
                    map.put(newPlayerAdvancements, currPlayerData);
                }
            });
        }

        public void hook(Player player) {
            Object playerHandle = HandleConversion.toEntityHandle(player);
            Object currAdvancements = logic.getAdvancements(playerHandle);
            if (ClassHook.get(currAdvancements, AdvancementDataPlayerHook.class) == null) {
                AdvancementDataPlayerHook hook = new AdvancementDataPlayerHook();
                hook.player = player;
                Object hookedAdvancements = hook.hook(currAdvancements);
                logic.setAdvancements(playerHandle, hookedAdvancements);
                swapInCriterionTriggers(currAdvancements, hookedAdvancements);
            }
        }

        public void unhook(Player player) {
            Object playerHandle = HandleConversion.toEntityHandle(player);
            Object currAdvancements = logic.getAdvancements(playerHandle);
            if (ClassHook.get(currAdvancements, AdvancementDataPlayerHook.class) != null) {
                Object unhookedAdvancements = ClassHook.unhook(currAdvancements);
                logic.setAdvancements(playerHandle, unhookedAdvancements);
                swapInCriterionTriggers(currAdvancements, unhookedAdvancements);
            }
        }

        private void swapInCriterionTriggers(Object oldPlayerAdvancements, Object newPlayerAdvancements) {
            if (criterionTriggerPlayerDataFields.values().isEmpty()) {
                return;
            }

            for (Object criterionTrigger : logic.getCriterionTriggers()) {
                for (CriterionDataSwapper swapper : criterionTriggerPlayerDataFields.getAll(criterionTrigger.getClass())) {
                    swapper.swap(criterionTrigger, oldPlayerAdvancements, newPlayerAdvancements);
                }
            }
        }
    }

    @FunctionalInterface
    private interface CriterionDataSwapper {
        void swap(Object criterion, Object oldPlayerAdvancements, Object newPlayerAdvancements);
    }

    @Template.Optional
    @Template.InstanceType("net.minecraft.server.AdvancementDataPlayer")
    @Template.Import("net.minecraft.server.level.EntityPlayer")
    /*
     * <PLAYER_ADVANCEMENTS_FIELD>
     * #select version >=
     * #case 1.17:    private final net.minecraft.server.AdvancementDataPlayer advancements;
     * #case 1.14:    private final net.minecraft.server.AdvancementDataPlayer advancements:advancementDataPlayer;
     * #case 1.13.1:  private final net.minecraft.server.AdvancementDataPlayer advancements:cf;
     * #case 1.13:    private final net.minecraft.server.AdvancementDataPlayer advancements:cg;
     * #case else:    private final net.minecraft.server.AdvancementDataPlayer advancements:bY;
     * #endselect
     */
    @Template.Require(declaring="net.minecraft.server.level.EntityPlayer", value="%PLAYER_ADVANCEMENTS_FIELD%")
    public static abstract class HandlerLogic extends Template.Class<Template.Handle> {

        @Template.Generated("public static Object getAdvancements(EntityPlayer nmsEntityPlayer) {\n" +
                            "    return nmsEntityPlayer#advancements;\n" +
                            "}")
        public abstract Object getAdvancements(Object nmsEntityPlayer);

        @Template.Generated("public static void setAdvancements(EntityPlayer nmsEntityPlayer, AdvancementDataPlayer newAdvancements) {\n" +
                            "    nmsEntityPlayer#advancements = newAdvancements;\n" +
                            "}")
        public abstract void setAdvancements(Object nmsEntityPlayer, Object newAdvancements);

        /*
         * <GET_CRITERION_TRIGGERS>
         * public static Iterable<Object> getCriterionTriggers() {
         * #if version >= 1.20.3
         *     return net.minecraft.core.registries.BuiltInRegistries.TRIGGER_TYPES;
         * #elseif version >= 1.18
         *     return net.minecraft.advancements.CriterionTriggers.all();
         * #else
         *     return net.minecraft.advancements.CriterionTriggers.a();
         * #endif
         * }
         */
        @Template.Generated("%GET_CRITERION_TRIGGERS%")
        public abstract Iterable<Object> getCriterionTriggers();
    }
}
