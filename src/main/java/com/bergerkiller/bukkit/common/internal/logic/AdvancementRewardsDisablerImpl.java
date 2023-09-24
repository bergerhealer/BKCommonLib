package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.advancements.AdvancementRewardsHandle;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import org.bukkit.advancement.Advancement;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Temporary sets the advancement reward experience, loot and recipes
 * to none until the advancement is granted. This disables giving an advancement
 * to a Player when handling the AdvancementDone event.
 */
class AdvancementRewardsDisablerImpl extends AdvancementRewardsDisabler {
    private final Set<Method> overridedMethods;

    AdvancementRewardsDisablerImpl() throws Throwable {
        // This is the input parameter to get()
        final Class<?> customFunctionDataType = CommonUtil.getClass("net.minecraft.server.CustomFunctionData");
        if (customFunctionDataType == null) {
            throw new IllegalStateException("CustomFunctionData type not found");
        }

        // This is a possible return type of get()
        final Class<?> customFunctionType = CommonUtil.getClass("net.minecraft.commands.CustomFunction");
        if (customFunctionType == null) {
            throw new IllegalStateException("CustomFunction type not found");
        }

        // Find the get() function of CustomFunction.a
        overridedMethods = ReflectionUtil.getAllMethods(AdvancementRewardsHandle.getNoneFunction().getClass())
                .filter(m -> {
                    int mod = m.getModifiers();
                    return !Modifier.isPrivate(mod) && !Modifier.isStatic(mod) && !Modifier.isFinal(mod);
                })
                .filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0] == customFunctionDataType)
                .filter(m -> m.getReturnType() == java.util.Optional.class || m.getReturnType() == customFunctionType)
                .collect(Collectors.toSet());
        if (overridedMethods.isEmpty()) {
            throw new IllegalStateException("Unable to find CustomFunction get(CustomFunctionData) method");
        }
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public void disableNextGrant(Advancement advancement) {
        AdvancementRewardsHandle rewards = AdvancementRewardsHandle.getRewardsOf(advancement);
        if (rewards == null) {
            return;
        }

        disableNextGrant(rewards);
    }

    private void disableNextGrant(AdvancementRewardsHandle rewards) {
        // Collect rewards that used to be set. If none, skip all this stuff.
        final PreviousRewards previous = new PreviousRewards(rewards);
        if (previous.isNone()) {
            return;
        }

        // Create a hook class of the 'none' reward function where we override
        // the get() method. When that is called, the rewards reached the end.
        // We roll back all experience/loot/recipes/function changes we've made.
        ClassInterceptor interceptor = new ClassInterceptor() {
            @Override
            protected Invoker<?> getCallback(final Method method) {
                if (overridedMethods.contains(method)) {
                    return (instance, args) -> {
                        // Restore all awards
                        previous.reset();

                        // Return no function here
                        if (method.getReturnType() == java.util.Optional.class) {
                            return java.util.Optional.empty();
                        } else {
                            return null; // @Nullable CustomFunction
                        }
                    };
                }
                return null;
            }
        };

        // Install the hooked function and reset all rewards
        rewards.setFunction(interceptor.hook(AdvancementRewardsHandle.getNoneFunction()));
        rewards.setExperience(0);
        rewards.setLoot(new MinecraftKeyHandle[0]);
        rewards.setRecipes(new MinecraftKeyHandle[0]);
    }

    /**
     * Stores the advancement rewards that were set before this was disabled
     */
    private static class PreviousRewards {
        public final AdvancementRewardsHandle liveRewards;
        public final int experience;
        public final MinecraftKeyHandle[] loot;
        public final MinecraftKeyHandle[] recipes;
        public final Object function;

        public PreviousRewards(AdvancementRewardsHandle rewards) {
            this.liveRewards = rewards;
            this.experience = rewards.getExperience();
            this.loot = rewards.getLoot();
            this.recipes = rewards.getRecipes();
            this.function = rewards.getFunction();
        }

        public boolean isNone() {
            return experience == 0 &&
                    loot.length == 0 &&
                    recipes.length == 0 &&
                    function == AdvancementRewardsHandle.getNoneFunction();
        }

        public void reset() {
            liveRewards.setExperience(experience);
            liveRewards.setLoot(loot);
            liveRewards.setRecipes(recipes);
            liveRewards.setFunction(function);
        }
    }
}
