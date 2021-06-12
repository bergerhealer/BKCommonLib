package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.generated.net.minecraft.world.entity.player.PlayerAbilitiesHandle;

import org.bukkit.entity.Player;

/**
 * Player abilities wrapper class. Note that Bukkit already provides methods to
 * change these abilities for Players. This wrapper class is intended for human
 * entities or non-player-bound processing.
 */
public class PlayerAbilities extends BasicWrapper<PlayerAbilitiesHandle> {

    public PlayerAbilities() {
        setHandle(PlayerAbilitiesHandle.createNew());
    }

    public PlayerAbilities(Object handle) {
        setHandle(PlayerAbilitiesHandle.createHandle(handle));
    }

    public boolean isInvulnerable() {
        return handle.isInvulnerable();
    }

    public void setInvulnerable(boolean invulnerable) {
        handle.setIsInvulnerable(invulnerable);
    }

    public boolean isFlying() {
        return handle.isFlying();
    }

    public void setFlying(boolean flying) {
        handle.setIsFlying(flying);
    }

    public boolean canFly() {
        return handle.isCanFly();
    }

    public void setCanFly(boolean canFly) {
        handle.setCanFly(canFly);
    }

    public boolean canInstantlyBuild() {
        return handle.isCanInstantlyBuild();
    }

    public void setCanInstantlyBuild(boolean canInstantlyBuild) {
        handle.setCanInstantlyBuild(canInstantlyBuild);
    }

    public boolean canBuild() {
        return handle.isMayBuild();
    }

    public void setCanBuild(boolean canBuild) {
        handle.setMayBuild(canBuild);
    }

    public double getFlySpeed() {
        return handle.getFlySpeed();
    }

    public void setFlySpeed(double speed) {
        handle.setFlySpeed(speed);
    }

    public float getWalkSpeed() {
        return handle.getWalkSpeed();
    }

    public void setWalkSpeed(float speed) {
        handle.setWalkSpeed(speed);
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
