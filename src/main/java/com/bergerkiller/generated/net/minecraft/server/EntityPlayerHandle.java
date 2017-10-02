package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityPlayer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EntityPlayerHandle extends EntityHumanHandle {
    /** @See {@link EntityPlayerClass} */
    public static final EntityPlayerClass T = new EntityPlayerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityPlayerHandle.class, "net.minecraft.server.EntityPlayer");

    /* ============================================================================== */

    public static EntityPlayerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public Object getPlayerConnection() {
        return T.playerConnection.get(getRaw());
    }

    public void setPlayerConnection(Object value) {
        T.playerConnection.set(getRaw(), value);
    }

    public double getChunkSyncX() {
        return T.chunkSyncX.getDouble(getRaw());
    }

    public void setChunkSyncX(double value) {
        T.chunkSyncX.setDouble(getRaw(), value);
    }

    public double getChunkSyncZ() {
        return T.chunkSyncZ.getDouble(getRaw());
    }

    public void setChunkSyncZ(double value) {
        T.chunkSyncZ.setDouble(getRaw(), value);
    }

    public int getPing() {
        return T.ping.getInteger(getRaw());
    }

    public void setPing(int value) {
        T.ping.setInteger(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.EntityPlayer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityPlayerClass extends Template.Class<EntityPlayerHandle> {
        public final Template.Field.Converted<Object> playerConnection = new Template.Field.Converted<Object>();
        public final Template.Field.Double chunkSyncX = new Template.Field.Double();
        public final Template.Field.Double chunkSyncZ = new Template.Field.Double();
        @Template.Optional
        public final Template.Field<List<Integer>> removeQueue = new Template.Field<List<Integer>>();
        public final Template.Field.Integer ping = new Template.Field.Integer();

    }

}

