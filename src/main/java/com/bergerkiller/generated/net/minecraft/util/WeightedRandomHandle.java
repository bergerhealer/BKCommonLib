package com.bergerkiller.generated.net.minecraft.util;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.util.WeightedRandom</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.util.WeightedRandom")
public abstract class WeightedRandomHandle extends Template.Handle {
    /** @See {@link WeightedRandomClass} */
    public static final WeightedRandomClass T = Template.Class.create(WeightedRandomClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static WeightedRandomHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.util.WeightedRandom</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WeightedRandomClass extends Template.Class<WeightedRandomHandle> {
    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.util.WeightedRandom.WeightedRandomChoice</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.util.WeightedRandom.WeightedRandomChoice")
    public abstract static class WeightedRandomChoiceHandle extends Template.Handle {
        /** @See {@link WeightedRandomChoiceClass} */
        public static final WeightedRandomChoiceClass T = Template.Class.create(WeightedRandomChoiceClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static WeightedRandomChoiceHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public abstract int getChance();
        public abstract void setChance(int value);
        /**
         * Stores class members for <b>net.minecraft.util.WeightedRandom.WeightedRandomChoice</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class WeightedRandomChoiceClass extends Template.Class<WeightedRandomChoiceHandle> {
            public final Template.Field.Integer chance = new Template.Field.Integer();

        }

    }

}

