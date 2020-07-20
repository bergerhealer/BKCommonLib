package com.bergerkiller.bukkit.common.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.internal.CommonTrigMath;

/**
 * Multiple Math utilities to compare and calculate using Vectors and raw values
 */
public class MathUtil {

    private static final int CHUNK_BITS = 4;
    private static final int CHUNK_VALUES = 16;
    public static final float DEGTORAD = 0.017453293F;
    public static final float RADTODEG = 57.29577951F;
    public static final double HALFROOTOFTWO = 0.707106781;

    public static double lengthSquared(double... values) {
        double rval = 0;
        for (double value : values) {
            rval += value * value;
        }
        return rval;
    }

    public static double length(double... values) {
        return Math.sqrt(lengthSquared(values));
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return length(x1 - x2, y1 - y2);
    }

    public static double distanceSquared(double x1, double y1, double x2, double y2) {
        return lengthSquared(x1 - x2, y1 - y2);
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return length(x1 - x2, y1 - y2, z1 - z2);
    }

    public static double distanceSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
        return lengthSquared(x1 - x2, y1 - y2, z1 - z2);
    }

    /**
     * Gets a percentage and round it with a cusotm amound of decimals
     *
     * @param subtotal to get percentags for
     * @param total to use as 100% value
     * @param decimals to round with
     * @return Percentage for subtotal with custom decimals
     */
    public static double getPercentage(int subtotal, int total, int decimals) {
        return round(getPercentage(subtotal, total), decimals);
    }

    /**
     * Gets a percentags of 2 values
     *
     * @param subtotal to get percentage for
     * @param total to sue as 100% value
     * @return percentage
     */
    public static double getPercentage(int subtotal, int total) {
        return ((float) subtotal / (float) total) * 100;
    }

    /**
     * Gets the angle difference between two angles
     *
     * @param angle1
     * @param angle2
     * @return angle difference
     */
    public static int getAngleDifference(int angle1, int angle2) {
        return Math.abs(wrapAngle(angle1 - angle2));
    }

    /**
     * Gets the angle difference between two angles
     *
     * @param angle1
     * @param angle2
     * @return angle difference
     */
    public static float getAngleDifference(float angle1, float angle2) {
        return Math.abs(wrapAngle(angle1 - angle2));
    }

    /**
     * Gets the angle difference between two angles
     *
     * @param angle1
     * @param angle2
     * @return angle difference
     */
    public static double getAngleDifference(double angle1, double angle2) {
        return Math.abs(wrapAngle(angle1 - angle2));
    }

    /**
     * Wraps the angle to be between -180 and 180 degrees
     *
     * @param angle to wrap
     * @return [-180 > angle >= 180]
     */
    public static int wrapAngle(int angle) {
        int wrappedAngle = angle;
        while (wrappedAngle <= -180) {
            wrappedAngle += 360;
        }
        while (wrappedAngle > 180) {
            wrappedAngle -= 360;
        }
        return wrappedAngle;
    }

    /**
     * Wraps the angle to be between -180 and 180 degrees
     *
     * @param angle to wrap
     * @return [-180 > angle >= 180]
     */
    public static float wrapAngle(float angle) {
        float wrappedAngle = angle;
        while (wrappedAngle <= -180f) {
            wrappedAngle += 360f;
        }
        while (wrappedAngle > 180f) {
            wrappedAngle -= 360f;
        }
        return wrappedAngle;
    }

    /**
     * Wraps the angle to be between -180 and 180 degrees
     *
     * @param angle to wrap
     * @return [-180 > angle >= 180]
     */
    public static double wrapAngle(double angle) {
        double wrappedAngle = angle;
        while (wrappedAngle <= -180.0) {
            wrappedAngle += 360.0;
        }
        while (wrappedAngle > 180.0) {
            wrappedAngle -= 360.0;
        }
        return wrappedAngle;
    }

    /**
     * Normalizes a 2D-vector to be the length of another 2D-vector<br>
     * Calculates the normalization factor to multiply the input vector with, to
     * get the requested length
     *
     * @param x axis of the vector
     * @param z axis of the vector
     * @param reqx axis of the length vector
     * @param reqz axis of the length vector
     * @return the normalization factor
     */
    public static double normalize(double x, double z, double reqx, double reqz) {
        return Math.sqrt(lengthSquared(reqx, reqz) / lengthSquared(x, z));
    }

    public static float getLookAtYaw(Entity loc, Entity lookat) {
        return getLookAtYaw(loc.getLocation(), lookat.getLocation());
    }

    public static float getLookAtYaw(Block loc, Block lookat) {
        return getLookAtYaw(loc.getLocation(), lookat.getLocation());
    }

    public static float getLookAtYaw(Location loc, Location lookat) {
        return getLookAtYaw(lookat.getX() - loc.getX(), lookat.getZ() - loc.getZ());
    }

    public static float getLookAtYaw(Vector motion) {
        return getLookAtYaw(motion.getX(), motion.getZ());
    }

    /**
     * Gets the horizontal look-at angle in degrees to look into the
     * 2D-direction specified
     *
     * @param dx axis of the direction
     * @param dz axis of the direction
     * @return the angle in degrees
     */
    public static float getLookAtYaw(double dx, double dz) {
        return atan2(dz, dx) - 180f;
    }

    /**
     * Gets the pitch angle in degrees to look into the direction specified
     *
     * @param dX axis of the direction
     * @param dY axis of the direction
     * @param dZ axis of the direction
     * @return look-at angle in degrees
     */
    public static float getLookAtPitch(double dX, double dY, double dZ) {
        return getLookAtPitch(dY, length(dX, dZ));
    }

    /**
     * Gets the pitch angle in degrees to look into the direction specified
     *
     * @param dY axis of the direction
     * @param dXZ axis of the direction (length of x and z)
     * @return look-at angle in degrees
     */
    public static float getLookAtPitch(double dY, double dXZ) {
        return -atan(dY / dXZ);
    }

    /**
     * Gets the inverse tangent of the value in degrees
     *
     * @param value
     * @return inverse tangent angle in degrees
     */
    public static float atan(double value) {
        return (float) Math.toDegrees(CommonTrigMath.atan(value));
    }

    /**
     * Gets the inverse tangent angle in degrees of the rectangle vector
     *
     * @param y axis
     * @param x axis
     * @return inverse tangent 2 angle in degrees
     */
    public static float atan2(double y, double x) {
        return (float) Math.toDegrees(CommonTrigMath.atan2(y, x));
    }

    /**
     * Gets the floor long value from a double value
     * 
     * @param value to get the floor of
     * @return floor value
     */
    public static long longFloor(double value) {
        long l = (long) value;
        return value < l ? l - 1L : l;
    }

    /**
     * Gets the floor integer value from a double value
     *
     * @param value to get the floor of
     * @return floor value
     */
    public static int floor(double value) {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }

    /**
     * Gets the floor integer value from a float value
     *
     * @param value to get the floor of
     * @return floor value
     */
    public static int floor(float value) {
        int i = (int) value;
        return value < (float) i ? i - 1 : i;
    }

    /**
     * Gets the ceiling integer value from a double value
     *
     * @param value to get the ceiling of
     * @return ceiling value
     */
    public static int ceil(double value) {
        return -floor(-value);
    }

    /**
     * Gets the ceiling integer value from a float value
     *
     * @param value to get the ceiling of
     * @return ceiling value
     */
    public static int ceil(float value) {
        return -floor(-value);
    }

    /**
     * Moves a Location into the yaw and pitch of the Location in the offset
     * specified
     *
     * @param loc to move
     * @param offset vector
     * @return Translated Location
     */
    public static Location move(Location loc, Vector offset) {
        return move(loc, offset.getX(), offset.getY(), offset.getZ());
    }

    /**
     * Moves a Location into the yaw and pitch of the Location in the offset
     * specified
     *
     * @param loc to move
     * @param dx offset
     * @param dy offset
     * @param dz offset
     * @return Translated Location
     */
    public static Location move(Location loc, double dx, double dy, double dz) {
        Vector off = rotate(loc.getYaw(), loc.getPitch(), dx, dy, dz);
        double x = loc.getX() + off.getX();
        double y = loc.getY() + off.getY();
        double z = loc.getZ() + off.getZ();
        return new Location(loc.getWorld(), x, y, z, loc.getYaw(), loc.getPitch());
    }

    /**
     * Rotates a 3D-vector using yaw and pitch
     *
     * @param yaw angle in degrees
     * @param pitch angle in degrees
     * @param vector to rotate
     * @return Vector rotated by the angle (new instance)
     */
    public static Vector rotate(float yaw, float pitch, Vector vector) {
        return rotate(yaw, pitch, vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Rotates a 3D-vector using yaw and pitch
     *
     * @param yaw angle in degrees
     * @param pitch angle in degrees
     * @param x axis of the vector
     * @param y axis of the vector
     * @param z axis of the vector
     * @return Vector rotated by the angle
     */
    public static Vector rotate(float yaw, float pitch, double x, double y, double z) {
        // Conversions found by (a lot of) testing
        double angle;
        angle = Math.toRadians((double) yaw);
        double sinyaw = Math.sin(angle);
        double cosyaw = Math.cos(angle);

        angle = Math.toRadians((double) pitch);
        double sinpitch = Math.sin(angle);
        double cospitch = Math.cos(angle);

        Vector vector = new Vector();
        vector.setX((x * sinyaw) - (y * cosyaw * sinpitch) - (z * cosyaw * cospitch));
        vector.setY((y * cospitch) - (z * sinpitch));
        vector.setZ(-(x * cosyaw) - (y * sinyaw * sinpitch) - (z * sinyaw * cospitch));
        return vector;
    }

    /**
     * Returns the floor modulus of the int arguments
     * 
     * @param x the dividend
     * @param y the divisor
     * @returnthe floor modulus x
     */
    public static int floorMod(int x, int y) {
        return Math.floorMod(x, y);
    }

    /**
     * Returns the floor modulus of the long arguments
     * 
     * @param x the dividend
     * @param y the divisor
     * @returnthe floor modulus x
     */
    public static long floorMod(long x, long y) {
        return Math.floorMod(x, y);
    }

    /**
     * Returns the floor division of the int arguments
     * @param x the dividend
     * @param y the divisor
     * @return floor division x
     */
    public static int floorDiv(int x, int y) {
        return Math.floorDiv(x, y);
    }

    /**
     * Returns the floor division of the long arguments
     * @param x the dividend
     * @param y the divisor
     * @return floor division x
     */
    public static long floorDiv(long x, long y) {
        return Math.floorDiv(x, y);
    }

    /**
     * Rounds the specified value to the amount of decimals specified
     *
     * @param value to round
     * @param decimals count
     * @return value round to the decimal count specified
     */
    public static double round(double value, int decimals) {
        double p = Math.pow(10, decimals);
        return Math.round(value * p) / p;
    }

    /**
     * Returns 0 if the value is not-a-number
     *
     * @param value to check
     * @return The value, or 0 if it is NaN
     */
    public static double fixNaN(double value) {
        return fixNaN(value, 0.0);
    }

    /**
     * Returns 0 if the value is not-a-number
     *
     * @param value to check
     * @return The value, or 0 if it is NaN
     */
    public static float fixNaN(float value) {
        return fixNaN(value, 0.0f);
    }

    /**
     * Returns the default if the value is not-a-number
     *
     * @param value to check
     * @param def value
     * @return The value, or the default if it is NaN
     */
    public static double fixNaN(double value, double def) {
        return Double.isNaN(value) ? def : value;
    }

    /**
     * Returns the default if the value is not-a-number
     *
     * @param value to check
     * @param def value
     * @return The value, or the default if it is NaN
     */
    public static float fixNaN(float value, float def) {
        return Float.isNaN(value) ? def : value;
    }

    /**
     * Converts a location value into a chunk coordinate
     *
     * @param loc to convert
     * @return chunk coordinate
     */
    public static int toChunk(double loc) {
        return floor(loc / (double) CHUNK_VALUES);
    }

    /**
     * Converts a location value into a chunk coordinate
     *
     * @param loc to convert
     * @return chunk coordinate
     */
    public static int toChunk(int loc) {
        return loc >> CHUNK_BITS;
    }

    public static double useOld(double oldvalue, double newvalue, double peruseold) {
        return oldvalue + (peruseold * (newvalue - oldvalue));
    }

    public static double lerp(double d1, double d2, double stage) {
        if (Double.isNaN(stage) || stage > 1) {
            return d2;
        } else if (stage < 0) {
            return d1;
        } else {
            return d1 * (1 - stage) + d2 * stage;
        }
    }

    public static Vector lerp(Vector vec1, Vector vec2, double stage) {
        Vector newvec = new Vector();
        newvec.setX(lerp(vec1.getX(), vec2.getX(), stage));
        newvec.setY(lerp(vec1.getY(), vec2.getY(), stage));
        newvec.setZ(lerp(vec1.getZ(), vec2.getZ(), stage));
        return newvec;
    }

    public static Location lerp(Location loc1, Location loc2, double stage) {
        Location newloc = new Location(loc1.getWorld(), 0, 0, 0);
        newloc.setX(lerp(loc1.getX(), loc2.getX(), stage));
        newloc.setY(lerp(loc1.getY(), loc2.getY(), stage));
        newloc.setZ(lerp(loc1.getZ(), loc2.getZ(), stage));
        newloc.setYaw((float) lerp(loc1.getYaw(), loc2.getYaw(), stage));
        newloc.setPitch((float) lerp(loc1.getPitch(), loc2.getPitch(), stage));
        return newloc;
    }

    /**
     * Checks whether one value is negative and the other positive, or opposite
     *
     * @param value1 to check
     * @param value2 to check
     * @return True if value1 is inverted from value2
     */
    public static boolean isInverted(double value1, double value2) {
        return (value1 > 0 && value2 < 0) || (value1 < 0 && value2 > 0);
    }

    /**
     * Gets the direction of yaw and pitch angles
     *
     * @param yaw angle in degrees
     * @param pitch angle in degrees
     * @return Direction Vector
     */
    public static Vector getDirection(float yaw, float pitch) {
        Vector vector = new Vector();
        double rotX = Math.toRadians((double) yaw);
        double rotY = Math.toRadians((double) pitch);
        vector.setY(-Math.sin(rotY));
        double h = Math.cos(rotY);
        vector.setX(-h * Math.sin(rotX));
        vector.setZ(h * Math.cos(rotX));
        return vector;
    }

    /**
     * Clamps the value between -limit and limit
     *
     * @param value to clamp
     * @param limit
     * @return value, -limit or limit
     */
    public static double clamp(double value, double limit) {
        return clamp(value, -limit, limit);
    }

    /**
     * Clamps the value between the min and max values
     *
     * @param value to clamp
     * @param min
     * @param max
     * @return value, min or max
     */
    public static double clamp(double value, double min, double max) {
        return value < min ? min : (value > max ? max : value);
    }

    /**
     * Clamps the value between -limit and limit
     *
     * @param value to clamp
     * @param limit
     * @return value, -limit or limit
     */
    public static float clamp(float value, float limit) {
        return clamp(value, -limit, limit);
    }

    /**
     * Clamps the value between the min and max values
     *
     * @param value to clamp
     * @param min
     * @param max
     * @return value, min or max
     */
    public static float clamp(float value, float min, float max) {
        return value < min ? min : (value > max ? max : value);
    }

    /**
     * Clamps the value between -limit and limit
     *
     * @param value to clamp
     * @param limit
     * @return value, -limit or limit
     */
    public static int clamp(int value, int limit) {
        return clamp(value, -limit, limit);
    }

    /**
     * Clamps the value between the min and max values
     *
     * @param value to clamp
     * @param min
     * @param max
     * @return value, min or max
     */
    public static int clamp(int value, int min, int max) {
        return value < min ? min : (value > max ? max : value);
    }

    /**
     * Clamps the value between -limit and limit
     *
     * @param value to clamp
     * @param limit
     * @return value, -limit or limit
     */
    public static long clamp(long value, long limit) {
        return clamp(value, -limit, limit);
    }

    /**
     * Clamps the value between the min and max values
     *
     * @param value to clamp
     * @param min
     * @param max
     * @return value, min or max
     */
    public static long clamp(long value, long min, long max) {
        return value < min ? min : (value > max ? max : value);
    }

    /**
     * Turns a value negative or keeps it positive based on a boolean input
     *
     * @param value to work with
     * @param negative - True to invert, False to keep the old value
     * @return the value or inverted (-value)
     */
    public static int invert(int value, boolean negative) {
        return negative ? -value : value;
    }

    /**
     * Turns a value negative or keeps it positive based on a boolean input
     *
     * @param value to work with
     * @param negative - True to invert, False to keep the old value
     * @return the value or inverted (-value)
     */
    public static float invert(float value, boolean negative) {
        return negative ? -value : value;
    }

    /**
     * Turns a value negative or keeps it positive based on a boolean input
     *
     * @param value to work with
     * @param negative - True to invert, False to keep the old value
     * @return the value or inverted (-value)
     */
    public static double invert(double value, boolean negative) {
        return negative ? -value : value;
    }

    /**
     * Merges two ints into a long
     *
     * @param msw integer
     * @param lsw integer
     * @return merged long value
     */
    public static long toLong(int msw, int lsw) {
        return ((long) msw << 32) + lsw - Integer.MIN_VALUE;
    }

    public static long longHashToLong(int msw, int lsw) {
        return ((long) msw << 32) + lsw - Integer.MIN_VALUE;
    }

    public static int longHashMsw(long key) {
        return (int) (key >> 32);
    }

    public static int longHashLsw(long key) {
        return (int) (key & 0xFFFFFFFF) + Integer.MIN_VALUE;
    }

    /**
     * Takes the most and least significant words of both keys, sums them together,
     * and produces a new key with the two words summed.
     * 
     * @param keyA
     * @param keyB
     * @return words of keyA and keyB summed, and turned back into a long
     */
    public static long longHashSumW(long keyA, long keyB) {
        long sum_msw = (keyA & 0xFFFFFFFF00000000L) + (keyB & 0xFFFFFFFF00000000L);
        long sum_lsw = (keyA & 0xFFFFFFFF) + (keyB & 0xFFFFFFFF);
        return sum_msw + (int) sum_lsw - Integer.MIN_VALUE;
    }

    /**
     * Shorthand equivalent of:<br>
     * longHashToLong(longHashMsw(a)+longHashMsw(b), longHashLsw(a)+longHashLsw(b))
     * 
     * @param key_a
     * @param key_b
     * @return key_a + key_b
     */
    public static long longHashAdd(long key_a, long key_b) {
        return key_a + key_b + Integer.MIN_VALUE;
    }

    public static void setVectorLength(Vector vector, double length) {
        setVectorLengthSquared(vector, Math.signum(length) * length * length);
    }

    public static void setVectorLengthSquared(Vector vector, double lengthsquared) {
        double vlength = vector.lengthSquared();
        if (Math.abs(vlength) > 0.0001) {
            if (lengthsquared < 0) {
                vector.multiply(-Math.sqrt(-lengthsquared / vlength));
            } else {
                vector.multiply(Math.sqrt(lengthsquared / vlength));
            }
        }
    }

    public static boolean isHeadingTo(BlockFace direction, Vector velocity) {
        return isHeadingTo(FaceUtil.faceToVector(direction), velocity);
    }

    public static boolean isHeadingTo(Location from, Location to, Vector velocity) {
        return isHeadingTo(new Vector(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ()), velocity);
    }

    public static boolean isHeadingTo(Vector offset, Vector velocity) {
        double dbefore = offset.lengthSquared();
        if (dbefore < 0.0001) {
            return true;
        }
        Vector clonedVelocity = velocity.clone();
        setVectorLengthSquared(clonedVelocity, dbefore);
        return dbefore > clonedVelocity.subtract(offset).lengthSquared();
    }

    /**
     * Calculates the normalization factor for a 3D vector.
     * Multiplying the input vector with this factor will turn it into a vector of unit length.
     * If the input vector is (0,0,0), Infinity is returned.
     * 
     * @param v
     * @return normalization factor
     */
    public static double getNormalizationFactor(Vector v) {
        return getNormalizationFactorLS(v.lengthSquared());
    }

    /**
     * Calculates the normalization factor for a 4D vector.
     * Multiplying the input vector with this factor will turn it into a vector of unit length.
     * If the input vector is (0,0,0,0), Infinity is returned.
     * 
     * @param x
     * @param y
     * @param z
     * @param w
     * @return normalization factor
     */
    public static double getNormalizationFactor(double x, double y, double z, double w) {
        return getNormalizationFactorLS(x * x + y * y + z * z + w * w);
    }

    /**
     * Calculates the normalization factor for a 3D vector.
     * Multiplying the input vector with this factor will turn it into a vector of unit length.
     * If the input vector is (0,0,0), Infinity is returned.
     * 
     * @param x
     * @param y
     * @param z
     * @return normalization factor
     */
    public static double getNormalizationFactor(double x, double y, double z) {
        return getNormalizationFactorLS(x * x + y * y + z * z);
    }

    /**
     * Calculates the normalization factor for a 2D vector.
     * Multiplying the input vector with this factor will turn it into a vector of unit length.
     * If the input vector is (0,0), Infinity is returned.
     * 
     * @param x
     * @param y
     * @return normalization factor
     */
    public static double getNormalizationFactor(double x, double y) {
        return getNormalizationFactorLS(x * x + y * y);
    }

    /**
     * Calculates the normalization factor for a squared length.
     * Multiplying the input values with this factor will turn it into a vector of unit length.
     * If the squared length is 0, Infinity is returned.
     * 
     * @param lengthSquared
     * @return normalization factor
     */
    public static double getNormalizationFactorLS(double lengthSquared) {
        // https://stackoverflow.com/a/12934750
        if (Math.abs(1.0 - lengthSquared) < 2.107342e-08) {
            return (2.0 / (1.0 + lengthSquared));
        } else {
            return 1.0 / Math.sqrt(lengthSquared);
        }
    }

    /**
     * Calculates the angle difference between two vectors in degrees
     * 
     * @param v0 first vector
     * @param v1 second vector
     * @return absolute angle difference in degrees
     */
    public static double getAngleDifference(Vector v0, Vector v1) {
        double dot = v0.dot(v1);
        dot *= MathUtil.getNormalizationFactor(v0);
        dot *= MathUtil.getNormalizationFactor(v1);
        return Math.toDegrees(Math.acos(dot));
    }

    /**
     * Sets the x, y and z coordinates of a Bukkit Vector
     * 
     * @param vector The vector to update
     * @param value The value to set vector to
     * @return input vector
     */
    public static Vector setVector(Vector vector, Vector value) {
        return vector.copy(value);
    }

    /**
     * Sets the x, y and z coordinates of a Bukkit Vector
     * 
     * @param vector The vector to update
     * @param x The new x-coordinate to set in vector
     * @param y The new y-coordinate to set in vector
     * @param z The new z-coordinate to set in vector
     * @return input vector
     */
    public static Vector setVector(Vector vector, double x, double y, double z) {
        vector.setX(x);
        vector.setY(y);
        vector.setZ(z);
        return vector;
    }

    /**
     * Adds the x, y and z coordinate values to the original coordinates of a vector.
     * The input vector is updated.
     * 
     * @param vector The vector to update
     * @param ax The value to add to the x-coordinate
     * @param ay The value to add to the y-coordinate
     * @param az The value to add to the z-coordinate
     * @return input vector
     */
    public static Vector addToVector(Vector vector, double ax, double ay, double az) {
        vector.setX(vector.getX() + ax);
        vector.setY(vector.getY() + ay);
        vector.setZ(vector.getZ() + az);
        return vector;
    }

    /**
     * Subtracts the x, y and z coordinate values from the original coordinates of a vector.
     * The input vector is updated.
     * 
     * @param vector The vector to update
     * @param sx The value to subtract from the x-coordinate
     * @param sy The value to subtract from the y-coordinate
     * @param sz The value to subtract from the z-coordinate
     * @return input vector
     */
    public static Vector subtractFromVector(Vector vector, double sx, double sy, double sz) {
        vector.setX(vector.getX() - sx);
        vector.setY(vector.getY() - sy);
        vector.setZ(vector.getZ() - sz);
        return vector;
    }

    /**
     * Multiplies the x, y and z coordinate values of the original coordinates of a vector.
     * The input vector is updated.
     * 
     * @param vector The vector to update
     * @param mx The value to multiply the x-coordinate with
     * @param my The value to multiply the y-coordinate with
     * @param mz The value to multiply the z-coordinate with
     * @return input vector
     */
    public static Vector multiplyVector(Vector vector, double mx, double my, double mz) {
        vector.setX(vector.getX() * mx);
        vector.setY(vector.getY() * my);
        vector.setZ(vector.getZ() * mz);
        return vector;
    }

    /**
     * Divides the x, y and z coordinate values of the original coordinates of a vector.
     * The input vector is updated.
     * 
     * @param vector The vector to update
     * @param mx The value to divide the x-coordinate with
     * @param my The value to divide the y-coordinate with
     * @param mz The value to divide the z-coordinate with
     * @return input vector
     */
    public static Vector divideVector(Vector vector, double mx, double my, double mz) {
        vector.setX(vector.getX() / mx);
        vector.setY(vector.getY() / my);
        vector.setZ(vector.getZ() / mz);
        return vector;
    }
}
