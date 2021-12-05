package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Method;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;

/**
 * Handler for Minecraft 1.14.1 onwards. Uses PortalCreateEvent introduced on this version.<br>
 * <br>
 * findPortal is a little bit bugged, and it cannot
 * find a recently created portal. We use the newly added PortalCreateEvent to track it down.
 * There is a createPortal() function that accepts the position to look from. On versions prior
 * to 1.15.2 this has to be done by proxying an entity, passing this information
 * using the locX/Y/Z fields.
 */
class PortalHandler_1_14_1 extends PortalHandler implements Listener {
    private final PortalTravelAgentHandle _pta = Template.Class.create(PortalTravelAgentHandle.class, Common.TEMPLATE_RESOLVER);
    private boolean _anticipatingNetherPairCreateEvent = false;
    private Block _createdNetherPortalBlock;
    private boolean _endPlatformCreated = false;

    /**
     * Used inside generated code to produce a valid Entity instance,
     * that creates the instantiator bukkit entity when it is time to fire events.
     */
    private Entity _bukkitEntityReturnedByDummy;
    private final Object _dummyEntityInstance;

    public PortalHandler_1_14_1() {
        ClassInterceptor interceptor = new ClassInterceptor() {
            @Override
            protected Invoker<?> getCallback(Method method) {
                if (method.getName().equals("getBukkitEntity")) {
                    return (inst, args) -> _bukkitEntityReturnedByDummy;
                }
                return null;
            }
        };
        _dummyEntityInstance = interceptor.createInstance(EntityPlayerHandle.T.getType());
    }

    @Override
    public void enable(CommonPlugin plugin) {
        plugin.register(this);
    }

    @Override
    public void disable(CommonPlugin plugin) {
    }

    @Override
    public void forceInitialization() {
        _pta.forceInitialization();
    }

    @Override
    public Block findNetherPortal(Block startBlock, int radius) {
        return _pta.findNetherPortal(startBlock, radius);
    }

    @Override
    public Block createNetherPortal(Block startBlock, BlockFace orientation, Entity initiator) {
        try {
            _anticipatingNetherPairCreateEvent = true;
            _createdNetherPortalBlock = null;
            _bukkitEntityReturnedByDummy = initiator;
            _pta.createNetherPortal(startBlock,
                    orientation,
                    initiator == null ? null : HandleConversion.toEntityHandle(initiator),
                    _dummyEntityInstance,
                    16);

            return _createdNetherPortalBlock;
        } finally {
            _anticipatingNetherPairCreateEvent = false;
        }
    }

    @Override
    public void markNetherPortal(Block netherPortalBlock) {
        if (MaterialUtil.ISNETHERPORTAL.get(netherPortalBlock)) {
            _pta.storeNetherPortal(netherPortalBlock);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPortalCreated(PortalCreateEvent event) {
        if (event.getReason() == CreateReason.NETHER_PAIR) {
            if (!_anticipatingNetherPairCreateEvent) {
                _createdNetherPortalBlock = null;
                return;
            }

            for (BlockState state : event.getBlocks()) {
                if (MaterialUtil.ISNETHERPORTAL.get(state.getType())) {
                    Block block = state.getBlock();
                    _pta.storeNetherPortal(block);
                    if (_createdNetherPortalBlock == null || state.getY() < block.getY()) {
                        _createdNetherPortalBlock = block;
                    }
                }
            }

            // We don't expect another one
            _anticipatingNetherPairCreateEvent = false;
        } else if (event.getReason() == CreateReason.END_PLATFORM) {
            _endPlatformCreated = true;
        }
    }

    @Override
    public Block findEndPlatform(World world) {
        return _pta.findEndPlatform(world);
    }

    @Override
    public Block createEndPlatform(World world, Entity initiator) {
        _endPlatformCreated = false;
        Block block = _pta.createEndPlatform(world, HandleConversion.toEntityHandle(initiator));
        return _endPlatformCreated ? block : null;
    }

    @Override
    public boolean isMainEndWorld(World world) {
        return _pta.isMainEndWorld(world);
    }

    @Override
    public void showEndCredits(Player player) {
        EntityPlayerHandle ep = EntityPlayerHandle.fromBukkit(player);
        _pta.showEndCredits(HandleConversion.toEntityHandle(player), ep.hasSeenCredits());
        ep.setHasSeenCredits(true);
    }

    @Template.Optional
    @Template.Import("net.minecraft.core.BlockPosition")
    @Template.Import("net.minecraft.core.BlockPosition$MutableBlockPosition")
    @Template.Import("net.minecraft.core.EnumDirection")
    @Template.Import("net.minecraft.core.EnumDirection$EnumAxis")
    @Template.Import("net.minecraft.server.level.EntityPlayer")
    @Template.Import("net.minecraft.server.level.WorldServer")
    @Template.Import("net.minecraft.network.protocol.game.PacketPlayOutGameStateChange")
    @Template.Import("net.minecraft.world.entity.ai.village.poi.VillagePlace")
    @Template.Import("net.minecraft.world.entity.ai.village.poi.VillagePlaceType")
    @Template.Import("net.minecraft.world.entity.Entity")
    @Template.Import("net.minecraft.world.entity.Entity$RemovalReason")
    @Template.Import("net.minecraft.world.level.block.Blocks")
    @Template.Import("net.minecraft.world.level.block.state.pattern.ShapeDetector")
    @Template.Import("net.minecraft.world.level.dimension.DimensionManager")
    @Template.Import("net.minecraft.world.level.World")
    @Template.Import("net.minecraft.world.level.border.WorldBorder")
    @Template.InstanceType("net.minecraft.world.level.portal.PortalTravelAgent")
    public static abstract class PortalTravelAgentHandle extends Template.Class<Template.Handle> {
        /*
         * <SHOW_END_CREDITS>
         * public static void showEndCredits(Object entityPlayerRaw, boolean seenCredits) {
         *     EntityPlayer player = (EntityPlayer) entityPlayerRaw;
         * #if version >= 1.18
         *     player.isChangingDimension = true;
         *     player.unRide();
         *     player.getLevel().removePlayerImmediately(player, Entity$RemovalReason.CHANGED_DIMENSION);
         *     if (!player.wonGame) {
         *         player.wonGame = true;
         *         player.connection.send(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.WIN_GAME, seenCredits ? 0.0F : 1.0F));
         *     }
         * #elseif version >= 1.17
         *     player.isChangingDimension = true;
         *     player.decouple();
         *     player.getWorldServer().a(player, Entity$RemovalReason.CHANGED_DIMENSION);
         *     if (!player.wonGame) {
         *         player.wonGame = true;
         *         player.connection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.WIN_GAME, seenCredits ? 0.0F : 1.0F));
         *     }
         * #else
         *     player.worldChangeInvuln = true;
         *     player.decouple();
         *     player.getWorldServer().removePlayer(player);
         *     if (!player.viewingCredits) {
         *         player.viewingCredits = true;
         *   #if version >= 1.16
         *         player.playerConnection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.e, seenCredits ? 0.0F : 1.0F));
         *   #else
         *         player.playerConnection.sendPacket(new PacketPlayOutGameStateChange(4, seenCredits ? 0.0F : 1.0F));
         *   #endif
         *     }
         * #endif
         * }
         */
        @Template.Generated("%SHOW_END_CREDITS%")
        public abstract void showEndCredits(Object entityPlayerRaw, boolean seenCredits);

        /*
         * <IS_MAIN_WORLD>
         * public static boolean isMainEndWorld(org.bukkit.World world) {
         *     World world = ((org.bukkit.craftbukkit.CraftWorld) world).getHandle();
         * #if version >= 1.18
         *     return world.dimension() == World.END;
         * #elseif version >= 1.17
         *     return world.getDimensionKey() == World.END;
         * #elseif version >= 1.16
         *     return world.getDimensionKey() == World.THE_END;
         * #else
         *     return world.getWorldProvider().getDimensionManager().getType() == DimensionManager.THE_END;
         * #endif
         * }
         */
        @Template.Generated("%IS_MAIN_WORLD%")
        public abstract boolean isMainEndWorld(World world);

        /*
         * <FIND_NETHER_PORTAL>
         * public static org.bukkit.block.Block findNetherPortal(org.bukkit.block.Block startBlock, int radius) {
         *     WorldServer world = ((org.bukkit.craftbukkit.CraftWorld) startBlock.getWorld()).getHandle();
         *     BlockPosition blockposition = new BlockPosition(startBlock.getX(), startBlock.getY(), startBlock.getZ());
         *     PortalTravelAgent agent = new PortalTravelAgent(world);
         * #if version >= 1.16.2
         *   #if version >= 1.18
         *     java.util.Optional opt_result = agent.findPortalAround(blockposition, world.getWorldBorder(), radius);
         *   #else
         *     java.util.Optional opt_result = agent.findPortal(blockposition, radius);
         *   #endif
         *     if (!opt_result.isPresent()) {
         *         return null;
         *     }
         *     net.minecraft.BlockUtil$Rectangle result = (net.minecraft.BlockUtil$Rectangle) opt_result.get();
         *   #if version >= 1.17
         *     return startBlock.getWorld().getBlockAt(result.minCorner.getX(), result.minCorner.getY(), result.minCorner.getZ());
         *   #else
         *     return startBlock.getWorld().getBlockAt(result.origin.getX(), result.origin.getY(), result.origin.getZ());
         *   #endif
         * #else
         *   #if version >= 1.15.2
         *     ShapeDetector$Shape result = agent.findPortal(blockposition, Vec3D.a, EnumDirection.NORTH, 0.5, 1.0, true, radius);
         *   #else
         *     ShapeDetector$Shape result = agent.a(blockposition, Vec3D.a, EnumDirection.NORTH, 0.5, 1.0, true);
         *   #endif
         *     if (result == null) {
         *         return null;
         *     }
         *     return startBlock.getWorld().getBlockAt(MathHelper.floor(result.position.x),
         *                                             MathHelper.floor(result.position.y),
         *                                             MathHelper.floor(result.position.z));
         * #endif
         * }
         */
        @Template.Generated("%FIND_NETHER_PORTAL%")
        public abstract Block findNetherPortal(Block startBlock, int radius);

        /*
         * <CREATE_NETHER_PORTAL>
         * public static void createNetherPortal(org.bukkit.block.Block startBlock, org.bukkit.block.BlockFace orientation, Object entityInitiator, Object dummyEntity, int createRadius) {
         *     WorldServer world = ((org.bukkit.craftbukkit.CraftWorld) startBlock.getWorld()).getHandle();
         *     BlockPosition blockposition = new BlockPosition(startBlock.getX(), startBlock.getY(), startBlock.getZ());
         *     PortalTravelAgent agent = new PortalTravelAgent(world);
         *     Entity initiator = (Entity) entityInitiator;
         * 
         * #if version >= 1.16.2
         *     // Orientation of the portal, if set, use random if invalid/null/self
         *     EnumDirection$EnumAxis axis;
         *     if (orientation == org.bukkit.block.BlockFace.NORTH || orientation == org.bukkit.block.BlockFace.SOUTH) {
         *         axis = EnumDirection$EnumAxis.X;
         *     } else if (orientation == org.bukkit.block.BlockFace.EAST || orientation == org.bukkit.block.BlockFace.WEST) {
         *         axis = EnumDirection$EnumAxis.Z;
         *     } else {
         *         axis = (Math.random() < 0.5) ? EnumDirection$EnumAxis.Z : EnumDirection$EnumAxis.X;
         *     }
         *     agent.createPortal(blockposition, axis, initiator, createRadius);
         * #elseif version >= 1.16
         *     // Use default entity if null, set it up properly
         *     if (initiator == null) {
         *         initiator = (Entity) dummyEntity;
         *         #require net.minecraft.world.entity.Entity private Vec3D loc;
         *         #require net.minecraft.world.entity.Entity private BlockPosition locBlock;
         *         initiator#loc = new Vec3D((double) startBlock.getX()+0.5, (double) startBlock.getY(), (double) startBlock.getZ()+0.5);
         *         initiator#locBlock = blockposition;
         *     }
         *     agent.createPortal(initiator, blockposition, createRadius);
         * #elseif version >= 1.15.2
         *     // Use default entity if null, set it up properly
         *     if (initiator == null) {
         *         initiator = (Entity) dummyEntity;
         *         initiator.setPositionRaw((double) startBlock.getX()+0.5, (double) startBlock.getY(), (double) startBlock.getZ()+0.5);
         *     }
         *     agent.createPortal(initiator, blockposition, createRadius);
         * #else
         *     // No blockposition/radius args, use initiator entity at all times to pass coordinates
         *     initiator = (Entity) dummyEntity;
         *     initiator.setPositionRaw((double) startBlock.getX()+0.5, (double) startBlock.getY(), (double) startBlock.getZ()+0.5);
         *     agent.createPortal(initiator);
         * #endif
         * }
         */
        @Template.Generated("%CREATE_NETHER_PORTAL%")
        public abstract void createNetherPortal(Block startBlock, BlockFace orientation, Object entityInitiator, Object dummyEntity, int createRadius);

        /*
         * <STORE_NETHER_PORTAL>
         * public static void storeNetherPortal(org.bukkit.block.Block startBlock) {
         *     WorldServer world = ((org.bukkit.craftbukkit.CraftWorld) startBlock.getWorld()).getHandle();
         *     BlockPosition blockposition = new BlockPosition(startBlock.getX(), startBlock.getY(), startBlock.getZ());
         * #if version >= 1.18
         *     VillagePlace villageplace = world.getPoiManager();
         *     villageplace.add(blockposition, VillagePlaceType.NETHER_PORTAL);
         * #elseif version >= 1.17
         *     VillagePlace villageplace = world.A();
         *     villageplace.a(blockposition, VillagePlaceType.NETHER_PORTAL);
         * #elseif version >= 1.16.2
         *     VillagePlace villageplace = world.y();
         *     villageplace.a(blockposition, VillagePlaceType.v);
         * #elseif version >= 1.16
         *     VillagePlace villageplace = world.x();
         *     villageplace.a(blockposition, VillagePlaceType.v);
         * #elseif version >= 1.15
         *     VillagePlace villageplace = world.B();
         *     villageplace.a(blockposition, VillagePlaceType.u);
         * #endif
         * }
         */
        @Template.Generated("%STORE_NETHER_PORTAL%")
        public abstract void storeNetherPortal(Block startBlock);

        /*
         * <FIND_END_PLATFORM>
         * public static org.bukkit.block.Block findEndPlatform(org.bukkit.World bworld) {
         *     WorldServer world = ((org.bukkit.craftbukkit.CraftWorld) bworld).getHandle();
         * #if version >= 1.17
         *     BlockPosition platformPos = WorldServer.END_SPAWN_POINT;
         * #elseif version >= 1.16
         *     BlockPosition platformPos = WorldServer.a;
         * #else
         *     BlockPosition platformPos = WorldProviderTheEnd.f;
         * #endif
         *     int i = platformPos.getX();
         *     int j = platformPos.getY() - 1;
         *     int k = platformPos.getZ();
         *     BlockPosition$MutableBlockPosition pos = new BlockPosition$MutableBlockPosition();
         *     byte b0 = 1;
         *     byte b1 = 0;
         *     for (int l = -2; l <= 2; ++l) {
         *         for (int i1 = -2; i1 <= 2; ++i1) {
         *             for (int j1 = -1; j1 < 3; ++j1) {
         *                 int k1 = i + i1 * b0 + l * b1;
         *                 int l1 = j + j1;
         *                 int i2 = k + i1 * b1 - l * b0;
         *                 boolean flag = j1 < 0;
         * #if version >= 1.18
         *                 pos.set(k1, l1, i2);
         *                 if (world.getBlockState(pos).getBlock() != (flag ? Blocks.OBSIDIAN : Blocks.AIR)) {
         * #else
         *                 pos.d(k1, l1, i2);
         *                 if (world.getType(pos).getBlock() != (flag ? Blocks.OBSIDIAN : Blocks.AIR)) {
         * #endif
         *                     return null;
         *                 }
         *             }
         *         }
         *     }
         *     return bworld.getBlockAt(platformPos.getX(), platformPos.getY()-2, platformPos.getZ());
         * }
         */
        @Template.Generated("%FIND_END_PLATFORM%")
        public abstract Block findEndPlatform(World world);

        /*
         * <CREATE_END_PLATFORM>
         * public static org.bukkit.block.Block createEndPlatform(org.bukkit.World bworld, Object entityInitiatorRaw) {
         *     WorldServer world = ((org.bukkit.craftbukkit.CraftWorld) bworld).getHandle();
         *     Entity entityInitiator = (Entity) entityInitiatorRaw;
         * 
         * #if version >= 1.18
         *     BlockPosition platformPos = WorldServer.END_SPAWN_POINT;
         *     WorldServer.makeObsidianPlatform(world, entityInitiator);
         * #elseif version >= 1.17
         *     BlockPosition platformPos = WorldServer.END_SPAWN_POINT;
         *     WorldServer.a(world, entityInitiator);
         * #elseif version >= 1.16
         *     BlockPosition platformPos = WorldServer.a;
         *     WorldServer.a(world, entityInitiator);
         * #else
         *     BlockPosition platformPos = WorldProviderTheEnd.f;
         * 
         *     // Code is embedded deep inside EntityPlayer.a(DimensionManager, TeleportCause)
         *     int i = platformPos.getX();
         *     int j = platformPos.getY() - 1;
         *     int k = platformPos.getZ();
         *     boolean flag = true;
         *     boolean flag1 = false;
         *     org.bukkit.craftbukkit.util.BlockStateListPopulator blockList = new org.bukkit.craftbukkit.util.BlockStateListPopulator(world);
         *     BlockPosition$MutableBlockPosition blockposition = new BlockPosition$MutableBlockPosition();
         *     for (int l = -2; l <= 2; ++l) {
         *         for (int i1 = -2; i1 <= 2; ++i1) {
         *             for (int j1 = -1; j1 < 3; ++j1) {
         *                 int k1 = i + i1 * 1 + l * 0;
         *                 int l1 = j + j1;
         *                 int i2 = k + i1 * 0 - l * 1;
         *                 boolean flag2 = j1 < 0;
         *                 blockposition.d(k1, l1, i2);
         *                 blockList.setTypeAndData(blockposition, flag2 ? Blocks.OBSIDIAN.getBlockData() : Blocks.AIR.getBlockData(), 3);
         *             }
         *         }
         *     }
         *     // Event handling
         *     org.bukkit.event.world.PortalCreateEvent portalEvent = new org.bukkit.event.world.PortalCreateEvent(blockList.getList(), bworld, (entityInitiator == null) ? null : entityInitiator.getBukkitEntity(), org.bukkit.event.world.PortalCreateEvent$CreateReason.END_PLATFORM);
         *     world.getServer().getPluginManager().callEvent(portalEvent);
         *     if (!portalEvent.isCancelled()) {
         *         blockList.updateList();
         *     }
         * 
         * #endif
         *     return bworld.getBlockAt(platformPos.getX(), platformPos.getY()-2, platformPos.getZ());
         * }
         */
        @Template.Generated("%CREATE_END_PLATFORM%")
        public abstract Block createEndPlatform(World world, Object entityInitiator);
    }
}
