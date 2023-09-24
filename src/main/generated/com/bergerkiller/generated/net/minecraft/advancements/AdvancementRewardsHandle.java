package com.bergerkiller.generated.net.minecraft.advancements;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import org.bukkit.advancement.Advancement;

/**
 * Instance wrapper handle for type <b>net.minecraft.advancements.AdvancementRewards</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.advancements.AdvancementRewards")
public abstract class AdvancementRewardsHandle extends Template.Handle {
    /** @see AdvancementRewardsClass */
    public static final AdvancementRewardsClass T = Template.Class.create(AdvancementRewardsClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static AdvancementRewardsHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Object getNoneFunction() {
        return T.getNoneFunction.invoker.invoke(null);
    }

    public static AdvancementRewardsHandle getRewardsOf(Advancement advancement) {
        return T.getRewardsOf.invoke(advancement);
    }

    public abstract int getExperience();
    public abstract void setExperience(int value);
    public abstract MinecraftKeyHandle[] getLoot();
    public abstract void setLoot(MinecraftKeyHandle[] value);
    public abstract MinecraftKeyHandle[] getRecipes();
    public abstract void setRecipes(MinecraftKeyHandle[] value);
    public abstract Object getFunction();
    public abstract void setFunction(Object value);
    /**
     * Stores class members for <b>net.minecraft.advancements.AdvancementRewards</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class AdvancementRewardsClass extends Template.Class<AdvancementRewardsHandle> {
        public final Template.Field.Integer experience = new Template.Field.Integer();
        public final Template.Field.Converted<MinecraftKeyHandle[]> loot = new Template.Field.Converted<MinecraftKeyHandle[]>();
        public final Template.Field.Converted<MinecraftKeyHandle[]> recipes = new Template.Field.Converted<MinecraftKeyHandle[]>();
        public final Template.Field.Converted<Object> function = new Template.Field.Converted<Object>();

        public final Template.StaticMethod<Object> getNoneFunction = new Template.StaticMethod<Object>();
        public final Template.StaticMethod.Converted<AdvancementRewardsHandle> getRewardsOf = new Template.StaticMethod.Converted<AdvancementRewardsHandle>();

    }

}

