package com.bergerkiller.generated.net.minecraft.util;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.util.RandomSource</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.util.RandomSource")
public abstract class RandomSourceHandle extends Template.Handle {
    /** @See {@link RandomSourceClass} */
    public static final RandomSourceClass T = Template.Class.create(RandomSourceClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static RandomSourceHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int nextIntUnbounded();
    public abstract int nextInt(int i);
    public abstract long nextLong();
    public abstract boolean nextBoolean();
    public abstract float nextFloat();
    public abstract double nextDouble();
    /**
     * Stores class members for <b>net.minecraft.util.RandomSource</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RandomSourceClass extends Template.Class<RandomSourceHandle> {
        public final Template.Method<Integer> nextIntUnbounded = new Template.Method<Integer>();
        public final Template.Method<Integer> nextInt = new Template.Method<Integer>();
        public final Template.Method<Long> nextLong = new Template.Method<Long>();
        public final Template.Method<Boolean> nextBoolean = new Template.Method<Boolean>();
        public final Template.Method<Float> nextFloat = new Template.Method<Float>();
        public final Template.Method<Double> nextDouble = new Template.Method<Double>();

    }

}

