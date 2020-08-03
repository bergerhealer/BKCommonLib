package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.LightEngineThreaded</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.server.LightEngineThreaded")
public abstract class LightEngineThreadedHandle extends Template.Handle {
    /** @See {@link LightEngineThreadedClass} */
    public static final LightEngineThreadedClass T = Template.Class.create(LightEngineThreadedClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static LightEngineThreadedHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static LightEngineThreadedHandle forWorld(World world) {
        return T.forWorld.invoke(world);
    }

    public abstract void schedule(Runnable runnable);
    /**
     * Stores class members for <b>net.minecraft.server.LightEngineThreaded</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class LightEngineThreadedClass extends Template.Class<LightEngineThreadedHandle> {
        public final Template.StaticMethod.Converted<LightEngineThreadedHandle> forWorld = new Template.StaticMethod.Converted<LightEngineThreadedHandle>();

        public final Template.Method<Void> schedule = new Template.Method<Void>();

    }

}

