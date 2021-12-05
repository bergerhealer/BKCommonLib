package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Method;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;

/**
 * Handler for Minecraft 1.14. Almost identical to the 1.14.1
 * handler, except the PortalCreateEvent isn't used anywhere.
 */
class PortalHandler_1_14 extends PortalHandler {
    private final PortalTravelAgentHandle _pta = Template.Class.create(PortalTravelAgentHandle.class, Common.TEMPLATE_RESOLVER);

    /**
     * Used inside generated code to produce a valid Entity instance,
     */
    private final Object _dummyEntityInstance;

    public PortalHandler_1_14() {
        ClassInterceptor interceptor = new ClassInterceptor() {
            @Override
            protected Invoker<?> getCallback(Method method) {
                if (method.getName().equals("getBukkitEntity")) {
                    return (inst, args) -> null;
                }
                return null;
            }
        };
        _dummyEntityInstance = interceptor.createInstance(EntityPlayerHandle.T.getType());
    }

    @Override
    public void enable(CommonPlugin plugin) {
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
        return _pta.findNetherPortal(startBlock);
    }

    @Override
    public Block createNetherPortal(Block startBlock, BlockFace orientation, Entity initiator) {
        if (_pta.createNetherPortal(startBlock, _dummyEntityInstance)) {
            return _pta.findNetherPortal(startBlock);
        } else {
            return null;
        }
    }

    @Override
    public void markNetherPortal(Block netherPortalBlock) {
        // No portals are stored on 1.14 and before
    }

    @Override
    public Block findEndPlatform(World world) {
        return _pta.findEndPlatform(world);
    }

    @Override
    public Block createEndPlatform(World world, Entity initiator) {
        return _pta.createEndPlatform(world);
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
    @Template.Import("net.minecraft.core.EnumDirection")
    @Template.Import("net.minecraft.server.level.EntityPlayer")
    @Template.Import("net.minecraft.server.level.WorldServer")
    @Template.Import("net.minecraft.network.protocol.game.PacketPlayOutGameStateChange")
    @Template.Import("net.minecraft.world.entity.Entity")
    @Template.Import("net.minecraft.world.level.block.state.pattern.ShapeDetector")
    @Template.Import("net.minecraft.world.level.dimension.DimensionManager")
    @Template.InstanceType("net.minecraft.server.PortalTravelAgent")
    public static abstract class PortalTravelAgentHandle extends Template.Class<Template.Handle> {

        /* 
         * <SHOW_END_CREDITS>
         * public static void showEndCredits(Object entityPlayerRaw, boolean seenCredits) {
         *     EntityPlayer player = (EntityPlayer) entityPlayerRaw;
         *     player.worldChangeInvuln = true;
         *     player.decouple();
         *     player.getWorldServer().removePlayer(player);
         *     if (!player.viewingCredits) {
         *         player.viewingCredits = true;
         *         player.playerConnection.sendPacket(new PacketPlayOutGameStateChange(4, seenCredits ? 0.0F : 1.0F));
         *     }
         * }
         */
        @Template.Generated("%SHOW_END_CREDITS%")
        public abstract void showEndCredits(Object entityPlayerRaw, boolean seenCredits);

        /*
         * <IS_MAIN_END_WORLD>
         * public static boolean isMainEndWorld(org.bukkit.World world) {
         *     WorldServer world = ((org.bukkit.craftbukkit.CraftWorld) world).getHandle();
         *     return world.getWorldProvider().getDimensionManager().getType() == DimensionManager.THE_END;
         * }
         */
        @Template.Generated("%IS_MAIN_END_WORLD%")
        public abstract boolean isMainEndWorld(World world);

        /*
         * <FIND_NETHER_PORTAL>
         * public static org.bukkit.block.Block findNetherPortal(org.bukkit.block.Block startBlock) {
         *     WorldServer world = ((org.bukkit.craftbukkit.CraftWorld) startBlock.getWorld()).getHandle();
         *     BlockPosition blockposition = new BlockPosition(startBlock.getX(), startBlock.getY(), startBlock.getZ());
         *     PortalTravelAgent agent = new PortalTravelAgent(world);
         *     ShapeDetector$Shape result = agent.a(blockposition, Vec3D.a, EnumDirection.NORTH, 0.5, 1.0, true);
         *     if (result == null) {
         *         return null;
         *     }
         *     return startBlock.getWorld().getBlockAt(MathHelper.floor(result.position.x),
         *                                             MathHelper.floor(result.position.y),
         *                                             MathHelper.floor(result.position.z));
         * }
         */
        @Template.Generated("%FIND_NETHER_PORTAL%")
        public abstract Block findNetherPortal(Block startBlock);

        /*
         * <CREATE_NETHER_PORTAL>
         * public static void createNetherPortal(org.bukkit.block.Block startBlock, Object dummyEntity) {
         *     WorldServer world = ((org.bukkit.craftbukkit.CraftWorld) startBlock.getWorld()).getHandle();
         *     PortalTravelAgent agent = new PortalTravelAgent(world);
         *     Entity dummy = (Entity) dummyEntity;
         *     dummy.setPositionRaw((double) startBlock.getX()+0.5, (double) startBlock.getY(), (double) startBlock.getZ()+0.5);
         *     return agent.a(dummy);
         * }
         */
        @Template.Generated("%CREATE_NETHER_PORTAL%")
        public abstract boolean createNetherPortal(Block startBlock, Object dummyEntity);

        /*
         * <FIND_END_PLATFORM>
         * public static org.bukkit.block.Block findEndPlatform(org.bukkit.World bworld) {
         *     WorldServer world = ((org.bukkit.craftbukkit.CraftWorld) bworld).getHandle();
         *     BlockPosition platformPos = WorldProviderTheEnd.f;
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
         *                 pos.d(k1, l1, i2);
         *                 if (world.getType(pos).getBlock() != (flag ? Blocks.OBSIDIAN : Blocks.AIR)) {
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
         * public static org.bukkit.block.Block createEndPlatform(org.bukkit.World bworld) {
         *     WorldServer world = ((org.bukkit.craftbukkit.CraftWorld) bworld).getHandle();
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
         *                 world.setTypeUpdate(blockposition, flag2 ? Blocks.OBSIDIAN.getBlockData() : Blocks.AIR.getBlockData());
         *             }
         *         }
         *     }
         * 
         *     return bworld.getBlockAt(platformPos.getX(), platformPos.getY()-2, platformPos.getZ());
         * }
         */
        @Template.Generated("%CREATE_END_PLATFORM%")
        public abstract Block createEndPlatform(World world);
    }
}
