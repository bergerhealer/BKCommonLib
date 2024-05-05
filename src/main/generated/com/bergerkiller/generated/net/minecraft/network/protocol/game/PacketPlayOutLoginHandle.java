package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutLogin</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutLogin")
public abstract class PacketPlayOutLoginHandle extends PacketHandle {
    /** @see PacketPlayOutLoginClass */
    public static final PacketPlayOutLoginClass T = Template.Class.create(PacketPlayOutLoginClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutLoginHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getPlayerId();
    public abstract boolean isHardcore();
    public abstract int getMaxPlayers();
    public abstract int getViewDistance();
    public abstract boolean isReducedDebugInfo();
    public abstract GameMode getGameMode();
    public abstract DimensionType getDimensionType();
    public abstract GameMode getPreviousGameMode();
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutLogin</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutLoginClass extends Template.Class<PacketPlayOutLoginHandle> {
        @Template.Optional
        public final Template.Field.Converted<Difficulty> difficulty = new Template.Field.Converted<Difficulty>();

        public final Template.Method<Integer> getPlayerId = new Template.Method<Integer>();
        public final Template.Method<Boolean> isHardcore = new Template.Method<Boolean>();
        public final Template.Method<Integer> getMaxPlayers = new Template.Method<Integer>();
        public final Template.Method<Integer> getViewDistance = new Template.Method<Integer>();
        public final Template.Method<Boolean> isReducedDebugInfo = new Template.Method<Boolean>();
        public final Template.Method.Converted<GameMode> getGameMode = new Template.Method.Converted<GameMode>();
        public final Template.Method.Converted<DimensionType> getDimensionType = new Template.Method.Converted<DimensionType>();
        public final Template.Method.Converted<GameMode> getPreviousGameMode = new Template.Method.Converted<GameMode>();

    }

}

