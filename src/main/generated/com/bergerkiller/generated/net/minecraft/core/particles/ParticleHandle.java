package com.bergerkiller.generated.net.minecraft.core.particles;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.core.particles.Particle</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.core.particles.Particle")
public abstract class ParticleHandle extends Template.Handle {
    /** @See {@link ParticleClass} */
    public static final ParticleClass T = Template.Class.create(ParticleClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ParticleHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Object byName(String name) {
        return T.byName.invoker.invoke(null,name);
    }

    public static List<?> values() {
        return T.values.invoker.invoke(null);
    }

    public abstract boolean hasOptions();
    public abstract String getName();
    /**
     * Stores class members for <b>net.minecraft.core.particles.Particle</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ParticleClass extends Template.Class<ParticleHandle> {
        public final Template.StaticMethod<Object> byName = new Template.StaticMethod<Object>();
        public final Template.StaticMethod<List<?>> values = new Template.StaticMethod<List<?>>();

        public final Template.Method<Boolean> hasOptions = new Template.Method<Boolean>();
        public final Template.Method<String> getName = new Template.Method<String>();

    }

}

