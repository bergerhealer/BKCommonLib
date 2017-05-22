package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EntityPlayerHandle extends EntityHumanHandle {
    public static final EntityPlayerClass T = new EntityPlayerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityPlayerHandle.class, "net.minecraft.server.EntityPlayer");


    /* ============================================================================== */

    public static EntityPlayerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityPlayerHandle handle = new EntityPlayerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public double getChunkSyncX() {
        return T.chunkSyncX.getDouble(instance);
    }

    public void setChunkSyncX(double value) {
        T.chunkSyncX.setDouble(instance, value);
    }

    public double getChunkSyncZ() {
        return T.chunkSyncZ.getDouble(instance);
    }

    public void setChunkSyncZ(double value) {
        T.chunkSyncZ.setDouble(instance, value);
    }

    public List<Integer> getRemoveQueue() {
        return T.removeQueue.get(instance);
    }

    public void setRemoveQueue(List<Integer> value) {
        T.removeQueue.set(instance, value);
    }

    public int getPing() {
        return T.ping.getInteger(instance);
    }

    public void setPing(int value) {
        T.ping.setInteger(instance, value);
    }

    public static final class EntityPlayerClass extends Template.Class<EntityPlayerHandle> {
        public final Template.Field.Double chunkSyncX = new Template.Field.Double();
        public final Template.Field.Double chunkSyncZ = new Template.Field.Double();
        public final Template.Field<List<Integer>> removeQueue = new Template.Field<List<Integer>>();
        public final Template.Field.Integer ping = new Template.Field.Integer();

    }
}
