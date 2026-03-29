package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.level.ThreadedLevelLightEngine</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.server.level.ThreadedLevelLightEngine")
public abstract class ThreadedLevelLightEngineHandle extends Template.Handle {
    /** @see ThreadedLevelLightEngineClass */
    public static final ThreadedLevelLightEngineClass T = Template.Class.create(ThreadedLevelLightEngineClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ThreadedLevelLightEngineHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ThreadedLevelLightEngineHandle forWorld(World world) {
        return T.forWorld.invoke(world);
    }

    public abstract void schedule(Runnable runnable);
    /**
     * Stores class members for <b>net.minecraft.server.level.ThreadedLevelLightEngine</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ThreadedLevelLightEngineClass extends Template.Class<ThreadedLevelLightEngineHandle> {
        public final Template.StaticMethod.Converted<ThreadedLevelLightEngineHandle> forWorld = new Template.StaticMethod.Converted<ThreadedLevelLightEngineHandle>();

        public final Template.Method<Void> schedule = new Template.Method<Void>();

    }

}

