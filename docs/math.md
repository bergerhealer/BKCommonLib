# Maths and Spatial Transforms

BKCommonLib's maths package provides vectors, rotations, matrices, and bounds for spatial work. `Matrix4x4` and `Quaternion` are the main tools for composing 3D transforms and orientations; `IntVector3` and related integer-coordinate types are useful for block and chunk positions. Larger recipes are in the [math examples](examples/math.md).

## Key files

- [`Matrix4x4`](../src/main/java/com/bergerkiller/bukkit/common/math/Matrix4x4.java): mutable 4x4 transformation matrix with translation, rotation, scaling, multiplication, inversion, and point transformation.
- [`Quaternion`](../src/main/java/com/bergerkiller/bukkit/common/math/Quaternion.java): normalized rotation representation, direction-to-orientation helpers, and spherical interpolation.
- [`Rotatable`](../src/main/java/com/bergerkiller/bukkit/common/math/Rotatable.java): shared rotation methods implemented by matrices and quaternions.
- [`Vector2`](../src/main/java/com/bergerkiller/bukkit/common/math/Vector2.java), [`Vector3`](../src/main/java/com/bergerkiller/bukkit/common/math/Vector3.java), and [`Vector4`](../src/main/java/com/bergerkiller/bukkit/common/math/Vector4.java): lightweight mutable coordinate values used by BKCommonLib APIs.
- [`IntVector2`](../src/main/java/com/bergerkiller/bukkit/common/bases/IntVector2.java) and [`IntVector3`](../src/main/java/com/bergerkiller/bukkit/common/bases/IntVector3.java): immutable integer coordinates for chunks and blocks.
- [`IntCuboid`](../src/main/java/com/bergerkiller/bukkit/common/bases/IntCuboid.java): integer block-coordinate bounds.
- [`MathUtil`](../src/main/java/com/bergerkiller/bukkit/common/utils/MathUtil.java): scalar/vector math helpers, angle utilities, and packing signed integer coordinate pairs into `long` keys.
- [`FaceUtil`](../src/main/java/com/bergerkiller/bukkit/common/utils/FaceUtil.java): conversion and rotation helpers between Bukkit `BlockFace`, vectors, and Minecraft yaw.

## Choosing a value type

For general floating-point positions and directions, Bukkit's mutable `org.bukkit.util.Vector` is accepted by most APIs. BKCommonLib's `Vector3` is a small mutable coordinate holder with public `x`, `y`, and `z` fields and a few value operations; it is not a replacement for Bukkit's full vector API. `Vector2` and `Vector4` are similarly lightweight coordinate holders.

Use `IntVector3` for block coordinates and `IntVector2` for chunk coordinates. These are immutable and can be used as map keys. Constructors from `double` coordinates floor values to integers, which is important for negative coordinates; use `blockOf(...)` when converting a world position to its containing block explicitly. `IntVector3.midVector()` returns the center of the represented block. `IntCuboid` uses inclusive minimum and exclusive maximum coordinates, so a region from `(0, 0, 0)` to `(2, 1, 1)` contains two blocks along X.

## Rotations with Quaternion

A quaternion represents orientation without the gimbal lock that can affect Euler-angle rotations. The default constructor creates the identity rotation, and the four-value constructor normalizes its input. Most operations mutate the quaternion; `clone()` first when the original orientation must be preserved.

- Use `Quaternion.fromAxisAngles(axis, degrees)` for a rotation about a known axis.
- Use `Quaternion.fromYawPitchRoll(xRot, yRot, zRot)` when working with Minecraft-style angles. **Naming pitfall:** despite the method name, the arguments are in axis order, not name order: `xRot` is pitch, `yRot` is yaw, and `zRot` is roll. All are in degrees. For example, pass `(0.0, 90.0, 0.0)` for a 90-degree yaw, not `(90.0, 0.0, 0.0)`.
- Use `Quaternion.fromLookDirection(direction)` to orient the local forward axis toward a direction. Supply an up vector to `fromLookDirection(direction, up)` when roll should be controlled.
- Use `Quaternion.slerp(start, end, amount)` to smoothly interpolate orientations, where `amount` ranges from 0 to 1.
- `rightVector()`, `upVector()`, and `forwardVector()` return the three axes of the rotated orientation.

For example, `Quaternion orientation = Quaternion.fromLookDirection(direction, new Vector(0.0, 1.0, 0.0));` creates an orientation looking along `direction` with world-up as the roll reference.

## Composing transformations with Matrix4x4

`Matrix4x4` starts as identity. It stores a 3D affine transform in a 4x4 matrix, with position in `m03`, `m13`, and `m23`. It can combine translation, rotation, and scale, and apply the result to Bukkit `Vector`, BKCommonLib `Vector3`, or `Vector4` values. Point-transform methods write the result back into the input object.

`translate(...)`, `rotate(...)`, and `scale(...)` compose changes in the matrix's local coordinate frame. `worldTranslate(...)` instead adds an offset in world axes, independent of the current rotation. Matrix multiplication order matters: `Matrix4x4.multiply(parent, local)` applies `local` first, then maps it through `parent`, which is the usual way to position an object relative to a transformed parent.

```java
Matrix4x4 transform = Matrix4x4.fromLocation(origin);
transform.translate(localOffset);
transform.rotateYawPitchRoll(pitch, yaw, roll);
transform.transformPoint(point); // Transforms point in place
```

Angles passed to the degree-based rotation methods are degrees. `rotateYawPitchRoll` uses Minecraft's pitch/yaw/roll convention, not a generic X/Y/Z Euler convention. Its positional arguments are likewise `(xRot, yRot, zRot)` = `(pitch, yaw, roll)`; the name `YawPitchRoll` does not describe the argument order. `fromLocation(location)` builds a transform from a Bukkit location; `toLocation(world)` converts position, yaw, and pitch back to a Bukkit location, but a Bukkit `Location` cannot represent roll.

`invert()` replaces the matrix with its inverse and returns `false` if it is singular. `getRotation()` extracts a quaternion from the matrix's rotation portion; use it for rotation transforms, not as a way to decompose arbitrary scaled or sheared matrices.

For complete recipes—including hierarchical transforms and orientation interpolation—see the [math examples](examples/math.md).

## MathUtil

[`MathUtil`](../src/main/java/com/bergerkiller/bukkit/common/utils/MathUtil.java) collects small operations used throughout BKCommonLib, including lengths and distances, rounding and clamping, normalization factors, and angle helpers. Prefer squared-distance methods when only comparing distances, since they avoid a square root. `wrapAngle(...)` normalizes angles to the range `(-180, 180]`, while `getAngleDifference(a, b)` returns the smallest absolute difference between two angles.

For long-keyed maps, `longHashToLong(int, int)` (also exposed as `toLong(int, int)`) packs two signed 32-bit values into one `long`. `longHashMsw(long)` and `longHashLsw(long)` recover the first and second values. This is a reversible coordinate key, not a general-purpose hash: use the same pair ordering when encoding and decoding. The low-word accessor compensates for the signed-int offset used by the packing scheme, so use the provided accessor instead of a raw cast when the second value can be negative.

```java
long key = MathUtil.longHashToLong(chunkX, chunkZ);
int x = MathUtil.longHashMsw(key);
int z = MathUtil.longHashLsw(key);
```

Additional packing and key arithmetic methods are available for operations on these encoded coordinate pairs. See the [math examples](examples/math.md) for a long-keyed map recipe.

## FaceUtil and BlockFace directions

[`FaceUtil`](../src/main/java/com/bergerkiller/bukkit/common/utils/FaceUtil.java) bridges Bukkit's `BlockFace` directions with vectors and angles. `faceToVector(face)` uses the face's X/Y/Z modifiers; `getDirection(dx, dz, useSubCardinalDirections)` quantizes a horizontal direction to a cardinal or, optionally, diagonal face. `vectorToBlockFace(...)` considers vertical direction too, choosing UP or DOWN when the vertical component dominates.

Horizontal faces use eight 45-degree notches, with `WEST` at notch 0 and `NORTH` at notch 2. `faceToYaw(face)` and `yawToFace(yaw, ...)` use this same Minecraft yaw mapping; don't assume that a positive yaw increment matches compass clockwise intuition without checking the documented face mapping. `rotate(face, notchCount)` rotates by notch counts (one notch is 45 degrees); for a 90-degree turn use `2` or `-2`. `isVertical(face)` distinguishes UP/DOWN from horizontal faces.

```java
BlockFace facing = FaceUtil.getDirection(movement, true);
Vector facingVector = FaceUtil.faceToVector(facing);
BlockFace turned = FaceUtil.rotate(facing, 2);
```

The simpler `getDirection(...)` overloads classify only X/Z direction and ignore Y; use `vectorToBlockFace(...)` when the vertical component should be considered. `FaceUtil` also provides helpers for combining and decomposing diagonal faces and for converting between face and yaw values.

## Geometry helpers

The maths package also includes [`OrientedBoundingBox`](../src/main/java/com/bergerkiller/bukkit/common/math/OrientedBoundingBox.java) for rotated bounds and [`VectorList`](../src/main/java/com/bergerkiller/bukkit/common/math/VectorList.java) for collections of Bukkit vectors. For axis-aligned integer block regions, use `IntCuboid` rather than a floating-point transform.

This page focuses on the current vector, quaternion, matrix, and bounds APIs. It intentionally does not document legacy mutable classes under `common/bases/mutable/`.
