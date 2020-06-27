package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.Dimension;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldType;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutRespawn</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PacketPlayOutRespawnHandle extends PacketHandle {
    /** @See {@link PacketPlayOutRespawnClass} */
    public static final PacketPlayOutRespawnClass T = new PacketPlayOutRespawnClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutRespawnHandle.class, "net.minecraft.server.PacketPlayOutRespawn", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static PacketPlayOutRespawnHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract GameMode getPreviousGameMode();
    public abstract void setPreviousGameMode(GameMode gameMode);
    public abstract void setEncryptedWorldSeed(World world);
    public abstract Dimension getDimension();
    public abstract void setDimension(Dimension value);
    public abstract GameMode getGamemode();
    public abstract void setGamemode(GameMode value);
    public abstract WorldType getWorldType();
    public abstract void setWorldType(WorldType value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutRespawn</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutRespawnClass extends Template.Class<PacketPlayOutRespawnHandle> {
        public final Template.Field.Converted<Dimension> dimension = new Template.Field.Converted<Dimension>();
        @Template.Optional
        public final Template.Field.Converted<Difficulty> difficulty = new Template.Field.Converted<Difficulty>();
        public final Template.Field.Converted<GameMode> gamemode = new Template.Field.Converted<GameMode>();
        public final Template.Field.Converted<WorldType> worldType = new Template.Field.Converted<WorldType>();

        public final Template.Method.Converted<GameMode> getPreviousGameMode = new Template.Method.Converted<GameMode>();
        public final Template.Method.Converted<Void> setPreviousGameMode = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> setEncryptedWorldSeed = new Template.Method.Converted<Void>();

    }

}

