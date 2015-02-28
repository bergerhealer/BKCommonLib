package com.bergerkiller.bukkit.common.protocol;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_8_R1.Vec3D;
import com.mojang.authlib.GameProfile;

import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingList;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;
import com.bergerkiller.bukkit.common.reflection.classes.DataWatcherRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityHumanRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.reflection.classes.ItemStackRef;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerAbilitiesRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.bukkit.common.wrappers.ScoreboardAction;
import com.bergerkiller.bukkit.common.wrappers.UseAction;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.ChunkMap;
import net.minecraft.server.v1_8_R1.MapIcon;
import net.minecraft.server.v1_8_R1.PacketDataSerializer;

public class PacketTypeClasses {

    public static class NMSPacket extends PacketType {

        private final SafeConstructor<CommonPacket> constructor0 = getPacketConstructor();

        protected SafeConstructor<CommonPacket> getPacketConstructor(Class<?>... args) {
            return getConstructor(args).translateOutput(Conversion.toCommonPacket);
        }

        @Override
        public CommonPacket newInstance() {
            return constructor0.newInstance();
        }
    }

    /*
     * ========================================================================================
     * ============================= Outgoing packets start ===================================
     * ========================================================================================
     */
    public static class NMSPacketPlayOutEntity extends NMSPacket {

        public final FieldAccessor<Integer> entityId = getField("a");
    }

    public static class NMSPacketPlayOutWindow extends NMSPacket {

        public final FieldAccessor<Integer> windowId = getField("a");
    }

    public static class NMSPacketPlayOutAbilities extends NMSPacket {

        public final FieldAccessor<Boolean> isInvulnerable = getField("a");
        public final FieldAccessor<Boolean> isFlying = getField("b");
        public final FieldAccessor<Boolean> canFly = getField("c");
        public final FieldAccessor<Boolean> canInstantlyBuild = getField("d");
        public final FieldAccessor<Float> flySpeed = getField("e");
        public final FieldAccessor<Float> walkSpeed = getField("f");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(PlayerAbilitiesRef.TEMPLATE.getType());

        public CommonPacket newInstance(PlayerAbilities abilities) {
            return constructor1.newInstance(abilities.getHandle());
        }
    }

    public static class NMSPacketPlayOutAnimation extends NMSPacketPlayOutEntity {

        public final FieldAccessor<Integer> animation = getField("b");
    }

    public static class NMSPacketPlayOutAttachEntity extends NMSPacket {

        public final FieldAccessor<Integer> lead = getField("a");
        public final FieldAccessor<Integer> passengerId = getField("b");
        public final FieldAccessor<Integer> vehicleId = getField("c");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, EntityRef.TEMPLATE.getType(), EntityRef.TEMPLATE.getType());

        public CommonPacket newInstance(org.bukkit.entity.Entity passenger, org.bukkit.entity.Entity vehicle) {
            return newInstance(0, passenger, vehicle);
        }

        public CommonPacket newInstance(int lead, org.bukkit.entity.Entity passenger, org.bukkit.entity.Entity vehicle) {
            return constructor1.newInstance(lead, Conversion.toEntityHandle.convert(passenger), Conversion.toEntityHandle.convert(vehicle));
        }
    }

    public static class NMSPacketPlayOutBed extends NMSPacketPlayOutEntity {

        public final FieldAccessor<BlockPosition> blockpostion = getField("b");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(EntityHumanRef.TEMPLATE.getType(), BlockPosition.class);

        public CommonPacket newInstance(HumanEntity entity, BlockPosition blockpostion) {
            return constructor1.newInstance(Conversion.toEntityHandle.convert(entity), blockpostion);
        }
    }

    public static class NMSPacketPlayOutBlockAction extends NMSPacket {

        public final FieldAccessor<BlockPosition> blockpostion = getField("a");
        public final FieldAccessor<Integer> arg1 = getField("b");
        public final FieldAccessor<Integer> arg2 = getField("c");
        public final FieldAccessor<Material> type = getField("d").translate(ConversionPairs.block);
    }

    public static class NMSPacketPlayOutChat extends NMSPacket {

        public final FieldAccessor<Object> chatComponent = getField("a");
        //public final FieldAccessor<Boolean> isFromServer = getField("b");
    }

    public static class NMSPacketPlayOutCloseWindow extends NMSPacketPlayOutWindow {
    }

    public static class NMSPacketPlayOutCollect extends NMSPacket {

        public final FieldAccessor<Integer> collectedItemId = getField("a");
        public final FieldAccessor<Integer> collectorEntityId = getField("b");
    }

    public static class NMSPacketPlayOutWindowData extends NMSPacketPlayOutWindow {

        public final FieldAccessor<Integer> count = getField("b");
        public final FieldAccessor<Integer> data = getField("c");
    }

    public static class NMSPacketPlayOutCustomPayload extends NMSPacket {

        public final FieldAccessor<String> tag = getField("a");
        //public final FieldAccessor<PacketDataSerializer> data = getField("b");
    }

    public static class NMSPacketPlayOutEntityDestroy extends NMSPacket {

        public final FieldAccessor<int[]> entityIds = getField("a");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int[].class);

        public CommonPacket newInstance(int... entityIds) {
            return constructor1.newInstance(entityIds);
        }

        public CommonPacket newInstance(Collection<Integer> entityIds) {
            return newInstance(Conversion.toIntArr.convert(entityIds));
        }

        public CommonPacket newInstance(org.bukkit.entity.Entity... entities) {
            int[] ids = new int[entities.length];
            for (int i = 0; i < ids.length; i++) {
                ids[i] = entities[i].getEntityId();
            }
            return newInstance(ids);
        }
    }

    public static class NMSPacketPlayOutEntityEffect extends NMSPacketPlayOutEntity {

        public final FieldAccessor<Byte> effectId = getField("b");
        public final FieldAccessor<Byte> effectAmplifier = getField("c");
        public final FieldAccessor<Integer> effectDuration = getField("d");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, CommonUtil.getNMSClass("MobEffect"));

        public CommonPacket newInstance(int entityId, Object mobEffect) {
            return constructor1.newInstance(entityId, mobEffect);
        }
    }

    public static class NMSPacketPlayOutEntityEquipment extends NMSPacketPlayOutEntity {

        public final FieldAccessor<Integer> slot = getField("b");
        public final TranslatorFieldAccessor<ItemStack> item = getField("c").translate(ConversionPairs.itemStack);
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, int.class, ItemStackRef.TEMPLATE.getType());

        public CommonPacket newInstance(int entityId, int slotId, ItemStack item) {
            return constructor1.newInstance(entityId, slotId, Conversion.toItemStackHandle.convert(item));
        }
    }

    public static class NMSPacketPlayOutEntityHeadRotation extends NMSPacketPlayOutEntity {

        public final FieldAccessor<Byte> headYaw = getField("b");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(EntityRef.TEMPLATE.getType(), byte.class);

        public CommonPacket newInstance(org.bukkit.entity.Entity entity, byte headRotation) {
            return constructor1.newInstance(Conversion.toEntityHandle.convert(entity), headRotation);
        }
    }

    public static class NMSPacketPlayOutEntityLook extends NMSPacketPlayOutEntity {

        public final FieldAccessor<Byte> yaw = getField("e");
        public final FieldAccessor<Byte> pitch = getField("f");
        public final FieldAccessor<Boolean> onGround = getField("h");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, byte.class, byte.class, boolean.class);

        public CommonPacket newInstance(int entityId, byte dyaw, byte dpitch, boolean onGround) {
            return constructor1.newInstance(entityId, dyaw, dpitch, onGround);
        }
    }

    public static class NMSPacketPlayOutEntityMetadata extends NMSPacketPlayOutEntity {

        /**
         * CraftBukkit uses rawtypes for this, so do we
         */
        @SuppressWarnings("rawtypes")
        public final FieldAccessor<List> watchedObjects = getField("b");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, DataWatcherRef.TEMPLATE.getType(), boolean.class);

        public CommonPacket newInstance(int entityId, DataWatcher dataWatcher, boolean sendUnchangedData) {
            return constructor1.newInstance(entityId, dataWatcher.getHandle(), sendUnchangedData);
        }
    }

    public static class NMSPacketPlayOutEntityStatus extends NMSPacketPlayOutEntity {

        public final FieldAccessor<Byte> status = getField("b");
    }

    public static class NMSPacketPlayOutEntityTeleport extends NMSPacketPlayOutEntity {

        public final FieldAccessor<Integer> x = getField("b");
        public final FieldAccessor<Integer> y = getField("c");
        public final FieldAccessor<Integer> z = getField("d");
        public final FieldAccessor<Byte> yaw = getField("e");
        public final FieldAccessor<Byte> pitch = getField("f");
        public final FieldAccessor<Boolean> onGround = getField("g");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(EntityRef.TEMPLATE.getType());
        private final SafeConstructor<CommonPacket> constructor2 = getPacketConstructor(int.class, int.class, int.class, int.class, byte.class, byte.class, boolean.class);

        public CommonPacket newInstance(org.bukkit.entity.Entity entity) {
            return constructor1.newInstance(Conversion.toEntityHandle.convert(entity));
        }

        public CommonPacket newInstance(int entityId, int x, int y, int z, byte yaw, byte pitch, boolean b) {
            return constructor2.newInstance(entityId, x, y, z, yaw, pitch, b);
        }
    }

    public static class NMSPacketPlayOutEntityVelocity extends NMSPacketPlayOutEntity {

        public final FieldAccessor<Integer> motX = getField("b");
        public final FieldAccessor<Integer> motY = getField("c");
        public final FieldAccessor<Integer> motZ = getField("d");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(EntityRef.TEMPLATE.getType());
        private final SafeConstructor<CommonPacket> constructor2 = getPacketConstructor(int.class, double.class, double.class, double.class);

        public CommonPacket newInstance(org.bukkit.entity.Entity entity) {
            return constructor1.newInstance(Conversion.toEntityHandle.convert(entity));
        }

        public CommonPacket newInstance(int entityId, double motX, double motY, double motZ) {
            return constructor2.newInstance(entityId, motX, motY, motZ);
        }

        public CommonPacket newInstance(int entityId, Vector velocity) {
            return newInstance(entityId, velocity.getX(), velocity.getY(), velocity.getZ());
        }
    }

    public static class NMSPacketPlayOutExperience extends NMSPacket {

        public final FieldAccessor<Float> bar = getField("a");
        public final FieldAccessor<Integer> level = getField("b");
        public final FieldAccessor<Integer> totalXp = getField("c");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(float.class, int.class, int.class);

        public CommonPacket newInstance(float bar, int level, int totalXp) {
            return constructor1.newInstance(bar, level, totalXp);
        }
    }

    public static class NMSPacketPlayOutExplosion extends NMSPacket {

        public final FieldAccessor<Double> x = getField("a");
        public final FieldAccessor<Double> y = getField("b");
        public final FieldAccessor<Double> z = getField("c");
        public final FieldAccessor<Float> radius = getField("d");
        public final FieldAccessor<List<Object>> blocks = getField("e");
        public final FieldAccessor<Float> pushMotX = getField("f");
        public final FieldAccessor<Float> pushMotY = getField("g");
        public final FieldAccessor<Float> pushMotZ = getField("h");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(double.class, double.class, double.class, float.class, List.class, Vec3D.class);

        @SuppressWarnings("unchecked")
        public CommonPacket newInstance(double x, double y, double z, float radius) {
            return newInstance(x, y, z, radius, Collections.EMPTY_LIST);
        }

        public CommonPacket newInstance(double x, double y, double z, float radius, List<IntVector3> blocks) {
            return newInstance(x, y, z, radius, blocks, null);
        }

        public CommonPacket newInstance(double x, double y, double z, float radius, List<IntVector3> blocks, Vector pushedForce) {
            Vec3D vec = pushedForce == null ? null : new Vec3D(pushedForce.getX(), pushedForce.getY(), pushedForce.getZ());
            return constructor1.newInstance(x, y, z, radius, blocks, vec);
        }
    }

    public static class NMSPacketPlayOutGameStateChange extends NMSPacket {

        public final FieldAccessor<Integer> UNKNOWN1 = getField("b");
        public final FieldAccessor<Float> UNKNOWN2 = getField("c");
    }

    public static class NMSPacketPlayOutHeldItemSlot extends NMSPacket {

        public final FieldAccessor<Integer> slot = getField("a");
    }

    public static class NMSPacketPlayOutKeepAlive extends NMSPacket {

        public final FieldAccessor<Integer> key = getField("a");
    }

    public static class NMSPacketPlayOutKickDisconnect extends NMSPacket {

        public final FieldAccessor<String> reason = getField("a");
    }

    public static class NMSPacketPlayOutLogin extends NMSPacket {

        public final FieldAccessor<Integer> playerId = getField("a");
        public final FieldAccessor<Boolean> hardcore = getField("b");
        public final TranslatorFieldAccessor<GameMode> gameMode = getField("c").translate(ConversionPairs.gameMode);
        public final FieldAccessor<Integer> dimension = getField("d");
        public final TranslatorFieldAccessor<Difficulty> difficulty = getField("e").translate(ConversionPairs.difficulty);
        public final FieldAccessor<Integer> maxPlayers = getField("f");
        public final TranslatorFieldAccessor<WorldType> worldType = getField("g").translate(ConversionPairs.worldType);
        public final FieldAccessor<Boolean> UNKNOWN1 = getField("h"); // Unkown field
    }

    public static class NMSPacketPlayOutMap extends NMSPacket {

        public final FieldAccessor<Integer> itemId = getField("a");
        public final FieldAccessor<Byte> UNKNOWN1 = getField("b");
        public final FieldAccessor<MapIcon[]> UNKNOWN2 = getField("c");
        public final FieldAccessor<Integer> UNKNOWN3 = getField("d");
        public final FieldAccessor<Integer> UNKNOWN4 = getField("d");
        public final FieldAccessor<Integer> UNKNOWN5 = getField("d");
        public final FieldAccessor<byte[]> text = getField("h");
    }

    public static class NMSPacketPlayOutMapChunk extends NMSPacket {
        public final FieldAccessor<Integer> x = getField("a");
        public final FieldAccessor<Integer> z = getField("b");
        public final FieldAccessor<ChunkMap> chunkDataBitMap = getField("c");
        public final FieldAccessor<Boolean> hasBiomeData = getField("d");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(CommonUtil.getNMSClass("Chunk"), boolean.class, int.class);

        public CommonPacket newInstance(Chunk chunk) {
            return newInstance(Conversion.toChunkHandle.convert(chunk));
        }

        public CommonPacket newInstance(Object chunk) {
            return newInstance(chunk, true, 0xFFFF);
        }

        public CommonPacket newInstance(Object chunk, boolean hasBiomeData, int sectionsMask) {
            return constructor1.newInstance(chunk, hasBiomeData, sectionsMask);
        }
    }

    public static class NMSPacketPlayOutMapChunkBulk extends NMSPacket {

        public final FieldAccessor<int[]> bulk_x = getField("a");
        public final FieldAccessor<int[]> bulk_z = getField("b");
        public final FieldAccessor<ChunkMap[]> bulk_chunkDataBitMap = getField("c");
        public final FieldAccessor<Boolean> hasSkyLight = getField("d");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(List.class);

        public CommonPacket newInstance(List<Chunk> chunks) {
            return constructor1.newInstance(new ConvertingList<>(chunks, ConversionPairs.chunk.reverse()));
        }
    }

    public static class NMSPacketPlayOutMultiBlockChange extends NMSPacket {

        public final FieldAccessor<IntVector2> chunk = getField("a").translate(ConversionPairs.chunkIntPair);
    }
    
    public static class NMSPacketPlayOutNamedEntitySpawn extends NMSPacketPlayOutEntity {

        public final FieldAccessor<UUID> uuid = getField("b");
        public final FieldAccessor<Integer> x = getField("c");
        public final FieldAccessor<Integer> y = getField("d");
        public final FieldAccessor<Integer> z = getField("e");
        public final FieldAccessor<Byte> yaw = getField("f");
        public final FieldAccessor<Byte> pitch = getField("g");
        public final FieldAccessor<Integer> heldItemId = getField("h");
        public final TranslatorFieldAccessor<DataWatcher> dataWatcher = getField("i").translate(ConversionPairs.dataWatcher);
    }

    public static class NMSPacketPlayOutNamedSoundEffect extends NMSPacket {

        public final FieldAccessor<String> soundName = getField("a");
        public final FieldAccessor<Integer> x = getField("b");
        public final FieldAccessor<Integer> y = getField("c");
        public final FieldAccessor<Integer> z = getField("d");
        public final FieldAccessor<Float> volume = getField("e");
        public final FieldAccessor<Integer> pitch = getField("f");
    }

    public static class NMSPacketPlayOutOpenSignEditor extends NMSPacket {

        public final FieldAccessor<BlockPosition> x = getField("a");
    }

    public static class NMSPacketPlayOutOpenWindow extends NMSPacketPlayOutWindow {

        public final FieldAccessor<Integer> type = getField("a");
        public final FieldAccessor<String> title = getField("c"); // Or field 'B'
        public final FieldAccessor<Integer> slotCount = getField("d");
    }

    public static class NMSPacketPlayOutPlayerInfo extends NMSPacket {
        /*
         public final FieldAccessor<String> playerName = getField("a");
         public final FieldAccessor<Boolean> online = getField("b");
         public final FieldAccessor<Integer> ping = getField("c");
         private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(String.class, boolean.class, int.class);

         public CommonPacket newInstance(String playerName, boolean online, int ping) {
         return constructor1.newInstance(playerName, online, ping);
         }
         */
    }
    
    public static class NMSPacketPlayOutPlayerListHeaderFooter extends NMSPacket {
        
        public final FieldAccessor<String> titleTop = getField("a");
        public final FieldAccessor<String> titleBottom = getField("b");
    }

    public static class NMSPacketPlayOutPosition extends NMSPacket {

        public final FieldAccessor<Double> x = getField("a");
        public final FieldAccessor<Double> y = getField("b");
        public final FieldAccessor<Double> z = getField("c");
        public final FieldAccessor<Float> yaw = getField("d");
        public final FieldAccessor<Float> pitch = getField("e");
        public final FieldAccessor<Set> UNKNOWN1 = getField("f");
    }

    public static class NMSPacketPlayOutRelEntityMove extends NMSPacketPlayOutEntity {

        public final FieldAccessor<Byte> dx = getField("b");
        public final FieldAccessor<Byte> dy = getField("c");
        public final FieldAccessor<Byte> dz = getField("d");
        public final FieldAccessor<Boolean> onGround = getField("g");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, byte.class, byte.class, byte.class, boolean.class);

        public CommonPacket newInstance(int entityId, byte dx, byte dy, byte dz, boolean onGround) {
            return constructor1.newInstance(entityId, dx, dy, dz, onGround);
        }
    }

    public static class NMSPacketPlayOutRelEntityMoveLook extends NMSPacketPlayOutEntity {

        public final FieldAccessor<Byte> dx = getField("b");
        public final FieldAccessor<Byte> dy = getField("c");
        public final FieldAccessor<Byte> dz = getField("d");
        public final FieldAccessor<Byte> dyaw = getField("e");
        public final FieldAccessor<Byte> dpitch = getField("f");
        public final FieldAccessor<Boolean> onGround = getField("g");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, byte.class, byte.class, byte.class, byte.class, byte.class, boolean.class);

        public CommonPacket newInstance(int entityId, byte dx, byte dy, byte dz, byte dyaw, byte dpitch, boolean onGround) {
            return constructor1.newInstance(entityId, dx, dy, dz, dyaw, dpitch, onGround);
        }
    }

    public static class NMSPacketPlayOutRemoveEntityEffect extends NMSPacketPlayOutEntity {

        public final FieldAccessor<Byte> effectId = getField("b");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, CommonUtil.getNMSClass("MobEffect"));

        public CommonPacket newInstance(int entityId, Object mobEffect) {
            return constructor1.newInstance(entityId, mobEffect);
        }
    }

    public static class NMSPacketPlayOutRespawn extends NMSPacket {

        public final FieldAccessor<Integer> dimension = getField("a");
        public final FieldAccessor<Difficulty> difficulty = getField("b").translate(ConversionPairs.difficulty);
        public final TranslatorFieldAccessor<GameMode> gamemode = getField("c").translate(ConversionPairs.gameMode);
        public final TranslatorFieldAccessor<WorldType> worldType = getField("d").translate(ConversionPairs.worldType);
    }

    public static class NMSPacketPlayOutScoreboardDisplayObjective extends NMSPacket {

        public final FieldAccessor<Integer> display = getField("a");
        public final FieldAccessor<String> name = getField("b");
    }

    public static class NMSPacketPlayOutScoreboardObjective extends NMSPacket {

        public final FieldAccessor<String> name = getField("a");
        public final FieldAccessor<String> displayName = getField("b");
        public final FieldAccessor<Integer> action = getField("d");
    }

    public static class NMSPacketPlayOutScoreboardScore extends NMSPacket {

        public final FieldAccessor<String> name = getField("a");
        public final FieldAccessor<String> objName = getField("b");
        public final FieldAccessor<Integer> value = getField("c");
        public final FieldAccessor<ScoreboardAction> action = getField("d").translate(ConversionPairs.scoreboardAction);
    }

    public static class NMSPacketPlayOutScoreboardTeam extends NMSPacket {

        public final FieldAccessor<String> team = getField("a");
        public final FieldAccessor<String> display = getField("b");
        public final FieldAccessor<String> prefix = getField("c");
        public final FieldAccessor<String> suffix = getField("d");
        public final FieldAccessor<Collection<String>> players = getField("g");
        public final FieldAccessor<Integer> mode = getField("h");
        public final FieldAccessor<Integer> friendlyFire = getField("i");
    }

    public static class NMSPacketPlayOutSetSlot extends NMSPacketPlayOutWindow {

        public final FieldAccessor<Integer> slot = getField("b");
        public final FieldAccessor<ItemStack> item = getField("c").translate(ConversionPairs.itemStack);
    }

    public static class NMSPacketPlayOutSpawnEntity extends NMSPacketPlayOutEntity {

        public final FieldAccessor<Integer> x = getField("b");
        public final FieldAccessor<Integer> y = getField("c");
        public final FieldAccessor<Integer> z = getField("d");
        public final FieldAccessor<Integer> motX = getField("e");
        public final FieldAccessor<Integer> motY = getField("f");
        public final FieldAccessor<Integer> motZ = getField("g");
        public final FieldAccessor<Integer> pitch = getField("h"); // Byte -> Integer
        public final FieldAccessor<Integer> yaw = getField("i"); // Byte -> Integer
        public final FieldAccessor<Integer> entityType = getField("j");
        public final FieldAccessor<Integer> extraData = getField("k");
    }

    public static class NMSPacketPlayOutSpawnEntityExperienceOrb extends NMSPacketPlayOutEntity {

        public final FieldAccessor<Integer> x = getField("b");
        public final FieldAccessor<Integer> y = getField("c");
        public final FieldAccessor<Integer> z = getField("d");
        public final FieldAccessor<Integer> experience = getField("e");
    }

    public static class NMSPacketPlayOutSpawnEntityLiving extends NMSPacketPlayOutEntity {

        public final FieldAccessor<Integer> entityType = getField("b");
        public final FieldAccessor<Integer> x = getField("c");
        public final FieldAccessor<Integer> y = getField("d");
        public final FieldAccessor<Integer> z = getField("e");
        public final FieldAccessor<Integer> motX = getField("f");
        public final FieldAccessor<Integer> motY = getField("g");
        public final FieldAccessor<Integer> motZ = getField("h");
        public final FieldAccessor<Byte> yaw = getField("i");
        public final FieldAccessor<Byte> pitch = getField("j");
        public final FieldAccessor<Byte> headYaw = getField("k");
        public final TranslatorFieldAccessor<DataWatcher> dataWatcher = getField("l").translate(ConversionPairs.dataWatcher);
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(CommonUtil.getNMSClass("EntityLiving"));

        public CommonPacket newInstance(Object entityLiving) {
            return constructor1.newInstance(entityLiving);
        }
    }

    public static class NMSPacketPlayOutSpawnEntityPainting extends NMSPacketPlayOutEntity {

        public final FieldAccessor<BlockPosition> blockPostion = getField("b");
        public final FieldAccessor<BlockFace> facing = getField("e").translate(ConversionPairs.paintingFacing);
        public final FieldAccessor<String> art = getField("f");
    }

    public static class NMSPacketPlayOutSpawnEntityWeather extends NMSPacketPlayOutEntity {

        public final FieldAccessor<Integer> x = getField("b");
        public final FieldAccessor<Integer> y = getField("c");
        public final FieldAccessor<Integer> z = getField("d");
        public final FieldAccessor<Integer> type = getField("e");
    }

    // DOWN FROM HERE NEEDS TO BE DONE!
    
    public static class NMSPacketPlayOutSpawnPosition extends NMSPacket {

        public final FieldAccessor<Integer> x = getField("x");
        public final FieldAccessor<Integer> y = getField("y");
        public final FieldAccessor<Integer> z = getField("z");
    }

    public static class NMSPacketPlayOutStatistic extends NMSPacket {

        public final FieldAccessor<Map<Object, Integer>> statsMap = getField("a");
    }

    public static class NMSPacketPlayOutTabComplete extends NMSPacket {

        public final FieldAccessor<String[]> response = getField("a");
    }

    public static class NMSPacketPlayOutTileEntityData extends NMSPacket {

        public final FieldAccessor<Integer> x = getField("a");
        public final FieldAccessor<Integer> y = getField("b");
        public final FieldAccessor<Integer> z = getField("c");
        public final FieldAccessor<Integer> action = getField("d");
        public final FieldAccessor<Object> data = getField("e");
    }

    public static class NMSPacketPlayOutTransaction extends NMSPacketPlayOutWindow {

        public final FieldAccessor<Short> action = getField("b");
        public final FieldAccessor<Boolean> accepted = getField("c");
    }

    public static class NMSPacketPlayOutUpdateAttributes extends NMSPacketPlayOutEntity {

        /**
         * A list of NMS.Attribute elements - may require further API to work
         * with. For now, use reflection.
         */
        public final FieldAccessor<List<?>> attributeSnapshots = getField("b");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, Collection.class);

        public CommonPacket newInstance(int entityId, Collection<?> attributes) {
            return constructor1.newInstance(entityId, attributes);
        }
    }

    public static class NMSPacketPlayOutUpdateHealth extends NMSPacket {

        public final FieldAccessor<Short> health = getField("a");
        public final FieldAccessor<Short> food = getField("b");
        public final FieldAccessor<Short> foodSaturation = getField("c");
    }

    public static class NMSPacketPlayOutUpdateSign extends NMSPacket {

        public final FieldAccessor<Integer> x = getField("x");
        public final FieldAccessor<Integer> y = getField("y");
        public final FieldAccessor<Integer> z = getField("z");
        public final FieldAccessor<String[]> lines = getField("lines");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, int.class, int.class, String[].class);

        public Block getBlock(Object packetInstance, World world) {
            return world.getBlockAt(x.get(packetInstance), y.get(packetInstance), z.get(packetInstance));
        }

        public void setBlock(Object packetInstance, Block block) {
            x.set(packetInstance, block.getX());
            y.set(packetInstance, block.getY());
            z.set(packetInstance, block.getZ());
        }

        public CommonPacket newInstance(int x, int y, int z, String[] lines) {
            return constructor1.newInstance(x, y, z, lines);
        }
    }

    public static class NMSPacketPlayOutUpdateTime extends NMSPacket {

        public final FieldAccessor<Long> age = getField("a");
        public final FieldAccessor<Long> timeOfDay = getField("b");
    }

    public static class NMSPacketPlayOutWindowItems extends NMSPacketPlayOutWindow {

        public final FieldAccessor<ItemStack[]> items = getField("b").translate(ConversionPairs.itemStackArr);
    }

    public static class NMSPacketPlayOutWorldEvent extends NMSPacket {

        public final FieldAccessor<Integer> effectId = getField("a");
        public final FieldAccessor<Integer> x = getField("c");
        public final FieldAccessor<Integer> y = getField("d");
        public final FieldAccessor<Integer> z = getField("e");
        public final FieldAccessor<Integer> data = getField("b");
        public final FieldAccessor<Boolean> noRelativeVolume = getField("f");
    }

    public static class NMSPacketPlayOutWorldParticles extends NMSPacket {

        public final FieldAccessor<String> effectName = getField("a");
        public final FieldAccessor<Float> x = getField("b");
        public final FieldAccessor<Float> y = getField("c");
        public final FieldAccessor<Float> z = getField("d");
        public final FieldAccessor<Float> randomX = getField("e");
        public final FieldAccessor<Float> randomY = getField("f");
        public final FieldAccessor<Float> randomZ = getField("g");
        public final FieldAccessor<Float> speed = getField("h");
        public final FieldAccessor<Integer> particleCount = getField("i");

        public CommonPacket newInstance(String name, int count, Location location, double randomness, double speed) {
            return newInstance(name, count, location.getX(), location.getY(), location.getZ(), randomness, randomness, randomness, speed);
        }

        public CommonPacket newInstance(String name, int count, double x, double y, double z, double rx, double ry, double rz, double speed) {
            final CommonPacket packet = newInstance();
            packet.write(this.effectName, name);
            packet.write(this.particleCount, count);
            packet.write(this.x, (float) x);
            packet.write(this.y, (float) y);
            packet.write(this.z, (float) z);
            packet.write(this.randomX, (float) rx);
            packet.write(this.randomY, (float) ry);
            packet.write(this.randomZ, (float) rz);
            packet.write(this.speed, (float) speed);
            return packet;
        }
    }

    /*
     * ========================================================================================
     * ============================= Incoming packets start ===================================
     * ========================================================================================
     */
    public static class NMSPacketPlayInWindow extends NMSPacket {

        public final FieldAccessor<Integer> windowId = getField("a");
    }

    public static class NMSPacketPlayInAbilities extends NMSPacket {

        public final FieldAccessor<Boolean> isInvulnerable = getField("a");
        public final FieldAccessor<Boolean> isFlying = getField("b");
        public final FieldAccessor<Boolean> canFly = getField("c");
        public final FieldAccessor<Boolean> canInstantlyBuild = getField("d");
        public final FieldAccessor<Float> flySpeed = getField("e");
        public final FieldAccessor<Float> walkSpeed = getField("f");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(PlayerAbilitiesRef.TEMPLATE.getType());

        public CommonPacket newInstance(PlayerAbilities abilities) {
            return constructor1.newInstance(abilities.getHandle());
        }
    }

    public static class NMSPacketPlayInArmAnimation extends NMSPacket {

        public final FieldAccessor<Integer> entityId = getField("a");
        public final FieldAccessor<Integer> animation = getField("b");
    }

    public static class NMSPacketPlayInBlockDig extends NMSPacket {

        public final FieldAccessor<Integer> x = getField("a");
        public final FieldAccessor<Integer> y = getField("b");
        public final FieldAccessor<Integer> z = getField("c");
        public final FieldAccessor<Integer> face = getField("face");
        public final FieldAccessor<Integer> status = getField("e");
    }

    public static class NMSPacketPlayInBlockPlace extends NMSPacket {

        public final FieldAccessor<Integer> x = getField("a");
        public final FieldAccessor<Integer> y = getField("b");
        public final FieldAccessor<Integer> z = getField("c");
        public final FieldAccessor<Integer> direction = getField("d");
        public final FieldAccessor<ItemStack> itemStack = getField("e").translate(ConversionPairs.itemStack);
        public final FieldAccessor<Float> cursorX = getField("f");
        public final FieldAccessor<Float> cursorY = getField("g");
        public final FieldAccessor<Float> cursorZ = getField("h");
    }

    public static class NMSPacketPlayInChat extends NMSPacket {

        public final FieldAccessor<String> message = getField("message");
    }

    public static class NMSPacketPlayInClientCommand extends NMSPacket {

        public final FieldAccessor<Object> command = getField("a");
    }

    public static class NMSPacketPlayInCloseWindow extends NMSPacketPlayInWindow {
    }

    public static class NMSPacketPlayInCustomPayload extends NMSPacket {

        public final FieldAccessor<String> tag = getField("tag");
        public final FieldAccessor<Integer> length = getField("length");
        public final FieldAccessor<byte[]> data = getField("data");
    }

    public static class NMSPacketPlayInEnchantItem extends NMSPacketPlayInWindow {

        public final FieldAccessor<Integer> enchantment = getField("b");
    }

    public static class NMSPacketPlayInEntityAction extends NMSPacket {

        public final FieldAccessor<Integer> playerId = getField("a");
        public final FieldAccessor<Integer> actionId = getField("animation");
        public final FieldAccessor<Integer> jumpBoost = getField("c");
    }

    public static class NMSPacketPlayInFlying extends NMSPacket {

        public final FieldAccessor<Double> x = getField("x");
        public final FieldAccessor<Double> y = getField("y");
        public final FieldAccessor<Double> z = getField("z");
        public final FieldAccessor<Double> stance = getField("stance");
        public final FieldAccessor<Float> yaw = getField("yaw");
        public final FieldAccessor<Float> pitch = getField("pitch");
        public final FieldAccessor<Boolean> hasPos = getField("hasPos");
        public final FieldAccessor<Boolean> hasLook = getField("hasLook");
        public final FieldAccessor<Boolean> onGround = getField("g");
    }

    public static class NMSPacketPlayInHeldItemSlot extends NMSPacket {

        public final FieldAccessor<Integer> slot = getField("itemInHandIndex");
    }

    public static class NMSPacketPlayInKeepAlive extends NMSPacket {

        public final FieldAccessor<Integer> key = getField("a");
    }

    public static class NMSPacketPlayInLook extends NMSPacket {

        public final FieldAccessor<Float> yaw = getField("yaw");
        public final FieldAccessor<Float> pitch = getField("pitch");
    }

    public static class NMSPacketPlayInPosition extends NMSPacket {

        public final FieldAccessor<Double> x = getField("x");
        public final FieldAccessor<Double> y = getField("y");
        public final FieldAccessor<Double> z = getField("z");
        public final FieldAccessor<Double> stance = getField("stance");
    }

    public static class NMSPacketPlayInPositionLook extends NMSPacket {

        public final FieldAccessor<Double> x = getField("x");
        public final FieldAccessor<Double> y = getField("y");
        public final FieldAccessor<Double> z = getField("z");
        public final FieldAccessor<Double> stance = getField("stance");
        public final FieldAccessor<Float> yaw = getField("yaw");
        public final FieldAccessor<Float> pitch = getField("pitch");
    }

    public static class NMSPacketPlayInSetCreativeSlot extends NMSPacket {

        public final FieldAccessor<Integer> slot = getField("slot");
        public final FieldAccessor<ItemStack> itemStack = getField("b").translate(ConversionPairs.itemStack);
    }

    public static class NMSPacketPlayInSettings extends NMSPacket {

        public final FieldAccessor<String> locale = getField("a");
        public final FieldAccessor<Integer> viewDistanceId = getField("b");
        public final FieldAccessor<Object> chatVisibility = getField("c");
        public final TranslatorFieldAccessor<Difficulty> difficulty = getField("e").translate(ConversionPairs.difficulty);
        public final FieldAccessor<Boolean> showCape = getField("f");
    }

    public static class NMSPacketPlayInSteerVehicle extends NMSPacket {

        public final FieldAccessor<Float> sideways = getField("a");
        public final FieldAccessor<Float> forwards = getField("b");
        public final FieldAccessor<Boolean> jump = getField("c");
        public final FieldAccessor<Boolean> unmount = getField("d");
    }

    public static class NMSPacketPlayInTabComplete extends NMSPacket {

        public final FieldAccessor<String> message = getField("a");
    }

    public static class NMSPacketPlayInTransaction extends NMSPacketPlayInWindow {

        public final FieldAccessor<Short> action = getField("b");
        public final FieldAccessor<Boolean> accepted = getField("c");
    }

    public static class NMSPacketPlayInUpdateSign extends NMSPacket {

        public final FieldAccessor<Integer> x = getField("a");
        public final FieldAccessor<Integer> y = getField("b");
        public final FieldAccessor<Integer> z = getField("c");
        public final FieldAccessor<String[]> lines = getField("d");

        public Block getBlock(Object packetInstance, World world) {
            return world.getBlockAt(x.get(packetInstance), y.get(packetInstance), z.get(packetInstance));
        }

        public void setBlock(Object packetInstance, Block block) {
            x.set(packetInstance, block.getX());
            y.set(packetInstance, block.getY());
            z.set(packetInstance, block.getZ());
        }
    }

    public static class NMSPacketPlayInUseEntity extends NMSPacket {

        public final FieldAccessor<Integer> clickedEntityId = getField("a");
        public final TranslatorFieldAccessor<UseAction> useAction = getField("action").translate(ConversionPairs.useAction);
    }

    public static class NMSPacketPlayInWindowClick extends NMSPacketPlayInWindow {

        public final FieldAccessor<Integer> windowId = getField("a");
        public final FieldAccessor<Integer> slot = getField("slot");
        public final FieldAccessor<Integer> button = getField("button");
        public final FieldAccessor<Short> action = getField("d");
        public final FieldAccessor<ItemStack> item = getField("item").translate(ConversionPairs.itemStack);
        public final FieldAccessor<Integer> shift = getField("shift");
    }
}
