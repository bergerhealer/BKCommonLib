package com.bergerkiller.bukkit.common.utils;

import net.minecraft.server.MathHelper;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class MathUtil {
	public static final float DEGTORAD = 0.017453293F;
	public static final float RADTODEG = 57.29577951F;
	public static final double halfRootOfTwo = 0.707106781;
	
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
	
	public static float getAngleDifference(float angle1, float angle2) {
        return Math.abs(normalAngle(angle1 - angle2));
	}
	public static float normalAngle(float angle) {
        while (angle <= -180) angle += 360;
        while (angle > 180) angle -= 360;
        return angle;
	}
	public static double normalize(double x, double z, double reqx, double reqz) {
		return Math.sqrt(lengthSquared(reqx, reqz) / lengthSquared(x, z));
	}
	public static float getLookAtYaw(net.minecraft.server.Entity loc, net.minecraft.server.Entity lookat) {
		return getLookAtYaw(loc.getBukkitEntity(), lookat.getBukkitEntity());
	}
	public static float getLookAtYaw(Entity loc, Entity lookat) {
		return getLookAtYaw(loc.getLocation(), lookat.getLocation());
	}
	public static float getLookAtYaw(Block loc, Block lookat) {
		return getLookAtYaw(loc.getLocation(), lookat.getLocation());
	}
	public static float getLookAtYaw(Location loc, Location lookat) {
        // Values of change in distance (make it relative)
        return getLookAtYaw(lookat.getX() - loc.getX(), lookat.getZ() - loc.getZ());
	}
	public static float getLookAtYaw(Vector motion) {
		return getLookAtYaw(motion.getX(), motion.getZ());
	}
	public static float getLookAtYaw(double dx, double dz) {
        float yaw = 0;
        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
            	yaw = 270;
            } else {
                yaw = 90;
            }
            yaw -= atan(dz / dx);
        } else if (dz < 0) {
        	yaw = 180;
        }
        return -yaw - 90;
	}
	public static float getLookAtPitch(double motX, double motY, double motZ) {
		return getLookAtPitch(motY, length(motX, motZ));
	}
	public static float getLookAtPitch(double motY, double motXZ) {
		return -atan(motY / motXZ);
	}
	public static float atan(double value) {
		return RADTODEG * (float) Math.atan(value);
	}
	
	public static Location move(Location loc, Vector offset) {
		return move(loc, offset.getX(), offset.getY(), offset.getZ());
	}
	public static Location move(Location loc, double dx, double dy, double dz) {
		Vector off = rotate(loc.getYaw(), loc.getPitch(), dx, dy, dz);
        double x = loc.getX() + off.getX();
        double y = loc.getY() + off.getY();
        double z = loc.getZ() + off.getZ();
        return new Location(loc.getWorld(), x, y, z, loc.getYaw(), loc.getPitch());
    }	
	public static Vector rotate(float yaw, float pitch, Vector value) {
		return rotate(yaw, pitch, value.getX(), value.getY(), value.getZ());
	}
	public static Vector rotate(float yaw, float pitch, double x, double y, double z) {
        //Conversions found by (a lot of) testing
		float angle;
        angle = yaw * DEGTORAD;
		double sinyaw = Math.sin(angle);
		double cosyaw = Math.cos(angle);
		
		angle = pitch * DEGTORAD;
		double sinpitch = Math.sin(angle);
		double cospitch = Math.cos(angle);
		
        double newx = 0.0;
        double newy = 0.0;
        double newz = 0.0;
        newz -= x * cosyaw;
        newz -= y * sinyaw * sinpitch;
        newz -= z * sinyaw * cospitch;
        newx += x * sinyaw;
        newx -= y * cosyaw * sinpitch;
        newx -= z * cosyaw * cospitch;
        newy += y * cospitch;
        newy -= z * sinpitch;
        
        return new Vector(newx, newy, newz);
	}
	
    public static double round(double Rval, int Rpl) {
  	  double p = Math.pow(10, Rpl);
  	  return Math.round(Rval * p) / p;
    }
    public static double fixNaN(double value, double def) {
    	if (Double.isNaN(value)) return def;
    	return value;
    }
    public static double fixNaN(double value) {
    	return fixNaN(value, 0);
    }
	public static int locToChunk(double loc) {
		return MathHelper.floor(loc / 16.0);
	}
    public static long toLong(int msw, int lsw) {
        return ((long) msw << 32) + lsw - Integer.MIN_VALUE;
    }
       
	public static double useOld(double oldvalue, double newvalue) {
		return useOld(oldvalue, newvalue, 0.2);
	}
	public static double useOld(double oldvalue, double newvalue, double peruseold) {
		return oldvalue + (peruseold * (newvalue - oldvalue));
	}
	
	  /*
     * Stages the value between the two points using a stage from 0 to 1
     */
    public static double stage(double d1, double d2, double stage) {
    	if (Double.isNaN(stage)) return d2;
    	if (stage < 0) stage = 0;
    	if (stage > 1) stage = 1;
    	return d1 * (1 - stage) + d2 * stage;
    }
    public static Vector stage(Vector vec1, Vector vec2, double stage) {
    	Vector newvec = new Vector();
    	newvec.setX(stage(vec1.getX(), vec2.getX(), stage));
    	newvec.setY(stage(vec1.getY(), vec2.getY(), stage));
    	newvec.setZ(stage(vec1.getZ(), vec2.getZ(), stage));
    	return newvec;
    }
    public static Location stage(Location loc1, Location loc2, double stage) {
    	Location newloc = new Location(loc1.getWorld(), 0, 0, 0);
    	newloc.setX(stage(loc1.getX(), loc2.getX(), stage));
    	newloc.setY(stage(loc1.getY(), loc2.getY(), stage));
    	newloc.setZ(stage(loc1.getZ(), loc2.getZ(), stage));
    	newloc.setYaw((float) stage(loc1.getYaw(), loc2.getYaw(), stage));
    	newloc.setPitch((float) stage(loc1.getPitch(), loc2.getPitch(), stage));
    	return newloc;
    }
    	
	public static boolean isInverted(double value1, double value2) {
		return (value1 > 0 && value2 < 0) || (value1 < 0 && value2 > 0);
	}

	public static Vector getDirection(float yaw, float pitch) {
		return new Location(null, 0, 0, 0, yaw, pitch).getDirection();
	}
		
	public static double limit(double value, double limit) {
		return limit(value, -limit, limit);
	}
	public static double limit(double value, double min, double max) {
		if (value < min) return min;
		if (value > max) return max;
		return value;
	}
	public static float limit(float value, float limit) {
		return limit(value, -limit, limit);
	}
	public static float limit(float value, float min, float max) {
		if (value < min) return min;
		if (value > max) return max;
		return value;
	}
	public static int limit(int value, int limit) {
		return limit(value, -limit, limit);
	}
	public static int limit(int value, int min, int max) {
		if (value < min) return min;
		if (value > max) return max;
		return value;
	}

	public static void setVectorLength(Vector vector, double length) {
		if (length >= 0) {
			setVectorLengthSquared(vector, length * length);
		} else {
			setVectorLengthSquared(vector, -length * length);
		}
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
		if (dbefore < 0.0001) return true;
		velocity = velocity.clone();
		setVectorLengthSquared(velocity, dbefore);
		return dbefore > velocity.subtract(offset).lengthSquared();
	}
	
}
