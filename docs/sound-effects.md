# Sound Effects

BKCommonLib represents a sound event by a `ResourceKey<SoundEffect>`. Use `PlayerUtil` to play a sound for one player, or `WorldUtil` to play it at a world location for nearby players.

## Play sounds

`PlayerUtil.playSound(...)` sends a sound to a single player. Without an explicit location, the sound plays at the player's eye location. You can also specify a location and a sound category:

```java
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;

// At the player's eye location
PlayerUtil.playSound(player, SoundEffect.CLICK, 1.0f, 1.0f);

// At a world location, heard only by this player
PlayerUtil.playSound(player, location, SoundEffect.PISTON_EXTEND, "block", 1.0f, 0.8f);
```

`WorldUtil.playSound(...)` plays at a location in its world, for players nearby:

```java
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.WorldUtil;

WorldUtil.playSound(location, SoundEffect.EXTINGUISH, 0.7f, 1.2f);
WorldUtil.playSound(location, SoundEffect.CLICK_WOOD, "block", 1.0f, 1.0f);
```

Both utilities take a volume and pitch as floats. The category selects which client sound slider controls playback; overloads without a category use `"master"`. `PlayerUtil` sends a packet, so follow the [packet guide](protocol.md)'s advice about sending from packet callbacks.

## Sound keys and constants

`SoundEffect` is a marker class, not an enum. Its constants are `ResourceKey<SoundEffect>` values. BKCommonLib selects the appropriate names for the running server version, including legacy names:

| Constant | Sound |
|---|---|
| `SoundEffect.EXTINGUISH` | Fire extinguishing |
| `SoundEffect.WALK_CLOTH` | Wool or cloth footstep/fall sound |
| `SoundEffect.CLICK` | UI button click |
| `SoundEffect.CLICK_WOOD` | Wooden button click |
| `SoundEffect.PISTON_CONTRACT` | Piston retracting |
| `SoundEffect.PISTON_EXTEND` | Piston extending |
| `SoundEffect.ITEM_BREAK` | Item breaking |

For a sound not included in these constants, use `SoundEffect.fromName(...)` to create a key:

```java
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;

ResourceKey<SoundEffect> sound = SoundEffect.fromName("entity.experience_orb.pickup");
PlayerUtil.playSound(player, sound, 0.8f, 1.0f);
```

Names can include a namespace, such as `minecraft:entity.experience_orb.pickup` or `example:custom_sound`; the default namespace is `minecraft`. Invalid resource identifiers return `null`: use lowercase letters, digits, `/`, `.`, `_`, and `-` in the path. Validate names from configuration before playing them, particularly with `WorldUtil.playSound(...)`, which expects a non-null key.

Creating a key does not verify that the sound exists or that the client can play it. Unlike the constants, a name supplied to `fromName(...)` is not automatically translated between Minecraft versions. Custom sounds also require the client to have a resource pack or mod providing the named sound.
