package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.MethodAccessor;
import com.bergerkiller.reflection.SafeConstructor;
import com.bergerkiller.reflection.TranslatorFieldAccessor;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class NMSEntityTrackerEntry {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityTrackerEntry");

    public static final TranslatorFieldAccessor<Entity> tracker = T.nextField("private final Entity tracker").translate(ConversionPairs.entity);

    public static final FieldAccessor<Integer> viewDistance = T.nextFieldSignature("private final int e");
    public static final FieldAccessor<Integer> playerViewDistance = T.nextFieldSignature("private int f");
    public static final FieldAccessor<Integer> updateInterval = T.nextFieldSignature("private final int g");

    public static final FieldAccessor<Long> xLoc = T.nextField("private long xLoc");
    public static final FieldAccessor<Long> yLoc = T.nextField("private long yLoc");
    public static final FieldAccessor<Long> zLoc = T.nextField("private long zLoc");
    public static final FieldAccessor<Integer> yRot = T.nextField("private int yRot");
    public static final FieldAccessor<Integer> xRot = T.nextField("private int xRot");
    public static final FieldAccessor<Integer> headYaw = T.nextField("private int headYaw");

    public static final FieldAccessor<Double> xVel = T.nextFieldSignature("private double n");
    public static final FieldAccessor<Double> yVel = T.nextFieldSignature("private double o");
    public static final FieldAccessor<Double> zVel = T.nextFieldSignature("private double p");

    public static final FieldAccessor<Integer> tickCounter = T.nextFieldSignature("public int a");

    public static final FieldAccessor<Double> prevX = T.nextFieldSignature("private double q");
    public static final FieldAccessor<Double> prevY = T.nextFieldSignature("private double r");
    public static final FieldAccessor<Double> prevZ = T.nextFieldSignature("private double s");

    public static final FieldAccessor<Boolean> synched = T.nextField("private boolean isMoving");
    public static final FieldAccessor<Boolean> isMobile = T.nextFieldSignature("private final boolean u");
    public static final FieldAccessor<Integer> timeSinceLocationSync = T.nextFieldSignature("private int v");
    public static final TranslatorFieldAccessor<List<Entity>> passengers = T.nextFieldSignature("private List<Entity> w").translate(ConversionPairs.entityList);

    public static final TranslatorFieldAccessor<Set<Player>> viewers = T.nextField("public final Set<EntityPlayer> trackedPlayers").translate(ConversionPairs.playerSet);

    /*
     # private Packet<?> ##METHODNAME##() {
     *     if (this.tracker.dead) {
     *         // CraftBukkit start - Remove useless error spam, just return
     *         // EntityTrackerEntry.d.warn("Fetching addPacket for removed entity");
     *         return null;
     *         // CraftBukkit end
     *     }
     * 
     *     if (this.tracker instanceof EntityPlayer) {
     *         return new PacketPlayOutNamedEntitySpawn((EntityHuman) this.tracker);
     *     } else if (this.tracker instanceof IAnimal) {
     *         ...
     *     }
     *     ...
     * }
     */
    private static final MethodAccessor<Object> getSpawnPacket = T.selectMethod("private Packet<?> e()");

    private static final MethodAccessor<Void> scanPlayers = T.selectMethod("public void scanPlayers(List<EntityHuman> list)");
    private static final MethodAccessor<Void> updatePlayer = T.selectMethod("public void updatePlayer(EntityPlayer entityplayer)");

    private static final SafeConstructor<?> constructor = T.getConstructor(NMSEntity.T.getType(), int.class, int.class, int.class, boolean.class);

    public static CommonPacket getSpawnPacket(Object instance) {
        return Conversion.toCommonPacket.convert(getSpawnPacket.invoke(instance));
    }

    public static void scanPlayers(Object instance, List<Player> players) {
        scanPlayers.invoke(instance, ConversionPairs.playerList.convertA(players));
    }

    public static void updatePlayer(Object instance, Player player) {
        updatePlayer.invoke(instance, Conversion.toEntityHandle.convert(player));
    }

    public static Object createNew(Entity entity, int viewDistance, int playerViewDistance, int updateInterval, boolean isMobile) {
        return constructor.newInstance(Conversion.toEntityHandle.convert(entity), viewDistance, playerViewDistance, updateInterval, isMobile);
    }
}
