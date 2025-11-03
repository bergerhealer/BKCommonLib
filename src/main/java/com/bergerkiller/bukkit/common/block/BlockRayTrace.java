package com.bergerkiller.bukkit.common.block;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.MovingObjectPositionHandle;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Stores the parameters for performing a block ray tracing operation.
 */
public final class BlockRayTrace {
    private final World world;
    private final Vector from;
    private final Vector to;

    /**
     * Creates a new ray trace from a starting eye Location of a living entity into where
     * the eye looks, with a maximum distance of the entity's reach.
     *
     * @param entity LivingEntity
     * @return BlockRayTrace
     */
    public static BlockRayTrace fromEyeOf(LivingEntity entity) {
        //TODO: API?
        final double reach = (entity instanceof Player && ((Player) entity).getGameMode() == GameMode.CREATIVE)
                ? 6.0 : 5.0;

        return fromEye(entity.getEyeLocation(), reach);
    }

    /**
     * Creates a new ray trace from a starting eye Location into where the eye looks,
     * with a maximum distance.
     *
     * @param eyeLocation World, position and direction. ({@link Player#getEyeLocation()})
     * @param maxDistance Maximum distance (reach)
     * @return BlockRayTrace
     */
    public static BlockRayTrace fromEye(Location eyeLocation, double maxDistance) {
        return fromInto(eyeLocation.getWorld(), eyeLocation.toVector(), eyeLocation.getDirection(), maxDistance);
    }

    /**
     * Creates a new ray trace from a starting position into a direction, with a maximum distance
     *
     * @param world World
     * @param from Starting position in world x/y/z
     * @param direction Direction vector (normalized)
     * @param maxDistance Maximum distance
     * @return BlockRayTrace
     */
    public static BlockRayTrace fromInto(World world, Vector from, Vector direction, double maxDistance) {
        Vector to = direction.clone().multiply(maxDistance).add(from);
        return new BlockRayTrace(world, from, to);
    }

    /**
     * Creates a new ray trace between two positions on a world
     *
     * @param world World
     * @param from Starting position
     * @param to Stopping position
     * @return BlockRayTrace
     */
    public static BlockRayTrace between(World world, Vector from, Vector to) {
        return new BlockRayTrace(world, from, to);
    }

    private BlockRayTrace(World world, Vector from, Vector to) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }
        if (from == null) {
            throw new IllegalArgumentException("From start position cannot be null");
        }
        if (to == null) {
            throw new IllegalArgumentException("To end vector cannot be null");
        }
        this.world = world;
        this.from = from;
        this.to = to;
    }

    /**
     * Gets the world in which is ray traced
     *
     * @return World
     */
    public World getWorld() {
        return world;
    }

    /**
     * Gets the starting coordinates of the ray trace
     *
     * @return Start position in world x/y/z
     */
    public Vector getStartPosition() {
        return from;
    }

    /**
     * Gets the maximum end coordinates of the ray trace. Beyond this no more
     * blocks can be 'hit'.
     *
     * @return End position in world x/y/z
     */
    public Vector getEndPosition() {
        return to;
    }

    /**
     * Gets the direction in which is ray traced as a normalized Vector.
     *
     * @return Ray trace direction vector
     */
    public Vector getDirection() {
        Vector direction = to.clone().subtract(from);
        direction.normalize();
        if (Double.isNaN(direction.getX())) {
            direction.setX(0.0);
            direction.setY(-1.0);
            direction.setZ(0.0);
        }
        return direction;
    }

    /**
     * Gets the maximum distance to ray trace from the {@link #getStartPosition()}
     *
     * @return Ray trace maximum distance
     */
    public double getMaximumDistance() {
        return from.distance(to);
    }

    /**
     * Performs the ray tracing operation
     *
     * @return HitResult if a block was hit by the ray, or <i>null</i> if no block
     *         was hit (air)
     */
    public HitResult rayTrace() {
        MovingObjectPositionHandle mop = WorldHandle.fromBukkit(world).rayTrace(from, to);
        return mop == null ? null : new HitResult(this, mop);
    }

    /**
     * The result of a successful ray tracing operation
     */
    public static final class HitResult {
        private final BlockRayTrace rayTrace;
        private final Vector absolutePosition;
        private final BlockFace hitFace;
        private final Block block;

        private HitResult(BlockRayTrace rayTrace, MovingObjectPositionHandle mop) {
            this.rayTrace = rayTrace;
            this.absolutePosition = mop.getPos();
            this.hitFace = mop.getDirection();

            // Move an infinitely small amount forwards so that we are properly inside the block we hit
            // Avoids the common issue of getting air depending on the direction axis
            Vector posBeyondBlock = this.absolutePosition.clone();
            MathUtil.nextForward(posBeyondBlock, rayTrace.getDirection());
            this.block = rayTrace.getWorld().getBlockAt(posBeyondBlock.getBlockX(),
                    posBeyondBlock.getBlockY(), posBeyondBlock.getBlockZ());
        }

        /**
         * Gets the original ray trace parameters that created this result
         *
         * @return BlockRayTrace
         */
        public BlockRayTrace getRayTrace() {
            return rayTrace;
        }

        /**
         * Gets the block that was hit by the ray
         *
         * @return Hit block
         */
        public Block getHitBlock() {
            return block;
        }

        /**
         * Gets the BlockFace of the hit block that was hit by the ray
         *
         * @return Hit face
         */
        public BlockFace getHitFace() {
            return hitFace;
        }

        /**
         * Gets the block-relative coordinates where the ray has hit
         *
         * @return Position on the block where the ray hit
         */
        public Vector getHitPosition() {
            Block block = this.block;
            Vector pos = this.absolutePosition;
            return new Vector(
                    pos.getX() - block.getX(),
                    pos.getY() - block.getY(),
                    pos.getZ() - block.getZ()
            );
        }

        /**
         * Gets the absolute position in World x/y/z that hit the block. This is
         * the position on the block's bounding box that the ray hit.
         *
         * @return Absolute world coordinates x/y/z of the hit position on the hit block
         */
        public Vector getAbsolutePosition() {
            return absolutePosition;
        }
    }
}
