# Maths Examples

These examples use Bukkit's `Vector` for floating-point positions and directions, `Matrix4x4` for transforms, and `Quaternion` for orientations. Matrix and quaternion methods mutate their receiver; clone a value before changing it when the original is needed later.

## Position and rotate an object relative to a parent

Build a local transform for an object's offset and orientation, then multiply it by the parent's world transform. The local offset follows the parent's orientation, so rotating the parent also rotates the object's offset around the parent origin.

```java
Matrix4x4 parentTransform = Matrix4x4.fromLocation(parentLocation);

Matrix4x4 localTransform = new Matrix4x4();
localTransform.translate(0.0, 0.5, 1.0);
localTransform.rotateYawPitchRoll(0.0, 30.0, 0.0); // xRot=pitch, yRot=yaw, zRot=roll

Matrix4x4 objectTransform = Matrix4x4.multiply(parentTransform, localTransform);

Vector objectPoint = new Vector(0.0, 0.0, 0.5);
objectTransform.transformPoint(objectPoint);
```

`objectPoint` is now in world coordinates. `transformPoint` changes the supplied vector, so pass a clone if the local-space point must be kept. Use the resulting transform for other points belonging to the same object; use `objectTransform.toLocation(world)` when a Bukkit location is needed.

To move in world axes rather than the object's rotated local axes, call `worldTranslate(dx, dy, dz)` on a copy of the transform. To convert a world-space point back to the object's local coordinates, invert a cloned transform and apply it:

```java
Matrix4x4 worldToObject = objectTransform.clone();
if (worldToObject.invert()) {
    worldToObject.transformPoint(worldPoint);
}
```

## Orient along a direction and interpolate

Create orientations from direction vectors, then use spherical interpolation to transition smoothly between them:

```java
Vector worldUp = new Vector(0.0, 1.0, 0.0);
Quaternion from = Quaternion.fromLookDirection(startDirection, worldUp);
Quaternion to = Quaternion.fromLookDirection(endDirection, worldUp);

double progress = 0.5; // 0 uses from; 1 uses to
Quaternion orientation = Quaternion.slerp(from, to, progress);

Vector forward = orientation.forwardVector();
```

The result's `forwardVector()` gives the rotated local forward axis. Use `orientation` to rotate a matrix with `transform.rotate(orientation)`, or inspect its right/up/forward basis vectors. When the desired rotation is already known as Minecraft pitch/yaw/roll angles, use `Quaternion.fromYawPitchRoll(xRot, yRot, zRot)` where the arguments mean `(pitch, yaw, roll)`—the method name can misleadingly suggest a different order.

## Convert a world position to block and chunk coordinates

Use integer-coordinate classes when indexing block or chunk data. Converting a floating-point position floors each coordinate, including negative values:

```java
IntVector3 block = IntVector3.blockOf(position);
IntVector2 chunk = block.toChunkCoordinates();

IntCuboid region = IntCuboid.create(
        new IntVector3(-16, 0, -16),
        new IntVector3(16, 128, 16)); // Maximum coordinates are exclusive

if (region.contains(block)) {
    // The block lies inside the region
}
```

For a center point inside the block, use `block.midVector()`. `IntVector3` is immutable, so adding an offset returns a new coordinate rather than changing `block`.

## Pack two coordinates into a long map key

`MathUtil.longHashToLong(x, z)` packs the complete signed integer pair into a `long`, so distinct coordinate pairs remain distinct. Decode with `longHashMsw` and `longHashLsw`; don't decode the low word with a plain `(int)` cast.

```java
Map<Long, String> chunkOwners = new HashMap<>();
long chunkKey = MathUtil.longHashToLong(chunkX, chunkZ);
chunkOwners.put(chunkKey, ownerName);

long storedKey = chunkOwners.keySet().iterator().next();
int storedX = MathUtil.longHashMsw(storedKey);
int storedZ = MathUtil.longHashLsw(storedKey);
```

The pair ordering is up to the caller, but it must remain consistent throughout the map's use. This encoding is useful for coordinate keys; it is not interchangeable with `BlockFace` or a generic hash code.

## Convert and rotate BlockFace directions

Use `FaceUtil` when a direction needs to cross between Bukkit faces, vectors, and Minecraft yaw. The optional diagonal mode quantizes the X/Z direction to one of eight horizontal faces; vertical classification is handled separately by `vectorToBlockFace`.

```java
Vector motion = new Vector(1.0, 0.0, -1.0);
BlockFace direction = FaceUtil.getDirection(motion, true);
Vector unitStep = FaceUtil.faceToVector(direction);

BlockFace clockwiseQuarterTurn = FaceUtil.rotate(direction, 2);
float yaw = FaceUtil.faceToYaw(clockwiseQuarterTurn);
BlockFace sameDirection = FaceUtil.yawToFace(yaw, true);
```

`rotate` takes notch counts, not degrees: two notches are 90 degrees. `getDirection(motion, ...)` ignores the Y component; use `FaceUtil.vectorToBlockFace(motion, ...)` if UP or DOWN should be returned when vertical motion dominates.
