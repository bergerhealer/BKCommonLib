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

    public abstract GameMode getGameMode();
    public abstract DimensionType getDimensionType();
    public abstract GameMode getPreviousGameMode();
    public abstract int getPlayerId();
    public abstract void setPlayerId(int value);
    public abstract boolean isHardcore();
    public abstract void setHardcore(boolean value);
    public abstract int getMaxPlayers();
    public abstract void setMaxPlayers(int value);
    public abstract boolean isReducedDebugInfo();
    public abstract void setReducedDebugInfo(boolean value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutLogin</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutLoginClass extends Template.Class<PacketPlayOutLoginHandle> {
        public final Template.Field.Integer playerId = new Template.Field.Integer();
        public final Template.Field.Boolean hardcore = new Template.Field.Boolean();
        @Template.Optional
        public final Template.Field.Converted<Difficulty> difficulty = new Template.Field.Converted<Difficulty>();
        public final Template.Field.Integer maxPlayers = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer viewDistance = new Template.Field.Integer();
        public final Template.Field.Boolean reducedDebugInfo = new Template.Field.Boolean();

        public final Template.Method.Converted<GameMode> getGameMode = new Template.Method.Converted<GameMode>();
        public final Template.Method.Converted<DimensionType> getDimensionType = new Template.Method.Converted<DimensionType>();
        public final Template.Method.Converted<GameMode> getPreviousGameMode = new Template.Method.Converted<GameMode>();

    }

}

