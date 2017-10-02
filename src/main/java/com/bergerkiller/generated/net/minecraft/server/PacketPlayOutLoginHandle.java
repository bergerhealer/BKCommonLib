package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.WorldType;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutLogin</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutLoginHandle extends PacketHandle {
    /** @See {@link PacketPlayOutLoginClass} */
    public static final PacketPlayOutLoginClass T = new PacketPlayOutLoginClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutLoginHandle.class, "net.minecraft.server.PacketPlayOutLogin");

    /* ============================================================================== */

    public static PacketPlayOutLoginHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public int getPlayerId() {
        return T.playerId.getInteger(getRaw());
    }

    public void setPlayerId(int value) {
        T.playerId.setInteger(getRaw(), value);
    }

    public boolean isHardcore() {
        return T.hardcore.getBoolean(getRaw());
    }

    public void setHardcore(boolean value) {
        T.hardcore.setBoolean(getRaw(), value);
    }

    public GameMode getGameMode() {
        return T.gameMode.get(getRaw());
    }

    public void setGameMode(GameMode value) {
        T.gameMode.set(getRaw(), value);
    }

    public int getDimension() {
        return T.dimension.getInteger(getRaw());
    }

    public void setDimension(int value) {
        T.dimension.setInteger(getRaw(), value);
    }

    public Difficulty getDifficulty() {
        return T.difficulty.get(getRaw());
    }

    public void setDifficulty(Difficulty value) {
        T.difficulty.set(getRaw(), value);
    }

    public int getMaxPlayers() {
        return T.maxPlayers.getInteger(getRaw());
    }

    public void setMaxPlayers(int value) {
        T.maxPlayers.setInteger(getRaw(), value);
    }

    public WorldType getWorldType() {
        return T.worldType.get(getRaw());
    }

    public void setWorldType(WorldType value) {
        T.worldType.set(getRaw(), value);
    }

    public boolean isUnknown1() {
        return T.unknown1.getBoolean(getRaw());
    }

    public void setUnknown1(boolean value) {
        T.unknown1.setBoolean(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutLogin</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutLoginClass extends Template.Class<PacketPlayOutLoginHandle> {
        public final Template.Field.Integer playerId = new Template.Field.Integer();
        public final Template.Field.Boolean hardcore = new Template.Field.Boolean();
        public final Template.Field.Converted<GameMode> gameMode = new Template.Field.Converted<GameMode>();
        public final Template.Field.Integer dimension = new Template.Field.Integer();
        public final Template.Field.Converted<Difficulty> difficulty = new Template.Field.Converted<Difficulty>();
        public final Template.Field.Integer maxPlayers = new Template.Field.Integer();
        public final Template.Field.Converted<WorldType> worldType = new Template.Field.Converted<WorldType>();
        public final Template.Field.Boolean unknown1 = new Template.Field.Boolean();

    }

}

