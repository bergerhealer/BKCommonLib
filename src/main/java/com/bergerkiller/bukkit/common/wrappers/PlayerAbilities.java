package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.reflection.net.minecraft.server.NMSPlayerAbilities;

import org.bukkit.entity.Player;

/**
 * Player abilities wrapper class. Note that Bukkit already provides methods to
 * change these abilities for Players. This wrapper class is intended for human
 * entities or non-player-bound processing.
 */
public class PlayerAbilities extends BasicWrapper {

    public PlayerAbilities() {
        this(NMSPlayerAbilities.T.newInstance());
    }

    public PlayerAbilities(Object handle) {
        setHandle(handle);
    }

    public boolean isInvulnerable() {
        return NMSPlayerAbilities.isInvulnerable.get(handle);
    }

    public void setInvulnerable(boolean invulnerable) {
        NMSPlayerAbilities.isInvulnerable.set(handle, invulnerable);
    }

    public boolean isFlying() {
        return NMSPlayerAbilities.isFlying.get(handle);
    }

    public void setFlying(boolean flying) {
        NMSPlayerAbilities.isFlying.set(handle, flying);
    }

    public boolean canFly() {
        return NMSPlayerAbilities.canFly.get(handle);
    }

    public void setCanFly(boolean canFly) {
        NMSPlayerAbilities.canFly.set(handle, canFly);
    }

    public boolean canInstantlyBuild() {
        return NMSPlayerAbilities.canInstantlyBuild.get(handle);
    }

    public void setCanInstantlyBuild(boolean canInstantlyBuild) {
        NMSPlayerAbilities.canInstantlyBuild.set(handle, canInstantlyBuild);
    }

    public boolean canBuild() {
        return NMSPlayerAbilities.mayBuild.get(handle);
    }

    public void setCanBuild(boolean canBuild) {
        NMSPlayerAbilities.mayBuild.set(handle, canBuild);
    }

    public float getFlySpeed() {
        return NMSPlayerAbilities.flySpeed.get(handle);
    }

    public void setFlySpeed(float speed) {
        NMSPlayerAbilities.flySpeed.set(handle, speed);
    }

    public float getWalkSpeed() {
        return NMSPlayerAbilities.walkSpeed.get(handle);
    }

    public void setWalkSpeed(float speed) {
        NMSPlayerAbilities.walkSpeed.set(handle, speed);
    }

    /**
     * Sends a message to the player containing the updated settings
     *
     * @param player to update
     */
    public void update(Player player) {
        PacketUtil.sendPacket(player, PacketType.OUT_ABILITIES.newInstance(this));
    }
}
