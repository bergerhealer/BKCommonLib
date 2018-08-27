package com.bergerkiller.reflection.net.minecraft.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatMessageType;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.Dimension;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.InventoryClickType;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.bukkit.common.wrappers.ResourceKey;
import com.bergerkiller.bukkit.common.wrappers.ScoreboardAction;
import com.bergerkiller.bukkit.common.wrappers.UseAction;
import com.bergerkiller.generated.net.minecraft.server.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHumanHandle;
import com.bergerkiller.generated.net.minecraft.server.IChatBaseComponentHandle;
import com.bergerkiller.generated.net.minecraft.server.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.server.MobEffectListHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayInArmAnimationHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayInBlockPlaceHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayInBoatMoveHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayInKeepAliveHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayInResourcePackStatusHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayInSetCreativeSlotHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayInSettingsHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayInTeleportAcceptHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayInUpdateSignHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayInUseEntityHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayInUseItemHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayInVehicleMoveHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayInWindowClickHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutAttachEntityHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutBossHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutChatHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutCollectHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutCombatEventHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutCustomSoundEffectHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutEntityEquipmentHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutEntityHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutEntityMetadataHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutEntityTeleportHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutKeepAliveHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutEntityHandle.PacketPlayOutEntityLookHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutEntityHandle.PacketPlayOutRelEntityMoveHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutEntityHandle.PacketPlayOutRelEntityMoveLookHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutLoginHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutMapChunkHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutMapHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutMountHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutNamedEntitySpawnHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutNamedSoundEffectHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutPlayerListHeaderFooterHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutRemoveEntityEffectHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutRespawnHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutScoreboardObjectiveHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutScoreboardTeamHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutSetCooldownHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutSpawnEntityExperienceOrbHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutSpawnEntityHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutSpawnEntityLivingHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutSpawnEntityPaintingHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutSpawnEntityWeatherHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutUnloadChunkHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutUpdateSignHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutVehicleMoveHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutWindowItemsHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutWorldParticlesHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutTitleHandle.EnumTitleActionHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeConstructor;
import com.bergerkiller.mountiplex.reflection.SafeDirectField;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

public class NMSPacketClasses {

    public static class NMSPacket extends PacketType {
        private SafeConstructor<CommonPacket> _constructor0 = null;

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
            if (this._constructor0 == null) {
                this._constructor0 = getPacketConstructor();
            }
            return this._constructor0.newInstance();
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
            return constructor1.newInstance(abilities.getRawHandle());
        }
    }

    public static class NMSPacketPlayInArmAnimation extends NMSPacket {

        /**
         * Sets the hand that is animated
         * 
         * @param packet to write to
         * @param humanEntity used for translating the hand from MAIN/OFF to LEFT/RIGHT, can be null
         * @param humanHand to set to
         */
        public final void setHand(CommonPacket packet, HumanEntity humanEntity, HumanHand humanHand) {
            if (PacketPlayInArmAnimationHandle.T.enumHand.isAvailable()) {
                PacketPlayInArmAnimationHandle.T.enumHand.set(packet.getHandle(), humanHand.toNMSEnumHand(humanEntity));
            }
        }

        /**
         * Gets the hand that is animated
         * 
         * @param packet to read from
         * @param humanEntity used for translating the hand from MAIN/OFF to LEFT/RIGHT, can be null
         * @return humanHand
         */
        public final HumanHand getHand(CommonPacket packet, HumanEntity humanEntity) {
            if (PacketPlayInArmAnimationHandle.T.enumHand.isAvailable()) {
                Object enumHand = PacketPlayInArmAnimationHandle.T.enumHand.get(packet.getHandle());
                return HumanHand.fromNMSEnumHand(humanEntity, enumHand);
            } else {
                return HumanHand.RIGHT;
            }
        }
    }

    public static class NMSPacketPlayInBlockDig extends NMSPacket {

        public final FieldAccessor<IntVector3> position = nextField("private BlockPosition a").translate(DuplexConversion.blockPosition);
        public final FieldAccessor<Object> face = nextFieldSignature("private EnumDirection b");
        public final FieldAccessor<Object> status = nextFieldSignature("private EnumPlayerDigType c");
    }

    public static class NMSPacketPlayInBlockPlace extends NMSPacket {

        public final FieldAccessor<Long> timestamp = PacketPlayInBlockPlaceHandle.T.timestamp.toFieldAccessor().ignoreInvalid(0L);

        @Override
        protected boolean matchPacket(Object packetHandle) {
            if (CommonCapabilities.PLACE_PACKETS_MERGED) {
                return PacketPlayInUseItemHandle.T.opt_direction_index.getInteger(packetHandle) == 255;
            } else {
                return true;
            }
        }

        @Override
        public void preprocess(Object packetHandle) {
            if (CommonCapabilities.PLACE_PACKETS_MERGED) {
                PacketPlayInUseItemHandle.T.opt_direction_index.setInteger(packetHandle, 255);
            }
        }

        /**
         * Sets the hand that placed the block
         * 
         * @param packet to write to
         * @param humanEntity used for translating the hand from MAIN/OFF to LEFT/RIGHT, can be null
         * @param humanHand to set to
         */
        public final void setHand(CommonPacket packet, HumanEntity humanEntity, HumanHand humanHand) {
            if (PacketPlayInBlockPlaceHandle.T.enumHand.isAvailable()) {
                PacketPlayInBlockPlaceHandle.T.enumHand.set(packet.getHandle(), humanHand.toNMSEnumHand(humanEntity));
            }
        }

        /**
         * Gets the hand that placed the block
         * 
         * @param packet to read from
         * @param humanEntity used for translating the hand from MAIN/OFF to LEFT/RIGHT, can be null
         * @return humanHand
         */
        public final HumanHand getHand(CommonPacket packet, HumanEntity humanEntity) {
            if (PacketPlayInBlockPlaceHandle.T.enumHand.isAvailable()) {
                Object enumHand = PacketPlayInBlockPlaceHandle.T.enumHand.get(packet.getHandle());
                return HumanHand.fromNMSEnumHand(humanEntity, enumHand);
            } else {
                return HumanHand.RIGHT;
            }
        }
    }

    public static class NMSPacketPlayInUseItem extends NMSPacket {

        public final TranslatorFieldAccessor<IntVector3> position = PacketPlayInUseItemHandle.T.position.toFieldAccessor();
        public final FieldAccessor<BlockFace> direction = new SafeDirectField<BlockFace>() {
            @Override
            public BlockFace get(Object instance) {
                return PacketPlayInUseItemHandle.T.getDirection.invoke(instance);
            }

            @Override
            public boolean set(Object instance, BlockFace value) {
                PacketPlayInUseItemHandle.T.setDirection.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Float> deltaX = PacketPlayInUseItemHandle.T.deltaX.toFieldAccessor();
        public final FieldAccessor<Float> deltaY = PacketPlayInUseItemHandle.T.deltaY.toFieldAccessor();
        public final FieldAccessor<Float> deltaZ = PacketPlayInUseItemHandle.T.deltaZ.toFieldAccessor();
        public final FieldAccessor<Long> timestamp = PacketPlayInUseItemHandle.T.timestamp.toFieldAccessor().ignoreInvalid(0L);

        @Override
        protected boolean matchPacket(Object packetHandle) {
            if (CommonCapabilities.PLACE_PACKETS_MERGED) {
                return PacketPlayInUseItemHandle.T.opt_direction_index.getInteger(packetHandle) != 255;
            } else {
                return true;
            }
        }

        /**
         * Sets the hand that used the item
         * 
         * @param packet to write to
         * @param humanEntity used for translating the hand from MAIN/OFF to LEFT/RIGHT, can be null
         * @param humanHand to set to
         */
        public final void setHand(CommonPacket packet, HumanEntity humanEntity, HumanHand humanHand) {
            if (PacketPlayInUseItemHandle.T.opt_enumHand.isAvailable()) {
                PacketPlayInUseItemHandle.T.opt_enumHand.set(packet.getHandle(), humanHand.toNMSEnumHand(humanEntity));
            }
        }

        /**
         * Gets the hand that used the item
         * 
         * @param packet to read from
         * @param humanEntity used for translating the hand from MAIN/OFF to LEFT/RIGHT, can be null
         * @return humanHand
         */
        public final HumanHand getHand(CommonPacket packet, HumanEntity humanEntity) {
            if (PacketPlayInUseItemHandle.T.opt_enumHand.isAvailable()) {
                Object enumHand = PacketPlayInUseItemHandle.T.opt_enumHand.get(packet.getHandle());
                return HumanHand.fromNMSEnumHand(humanEntity, enumHand);
            } else {
                return HumanHand.RIGHT;
            }
        }
    }

    public static class NMSPacketPlayInBoatMove extends NMSPacket {
        
        public final FieldAccessor<Boolean> leftPaddle = PacketPlayInBoatMoveHandle.T.leftPaddle.toFieldAccessor();
        public final FieldAccessor<Boolean> rightPaddle = PacketPlayInBoatMoveHandle.T.rightPaddle.toFieldAccessor();
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

        // public final FieldAccessor<String> tag = nextField("private String a");
        // public final FieldAccessor<Object> data = nextFieldSignature("private PacketDataSerializer b");
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

        public final FieldAccessor<Long> key = PacketPlayInKeepAliveHandle.T.key.toFieldAccessor();
    }

    public static class NMSPacketPlayInResourcePackStatus extends NMSPacket {

        public final FieldAccessor<Object> enumStatus = PacketPlayInResourcePackStatusHandle.T.status.toFieldAccessor();
    }

    public static class NMSPacketPlayInSetCreativeSlot extends NMSPacket {

        public final FieldAccessor<Integer> slot = PacketPlayInSetCreativeSlotHandle.T.slot.toFieldAccessor();
        public final FieldAccessor<ItemStack> item = PacketPlayInSetCreativeSlotHandle.T.item.toFieldAccessor();
    }

    public static class NMSPacketPlayInSettings extends NMSPacket {

        public final FieldAccessor<String> lang = PacketPlayInSettingsHandle.T.lang.toFieldAccessor();
        public final FieldAccessor<Integer> view = PacketPlayInSettingsHandle.T.view.toFieldAccessor();
        public final FieldAccessor<Object> chatVisibility = PacketPlayInSettingsHandle.T.chatVisibility.toFieldAccessor();
        public final FieldAccessor<Boolean> enableColors = PacketPlayInSettingsHandle.T.enableColors.toFieldAccessor();
        public final FieldAccessor<Integer> modelPartFlags = PacketPlayInSettingsHandle.T.modelPartFlags.toFieldAccessor();
        public final FieldAccessor<HumanHand> mainHand = PacketPlayInSettingsHandle.T.mainHand.toFieldAccessor().ignoreInvalid(HumanHand.RIGHT);
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

        // public final FieldAccessor<String> text = PacketPlayInTabCompleteHandle.T.text.toFieldAccessor();
        // public final FieldAccessor<Boolean> assumeCommand = PacketPlayInTabCompleteHandle.T.assumeCommand.toFieldAccessor().ignoreInvalid(false);
        // public final FieldAccessor<IntVector3> position = PacketPlayInTabCompleteHandle.T.position.toFieldAccessor();
    }

    public static class NMSPacketPlayInTeleportAccept extends NMSPacket {

        public final FieldAccessor<Integer> teleportId = PacketPlayInTeleportAcceptHandle.T.teleportId.toFieldAccessor();
    }

    public static class NMSPacketPlayInTransaction extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int a");
        public final FieldAccessor<Short> action = nextFieldSignature("private short b");
        public final FieldAccessor<Boolean> accepted = nextFieldSignature("private boolean c");
    }
    
    public static class NMSPacketPlayInUpdateSign extends NMSPacket {

        public final FieldAccessor<IntVector3> position = PacketPlayInUpdateSignHandle.T.position.toFieldAccessor();
        public final FieldAccessor<ChatText[]> lines = PacketPlayInUpdateSignHandle.T.lines.toFieldAccessor();

        public Block getBlock(CommonPacket packet, World world) {
            return BlockUtil.getBlock(world, position.get(packet.getHandle()));
        }

        public void setBlock(CommonPacket packet, Block block) {
            position.set(packet.getHandle(), new IntVector3(block));
        }
    }

    public static class NMSPacketPlayInUseEntity extends NMSPacket {

        public final FieldAccessor<Integer> clickedEntityId = PacketPlayInUseEntityHandle.T.usedEntityId.toFieldAccessor();
        public final TranslatorFieldAccessor<UseAction> useAction = PacketPlayInUseEntityHandle.T.action.toFieldAccessor();
        public final TranslatorFieldAccessor<Vector> offset = PacketPlayInUseEntityHandle.T.offset.toFieldAccessor();

        /**
         * Sets the hand that interacted with the entity
         * 
         * @param packet to write to
         * @param humanEntity used for translating the hand from MAIN/OFF to LEFT/RIGHT, can be null
         * @param humanHand to set to
         */
        public final void setHand(CommonPacket packet, HumanEntity humanEntity, HumanHand humanHand) {
            if (PacketPlayInUseEntityHandle.T.enumHand.isAvailable()) {
                PacketPlayInUseEntityHandle.T.enumHand.set(packet.getHandle(), humanHand.toNMSEnumHand(humanEntity));
            }
        }

        /**
         * Gets the hand that interacted with the entity
         * 
         * @param packet to read from
         * @param humanEntity used for translating the hand from MAIN/OFF to LEFT/RIGHT, can be null
         * @return humanHand
         */
        public final HumanHand getHand(CommonPacket packet, HumanEntity humanEntity) {
            if (PacketPlayInUseEntityHandle.T.enumHand.isAvailable()) {
                Object enumHand = PacketPlayInUseEntityHandle.T.enumHand.get(packet.getHandle());
                return HumanHand.fromNMSEnumHand(humanEntity, enumHand);
            } else {
                return HumanHand.RIGHT;
            }
        }
    }

    public static class NMSPacketPlayInVehicleMove extends NMSPacket {

        public final FieldAccessor<Double> posX = PacketPlayInVehicleMoveHandle.T.posX.toFieldAccessor();
        public final FieldAccessor<Double> posY = PacketPlayInVehicleMoveHandle.T.posY.toFieldAccessor();
        public final FieldAccessor<Double> posZ = PacketPlayInVehicleMoveHandle.T.posZ.toFieldAccessor();
        public final FieldAccessor<Float> yaw = PacketPlayInVehicleMoveHandle.T.yaw.toFieldAccessor();
        public final FieldAccessor<Float> pitch = PacketPlayInVehicleMoveHandle.T.pitch.toFieldAccessor();
    }

    public static class NMSPacketPlayInWindowClick extends NMSPacket {

        public final FieldAccessor<Integer> windowId = PacketPlayInWindowClickHandle.T.windowId.toFieldAccessor();
        public final FieldAccessor<Integer> slot = PacketPlayInWindowClickHandle.T.slot.toFieldAccessor();
        public final FieldAccessor<Integer> button = PacketPlayInWindowClickHandle.T.button.toFieldAccessor();
        public final FieldAccessor<Short> action = PacketPlayInWindowClickHandle.T.action.toFieldAccessor();
        public final FieldAccessor<ItemStack> item = PacketPlayInWindowClickHandle.T.item.toFieldAccessor();
        public final FieldAccessor<InventoryClickType> mode = PacketPlayInWindowClickHandle.T.mode.toFieldAccessor();
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
            return constructor1.newInstance(abilities.getRawHandle());
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

        public final FieldAccessor<Integer> vehicleId = PacketPlayOutAttachEntityHandle.T.vehicleId.toFieldAccessor();
        public final FieldAccessor<Integer> passengerId = PacketPlayOutAttachEntityHandle.T.passengerId.toFieldAccessor();

        public CommonPacket newInstance(org.bukkit.entity.Entity passenger, org.bukkit.entity.Entity vehicle) {
            return new CommonPacket(PacketPlayOutAttachEntityHandle.createNew(passenger, vehicle).getRaw());
        }
    }

    public static class NMSPacketPlayOutBed extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final TranslatorFieldAccessor<IntVector3> bedPosition = nextFieldSignature("private BlockPosition b").translate(DuplexConversion.blockPosition);
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(EntityHumanHandle.T.getType(), BlockPositionHandle.T.getType());

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
        public final FieldAccessor<UUID> entityUUID = PacketPlayOutBossHandle.T.entityUUID.toFieldAccessor();
        public final FieldAccessor<Object> action = PacketPlayOutBossHandle.T.action.toFieldAccessor();
        public final FieldAccessor<Object> chat = PacketPlayOutBossHandle.T.chat.toFieldAccessor();
        public final FieldAccessor<Float> progress = PacketPlayOutBossHandle.T.progress.toFieldAccessor();
        public final FieldAccessor<Object> bossBarColor = PacketPlayOutBossHandle.T.bossBarColor.toFieldAccessor();
        public final FieldAccessor<Object> bossBarStyle = PacketPlayOutBossHandle.T.bossBarStyle.toFieldAccessor();
        public final FieldAccessor<Boolean> unknown1 = PacketPlayOutBossHandle.T.unknown1.toFieldAccessor();
        public final FieldAccessor<Boolean> unknown2 = PacketPlayOutBossHandle.T.unknown2.toFieldAccessor();
        public final FieldAccessor<Boolean> unknown3 = PacketPlayOutBossHandle.T.unknown3.toFieldAccessor();
    }

    public static class NMSPacketPlayOutCamera extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("public int a");
    }

    public static class NMSPacketPlayOutChat extends NMSPacket {
        public final FieldAccessor<ChatText> text = PacketPlayOutChatHandle.T.text.toFieldAccessor();
        public final FieldAccessor<ChatMessageType> type = PacketPlayOutChatHandle.T.type.toFieldAccessor();

        /** Deprecated: use {@link #text} instead */
        @Deprecated
        public final FieldAccessor<Object> chatComponent = PacketPlayOutChatHandle.T.text.raw.toFieldAccessor();

        /** Deprecated: this is only available on Spigot servers, and acts as a proxy of {@link #text} */
        @Deprecated
        public final FieldAccessor<Object[]> components = PacketPlayOutChatHandle.T.components.toFieldAccessor();
    }

    public static class NMSPacketPlayOutCloseWindow extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int a");
    }    

    public static class NMSPacketPlayOutCollect extends NMSPacket {

        public final FieldAccessor<Integer> collectedItemId = PacketPlayOutCollectHandle.T.collectedItemId.toFieldAccessor();
        public final FieldAccessor<Integer> collectorEntityId = PacketPlayOutCollectHandle.T.collectorEntityId.toFieldAccessor();
        public final FieldAccessor<Integer> unknown1;
        
        public NMSPacketPlayOutCollect() {
            if (PacketPlayOutCollectHandle.T.unknown.isAvailable()) {
                this.unknown1 = PacketPlayOutCollectHandle.T.unknown.toFieldAccessor();
            } else {
                this.unknown1 = null;
            }
        }
    }

    public static class NMSPacketPlayOutCombatEvent extends NMSPacket {

          public final FieldAccessor<Object> eventType = PacketPlayOutCombatEventHandle.T.eventType.toFieldAccessor();
          public final FieldAccessor<Integer> entityId1 = PacketPlayOutCombatEventHandle.T.entityId1.toFieldAccessor();
          public final FieldAccessor<Integer> entityId2 = PacketPlayOutCombatEventHandle.T.entityId2.toFieldAccessor();
          public final FieldAccessor<Integer> tickDuration = PacketPlayOutCombatEventHandle.T.tickDuration.toFieldAccessor();
          public final FieldAccessor<ChatText> message = PacketPlayOutCombatEventHandle.T.message.toFieldAccessor();
    }

    public static class NMSPacketPlayOutCustomPayload extends NMSPacket {

        // public final FieldAccessor<String> tag = nextField("private String a");
        // public final FieldAccessor<Object> data = nextFieldSignature("private PacketDataSerializer b");
    }

    public static class NMSPacketPlayOutCustomSoundEffect extends NMSPacket {

        public final FieldAccessor<ResourceKey> sound = PacketPlayOutCustomSoundEffectHandle.T.sound.toFieldAccessor();
        public final FieldAccessor<String> category = PacketPlayOutCustomSoundEffectHandle.T.category.toFieldAccessor();
        public final FieldAccessor<Integer> x = PacketPlayOutCustomSoundEffectHandle.T.x.toFieldAccessor();
        public final FieldAccessor<Integer> y = PacketPlayOutCustomSoundEffectHandle.T.y.toFieldAccessor();
        public final FieldAccessor<Integer> z = PacketPlayOutCustomSoundEffectHandle.T.z.toFieldAccessor();
        public final FieldAccessor<Float> volume = PacketPlayOutCustomSoundEffectHandle.T.volume.toFieldAccessor();
        public final FieldAccessor<Float> pitch = PacketPlayOutCustomSoundEffectHandle.T.pitch.toFieldAccessor();
    }

    /// ====================== NMSPacketPlayOutEntity and derivatives ===========================

    public static class NMSPacketPlayOutEntity extends NMSPacket {

        public final FieldAccessor<Integer> entityId = PacketPlayOutEntityHandle.T.entityId.toFieldAccessor();

        public final FieldAccessor<Double> dx = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutEntityHandle.createHandle(instance).getDeltaX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutEntityHandle.createHandle(instance).setDeltaX(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> dy = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutEntityHandle.createHandle(instance).getDeltaY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutEntityHandle.createHandle(instance).setDeltaY(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> dz = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutEntityHandle.createHandle(instance).getDeltaZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutEntityHandle.createHandle(instance).setDeltaZ(value.doubleValue());
                return true;
            }
        };

        public final FieldAccessor<Float> dyaw = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayOutEntityHandle.createHandle(instance).getDeltaYaw();
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayOutEntityHandle.createHandle(instance).setDeltaYaw(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Float> dpitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayOutEntityHandle.createHandle(instance).getDeltaPitch();
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayOutEntityHandle.createHandle(instance).setDeltaPitch(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Boolean> onGround = PacketPlayOutEntityHandle.T.onGround.toFieldAccessor();

        public NMSPacketPlayOutEntity() {
            super();
        }

        protected NMSPacketPlayOutEntity(Class<?> packetClass) {
            super(packetClass);
        }
    }

    public static class NMSPacketPlayOutRelEntityMove extends NMSPacketPlayOutEntity {

        public NMSPacketPlayOutRelEntityMove() {
            super(CommonUtil.getNMSClass("PacketPlayOutEntity.PacketPlayOutRelEntityMove"));
        }

        public CommonPacket newInstance(int entityId, double dx, double dy, double dz, boolean onGround) {
            return PacketPlayOutRelEntityMoveHandle.createNew(entityId, dx, dy, dz, onGround).toCommonPacket();
        }
    }

    public static class NMSPacketPlayOutRelEntityMoveLook extends NMSPacketPlayOutEntity {

        public NMSPacketPlayOutRelEntityMoveLook() {
            super(CommonUtil.getNMSClass("PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook"));
        }

        public CommonPacket newInstance(int entityId, double dx, double dy, double dz, float dyaw, float dpitch, boolean onGround) {
            return PacketPlayOutRelEntityMoveLookHandle.createNew(entityId, dx, dy, dz, dyaw, dpitch, onGround).toCommonPacket();
        }
    }

    public static class NMSPacketPlayOutEntityLook extends NMSPacketPlayOutEntity {

        public NMSPacketPlayOutEntityLook() {
            super(CommonUtil.getNMSClass("PacketPlayOutEntity.PacketPlayOutEntityLook"));
        }

        public CommonPacket newInstance(int entityId, float dyaw, float dpitch, boolean onGround) {
            return PacketPlayOutEntityLookHandle.createNew(entityId, dyaw, dpitch, onGround).toCommonPacket();
        }
    }

    // ====================================================================================================

    public static class NMSPacketPlayOutEntityDestroy extends NMSPacket {

        public final FieldAccessor<int[]> entityIds = getField("a", int[].class);
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int[].class);

        public CommonPacket newInstance(int... entityIds) {
            return constructor1.newInstance(entityIds);
        }

        /**
         * Creates a new instance with the entity Ids specified.
         * Note: input Collection will be copied.
         * 
         * @param entityIds
         * @return packet
         */
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
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(int.class, MobEffectHandle.T.getType());

        public CommonPacket newInstance(int entityId, Object mobEffect) {
            return constructor1.newInstance(entityId,  Conversion.toMobEffect.convert(mobEffect));
        }

        public CommonPacket newInstance(int entityId, PotionEffect effect) {
            return constructor1.newInstance(entityId, Conversion.toMobEffect.convert(effect));
        }
    }

    public static class NMSPacketPlayOutEntityEquipment extends NMSPacket {

        public final FieldAccessor<Integer> entityId = PacketPlayOutEntityEquipmentHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<EquipmentSlot> slot = PacketPlayOutEntityEquipmentHandle.T.slot.toFieldAccessor();
        public final TranslatorFieldAccessor<ItemStack> item = PacketPlayOutEntityEquipmentHandle.T.itemStack.toFieldAccessor();

        public CommonPacket newInstance(int entityId, EquipmentSlot equipmentSlot, ItemStack item) {
            return PacketPlayOutEntityEquipmentHandle.createNew(entityId, equipmentSlot, item).toCommonPacket();
        }
    }

    public static class NMSPacketPlayOutEntityHeadRotation extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<Byte> headYaw = nextFieldSignature("private byte b");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(EntityHandle.T.getType(), byte.class);

        public CommonPacket newInstance(org.bukkit.entity.Entity entity, byte headRotation) {
            return constructor1.newInstance(Conversion.toEntityHandle.convert(entity), headRotation);
        }
    }

    public static class NMSPacketPlayOutEntityMetadata extends NMSPacket {

        public final FieldAccessor<Integer> entityId = PacketPlayOutEntityMetadataHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<List<DataWatcher.Item<Object>>> watchedObjects = PacketPlayOutEntityMetadataHandle.T.metadataItems.toFieldAccessor();

        public CommonPacket newInstance(int entityId, DataWatcher dataWatcher, boolean sendUnchangedData) {
            return PacketPlayOutEntityMetadataHandle.createNew(entityId, dataWatcher, sendUnchangedData).toCommonPacket();
        }
    }

    public static class NMSPacketPlayOutEntityStatus extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<Byte> status = nextFieldSignature("private byte b");
    }

    public static class NMSPacketPlayOutEntityTeleport extends NMSPacket {

        public final FieldAccessor<Integer> entityId = PacketPlayOutEntityTeleportHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<Double> x = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutEntityTeleportHandle.createHandle(instance).getPosX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutEntityTeleportHandle.createHandle(instance).setPosX(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> y = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutEntityTeleportHandle.createHandle(instance).getPosY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutEntityTeleportHandle.createHandle(instance).setPosY(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> z = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutEntityTeleportHandle.createHandle(instance).getPosZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutEntityTeleportHandle.createHandle(instance).setPosZ(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Float> yaw = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayOutEntityTeleportHandle.createHandle(instance).getYaw();
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayOutEntityTeleportHandle.createHandle(instance).setYaw(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayOutEntityTeleportHandle.createHandle(instance).getPitch();
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayOutEntityTeleportHandle.createHandle(instance).setPitch(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Boolean> onGround = PacketPlayOutEntityTeleportHandle.T.onGround.toFieldAccessor();

        public CommonPacket newInstance(org.bukkit.entity.Entity entity) {
            return PacketPlayOutEntityTeleportHandle.createNew(entity).toCommonPacket();
        }

        public CommonPacket newInstance(int entityId, double posX, double posY, double posZ, float yaw, float pitch, boolean onGround) {
            return PacketPlayOutEntityTeleportHandle.createNew(entityId, posX, posY, posZ, yaw, pitch, onGround).toCommonPacket();
        }
    }

    public static class NMSPacketPlayOutEntityVelocity extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final FieldAccessor<Integer> motX = nextFieldSignature("private int b");
        public final FieldAccessor<Integer> motY = nextFieldSignature("private int c");
        public final FieldAccessor<Integer> motZ = nextFieldSignature("private int d");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(EntityHandle.T.getType());
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
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(double.class, double.class, double.class, float.class, List.class, CommonUtil.getNMSClass("Vec3D"));

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

        public final FieldAccessor<Long> key = PacketPlayOutKeepAliveHandle.T.key.toFieldAccessor();
    }

    public static class NMSPacketPlayOutKickDisconnect extends NMSPacket {

        public final FieldAccessor<Object> reason = nextField("private IChatBaseComponent a");
    }

    public static class NMSPacketPlayOutLogin extends NMSPacket {

        public final FieldAccessor<Integer> playerId = PacketPlayOutLoginHandle.T.playerId.toFieldAccessor();
        public final FieldAccessor<Boolean> hardcore = PacketPlayOutLoginHandle.T.hardcore.toFieldAccessor();
        public final TranslatorFieldAccessor<GameMode> gameMode = PacketPlayOutLoginHandle.T.gameMode.toFieldAccessor();
        public final FieldAccessor<Dimension> dimension = PacketPlayOutLoginHandle.T.dimension.toFieldAccessor();
        public final TranslatorFieldAccessor<Difficulty> difficulty = PacketPlayOutLoginHandle.T.difficulty.toFieldAccessor();
        public final FieldAccessor<Integer> maxPlayers = PacketPlayOutLoginHandle.T.maxPlayers.toFieldAccessor();
        public final TranslatorFieldAccessor<WorldType> worldType = PacketPlayOutLoginHandle.T.worldType.toFieldAccessor();
        public final FieldAccessor<Boolean> unknown1 = PacketPlayOutLoginHandle.T.unknown1.toFieldAccessor();
    }

    public static class NMSPacketPlayOutMap extends NMSPacket {

        public final FieldAccessor<Integer> itemId = PacketPlayOutMapHandle.T.itemId.toFieldAccessor();
        public final FieldAccessor<Byte> scale = PacketPlayOutMapHandle.T.scale.toFieldAccessor();
        public final FieldAccessor<MapCursor[]> cursors = PacketPlayOutMapHandle.T.cursors.toFieldAccessor();
        public final FieldAccessor<Integer> xmin = PacketPlayOutMapHandle.T.xmin.toFieldAccessor();
        public final FieldAccessor<Integer> ymin = PacketPlayOutMapHandle.T.ymin.toFieldAccessor();
        public final FieldAccessor<Integer> width = PacketPlayOutMapHandle.T.width.toFieldAccessor();
        public final FieldAccessor<Integer> height = PacketPlayOutMapHandle.T.height.toFieldAccessor();
        public final FieldAccessor<byte[]> pixels = PacketPlayOutMapHandle.T.pixels.toFieldAccessor();

        public final FieldAccessor<Boolean> track = new SafeDirectField<Boolean>() {
            @Override
            public Boolean get(Object instance) {
                if (PacketPlayOutMapHandle.T.track.isAvailable()) {
                    return PacketPlayOutMapHandle.T.track.get(instance);
                } else {
                    return true;
                }
            }

            @Override
            public boolean set(Object instance, Boolean value) {
                if (PacketPlayOutMapHandle.T.track.isAvailable()) {
                    PacketPlayOutMapHandle.T.track.set(instance, value);
                    return true;
                } else {
                    return false;
                }
            }
        };
    }

    public static class NMSPacketPlayOutMapChunk extends NMSPacket {

        public final FieldAccessor<Integer> x = PacketPlayOutMapChunkHandle.T.x.toFieldAccessor();
        public final FieldAccessor<Integer> z = PacketPlayOutMapChunkHandle.T.z.toFieldAccessor();
        public final FieldAccessor<Integer> sectionsMask = new SafeDirectField<Integer>() {
            @Override
            public Integer get(Object instance) {
                return PacketPlayOutMapChunkHandle.createHandle(instance).getSectionsMask();
            }

            @Override
            public boolean set(Object instance, Integer value) {
                PacketPlayOutMapChunkHandle.createHandle(instance).setSectionsMask(value.intValue());
                return true;
            }
        };
        public final FieldAccessor<byte[]> data = new SafeDirectField<byte[]>() {
            @Override
            public byte[] get(Object instance) {
                return PacketPlayOutMapChunkHandle.createHandle(instance).getData();
            }

            @Override
            public boolean set(Object instance, byte[] value) {
                PacketPlayOutMapChunkHandle.createHandle(instance).setData(value);
                return true;
            }
        };
        public final FieldAccessor<List<CommonTagCompound>> tags = new SafeDirectField<List<CommonTagCompound>>() {
            @Override
            public List<CommonTagCompound> get(Object instance) {
                return PacketPlayOutMapChunkHandle.createHandle(instance).getTags();
            }

            @Override
            public boolean set(Object instance, List<CommonTagCompound> value) {
                if (PacketPlayOutMapChunkHandle.T.tags.isAvailable()) {
                    PacketPlayOutMapChunkHandle.createHandle(instance).setTags(value);
                    return true;
                }
                return false;
            }
        };

        public final FieldAccessor<Boolean> hasBiomeData = PacketPlayOutMapChunkHandle.T.hasBiomeData.toFieldAccessor();

        public CommonPacket newInstance(Chunk chunk) {
            return newInstance(chunk, 0xFFFF);
        }

        public CommonPacket newInstance(Chunk chunk, int sectionsMask) {
            return new CommonPacket(PacketPlayOutMapChunkHandle.createNew(chunk, sectionsMask).getRaw());
        }
    }
    
    public static class NMSPacketPlayOutMount extends NMSPacket {

        public final FieldAccessor<Integer> entityId = PacketPlayOutMountHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<int[]> mountedEntityIds = PacketPlayOutMountHandle.T.mountedEntityIds.toFieldAccessor();

        public CommonPacket newInstanceHandles(org.bukkit.entity.Entity entity, List<EntityHandle> passengers) {
            int[] passengerIds = new int[passengers.size()];
            for (int i = 0; i < passengerIds.length; i++) {
                passengerIds[i] = passengers.get(i).getId();
            }
            return PacketPlayOutMountHandle.createNew(entity.getEntityId(), passengerIds).toCommonPacket();
        }

        public CommonPacket newInstance(org.bukkit.entity.Entity vehicle, List<org.bukkit.entity.Entity> passengers) {
            int[] passengerIds = new int[passengers.size()];
            for (int i = 0; i < passengerIds.length; i++) {
                passengerIds[i] = passengers.get(i).getEntityId();
            }
            return PacketPlayOutMountHandle.createNew(vehicle.getEntityId(), passengerIds).toCommonPacket();
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

        public final FieldAccessor<Integer> entityId = PacketPlayOutNamedEntitySpawnHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<UUID> uuid = PacketPlayOutNamedEntitySpawnHandle.T.entityUUID.toFieldAccessor();
        public final FieldAccessor<Double> posX = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutNamedEntitySpawnHandle.createHandle(instance).getPosX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutNamedEntitySpawnHandle.createHandle(instance).setPosX(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posY = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutNamedEntitySpawnHandle.createHandle(instance).getPosY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutNamedEntitySpawnHandle.createHandle(instance).setPosY(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posZ = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutNamedEntitySpawnHandle.createHandle(instance).getPosZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutNamedEntitySpawnHandle.createHandle(instance).setPosZ(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Float> yaw = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayOutNamedEntitySpawnHandle.createHandle(instance).getYaw();
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayOutNamedEntitySpawnHandle.createHandle(instance).setYaw(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayOutNamedEntitySpawnHandle.createHandle(instance).getPitch();
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayOutNamedEntitySpawnHandle.createHandle(instance).setPitch(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Material> heldItemId = PacketPlayOutNamedEntitySpawnHandle.T.heldItem.toFieldAccessor().ignoreInvalid(Material.AIR);
        public final FieldAccessor<DataWatcher> dataWatcher = PacketPlayOutNamedEntitySpawnHandle.T.dataWatcher.toFieldAccessor();
        public final FieldAccessor<List<DataWatcher.Item<?>>> dataWatcherItems = PacketPlayOutNamedEntitySpawnHandle.T.dataWatcherItems.toFieldAccessor();
    }

    public static class NMSPacketPlayOutNamedSoundEffect extends NMSPacket {

        // Only used >= MC 1.10.2 to denote the sound bank name
        public final FieldAccessor<String> category = new SafeDirectField<String>() {
            @Override
            public String get(Object instance) {
                return PacketPlayOutNamedSoundEffectHandle.createHandle(instance).getCategory();
            }

            @Override
            public boolean set(Object instance, String value) {
                PacketPlayOutNamedSoundEffectHandle.createHandle(instance).setCategory(value);
                return true;
            }
        };

        public final FieldAccessor<ResourceKey> sound = PacketPlayOutNamedSoundEffectHandle.T.sound.toFieldAccessor();
        public final FieldAccessor<Integer> x = PacketPlayOutNamedSoundEffectHandle.T.x.toFieldAccessor();
        public final FieldAccessor<Integer> y = PacketPlayOutNamedSoundEffectHandle.T.y.toFieldAccessor();
        public final FieldAccessor<Integer> z = PacketPlayOutNamedSoundEffectHandle.T.z.toFieldAccessor();
        public final FieldAccessor<Float> volume = PacketPlayOutNamedSoundEffectHandle.T.volume.toFieldAccessor();
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayOutNamedSoundEffectHandle.createHandle(instance).getPitch();
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayOutNamedSoundEffectHandle.createHandle(instance).setPitch(value.floatValue());
                return true;
            }
        };
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

        public final FieldAccessor<ChatText> header = PacketPlayOutPlayerListHeaderFooterHandle.T.header.toFieldAccessor();
        public final FieldAccessor<ChatText> footer = PacketPlayOutPlayerListHeaderFooterHandle.T.footer.toFieldAccessor();
    }

    public static class NMSPacketPlayOutPosition extends NMSPacket {

        public final FieldAccessor<Double> x = PacketPlayOutPositionHandle.T.x.toFieldAccessor();
        public final FieldAccessor<Double> y = PacketPlayOutPositionHandle.T.y.toFieldAccessor();
        public final FieldAccessor<Double> z = PacketPlayOutPositionHandle.T.z.toFieldAccessor();
        public final FieldAccessor<Float> yaw = PacketPlayOutPositionHandle.T.yaw.toFieldAccessor();
        public final FieldAccessor<Float> pitch = PacketPlayOutPositionHandle.T.pitch.toFieldAccessor();
        public final FieldAccessor<Set<?>> teleportFlags = PacketPlayOutPositionHandle.T.teleportFlags.toFieldAccessor();
        public final FieldAccessor<Integer> unknown1 = new SafeDirectField<Integer>() {
            @Override
            public Integer get(Object instance) {
                return PacketPlayOutPositionHandle.createHandle(instance).getTeleportWaitTimer();
            }

            @Override
            public boolean set(Object instance, Integer value) {
                PacketPlayOutPositionHandle.createHandle(instance).setTeleportWaitTimer(value.intValue());
                return true;
            }
        };
    }

    public static class NMSPacketPlayOutRemoveEntityEffect extends NMSPacket {

        public final FieldAccessor<Integer> entityId = PacketPlayOutRemoveEntityEffectHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<MobEffectListHandle> effectList = PacketPlayOutRemoveEntityEffectHandle.T.effectList.toFieldAccessor();

        public CommonPacket newInstance(int entityId, MobEffectListHandle mobEffectList) {
            return PacketPlayOutRemoveEntityEffectHandle.createNew(entityId, mobEffectList).toCommonPacket();
        }
    }

    public static class NMSPacketPlayOutResourcePackSend extends NMSPacket {
        
        public final FieldAccessor<String> name = nextField("private String a");
        public final FieldAccessor<String> hash = nextField("private String b");
    }

    public static class NMSPacketPlayOutRespawn extends NMSPacket {

        public final FieldAccessor<Dimension> dimension = PacketPlayOutRespawnHandle.T.dimension.toFieldAccessor();
        public final FieldAccessor<Difficulty> difficulty = PacketPlayOutRespawnHandle.T.difficulty.toFieldAccessor();
        public final TranslatorFieldAccessor<GameMode> gamemode = PacketPlayOutRespawnHandle.T.gamemode.toFieldAccessor();
        public final TranslatorFieldAccessor<WorldType> worldType = PacketPlayOutRespawnHandle.T.worldType.toFieldAccessor();
    }

    public static class NMSPacketPlayOutScoreboardDisplayObjective extends NMSPacket {

        public final FieldAccessor<Integer> display = nextField("private int a");
        public final FieldAccessor<String> name = nextFieldSignature("private String b");
    }

    public static class NMSPacketPlayOutScoreboardObjective extends NMSPacket {

        public final FieldAccessor<String> name = PacketPlayOutScoreboardObjectiveHandle.T.name.toFieldAccessor();
        public final FieldAccessor<ChatText> displayName = PacketPlayOutScoreboardObjectiveHandle.T.displayName.toFieldAccessor();
        public final FieldAccessor<Object> criteria = PacketPlayOutScoreboardObjectiveHandle.T.criteria.toFieldAccessor();
        public final FieldAccessor<Integer> action = PacketPlayOutScoreboardObjectiveHandle.T.action.toFieldAccessor();
    }

    public static class NMSPacketPlayOutScoreboardScore extends NMSPacket {

        public final FieldAccessor<String> name = nextField("private String a");
        public final FieldAccessor<String> objName = nextFieldSignature("private String b");
        public final FieldAccessor<Integer> value = nextFieldSignature("private int c");
        public final FieldAccessor<ScoreboardAction> action = nextFieldSignature("private EnumScoreboardAction d").translate(DuplexConversion.scoreboardAction);
    }
    
    public static class NMSPacketPlayOutScoreboardTeam extends NMSPacket {

        public final FieldAccessor<String> name = PacketPlayOutScoreboardTeamHandle.T.name.toFieldAccessor();
        public final FieldAccessor<ChatText> displayName = PacketPlayOutScoreboardTeamHandle.T.displayName.toFieldAccessor();
        public final FieldAccessor<ChatText> prefix = PacketPlayOutScoreboardTeamHandle.T.prefix.toFieldAccessor();
        public final FieldAccessor<ChatText> suffix = PacketPlayOutScoreboardTeamHandle.T.suffix.toFieldAccessor();
        public final FieldAccessor<String> visibility = PacketPlayOutScoreboardTeamHandle.T.visibility.toFieldAccessor();
        public final FieldAccessor<String> collisionRule = PacketPlayOutScoreboardTeamHandle.T.collisionRule.toFieldAccessor().ignoreInvalid("");
        public final FieldAccessor<ChatColor> color = PacketPlayOutScoreboardTeamHandle.T.color.toFieldAccessor();
        public final FieldAccessor<Collection<String>> players = PacketPlayOutScoreboardTeamHandle.T.players.toFieldAccessor();
        public final FieldAccessor<Integer> mode = PacketPlayOutScoreboardTeamHandle.T.mode.toFieldAccessor();
        public final FieldAccessor<Integer> friendlyFire = PacketPlayOutScoreboardTeamHandle.T.friendlyFire.toFieldAccessor();
    }

    public static class NMSPacketPlayOutServerDifficulty extends NMSPacket {
        
        public final FieldAccessor<Difficulty> difficulty = nextField("private EnumDifficulty a").translate(DuplexConversion.difficulty);
        public final FieldAccessor<Boolean> hardcore = nextFieldSignature("private boolean b");
    }

    public static class NMSPacketPlayOutSetCooldown extends NMSPacket {

        public final FieldAccessor<Material> material = PacketPlayOutSetCooldownHandle.T.material.toFieldAccessor();
        public final FieldAccessor<Integer> cooldown = PacketPlayOutSetCooldownHandle.T.cooldown.toFieldAccessor();
    }

    public static class NMSPacketPlayOutSetSlot extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int a");
        public final FieldAccessor<Integer> slot = nextField("private int b");
        public final FieldAccessor<ItemStack> item = nextFieldSignature("private ItemStack c").translate(DuplexConversion.itemStack);
    }

    public static class NMSPacketPlayOutSpawnEntity extends NMSPacket {
        public final FieldAccessor<Integer> entityId = PacketPlayOutSpawnEntityHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<UUID> UUID = PacketPlayOutSpawnEntityHandle.T.entityUUID.toFieldAccessor().ignoreInvalid(new java.util.UUID(0L, 0L));
        public final FieldAccessor<Double> posX = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityHandle.createHandle(instance).getPosX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityHandle.createHandle(instance).setPosX(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posY = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityHandle.createHandle(instance).getPosY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityHandle.createHandle(instance).setPosY(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posZ = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityHandle.createHandle(instance).getPosZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityHandle.createHandle(instance).setPosZ(value.doubleValue());
                return true;
            }
        };
        
        public final FieldAccessor<Double> motX = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityHandle.createHandle(instance).getMotX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityHandle.createHandle(instance).setMotX(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> motY = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityHandle.createHandle(instance).getMotY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityHandle.createHandle(instance).setMotY(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> motZ = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityHandle.createHandle(instance).getMotZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityHandle.createHandle(instance).setMotZ(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayOutSpawnEntityHandle.createHandle(instance).getPitch();
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayOutSpawnEntityHandle.createHandle(instance).setPitch(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Float> yaw = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayOutSpawnEntityHandle.createHandle(instance).getYaw();
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayOutSpawnEntityHandle.createHandle(instance).setYaw(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Integer> entityType = PacketPlayOutSpawnEntityHandle.T.entityTypeId.toFieldAccessor();
        public final FieldAccessor<Integer> extraData = PacketPlayOutSpawnEntityHandle.T.extraData.toFieldAccessor();
    }

    public static class NMSPacketPlayOutSpawnEntityExperienceOrb extends NMSPacket {

        public final FieldAccessor<Integer> entityId = PacketPlayOutSpawnEntityExperienceOrbHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<Double> posX = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityExperienceOrbHandle.createHandle(instance).getPosX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityExperienceOrbHandle.createHandle(instance).setPosX(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posY = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityExperienceOrbHandle.createHandle(instance).getPosY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityExperienceOrbHandle.createHandle(instance).setPosY(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posZ = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityExperienceOrbHandle.createHandle(instance).getPosZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityExperienceOrbHandle.createHandle(instance).setPosZ(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Integer> experience = PacketPlayOutSpawnEntityExperienceOrbHandle.T.experience.toFieldAccessor();
    }

    public static class NMSPacketPlayOutSpawnEntityLiving extends NMSPacket {

        public final FieldAccessor<Integer> entityId = PacketPlayOutSpawnEntityLivingHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<UUID> entityUUID = PacketPlayOutSpawnEntityLivingHandle.T.entityUUID.toFieldAccessor().ignoreInvalid(new UUID(0L, 0L));
        public final FieldAccessor<Integer> entityType = PacketPlayOutSpawnEntityLivingHandle.T.entityTypeId.toFieldAccessor();
        public final FieldAccessor<Double> posX = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).getPosX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).setPosX(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posY = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).getPosY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).setPosY(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posZ = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).getPosZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).setPosZ(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> motX = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).getMotX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).setMotX(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> motY = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).getMotY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).setMotY(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> motZ = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).getMotZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).setMotZ(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Float> yaw = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).getYaw();
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).setYaw(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).getPitch();
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).setPitch(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Float> headYaw = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).getHeadYaw();
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).setHeadYaw(value.floatValue());
                return true;
            }
        };
        public final TranslatorFieldAccessor<DataWatcher> dataWatcher = PacketPlayOutSpawnEntityLivingHandle.T.dataWatcher.toFieldAccessor();
        public final FieldAccessor<List<DataWatcher.Item<?>>> dataWatcherItems = PacketPlayOutSpawnEntityLivingHandle.T.dataWatcherItems.toFieldAccessor();

        public CommonPacket newInstance(LivingEntity livingEntity) {
            return PacketPlayOutSpawnEntityLivingHandle.createNew(livingEntity).toCommonPacket();
        }
    }

    public static class NMSPacketPlayOutSpawnEntityPainting extends NMSPacket {

        public final FieldAccessor<Integer> entityId = PacketPlayOutSpawnEntityPaintingHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<UUID> entityUUID = PacketPlayOutSpawnEntityPaintingHandle.T.entityUUID.toFieldAccessor().ignoreInvalid(new UUID(0L, 0L));
        public final TranslatorFieldAccessor<IntVector3> postion = PacketPlayOutSpawnEntityPaintingHandle.T.position.toFieldAccessor();
        public final FieldAccessor<BlockFace> facing = PacketPlayOutSpawnEntityPaintingHandle.T.facing.toFieldAccessor();
        public final FieldAccessor<org.bukkit.Art> art = PacketPlayOutSpawnEntityPaintingHandle.T.art.toFieldAccessor();
    }

    public static class NMSPacketPlayOutSpawnEntityWeather extends NMSPacket {

        public final FieldAccessor<Integer> entityId = PacketPlayOutSpawnEntityWeatherHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<Double> posX = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityWeatherHandle.createHandle(instance).getPosX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityWeatherHandle.createHandle(instance).setPosX(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posY = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityWeatherHandle.createHandle(instance).getPosY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityWeatherHandle.createHandle(instance).setPosY(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posZ = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutSpawnEntityWeatherHandle.createHandle(instance).getPosZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutSpawnEntityWeatherHandle.createHandle(instance).setPosZ(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Integer> type = PacketPlayOutSpawnEntityWeatherHandle.T.type.toFieldAccessor();
    }

    public static class NMSPacketPlayOutSpawnPosition extends NMSPacket {

        public final TranslatorFieldAccessor<IntVector3> position = getField("position").translate(DuplexConversion.blockPosition);
    }

    public static class NMSPacketPlayOutStatistic extends NMSPacket {

        // Changed to Object2IntMap<Statistic> on MC 1.13
        // public final FieldAccessor<Map<Object, Integer>> statsMap = nextField("private Map<Statistic, Integer> a");
    }    

    public static class NMSPacketPlayOutTabComplete extends NMSPacket {

        // Changed format on MC 1.13
        // public final FieldAccessor<String[]> response = nextField("private String[] a");
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

        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(EnumTitleActionHandle.T.getType(), IChatBaseComponentHandle.T.getType(), int.class, int.class, int.class);

        public CommonPacket newInstance(int a, int b, int c) {
            return constructor1.newInstance(EnumTitleActionHandle.TIMES.getRaw(), null, a, b, c);
        }

        public CommonPacket newInstance(EnumTitleActionHandle enumTitleAction, IChatBaseComponentHandle iChatBaseComponent) {
            return constructor1.newInstance(enumTitleAction.getRaw(), iChatBaseComponent.getRaw(), -1, -1, -1);
        }

        public CommonPacket newInstance(EnumTitleActionHandle enumTitleAction, IChatBaseComponentHandle iChatBaseComponent, int a, int b, int c) {
            return constructor1.newInstance(enumTitleAction.getRaw(), iChatBaseComponent.getRaw(), a, b, c);
        }
    }

    public static class NMSPacketPlayOutTransaction extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int a");
        public final FieldAccessor<Short> action = nextField("private short b");
        public final FieldAccessor<Boolean> accepted = nextFieldSignature("private boolean c");
    }

    public static class NMSPacketPlayOutUnloadChunk extends NMSPacket {

        public final FieldAccessor<Integer> x = PacketPlayOutUnloadChunkHandle.T.cx.toFieldAccessor();
        public final FieldAccessor<Integer> z = PacketPlayOutUnloadChunkHandle.T.cz.toFieldAccessor();
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

        public final FieldAccessor<Double> posX = PacketPlayOutVehicleMoveHandle.T.posX.toFieldAccessor();
        public final FieldAccessor<Double> posY = PacketPlayOutVehicleMoveHandle.T.posY.toFieldAccessor();
        public final FieldAccessor<Double> posZ = PacketPlayOutVehicleMoveHandle.T.posZ.toFieldAccessor();
        public final FieldAccessor<Float> yaw = PacketPlayOutVehicleMoveHandle.T.yaw.toFieldAccessor();
        public final FieldAccessor<Float> pitch = PacketPlayOutVehicleMoveHandle.T.pitch.toFieldAccessor();
    }

    public static class NMSPacketPlayOutWindowData extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int a");
        public final FieldAccessor<Integer> count = nextFieldSignature("private int b");
        public final FieldAccessor<Integer> data = nextFieldSignature("private int c");
    }

    public static class NMSPacketPlayOutWindowItems extends NMSPacket {

        public final FieldAccessor<Integer> windowId = PacketPlayOutWindowItemsHandle.T.windowId.toFieldAccessor();
        public final FieldAccessor<List<ItemStack>> items = PacketPlayOutWindowItemsHandle.T.items.toFieldAccessor();
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
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(CommonUtil.getNMSClass("WorldBorder"), CommonUtil.getNMSClass("PacketPlayOutWorldBorder.EnumWorldBorderAction"));

        public CommonPacket newInstance (Object nmsWorldBorder, Object nmsEnumBorderAction) {
            return constructor1.newInstance(nmsWorldBorder, nmsEnumBorderAction);
        }
    }

    public static class NMSPacketPlayOutWorldEvent extends NMSPacket {

        public final FieldAccessor<Integer> effectId = nextField("private int a");
        public final TranslatorFieldAccessor<IntVector3> position = nextFieldSignature("private BlockPosition b").translate(DuplexConversion.blockPosition);
        public final FieldAccessor<Integer> data = nextFieldSignature("private int c");
        public final FieldAccessor<Boolean> noRelativeVolume = nextFieldSignature("private boolean d");
    }

    public static class NMSPacketPlayOutWorldParticles extends NMSPacket {

        public final FieldAccessor<Float> x = PacketPlayOutWorldParticlesHandle.T.x.toFieldAccessor();
        public final FieldAccessor<Float> y = PacketPlayOutWorldParticlesHandle.T.y.toFieldAccessor();
        public final FieldAccessor<Float> z = PacketPlayOutWorldParticlesHandle.T.z.toFieldAccessor();
        public final FieldAccessor<Float> randomX = PacketPlayOutWorldParticlesHandle.T.randomX.toFieldAccessor();
        public final FieldAccessor<Float> randomY = PacketPlayOutWorldParticlesHandle.T.randomY.toFieldAccessor();
        public final FieldAccessor<Float> randomZ = PacketPlayOutWorldParticlesHandle.T.randomZ.toFieldAccessor();
        public final FieldAccessor<Float> speed = PacketPlayOutWorldParticlesHandle.T.speed.toFieldAccessor();
        public final FieldAccessor<Integer> particleCount = PacketPlayOutWorldParticlesHandle.T.count.toFieldAccessor();
        public final FieldAccessor<Boolean> longDistance = PacketPlayOutWorldParticlesHandle.T.longDistance.toFieldAccessor();

        /*
        public CommonPacket newInstance(String name, int count, Location location, double randomness, double speed) {
            return newInstance(name, count, location.getX(), location.getY(), location.getZ(), randomness, randomness, randomness, speed);
        }

        public CommonPacket newInstance(String name, int count, double x, double y, double z, double rx, double ry, double rz, double speed) {
            final CommonPacket packet = newInstance();
            packet.write(this.particle, EnumParticleHandle.getRaw(EnumParticleHandle.getByName(name)));
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
        */
    }

    //////////////////////////////////////////////////////////////////////

    public static class NMSPacketPlayOutUpdateSign extends NMSPacket {

        public final FieldAccessor<World> world = PacketPlayOutUpdateSignHandle.T.world.toFieldAccessor();
        public final FieldAccessor<IntVector3> position = PacketPlayOutUpdateSignHandle.T.position.toFieldAccessor();
        public final FieldAccessor<ChatText[]> lines = PacketPlayOutUpdateSignHandle.T.lines.toFieldAccessor();
    }

}
