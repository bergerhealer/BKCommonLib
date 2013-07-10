package com.bergerkiller.bukkit.common.reflection.classes;

import java.security.PublicKey;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.crypto.SecretKey;

import net.minecraft.server.Vec3D;

import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingList;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;

public class PacketFieldClasses {
	public static class NMSPacket extends NMSClassTemplate {
		public final FieldAccessor<Integer> packetID = getField("packetID");
		public final FieldAccessor<Boolean> lowPriority = getField("lowPriority");
		public final FieldAccessor<Long> timestamp = getField("timestamp");
		private final MethodAccessor<Integer> packetSize = getMethod("a");
		private final MethodAccessor<Void> register = getMethod("a", int.class, boolean.class, boolean.class, Class.class);
		private final SafeConstructor<CommonPacket> constructor0 = getPacketConstructor();

		/**
		 * Checks whether a given object is an instance of the class represented by this Packet
		 * 
		 * @param packet to check
		 * @return True if the packet is an instance of this Packet, False if not
		 */
		public boolean isInstance(CommonPacket packet) {
			return packet != null && super.isInstance(packet.getHandle());
		}

		@Override
		public boolean isInstance(Object packet) {
			if (packet instanceof CommonPacket) {
				packet = ((CommonPacket) packet).getHandle();
			}
			return super.isInstance(packet);
		}

		@Override
		public CommonPacket newInstance() {
			return constructor0.newInstance();
		}

		/**
		 * @deprecated - use getPacketConstructor instead.
		 */
		@Override
		@Deprecated
		public SafeConstructor<Object> getConstructor(Class<?>... parameterTypes) {
			return super.getConstructor(parameterTypes);
		}

		public SafeConstructor<CommonPacket> getPacketConstructor(Class<?>... args) {
			return getConstructor(args).translateOutput(Conversion.toCommonPacket);
		}

		public int getPacketSize(Object packet) {
			return packetSize.invoke(packet);
		}
		
		public void register(int id, boolean serverPacket, boolean clientPacket, Class<?> packetClass) {
			register.invoke(null, id, serverPacket, clientPacket, packetClass);
		}
	}
	public static class NMSPacket0KeepAlive extends NMSPacket {
		public final FieldAccessor<Integer> key = getField("a");
	}
	public static class NMSPacket1Login extends NMSPacket {
		public final FieldAccessor<Integer> playerId = getField("a");
		public final TranslatorFieldAccessor<WorldType> worldType = getField("b").translate(ConversionPairs.worldType);
		public final FieldAccessor<Boolean> hardcore = getField("c");
		public final TranslatorFieldAccessor<GameMode> gameMode = getField("d").translate(ConversionPairs.gameMode);
		public final FieldAccessor<Integer> dimension = getField("e");
		public final TranslatorFieldAccessor<Difficulty> difficulty = getField("f").translate(ConversionPairs.difficulty);
		//public final FieldAccessor<Byte> unused = getField("g");
		public final FieldAccessor<Byte> maxPlayers = getField("h");
	}
	public static class NMSPacket2Handshake extends NMSPacket {
		public final FieldAccessor<Integer> protocolVersion = getField("a");
		public final FieldAccessor<String> playerName = getField("b");
		public final FieldAccessor<String> serverName = getField("c");
		public final FieldAccessor<Integer> serverPort = getField("d");
	}
	public static class NMSPacket3Chat extends NMSPacket {
		public final FieldAccessor<Boolean> isFromServer = getField("c");
		public final FieldAccessor<String> message = getField("message");
	}
	public static class NMSPacket4UpdateTime extends NMSPacket {
		public final FieldAccessor<Long> age = getField("a");
		public final FieldAccessor<Long> timeOfDay = getField("b");
	}
	public static class NMSPacket5EntityEquipment extends NMSPacket30Entity {
		public final FieldAccessor<Integer> slot = getField("b");
		public final TranslatorFieldAccessor<ItemStack> item = getField("c").translate(ConversionPairs.itemStack);
	}
	public static class NMSPacket6SpawnPosition extends NMSPacket {
		public final FieldAccessor<Integer> x = getField("x");
		public final FieldAccessor<Integer> y = getField("y");
		public final FieldAccessor<Integer> z = getField("z");
	}
	public static class NMSPacket7UseEntity extends NMSPacket {
		public final FieldAccessor<Integer> playerId = getField("a");
		public final FieldAccessor<Integer> targetEntityId = getField("target");
		public final FieldAccessor<Integer> action = getField("action");
	}
	public static class NMSPacket8UpdateHealth extends NMSPacket {
		public final FieldAccessor<Short> health = getField("a");
		public final FieldAccessor<Short> food = getField("b");
		public final FieldAccessor<Short> foodSaturation = getField("c");
	}
	public static class NMSPacket9Respawn extends NMSPacket {
		public final FieldAccessor<Integer> x = getField("a");
		public final FieldAccessor<Integer> y = getField("b");
		public final FieldAccessor<Integer> z = getField("c");
		public final FieldAccessor<Object> gamemode = getField("d");
		public final FieldAccessor<Object> worldType = getField("e");
	}
	public static class NMSPacket10Flying extends NMSPacket {
		public final FieldAccessor<Double> x = getField("x");
		public final FieldAccessor<Double> y = getField("y");
		public final FieldAccessor<Double> z = getField("z");
		public final FieldAccessor<Double> stance = getField("stance");
		public final FieldAccessor<Float> yaw = getField("yaw");
		public final FieldAccessor<Float> pitch = getField("pitch");
		public final FieldAccessor<Boolean> onGround = getField("g");
		public final FieldAccessor<Boolean> hasPos = getField("hasPos");
		public final FieldAccessor<Boolean> hasLook = getField("hasLook");
	}
	public static class NMSPacket11PlayerPosition extends NMSPacket10Flying {
	}
	public static class NMSPacket12PlayerLook extends NMSPacket10Flying {
	}
	public static class NMSPacket13PlayerLookMove extends NMSPacket10Flying {
	}
	public static class NMSPacket14BlockDig extends NMSPacket {
		public final FieldAccessor<Integer> x = getField("a");
		public final FieldAccessor<Integer> y = getField("b");
		public final FieldAccessor<Integer> z = getField("c");
		public final FieldAccessor<Integer> face = getField("face");
		public final FieldAccessor<Integer> status = getField("e");
	}
	public static class NMSPacket15Place extends NMSPacket {
		public final FieldAccessor<Integer> x = getField("a");
		public final FieldAccessor<Integer> y = getField("b");
		public final FieldAccessor<Integer> z = getField("c");
		public final FieldAccessor<Integer> direction = getField("d");
		public final FieldAccessor<ItemStack> itemStack = getField("e").translate(ConversionPairs.itemStack);
		public final FieldAccessor<Float> cursorX = getField("f");
		public final FieldAccessor<Float> cursorY = getField("g");
		public final FieldAccessor<Float> cursorZ = getField("h");
	}
	public static class NMSPacket16BlockItemSwitch extends NMSPacket {
		public final FieldAccessor<Integer> itemInHandIndex = getField("itemInHandIndex");
		private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class);

		public CommonPacket newInstance(int itemInHandIndex) {
			return constructor1.newInstance(itemInHandIndex);
		}
	}
	public static class NMSPacket17EntityLocationAction extends NMSPacket30Entity {
		public final FieldAccessor<Integer> blockX = getField("b");
		public final FieldAccessor<Integer> blockY = getField("c");
		public final FieldAccessor<Integer> blockZ = getField("d");
		public final FieldAccessor<Integer> action = getField("e");
		private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(EntityRef.TEMPLATE.getType(), int.class, int.class, int.class, int.class);

		public CommonPacket newInstance(Entity entity, int action, int blockX, int blockY, int blockZ) {
			return constructor1.newInstance(Conversion.toEntityHandle.convert(entity), action, blockX, blockY, blockZ);
		}
	}
	public static class NMSPacket18ArmAnimation extends NMSPacket30Entity {
		public final FieldAccessor<Integer> animation = getField("b");
	}
	public static class NMSPacket19EntityAction extends NMSPacket30Entity {
		public final FieldAccessor<Integer> animation = getField("animation");
	}
	public static class NMSPacket20NamedEntitySpawn extends NMSPacket30Entity {
		public final FieldAccessor<String> entityName = getField("b");
		public final FieldAccessor<Integer> x = getField("c");
		public final FieldAccessor<Integer> y = getField("d");
		public final FieldAccessor<Integer> z = getField("e");
		public final FieldAccessor<Byte> yaw = getField("f");
		public final FieldAccessor<Byte> pitch = getField("g");
		public final FieldAccessor<Integer> heldItemId = getField("h");
		public final TranslatorFieldAccessor<DataWatcher> dataWatcher = getField("i").translate(ConversionPairs.dataWatcher);
	}
	public static class NMSPacket22Collect extends NMSPacket {
		public final FieldAccessor<Integer> collectedItemId = getField("a");
		public final FieldAccessor<Integer> collectorEntityId = getField("b");
	}
	public static class NMSPacket23VehicleSpawn extends NMSPacket30Entity {
		public final FieldAccessor<Integer> entityType = getField("j");
		public final FieldAccessor<Integer> x = getField("b");
		public final FieldAccessor<Integer> y = getField("c");
		public final FieldAccessor<Integer> z = getField("d");
		public final FieldAccessor<Byte> pitch = getField("h");
		public final FieldAccessor<Byte> yaw = getField("i");
		public final FieldAccessor<Integer> extraData = getField("k");
		public final FieldAccessor<Integer> motX = getField("e");
		public final FieldAccessor<Integer> motY = getField("f");
		public final FieldAccessor<Integer> motZ = getField("g");
		private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(EntityRef.TEMPLATE.getType(), int.class);

		public CommonPacket newInstance(org.bukkit.entity.Entity entity, int type) {
			return constructor1.newInstance(Conversion.toEntityHandle.convert(entity), type);
		}
	}
	public static class NMSPacket24MobSpawn extends NMSPacket30Entity {
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
		public final TranslatorFieldAccessor<DataWatcher> dataWatcher = getField("t").translate(ConversionPairs.dataWatcher);
		private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(CommonUtil.getNMSClass("EntityLiving"));
		public CommonPacket newInstance(Object entityLiving) {
			return constructor1.newInstance(entityLiving);
		}
	}
	public static class NMSPacket25EntityPainting extends NMSPacket30Entity {
		public final FieldAccessor<Integer> x = getField("b");
		public final FieldAccessor<Integer> y = getField("c");
		public final FieldAccessor<Integer> z = getField("d");
		public final FieldAccessor<BlockFace> facing = getField("e").translate(ConversionPairs.paintingFacing);
		public final FieldAccessor<String> art = getField("f");
	}
	public static class NMSPacket26AddExpOrb extends NMSPacket30Entity {
		public final FieldAccessor<Integer> x = getField("b");
		public final FieldAccessor<Integer> y = getField("c");
		public final FieldAccessor<Integer> z = getField("d");
		public final FieldAccessor<Integer> experience = getField("e");
	}
	public static class NMSPacket27PlayerInput extends NMSPacket {
		public final FieldAccessor<Float> sideways = getField("a");
		public final FieldAccessor<Float> forward = getField("b");
		public final FieldAccessor<Boolean> jump = getField("c");
		public final FieldAccessor<Boolean> unmount = getField("d");
	}
	public static class NMSPacket28EntityVelocity extends NMSPacket30Entity {
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
	public static class NMSPacket29DestroyEntity extends NMSPacket {
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
	public static class NMSPacket30Entity extends NMSPacket {
		public final FieldAccessor<Integer> entityId = getField("a");
	}
	public static class NMSPacket31RelEntityMove extends NMSPacket30Entity {
		public final FieldAccessor<Byte> dx = getField("b");
		public final FieldAccessor<Byte> dy = getField("c");
		public final FieldAccessor<Byte> dz = getField("d");
		private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, byte.class, byte.class, byte.class);

		public CommonPacket newInstance(int entityId, byte dx, byte dy, byte dz) {
			return constructor1.newInstance(entityId, dx, dy, dz);
		}
	}
	public static class NMSPacket32EntityLook extends NMSPacket30Entity {
		public final FieldAccessor<Byte> dyaw = getField("e");
		public final FieldAccessor<Byte> dpitch = getField("f");
		private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, byte.class, byte.class);

		public CommonPacket newInstance(int entityId, byte dyaw, byte dpitch) {
			return constructor1.newInstance(entityId, dyaw, dpitch);
		}
	}
	public static class NMSPacket33RelEntityMoveLook extends NMSPacket30Entity {
		public final FieldAccessor<Byte> dx = getField("b");
		public final FieldAccessor<Byte> dy = getField("c");
		public final FieldAccessor<Byte> dz = getField("d");
		public final FieldAccessor<Byte> dyaw = getField("e");
		public final FieldAccessor<Byte> dpitch = getField("f");
		private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, byte.class, byte.class, byte.class, byte.class, byte.class);

		public CommonPacket newInstance(int entityId, byte dx, byte dy, byte dz, byte dyaw, byte dpitch) {
			return constructor1.newInstance(entityId, dx, dy, dz, dyaw, dpitch);
		}
	}
	public static class NMSPacket34EntityTeleport extends NMSPacket30Entity {
		public final FieldAccessor<Integer> x = getField("b");
		public final FieldAccessor<Integer> y = getField("c");
		public final FieldAccessor<Integer> z = getField("d");
		public final FieldAccessor<Byte> yaw = getField("e");
		public final FieldAccessor<Byte> pitch = getField("f");
		private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(EntityRef.TEMPLATE.getType());
		private final SafeConstructor<CommonPacket> constructor2 = getPacketConstructor(int.class, int.class, int.class, int.class, byte.class, byte.class);

		public CommonPacket newInstance(org.bukkit.entity.Entity entity) {
			return constructor1.newInstance(Conversion.toEntityHandle.convert(entity));
		}
		public CommonPacket newInstance(int entityId, int x, int y, int z, byte yaw, byte pitch) {
			return constructor2.newInstance(entityId, x, y, z, yaw, pitch);
		}
	}
	public static class NMSPacket35EntityHeadRotation extends NMSPacket30Entity {
		public final FieldAccessor<Byte> headYaw = getField("b");
		private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, byte.class);

		public CommonPacket newInstance(int entityId, byte headRotation) {
			return constructor1.newInstance(entityId, headRotation);
		}
	}
	public static class NMSPacket38EntityStatus extends NMSPacket30Entity {
		public final FieldAccessor<Byte> status = getField("b");
	}
	public static class NMSPacket39AttachEntity extends NMSPacket {
		public final FieldAccessor<Integer> passengerId = getField("a");
		public final FieldAccessor<Integer> vehicleId = getField("b");
		public final FieldAccessor<Boolean> lead = getField("c");
		private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, EntityRef.TEMPLATE.getType(), EntityRef.TEMPLATE.getType());

		public CommonPacket newInstance(org.bukkit.entity.Entity passenger, org.bukkit.entity.Entity vehicle) {
			return constructor1.newInstance(Conversion.toEntityHandle.convert(passenger), Conversion.toEntityHandle.convert(vehicle));
		}
	}
	public static class NMSPacket40EntityMetadata extends NMSPacket30Entity {
		/** CraftBukkit uses rawtypes for this, so do we */
		@SuppressWarnings("rawtypes")
		public final FieldAccessor<List> watchedObjects = getField("b");
		private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, DataWatcherRef.TEMPLATE.getType(), boolean.class);

		public CommonPacket newInstance(int entityId, DataWatcher dataWatcher, boolean sendUnchangedData) {
			return constructor1.newInstance(entityId, dataWatcher.getHandle(), sendUnchangedData);
		}
	}
	public static class NMSPacket41MobEffect extends NMSPacket30Entity {
		public final FieldAccessor<Byte> effectId = getField("b");
		public final FieldAccessor<Byte> effectAmplifier = getField("c");
		public final FieldAccessor<Short> effectDuration = getField("d");
		private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, CommonUtil.getNMSClass("MobEffect"));

		public CommonPacket newInstance(int entityId, Object mobEffect) {
			return constructor1.newInstance(entityId, mobEffect);
		}
	}
	public static class NMSPacket42RemoveMobEffect extends NMSPacket30Entity {
		public final FieldAccessor<Byte> effectId = getField("b");
		private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, CommonUtil.getNMSClass("MobEffect"));

		public CommonPacket newInstance(int entityId, Object mobEffect) {
			return constructor1.newInstance(entityId, mobEffect);
		}
	}
	public static class NMSPacket43SetExperience extends NMSPacket {
		public final FieldAccessor<Float> bar = getField("a");
		public final FieldAccessor<Integer> level = getField("b");
		public final FieldAccessor<Integer> totalXp = getField("c");
		private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(float.class, int.class, int.class);
		
		public CommonPacket newInstance(float bar, int level, int totalXp) {
			return constructor1.newInstance(bar, level, totalXp);
		}
	}
	public static class NMSPacket44UpdateAttributes extends NMSPacket30Entity {
		public final FieldAccessor<Map<String, Double>> properties = getField("b");
	}
	public static class NMSPacket51MapChunk extends NMSPacket {
		public final FieldAccessor<Integer> size = getField("size");
		public final FieldAccessor<byte[]> buffer = getField("buffer");
		public final FieldAccessor<byte[]> inflatedBuffer = getField("inflatedBuffer");
		public final FieldAccessor<Boolean> hasBiomeData = getField("e");
		public final FieldAccessor<Integer> x = getField("a");
		public final FieldAccessor<Integer> z = getField("b");
		public final FieldAccessor<Integer> chunkDataBitMap = getField("c");
		public final FieldAccessor<Integer> chunkBiomeBitMap = getField("d");
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
	public static class NMSPacket52MultiBlockChange extends NMSPacket {
		public final FieldAccessor<Integer> chunkX = getField("a");
		public final FieldAccessor<Integer> chunkZ = getField("b");
		public final FieldAccessor<Integer> blockCount = getField("d");
		public final FieldAccessor<byte[]> blockData = getField("e");
	}
	public static class NMSPacket53BlockChange extends NMSPacket {
		public final FieldAccessor<Integer> x = getField("a");
		public final FieldAccessor<Integer> y = getField("b");
		public final FieldAccessor<Integer> z = getField("c");
		public final FieldAccessor<Integer> typeId = getField("material");
		public final FieldAccessor<Integer> data = getField("data");
	}
	public static class NMSPacket54PlayNoteBlock extends NMSPacket {
		public final FieldAccessor<Integer> x = getField("a");
		public final FieldAccessor<Integer> y = getField("b");
		public final FieldAccessor<Integer> z = getField("c");
		public final FieldAccessor<Integer> arg1 = getField("d");
		public final FieldAccessor<Integer> arg2 = getField("e");
		public final FieldAccessor<Integer> typeId = getField("f");
	}
	public static class NMSPacket55BlockBreakAnimation extends NMSPacket {
	}
	public static class NMSPacket56MapChunkBulk extends NMSPacket {
		public final FieldAccessor<int[]> bulk_x = getField("c");
		public final FieldAccessor<int[]> bulk_z = getField("d");
		public final FieldAccessor<int[]> bulk_chunkDataBitMap = getField("a");
		public final FieldAccessor<int[]> bulk_chunkBiomeBitMap = getField("b");
		public final FieldAccessor<byte[][]> inflatedBuffers = getField("inflatedBuffers");
		public final FieldAccessor<byte[]> buildBuffer = getField("buildBuffer");
		public final FieldAccessor<byte[]> deflatedData = getField("buffer");
		public final FieldAccessor<Integer> deflatedSize = getField("size");
		public final FieldAccessor<Boolean> hasSkyLight = getField("h");
		private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(List.class);

		public CommonPacket newInstance(List<Chunk> chunks) {
			return constructor1.newInstance(new ConvertingList<Object>(chunks, ConversionPairs.chunk.reverse()));
		}
	}
	public static class NMSPacket60Explosion extends NMSPacket {
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
			Vec3D vec = pushedForce == null ? null : Vec3D.a(pushedForce.getX(), pushedForce.getY(), pushedForce.getZ());
			return constructor1.newInstance(x, y, z, radius, blocks, vec);
		}
	}
	public static class NMSPacket61WorldEvent extends NMSPacket {
		public final FieldAccessor<Integer> effectId = getField("a");
		public final FieldAccessor<Integer> x = getField("c");
		public final FieldAccessor<Integer> y = getField("d");
		public final FieldAccessor<Integer> z = getField("e");
		public final FieldAccessor<Integer> data = getField("b");
		public final FieldAccessor<Boolean> noRelativeVolume = getField("f");
	}
	public static class NMSPacket62NamedSoundEffect extends NMSPacket {
		public final FieldAccessor<String> soundName = getField("a");
		public final FieldAccessor<Integer> x = getField("b");
		public final FieldAccessor<Integer> y = getField("c");
		public final FieldAccessor<Integer> z = getField("d");
		public final FieldAccessor<Float> volume = getField("e");
		public final FieldAccessor<Integer> pitch = getField("f");
	}
	public static class NMSPacket63WorldParticles extends NMSPacket {
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
	public static class NMSPacket70Bed extends NMSPacket {
		public final FieldAccessor<Integer> reason = getField("b");
		public final FieldAccessor<Integer> gamemode = getField("c");
	}
	public static class NMSPacket71Weather extends NMSPacket30Entity {
		public final FieldAccessor<Integer> type = getField("e");
		public final FieldAccessor<Integer> x = getField("b");
		public final FieldAccessor<Integer> y = getField("c");
		public final FieldAccessor<Integer> z = getField("d");
	}
	public static class NMSPacket100OpenWindow extends NMSPacket {
		public final FieldAccessor<Integer> windowId = getField("a");
		public final FieldAccessor<Integer> type = getField("b");
		public final FieldAccessor<String> title = getField("c");
		public final FieldAccessor<Integer> slotCount = getField("d");
	}
	public static class NMSPacket101CloseWindow extends NMSPacket {
		public final FieldAccessor<Integer> windowId = getField("a");
	}
	public static class NMSPacket102WindowClick extends NMSPacket {
		public final FieldAccessor<Integer> windowId = getField("a");
		public final FieldAccessor<Integer> slot = getField("slot");
		public final FieldAccessor<Integer> button = getField("button");
		public final FieldAccessor<Short> action = getField("d");
		public final FieldAccessor<ItemStack> item = getField("item").translate(ConversionPairs.itemStack);
		public final FieldAccessor<Integer> shift = getField("shift");
	}
	public static class NMSPacket103SetSlot extends NMSPacket {
		public final FieldAccessor<Integer> windowId = getField("a");
		public final FieldAccessor<Integer> slot = getField("b");
		public final FieldAccessor<ItemStack> item = getField("c").translate(ConversionPairs.itemStack);
	}
	public static class NMSPacket104WindowItems extends NMSPacket {
		public final FieldAccessor<Integer> windowId = getField("a");
		public final FieldAccessor<ItemStack[]> items = getField("b").translate(ConversionPairs.itemStackArr);
	}
	public static class NMSPacket105CraftProgressBar extends NMSPacket {
		public final FieldAccessor<Integer> windowId = getField("a");
		public final FieldAccessor<Integer> count = getField("b");
		public final FieldAccessor<Integer> data = getField("c");
	}
	public static class NMSPacket106Transaction extends NMSPacket {
		public final FieldAccessor<Integer> windowId = getField("a");
		public final FieldAccessor<Short> action = getField("b");
		public final FieldAccessor<Boolean> accapted = getField("c");
	}
	public static class NMSPacket107SetCreativeSlot extends NMSPacket {
		public final FieldAccessor<Integer> slot = getField("a");
		public final FieldAccessor<ItemStack> clicked = getField("b").translate(ConversionPairs.itemStack);
	}
	public static class NMSPacket108ButtonClick extends NMSPacket {
		public final FieldAccessor<Integer> windowId = getField("a");
		public final FieldAccessor<Integer> enchantment = getField("b");
	}
	public static class NMSPacket130UpdateSign extends NMSPacket {
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
	public static class NMSPacket131ItemData extends NMSPacket {
		public final FieldAccessor<Integer> type = getField("a");
		public final FieldAccessor<Integer> itemId = getField("b");
		public final FieldAccessor<byte[]> text = getField("c");
	}
	public static class NMSPacket132TileEntityData extends NMSPacket {
		public final FieldAccessor<Integer> x = getField("a");
		public final FieldAccessor<Integer> y = getField("b");
		public final FieldAccessor<Integer> z = getField("c");
		public final FieldAccessor<Integer> action = getField("d");
		public final FieldAccessor<Object> data = getField("e");
	}
	public static class NMSPacket133OpenTileEntity extends NMSPacket {
		public final FieldAccessor<Integer> id = getField("a");
		public final FieldAccessor<Integer> x = getField("b");
		public final FieldAccessor<Integer> y = getField("c");
		public final FieldAccessor<Integer> z = getField("d");
	}
	public static class NMSPacket200Statistic extends NMSPacket {
		public final FieldAccessor<Integer> id = getField("a");
		public final FieldAccessor<Integer> amount = getField("b");
	}
	public static class NMSPacket201PlayerInfo extends NMSPacket {
		public final FieldAccessor<String> playerName = getField("a");
		public final FieldAccessor<Boolean> online = getField("b");
		public final FieldAccessor<Integer> ping = getField("c");
	}
	public static class NMSPacket202Abilities extends NMSPacket {
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
	public static class NMSPacket203TabComplete extends NMSPacket {
	}
	public static class NMSPacket204LocaleAndViewDistance extends NMSPacket {
		public final FieldAccessor<String> locale = getField("a");
		public final FieldAccessor<Integer> viewDistance = getField("b");
		public final FieldAccessor<Integer> chatFlags = getField("c");
		public final FieldAccessor<Boolean> chatColorsEnabled = getField("d");
		public final TranslatorFieldAccessor<Difficulty> difficulty = getField("e").translate(ConversionPairs.difficulty);
		public final FieldAccessor<Boolean> showCape = getField("f");
	}
	public static class NMSPacket205ClientCommand extends NMSPacket {
		public final FieldAccessor<Integer> payload = getField("a");
	}
	public static class NMSPacket206SetScoreboardObjective extends NMSPacket {
		public final FieldAccessor<String> name = getField("a");
		public final FieldAccessor<String> displayName = getField("b");
		public final FieldAccessor<Integer> action = getField("c");
	}
	public static class NMSPacket207SetScoreboardScore extends NMSPacket {
		public final FieldAccessor<String> name = getField("a");
		public final FieldAccessor<String> objName = getField("b");
		public final FieldAccessor<Integer> value = getField("c");
		public final FieldAccessor<Integer> action = getField("d");
	}
	public static class NMSPacket208SetScoreboardDisplayObjective extends NMSPacket {
		public final FieldAccessor<Integer> display = getField("a");
		public final FieldAccessor<String> name = getField("b");
	}
	public static class NMSPacket209SetScoreboardTeam extends NMSPacket {
		public final FieldAccessor<String> team = getField("a");
		public final FieldAccessor<String> display = getField("b");
		public final FieldAccessor<String> prefix = getField("c");
		public final FieldAccessor<String> suffix = getField("d");
		public final FieldAccessor<Collection<String>> players = getField("e");
		public final FieldAccessor<Integer> mode = getField("f");
		public final FieldAccessor<Integer> friendlyFire = getField("g");
	}
	public static class NMSPacket250CustomPayload extends NMSPacket {
		public final FieldAccessor<String> tag = getField("tag");
		public final FieldAccessor<Integer> length = getField("length");
		public final FieldAccessor<byte[]> data = getField("data");
	}
	public static class NMSPacket252KeyResponse extends NMSPacket {
		public final FieldAccessor<byte[]> sharedSecret = getField("a");
		public final FieldAccessor<byte[]> tokenResponse = getField("b");
		public final FieldAccessor<SecretKey> secretKey = getField("c");
	}
	public static class NMSPacket253KeyRequest extends NMSPacket {
		public final FieldAccessor<String> serverId = getField("a");
		public final FieldAccessor<PublicKey> publicKey = getField("b");
		public final FieldAccessor<byte[]> verifyToken = getField("c");
	}
	public static class NMSPacket254GetInfo extends NMSPacket {
		public final FieldAccessor<Integer> magic = getField("a");
	}
	public static class NMSPacket255KickDisconnect extends NMSPacket {
		public final FieldAccessor<String> reason = getField("a");
	}

	static {
		// Validate that all packet types are implemented
		final HashSet<String> implemented = new HashSet<String>(256);
		for (Class<?> subClass : PacketFieldClasses.class.getDeclaredClasses()) {
			implemented.add(subClass.getSimpleName());
		}
		for (Class<?> packetClass : PacketUtil.getPacketClasses()) {
			// Ignore custom packet types
			if (!packetClass.getName().startsWith(Common.NMS_ROOT)) {
				continue;
			}
			// Get name of packet according to the classes in this Class
			if (!implemented.contains("NMS" + packetClass.getSimpleName())) {
				CommonPlugin.LOGGER_NETWORK.log(Level.WARNING, "Packet not implemented: " + packetClass.getSimpleName());
			}
		}
	}
}
