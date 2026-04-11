package com.bergerkiller.reflection.net.minecraft.server;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeInstanceHandle;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Conversion;
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
import com.bergerkiller.bukkit.common.wrappers.WindowType;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.*;
import com.bergerkiller.generated.net.minecraft.network.protocol.common.*;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveEntityPacketHandle.PosHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveEntityPacketHandle.PosRotHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectInstanceHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
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

                MethodDeclaration mDec = new MethodDeclaration(resolver, SourceDeclaration.preprocess("" +
                        "public static CommonPacket newInstance() {\n" +
                        "#if exists " + this.getType().getName() + " private " + this.getType().getSimpleName() + "();\n" +
                        "    #require " + this.getType().getName() + " private " + this.getType().getSimpleName() + " createPacket:<init>()\n" +
                        "    Object packet = #createPacket();\n" +
                        "#elseif exists " + this.getType().getName() + " private " + this.getType().getSimpleName() + " (net.minecraft.network.FriendlyByteBuf serializer)\n" +
                        "    #require " + this.getType().getName() + " private " + this.getType().getSimpleName() + " createPacket:<init>(net.minecraft.network.FriendlyByteBuf serializer)\n" +
                        "    Object packet = #createPacket(com.bergerkiller.bukkit.common.internal.logic.NullPacketDataSerializer.INSTANCE);\n" +
                        "#elseif exists " + this.getType().getName() + " private " + this.getType().getSimpleName() + " (net.minecraft.network.RegistryFriendlyByteBuf byteBuf)\n" +
                        "    #require " + this.getType().getName() + " private " + this.getType().getSimpleName() + " createPacket:<init>(net.minecraft.network.RegistryFriendlyByteBuf byteBuf)\n" +
                        "    Object packet = #createPacket(com.bergerkiller.bukkit.common.internal.logic.NullPacketDataSerializer.INSTANCE);\n" +
                        "#else\n" +
                        "    #error No " + this.getType().getName() + " packet constructor found\n" +
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

    public static class NMSServerboundPlayerAbilitiesPacket extends NMSPacket {
        //TODO: Only has 'isFlying' property since 1.16
        //Do we care about the other fields for past versions?
        public final FieldAccessor<Boolean> isFlying = ServerboundPlayerAbilitiesPacketHandle.T.isFlying.toFieldAccessor();
    }

    public static class NMSServerboundSwingPacket extends NMSPacket {

        /**
         * Sets the hand that is animated
         *
         * @param packet to write to
         * @param humanEntity used for translating the hand from MAIN/OFF to LEFT/RIGHT, can be null
         * @param humanHand to set to
         */
        public final void setHand(CommonPacket packet, HumanEntity humanEntity, HumanHand humanHand) {
            ServerboundSwingPacketHandle.createHandle(packet.getHandle()).setHand(humanEntity, humanHand);
        }

        /**
         * Gets the hand that is animated
         *
         * @param packet to read from
         * @param humanEntity used for translating the hand from MAIN/OFF to LEFT/RIGHT, can be null
         * @return humanHand
         */
        public final HumanHand getHand(CommonPacket packet, HumanEntity humanEntity) {
            return ServerboundSwingPacketHandle.createHandle(packet.getHandle()).getHand(humanEntity);
        }
    }

    /**
     * @deprecated Use {@link ServerboundPlayerActionPacketHandle} instead
     */
    @Deprecated
    public static class NMSServerboundPlayerActionPacket extends NMSPacket {

        public final FieldAccessor<IntVector3> position = ServerboundPlayerActionPacketHandle.T.position.toFieldAccessor();
        public final FieldAccessor<BlockFace> direction = ServerboundPlayerActionPacketHandle.T.direction.toFieldAccessor();
        public final FieldAccessor<ServerboundPlayerActionPacketHandle.ActionHandle> status = ServerboundPlayerActionPacketHandle.T.digType.toFieldAccessor();
    }

    public static class NMSServerboundUseItemPacket extends NMSPacket {

        public final FieldAccessor<Long> timestamp = ServerboundUseItemPacketHandle.T.timestamp.toFieldAccessor().ignoreInvalid(0L);

        @Override
        protected boolean matchPacket(Object packetHandle) {
            if (CommonCapabilities.PLACE_PACKETS_MERGED) {
                return ServerboundUseItemOnPacketHandle.T.isBlockPlacePacket.invoke(packetHandle).booleanValue();
            } else {
                return true;
            }
        }

        @Override
        public void preprocess(Object packetHandle) {
            if (CommonCapabilities.PLACE_PACKETS_MERGED) {
                ServerboundUseItemOnPacketHandle.T.setBlockPlacePacket.invoke(packetHandle);
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
            ServerboundUseItemPacketHandle.createHandle(packet.getHandle()).setHand(humanEntity, humanHand);
        }

        /**
         * Gets the hand that placed the block
         *
         * @param packet to read from
         * @param humanEntity used for translating the hand from MAIN/OFF to LEFT/RIGHT, can be null
         * @return humanHand
         */
        public final HumanHand getHand(CommonPacket packet, HumanEntity humanEntity) {
            return ServerboundUseItemPacketHandle.createHandle(packet.getHandle()).getHand(humanEntity);
        }
    }

    public static class NMSServerboundUseItemOnPacket extends NMSPacket {

        public final FieldAccessor<IntVector3> position = new SafeDirectField<IntVector3>() {
            @Override
            public IntVector3 get(Object instance) {
                return ServerboundUseItemOnPacketHandle.T.getPosition.invoke(instance);
            }

            @Override
            public boolean set(Object instance, IntVector3 value) {
                ServerboundUseItemOnPacketHandle.T.setPosition.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<BlockFace> direction = new SafeDirectField<BlockFace>() {
            @Override
            public BlockFace get(Object instance) {
                return ServerboundUseItemOnPacketHandle.T.getDirection.invoke(instance);
            }

            @Override
            public boolean set(Object instance, BlockFace value) {
                ServerboundUseItemOnPacketHandle.T.setDirection.invoke(instance, value);
                return true;
            }
        };


        public final FieldAccessor<Float> deltaX = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return ServerboundUseItemOnPacketHandle.T.getDeltaX.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                ServerboundUseItemOnPacketHandle.T.setDeltaX.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Float> deltaY = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return ServerboundUseItemOnPacketHandle.T.getDeltaY.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                ServerboundUseItemOnPacketHandle.T.setDeltaY.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Float> deltaZ = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return ServerboundUseItemOnPacketHandle.T.getDeltaZ.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                ServerboundUseItemOnPacketHandle.T.setDeltaZ.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Long> timestamp = ServerboundUseItemOnPacketHandle.T.timestamp.toFieldAccessor().ignoreInvalid(0L);

        @Override
        protected boolean matchPacket(Object packetHandle) {
            if (CommonCapabilities.PLACE_PACKETS_MERGED) {
                return !ServerboundUseItemOnPacketHandle.T.isBlockPlacePacket.invoke(packetHandle).booleanValue();
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
            ServerboundUseItemOnPacketHandle.createHandle(packet.getHandle()).setHand(humanEntity, humanHand);
        }

        /**
         * Gets the hand that used the item
         *
         * @param packet to read from
         * @param humanEntity used for translating the hand from MAIN/OFF to LEFT/RIGHT, can be null
         * @return humanHand
         */
        public final HumanHand getHand(CommonPacket packet, HumanEntity humanEntity) {
            return ServerboundUseItemOnPacketHandle.createHandle(packet.getHandle()).getHand(humanEntity);
        }
    }

    public static class NMSServerboundPaddleBoatPacket extends NMSPacket {

        public final FieldAccessor<Boolean> leftPaddle = ServerboundPaddleBoatPacketHandle.T.leftPaddle.toFieldAccessor();
        public final FieldAccessor<Boolean> rightPaddle = ServerboundPaddleBoatPacketHandle.T.rightPaddle.toFieldAccessor();
    }

    public static class NMSServerboundChatPacket extends NMSPacket {

        public final FieldAccessor<String> message = ServerboundChatPacketHandle.T.message.toFieldAccessor();
    }

    public static class NMSServerboundClientCommandPacket extends NMSPacket {

        public final FieldAccessor<Object> command = ServerboundClientCommandPacketHandle.T.action.toFieldAccessor();
    }

    public static class NMSServerboundContainerClosePacket extends NMSPacket {

        public final FieldAccessor<Integer> windowId = ServerboundContainerClosePacketHandle.T.windowId.toFieldAccessor();
    }

    public static class NMSServerboundCustomPayloadPacket extends NMSPacket {

        // public final FieldAccessor<String> tag = nextField("private String a");
        // public final FieldAccessor<Object> data = nextFieldSignature("private PacketDataSerializer b");
    }

    public static class NMSServerboundContainerButtonClickPacket extends NMSPacket {

        public final FieldAccessor<Integer> windowId = ServerboundContainerButtonClickPacketHandle.T.windowId.toFieldAccessor();
        public final FieldAccessor<Integer> buttonId = ServerboundContainerButtonClickPacketHandle.T.buttonId.toFieldAccessor();
    }

    public static class NMSServerboundPlayerCommandPacket extends NMSPacket {

        public final FieldAccessor<Integer> playerId = ServerboundPlayerCommandPacketHandle.T.playerId.toFieldAccessor();
        public final FieldAccessor<Object> action = ServerboundPlayerCommandPacketHandle.T.action.toFieldAccessor();
        public final FieldAccessor<Integer> jumpBoost = ServerboundPlayerCommandPacketHandle.T.data.toFieldAccessor();
    }

    public static class NMSServerboundMovePlayerPacket extends NMSPacket {

        protected NMSServerboundMovePlayerPacket(String subType) {
            super(CommonUtil.getClass("net.minecraft.network.protocol.game.ServerboundMovePlayerPacket." + subType));
        }

        public final FieldAccessor<Double> x = ServerboundMovePlayerPacketHandle.T.x.toFieldAccessor();
        public final FieldAccessor<Double> y = ServerboundMovePlayerPacketHandle.T.y.toFieldAccessor();
        public final FieldAccessor<Double> z = ServerboundMovePlayerPacketHandle.T.z.toFieldAccessor();
        public final FieldAccessor<Float> yaw = ServerboundMovePlayerPacketHandle.T.yaw.toFieldAccessor();
        public final FieldAccessor<Float> pitch = ServerboundMovePlayerPacketHandle.T.pitch.toFieldAccessor();
        public final FieldAccessor<Boolean> onGround = ServerboundMovePlayerPacketHandle.T.onGround.toFieldAccessor();
        public final FieldAccessor<Boolean> hasPos = ServerboundMovePlayerPacketHandle.T.hasPos.toFieldAccessor();
        public final FieldAccessor<Boolean> hasLook = ServerboundMovePlayerPacketHandle.T.hasLook.toFieldAccessor();
    }

    public static class NMSServerboundMovePlayerPacketRot extends NMSServerboundMovePlayerPacket {
        public NMSServerboundMovePlayerPacketRot() {
            super("Rot");
        }
    }

    public static class NMSServerboundMovePlayerPacketPos extends NMSServerboundMovePlayerPacket {
        public NMSServerboundMovePlayerPacketPos() {
            super("Pos");
        }
    }

    public static class NMSServerboundMovePlayerPacketPosRot extends NMSServerboundMovePlayerPacket {
        public NMSServerboundMovePlayerPacketPosRot() {
            super("PosRot");
        }
    }

    public static class NMSServerboundSetCarriedItemPacket extends NMSPacket {

        public final FieldAccessor<Integer> slot = ServerboundSetCarriedItemPacketHandle.T.itemInHandIndex.toFieldAccessor();
    }

    public static class NMSServerboundKeepAlivePacket extends NMSPacket {

        public final FieldAccessor<Long> key = new SafeDirectField<Long>() {
            @Override
            public Long get(Object instance) {
                return Long.valueOf(ServerboundKeepAlivePacketHandle.createHandle(instance).getKey());
            }

            @Override
            public boolean set(Object instance, Long value) {
                ServerboundKeepAlivePacketHandle.createHandle(instance).setKey(value.longValue());
                return true;
            }
        };
    }

    public static class NMSServerboundResourcePackPacket extends NMSPacket {

        public final FieldAccessor<Object> enumStatus = ServerboundResourcePackPacketHandle.T.status.toFieldAccessor();
    }

    public static class NMSServerboundSetCreativeModeSlotPacket extends NMSPacket {
    }

    public static class NMSServerboundClientInformationPacket extends NMSPacket {

        public final FieldAccessor<String> locale = new SafeDirectField<String>() {
            @Override
            public String get(Object instance) {
                return ServerboundClientInformationPacketHandle.T.getLocale.invoke(instance);
            }

            @Override
            public boolean set(Object instance, String value) {
                return false;
            }
        };
        public final FieldAccessor<Integer> view = new SafeDirectField<Integer>() {
            @Override
            public Integer get(Object instance) {
                return ServerboundClientInformationPacketHandle.T.getView.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Integer value) {
                return false;
            }
        };
        public final FieldAccessor<Object> chatVisibility = new SafeDirectField<Object>() {
            @Override
            public Object get(Object instance) {
                return ServerboundClientInformationPacketHandle.T.getChatVisibility.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Object value) {
                return false;
            }
        };
        public final FieldAccessor<Boolean> enableColors = new SafeDirectField<Boolean>() {
            @Override
            public Boolean get(Object instance) {
                return ServerboundClientInformationPacketHandle.T.getEnableColors.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Boolean value) {
                return false;
            }
        };
        public final FieldAccessor<Integer> modelPartFlags = new SafeDirectField<Integer>() {
            @Override
            public Integer get(Object instance) {
                return ServerboundClientInformationPacketHandle.T.getModelPartFlags.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Integer value) {
                return false;
            }
        };
        public final FieldAccessor<HumanHand> mainHand = new SafeDirectField<HumanHand>() {
            @Override
            public HumanHand get(Object instance) {
                return ServerboundClientInformationPacketHandle.T.getMainHand.invoke(instance);
            }

            @Override
            public boolean set(Object instance, HumanHand value) {
                return false;
            }
        };
    }

    public static class NMSServerboundTeleportToEntityPacket extends NMSPacket {

        public final FieldAccessor<UUID> uuid = ServerboundTeleportToEntityPacketHandle.T.uuid.toFieldAccessor();

        public CommonPacket newInstance(UUID uuid) {
            return ServerboundTeleportToEntityPacketHandle.createNew(uuid).toCommonPacket();
        }
    }

    public static class NMSServerboundPlayerInputPacket extends NMSPacket {

        public final FieldAccessor<Float> sideways = new FieldAccessor<Float>() {
            @Override
            public Float get(Object instance) {
                return ServerboundPlayerInputPacketHandle.T.getSideways.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                return false;
            }
        };
        public final FieldAccessor<Float> forwards = new FieldAccessor<Float>() {
            @Override
            public Float get(Object instance) {
                return ServerboundPlayerInputPacketHandle.T.getForwards.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                return false;
            }
        };
        public final FieldAccessor<Boolean> jump = new FieldAccessor<Boolean>() {
            @Override
            public Boolean get(Object instance) {
                return ServerboundPlayerInputPacketHandle.T.isJump.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Boolean value) {
                return false;
            }
        };
        public final FieldAccessor<Boolean> unmount = new FieldAccessor<Boolean>() {
            @Override
            public Boolean get(Object instance) {
                return ServerboundPlayerInputPacketHandle.T.isUnmount.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Boolean value) {
                return false;
            }
        };
    }

    public static class NMSServerboundCommandSuggestionPacket extends NMSPacket {

        // public final FieldAccessor<String> text = PacketPlayInTabCompleteHandle.T.text.toFieldAccessor();
        // public final FieldAccessor<Boolean> assumeCommand = PacketPlayInTabCompleteHandle.T.assumeCommand.toFieldAccessor().ignoreInvalid(false);
        // public final FieldAccessor<IntVector3> position = PacketPlayInTabCompleteHandle.T.position.toFieldAccessor();
    }

    public static class NMSServerboundAcceptTeleportationPacket extends NMSPacket {

        public final FieldAccessor<Integer> teleportId = ServerboundAcceptTeleportationPacketHandle.T.teleportId.toFieldAccessor();
    }

    public static class NMSServerboundSignUpdatePacket extends NMSPacket {

        public final FieldAccessor<IntVector3> position = ServerboundSignUpdatePacketHandle.T.position.toFieldAccessor();
        public final FieldAccessor<ChatText[]> lines = ServerboundSignUpdatePacketHandle.T.lines.toFieldAccessor();

        public Block getBlock(CommonPacket packet, World world) {
            return BlockUtil.getBlock(world, position.get(packet.getHandle()));
        }

        public void setBlock(CommonPacket packet, Block block) {
            position.set(packet.getHandle(), new IntVector3(block));
        }
    }

    /**
     * @deprecated Please use ServerboundInteractPacketHandle instead for complete api support
     */
    @Deprecated
    public static class NMSServerboundInteractPacket extends NMSPacket {
        public final FieldAccessor<Integer> clickedEntityId = new SafeDirectField<Integer>() {
            @Override
            public Integer get(Object instance) {
                return ServerboundInteractPacketHandle.createHandle(instance).getUsedEntityId();
            }

            @Override
            public boolean set(Object instance, Integer value) {
                return false;
            }
        };

        /**
         * Gets the hand that interacted with the entity
         *
         * @param packet to read from
         * @param humanEntity used for translating the hand from MAIN/OFF to LEFT/RIGHT, can be null
         * @return humanHand
         */
        public final HumanHand getHand(CommonPacket packet, HumanEntity humanEntity) {
            return ServerboundInteractPacketHandle.createHandle(packet.getHandle()).getHand(humanEntity);
        }

        @Override
        public boolean matchPacket(Object packetHandle) {
            if (CommonCapabilities.INTERACT_PACKET_ATTACK_SPLIT) {
                return true;
            } else {
                return !ServerboundAttackPacketHandle.isAttackInteractionPacket(packetHandle);
            }
        }
    }

    public static class NMSServerboundAttackPacket extends NMSPacket {
        @Override
        public boolean matchPacket(Object packetHandle) {
            if (CommonCapabilities.INTERACT_PACKET_ATTACK_SPLIT) {
                return true;
            } else {
                return ServerboundAttackPacketHandle.isAttackInteractionPacket(packetHandle);
            }
        }
    }

    public static class NMSServerboundMoveVehiclePacket extends NMSPacket {

        public final FieldAccessor<Double> posX = new SafeDirectField<Double>() {
            @Override
            public Double get(Object o) {
                return ServerboundMoveVehiclePacketHandle.T.getPosX.invoke(o);
            }

            @Override
            public boolean set(Object o, Double aDouble) {
                return false;
            }
        };
        public final FieldAccessor<Double> posY = new SafeDirectField<Double>() {
            @Override
            public Double get(Object o) {
                return ServerboundMoveVehiclePacketHandle.T.getPosY.invoke(o);
            }

            @Override
            public boolean set(Object o, Double aDouble) {
                return false;
            }
        };
        public final FieldAccessor<Double> posZ = new SafeDirectField<Double>() {
            @Override
            public Double get(Object o) {
                return ServerboundMoveVehiclePacketHandle.T.getPosZ.invoke(o);
            }

            @Override
            public boolean set(Object o, Double aDouble) {
                return false;
            }
        };
        public final FieldAccessor<Float> yaw = new SafeDirectField<Float>() {
            @Override
            public Float get(Object o) {
                return ServerboundMoveVehiclePacketHandle.T.getYaw.invoke(o);
            }

            @Override
            public boolean set(Object o, Float aFloat) {
                return false;
            }
        };
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object o) {
                return ServerboundMoveVehiclePacketHandle.T.getPitch.invoke(o);
            }

            @Override
            public boolean set(Object o, Float aFloat) {
                return false;
            }
        };
        public final FieldAccessor<Boolean> onGround = new SafeDirectField<Boolean>() {
            @Override
            public Boolean get(Object o) {
                return ServerboundMoveVehiclePacketHandle.T.isOnGround.invoke(o);
            }

            @Override
            public boolean set(Object o, Boolean aBool) {
                return false;
            }
        };

        public CommonPacket newInstance(double posX, double posY, double posZ, float yaw, float pitch, boolean onGround) {
            return ServerboundMoveVehiclePacketHandle.createNew(posX, posY, posZ, yaw, pitch, onGround).toCommonPacket();
        }
    }

    public static class NMSServerboundContainerClickPacket extends NMSPacket {

        public final FieldAccessor<Integer> windowId = ServerboundContainerClickPacketHandle.T.windowId.toFieldAccessor();
        public final FieldAccessor<Short> slot = ServerboundContainerClickPacketHandle.T.slot.toFieldAccessor();
        public final FieldAccessor<Byte> button = ServerboundContainerClickPacketHandle.T.button.toFieldAccessor();
        //public final FieldAccessor<ItemStack> item = ServerboundContainerClickPacketHandle.T.item.toFieldAccessor();
        public final FieldAccessor<InventoryClickType> mode = ServerboundContainerClickPacketHandle.T.mode.toFieldAccessor();
    }

    /*
     * ========================================================================================
     * ============================= Outgoing packets start ===================================
     * ========================================================================================
     */

    public static class NMSClientboundPlayerAbilitiesPacket extends NMSPacket {

        public final FieldAccessor<Boolean> isInvulnerable = ClientboundPlayerAbilitiesPacketHandle.T.invulnerable.toFieldAccessor();
        public final FieldAccessor<Boolean> isFlying = ClientboundPlayerAbilitiesPacketHandle.T.isFlying.toFieldAccessor();
        public final FieldAccessor<Boolean> canFly = ClientboundPlayerAbilitiesPacketHandle.T.canFly.toFieldAccessor();
        public final FieldAccessor<Boolean> canInstantlyBuild = ClientboundPlayerAbilitiesPacketHandle.T.instabuild.toFieldAccessor();
        public final FieldAccessor<Float> flySpeed = ClientboundPlayerAbilitiesPacketHandle.T.flyingSpeed.toFieldAccessor();
        public final FieldAccessor<Float> walkSpeed = ClientboundPlayerAbilitiesPacketHandle.T.walkingSpeed.toFieldAccessor();

        public CommonPacket newInstance(PlayerAbilities abilities) {
            return ClientboundPlayerAbilitiesPacketHandle.createNew(abilities).toCommonPacket();
        }
    }

    public static class NMSClientboundUpdateAdvancementsPacket extends NMSPacket {

        public final FieldAccessor<Boolean> initial = ClientboundUpdateAdvancementsPacketHandle.T.initial.toFieldAccessor();
        //TODO: Fields
    }

    public static class NMSClientboundAnimatePacket extends NMSPacket {

        public final FieldAccessor<Integer> entityId = ClientboundAnimatePacketHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<Integer> animation = ClientboundAnimatePacketHandle.T.action.toFieldAccessor();
    }

    /*
     * Note: is used exclusvely to attach a leash to an entity
     * It no longer implies anything about vehicles or passengers
     * The mount packet is for that instead
     */
    public static class NMSClientboundSetEntityLinkPacket extends NMSPacket {

        public final FieldAccessor<Integer> vehicleId = ClientboundSetEntityLinkPacketHandle.T.vehicleId.toFieldAccessor();
        public final FieldAccessor<Integer> passengerId = ClientboundSetEntityLinkPacketHandle.T.passengerId.toFieldAccessor();

        public CommonPacket newInstanceMount(org.bukkit.entity.Entity passenger, org.bukkit.entity.Entity vehicle) {
            return new CommonPacket(ClientboundSetEntityLinkPacketHandle.createNewMount(passenger, vehicle).getRaw());
        }

        public CommonPacket newInstanceLeash(org.bukkit.entity.Entity leashedEntity, org.bukkit.entity.Entity holderEntity) {
            return new CommonPacket(ClientboundSetEntityLinkPacketHandle.createNewLeash(leashedEntity, holderEntity).getRaw());
        }
    }

    public static class NMSClientboundBlockEventPacket extends NMSPacket {

        public final TranslatorFieldAccessor<IntVector3> position = ClientboundBlockEventPacketHandle.T.position.toFieldAccessor();
        public final FieldAccessor<Integer> b0 = ClientboundBlockEventPacketHandle.T.b0.toFieldAccessor();
        public final FieldAccessor<Integer> b1 = ClientboundBlockEventPacketHandle.T.b1.toFieldAccessor();
        public final FieldAccessor<Material> block = ClientboundBlockEventPacketHandle.T.block.toFieldAccessor();
    }

    public static class NMSClientboundBlockDestructionPacket extends NMSPacket {

        public final FieldAccessor<Integer> id = ClientboundBlockDestructionPacketHandle.T.id.toFieldAccessor();
        public final TranslatorFieldAccessor<IntVector3> position = ClientboundBlockDestructionPacketHandle.T.position.toFieldAccessor();
        public final FieldAccessor<Integer> progress = ClientboundBlockDestructionPacketHandle.T.progress.toFieldAccessor();
    }

    public static class NMSClientboundBlockUpdatePacket extends NMSPacket {

        public final TranslatorFieldAccessor<IntVector3> position = ClientboundBlockUpdatePacketHandle.T.position.toFieldAccessor();
        public final TranslatorFieldAccessor<BlockData> blockData = ClientboundBlockUpdatePacketHandle.T.blockData.toFieldAccessor();

        @Override
        public CommonPacket newInstance() {
            return ClientboundBlockUpdatePacketHandle.createNewNull().toCommonPacket();
        }

        public CommonPacket newInstance(IntVector3 position, BlockData blockData) {
            return ClientboundBlockUpdatePacketHandle.createNew(position, blockData).toCommonPacket();
        }
    }

    @Deprecated
    public static class NMSClientboundBossEventPacket extends NMSPacket {
    }

    public static class NMSClientboundSetCameraPacket extends NMSPacket {

        public final FieldAccessor<Integer> entityId = ClientboundSetCameraPacketHandle.T.entityId.toFieldAccessor();
    }

    public static class NMSClientboundContainerClosePacket extends NMSPacket {

        public final FieldAccessor<Integer> windowId = ClientboundContainerClosePacketHandle.T.windowId.toFieldAccessor();
    }

    public static class NMSClientboundTakeItemEntityPacket extends NMSPacket {

        public final FieldAccessor<Integer> collectedItemId = ClientboundTakeItemEntityPacketHandle.T.collectedItemId.toFieldAccessor();
        public final FieldAccessor<Integer> collectorEntityId = ClientboundTakeItemEntityPacketHandle.T.collectorEntityId.toFieldAccessor();
        public final FieldAccessor<Integer> amount;

        public NMSClientboundTakeItemEntityPacket() {
            if (ClientboundTakeItemEntityPacketHandle.T.amount.isAvailable()) {
                this.amount = ClientboundTakeItemEntityPacketHandle.T.amount.toFieldAccessor();
            } else {
                this.amount = null;
            }
        }
    }

    public static class NMSClientboundCustomPayloadPacket extends NMSPacket {

        public static final FieldAccessor<String> channel = new SafeDirectField<String>() {
            @Override
            public String get(Object instance) {
                return ClientboundCustomPayloadPacketHandle.createHandle(instance).getChannel();
            }

            @Override
            public boolean set(Object instance, String value) {
                return false;
            }
        };

        public static final FieldAccessor<byte[]> message = new SafeDirectField<byte[]>() {
            @Override
            public byte[] get(Object instance) {
                return ClientboundCustomPayloadPacketHandle.createHandle(instance).getMessage();
            }

            @Override
            public boolean set(Object instance, byte[] value) {
                return false;
            }
        };

        public static CommonPacket createNew(String channel, byte[] message) {
            return ClientboundCustomPayloadPacketHandle.createNew(channel, message).toCommonPacket();
        }
    }

    public static class NMSClientboundCustomSoundPacket extends NMSPacket {

        public final FieldAccessor<ResourceKey<SoundEffect>> sound = ClientboundCustomSoundPacketHandle.T.sound.toFieldAccessor();
        public final FieldAccessor<String> category = new SafeDirectField<String>() {
            @Override
            public String get(Object instance) {
                return ClientboundCustomSoundPacketHandle.createHandle(instance).getCategory();
            }

            @Override
            public boolean set(Object instance, String value) {
                ClientboundCustomSoundPacketHandle.createHandle(instance).setCategory(value);
                return false;
            }
        };
        public final FieldAccessor<Double> x = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return Double.valueOf(ClientboundCustomSoundPacketHandle.createHandle(instance).getX());
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundCustomSoundPacketHandle.createHandle(instance).setX(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Double> y = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return Double.valueOf(ClientboundCustomSoundPacketHandle.createHandle(instance).getY());
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundCustomSoundPacketHandle.createHandle(instance).setY(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Double> z = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return Double.valueOf(ClientboundCustomSoundPacketHandle.createHandle(instance).getZ());
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundCustomSoundPacketHandle.createHandle(instance).setZ(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Float> volume = ClientboundCustomSoundPacketHandle.T.volume.toFieldAccessor();
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return Float.valueOf(ClientboundCustomSoundPacketHandle.createHandle(instance).getPitch());
            }

            @Override
            public boolean set(Object instance, Float value) {
                ClientboundCustomSoundPacketHandle.createHandle(instance).setPitch(value.floatValue());
                return true;
            }
        };
    }

    /// ====================== NMSPacketPlayOutEntity and derivatives ===========================

    public static class NMSClientboundMoveEntityPacket extends NMSPacket {

        public final FieldAccessor<Integer> entityId = ClientboundMoveEntityPacketHandle.T.entityId.toFieldAccessor();

        public final FieldAccessor<Double> dx = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundMoveEntityPacketHandle.createHandle(instance).getDeltaX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundMoveEntityPacketHandle.createHandle(instance).setDeltaX(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> dy = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundMoveEntityPacketHandle.createHandle(instance).getDeltaY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundMoveEntityPacketHandle.createHandle(instance).setDeltaY(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> dz = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundMoveEntityPacketHandle.createHandle(instance).getDeltaZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundMoveEntityPacketHandle.createHandle(instance).setDeltaZ(value.doubleValue());
                return true;
            }
        };

        public final FieldAccessor<Float> dyaw = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundMoveEntityPacketHandle.createHandle(instance).getYaw();
            }

            @Override
            public boolean set(Object instance, Float value) {
                ClientboundMoveEntityPacketHandle.createHandle(instance).setYaw(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Float> dpitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundMoveEntityPacketHandle.createHandle(instance).getPitch();
            }

            @Override
            public boolean set(Object instance, Float value) {
                ClientboundMoveEntityPacketHandle.createHandle(instance).setPitch(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Boolean> onGround = ClientboundMoveEntityPacketHandle.T.onGround.toFieldAccessor();

        public NMSClientboundMoveEntityPacket() {
            super();
        }

        protected NMSClientboundMoveEntityPacket(Class<?> packetClass) {
            super(packetClass);
        }
    }

    public static class NMSClientboundMoveEntityPacketPos extends NMSClientboundMoveEntityPacket {

        public NMSClientboundMoveEntityPacketPos() {
            super(ClientboundMoveEntityPacketHandle.PosHandle.T.getType());
        }

        public CommonPacket newInstance(int entityId, double dx, double dy, double dz, boolean onGround) {
            return PosHandle.createNew(entityId, dx, dy, dz, onGround).toCommonPacket();
        }
    }

    public static class NMSClientboundMoveEntityPacketPosRot extends NMSClientboundMoveEntityPacket {

        public NMSClientboundMoveEntityPacketPosRot() {
            super(ClientboundMoveEntityPacketHandle.PosRotHandle.T.getType());
        }

        public CommonPacket newInstance(int entityId, double dx, double dy, double dz, float dyaw, float dpitch, boolean onGround) {
            return PosRotHandle.createNew(entityId, dx, dy, dz, dyaw, dpitch, onGround).toCommonPacket();
        }
    }

    public static class NMSClientboundMoveEntityPacketRot extends NMSClientboundMoveEntityPacket {

        public NMSClientboundMoveEntityPacketRot() {
            super(ClientboundMoveEntityPacketHandle.RotHandle.T.getType());
        }

        public CommonPacket newInstance(int entityId, float dyaw, float dpitch, boolean onGround) {
            return ClientboundMoveEntityPacketHandle.RotHandle.createNew(entityId, dyaw, dpitch, onGround).toCommonPacket();
        }
    }

    // ====================================================================================================

    public static class NMSClientboundRemoveEntitiesPacket extends NMSPacket {

        /**
         * <b>Warning: </b>Setting multiple entity id's
         * is not supported on Minecraft 1.17 and later. Getting is always safe.
         */
        public final FieldAccessor<int[]> entityIds = new SafeDirectField<int[]>() {
            @Override
            public int[] get(Object instance) {
                return ClientboundRemoveEntitiesPacketHandle.T.getEntityIds.invoke(instance);
            }

            @Override
            public boolean set(Object instance, int[] value) {
                ClientboundRemoveEntitiesPacketHandle.T.setMultipleEntityIds.invoke(instance, value);
                return true;
            }
        };

        /**
         * <b>Warning:</b> Getting this field on Minecraft 1.16 and earlier may raise an
         * exception if multiple entity id's are stored. Setting is always safe.
         */
        public final FieldAccessor<Integer> entityId = new SafeDirectField<Integer>() {
            @Override
            public Integer get(Object instance) {
                return ClientboundRemoveEntitiesPacketHandle.T.getSingleEntityId.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Integer value) {
                ClientboundRemoveEntitiesPacketHandle.T.setSingleEntityId.invoke(instance, value);
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
            Object raw = ClientboundRemoveEntitiesPacketHandle.T.createNewSingle.raw.invoke(entityId);
            return new CommonPacket(raw, PacketType.OUT_ENTITY_DESTROY);
        }

        /**
         * <b>Warning: </b>Multiple entity id's only supported on Minecraft 1.16 and earlier!
         *
         * @param entityIds
         * @return packet
         */
        public CommonPacket newInstanceMultiple(int... entityIds) {
            Object raw = ClientboundRemoveEntitiesPacketHandle.T.createNewMultiple.raw.invoke(entityIds);
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
         * @param entities Entities to be destroyed
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

    public static class NMSClientboundUpdateMobEffectPacket extends NMSPacket {

        public final FieldAccessor<Integer> entityId = ClientboundUpdateMobEffectPacketHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<PotionEffectType> effect = new SafeDirectField<PotionEffectType>() {
            @Override
            public PotionEffectType get(Object instance) {
                return ClientboundUpdateMobEffectPacketHandle.createHandle(instance).getPotionEffectType();
            }

            @Override
            public boolean set(Object instance, PotionEffectType value) {
                ClientboundUpdateMobEffectPacketHandle.createHandle(instance).setPotionEffectType(value);
                return true;
            }
        };
        public final FieldAccessor<Integer> effectAmplifier = new SafeDirectField<Integer>() {
            @Override
            public Integer get(Object instance) {
                return ClientboundUpdateMobEffectPacketHandle.createHandle(instance).getEffectAmplifier();
            }

            @Override
            public boolean set(Object instance, Integer value) {
                ClientboundUpdateMobEffectPacketHandle.createHandle(instance).setEffectAmplifier(value);
                return true;
            }
        };
        public final FieldAccessor<Integer> effectDuration = ClientboundUpdateMobEffectPacketHandle.T.effectDurationTicks.toFieldAccessor();
        public final FieldAccessor<Byte> effectFlags = ClientboundUpdateMobEffectPacketHandle.T.flags.toFieldAccessor();

        public CommonPacket newInstance(int entityId, MobEffectInstanceHandle mobEffect) {
            return new CommonPacket(ClientboundUpdateMobEffectPacketHandle.T.createNew.raw.invoke(entityId, mobEffect.getRaw(), false));
        }

        public CommonPacket newInstance(int entityId, PotionEffect effect) {
            return ClientboundUpdateMobEffectPacketHandle.createNew(entityId, effect).toCommonPacket();
        }
    }

    /**
     * <b>Deprecated: please use {@link ClientboundSetEquipmentPacketHandle} instead.</b>
     */
    @Deprecated
    public static class NMSClientboundSetEquipmentPacket extends NMSPacket {

        public final FieldAccessor<Integer> entityId = ClientboundSetEquipmentPacketHandle.T.entityId.toFieldAccessor();

        public CommonPacket newInstance(int entityId, EquipmentSlot equipmentSlot, ItemStack item) {
            return ClientboundSetEquipmentPacketHandle.createNew(entityId, equipmentSlot, item).toCommonPacket();
        }
    }

    public static class NMSClientboundRotateHeadPacket extends NMSPacket {
        public final FieldAccessor<Integer> entityId = ClientboundRotateHeadPacketHandle.T.entityId.toFieldAccessor();

        public final FieldAccessor<Float> headYaw = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundRotateHeadPacketHandle.createHandle(instance).getHeadYaw();
            }

            @Override
            public boolean set(Object instance, Float value) {
                ClientboundRotateHeadPacketHandle.createHandle(instance).setHeadYaw(value.floatValue());
                return true;
            }
        };

        public CommonPacket newInstance(org.bukkit.entity.Entity entity, float headRotation) {
            return ClientboundRotateHeadPacketHandle.createNew(entity, headRotation).toCommonPacket();
        }
    }

    public static class NMSClientboundSetEntityDataPacket extends NMSPacket {

        public final FieldAccessor<Integer> entityId = ClientboundSetEntityDataPacketHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<List<DataWatcher.PackedItem<Object>>> watchedObjects = ClientboundSetEntityDataPacketHandle.T.metadataItems.toFieldAccessor();

        public CommonPacket newForSpawn(int entityId, DataWatcher dataWatcher) {
            return ClientboundSetEntityDataPacketHandle.createForSpawn(entityId, dataWatcher).toCommonPacket();
        }

        public CommonPacket newForChanges(int entityId, DataWatcher dataWatcher) {
            return ClientboundSetEntityDataPacketHandle.createForChanges(entityId, dataWatcher).toCommonPacket();
        }

        public CommonPacket newInstance(int entityId, DataWatcher dataWatcher, boolean sendUnchangedData) {
            return ClientboundSetEntityDataPacketHandle.createNew(entityId, dataWatcher, sendUnchangedData).toCommonPacket();
        }
    }

    public static class NMSClientboundEntityEventPacket extends NMSPacket {

        public final FieldAccessor<Integer> entityId = ClientboundEntityEventPacketHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<Byte> status = ClientboundEntityEventPacketHandle.T.eventId.toFieldAccessor();
    }

    public static class NMSClientboundEntityPositionSyncPacket extends NMSPacket {

        public final FieldAccessor<Integer> entityId = new FieldAccessor<Integer>() {
            @Override
            public Integer get(Object instance) {
                return ClientboundEntityPositionSyncPacketHandle.createHandle(instance).getEntityId();
            }

            @Override
            public boolean set(Object instance, Integer value) {
                return false;
            }
        };
        public final FieldAccessor<Double> x = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundEntityPositionSyncPacketHandle.createHandle(instance).getPosX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                return false;
            }
        };
        public final FieldAccessor<Double> y = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundEntityPositionSyncPacketHandle.createHandle(instance).getPosY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                return false;
            }
        };
        public final FieldAccessor<Double> z = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundEntityPositionSyncPacketHandle.createHandle(instance).getPosZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                return false;
            }
        };
        public final FieldAccessor<Float> yaw = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundEntityPositionSyncPacketHandle.createHandle(instance).getYaw();
            }

            @Override
            public boolean set(Object instance, Float value) {
                return false;
            }
        };
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundEntityPositionSyncPacketHandle.createHandle(instance).getPitch();
            }

            @Override
            public boolean set(Object instance, Float value) {
                return false;
            }
        };
        public final FieldAccessor<Boolean> onGround = new SafeDirectField<Boolean>() {
            @Override
            public Boolean get(Object instance) {
                return ClientboundEntityPositionSyncPacketHandle.createHandle(instance).isOnGround();
            }

            @Override
            public boolean set(Object instance, Boolean value) {
                return false;
            }
        };

        /**
         * @deprecated It is no longer possible to construct this packet with initial values, as the
         * class is now a record class. Use the other constructors instead.
         */
        @Override
        @Deprecated
        public CommonPacket newInstance() {
            throw new UnsupportedOperationException("Not supported anymore");
        }

        public CommonPacket newInstance(org.bukkit.entity.Entity entity) {
            return ClientboundEntityPositionSyncPacketHandle.createNewForEntity(entity).toCommonPacket();
        }

        public CommonPacket newInstance(int entityId, double posX, double posY, double posZ, float yaw, float pitch, boolean onGround) {
            return ClientboundEntityPositionSyncPacketHandle.createNew(entityId, posX, posY, posZ, yaw, pitch, onGround).toCommonPacket();
        }
    }

    /**
     * @deprecated Packet is immutable, use {@link ClientboundSetEntityMotionPacketHandle} instead
     */
    @Deprecated
    public static class NMSClientboundSetEntityMotionPacket extends NMSPacket {

        public final FieldAccessor<Integer> entityId = ClientboundSetEntityMotionPacketHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<Vector> motion = new SafeDirectField<Vector>() {
            @Override
            public Vector get(Object o) {
                return ClientboundSetEntityMotionPacketHandle.T.getMotVector.invoke(o);
            }

            @Override
            public boolean set(Object o, Vector vector) {
                return false;
            }
        };

        @Deprecated
        public CommonPacket newInstance(org.bukkit.entity.Entity entity) {
            return ClientboundSetEntityMotionPacketHandle.createNew(entity).toCommonPacket();
        }

        @Deprecated
        public CommonPacket newInstance(int entityId, double motX, double motY, double motZ) {
            return ClientboundSetEntityMotionPacketHandle.createNew(entityId, motX, motY, motZ).toCommonPacket();
        }

        public CommonPacket newInstance(int entityId, Vector velocity) {
            return newInstance(entityId, velocity.getX(), velocity.getY(), velocity.getZ());
        }
    }

    public static class NMSClientboundSetExperiencePacket extends NMSPacket {

        public final FieldAccessor<Float> bar = ClientboundSetExperiencePacketHandle.T.experienceProgress.toFieldAccessor();
        public final FieldAccessor<Integer> level = ClientboundSetExperiencePacketHandle.T.experienceLevel.toFieldAccessor();
        public final FieldAccessor<Integer> totalXp = ClientboundSetExperiencePacketHandle.T.totalExperience.toFieldAccessor();
        private final SafeConstructor<CommonPacket> constructor1 = getPacketConstructor(float.class, int.class, int.class);

        public CommonPacket newInstance(float bar, int level, int totalXp) {
            return constructor1.newInstance(bar, level, totalXp);
        }
    }

    //TODO: Actually implement a method to create new explosion packets
    //      Inspecting the fields of the packet is just not worth it anymore (is a record class now...)
    public static class NMSClientboundExplodePacket extends NMSPacket {
        // Pain in the ass API with loads of parameters. Needs builder API honestly.
        /*
        public CommonPacket newInstance(double x, double y, double z, float radius) {
            return newInstance(x, y, z, radius, Collections.emptyList());
        }

        public CommonPacket newInstance(double x, double y, double z, float radius, List<IntVector3> blocks) {
            return newInstance(x, y, z, radius, blocks, null);
        }

        public CommonPacket newInstance(double x, double y, double z, float power, List<IntVector3> blocks, Vector knockback) {
            return ClientboundExplodePacketHandle.createNew(x, y, z, power, blocks, knockback).toCommonPacket();
        }
         */
    }

    public static class NMSClientboundGameEventPacket extends NMSPacket {
        //TODO: What does it all mean???
    }

    public static class NMSClientboundSetHeldSlotPacket extends NMSPacket {

        public final FieldAccessor<Integer> slot = ClientboundSetHeldSlotPacketHandle.T.itemInHandIndex.toFieldAccessor();
    }

    public static class NMSClientboundKeepAlivePacket extends NMSPacket {

        public final FieldAccessor<Long> key = FieldAccessor.wrapMethods(ClientboundKeepAlivePacketHandle.T.getKey, ClientboundKeepAlivePacketHandle.T.setKey);
    }

    public static class NMSClientboundDisconnectPacket extends NMSPacket {

        public final FieldAccessor<ChatText> reason = ClientboundDisconnectPacketHandle.T.reason.toFieldAccessor();
    }

    public static class NMSClientboundLoginPacket extends NMSPacket {

        public final FieldAccessor<GameMode> gameMode = new SafeDirectField<GameMode>() {
            @Override
            public GameMode get(Object instance) {
                return ClientboundLoginPacketHandle.T.getGameMode.invoke(instance);
            }

            @Override
            public boolean set(Object instance, GameMode value) {
                return false;
            }
        };
        public final FieldAccessor<DimensionType> dimensionType = new SafeDirectField<DimensionType>() {
            @Override
            public DimensionType get(Object instance) {
                return ClientboundLoginPacketHandle.T.getDimensionType.invoke(instance);
            }

            @Override
            public boolean set(Object instance, DimensionType value) {
                return false;
            }
        };
        public final FieldAccessor<Difficulty> difficulty = ClientboundLoginPacketHandle.T.difficulty.toFieldAccessor().ignoreInvalid(Difficulty.NORMAL);
    }

    public static class NMSClientboundMapItemDataPacket extends NMSPacket {
    }

    public static class NMSClientboundLevelChunkWithLightPacket extends NMSPacket {

        public final FieldAccessor<Integer> x = ClientboundLevelChunkWithLightPacketHandle.T.x.toFieldAccessor();
        public final FieldAccessor<Integer> z = ClientboundLevelChunkWithLightPacketHandle.T.z.toFieldAccessor();
        public final FieldAccessor<CommonTagCompound> heightmaps = new SafeDirectField<CommonTagCompound>() {
            @Override
            public CommonTagCompound get(Object instance) {
                return ClientboundLevelChunkWithLightPacketHandle.createHandle(instance).getHeightmaps();
            }

            @Override
            public boolean set(Object instance, CommonTagCompound value) {
                ClientboundLevelChunkWithLightPacketHandle.createHandle(instance).setHeightmaps(value);
                return true;
            }
        };
        public final FieldAccessor<byte[]> buffer = new SafeDirectField<byte[]>() {
            @Override
            public byte[] get(Object instance) {
                return ClientboundLevelChunkWithLightPacketHandle.createHandle(instance).getBuffer();
            }

            @Override
            public boolean set(Object instance, byte[] value) {
                ClientboundLevelChunkWithLightPacketHandle.createHandle(instance).setBuffer(value);
                return true;
            }
        };
        public final FieldAccessor<List<BlockStateChange>> blockStates = new SafeDirectField<List<BlockStateChange>>() {
            @Override
            public List<BlockStateChange> get(Object instance) {
                return ClientboundLevelChunkWithLightPacketHandle.createHandle(instance).getBlockStates();
            }

            @Override
            public boolean set(Object instance, List<BlockStateChange> value) {
                ClientboundLevelChunkWithLightPacketHandle.createHandle(instance).setBlockStates(value);
                return true;
            }
        };
    }

    public static class NMSClientboundSetPassengersPacket extends NMSPacket {

        public final FieldAccessor<Integer> entityId = ClientboundSetPassengersPacketHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<int[]> mountedEntityIds = ClientboundSetPassengersPacketHandle.T.mountedEntityIds.toFieldAccessor();

        public CommonPacket newInstanceHandles(org.bukkit.entity.Entity entity, List<EntityHandle> passengers) {
            int[] passengerIds = new int[passengers.size()];
            for (int i = 0; i < passengerIds.length; i++) {
                passengerIds[i] = passengers.get(i).getId();
            }
            return ClientboundSetPassengersPacketHandle.createNew(entity.getEntityId(), passengerIds).toCommonPacket();
        }

        public CommonPacket newInstance(org.bukkit.entity.Entity vehicle, List<org.bukkit.entity.Entity> passengers) {
            int[] passengerIds = new int[passengers.size()];
            for (int i = 0; i < passengerIds.length; i++) {
                passengerIds[i] = passengers.get(i).getEntityId();
            }
            return ClientboundSetPassengersPacketHandle.createNew(vehicle.getEntityId(), passengerIds).toCommonPacket();
        }
    }

    public static class NMSClientboundAddPlayerPacket extends NMSPacket {

        public final FieldAccessor<Integer> entityId = ClientboundAddPlayerPacketHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<UUID> uuid = ClientboundAddPlayerPacketHandle.T.entityUUID.toFieldAccessor();
        public final FieldAccessor<Double> posX = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddPlayerPacketHandle.createHandle(instance).getPosX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddPlayerPacketHandle.createHandle(instance).setPosX(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posY = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddPlayerPacketHandle.createHandle(instance).getPosY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddPlayerPacketHandle.createHandle(instance).setPosY(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posZ = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddPlayerPacketHandle.createHandle(instance).getPosZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddPlayerPacketHandle.createHandle(instance).setPosZ(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Float> yaw = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundAddPlayerPacketHandle.createHandle(instance).getYaw();
            }

            @Override
            public boolean set(Object instance, Float value) {
                ClientboundAddPlayerPacketHandle.createHandle(instance).setYaw(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundAddPlayerPacketHandle.createHandle(instance).getPitch();
            }

            @Override
            public boolean set(Object instance, Float value) {
                ClientboundAddPlayerPacketHandle.createHandle(instance).setPitch(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Material> heldItemId = ClientboundAddPlayerPacketHandle.T.heldItem.toFieldAccessor().ignoreInvalid(Material.AIR);
    }

    public static class NMSClientboundSoundPacket extends NMSPacket {

        // Only used >= MC 1.10.2 to denote the sound bank name
        public final FieldAccessor<String> category = new SafeDirectField<String>() {
            @Override
            public String get(Object instance) {
                return ClientboundSoundPacketHandle.createHandle(instance).getCategory();
            }

            @Override
            public boolean set(Object instance, String value) {
                ClientboundSoundPacketHandle.createHandle(instance).setCategory(value);
                return true;
            }
        };

        public final FieldAccessor<ResourceKey<SoundEffect>> sound = ClientboundSoundPacketHandle.T.sound.toFieldAccessor();
        public final FieldAccessor<Integer> x = ClientboundSoundPacketHandle.T.x.toFieldAccessor();
        public final FieldAccessor<Integer> y = ClientboundSoundPacketHandle.T.y.toFieldAccessor();
        public final FieldAccessor<Integer> z = ClientboundSoundPacketHandle.T.z.toFieldAccessor();
        public final FieldAccessor<Float> volume = ClientboundSoundPacketHandle.T.volume.toFieldAccessor();
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundSoundPacketHandle.createHandle(instance).getPitch();
            }

            @Override
            public boolean set(Object instance, Float value) {
                ClientboundSoundPacketHandle.createHandle(instance).setPitch(value.floatValue());
                return true;
            }
        };
    }

    public static class NMSClientboundOpenSignEditorPacket extends NMSPacket {

        public final FieldAccessor<IntVector3> signPosition = ClientboundOpenSignEditorPacketHandle.T.signPosition.toFieldAccessor();
    }

    public static class NMSClientboundOpenScreenPacket extends NMSPacket {

        public final FieldAccessor<Integer> windowId = ClientboundOpenScreenPacketHandle.T.windowId.toFieldAccessor();
        public final FieldAccessor<WindowType> windowType = new SafeDirectField<WindowType>() {
            @Override
            public WindowType get(Object instance) {
                return ClientboundOpenScreenPacketHandle.T.getWindowType.invoke(instance);
            }

            @Override
            public boolean set(Object instance, WindowType value) {
                ClientboundOpenScreenPacketHandle.T.setWindowType.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<ChatText> windowTitle = ClientboundOpenScreenPacketHandle.T.windowTitle.toFieldAccessor();
    }

    public static class NMSClientboundPlayerInfoUpdatePacket extends NMSPacket {
        @Override
        protected boolean matchPacket(Object packetHandle) {
            if (CommonCapabilities.PLAYER_INFO_PACKET_SPLIT) {
                return true;
            } else {
                // Action must NOT be REMOVE
                return !ClientboundPlayerInfoUpdatePacketHandle.isPlayerInfoRemovePacket(packetHandle);
            }
        }
    }

    public static class NMSClientboundPlayerInfoRemovePacket extends NMSPacket {
        @Override
        protected boolean matchPacket(Object packetHandle) {
            if (CommonCapabilities.PLAYER_INFO_PACKET_SPLIT) {
                return true;
            } else {
                // Action must be REMOVE
                return ClientboundPlayerInfoUpdatePacketHandle.isPlayerInfoRemovePacket(packetHandle);
            }
        }
    }

    public static class NMSClientboundTabListPacket extends NMSPacket {

        public final FieldAccessor<ChatText> header = ClientboundTabListPacketHandle.T.header.toFieldAccessor();
        public final FieldAccessor<ChatText> footer = ClientboundTabListPacketHandle.T.footer.toFieldAccessor();
    }

    public static class NMSClientboundPlayerPositionPacket extends NMSPacket {

        public final FieldAccessor<Double> x = new FieldAccessor<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundPlayerPositionPacketHandle.T.getX.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Double value) {
                return false;
            }
        };
        public final FieldAccessor<Double> y = new FieldAccessor<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundPlayerPositionPacketHandle.T.getY.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Double value) {
                return false;
            }
        };
        public final FieldAccessor<Double> z = new FieldAccessor<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundPlayerPositionPacketHandle.T.getZ.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Double value) {
                return false;
            }
        };
        public final FieldAccessor<Float> yaw = new FieldAccessor<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundPlayerPositionPacketHandle.T.getYaw.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                return false;
            }
        };
        public final FieldAccessor<Float> pitch = new FieldAccessor<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundPlayerPositionPacketHandle.T.getPitch.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                return false;
            }
        };
        public final FieldAccessor<Integer> teleportWaitTimer = new SafeDirectField<Integer>() {
            @Override
            public Integer get(Object instance) {
                return ClientboundPlayerPositionPacketHandle.createHandle(instance).getTeleportWaitTimer();
            }

            @Override
            public boolean set(Object instance, Integer value) {
                return false;
            }
        };
    }

    public static class NMSClientboundPlayerRotationPacket extends NMSPacket {
        private static final boolean IS_SEPARATE_PACKET = CommonBootstrap.evaluateMCVersion(">=", "1.21.2");

        public final FieldAccessor<Float> yaw = new FieldAccessor<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundPlayerRotationPacketHandle.T.getYaw.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                return false;
            }
        };
        public final FieldAccessor<Float> pitch = new FieldAccessor<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundPlayerRotationPacketHandle.T.getPitch.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Float value) {
                return false;
            }
        };
        public final FieldAccessor<Boolean> isYawRelative = new FieldAccessor<Boolean>() {
            @Override
            public Boolean get(Object instance) {
                return ClientboundPlayerRotationPacketHandle.T.isYawRelative.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Boolean value) {
                return false;
            }
        };
        public final FieldAccessor<Boolean> isPitchRelative = new FieldAccessor<Boolean>() {
            @Override
            public Boolean get(Object instance) {
                return ClientboundPlayerRotationPacketHandle.T.isPitchRelative.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Boolean value) {
                return false;
            }
        };

        @Override
        protected boolean matchPacket(Object packetHandle) {
            return IS_SEPARATE_PACKET;
        }
    }

    public static class NMSClientboundRemoveMobEffectPacket extends NMSPacket {

        public CommonPacket newInstance(int entityId, PotionEffectType effectType) {
            return ClientboundRemoveMobEffectPacketHandle.createNew(entityId, effectType).toCommonPacket();
        }

        public CommonPacket newInstance(int entityId, Holder<MobEffectHandle> mobEffectList) {
            return ClientboundRemoveMobEffectPacketHandle.createNew(entityId, mobEffectList).toCommonPacket();
        }
    }

    /**
     * @deprecated Use ClientboundResourcePackPushPacket instead
     */
    @Deprecated
    public static class NMSClientboundResourcePackPushPacket extends NMSPacket {

        public final FieldAccessor<String> name = ClientboundResourcePackPushPacketHandle.T.url.toFieldAccessor();
        public final FieldAccessor<String> hash = ClientboundResourcePackPushPacketHandle.T.hash.toFieldAccessor();
    }

    /**
     * @deprecated Use ClientboundResourcePackPopPacketHandle instead
     */
    @Deprecated
    public static class NMSClientboundResourcePackPopPacket extends NMSPacket {
    }

    public static class NMSClientboundRespawnPacket extends NMSPacket {

        public final FieldAccessor<DimensionType> dimensionType = new SafeDirectField<DimensionType>() {
            @Override
            public DimensionType get(Object instance) {
                return ClientboundRespawnPacketHandle.T.getDimensionType.invoke(instance);
            }

            @Override
            public boolean set(Object instance, DimensionType value) {
                return false;
            }
        };
        public final FieldAccessor<GameMode> gamemode = new SafeDirectField<GameMode>() {
            @Override
            public GameMode get(Object instance) {
                return ClientboundRespawnPacketHandle.T.getGamemode.invoke(instance);
            }

            @Override
            public boolean set(Object instance, GameMode value) {
                return false;
            }
        };

        public final FieldAccessor<Difficulty> difficulty = new SafeDirectField<Difficulty>() {
            @Override
            public Difficulty get(Object instance) {
                if (ClientboundRespawnPacketHandle.T.difficulty.isAvailable()) {
                    return ClientboundRespawnPacketHandle.T.difficulty.get(instance);
                } else {
                    return Difficulty.NORMAL;
                }
            }

            @Override
            public boolean set(Object instance, Difficulty value) {
                if (ClientboundRespawnPacketHandle.T.difficulty.isAvailable()) {
                    ClientboundRespawnPacketHandle.T.difficulty.set(instance, value);
                    return true;
                } else {
                    return false;
                }
            }
        };
    }

    public static class NMSClientboundSetDisplayObjectivePacket extends NMSPacket {

        public final FieldAccessor<DisplaySlot> display = ClientboundSetDisplayObjectivePacketHandle.T.display.toFieldAccessor();
        public final FieldAccessor<String> name = ClientboundSetDisplayObjectivePacketHandle.T.name.toFieldAccessor();
    }

    public static class NMSClientboundSetObjectivePacket extends NMSPacket {

        public final FieldAccessor<String> name = ClientboundSetObjectivePacketHandle.T.name.toFieldAccessor();
        public final FieldAccessor<ChatText> displayName = ClientboundSetObjectivePacketHandle.T.displayName.toFieldAccessor();
        public final FieldAccessor<Object> criteria = ClientboundSetObjectivePacketHandle.T.criteria.toFieldAccessor();
        public final FieldAccessor<Integer> action = ClientboundSetObjectivePacketHandle.T.action.toFieldAccessor();
    }

    public static class NMSClientboundResetScorePacket extends NMSPacket {

        public static CommonPacket createNew(String name, String objectiveName) {
            return ClientboundResetScorePacketHandle.createNew(name, objectiveName).toCommonPacket();
        }
    }

    public static class NMSClientboundSetScorePacket extends NMSPacket {

        public final FieldAccessor<String> name = ClientboundSetScorePacketHandle.T.name.toFieldAccessor();
        public final FieldAccessor<String> objName = ClientboundSetScorePacketHandle.T.objName.toFieldAccessor();
        public final FieldAccessor<Integer> value = ClientboundSetScorePacketHandle.T.value.toFieldAccessor();

        public static CommonPacket createNew(String name, String objectiveName, int value) {
            return ClientboundSetScorePacketHandle.createNew(name, objectiveName, value).toCommonPacket();
        }
    }

    /**
     * @deprecated Please use {@link ClientboundSetPlayerTeamPacketHandle} instead
     */
    @Deprecated
    public static class NMSClientboundSetPlayerTeamPacket extends NMSPacket {

        public final FieldAccessor<Integer> method = ClientboundSetPlayerTeamPacketHandle.T.method.toFieldAccessor();
        public final FieldAccessor<String> name = ClientboundSetPlayerTeamPacketHandle.T.name.toFieldAccessor();
        public final FieldAccessor<ChatText> displayName = FieldAccessor.wrapMethods(ClientboundSetPlayerTeamPacketHandle.T.getDisplayName, ClientboundSetPlayerTeamPacketHandle.T.setDisplayName);
        public final FieldAccessor<ChatText> prefix = FieldAccessor.wrapMethods(ClientboundSetPlayerTeamPacketHandle.T.getPrefix, ClientboundSetPlayerTeamPacketHandle.T.setPrefix);
        public final FieldAccessor<ChatText> suffix = FieldAccessor.wrapMethods(ClientboundSetPlayerTeamPacketHandle.T.getSuffix, ClientboundSetPlayerTeamPacketHandle.T.setSuffix);
        public final FieldAccessor<String> visibility = FieldAccessor.wrapMethods(ClientboundSetPlayerTeamPacketHandle.T.getVisibility, ClientboundSetPlayerTeamPacketHandle.T.setVisibility);
        public final FieldAccessor<String> collisionRule = FieldAccessor.wrapMethods(ClientboundSetPlayerTeamPacketHandle.T.getCollisionRule, ClientboundSetPlayerTeamPacketHandle.T.setCollisionRule);
        public final FieldAccessor<ChatColor> color = FieldAccessor.wrapMethods(ClientboundSetPlayerTeamPacketHandle.T.getColor, ClientboundSetPlayerTeamPacketHandle.T.setColor);
        public final FieldAccessor<Collection<String>> players = ClientboundSetPlayerTeamPacketHandle.T.players.toFieldAccessor();
        public final FieldAccessor<Integer> teamOptionFlags = FieldAccessor.wrapMethods(ClientboundSetPlayerTeamPacketHandle.T.getTeamOptionFlags, ClientboundSetPlayerTeamPacketHandle.T.setTeamOptionFlags);

        @Override
        public CommonPacket newInstance() {
            return ClientboundSetPlayerTeamPacketHandle.createNew().toCommonPacket();
        }
    }

    public static class NMSClientboundChangeDifficultyPacket extends NMSPacket {

        public final FieldAccessor<Difficulty> difficulty = ClientboundChangeDifficultyPacketHandle.T.difficulty.toFieldAccessor();
        public final FieldAccessor<Boolean> hardcore = ClientboundChangeDifficultyPacketHandle.T.hardcore.toFieldAccessor();
    }

    public static class NMSClientboundCooldownPacket extends NMSPacket {

        //public final FieldAccessor<Material> material = ClientboundCooldownPacketHandle.T.material.toFieldAccessor();
        public final FieldAccessor<Integer> cooldown = ClientboundCooldownPacketHandle.T.cooldown.toFieldAccessor();
    }

    public static class NMSClientboundContainerSetSlotPacket extends NMSPacket {

        public final FieldAccessor<Integer> windowId = ClientboundContainerSetSlotPacketHandle.T.windowId.toFieldAccessor();
        public final FieldAccessor<Integer> slot = ClientboundContainerSetSlotPacketHandle.T.slot.toFieldAccessor();
        public final FieldAccessor<ItemStack> item = ClientboundContainerSetSlotPacketHandle.T.item.toFieldAccessor();
    }

    public static class NMSClientboundAddEntityPacket extends NMSPacket {
        public final FieldAccessor<Integer> entityId = ClientboundAddEntityPacketHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<UUID> UUID = ClientboundAddEntityPacketHandle.T.entityUUID.toFieldAccessor().ignoreInvalid(new java.util.UUID(0L, 0L));
        public final FieldAccessor<Double> posX = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddEntityPacketHandle.createHandle(instance).getPosX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddEntityPacketHandle.createHandle(instance).setPosX(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posY = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddEntityPacketHandle.createHandle(instance).getPosY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddEntityPacketHandle.createHandle(instance).setPosY(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posZ = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddEntityPacketHandle.createHandle(instance).getPosZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddEntityPacketHandle.createHandle(instance).setPosZ(value.doubleValue());
                return true;
            }
        };

        public final FieldAccessor<Double> motX = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddEntityPacketHandle.createHandle(instance).getMotX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddEntityPacketHandle.createHandle(instance).setMotX(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> motY = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddEntityPacketHandle.createHandle(instance).getMotY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddEntityPacketHandle.createHandle(instance).setMotY(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> motZ = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddEntityPacketHandle.createHandle(instance).getMotZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddEntityPacketHandle.createHandle(instance).setMotZ(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundAddEntityPacketHandle.createHandle(instance).getPitch();
            }

            @Override
            public boolean set(Object instance, Float value) {
                ClientboundAddEntityPacketHandle.createHandle(instance).setPitch(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Float> yaw = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundAddEntityPacketHandle.createHandle(instance).getYaw();
            }

            @Override
            public boolean set(Object instance, Float value) {
                ClientboundAddEntityPacketHandle.createHandle(instance).setYaw(value.floatValue());
                return true;
            }
        };

        public final FieldAccessor<Integer> extraData = ClientboundAddEntityPacketHandle.T.extraData.toFieldAccessor();

        /**
         * Deprecated: use the bukkitEntityType instead (safer)
         */
        @Deprecated
        public final FieldAccessor<Integer> entityType = new SafeDirectField<Integer>() {
            @Override
            public Integer get(Object instance) {
                return ClientboundAddEntityPacketHandle.createHandle(instance).getEntityTypeId();
            }

            @Override
            public boolean set(Object instance, Integer value) {
                ClientboundAddEntityPacketHandle.createHandle(instance).setEntityTypeId(value.intValue());
                return true;
            }
        };

        public final FieldAccessor<EntityType> bukkitEntityType = new SafeDirectField<EntityType>() {
            @Override
            public EntityType get(Object instance) {
                return ClientboundAddEntityPacketHandle.createHandle(instance).getEntityType();
            }

            @Override
            public boolean set(Object instance, EntityType value) {
                ClientboundAddEntityPacketHandle.createHandle(instance).setEntityType(value);
                return true;
            }
        };
    }

    public static class NMSClientboundAddExperienceOrbPacket extends NMSPacket {

        public final FieldAccessor<Integer> entityId = ClientboundAddExperienceOrbPacketHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<Double> posX = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddExperienceOrbPacketHandle.createHandle(instance).getPosX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddExperienceOrbPacketHandle.createHandle(instance).setPosX(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posY = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddExperienceOrbPacketHandle.createHandle(instance).getPosY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddExperienceOrbPacketHandle.createHandle(instance).setPosY(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posZ = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddExperienceOrbPacketHandle.createHandle(instance).getPosZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddExperienceOrbPacketHandle.createHandle(instance).setPosZ(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Integer> experience = ClientboundAddExperienceOrbPacketHandle.T.experience.toFieldAccessor();
    }

    public static class NMSClientboundAddMobPacket extends NMSPacket {

        public final FieldAccessor<Integer> entityId = ClientboundAddMobPacketHandle.T.entityId.toFieldAccessor();
        public final FieldAccessor<UUID> entityUUID = ClientboundAddMobPacketHandle.T.entityUUID.toFieldAccessor().ignoreInvalid(new UUID(0L, 0L));
        public final FieldAccessor<EntityType> entityType = new SafeDirectField<EntityType>() {

            @Override
            public EntityType get(Object instance) {
                return ClientboundAddMobPacketHandle.createHandle(instance).getEntityType();
            }

            @Override
            public boolean set(Object instance, EntityType value) {
                ClientboundAddMobPacketHandle.createHandle(instance).setEntityType(value);
                return true;
            }
        };
        public final FieldAccessor<Double> posX = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddMobPacketHandle.createHandle(instance).getPosX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddMobPacketHandle.createHandle(instance).setPosX(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posY = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddMobPacketHandle.createHandle(instance).getPosY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddMobPacketHandle.createHandle(instance).setPosY(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> posZ = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddMobPacketHandle.createHandle(instance).getPosZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddMobPacketHandle.createHandle(instance).setPosZ(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> motX = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddMobPacketHandle.createHandle(instance).getMotX();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddMobPacketHandle.createHandle(instance).setMotX(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> motY = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddMobPacketHandle.createHandle(instance).getMotY();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddMobPacketHandle.createHandle(instance).setMotY(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Double> motZ = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundAddMobPacketHandle.createHandle(instance).getMotZ();
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundAddMobPacketHandle.createHandle(instance).setMotZ(value.doubleValue());
                return true;
            }
        };
        public final FieldAccessor<Float> yaw = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundAddMobPacketHandle.createHandle(instance).getYaw();
            }

            @Override
            public boolean set(Object instance, Float value) {
                ClientboundAddMobPacketHandle.createHandle(instance).setYaw(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundAddMobPacketHandle.createHandle(instance).getPitch();
            }

            @Override
            public boolean set(Object instance, Float value) {
                ClientboundAddMobPacketHandle.createHandle(instance).setPitch(value.floatValue());
                return true;
            }
        };
        public final FieldAccessor<Float> headYaw = new SafeDirectField<Float>() {
            @Override
            public Float get(Object instance) {
                return ClientboundAddMobPacketHandle.createHandle(instance).getHeadYaw();
            }

            @Override
            public boolean set(Object instance, Float value) {
                ClientboundAddMobPacketHandle.createHandle(instance).setHeadYaw(value.floatValue());
                return true;
            }
        };

        @Override
        protected boolean matchPacket(Object packetHandle) {
            //TODO: Do we actually go through the effort of checking what entity types are for living entities?
            //      It's not really worth it. Just don't fire events for this type of entity anymore.
            return !CommonCapabilities.ENTITY_SPAWN_PACKETS_MERGED;
        }
    }

    public static class NMSClientboundAddPaintingPacket extends NMSPacket {

        public final FieldAccessor<Integer> entityId = ClientboundAddPaintingPacketHandle.T.entityId.toFieldAccessor();

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
     * @deprecated Use {@link ClientboundSetDefaultSpawnPositionPacketHandle} instead
     */
    @Deprecated
    public static class NMSClientboundSetDefaultSpawnPositionPacket extends NMSPacket {

        public final FieldAccessor<IntVector3> position = new SafeDirectField<IntVector3>() {
            @Override
            public IntVector3 get(Object o) {
                return ClientboundSetDefaultSpawnPositionPacketHandle.T.getSpawn.invoke(o).position();
            }

            @Override
            public boolean set(Object o, IntVector3 intVector3) {
                return false;
            }
        };
    }

    public static class NMSClientboundAwardStatsPacket extends NMSPacket {

        // Changed to Object2IntMap<Statistic> on MC 1.13
        // public final FieldAccessor<Map<Object, Integer>> statsMap = nextField("private Map<Statistic, Integer> a");
    }

    public static class NMSClientboundCommandSuggestionsPacket extends NMSPacket {

        // Changed format on MC 1.13
        // public final FieldAccessor<String[]> response = nextField("private String[] a");
    }

    public static class NMSClientboundBlockEntityDataPacket extends NMSPacket {

        public final TranslatorFieldAccessor<IntVector3> position = ClientboundBlockEntityDataPacketHandle.T.position.toFieldAccessor();
        public final FieldAccessor<BlockStateType> type = ClientboundBlockEntityDataPacketHandle.T.type.toFieldAccessor();
        public final FieldAccessor<CommonTagCompound> data = ClientboundBlockEntityDataPacketHandle.T.data.toFieldAccessor();

        public CommonPacket newInstance(IntVector3 blockPosition, BlockStateType type, CommonTagCompound data) {
            return ClientboundBlockEntityDataPacketHandle.createNew(blockPosition, type, data).toCommonPacket();
        }
    }

    public static class NMSClientboundForgetLevelChunkPacket extends NMSPacket {

        public final FieldAccessor<Integer> x = new SafeDirectField<Integer>() {
            @Override
            public Integer get(Object instance) {
                return ClientboundForgetLevelChunkPacketHandle.T.getCx.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Integer value) {
                ClientboundForgetLevelChunkPacketHandle.createHandle(instance).setCx(value);
                return true;
            }
        };
        public final FieldAccessor<Integer> z = new SafeDirectField<Integer>() {
            @Override
            public Integer get(Object instance) {
                return ClientboundForgetLevelChunkPacketHandle.T.getCz.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Integer value) {
                ClientboundForgetLevelChunkPacketHandle.createHandle(instance).setCz(value);
                return true;
            }
        };
    }

    public static class NMSClientboundUpdateAttributesPacket extends NMSPacket {

        /**
         * A list of NMS.Attribute elements - may require further API to work
         * with. For now, use reflection.
         */
        public final FieldAccessor<Integer> entityId = ClientboundUpdateAttributesPacketHandle.T.entityId.toFieldAccessor();

        public CommonPacket newInstance(int entityId, Collection<AttributeInstanceHandle> attributes) {
            return ClientboundUpdateAttributesPacketHandle.createNew(entityId, attributes).toCommonPacket();
        }
    }

    public static class NMSClientboundSetHealthPacket extends NMSPacket {

        public final FieldAccessor<Float> health = ClientboundSetHealthPacketHandle.T.health.toFieldAccessor();
        public final FieldAccessor<Integer> food = ClientboundSetHealthPacketHandle.T.food.toFieldAccessor();
        public final FieldAccessor<Float> foodSaturation = ClientboundSetHealthPacketHandle.T.foodSaturation.toFieldAccessor();
    }

    public static class NMSClientboundSetTimePacket extends NMSPacket {

        public final FieldAccessor<Long> age = ClientboundSetTimePacketHandle.T.gameTime.toFieldAccessor();
        public final FieldAccessor<Long> timeOfDay = ClientboundSetTimePacketHandle.T.dayTime.toFieldAccessor();
    }

    public static class NMSClientboundMoveVehiclePacket extends NMSPacket {

        public final FieldAccessor<Double> posX = new SafeDirectField<Double>() {
            @Override
            public Double get(Object o) {
                return ClientboundMoveVehiclePacketHandle.T.getPosX.invoke(o);
            }

            @Override
            public boolean set(Object o, Double aDouble) {
                return false;
            }
        };
        public final FieldAccessor<Double> posY = new SafeDirectField<Double>() {
            @Override
            public Double get(Object o) {
                return ClientboundMoveVehiclePacketHandle.T.getPosY.invoke(o);
            }

            @Override
            public boolean set(Object o, Double aDouble) {
                return false;
            }
        };
        public final FieldAccessor<Double> posZ = new SafeDirectField<Double>() {
            @Override
            public Double get(Object o) {
                return ClientboundMoveVehiclePacketHandle.T.getPosZ.invoke(o);
            }

            @Override
            public boolean set(Object o, Double aDouble) {
                return false;
            }
        };
        public final FieldAccessor<Float> yaw = new SafeDirectField<Float>() {
            @Override
            public Float get(Object o) {
                return ClientboundMoveVehiclePacketHandle.T.getYaw.invoke(o);
            }

            @Override
            public boolean set(Object o, Float aFloat) {
                return false;
            }
        };
        public final FieldAccessor<Float> pitch = new SafeDirectField<Float>() {
            @Override
            public Float get(Object o) {
                return ClientboundMoveVehiclePacketHandle.T.getPitch.invoke(o);
            }

            @Override
            public boolean set(Object o, Float aFloat) {
                return false;
            }
        };

        public CommonPacket newInstance(double posX, double posY, double posZ, float yaw, float pitch) {
            return ClientboundMoveVehiclePacketHandle.createNew(posX, posY, posZ, yaw, pitch).toCommonPacket();
        }
    }

    public static class NMSClientboundContainerSetDataPacket extends NMSPacket {

        public final FieldAccessor<Integer> windowId = ClientboundContainerSetDataPacketHandle.T.windowId.toFieldAccessor();
        public final FieldAccessor<Integer> id = ClientboundContainerSetDataPacketHandle.T.id.toFieldAccessor();
        public final FieldAccessor<Integer> value = ClientboundContainerSetDataPacketHandle.T.value.toFieldAccessor();
    }

    public static class NMSClientboundContainerSetContentPacket extends NMSPacket {

        public final FieldAccessor<Integer> windowId = ClientboundContainerSetContentPacketHandle.T.windowId.toFieldAccessor();
        public final FieldAccessor<List<ItemStack>> items = ClientboundContainerSetContentPacketHandle.T.items.toFieldAccessor();
    }

    public static class NMSClientboundLevelEventPacket extends NMSPacket {

        public final FieldAccessor<Integer> effectId = ClientboundLevelEventPacketHandle.T.effectId.toFieldAccessor();
        public final TranslatorFieldAccessor<IntVector3> position = ClientboundLevelEventPacketHandle.T.position.toFieldAccessor();
        public final FieldAccessor<Integer> data = ClientboundLevelEventPacketHandle.T.data.toFieldAccessor();
        public final FieldAccessor<Boolean> noRelativeVolume = ClientboundLevelEventPacketHandle.T.globalEvent.toFieldAccessor();
    }

    public static class NMSClientboundLevelParticlesPacket extends NMSPacket {

        public final FieldAccessor<Double> x = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundLevelParticlesPacketHandle.T.getPosX.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundLevelParticlesPacketHandle.T.setPosX.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Double> y = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundLevelParticlesPacketHandle.T.getPosY.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundLevelParticlesPacketHandle.T.setPosY.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Double> z = new SafeDirectField<Double>() {
            @Override
            public Double get(Object instance) {
                return ClientboundLevelParticlesPacketHandle.T.getPosZ.invoke(instance);
            }

            @Override
            public boolean set(Object instance, Double value) {
                ClientboundLevelParticlesPacketHandle.T.setPosZ.invoke(instance, value);
                return true;
            }
        };
        public final FieldAccessor<Float> randomX = ClientboundLevelParticlesPacketHandle.T.randomX.toFieldAccessor();
        public final FieldAccessor<Float> randomY = ClientboundLevelParticlesPacketHandle.T.randomY.toFieldAccessor();
        public final FieldAccessor<Float> randomZ = ClientboundLevelParticlesPacketHandle.T.randomZ.toFieldAccessor();
        public final FieldAccessor<Float> speed = ClientboundLevelParticlesPacketHandle.T.speed.toFieldAccessor();
        public final FieldAccessor<Integer> particleCount = ClientboundLevelParticlesPacketHandle.T.count.toFieldAccessor();
        public final FieldAccessor<Boolean> overrideLimiter = ClientboundLevelParticlesPacketHandle.T.overrideLimiter.toFieldAccessor();

        @Override
        public CommonPacket newInstance() {
            return new CommonPacket(ClientboundLevelParticlesPacketHandle.T.createNew.raw.invoke(), this);
        }
    }

    //////////////////////////////////////////////////////////////////////

    public static class NMSPacketPlayOutUpdateSign extends NMSPacket {

        public final FieldAccessor<World> world = PacketPlayOutUpdateSignHandle.T.world.toFieldAccessor();
        public final FieldAccessor<IntVector3> position = PacketPlayOutUpdateSignHandle.T.position.toFieldAccessor();
        public final FieldAccessor<ChatText[]> lines = PacketPlayOutUpdateSignHandle.T.lines.toFieldAccessor();
    }

    public static class NMSClientboundBundlePacket extends NMSPacket {

        @Override
        public boolean isOutGoing() {
            return true; // Bug because this packet isn't registered in the normal places
        }
    }

    // Since 1.21.2. Can be used to identify when network issues occur.
    public static class NMSServerboundClientTickEndPacket extends NMSPacket {
    }
}
