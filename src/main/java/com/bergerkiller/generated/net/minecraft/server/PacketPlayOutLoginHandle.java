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
        if (handleInstance == null) return null;
        PacketPlayOutLoginHandle handle = new PacketPlayOutLoginHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public int getPlayerId() {
        return T.playerId.getInteger(instance);
    }

    public void setPlayerId(int value) {
        T.playerId.setInteger(instance, value);
    }

    public boolean isHardcore() {
        return T.hardcore.getBoolean(instance);
    }

    public void setHardcore(boolean value) {
        T.hardcore.setBoolean(instance, value);
    }

    public GameMode getGameMode() {
        return T.gameMode.get(instance);
    }

    public void setGameMode(GameMode value) {
        T.gameMode.set(instance, value);
    }

    public int getDimension() {
        return T.dimension.getInteger(instance);
    }

    public void setDimension(int value) {
        T.dimension.setInteger(instance, value);
    }

    public Difficulty getDifficulty() {
        return T.difficulty.get(instance);
    }

    public void setDifficulty(Difficulty value) {
        T.difficulty.set(instance, value);
    }

    public int getMaxPlayers() {
        return T.maxPlayers.getInteger(instance);
    }

    public void setMaxPlayers(int value) {
        T.maxPlayers.setInteger(instance, value);
    }

    public WorldType getWorldType() {
        return T.worldType.get(instance);
    }

    public void setWorldType(WorldType value) {
        T.worldType.set(instance, value);
    }

    public boolean isUnknown1() {
        return T.unknown1.getBoolean(instance);
    }

    public void setUnknown1(boolean value) {
        T.unknown1.setBoolean(instance, value);
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

