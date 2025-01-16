package com.bergerkiller.generated.org.bukkit;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import org.bukkit.Location;

/**
 * Instance wrapper handle for type <b>org.bukkit.World</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.World")
public abstract class WorldHandle extends Template.Handle {
    /** @see WorldClass */
    public static final WorldClass T = Template.Class.create(WorldClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static WorldHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean hasFeatureFlag(String featureFlagName);
    public abstract void playSound(Location loc, MinecraftKeyHandle sound, float volume, float pitch);
    public abstract int getMinHeight();
    public abstract void setClearWeatherDuration(int duration);
    /**
     * Stores class members for <b>org.bukkit.World</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WorldClass extends Template.Class<WorldHandle> {
        public final Template.Method<Boolean> hasFeatureFlag = new Template.Method<Boolean>();
        public final Template.Method<Void> playSound = new Template.Method<Void>();
        public final Template.Method<Integer> getMinHeight = new Template.Method<Integer>();
        public final Template.Method<Void> setClearWeatherDuration = new Template.Method<Void>();

    }

}

