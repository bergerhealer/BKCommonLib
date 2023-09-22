package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutRespawn</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutRespawn")
public abstract class PacketPlayOutRespawnHandle extends PacketHandle {
    /** @see PacketPlayOutRespawnClass */
    public static final PacketPlayOutRespawnClass T = Template.Class.create(PacketPlayOutRespawnClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutRespawnHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract DimensionType getDimensionType();
    public abstract void setDimensionType(DimensionType type);
    public abstract GameMode getGamemode();
    public abstract void setGamemode(GameMode gamemode);
    public abstract ResourceKey<World> getWorldName();
    public abstract void setWorldName(ResourceKey<World> worldType);
    public abstract GameMode getPreviousGameMode();
    public abstract void setPreviousGameMode(GameMode gameMode);
    public abstract void setEncryptedWorldSeed(World world);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutRespawn</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutRespawnClass extends Template.Class<PacketPlayOutRespawnHandle> {
        @Template.Optional
        public final Template.Field.Converted<Difficulty> difficulty = new Template.Field.Converted<Difficulty>();

        public final Template.Method<DimensionType> getDimensionType = new Template.Method<DimensionType>();
        public final Template.Method<Void> setDimensionType = new Template.Method<Void>();
        public final Template.Method<GameMode> getGamemode = new Template.Method<GameMode>();
        public final Template.Method<Void> setGamemode = new Template.Method<Void>();
        public final Template.Method.Converted<ResourceKey<World>> getWorldName = new Template.Method.Converted<ResourceKey<World>>();
        public final Template.Method.Converted<Void> setWorldName = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<GameMode> getPreviousGameMode = new Template.Method.Converted<GameMode>();
        public final Template.Method.Converted<Void> setPreviousGameMode = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> setEncryptedWorldSeed = new Template.Method.Converted<Void>();

    }

}

