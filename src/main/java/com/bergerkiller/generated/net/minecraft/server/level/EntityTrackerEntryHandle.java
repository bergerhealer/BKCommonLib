package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.level.EntityTrackerEntry</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.level.EntityTrackerEntry")
public abstract class EntityTrackerEntryHandle extends Template.Handle {
    /** @See {@link EntityTrackerEntryClass} */
    public static final EntityTrackerEntryClass T = Template.Class.create(EntityTrackerEntryClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityTrackerEntryHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Player convertRawViewer(Object viewer) {
        return T.convertRawViewer.invoker.invoke(null,viewer);
    }

    public abstract int getPlayerViewDistance();
    public abstract Collection<Player> getViewers();
    public abstract Collection<Object> getRawViewers();
    public abstract void clearViewers();
    public abstract boolean addViewerToSet(Player viewer);
    public abstract boolean removeViewerFromSet(Player viewer);
    public abstract EntityTrackerEntryStateHandle getState();
    public abstract EntityHandle getEntity();
    public abstract void setEntity(EntityHandle entity);
    public abstract void scanPlayers(List<Player> playerList);
    public abstract void removeViewer(Player player);
    public abstract void updatePlayer(Player player);
    public abstract void hideForAll();

    @Deprecated
    public static final boolean hasProtocolRotationChanged(float angle1, float angle2) {
        return EntityTrackerEntryStateHandle.hasProtocolRotationChanged(angle1, angle2);
    }

    @Deprecated
    public static final int getProtocolRotation(float angle) {
        return EntityTrackerEntryStateHandle.getProtocolRotation(angle);
    }

    @Deprecated
    public static final float getRotationFromProtocol(int protocol) {
        return EntityTrackerEntryStateHandle.getRotationFromProtocol(protocol);
    }

    @Deprecated
    public void setTimeSinceLocationSync(int time) {
        getState().setTimeSinceLocationSync(time);
    }
    public abstract int getTrackingDistance();
    public abstract void setTrackingDistance(int value);
    /**
     * Stores class members for <b>net.minecraft.server.level.EntityTrackerEntry</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityTrackerEntryClass extends Template.Class<EntityTrackerEntryHandle> {
        public final Template.Field.Integer trackingDistance = new Template.Field.Integer();

        public final Template.StaticMethod<Player> convertRawViewer = new Template.StaticMethod<Player>();

        public final Template.Method<Integer> getPlayerViewDistance = new Template.Method<Integer>();
        public final Template.Method.Converted<Collection<Player>> getViewers = new Template.Method.Converted<Collection<Player>>();
        public final Template.Method<Collection<Object>> getRawViewers = new Template.Method<Collection<Object>>();
        public final Template.Method<Void> clearViewers = new Template.Method<Void>();
        public final Template.Method.Converted<Boolean> addViewerToSet = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Boolean> removeViewerFromSet = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<EntityTrackerEntryStateHandle> getState = new Template.Method.Converted<EntityTrackerEntryStateHandle>();
        @Template.Optional
        public final Template.Method.Converted<Void> setState = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<EntityHandle> getEntity = new Template.Method.Converted<EntityHandle>();
        public final Template.Method.Converted<Void> setEntity = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> scanPlayers = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> removeViewer = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> updatePlayer = new Template.Method.Converted<Void>();
        public final Template.Method<Void> hideForAll = new Template.Method<Void>();

    }

}

