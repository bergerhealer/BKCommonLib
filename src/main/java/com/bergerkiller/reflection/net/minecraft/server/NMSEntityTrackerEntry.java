package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeDirectField;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * <b>Deprecated: </b>Please move on to using {@link EntityTrackerEntryHandle} instead
 */
@Deprecated
public class NMSEntityTrackerEntry {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityTrackerEntry");

    public static final TranslatorFieldAccessor<Entity> tracker = T.nextField("private final Entity tracker").translate(DuplexConversion.entity);

    public static final FieldAccessor<Integer> viewDistance = EntityTrackerEntryHandle.T.viewDistance.toFieldAccessor();
    public static final FieldAccessor<Integer> updateInterval = EntityTrackerEntryHandle.T.updateInterval.toFieldAccessor();

    public static final FieldAccessor<Double> xVel = EntityTrackerEntryHandle.T.xVel.toFieldAccessor();
    public static final FieldAccessor<Double> yVel = EntityTrackerEntryHandle.T.yVel.toFieldAccessor();
    public static final FieldAccessor<Double> zVel = EntityTrackerEntryHandle.T.zVel.toFieldAccessor();

    public static final FieldAccessor<Integer> tickCounter = EntityTrackerEntryHandle.T.tickCounter.toFieldAccessor();

    public static final FieldAccessor<Double> prevX = EntityTrackerEntryHandle.T.prevX.toFieldAccessor();
    public static final FieldAccessor<Double> prevY = EntityTrackerEntryHandle.T.prevY.toFieldAccessor();
    public static final FieldAccessor<Double> prevZ = EntityTrackerEntryHandle.T.prevZ.toFieldAccessor();

    public static final FieldAccessor<Boolean> synched = EntityTrackerEntryHandle.T.synched.toFieldAccessor();
    public static final FieldAccessor<Boolean> isMobile = EntityTrackerEntryHandle.T.isMobile.toFieldAccessor();
    public static final FieldAccessor<Integer> timeSinceLocationSync = EntityTrackerEntryHandle.T.timeSinceLocationSync.toFieldAccessor();

    public static final FieldAccessor<Collection<Player>> viewers = new SafeDirectField<Collection<Player>>() {
        @Override
        public Collection<Player> get(Object instance) {
            return EntityTrackerEntryHandle.createHandle(instance).getViewers();
        }

        @Override
        public boolean set(Object instance, Collection<Player> value) {
            EntityTrackerEntryHandle handle = EntityTrackerEntryHandle.createHandle(instance);
            HashSet<Player> oldViewers = new HashSet<Player>(handle.getViewers());
            for (Player p : oldViewers) {
                if (!value.contains(p)) {
                    handle.removeViewerFromSet(p);
                }
            }
            for (Player p : value) {
                if (!oldViewers.contains(p)) {
                    handle.addViewerToSet(p);
                }
            }
            return true;
        }
    };

    public static CommonPacket getSpawnPacket(Object instance) {
        return EntityTrackerEntryHandle.T.getSpawnPacket.invoke(instance);
    }

    public static void scanPlayers(Object instance, List<Player> players) {
        EntityTrackerEntryHandle.T.scanPlayers.invoke(instance, players);
    }

    public static void updatePlayer(Object instance, Player player) {
        EntityTrackerEntryHandle.T.updatePlayer.invoke(instance, player);
    }

    public static Object createNew(Entity entity, int viewDistance, int playerViewDistance, int updateInterval, boolean isMobile) {
        return EntityTrackerEntryHandle.createNew(entity, viewDistance, playerViewDistance, updateInterval, isMobile).getRaw();
    }
}
