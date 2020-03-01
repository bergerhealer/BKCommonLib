package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;
import java.util.function.IntSupplier;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.LightEngineThreaded</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public abstract class LightEngineThreadedHandle extends Template.Handle {
    /** @See {@link LightEngineThreadedClass} */
    public static final LightEngineThreadedClass T = new LightEngineThreadedClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(LightEngineThreadedHandle.class, "net.minecraft.server.LightEngineThreaded", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static LightEngineThreadedHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static LightEngineThreadedHandle forWorld(World world) {
        return T.forWorld.invoke(world);
    }

    public abstract void schedule(int cx, int cz, IntSupplier ticketLevelSupplier, Object lightenginethreaded_update, Runnable runnable);
    /**
     * Stores class members for <b>net.minecraft.server.LightEngineThreaded</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class LightEngineThreadedClass extends Template.Class<LightEngineThreadedHandle> {
        public final Template.StaticMethod.Converted<LightEngineThreadedHandle> forWorld = new Template.StaticMethod.Converted<LightEngineThreadedHandle>();

        public final Template.Method.Converted<Void> schedule = new Template.Method.Converted<Void>();

    }

}

