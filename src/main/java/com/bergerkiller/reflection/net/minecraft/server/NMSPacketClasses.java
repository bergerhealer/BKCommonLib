package com.bergerkiller.reflection.net.minecraft.server;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockStateChange;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.InventoryClickType;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.bukkit.common.wrappers.ScoreboardAction;
import com.bergerkiller.bukkit.common.wrappers.WindowType;
import com.bergerkiller.generated.net.minecraft.core.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInAbilitiesHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInArmAnimationHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInBlockDigHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInBlockPlaceHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInBoatMoveHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInChatHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInCloseWindowHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInEnchantItemHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInEntityActionHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInFlyingHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInHeldItemSlotHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInKeepAliveHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInResourcePackStatusHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInSetCreativeSlotHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInSettingsHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInSpectateHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInSteerVehicleHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInTeleportAcceptHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInUpdateSignHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInUseEntityHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInUseItemHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInVehicleMoveHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInWindowClickHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutAbilitiesHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutAdvancementsHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutAttachEntityHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutBlockActionHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimationHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutBlockChangeHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutCameraHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutCollectHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutCustomPayloadHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutCustomSoundEffectHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityDestroyHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityEffectHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityEquipmentHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotationHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityMetadataHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityTeleportHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityVelocityHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutExplosionHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutKeepAliveHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutKickDisconnectHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutLoginHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutMapChunkHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutMapHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutMountHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawnHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutNamedSoundEffectHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditorHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutOpenWindowHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutPlayerInfoHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutPlayerListHeaderFooterHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutPositionHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutRemoveEntityEffectHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutResourcePackSendHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutRespawnHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjectiveHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutScoreboardScoreHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeamHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutSetCooldownHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutSetSlotHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityExperienceOrbHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLivingHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityPaintingHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityWeatherHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutSpawnPositionHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutTileEntityDataHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutUnloadChunkHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutUpdateAttributesHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutUpdateSignHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutVehicleMoveHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutWindowItemsHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutWorldEventHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutWorldParticlesHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityHandle.PacketPlayOutEntityLookHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityHandle.PacketPlayOutRelEntityMoveHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityHandle.PacketPlayOutRelEntityMoveLookHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectListHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeModifiableHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.EntityHumanHandle;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeConstructor;
import com.bergerkiller.mountiplex.reflection.SafeDirectField;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

public class NMSPacketClasses {

    public static class NMSPacket extends PacketType {
        private FastMethod<CommonPacket> _constructor0 = null;

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
                ClassResolver resolver = new ClassResolver();
                resolver.addImport(CommonPacket.class.getName());
                resolver.setDeclaredClass(this.getType());
                resolver.setAllVariables(Common.TEMPLATE_RESOLVER);
                MethodDeclaration mDec = new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                        "public static CommonPacket newInstance() {\n" +
                        "#if version >= 1.17\n" +
                        "    Object packet = new " + this.getType().getName() + "(com.bergerkiller.bukkit.common.internal.logic.NullPacketDataSerializer.INSTANCE);\n" +
                        "#else\n" +
                        "    Object packet = new " + this.getType().getName() + "();\n" +
                        "#endif\n" +
                        "    return new CommonPacket(packet);\n" +
                        "}", resolver));
                this._constructor0 = new FastMethod<CommonPacket>();
                this._constructor0.init(mDec);
                this._constructor0.forceInitialization();
            }
            return this._constructor0.invoke(null);
        }
    }

    /*
     * ========================================================================================
     * ============================= Incoming packets start ===================================
     * ========================================================================================
     */

    public static class NMSPacketPlayInAbilities extends NMSPacket {
        //TODO: Only has 'isFlying' property since 1.16
        //Do we care about the other fields for past versions?
        public final FieldAccessor<Boolean> isFlying = PacketPlayInAbilitiesHandle.T.isFlying.toFieldAccessor();
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

    /**
     * @deprecated Use {@link PacketPlayInBlockDigHandle} instead
     */
    @Deprecated
    public static class NMSPacketPlayInBlockDig extends NMSPacket {

        public final FieldAccessor<IntVector3> position = PacketPlayInBlockDigHandle.T.position.toFieldAccessor();
        public final FieldAccessor<BlockFace> direction = PacketPlayInBlockDigHandle.T.direction.toFieldAccessor();
        public final FieldAccessor<PacketPlayInBlockDigHandle.EnumPlayerDigTypeHandle> status = PacketPlayInBlockDigHandle.T.digType.toFieldAccessor();
    }

    public static class NMSPacketPlayInBlockPlace extends NMSPacket {

        public final FieldAccessor<Long> timestamp = PacketPlayInBlockPlaceHandle.T.timestamp.toFieldAccessor().ignoreInvalid(0L);

        @Override
        protected boolean matchPacket(Object packetHandle) {
            if (CommonCapabilities.PLACE_PACKETS_MERGED) {
                return PacketPlayInUseItemHandle.T.isBlockPlacePacket.invoke(packetHandle).booleanValue();
            } else {
                return true;
            }
        }

        @Override
        public void preprocess(Object packetHandle) {
            if (CommonCapabilities.PLACE_PACKETS_MERGED) {
                PacketPlayInUseItemHandle.T.setBlockPlacePacket.invoke(packetHandle);
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

        public final FieldAccessor<IntVector3> position = new SafeDirectField<IntVector3>() {
            @Override
            public IntVector3 get(Object instance) {
                return PacketPlayInUseItemHandle.T.getPosition.invoke(instance);
            }

            @Override
            public boolean set(Object instance, IntVector3 value) {
                PacketPlayInUseItemHandle.T.setPosition.invoke(instance, value);
                return true;
            }
        };
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
        
        
        public final FieldAccessor<Float> deltaX = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayInUseItemHandle.T.getDeltaX.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayInUseItemHandle.T.setDeltaX.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Float> deltaY = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayInUseItemHandle.T.getDeltaY.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayInUseItemHandle.T.setDeltaY.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Float> deltaZ = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayInUseItemHandle.T.getDeltaZ.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayInUseItemHandle.T.setDeltaZ.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Long> timestamp = PacketPlayInUseItemHandle.T.timestamp.toFieldAccessor().ignoreInvalid(0L);

        @Override
        protected boolean matchPacket(Object packetHandle) {
            if (CommonCapabilities.PLACE_PACKETS_MERGED) {
                return !PacketPlayInUseItemHandle.T.isBlockPlacePacket.invoke(packetHandle).booleanValue();
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
            PacketPlayInUseItemHandle.T.setHand.invoke(packet.getHandle(), humanEntity, humanHand);
        }

        /**
         * Gets the hand that used the item
         * 
         * @param packet to read from
         * @param humanEntity used for translating the hand from MAIN/OFF to LEFT/RIGHT, can be null
         * @return humanHand
         */
        public final HumanHand getHand(CommonPacket packet, HumanEntity humanEntity) {
            return PacketPlayInUseItemHandle.T.getHand.invoke(packet.getHandle(), humanEntity);
        }
    }

    public static class NMSPacketPlayInBoatMove extends NMSPacket {
        
        public final FieldAccessor<Boolean> leftPaddle = PacketPlayInBoatMoveHandle.T.leftPaddle.toFieldAccessor();
        public final FieldAccessor<Boolean> rightPaddle = PacketPlayInBoatMoveHandle.T.rightPaddle.toFieldAccessor();
    }

    public static class NMSPacketPlayInChat extends NMSPacket {

        public final FieldAccessor<String> message = PacketPlayInChatHandle.T.message.toFieldAccessor();
    }

    public static class NMSPacketPlayInClientCommand extends NMSPacket {

        public final FieldAccessor<Object> command = nextField("private PacketPlayInClientCommand.EnumClientCommand a");
    }

    public static class NMSPacketPlayInCloseWindow extends NMSPacket {

        public final FieldAccessor<Integer> windowId = PacketPlayInCloseWindowHandle.T.windowId.toFieldAccessor();
    }

    public static class NMSPacketPlayInCustomPayload extends NMSPacket {

        // public final FieldAccessor<String> tag = nextField("private String a");
        // public final FieldAccessor<Object> data = nextFieldSignature("private PacketDataSerializer b");
    }

    public static class NMSPacketPlayInEnchantItem extends NMSPacket {

        public final FieldAccessor<Integer> windowId = PacketPlayInEnchantItemHandle.T.windowId.toFieldAccessor();
        public final FieldAccessor<Integer> buttonId = PacketPlayInEnchantItemHandle.T.buttonId.toFieldAccessor();
    }

    public static class NMSPacketPlayInEntityAction extends NMSPacket {

        public final FieldAccessor<Integer> playerId = PacketPlayInEntityActionHandle.T.playerId.toFieldAccessor();
        public final FieldAccessor<Object> action = PacketPlayInEntityActionHandle.T.action.toFieldAccessor();
        public final FieldAccessor<Integer> jumpBoost = PacketPlayInEntityActionHandle.T.data.toFieldAccessor();
    }

    public static class NMSPacketPlayInFlying extends NMSPacket {

        protected NMSPacketPlayInFlying(String subType) {
            super(CommonUtil.getClass("net.minecraft.network.protocol.game.PacketPlayInFlying." + subType));
        }

        public final FieldAccessor<Double> x = PacketPlayInFlyingHandle.T.x.toFieldAccessor();
        public final FieldAccessor<Double> y = PacketPlayInFlyingHandle.T.y.toFieldAccessor();
        public final FieldAccessor<Double> z = PacketPlayInFlyingHandle.T.z.toFieldAccessor();
        public final FieldAccessor<Float> yaw = PacketPlayInFlyingHandle.T.yaw.toFieldAccessor();
        public final FieldAccessor<Float> pitch = PacketPlayInFlyingHandle.T.pitch.toFieldAccessor();
        public final FieldAccessor<Boolean> onGround = PacketPlayInFlyingHandle.T.onGround.toFieldAccessor();
        public final FieldAccessor<Boolean> hasPos = PacketPlayInFlyingHandle.T.hasPos.toFieldAccessor();
        public final FieldAccessor<Boolean> hasLook = PacketPlayInFlyingHandle.T.hasLook.toFieldAccessor();
    }

    public static class NMSPacketPlayInLook extends NMSPacketPlayInFlying {
        public NMSPacketPlayInLook() {
            super("PacketPlayInLook");
        }
    }

    public static class NMSPacketPlayInPosition extends NMSPacketPlayInFlying {
        public NMSPacketPlayInPosition() {
            super("PacketPlayInPosition");
        }
    }

    public static class NMSPacketPlayInPositionLook extends NMSPacketPlayInFlying {
        public NMSPacketPlayInPositionLook() {
            super("PacketPlayInPositionLook");
        }
    }

    public static class NMSPacketPlayInHeldItemSlot extends NMSPacket {

        public final FieldAccessor<Integer> slot = PacketPlayInHeldItemSlotHandle.T.itemInHandIndex.toFieldAccessor();
    }

    public static class NMSPacketPlayInKeepAlive extends NMSPacket {

        public final FieldAccessor<Long> key = new SafeDirectField<Long>() {
            @Override
            public Long get(Object instance) {
                return Long.valueOf(PacketPlayInKeepAliveHandle.createHandle(instance).getKey());
            }

            @Override
            public boolean set(Object instance, Long value) {
                PacketPlayInKeepAliveHandle.createHandle(instance).setKey(value.longValue());
                return true;
            }
        };
    }

    public static class NMSPacketPlayInResourcePackStatus extends NMSPacket {

        public final FieldAccessor<Object> enumStatus = PacketPlayInResourcePackStatusHandle.T.status.toFieldAccessor();
    }

    public static class NMSPacketPlayInSetCreativeSlot extends NMSPacket {

        public final FieldAccessor<Integer> slot = PacketPlayInSetCreativeSlotHandle.T.slot.toFieldAccessor();
        public final FieldAccessor<ItemStack> item = PacketPlayInSetCreativeSlotHandle.T.item.toFieldAccessor();
    }

    public static class NMSPacketPlayInSettings extends NMSPacket {

        public final FieldAccessor<String> locale = PacketPlayInSettingsHandle.T.locale.toFieldAccessor();
        public final FieldAccessor<Integer> view = PacketPlayInSettingsHandle.T.view.toFieldAccessor();
        public final FieldAccessor<Object> chatVisibility = PacketPlayInSettingsHandle.T.chatVisibility.toFieldAccessor();
        public final FieldAccessor<Boolean> enableColors = PacketPlayInSettingsHandle.T.enableColors.toFieldAccessor();
        public final FieldAccessor<Integer> modelPartFlags = PacketPlayInSettingsHandle.T.modelPartFlags.toFieldAccessor();
        public final FieldAccessor<HumanHand> mainHand = PacketPlayInSettingsHandle.T.mainHand.toFieldAccessor().ignoreInvalid(HumanHand.RIGHT);
    }

    public static class NMSPacketPlayInSpectate extends NMSPacket {

        public final FieldAccessor<UUID> uuid = PacketPlayInSpectateHandle.T.uuid.toFieldAccessor();

        public CommonPacket newInstance(UUID uuid) {
            return PacketPlayInSpectateHandle.createNew(uuid).toCommonPacket();
        }
    }

    public static class NMSPacketPlayInSteerVehicle extends NMSPacket {

        public final FieldAccessor<Float> sideways = PacketPlayInSteerVehicleHandle.T.sideways.toFieldAccessor();
        public final FieldAccessor<Float> forwards = PacketPlayInSteerVehicleHandle.T.forwards.toFieldAccessor();
        public final FieldAccessor<Boolean> jump = PacketPlayInSteerVehicleHandle.T.jump.toFieldAccessor();
        public final FieldAccessor<Boolean> unmount = PacketPlayInSteerVehicleHandle.T.unmount.toFieldAccessor();
    }

    public static class NMSPacketPlayInTabComplete extends NMSPacket {

        // public final FieldAccessor<String> text = PacketPlayInTabCompleteHandle.T.text.toFieldAccessor();
        // public final FieldAccessor<Boolean> assumeCommand = PacketPlayInTabCompleteHandle.T.assumeCommand.toFieldAccessor().ignoreInvalid(false);
        // public final FieldAccessor<IntVector3> position = PacketPlayInTabCompleteHandle.T.position.toFieldAccessor();
    }

    public static class NMSPacketPlayInTeleportAccept extends NMSPacket {

        public final FieldAccessor<Integer> teleportId = PacketPlayInTeleportAcceptHandle.T.teleportId.toFieldAccessor();
    }

    // Gone since 1.17, won't bother supporting
    /*
    public static class NMSPacketPlayInTransaction extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int a");
        public final FieldAccessor<Short> action = nextFieldSignature("private short b");
        public final FieldAccessor<Boolean> accepted = nextFieldSignature("private boolean c");
    }

    public static class NMSPacketPlayOutTransaction extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int a");
        public final FieldAccessor<Short> action = nextField("private short b");
        public final FieldAccessor<Boolean> accepted = nextFieldSignature("private boolean c");
    }
    */

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

    /**
     * @deprecated Please use PacketPlayInUseEntityHandle instead for complete api support
     */
    @Deprecated
    public static class NMSPacketPlayInUseEntity extends NMSPacket {
        public final FieldAccessor<Integer> clickedEntityId = PacketPlayInUseEntityHandle.T.usedEntityId.toFieldAccessor();

        /**
         * Sets the hand that interacted with the entity
         * 
         * @param packet to write to
         * @param humanEntity used for translating the hand from MAIN/OFF to LEFT/RIGHT, can be null
         * @param humanHand to set to
         */
        public final void setHand(CommonPacket packet, HumanEntity humanEntity, HumanHand humanHand) {
        }

        /**
         * Gets the hand that interacted with the entity
         * 
         * @param packet to read from
         * @param humanEntity used for translating the hand from MAIN/OFF to LEFT/RIGHT, can be null
         * @return humanHand
         */
        public final HumanHand getHand(CommonPacket packet, HumanEntity humanEntity) {
            return PacketPlayInUseEntityHandle.createHandle(packet.getHandle()).getInteractHand(humanEntity);
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
        public final FieldAccessor<ItemStack> item = PacketPlayInWindowClickHandle.T.item.toFieldAccessor();
        public final FieldAccessor<InventoryClickType> mode = PacketPlayInWindowClickHandle.T.mode.toFieldAccessor();
    }

    /*
     * ========================================================================================
     * ============================= Outgoing packets start ===================================
     * ========================================================================================
     */

    public static class NMSPacketPlayOutAbilities extends NMSPacket {

        public final FieldAccessor<Boolean> isInvulnerable = PacketPlayOutAbilitiesHandle.T.invulnerable.toFieldAccessor();
        public final FieldAccessor<Boolean> isFlying = PacketPlayOutAbilitiesHandle.T.isFlying.toFieldAccessor();
        public final FieldAccessor<Boolean> canFly = PacketPlayOutAbilitiesHandle.T.canFly.toFieldAccessor();
        public final FieldAccessor<Boolean> canInstantlyBuild = PacketPlayOutAbilitiesHandle.T.instabuild.toFieldAccessor();
        public final FieldAccessor<Float> flySpeed = PacketPlayOutAbilitiesHandle.T.flyingSpeed.toFieldAccessor();
        public final FieldAccessor<Float> walkSpeed = PacketPlayOutAbilitiesHandle.T.walkingSpeed.toFieldAccessor();

        public CommonPacket newInstance(PlayerAbilities abilities) {
            return PacketPlayOutAbilitiesHandle.createNew(abilities).toCommonPacket();
        }
    }

    public static class NMSPacketPlayOutAdvancements extends NMSPacket {

        public final FieldAccessor<Boolean> initial = PacketPlayOutAdvancementsHandle.T.initial.toFieldAccessor();
        //TODO: Fields
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

        public CommonPacket newInstanceMount(org.bukkit.entity.Entity passenger, org.bukkit.entity.Entity vehicle) {
            return new CommonPacket(PacketPlayOutAttachEntityHandle.createNewMount(passenger, vehicle).getRaw());
        }

        public CommonPacket newInstanceLeash(org.bukkit.entity.Entity leashedEntity, org.bukkit.entity.Entity holderEntity) {
            return new CommonPacket(PacketPlayOutAttachEntityHandle.createNewLeash(leashedEntity, holderEntity).getRaw());
        }
    }

    public static class NMSPacketPlayOutBed extends NMSPacket {

        public final FieldAccessor<Integer> entityId = nextField("private int a");
        public final TranslatorFieldAccessor<IntVector3> bedPosition = nextFieldSignature("private BlockPosition b").translate(DuplexConversion.blockPosition);
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(EntityHumanHandle.T.getType(), BlockPositionHandle.T.getType());

        public CommonPacket newInstance(HumanEntity entity, IntVector3 bedPosition) {
            return constructor1.newInstance(HandleConversion.toEntityHandle(entity), Conversion.toBlockPositionHandle.convert(bedPosition));
        }
    }

    public static class NMSPacketPlayOutBlockAction extends NMSPacket {

        public final TranslatorFieldAccessor<IntVector3> position = PacketPlayOutBlockActionHandle.T.position.toFieldAccessor();
        public final FieldAccessor<Integer> b0 = PacketPlayOutBlockActionHandle.T.b0.toFieldAccessor();
        public final FieldAccessor<Integer> b1 = PacketPlayOutBlockActionHandle.T.b1.toFieldAccessor();
        public final FieldAccessor<Material> block = PacketPlayOutBlockActionHandle.T.block.toFieldAccessor();
    }

    public static class NMSPacketPlayOutBlockBreakAnimation extends NMSPacket {

        public final FieldAccessor<Integer> id = PacketPlayOutBlockBreakAnimationHandle.T.id.toFieldAccessor();
        public final TranslatorFieldAccessor<IntVector3> position = PacketPlayOutBlockBreakAnimationHandle.T.position.toFieldAccessor();
        public final FieldAccessor<Integer> progress = PacketPlayOutBlockBreakAnimationHandle.T.progress.toFieldAccessor();
    }

    public static class NMSPacketPlayOutBlockChange extends NMSPacket {

        public final TranslatorFieldAccessor<IntVector3> position = PacketPlayOutBlockChangeHandle.T.position.toFieldAccessor();
        public final TranslatorFieldAccessor<BlockData> blockData = PacketPlayOutBlockChangeHandle.T.blockData.toFieldAccessor();
    }

    @Deprecated
    public static class NMSPacketPlayOutBoss extends NMSPacket {
    }

    public static class NMSPacketPlayOutCamera extends NMSPacket {

        public final FieldAccessor<Integer> entityId = PacketPlayOutCameraHandle.T.entityId.toFieldAccessor();
    }

    public static class NMSPacketPlayOutCloseWindow extends NMSPacket {

        public final FieldAccessor<Integer> windowId = nextField("private int a");
    }    

    public static class NMSPacketPlayOutCollect extends NMSPacket {

        public final FieldAccessor<Integer> collectedItemId = PacketPlayOutCollectHandle.T.collectedItemId.toFieldAccessor();
        public final FieldAccessor<Integer> collectorEntityId = PacketPlayOutCollectHandle.T.collectorEntityId.toFieldAccessor();
        public final FieldAccessor<Integer> amount;
        
        public NMSPacketPlayOutCollect() {
            if (PacketPlayOutCollectHandle.T.amount.isAvailable()) {
                this.amount = PacketPlayOutCollectHandle.T.amount.toFieldAccessor();
            } else {
                this.amount = null;
            }
        }
    }

    // Gone since 1.17, not worth keeping.
    /*
    public static class NMSPacketPlayOutCombatEvent extends NMSPacket {

          public final FieldAccessor<Object> eventType = PacketPlayOutCombatEventHandle.T.eventType.toFieldAccessor();
          public final FieldAccessor<Integer> entityId1 = PacketPlayOutCombatEventHandle.T.entityId1.toFieldAccessor();
          public final FieldAccessor<Integer> entityId2 = PacketPlayOutCombatEventHandle.T.entityId2.toFieldAccessor();
          public final FieldAccessor<Integer> tickDuration = PacketPlayOutCombatEventHandle.T.tickDuration.toFieldAccessor();
          public final FieldAccessor<ChatText> message = PacketPlayOutCombatEventHandle.T.message.toFieldAccessor();
    }
    */

    public static class NMSPacketPlayOutCustomPayload extends NMSPacket {

        public static final FieldAccessor<String> channel = new SafeDirectField<String>() {
            @Override
            public String get(Object instance) {
                return PacketPlayOutCustomPayloadHandle.createHandle(instance).getChannel();
            }

            @Override
            public boolean set(Object instance, String value) {
                PacketPlayOutCustomPayloadHandle.createHandle(instance).setChannel(value);
                return true;
            }
        };

        public static final FieldAccessor<byte[]> message = new SafeDirectField<byte[]>() {
            @Override
            public byte[] get(Object instance) {
                return PacketPlayOutCustomPayloadHandle.createHandle(instance).getMessage();
            }

            @Override
            public boolean set(Object instance, byte[] value) {
                return false;
            }
        };

        public static CommonPacket createNew(String channel, byte[] message) {
            return PacketPlayOutCustomPayloadHandle.createNew(channel, message).toCommonPacket();
        }
    }

    public static class NMSPacketPlayOutCustomSoundEffect extends NMSPacket {

        public final FieldAccessor<ResourceKey<SoundEffect>> sound = PacketPlayOutCustomSoundEffectHandle.T.sound.toFieldAccessor();
        public final FieldAccessor<String> category = PacketPlayOutCustomSoundEffectHandle.T.category.toFieldAccessor();
        public final FieldAccessor<Integer> x = PacketPlayOutCustomSoundEffectHandle.T.x.toFieldAccessor();
        public final FieldAccessor<Integer> y = PacketPlayOutCustomSoundEffectHandle.T.y.toFieldAccessor();
        public final FieldAccessor<Integer> z = PacketPlayOutCustomSoundEffectHandle.T.z.toFieldAccessor();
        public final FieldAccessor<Float> volume = PacketPlayOutCustomSoundEffectHandle.T.volume.toFieldAccessor();
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return Float.valueOf(PacketPlayOutCustomSoundEffectHandle.createHandle(instance).getPitch());
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayOutCustomSoundEffectHandle.createHandle(instance).setPitch(value.floatValue());
                return true;
            }
        };
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
                return PacketPlayOutEntityHandle.createHandle(instance).getYaw();
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayOutEntityHandle.createHandle(instance).setYaw(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Float> dpitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayOutEntityHandle.createHandle(instance).getPitch();
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayOutEntityHandle.createHandle(instance).setPitch(value.floatValue());
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
            super(PacketPlayOutRelEntityMoveHandle.T.getType());
        }

        public CommonPacket newInstance(int entityId, double dx, double dy, double dz, boolean onGround) {
            return PacketPlayOutRelEntityMoveHandle.createNew(entityId, dx, dy, dz, onGround).toCommonPacket();
        }
    }

    public static class NMSPacketPlayOutRelEntityMoveLook extends NMSPacketPlayOutEntity {

        public NMSPacketPlayOutRelEntityMoveLook() {
            super(PacketPlayOutRelEntityMoveLookHandle.T.getType());
        }

        public CommonPacket newInstance(int entityId, double dx, double dy, double dz, float dyaw, float dpitch, boolean onGround) {
            return PacketPlayOutRelEntityMoveLookHandle.createNew(entityId, dx, dy, dz, dyaw, dpitch, onGround).toCommonPacket();
        }
    }

    public static class NMSPacketPlayOutEntityLook extends NMSPacketPlayOutEntity {

        public NMSPacketPlayOutEntityLook() {
            super(PacketPlayOutEntityLookHandle.T.getType());
        }

        public CommonPacket newInstance(int entityId, float dyaw, float dpitch, boolean onGround) {
            return PacketPlayOutEntityLookHandle.createNew(entityId, dyaw, dpitch, onGround).toCommonPacket();
        }
    }

    // ====================================================================================================

    public static class NMSPacketPlayOutEntityDestroy extends NMSPacket {

        /**
         * <b>Warning: </b>Setting multiple entity id's
         * is not supported on Minecraft 1.17 and later. Getting is always safe.
         */
        public final FieldAccessor<int[]> entityIds = new SafeDirectField<int[]>() {
            @Override
            public int[] get(Object instance) {
                return PacketPlayOutEntityDestroyHandle.T.getEntityIds.invoke(instance);
            }

            @Override
            public boolean set(Object instance, int[] value) {
                PacketPlayOutEntityDestroyHandle.T.setMultipleEntityIds.invoke(instance, value);
                return true;
            }
        };

        /**
         * <b>Warning: </b>Getting this field on Minecraft 1.16 and earlier may raise an
         * exception if multiple entity id's are stored. Setting is always safe.</b>
         */
        public final FieldAccessor<Integer> entityId = new SafeDirectField<Integer>() {
            @Override
            public Integer get(Object instance) {
                return PacketPlayOutEntityDestroyHandle.T.getSingleEntityId.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Integer value) {
                PacketPlayOutEntityDestroyHandle.T.setSingleEntityId.invoke(instance, value);
                return true;
            }
        };

        /**
         * Gets whether on this server version, destroying multiple entities at once
         * is supported.
         *
         * @return True if multiple entity ids can be specified at once
         */
        public boolean canSupportMultipleEntityIds() {
            return CommonCapabilities.PACKET_DESTROY_MULTIPLE;
        }

        /**
         * Creates a new destroy packet for destroying a single entity
         *
         * @param entityId
         * @return packet
         */
        public CommonPacket newInstanceSingle(int entityId) {
            Object raw = PacketPlayOutEntityDestroyHandle.T.createNewSingle.raw.invoke(entityId);
            return new CommonPacket(raw, PacketType.OUT_ENTITY_DESTROY);
        }

        /**
         * <b>Warning: </b>Multiple entity id's only supported on Minecraft 1.16 and earlier!
         *
         * @param entityIds
         * @return packet
         */
        public CommonPacket newInstanceMultiple(int... entityIds) {
            Object raw = PacketPlayOutEntityDestroyHandle.T.createNewMultiple.raw.invoke(entityIds);
            return new CommonPacket(raw, PacketType.OUT_ENTITY_DESTROY);
        }

        /**
         * <b>Warning: </b>Multiple entity id's only supported on Minecraft 1.16 and earlier!<br>
         * <br>
         * Creates a new instance with the entity Ids specified.
         * Note: input Collection will be copied.
         * 
         * @param entityIds
         * @return packet
         */
        public CommonPacket newInstanceMultiple(Collection<Integer> entityIds) {
            return newInstanceMultiple(Conversion.toIntArr.convert(entityIds));
        }

        /**
         * <b>Warning: </b>Multiple entity id's only supported on Minecraft 1.16 and earlier!
         *
         * @param entityIds
         * @return packet
         */
        public CommonPacket newInstanceMultiple(org.bukkit.entity.Entity... entities) {
            int[] ids = new int[entities.length];
            for (int i = 0; i < ids.length; i++) {
                ids[i] = entities[i].getEntityId();
            }
            return newInstanceMultiple(ids);
        }
    }

    public static class NMSPacketPlayOutEntityEffect extends NMSPacket {

        public final FieldAccessor<Integer> entityId = PacketPlayOutEntityEffectHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<PotionEffectType> effect = new SafeDirectField<PotionEffectType>() {
            @Override
            public PotionEffectType get(Object instance) {
                return PacketPlayOutEntityEffectHandle.T.getEffect.invoke(instance);
            }

            @Override
            public boolean set(Object instance, PotionEffectType value) {
                PacketPlayOutEntityEffectHandle.T.setEffect.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Byte> effectAmplifier = PacketPlayOutEntityEffectHandle.T.effectAmplifier.toFieldAccessor();
        public final FieldAccessor<Integer> effectDuration = PacketPlayOutEntityEffectHandle.T.effectDurationTicks.toFieldAccessor();
        public final FieldAccessor<Byte> effectFlags = PacketPlayOutEntityEffectHandle.T.flags.toFieldAccessor();

        /**
         * @deprecated Use {@link #effect} instead
         */
        @Deprecated
        public final FieldAccessor<Integer> effectId = new SafeDirectField<Integer>() {
            @Override
            public Integer get(Object instance) {
                PotionEffectType type = PacketPlayOutEntityEffectHandle.T.getEffect.invoke(instance);
                return (type == null) ? -1 : type.getId();
            }

            @Override
            public boolean set(Object instance, Integer value) {
                PotionEffectType type = PotionEffectType.getById(value);
                PacketPlayOutEntityEffectHandle.T.setEffect.invoke(instance, type);
                return true;
            }
        };

        public CommonPacket newInstance(int entityId, MobEffectHandle mobEffect) {
            return new CommonPacket(PacketPlayOutEntityEffectHandle.T.constr_entityId_mobeffect.raw.newInstance(entityId, mobEffect.getRaw()));
        }

        public CommonPacket newInstance(int entityId, PotionEffect effect) {
            return PacketPlayOutEntityEffectHandle.createNew(entityId, effect).toCommonPacket();
        }
    }

    /**
     * <b>Deprecated: please use {@link PacketPlayOutEntityEquipmentHandle} instead.</b>
     */
    @Deprecated
    public static class NMSPacketPlayOutEntityEquipment extends NMSPacket {

        public final FieldAccessor<Integer> entityId = PacketPlayOutEntityEquipmentHandle.T.entityId.toFieldAccessor();

        public CommonPacket newInstance(int entityId, EquipmentSlot equipmentSlot, ItemStack item) {
            return PacketPlayOutEntityEquipmentHandle.createNew(entityId, equipmentSlot, item).toCommonPacket();
        }
    }

    public static class NMSPacketPlayOutEntityHeadRotation extends NMSPacket {
        public final FieldAccessor<Integer> entityId = PacketPlayOutEntityHeadRotationHandle.T.entityId.toFieldAccessor();

        public final FieldAccessor<Float> headYaw = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return PacketPlayOutEntityHeadRotationHandle.createHandle(instance).getHeadYaw();
            }

            @Override
            public boolean set(Object instance, Float value) {
                PacketPlayOutEntityHeadRotationHandle.createHandle(instance).setHeadYaw(value.floatValue());
                return true;
            }
        };

        public CommonPacket newInstance(org.bukkit.entity.Entity entity, float headRotation) {
            return PacketPlayOutEntityHeadRotationHandle.createNew(entity, headRotation).toCommonPacket();
        }
    }

    public static class NMSPacketPlayOutEntityMetadata extends NMSPacket {

        public final FieldAccessor<Integer> entityId = PacketPlayOutEntityMetadataHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<List<DataWatcher.PackedItem<Object>>> watchedObjects = PacketPlayOutEntityMetadataHandle.T.metadataItems.toFieldAccessor();

        public CommonPacket newForSpawn(int entityId, DataWatcher dataWatcher) {
            return PacketPlayOutEntityMetadataHandle.createForSpawn(entityId, dataWatcher).toCommonPacket();
        }

        public CommonPacket newForChanges(int entityId, DataWatcher dataWatcher) {
            return PacketPlayOutEntityMetadataHandle.createForChanges(entityId, dataWatcher).toCommonPacket();
        }

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

        public final FieldAccessor<Integer> entityId = PacketPlayOutEntityVelocityHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<Integer> motX = PacketPlayOutEntityVelocityHandle.T.motX_raw.toFieldAccessor();
        public final FieldAccessor<Integer> motY = PacketPlayOutEntityVelocityHandle.T.motY_raw.toFieldAccessor();
        public final FieldAccessor<Integer> motZ = PacketPlayOutEntityVelocityHandle.T.motZ_raw.toFieldAccessor();

        @Deprecated
        public CommonPacket newInstance(org.bukkit.entity.Entity entity) {
            return PacketPlayOutEntityVelocityHandle.createNew(entity).toCommonPacket();
        }

        @Deprecated
        public CommonPacket newInstance(int entityId, double motX, double motY, double motZ) {
            return PacketPlayOutEntityVelocityHandle.createNew(entityId, motX, motY, motZ).toCommonPacket();
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

        public final FieldAccessor<Double> x = PacketPlayOutExplosionHandle.T.x.toFieldAccessor();
        public final FieldAccessor<Double> y = PacketPlayOutExplosionHandle.T.y.toFieldAccessor();
        public final FieldAccessor<Double> z = PacketPlayOutExplosionHandle.T.z.toFieldAccessor();
        public final FieldAccessor<Float> power = PacketPlayOutExplosionHandle.T.power.toFieldAccessor();
        public final FieldAccessor<List<IntVector3>> blocks = PacketPlayOutExplosionHandle.T.blocks.toFieldAccessor();
        public final FieldAccessor<Float> knockbackX = PacketPlayOutExplosionHandle.T.knockbackX.toFieldAccessor();
        public final FieldAccessor<Float> knockbackY = PacketPlayOutExplosionHandle.T.knockbackY.toFieldAccessor();
        public final FieldAccessor<Float> knockbackZ = PacketPlayOutExplosionHandle.T.knockbackZ.toFieldAccessor();

        public CommonPacket newInstance(double x, double y, double z, float radius) {
            return newInstance(x, y, z, radius, Collections.emptyList());
        }

        public CommonPacket newInstance(double x, double y, double z, float radius, List<IntVector3> blocks) {
            return newInstance(x, y, z, radius, blocks, null);
        }

        public CommonPacket newInstance(double x, double y, double z, float power, List<IntVector3> blocks, Vector knockback) {
            return PacketPlayOutExplosionHandle.createNew(x, y, z, power, blocks, knockback).toCommonPacket();
        }
    }
    
    public static class NMSPacketPlayOutGameStateChange extends NMSPacket {
        //TODO: What does it all mean???
    }

    public static class NMSPacketPlayOutHeldItemSlot extends NMSPacket {

        public final FieldAccessor<Integer> slot = getField("a", int.class);
    }

    public static class NMSPacketPlayOutKeepAlive extends NMSPacket {

        public final FieldAccessor<Long> key = FieldAccessor.wrapMethods(PacketPlayOutKeepAliveHandle.T.getKey, PacketPlayOutKeepAliveHandle.T.setKey);
    }

    public static class NMSPacketPlayOutKickDisconnect extends NMSPacket {

        public final FieldAccessor<ChatText> reason = PacketPlayOutKickDisconnectHandle.T.reason.toFieldAccessor();
    }

    public static class NMSPacketPlayOutLogin extends NMSPacket {

        public final FieldAccessor<Integer> playerId = PacketPlayOutLoginHandle.T.playerId.toFieldAccessor();
        public final FieldAccessor<Boolean> hardcore = PacketPlayOutLoginHandle.T.hardcore.toFieldAccessor();
        public final TranslatorFieldAccessor<GameMode> gameMode = PacketPlayOutLoginHandle.T.gameMode.toFieldAccessor();
        public final FieldAccessor<DimensionType> dimensionType = PacketPlayOutLoginHandle.T.dimensionType.toFieldAccessor();
        public final FieldAccessor<Integer> maxPlayers = PacketPlayOutLoginHandle.T.maxPlayers.toFieldAccessor();
        public final FieldAccessor<Boolean> reducedDebugInfo = PacketPlayOutLoginHandle.T.reducedDebugInfo.toFieldAccessor();
        public final FieldAccessor<Difficulty> difficulty = PacketPlayOutLoginHandle.T.difficulty.toFieldAccessor().ignoreInvalid(Difficulty.NORMAL);
        public final FieldAccessor<Integer> viewDistance = PacketPlayOutLoginHandle.T.viewDistance.toFieldAccessor().ignoreInvalid(10);
    }

    public static class NMSPacketPlayOutMap extends NMSPacket {

        public final FieldAccessor<Integer> mapId = PacketPlayOutMapHandle.T.mapId.toFieldAccessor();
        public final FieldAccessor<Byte> scale = PacketPlayOutMapHandle.T.scale.toFieldAccessor();
        public final FieldAccessor<List<MapCursor>> cursors = PacketPlayOutMapHandle.T.cursors.toFieldAccessor();
        public final FieldAccessor<Integer> startX = FieldAccessor.wrapMethods(PacketPlayOutMapHandle.T.getStartX, PacketPlayOutMapHandle.T.setStartX);
        public final FieldAccessor<Integer> startY = FieldAccessor.wrapMethods(PacketPlayOutMapHandle.T.getStartY, PacketPlayOutMapHandle.T.setStartY);
        public final FieldAccessor<Integer> width = FieldAccessor.wrapMethods(PacketPlayOutMapHandle.T.getWidth, PacketPlayOutMapHandle.T.setWidth);
        public final FieldAccessor<Integer> height = FieldAccessor.wrapMethods(PacketPlayOutMapHandle.T.getHeight, PacketPlayOutMapHandle.T.setHeight);
        public final FieldAccessor<byte[]> pixels = FieldAccessor.wrapMethods(PacketPlayOutMapHandle.T.getPixels, PacketPlayOutMapHandle.T.setPixels);
        public final FieldAccessor<Boolean> locked = FieldAccessor.wrapMethods(PacketPlayOutMapHandle.T.isLocked, PacketPlayOutMapHandle.T.setLocked);
        public final FieldAccessor<Boolean> track = FieldAccessor.wrapMethods(PacketPlayOutMapHandle.T.isTrack, PacketPlayOutMapHandle.T.setTrack);
    }

    public static class NMSPacketPlayOutMapChunk extends NMSPacket {

        public final FieldAccessor<Integer> x = PacketPlayOutMapChunkHandle.T.x.toFieldAccessor();
        public final FieldAccessor<Integer> z = PacketPlayOutMapChunkHandle.T.z.toFieldAccessor();
        public final FieldAccessor<CommonTagCompound> heightmaps = new SafeDirectField<CommonTagCompound>() {
            @Override
            public CommonTagCompound get(Object instance) {
                return PacketPlayOutMapChunkHandle.createHandle(instance).getHeightmaps();
            }

            @Override
            public boolean set(Object instance, CommonTagCompound value) {
                PacketPlayOutMapChunkHandle.createHandle(instance).setHeightmaps(value);
                return true;
            }
        };
        public final FieldAccessor<byte[]> buffer = new SafeDirectField<byte[]>() {
            @Override
            public byte[] get(Object instance) {
                return PacketPlayOutMapChunkHandle.createHandle(instance).getBuffer();
            }

            @Override
            public boolean set(Object instance, byte[] value) {
                PacketPlayOutMapChunkHandle.createHandle(instance).setBuffer(value);
                return true;
            }
        };
        public final FieldAccessor<List<BlockStateChange>> blockStates = new SafeDirectField<List<BlockStateChange>>() {
            @Override
            public List<BlockStateChange> get(Object instance) {
                return PacketPlayOutMapChunkHandle.createHandle(instance).getBlockStates();
            }

            @Override
            public boolean set(Object instance, List<BlockStateChange> value) {
                PacketPlayOutMapChunkHandle.createHandle(instance).setBlockStates(value);
                return true;
            }
        };
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

    // Too complicated to maintain
    // TODO: Restore this for post-1.16.2 or in general I guess
    /*
    public static class NMSPacketPlayOutMultiBlockChange extends NMSPacket {

        public final FieldAccessor<IntVector2> chunk = nextField("private net.minecraft.world.level.ChunkCoordIntPair a").translate(DuplexConversion.chunkIntPair);
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
    */

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

        public final FieldAccessor<ResourceKey<SoundEffect>> sound = PacketPlayOutNamedSoundEffectHandle.T.sound.toFieldAccessor();
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

        public final FieldAccessor<IntVector3> signPosition = PacketPlayOutOpenSignEditorHandle.T.signPosition.toFieldAccessor();
    }

    public static class NMSPacketPlayOutOpenWindow extends NMSPacket {

        public final FieldAccessor<Integer> windowId = PacketPlayOutOpenWindowHandle.T.windowId.toFieldAccessor();
        public final FieldAccessor<WindowType> windowType = new SafeDirectField<WindowType>() {
            @Override
            public WindowType get(Object instance) {
                return PacketPlayOutOpenWindowHandle.T.getWindowType.invoke(instance);
            }

            @Override
            public boolean set(Object instance, WindowType value) {
                PacketPlayOutOpenWindowHandle.T.setWindowType.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<ChatText> windowTitle = PacketPlayOutOpenWindowHandle.T.windowTitle.toFieldAccessor();
    }

    public static class NMSPacketPlayOutPlayerInfo extends NMSPacket {

        public final FieldAccessor<Object> action = PacketPlayOutPlayerInfoHandle.T.action.raw.toFieldAccessor();
        public final FieldAccessor<List<?>> playerInfoData = CommonUtil.unsafeCast(PacketPlayOutPlayerInfoHandle.T.players.raw.toFieldAccessor());
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
        public final FieldAccessor<Set<?>> teleportFlags = CommonUtil.unsafeCast(PacketPlayOutPositionHandle.T.teleportFlags.raw.toFieldAccessor());
        public final FieldAccessor<Integer> teleportWaitTimer = new SafeDirectField<Integer>() {
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
        @Deprecated
        public final FieldAccessor<Integer> unknown1 = teleportWaitTimer;
    }

    public static class NMSPacketPlayOutRemoveEntityEffect extends NMSPacket {

        public final FieldAccessor<Integer> entityId = PacketPlayOutRemoveEntityEffectHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<MobEffectListHandle> effectList = PacketPlayOutRemoveEntityEffectHandle.T.effectList.toFieldAccessor();

        public CommonPacket newInstance(int entityId, MobEffectListHandle mobEffectList) {
            return PacketPlayOutRemoveEntityEffectHandle.createNew(entityId, mobEffectList).toCommonPacket();
        }
    }

    /**
     * @deprecated Use PacketPlayOutResourcePackSendHandle instead
     */
    @Deprecated
    public static class NMSPacketPlayOutResourcePackSend extends NMSPacket {
        
        public final FieldAccessor<String> name = PacketPlayOutResourcePackSendHandle.T.url.toFieldAccessor();
        public final FieldAccessor<String> hash = PacketPlayOutResourcePackSendHandle.T.hash.toFieldAccessor();
    }

    public static class NMSPacketPlayOutRespawn extends NMSPacket {

        public final FieldAccessor<DimensionType> dimensionType = PacketPlayOutRespawnHandle.T.dimensionType.toFieldAccessor();
        public final TranslatorFieldAccessor<GameMode> gamemode = PacketPlayOutRespawnHandle.T.gamemode.toFieldAccessor();

        public final FieldAccessor<Difficulty> difficulty = new SafeDirectField<Difficulty>() {
            @Override
            public Difficulty get(Object instance) {
                if (PacketPlayOutRespawnHandle.T.difficulty.isAvailable()) {
                    return PacketPlayOutRespawnHandle.T.difficulty.get(instance);
                } else {
                    return Difficulty.NORMAL;
                }
            }

            @Override
            public boolean set(Object instance, Difficulty value) {
                if (PacketPlayOutRespawnHandle.T.difficulty.isAvailable()) {
                    PacketPlayOutRespawnHandle.T.difficulty.set(instance, value);
                    return true;
                } else {
                    return false;
                }
            }
        };
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

        public final FieldAccessor<String> name = PacketPlayOutScoreboardScoreHandle.T.name.toFieldAccessor();
        public final FieldAccessor<String> objName = PacketPlayOutScoreboardScoreHandle.T.objName.toFieldAccessor();
        public final FieldAccessor<Integer> value = PacketPlayOutScoreboardScoreHandle.T.value.toFieldAccessor();
        public final FieldAccessor<ScoreboardAction> action = PacketPlayOutScoreboardScoreHandle.T.action.toFieldAccessor();
    }

    /**
     * @deprecated Please use {@link PacketPlayOutScoreboardTeamHandle} instead
     */
    @Deprecated
    public static class NMSPacketPlayOutScoreboardTeam extends NMSPacket {

        public final FieldAccessor<Integer> method = PacketPlayOutScoreboardTeamHandle.T.method.toFieldAccessor();
        public final FieldAccessor<String> name = PacketPlayOutScoreboardTeamHandle.T.name.toFieldAccessor();
        public final FieldAccessor<ChatText> displayName = FieldAccessor.wrapMethods(PacketPlayOutScoreboardTeamHandle.T.getDisplayName, PacketPlayOutScoreboardTeamHandle.T.setDisplayName);
        public final FieldAccessor<ChatText> prefix = FieldAccessor.wrapMethods(PacketPlayOutScoreboardTeamHandle.T.getPrefix, PacketPlayOutScoreboardTeamHandle.T.setPrefix);
        public final FieldAccessor<ChatText> suffix = FieldAccessor.wrapMethods(PacketPlayOutScoreboardTeamHandle.T.getSuffix, PacketPlayOutScoreboardTeamHandle.T.setSuffix);
        public final FieldAccessor<String> visibility = FieldAccessor.wrapMethods(PacketPlayOutScoreboardTeamHandle.T.getVisibility, PacketPlayOutScoreboardTeamHandle.T.setVisibility);
        public final FieldAccessor<String> collisionRule = FieldAccessor.wrapMethods(PacketPlayOutScoreboardTeamHandle.T.getCollisionRule, PacketPlayOutScoreboardTeamHandle.T.setCollisionRule);
        public final FieldAccessor<ChatColor> color = FieldAccessor.wrapMethods(PacketPlayOutScoreboardTeamHandle.T.getColor, PacketPlayOutScoreboardTeamHandle.T.setColor);
        public final FieldAccessor<Collection<String>> players = PacketPlayOutScoreboardTeamHandle.T.players.toFieldAccessor();
        public final FieldAccessor<Integer> teamOptionFlags = FieldAccessor.wrapMethods(PacketPlayOutScoreboardTeamHandle.T.getTeamOptionFlags, PacketPlayOutScoreboardTeamHandle.T.setTeamOptionFlags);
    
        @Override
        public CommonPacket newInstance() {
            return PacketPlayOutScoreboardTeamHandle.createNew().toCommonPacket();
        }
    }

    public static class NMSPacketPlayOutServerDifficulty extends NMSPacket {
        
        public final FieldAccessor<Difficulty> difficulty = nextField("private net.minecraft.world.EnumDifficulty a").translate(DuplexConversion.difficulty);
        public final FieldAccessor<Boolean> hardcore = nextFieldSignature("private boolean b");
    }

    public static class NMSPacketPlayOutSetCooldown extends NMSPacket {

        public final FieldAccessor<Material> material = PacketPlayOutSetCooldownHandle.T.material.toFieldAccessor();
        public final FieldAccessor<Integer> cooldown = PacketPlayOutSetCooldownHandle.T.cooldown.toFieldAccessor();
    }

    public static class NMSPacketPlayOutSetSlot extends NMSPacket {

        public final FieldAccessor<Integer> windowId = PacketPlayOutSetSlotHandle.T.windowId.toFieldAccessor();
        public final FieldAccessor<Integer> slot = PacketPlayOutSetSlotHandle.T.slot.toFieldAccessor();
        public final FieldAccessor<ItemStack> item = PacketPlayOutSetSlotHandle.T.item.toFieldAccessor();
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

        public final FieldAccessor<Integer> extraData = PacketPlayOutSpawnEntityHandle.T.extraData.toFieldAccessor();

        /**
         * Deprecated: use the bukkitEntityType instead (safer)
         */
        @Deprecated
        public final FieldAccessor<Integer> entityType = new SafeDirectField<Integer>() {
            @Override
            public Integer get(Object instance) {
                return PacketPlayOutSpawnEntityHandle.createHandle(instance).getEntityTypeId();
            }

            @Override
            public boolean set(Object instance, Integer value) {
                PacketPlayOutSpawnEntityHandle.createHandle(instance).setEntityTypeId(value.intValue());
                return true;
            }
        };

        public final FieldAccessor<EntityType> bukkitEntityType = new SafeDirectField<EntityType>() {
            @Override
            public EntityType get(Object instance) {
                return PacketPlayOutSpawnEntityHandle.createHandle(instance).getEntityType();
            }

            @Override
            public boolean set(Object instance, EntityType value) {
                PacketPlayOutSpawnEntityHandle.createHandle(instance).setEntityType(value);
                return true;
            }
        };
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
        public final FieldAccessor<EntityType> entityType = new SafeDirectField<EntityType>() {

            @Override
            public EntityType get(Object instance) {
                return PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).getEntityType();
            }

            @Override
            public boolean set(Object instance, EntityType value) {
                PacketPlayOutSpawnEntityLivingHandle.createHandle(instance).setEntityType(value);
                return true;
            }
        };
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

        public CommonPacket newInstance(LivingEntity livingEntity) {
            return PacketPlayOutSpawnEntityLivingHandle.createNew(livingEntity).toCommonPacket();
        }

        @Override
        protected boolean matchPacket(Object packetHandle) {
            //TODO: Do we actually go through the effort of checking what entity types are for living entities?
            //      It's not really worth it. Just don't fire events for this type of entity anymore.
            return !CommonCapabilities.ENTITY_SPAWN_PACKETS_MERGED;
        }
    }

    public static class NMSPacketPlayOutSpawnEntityPainting extends NMSPacket {

        public final FieldAccessor<Integer> entityId = PacketPlayOutSpawnEntityPaintingHandle.T.entityId.toFieldAccessor();

        @Override
        protected boolean matchPacket(Object packetHandle) {
            //TODO: Do we actually go through the effort of checking what entity types are for paintings?
            //      It's not really worth it. Just don't fire events for this type of entity anymore.
            return !CommonCapabilities.ENTITY_SPAWN_PACKETS_MERGED;
        }
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

    /**
     * @deprecated Use {@link PacketPlayOutSpawnPositionHandle} instead
     */
    @Deprecated
    public static class NMSPacketPlayOutSpawnPosition extends NMSPacket {

        public final TranslatorFieldAccessor<IntVector3> position = PacketPlayOutSpawnPositionHandle.T.position.toFieldAccessor();
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

        public final TranslatorFieldAccessor<IntVector3> position = PacketPlayOutTileEntityDataHandle.T.position.toFieldAccessor();
        public final FieldAccessor<BlockStateType> type = PacketPlayOutTileEntityDataHandle.T.type.toFieldAccessor();
        public final FieldAccessor<CommonTagCompound> data = PacketPlayOutTileEntityDataHandle.T.data.toFieldAccessor();

        public CommonPacket newInstance(IntVector3 blockPosition, BlockStateType type, CommonTagCompound data) {
            return PacketPlayOutTileEntityDataHandle.createNew(blockPosition, type, data).toCommonPacket();
        }
    }

    /*
    public static class NMSPacketPlayOutTitle extends NMSPacket {
        public final FieldAccessor<PacketPlayOutTitleHandle.EnumTitleActionHandle> action = PacketPlayOutTitleHandle.T.action.toFieldAccessor();
        public final FieldAccessor<ChatText> chatComponent = PacketPlayOutTitleHandle.T.title.toFieldAccessor();
        public final FieldAccessor<Integer> fadeIn = PacketPlayOutTitleHandle.T.fadeIn.toFieldAccessor();
        public final FieldAccessor<Integer> stay = PacketPlayOutTitleHandle.T.stay.toFieldAccessor();
        public final FieldAccessor<Integer> fadeOut = PacketPlayOutTitleHandle.T.fadeOut.toFieldAccessor();

        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(EnumTitleActionHandle.T.getType(), IChatBaseComponentHandle.T.getType(), int.class, int.class, int.class);

        public CommonPacket newInstance(int fadeIn, int stay, int fadeOut) {
            return constructor1.newInstance(EnumTitleActionHandle.TIMES.getRaw(), null, fadeIn, stay, fadeOut);
        }

        public CommonPacket newInstance(EnumTitleActionHandle enumTitleAction, ChatText title) {
            return constructor1.newInstance(enumTitleAction.getRaw(), title.getRawHandle(), -1, -1, -1);
        }

        public CommonPacket newInstance(EnumTitleActionHandle enumTitleAction, ChatText title, int fadeIn, int stay, int fadeOut) {
            return constructor1.newInstance(enumTitleAction.getRaw(), title.getRawHandle(), fadeIn, stay, fadeOut);
        }

        @Deprecated
        public CommonPacket newInstance(EnumTitleActionHandle enumTitleAction, IChatBaseComponentHandle iChatBaseComponent) {
            return constructor1.newInstance(enumTitleAction.getRaw(), iChatBaseComponent.getRaw(), -1, -1, -1);
        }

        @Deprecated
        public CommonPacket newInstance(EnumTitleActionHandle enumTitleAction, IChatBaseComponentHandle iChatBaseComponent, int fadeIn, int stay, int fadeOut) {
            return constructor1.newInstance(enumTitleAction.getRaw(), iChatBaseComponent.getRaw(), fadeIn, stay, fadeOut);
        }
    }
    */

    public static class NMSPacketPlayOutUnloadChunk extends NMSPacket {

        public final FieldAccessor<Integer> x = PacketPlayOutUnloadChunkHandle.T.cx.toFieldAccessor();
        public final FieldAccessor<Integer> z = PacketPlayOutUnloadChunkHandle.T.cz.toFieldAccessor();
    }

    public static class NMSPacketPlayOutUpdateAttributes extends NMSPacket {

        /**
         * A list of NMS.Attribute elements - may require further API to work
         * with. For now, use reflection.
         */
        public final FieldAccessor<Integer> entityId = PacketPlayOutUpdateAttributesHandle.T.entityId.toFieldAccessor();

        public CommonPacket newInstance(int entityId, Collection<AttributeModifiableHandle> attributes) {
            return PacketPlayOutUpdateAttributesHandle.createNew(entityId, attributes).toCommonPacket();
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

    // Removed since MC 1.17, there is little use for this packet so I'm getting rid of it.
    /*
    public static class NMSPacketPlayOutWorldBorder extends NMSPacket {

        public final FieldAccessor<Object> action = nextField("private PacketPlayOutWorldBorder.EnumWorldBorderAction a");
        public final FieldAccessor<Integer> b = nextFieldSignature("private int b");
        public final FieldAccessor<Double> cx = nextFieldSignature("private double c");
        public final FieldAccessor<Double> cz = nextFieldSignature("private double d");
        public final FieldAccessor<Double> e = nextFieldSignature("private double e");
        public final FieldAccessor<Double> size = nextFieldSignature("private double f");
        public final FieldAccessor<Long> g = nextFieldSignature("private long g");
        public final FieldAccessor<Integer> warningTime = nextFieldSignature("private int h");
        public final FieldAccessor<Integer> warningDistance = nextFieldSignature("private int i");
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(
                CommonUtil.getClass("net.minecraft.world.level.border.WorldBorder"),
                CommonUtil.getClass("net.minecraft.network.protocol.game.PacketPlayOutWorldBorder.EnumWorldBorderAction"));

        public CommonPacket newInstance (Object nmsWorldBorder, Object nmsEnumBorderAction) {
            return constructor1.newInstance(nmsWorldBorder, nmsEnumBorderAction);
        }
    }
    */

    public static class NMSPacketPlayOutWorldEvent extends NMSPacket {

        public final FieldAccessor<Integer> effectId = PacketPlayOutWorldEventHandle.T.effectId.toFieldAccessor();
        public final TranslatorFieldAccessor<IntVector3> position = PacketPlayOutWorldEventHandle.T.position.toFieldAccessor();
        public final FieldAccessor<Integer> data = PacketPlayOutWorldEventHandle.T.data.toFieldAccessor();
        public final FieldAccessor<Boolean> noRelativeVolume = PacketPlayOutWorldEventHandle.T.globalEvent.toFieldAccessor();
    }

    public static class NMSPacketPlayOutWorldParticles extends NMSPacket {

        public final FieldAccessor<Double> x = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutWorldParticlesHandle.T.getPosX.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutWorldParticlesHandle.T.setPosX.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Double> y = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutWorldParticlesHandle.T.getPosY.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutWorldParticlesHandle.T.setPosY.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Double> z = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return PacketPlayOutWorldParticlesHandle.T.getPosZ.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Double value) {
                PacketPlayOutWorldParticlesHandle.T.setPosZ.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Float> randomX = PacketPlayOutWorldParticlesHandle.T.randomX.toFieldAccessor();
        public final FieldAccessor<Float> randomY = PacketPlayOutWorldParticlesHandle.T.randomY.toFieldAccessor();
        public final FieldAccessor<Float> randomZ = PacketPlayOutWorldParticlesHandle.T.randomZ.toFieldAccessor();
        public final FieldAccessor<Float> speed = PacketPlayOutWorldParticlesHandle.T.speed.toFieldAccessor();
        public final FieldAccessor<Integer> particleCount = PacketPlayOutWorldParticlesHandle.T.count.toFieldAccessor();
        public final FieldAccessor<Boolean> overrideLimiter = PacketPlayOutWorldParticlesHandle.T.overrideLimiter.toFieldAccessor();

        @Override
        public CommonPacket newInstance() {
            return new CommonPacket(PacketPlayOutWorldParticlesHandle.T.createNew.raw.invoke(), this);
        }
    }

    //////////////////////////////////////////////////////////////////////

    public static class NMSPacketPlayOutUpdateSign extends NMSPacket {

        public final FieldAccessor<World> world = PacketPlayOutUpdateSignHandle.T.world.toFieldAccessor();
        public final FieldAccessor<IntVector3> position = PacketPlayOutUpdateSignHandle.T.position.toFieldAccessor();
        public final FieldAccessor<ChatText[]> lines = PacketPlayOutUpdateSignHandle.T.lines.toFieldAccessor();
    }

}
