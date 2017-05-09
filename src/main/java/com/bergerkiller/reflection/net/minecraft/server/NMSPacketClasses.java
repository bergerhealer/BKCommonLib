package com.bergerkiller.reflection.net.minecraft.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.server.v1_11_R1.*;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion2.DuplexConversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.bukkit.common.wrappers.ScoreboardAction;
import com.bergerkiller.bukkit.common.wrappers.UseAction;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeConstructor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

public class NMSPacketClasses {

    public static class NMSPacket extends PacketType {

        protected NMSPacket(Class<?> packetClass) {
            super(packetClass);
        }

        public NMSPacket() {
            super();
        }

        protected SafeConstructor<CommonPacket> getPacketConstructor(Class<?>... args) {
            return getConstructor(args).translateOutput(Conversion.toCommonPacket);
        }

        @Override
        public CommonPacket newInstance() {
            SafeConstructor<CommonPacket> constructor0 = getPacketConstructor();
            return constructor0.newInstance();
        }
    }

    /*
     * ========================================================================================
     * ============================= Incoming packets start ===================================
     * ========================================================================================
     */

    public static class NMSPacketPlayInAbilities extends NMSPacket {

        public final FieldAccessor<Boolean> isInvulnerable = nextField("private boolean a");
        public final FieldAccessor<Boolean> isFlying = nextFieldSignature("private boolean b");
        public final FieldAccessor<Boolean> canFly = nextFieldSignature("private boolean c");
        public final FieldAccessor<Boolean> canInstantlyBuild = nextFieldSignature("private boolean d");
        public final FieldAccessor<Float> flySpeed = nextField("private float e");
        public final FieldAccessor<Float> walkSpeed = nextField("private float f");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(NMSPlayerAbilities.T.getType());

        public CommonPacket newInstance(PlayerAbilities abilities) {
            return constructor1.newInstance(abilities.getHandle());
        }
    }

    public static class NMSPacketPlayInArmAnimation extends NMSPacket {

        public final TranslatorFieldAccessor<MainHand> enumHand = nextField("private EnumHand a").translate(DuplexConversion.mainHand);
    }
    
    public static class NMSPacketPlayInBlockDig extends NMSPacket {

        public final FieldAccessor<IntVector3> position = nextField("private BlockPosition a").translate(DuplexConversion.blockPosition);
        public final FieldAccessor<Object> face = nextFieldSignature("private EnumDirection b");
        public final FieldAccessor<Object> status = nextFieldSignature("private EnumPlayerDigType c");
    }
    
    public static class NMSPacketPlayInBlockPlace extends NMSPacket {

        public final TranslatorFieldAccessor<MainHand> enumHand = nextField("private EnumHand a").translate(DuplexConversion.mainHand);
        public final FieldAccessor<Long> timestamp = nextField("public long timestamp");
    }
    
    public static class NMSPacketPlayInBoatMove extends NMSPacket {
        
        public final FieldAccessor<Boolean> arg1 = nextField("private boolean a");
        public final FieldAccessor<Boolean> arg2 = nextFieldSignature("private boolean b");
    }
    
    public static class NMSPacketPlayInChat extends NMSPacket {

        public final FieldAccessor<String> message = nextField("private String a");
    }
    
    public static class NMSPacketPlayInClientCommand extends NMSPacket {

        public final FieldAccessor<Object> command = nextField("private EnumClientCommand a");
    }
    
    public static class NMSPacketPlayInCloseWindow extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int id");
    }
    
    public static class NMSPacketPlayInCustomPayload extends NMSPacket {

        public final FieldAccessor<String> tag = nextField("private String a");
        public final FieldAccessor<Object> data = nextFieldSignature("private PacketDataSerializer b");
    }

    public static class NMSPacketPlayInEnchantItem extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextFieldSignature("private int a");
        public final FieldAccessor<Integer> enchantment = nextField("private int b");
    }

    public static class NMSPacketPlayInEntityAction extends NMSPacket {

        public final FieldAccessor<Integer> playerId = nextField("private int a");
        public final FieldAccessor<Object> actionId = nextFieldSignature("private EnumPlayerAction animation");
        public final FieldAccessor<Integer> jumpBoost = nextFieldSignature("private int c");
    }

    public static class NMSPacketPlayInFlying extends NMSPacket {

        public final FieldAccessor<Double> x = nextField("protected double x");
        public final FieldAccessor<Double> y = nextField("protected double y");
        public final FieldAccessor<Double> z = nextField("protected double z");
        public final FieldAccessor<Float> yaw = nextField("protected float yaw");
        public final FieldAccessor<Float> pitch = nextField("protected float pitch");
        public final FieldAccessor<Boolean> onGround = nextFieldSignature("protected boolean f");
        public final FieldAccessor<Boolean> hasPos = nextField("protected boolean hasPos");
        public final FieldAccessor<Boolean> hasLook = nextField("protected boolean hasLook");
    }

    public static class NMSPacketPlayInHeldItemSlot extends NMSPacket {

        public final FieldAccessor<Integer> slot = nextField("private int itemInHandIndex");
    }

    public static class NMSPacketPlayInKeepAlive extends NMSPacket {

        public final FieldAccessor<Integer> key = nextField("private int a");
    }

    public static class NMSPacketPlayInResourcePackStatus extends NMSPacket {

        public final FieldAccessor<Object> enumStatus = nextField("public EnumResourcePackStatus status");
    }

    public static class NMSPacketPlayInSetCreativeSlot extends NMSPacket {

        public final FieldAccessor<Integer> slot = nextField("private int slot");
        public final FieldAccessor<Object> item = nextFieldSignature("private ItemStack b");
    }

    public static class NMSPacketPlayInSettings extends NMSPacket {

        public final FieldAccessor<String> lang = nextField("private String a");
        public final FieldAccessor<Integer> view = nextFieldSignature("private int b");
        public final FieldAccessor<Object> chatVisibility = nextFieldSignature("private EntityHuman.EnumChatVisibility c");
        public final FieldAccessor<Boolean> enableColors = nextFieldSignature("private boolean d");
        public final FieldAccessor<Integer> modelPartFlags = nextFieldSignature("private int e");
        public final FieldAccessor<Object> mainHand = nextFieldSignature("private EnumMainHand f");
    }

    public static class NMSPacketPlayInSpectate extends NMSPacket {
        
        public final FieldAccessor<UUID> uuid = nextField("private UUID a");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(UUID.class);

        public CommonPacket newInstance(UUID uuid) {
            return constructor1.newInstance(uuid);
        }
    }

    public static class NMSPacketPlayInSteerVehicle extends NMSPacket {

        public final FieldAccessor<Float> sideways = nextField("private float a");
        public final FieldAccessor<Float> forwards = nextFieldSignature("private float b");
        public final FieldAccessor<Boolean> jump = nextFieldSignature("private boolean c");
        public final FieldAccessor<Boolean> unmount = nextFieldSignature("private boolean d");
    }

    public static class NMSPacketPlayInTabComplete extends NMSPacket {

        public final FieldAccessor<String> message = nextField("private String a");
        public final FieldAccessor<Boolean> unknown1 = nextFieldSignature("private boolean b");
        public final FieldAccessor<IntVector3> position = nextFieldSignature("private BlockPosition c").translate(DuplexConversion.blockPosition);
    }

    public static class NMSPacketPlayInTeleportAccept extends NMSPacket {

        public final FieldAccessor<Integer> unknown1 = nextField("private int a");
    }

    public static class NMSPacketPlayInTransaction extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int a");
        public final FieldAccessor<Short> action = nextFieldSignature("private short b");
        public final FieldAccessor<Boolean> accepted = nextFieldSignature("private boolean c");
    }
    
    public static class NMSPacketPlayInUpdateSign extends NMSPacket {

        public final FieldAccessor<IntVector3> position = nextField("private BlockPosition a").translate(DuplexConversion.blockPosition);
        public final FieldAccessor<String[]> lines = nextFieldSignature("private String[] b");

        public Block getBlock(CommonPacket packet, World world) {
            return BlockUtil.getBlock(world, position.get(packet.getHandle()));
        }

        public void setBlock(CommonPacket packet, Block block) {
            position.set(packet.getHandle(), new IntVector3(block));
        }
    }

    public static class NMSPacketPlayInUseEntity extends NMSPacket {

        public final FieldAccessor<Integer> clickedEntityId = nextFieldSignature("private int a");
        public final TranslatorFieldAccessor<UseAction> useAction = nextFieldSignature("private EnumEntityUseAction action").translate(DuplexConversion.useAction);
        public final TranslatorFieldAccessor<Vector> offset = nextFieldSignature("private Vec3D c").translate(DuplexConversion.vector);
        public final TranslatorFieldAccessor<MainHand> hand = nextFieldSignature("private EnumHand d").translate(DuplexConversion.mainHand);
    }

    public static class NMSPacketPlayInUseItem extends NMSPacket {

        public final TranslatorFieldAccessor<IntVector3> position = nextField("private BlockPosition a").translate(DuplexConversion.blockPosition);
        public final FieldAccessor<Object> direction = nextFieldSignature("private EnumDirection b");
        public final TranslatorFieldAccessor<MainHand> hand = nextFieldSignature("private EnumHand c").translate(DuplexConversion.mainHand);
        public final FieldAccessor<Float> unknown1 = nextFieldSignature("private float d");
        public final FieldAccessor<Float> unknown2 = nextFieldSignature("private float e");
        public final FieldAccessor<Float> unknown3 = nextFieldSignature("private float f");
        public final FieldAccessor<Long> timestamp = nextField("public long timestamp");
    }

    public static class NMSPacketPlayInVehicleMove extends NMSPacket {

        public final FieldAccessor<Double> dx = nextField("private double a");
        public final FieldAccessor<Double> dy = nextFieldSignature("private double b");
        public final FieldAccessor<Double> dz = nextFieldSignature("private double c");
        public final FieldAccessor<Float> dyaw = nextFieldSignature("private float d");
        public final FieldAccessor<Float> dpitch = nextFieldSignature("private float e");
    }

    public static class NMSPacketPlayInWindowClick extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int a");
        public final FieldAccessor<Integer> slot = nextField("private int slot");
        public final FieldAccessor<Integer> button = nextField("private int button");
        public final FieldAccessor<Short> action = nextFieldSignature("private short d");
        public final FieldAccessor<ItemStack> item = nextField("private ItemStack item").translate(DuplexConversion.itemStack);
        public final FieldAccessor<Object> shift = nextFieldSignature("private InventoryClickType shift");
    }

    /*
     * ========================================================================================
     * ============================= Outgoing packets start ===================================
     * ========================================================================================
     */

    public static class NMSPacketPlayOutAbilities extends NMSPacket {

        public final FieldAccessor<Boolean> isInvulnerable = nextField("private boolean a");
        public final FieldAccessor<Boolean> isFlying = nextFieldSignature("private boolean b");
        public final FieldAccessor<Boolean> canFly = nextFieldSignature("private boolean c");
        public final FieldAccessor<Boolean> canInstantlyBuild = nextFieldSignature("private boolean d");
        public final FieldAccessor<Float> flySpeed = nextFieldSignature("private float e");
        public final FieldAccessor<Float> walkSpeed = nextFieldSignature("private float f");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(NMSPlayerAbilities.T.getType());

        public CommonPacket newInstance(PlayerAbilities abilities) {
            return constructor1.newInstance(abilities.getHandle());
        }
    }

    public static class NMSPacketPlayOutAnimation extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<Integer> animation = nextFieldSignature("private int b");
    }

    /*
     * Note: is used exclusvely to attach a leash to an entity
     * It no longer implies anything about vehicles or passengers
     * The mount packet is for that instead
     */
    public static class NMSPacketPlayOutAttachEntity extends NMSPacket {

        public final FieldAccessor<Integer> vehicleId = nextField("private int a");
        public final FieldAccessor<Integer> passengerId = nextFieldSignature("private int b");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(NMSEntity.T.getType(), NMSEntity.T.getType());

        public CommonPacket newInstance(org.bukkit.entity.Entity passenger, org.bukkit.entity.Entity vehicle) {
            return constructor1.newInstance(Conversion.toEntityHandle.convert(passenger), Conversion.toEntityHandle.convert(vehicle));
        }
    }

    public static class NMSPacketPlayOutBed extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final TranslatorFieldAccessor<IntVector3> bedPosition = nextFieldSignature("private BlockPosition b").translate(DuplexConversion.blockPosition);
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(NMSEntityHuman.T.getType(), BlockPosition.class);

        public CommonPacket newInstance(HumanEntity entity, IntVector3 bedPosition) {
            return constructor1.newInstance(Conversion.toEntityHandle.convert(entity), Conversion.toBlockPositionHandle.convert(bedPosition));
        }
    }
    
    public static class NMSPacketPlayOutBlockAction extends NMSPacket {

        public final TranslatorFieldAccessor<IntVector3> position = nextField("private BlockPosition a").translate(DuplexConversion.blockPosition);
        public final FieldAccessor<Integer> unknown1 = nextFieldSignature("private int b");
        public final FieldAccessor<Integer> unknown2 = nextFieldSignature("private int c");
        public final FieldAccessor<Material> type = nextFieldSignature("private Block d").translate(DuplexConversion.block);
    }

    public static class NMSPacketPlayOutBlockBreakAnimation extends NMSPacket {

        public final FieldAccessor<Integer> unknown1 = nextField("private int a");
        public final TranslatorFieldAccessor<IntVector3> position = nextFieldSignature("private BlockPosition b").translate(DuplexConversion.blockPosition);
        public final FieldAccessor<Integer> unknown2 = nextFieldSignature("private int c");
    }

    public static class NMSPacketPlayOutBlockChange extends NMSPacket {

        public final TranslatorFieldAccessor<IntVector3> position = nextField("private BlockPosition a").translate(DuplexConversion.blockPosition);
        public final TranslatorFieldAccessor<BlockData> blockData = nextFieldSignature("public IBlockData block").translate(DuplexConversion.blockData);
    }

    public static class NMSPacketPlayOutBoss extends NMSPacket {
        public final FieldAccessor<UUID> entityUUID = nextField("private UUID a");
        public final FieldAccessor<Object> action = nextFieldSignature("private Action b");
        public final FieldAccessor<Object> chat = nextFieldSignature("private IChatBaseComponent c");
        public final FieldAccessor<Float> progress = nextFieldSignature("private float d");
        public final FieldAccessor<Object> bossBarColor = nextFieldSignature("private BossBattle.BarColor e");
        public final FieldAccessor<Object> bossBarStyle = nextFieldSignature("private BossBattle.BarStyle f");
        public final FieldAccessor<Boolean> unknown1 = nextFieldSignature("private boolean g");
        public final FieldAccessor<Boolean> unknown2 = nextFieldSignature("private boolean h");
        public final FieldAccessor<Boolean> unknown3 = nextFieldSignature("private boolean i");
    }

    public static class NMSPacketPlayOutCamera extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("public int a");
    }

    public static class NMSPacketPlayOutChat extends NMSPacket {
        public NMSPacketPlayOutChat() {
            this.addImport("net.md_5.bungee.api.chat.BaseComponent[]");
            this.chatComponent = nextField("private IChatBaseComponent a");
            this.components = nextField("public BaseComponent[] components");
            this.unknown1 = nextFieldSignature("private byte b");    
        }

        public final FieldAccessor<Object> chatComponent;
        public final FieldAccessor<Object[]> components;
        public final FieldAccessor<Byte> unknown1;
    }

    public static class NMSPacketPlayOutCloseWindow extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int a");
    }    

    public static class NMSPacketPlayOutCollect extends NMSPacket {

        public final FieldAccessor<Integer> collectedItemId = nextField("private int a");
        public final FieldAccessor<Integer> collectorEntityId = nextFieldSignature("private int b");
        public final FieldAccessor<Integer> unknown1 = nextFieldSignature("private int c");
    }

    public static class NMSPacketPlayOutCombatEvent extends NMSPacket {

          public final FieldAccessor<Object> eventType = nextField("public EnumCombatEventType a");
          public final FieldAccessor<Integer> unknown2 = nextFieldSignature("public int b");
          public final FieldAccessor<Integer> unknown3 = nextFieldSignature("public int c");
          public final FieldAccessor<Integer> unknown4 = nextFieldSignature("public int d");
          public final FieldAccessor<Object> chatComponent = nextFieldSignature("public IChatBaseComponent e");
    }

    public static class NMSPacketPlayOutCustomPayload extends NMSPacket {

        public final FieldAccessor<String> tag = nextField("private String a");
        public final FieldAccessor<Object> data = nextFieldSignature("private PacketDataSerializer b");
    }

    public static class NMSPacketPlayOutCustomSoundEffect extends NMSPacket {

          public final FieldAccessor<?> soundName = nextFieldSignature("private String a");
          public final FieldAccessor<?> soundCategory = nextFieldSignature("private SoundCategory b");
          public final FieldAccessor<Integer> unknown1 = nextFieldSignature("private int c");
          public final FieldAccessor<Integer> unknown2 = nextFieldSignature("private int d");
          public final FieldAccessor<Integer> unknown3 = nextFieldSignature("private int e");
          public final FieldAccessor<Float> volume = nextFieldSignature("private float f");
          public final FieldAccessor<Float> pitch = nextFieldSignature("private float g");
    }

    /// ====================== NMSPacketPlayOutEntity and derivatives ===========================

    public static class NMSPacketPlayOutEntity extends NMSPacket {

        public NMSPacketPlayOutEntity() {
            super();
        }

        protected NMSPacketPlayOutEntity(Class<?> packetClass) {
            super(packetClass);
        }

        public final FieldAccessor<Integer> entityId = getField("a", int.class);
        // Note: field boolean h is a flag indicating that 'look' information is contained
        // this flag is automatically set on packet construction
    }

    public static class NMSPacketPlayOutRelEntityMove extends NMSPacketPlayOutEntity {

        public NMSPacketPlayOutRelEntityMove() {
            super(CommonUtil.getNMSClass("PacketPlayOutEntity.PacketPlayOutRelEntityMove"));
        }

        public final FieldAccessor<Integer> dx = getField("b", int.class);
        public final FieldAccessor<Integer> dy = getField("c", int.class);
        public final FieldAccessor<Integer> dz = getField("d", int.class);
        public final FieldAccessor<Boolean> onGround = getField("g", boolean.class);
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, long.class, long.class, long.class, boolean.class);

        public CommonPacket newInstance(int paramInt, long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean) {
            return constructor1.newInstance(paramInt,paramLong1,paramLong2,paramLong3,paramBoolean);
        }
    }

    public static class NMSPacketPlayOutRelEntityMoveLook extends NMSPacketPlayOutEntity {

        public NMSPacketPlayOutRelEntityMoveLook() {
            super(CommonUtil.getNMSClass("PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook"));
        }

        public final FieldAccessor<Integer> dx = getField("b", int.class);
        public final FieldAccessor<Integer> dy = getField("c", int.class);
        public final FieldAccessor<Integer> dz = getField("d", int.class);
        public final FieldAccessor<Byte> dyaw = getField("e", byte.class);
        public final FieldAccessor<Byte> dpitch = getField("f", byte.class);
        public final FieldAccessor<Boolean> onGround = getField("g", boolean.class);
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, long.class, long.class, long.class, byte.class, byte.class, boolean.class);

        public CommonPacket newInstance(int entityId, long dx, long dy, long dz, byte dyaw, byte dpitch, boolean onGround) {
            return constructor1.newInstance(entityId, dx, dy, dz, dyaw, dpitch, onGround);
        }
    }

    public static class NMSPacketPlayOutEntityLook extends NMSPacketPlayOutEntity {

        public NMSPacketPlayOutEntityLook() {
            super(CommonUtil.getNMSClass("PacketPlayOutEntity.PacketPlayOutEntityLook"));
        }

        public final FieldAccessor<Byte> yaw = getField("e", byte.class);
        public final FieldAccessor<Byte> pitch = getField("f", byte.class);
        public final FieldAccessor<Boolean> onGround = getField("g", boolean.class);
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, byte.class, byte.class, boolean.class);

        public CommonPacket newInstance(int entityId, byte dyaw, byte dpitch, boolean onGround) {
            return constructor1.newInstance(entityId, dyaw, dpitch, onGround);
        }
    }

    // ====================================================================================================

    public static class NMSPacketPlayOutEntityDestroy extends NMSPacket {

        public final FieldAccessor<int[]> entityIds = getField("a", int[].class);
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

    public static class NMSPacketPlayOutEntityEffect extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<Byte> effectId = nextFieldSignature("private byte b");
        public final FieldAccessor<Byte> effectAmplifier = nextFieldSignature("private byte c");
        public final FieldAccessor<Integer> effectDuration = nextFieldSignature("private int d");
        public final FieldAccessor<Byte> effectFlags = nextFieldSignature("private byte e");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, NMSMobEffect.T.getType());

        public CommonPacket newInstance(int entityId, Object mobEffect) {
            return constructor1.newInstance(entityId,  Conversion.toMobEffect.convert(mobEffect));
        }

        public CommonPacket newInstance(int entityId, PotionEffect effect) {
            return constructor1.newInstance(entityId, Conversion.toMobEffect.convert(effect));
        }
    }

    public static class NMSPacketPlayOutEntityEquipment extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<EnumItemSlot> slot = nextFieldSignature("private EnumItemSlot b");
        public final TranslatorFieldAccessor<ItemStack> item = nextFieldSignature("private ItemStack c").translate(DuplexConversion.itemStack);
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, CommonUtil.getNMSClass("EnumItemSlot"), NMSItemStack.T.getType());

        public CommonPacket newInstance(int entityId, Object enumItemSlot, ItemStack item) {
            return constructor1.newInstance(entityId, enumItemSlot, Conversion.toItemStackHandle.convert(item));
        }
    }

    public static class NMSPacketPlayOutEntityHeadRotation extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<Byte> headYaw = nextFieldSignature("private byte b");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(NMSEntity.T.getType(), byte.class);

        public CommonPacket newInstance(org.bukkit.entity.Entity entity, byte headRotation) {
            return constructor1.newInstance(Conversion.toEntityHandle.convert(entity), headRotation);
        }
    }

    public static class NMSPacketPlayOutEntityMetadata extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<List<?>> watchedObjects = nextFieldSignature("private List<DataWatcher.Item<?>> b");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, NMSDataWatcher.T.getType(), boolean.class);

        public CommonPacket newInstance(int entityId, DataWatcher dataWatcher, boolean sendUnchangedData) {
            return constructor1.newInstance(entityId, dataWatcher.getHandle(), sendUnchangedData);
        }
    }

    public static class NMSPacketPlayOutEntityStatus extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<Byte> status = nextFieldSignature("private byte b");
    }

    public static class NMSPacketPlayOutEntityTeleport extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<Double> x = nextFieldSignature("private double b");
        public final FieldAccessor<Double> y = nextFieldSignature("private double c");
        public final FieldAccessor<Double> z = nextFieldSignature("private double d");
        public final FieldAccessor<Byte> yaw = nextFieldSignature("private byte e");
        public final FieldAccessor<Byte> pitch = nextFieldSignature("private byte f");
        public final FieldAccessor<Boolean> onGround = nextFieldSignature("private boolean g");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(NMSEntity.T.getType());

        public CommonPacket newInstance(org.bukkit.entity.Entity entity) {
            return constructor1.newInstance(Conversion.toEntityHandle.convert(entity));
        }

        public CommonPacket newInstance(int entityId, double posX, double posY, double posZ, byte yaw, byte pitch, boolean onGround) {
            CommonPacket packet = this.newInstance();
            packet.write(this.entityId, entityId);
            packet.write(this.x, posX);
            packet.write(this.y, posY);
            packet.write(this.z, posZ);
            packet.write(this.yaw, yaw);
            packet.write(this.pitch, pitch);
            packet.write(this.onGround, onGround);
            return packet;
        }
    }

    public static class NMSPacketPlayOutEntityVelocity extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<Integer> motX = nextFieldSignature("private int b");
        public final FieldAccessor<Integer> motY = nextFieldSignature("private int c");
        public final FieldAccessor<Integer> motZ = nextFieldSignature("private int d");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(NMSEntity.T.getType());
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

        public final FieldAccessor<Float> bar = nextField("private float a");
        public final FieldAccessor<Integer> level = nextFieldSignature("private int b");
        public final FieldAccessor<Integer> totalXp = nextFieldSignature("private int c");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(float.class, int.class, int.class);

        public CommonPacket newInstance(float bar, int level, int totalXp) {
            return constructor1.newInstance(bar, level, totalXp);
        }
    }

    public static class NMSPacketPlayOutExplosion extends NMSPacket {

        public final FieldAccessor<Double> x = nextField("private double a");
        public final FieldAccessor<Double> y = nextFieldSignature("private double b");
        public final FieldAccessor<Double> z = nextFieldSignature("private double c");
        public final FieldAccessor<Float> radius = nextFieldSignature("private float d");
        public final FieldAccessor<List<Object>> blocks = nextFieldSignature("private List<BlockPosition> e");
        public final FieldAccessor<Float> pushMotX = nextFieldSignature("private float f");
        public final FieldAccessor<Float> pushMotY = nextFieldSignature("private float g");
        public final FieldAccessor<Float> pushMotZ = nextFieldSignature("private float h");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(double.class, double.class, double.class, float.class, List.class, Vec3D.class);

        @SuppressWarnings("unchecked")
        public CommonPacket newInstance(double x, double y, double z, float radius) {
            return newInstance(x, y, z, radius, Collections.EMPTY_LIST);
        }

        public CommonPacket newInstance(double x, double y, double z, float radius, List<IntVector3> blocks) {
            return newInstance(x, y, z, radius, blocks, null);
        }

        public CommonPacket newInstance(double x, double y, double z, float radius, List<IntVector3> blocks, Vector pushedForce) {
            Object vec = (pushedForce == null) ? null : NMSVector.newVec(pushedForce.getX(), pushedForce.getY(), pushedForce.getZ());
            List<Object> blocksHandles = new ArrayList<Object>(blocks.size());
            for (IntVector3 block : blocks) {
                blocksHandles.add(NMSVector.newPosition(block.x, block.y, block.z));
            }
            return constructor1.newInstance(x, y, z, radius, blocksHandles, vec);
        }
    }
    
    public static class NMSPacketPlayOutGameStateChange extends NMSPacket {

        public final FieldAccessor<Integer> unknown1 = nextField("private int b");
        public final FieldAccessor<Float> unknown2 = nextFieldSignature("private float c");
    }

    public static class NMSPacketPlayOutHeldItemSlot extends NMSPacket {

        public final FieldAccessor<Integer> slot = getField("a", int.class);
    }

    public static class NMSPacketPlayOutKeepAlive extends NMSPacket {

        public final FieldAccessor<Integer> key = getField("a", int.class);
    }

    public static class NMSPacketPlayOutKickDisconnect extends NMSPacket {

        public final FieldAccessor<Object> reason = nextField("private IChatBaseComponent a");
    }

    public static class NMSPacketPlayOutLogin extends NMSPacket {

        public final FieldAccessor<Integer> playerId = nextField("private int a");
        public final FieldAccessor<Boolean> hardcore = nextFieldSignature("private boolean b");
        public final TranslatorFieldAccessor<GameMode> gameMode = nextFieldSignature("private EnumGamemode c").translate(DuplexConversion.gameMode);
        public final FieldAccessor<Integer> dimension = nextFieldSignature("private int d");
        public final TranslatorFieldAccessor<Difficulty> difficulty = nextFieldSignature("private EnumDifficulty e").translate(DuplexConversion.difficulty);
        public final FieldAccessor<Integer> maxPlayers = nextFieldSignature("private int f");
        public final TranslatorFieldAccessor<WorldType> worldType = nextFieldSignature("private WorldType g").translate(DuplexConversion.worldType);
        public final FieldAccessor<Boolean> unknown1 = nextFieldSignature("private boolean h"); // Unknown field
    }

    public static class NMSPacketPlayOutMap extends NMSPacket {

        public final FieldAccessor<Integer> itemId = nextField("private int a");
        public final FieldAccessor<Byte> scale = nextFieldSignature("private byte b");
        public final FieldAccessor<Boolean> track = nextFieldSignature("private boolean c");
        public final FieldAccessor<MapCursor[]> cursors = nextFieldSignature("private MapIcon[] d").translate(DuplexConversion.mapCursorArray);
        public final FieldAccessor<Integer> xmin = nextFieldSignature("private int e");
        public final FieldAccessor<Integer> ymin = nextFieldSignature("private int f");
        public final FieldAccessor<Integer> width = nextFieldSignature("private int g");
        public final FieldAccessor<Integer> height = nextFieldSignature("private int h");
        public final FieldAccessor<byte[]> pixels = nextFieldSignature("private byte[] i");
    }

    public static class NMSPacketPlayOutMapChunk extends NMSPacket {

        public final FieldAccessor<Integer> x = nextField("private int a");
        public final FieldAccessor<Integer> z = nextFieldSignature("private int b");
        public final FieldAccessor<Integer> sectionsMask = nextFieldSignature("private int c");
        public final FieldAccessor<byte[]> data = nextFieldSignature("private byte[] d");
        public final FieldAccessor<List<CommonTagCompound>> tags = nextFieldSignature("private List<NBTTagCompound> e");
        public final FieldAccessor<Boolean> hasBiomeData = nextFieldSignature("private boolean f");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(NMSChunk.T.getType(), int.class);

        public CommonPacket newInstance(Chunk chunk) {
            return newInstance(Conversion.toChunkHandle.convert(chunk));
        }

        public CommonPacket newInstance(Object chunk) {
            return newInstance(chunk, 0xFFFF);
        }

        public CommonPacket newInstance(Object chunk, int sectionsMask) {
            return constructor1.newInstance(chunk, sectionsMask);
        }
    }
    
    public static class NMSPacketPlayOutMount extends NMSPacket {
        
        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<int[]> mountedEntityIds = nextFieldSignature("private int[] b");

        public CommonPacket newInstance(org.bukkit.entity.Entity entity, List<org.bukkit.entity.Entity> passengers) {
            int[] passengerIds = new int[passengers.size()];
            for (int i = 0; i < passengerIds.length; i++) {
                passengerIds[i] = passengers.get(i).getEntityId();
            }

            CommonPacket packet = this.newInstance();
            packet.write(entityId, entity.getEntityId());
            packet.write(mountedEntityIds, passengerIds);
            return packet;
        }
    }

    public static class NMSPacketPlayOutMultiBlockChange extends NMSPacket {

        public final FieldAccessor<IntVector2> chunk = nextField("private ChunkCoordIntPair a").translate(DuplexConversion.chunkIntPair);
        public final FieldAccessor<Object[]> blockChangeInfoArray = nextFieldSignature("private MultiBlockChangeInfo[] b");

        public ChangeInfo CHANGE_INFO = new ChangeInfo(resolveClass("MultiBlockChangeInfo"));

        public class ChangeInfo {
            public final ClassTemplate<?> T;
            public final FieldAccessor<Short> typeId;
            public final FieldAccessor<Object> data;
            
            protected ChangeInfo(Class<?> clazz) {
                T = ClassTemplate.create(clazz);
                typeId = T.nextField("private final short b");
                data = T.nextFieldSignature("private final IBlockData c");
            }
        }
    }

    public static class NMSPacketPlayOutNamedEntitySpawn extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<UUID> uuid = nextFieldSignature("private UUID b");
        public final FieldAccessor<Double> x = nextFieldSignature("private double c");
        public final FieldAccessor<Double> y = nextFieldSignature("private double d");
        public final FieldAccessor<Double> z = nextFieldSignature("private double e");
        public final FieldAccessor<Byte> yaw = nextFieldSignature("private byte f");
        public final FieldAccessor<Byte> pitch = nextFieldSignature("private byte g");
        public final FieldAccessor<DataWatcher> dataWatcher = nextFieldSignature("private DataWatcher h").translate(DuplexConversion.dataWatcher);
        public final FieldAccessor<List<Object>> watchedObjects = nextFieldSignature("private List<DataWatcher.Item<?>> i");
    }
   
    public static class NMSPacketPlayOutNamedSoundEffect extends NMSPacket {

        public final FieldAccessor<Object> sound = nextField("private SoundEffect a");
        public final FieldAccessor<Object> category = nextFieldSignature("private SoundCategory b");
        public final FieldAccessor<Integer> x = nextFieldSignature("private int c");
        public final FieldAccessor<Integer> y = nextFieldSignature(" private int d");
        public final FieldAccessor<Integer> z = nextFieldSignature("private int e");
        public final FieldAccessor<Float> volume = nextFieldSignature("private float f");
        public final FieldAccessor<Float> pitch = nextFieldSignature("private float g");
    }

    public static class NMSPacketPlayOutOpenSignEditor extends NMSPacket {

        public final FieldAccessor<IntVector3> signPosition = nextField("private BlockPosition a").translate(DuplexConversion.blockPosition);
    }

    public static class NMSPacketPlayOutOpenWindow extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int a");
        public final FieldAccessor<String> windowType = nextFieldSignature("private String b");
        public final FieldAccessor<Object> windowTitle = nextFieldSignature("private IChatBaseComponent c");
        public final FieldAccessor<Integer> slotCount = nextFieldSignature("private int d");
        public final FieldAccessor<Integer> horseEntityId = nextFieldSignature("private int e");
    }

    public static class NMSPacketPlayOutPlayerInfo extends NMSPacket {

        public final FieldAccessor<Object> action = nextField("private EnumPlayerInfoAction a");
        public final FieldAccessor<List<?>> playerInfoData = nextFieldSignature("private final List<PlayerInfoData> b");
    }

    public static class NMSPacketPlayOutPlayerListHeaderFooter extends NMSPacket {

        public final FieldAccessor<Object> titleTop = nextField("private IChatBaseComponent a");
        public final FieldAccessor<Object> titleBottom = nextFieldSignature("private IChatBaseComponent b");
    }

    public static class NMSPacketPlayOutPosition extends NMSPacket {

        public final FieldAccessor<Double> x = nextField("private double a");
        public final FieldAccessor<Double> y = nextFieldSignature("private double b");
        public final FieldAccessor<Double> z = nextFieldSignature("private double c");
        public final FieldAccessor<Float> yaw = nextFieldSignature("private float d");
        public final FieldAccessor<Float> pitch = nextFieldSignature("private float e");
        public final FieldAccessor<Set<?>> teleportFlags = nextFieldSignature("private Set<EnumPlayerTeleportFlags> f");
        public final FieldAccessor<Integer> unknown1 = nextFieldSignature("private int g");
    }

    public static class NMSPacketPlayOutRemoveEntityEffect extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<Object> effectList = nextFieldSignature("private MobEffectList b");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, CommonUtil.getNMSClass("MobEffectList"));

        public CommonPacket newInstance(int entityId, Object mobEffectList) {
            return constructor1.newInstance(entityId, mobEffectList);
        }
    }

    public static class NMSPacketPlayOutResourcePackSend extends NMSPacket {
        
        public final FieldAccessor<String> name = nextField("private String a");
        public final FieldAccessor<String> hash = nextField("private String b");
    }

    public static class NMSPacketPlayOutRespawn extends NMSPacket {

        public final FieldAccessor<Integer> dimension = nextField("private int a");
        public final FieldAccessor<Difficulty> difficulty = nextFieldSignature("private EnumDifficulty b").translate(DuplexConversion.difficulty);
        public final TranslatorFieldAccessor<GameMode> gamemode = nextFieldSignature("private EnumGamemode c").translate(DuplexConversion.gameMode);
        public final TranslatorFieldAccessor<WorldType> worldType = nextFieldSignature("private WorldType d").translate(DuplexConversion.worldType);
    }

    public static class NMSPacketPlayOutScoreboardDisplayObjective extends NMSPacket {

        public final FieldAccessor<Integer> display = nextField("private int a");
        public final FieldAccessor<String> name = nextFieldSignature("private String b");
    }

    public static class NMSPacketPlayOutScoreboardObjective extends NMSPacket {

        public final FieldAccessor<String> name = nextField("private String a");
        public final FieldAccessor<String> displayName = nextFieldSignature("private String b");
        public final FieldAccessor<Object> criteria = nextFieldSignature("private IScoreboardCriteria.EnumScoreboardHealthDisplay c");
        public final FieldAccessor<Integer> action = nextFieldSignature("private int d");
    }

    public static class NMSPacketPlayOutScoreboardScore extends NMSPacket {

        public final FieldAccessor<String> name = nextField("private String a");
        public final FieldAccessor<String> objName = nextFieldSignature("private String b");
        public final FieldAccessor<Integer> value = nextFieldSignature("private int c");
        public final FieldAccessor<ScoreboardAction> action = nextFieldSignature("private EnumScoreboardAction d").translate(DuplexConversion.scoreboardAction);
    }
    
    public static class NMSPacketPlayOutScoreboardTeam extends NMSPacket {

        public final FieldAccessor<String> name = nextField("private String a");
        public final FieldAccessor<String> displayName = nextFieldSignature("private String b");
        public final FieldAccessor<String> prefix = nextFieldSignature("private String c");
        public final FieldAccessor<String> suffix = nextFieldSignature("private String d");
        public final FieldAccessor<String> visibility = nextFieldSignature("private String e");
        public final FieldAccessor<String> collisionRule = nextFieldSignature("private String f");
        public final FieldAccessor<Integer> chatFormat = nextFieldSignature("private int g");
        public final FieldAccessor<Collection<String>> players = nextFieldSignature("private final Collection<String> h");
        public final FieldAccessor<Integer> mode = nextFieldSignature("private int i");
        public final FieldAccessor<Integer> friendlyFire = nextFieldSignature("private int j");
    }

    public static class NMSPacketPlayOutServerDifficulty extends NMSPacket {
        
        public final FieldAccessor<Difficulty> difficulty = nextField("private EnumDifficulty a").translate(DuplexConversion.difficulty);
        public final FieldAccessor<Boolean> hardcore = nextFieldSignature("private boolean b");
    }

    public static class NMSPacketPlayOutSetCooldown extends NMSPacket {
        
        public final FieldAccessor<Material> material = nextField("private Item a").translate(DuplexConversion.item);
        public final FieldAccessor<Integer> cooldown = nextFieldSignature("private int b");
    }

    public static class NMSPacketPlayOutSetSlot extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int a");
        public final FieldAccessor<Integer> slot = nextField("private int b");
        public final FieldAccessor<ItemStack> item = nextFieldSignature("private ItemStack c").translate(DuplexConversion.itemStack);
    }

    public static class NMSPacketPlayOutSpawnEntity extends NMSPacket {
        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<UUID> UUID = nextFieldSignature("private UUID b");
        public final FieldAccessor<Double> x = nextFieldSignature("private double c");
        public final FieldAccessor<Double> y = nextFieldSignature("private double d");
        public final FieldAccessor<Double> z = nextFieldSignature("private double e");
        public final FieldAccessor<Integer> motX = nextFieldSignature("private int f");
        public final FieldAccessor<Integer> motY = nextFieldSignature("private int g");
        public final FieldAccessor<Integer> motZ = nextFieldSignature("private int h");
        public final FieldAccessor<Integer> pitch = nextFieldSignature("private int i");
        public final FieldAccessor<Integer> yaw = nextFieldSignature("private int j");
        public final FieldAccessor<Integer> entityType = nextFieldSignature("private int k");
        public final FieldAccessor<Integer> extraData = nextFieldSignature("private int l");
    }

    public static class NMSPacketPlayOutSpawnEntityExperienceOrb extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<Double> x = nextFieldSignature("private double b");
        public final FieldAccessor<Double> y = nextFieldSignature("private double c");
        public final FieldAccessor<Double> z = nextFieldSignature("private double d");
        public final FieldAccessor<Integer> experience = nextFieldSignature("private int e");
    }

    public static class NMSPacketPlayOutSpawnEntityLiving extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<UUID> UUID = nextFieldSignature("private UUID b");
        public final FieldAccessor<Integer> entityType = nextFieldSignature("private int c");
        public final FieldAccessor<Double> x = nextFieldSignature("private double d");
        public final FieldAccessor<Double> y = nextFieldSignature("private double e");
        public final FieldAccessor<Double> z = nextFieldSignature("private double f");
        public final FieldAccessor<Integer> motX = nextFieldSignature("private int g");
        public final FieldAccessor<Integer> motY = nextFieldSignature("private int h");
        public final FieldAccessor<Integer> motZ = nextFieldSignature("private int i");
        public final FieldAccessor<Byte> yaw = nextFieldSignature("private byte j");
        public final FieldAccessor<Byte> pitch = nextFieldSignature("private byte k");
        public final FieldAccessor<Byte> headYaw = nextFieldSignature("private byte l");
        public final TranslatorFieldAccessor<DataWatcher> dataWatcher = nextFieldSignature("private DataWatcher m").translate(DuplexConversion.dataWatcher);
        private final FieldAccessor<List<?>> dataWatcherItems = nextFieldSignature("private List<DataWatcher.Item<?>> n"); // unused!
        
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(CommonUtil.getNMSClass("EntityLiving"));

        public CommonPacket newInstance(Object entityLiving) {
            return constructor1.newInstance(entityLiving);
        }
    }

    public static class NMSPacketPlayOutSpawnEntityPainting extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<UUID> UUID = nextFieldSignature("private UUID b");
        public final TranslatorFieldAccessor<IntVector3> blockPostion = nextFieldSignature("private BlockPosition c").translate(DuplexConversion.blockPosition);
        public final FieldAccessor<Object> facing = nextFieldSignature("private EnumDirection d");
        public final FieldAccessor<String> art = nextFieldSignature("private String e");
    }

    public static class NMSPacketPlayOutSpawnEntityWeather extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<Double> x = nextFieldSignature("private double b");
        public final FieldAccessor<Double> y = nextFieldSignature("private double c");
        public final FieldAccessor<Double> z = nextFieldSignature("private double d");
        public final FieldAccessor<Integer> type = nextFieldSignature("private int e");
    }

    public static class NMSPacketPlayOutSpawnPosition extends NMSPacket {

        public final TranslatorFieldAccessor<IntVector3> position = getField("position").translate(DuplexConversion.blockPosition);
    }

    public static class NMSPacketPlayOutStatistic extends NMSPacket {

        public final FieldAccessor<Map<Object, Integer>> statsMap = nextField("private Map<Statistic, Integer> a");
    }    

    public static class NMSPacketPlayOutTabComplete extends NMSPacket {

        public final FieldAccessor<String[]> response = nextField("private String[] a");
    }

    public static class NMSPacketPlayOutTileEntityData extends NMSPacket {

        public final TranslatorFieldAccessor<IntVector3> position = nextField("private BlockPosition a").translate(DuplexConversion.blockPosition);
        public final FieldAccessor<Integer> action = nextFieldSignature("private int b");
        public final FieldAccessor<CommonTagCompound> data = nextFieldSignature("private NBTTagCompound c").translate(DuplexConversion.commonTagCompound);

        public CommonPacket newInstance(IntVector3 blockPosition, int action, CommonTagCompound data) {
            CommonPacket packet = this.newInstance();
            packet.write(this.position, blockPosition);
            packet.write(this.action, action);
            packet.write(this.data, data);
            return packet;
        }
    }

    public static class NMSPacketPlayOutTitle extends NMSPacket {
        public final FieldAccessor<Object> action = nextField("private EnumTitleAction a");
        public final FieldAccessor<Object> chatComponent = nextFieldSignature("private IChatBaseComponent b");
        public final FieldAccessor<Integer> a = nextFieldSignature("private int c");
        public final FieldAccessor<Integer> b = nextFieldSignature("private int d");
        public final FieldAccessor<Integer> c = nextFieldSignature("private int e");
        
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(PacketPlayOutTitle.EnumTitleAction.class, IChatBaseComponent.class, int.class, int.class, int.class);

        public CommonPacket newInstance(int a, int b, int c) {
            return constructor1.newInstance(PacketPlayOutTitle.EnumTitleAction.TIMES, (IChatBaseComponent) null, a, b, c);
        }

        public CommonPacket newInstance(PacketPlayOutTitle.EnumTitleAction enumTitleAction, IChatBaseComponent iChatBaseComponent) {
            return constructor1.newInstance(enumTitleAction, iChatBaseComponent, -1, -1, -1);
        }

        public CommonPacket newInstance(PacketPlayOutTitle.EnumTitleAction enumTitleAction, IChatBaseComponent iChatBaseComponent, int a, int b, int c) {
            return constructor1.newInstance(enumTitleAction, iChatBaseComponent, a, b, c);
        }
    }

    public static class NMSPacketPlayOutTransaction extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int a");
        public final FieldAccessor<Short> action = nextField("private short b");
        public final FieldAccessor<Boolean> accepted = nextFieldSignature("private boolean c");
    }

    public static class NMSPacketPlayOutUnloadChunk extends NMSPacket {

        public final FieldAccessor<Integer> x = nextField("private int a");
        public final FieldAccessor<Integer> z = nextFieldSignature("private int b");
    }

    public static class NMSPacketPlayOutUpdateAttributes extends NMSPacket {

        /**
         * A list of NMS.Attribute elements - may require further API to work
         * with. For now, use reflection.
         */
        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<List<?>> attributeSnapshots = nextFieldSignature("private final List<AttributeSnapshot> b");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, Collection.class);

        public CommonPacket newInstance(int entityId, Collection<?> attributes) {
            return constructor1.newInstance(entityId, attributes);
        }
    }

    public static class NMSPacketPlayOutUpdateHealth extends NMSPacket {

        public final FieldAccessor<Float> health = nextField("private float a");
        public final FieldAccessor<Short> food = nextFieldSignature("private int b");
        public final FieldAccessor<Float> foodSaturation = nextFieldSignature("private float c");
    }

    public static class NMSPacketPlayOutUpdateTime extends NMSPacket {

        public final FieldAccessor<Long> age = nextField("private long a");
        public final FieldAccessor<Long> timeOfDay = nextFieldSignature("private long b");
    }

    public static class NMSPacketPlayOutVehicleMove extends NMSPacket {

        public final FieldAccessor<Double> locX = nextField("private double a");
        public final FieldAccessor<Double> locY = nextFieldSignature("private double b");
        public final FieldAccessor<Double> locZ = nextFieldSignature("private double c");
        public final FieldAccessor<Double> yaw = nextFieldSignature("private float d");
        public final FieldAccessor<Double> pitch = nextFieldSignature("private float e");
    }

    public static class NMSPacketPlayOutWindowData extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int a");
        public final FieldAccessor<Integer> count = nextFieldSignature("private int b");
        public final FieldAccessor<Integer> data = nextFieldSignature("private int c");
    }

    public static class NMSPacketPlayOutWindowItems extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int a");
        public final FieldAccessor<List<ItemStack>> items = nextFieldSignature("private List<ItemStack> b").translate(DuplexConversion.itemStackList);
    }

    public static class NMSPacketPlayOutWorldBorder extends NMSPacket {

        public final FieldAccessor<Object> action = nextField("private EnumWorldBorderAction a");
        public final FieldAccessor<Integer> b = nextFieldSignature("private int b");
        public final FieldAccessor<Double> cx = nextFieldSignature("private double c");
        public final FieldAccessor<Double> cz = nextFieldSignature("private double d");
        public final FieldAccessor<Double> e = nextFieldSignature("private double e");
        public final FieldAccessor<Double> size = nextFieldSignature("private double f");
        public final FieldAccessor<Long> g = nextFieldSignature("private long g");
        public final FieldAccessor<Integer> warningTime = nextFieldSignature("private int h");
        public final FieldAccessor<Integer> warningDistance = nextFieldSignature("private int i");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(WorldBorder.class, PacketPlayOutWorldBorder.EnumWorldBorderAction.class);

        public CommonPacket newInstance (WorldBorder worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction enumw) {
            return constructor1.newInstance(worldBorder, enumw);
        }
    }

    public static class NMSPacketPlayOutWorldEvent extends NMSPacket {

        public final FieldAccessor<Integer> effectId = nextField("private int a");
        public final TranslatorFieldAccessor<IntVector3> position = nextFieldSignature("private BlockPosition b").translate(DuplexConversion.blockPosition);
        public final FieldAccessor<Integer> data = nextFieldSignature("private int c");
        public final FieldAccessor<Boolean> noRelativeVolume = nextFieldSignature("private boolean d");
    }

    public static class NMSPacketPlayOutWorldParticles extends NMSPacket {

        public final FieldAccessor<Object> particle = nextField("private EnumParticle a");
        public final FieldAccessor<Float> x = nextFieldSignature("private float b");
        public final FieldAccessor<Float> y = nextFieldSignature("private float c");
        public final FieldAccessor<Float> z = nextFieldSignature("private float d");
        public final FieldAccessor<Float> randomX = nextFieldSignature("private float e");
        public final FieldAccessor<Float> randomY = nextFieldSignature("private float f");
        public final FieldAccessor<Float> randomZ = nextFieldSignature("private float g");
        public final FieldAccessor<Float> speed = nextFieldSignature("private float h");
        public final FieldAccessor<Integer> particleCount = nextFieldSignature("private int i");
        public final FieldAccessor<Boolean> unknown1 = nextFieldSignature("private boolean j");
        public final FieldAccessor<int[]> unknown2 = nextFieldSignature("private int[] k");

        public CommonPacket newInstance(String name, int count, Location location, double randomness, double speed) {
            return newInstance(name, count, location.getX(), location.getY(), location.getZ(), randomness, randomness, randomness, speed);
        }

        public CommonPacket newInstance(String name, int count, double x, double y, double z, double rx, double ry, double rz, double speed) {
            final CommonPacket packet = newInstance();
            packet.write(this.particle, EnumParticle.a(name));
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

    //////////////////////////////////////////////////////////////////////



}
