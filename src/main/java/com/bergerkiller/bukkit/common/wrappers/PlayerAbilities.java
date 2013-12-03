package com.bergerkiller.bukkit.common.wrappers;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerAbilitiesRef;
import com.bergerkiller.bukkit.common.utils.PacketUtil;

/**
 * Player abilities wrapper class.
 * Note that Bukkit already provides methods to change these abilities for Players.
 * This wrapper class is intended for human entities or non-player-bound processing.
 */
public class PlayerAbilities extends BasicWrapper {

	public PlayerAbilities() {
		this(PlayerAbilitiesRef.TEMPLATE.newInstance());
	}

	public PlayerAbilities(Object handle) {
		setHandle(handle);
	}

	public boolean isInvulnerable() {
		return PlayerAbilitiesRef.isInvulnerable.get(handle);
	}

	public void setInvulnerable(boolean invulnerable) {
		PlayerAbilitiesRef.isInvulnerable.set(handle, invulnerable);
	}

	public boolean isFlying() {
		return PlayerAbilitiesRef.isFlying.get(handle);
	}

	public void setFlying(boolean flying) {
		PlayerAbilitiesRef.isFlying.set(handle, flying);
	}

	public boolean canFly() {
		return PlayerAbilitiesRef.canFly.get(handle);
	}

	public void setCanFly(boolean canFly) {
		PlayerAbilitiesRef.canFly.set(handle, canFly);
	}

	public boolean canInstantlyBuild() {
		return PlayerAbilitiesRef.canInstantlyBuild.get(handle);
	}

	public void setCanInstantlyBuild(boolean canInstantlyBuild) {
		PlayerAbilitiesRef.canInstantlyBuild.set(handle, canInstantlyBuild);
	}

	public boolean canBuild() {
		return PlayerAbilitiesRef.mayBuild.get(handle);
	}

	public void setCanBuild(boolean canBuild) {
		PlayerAbilitiesRef.mayBuild.set(handle, canBuild);
	}

	public float getFlySpeed() {
		return PlayerAbilitiesRef.flySpeed.get(handle);
	}

	public void setFlySpeed(float speed) {
		PlayerAbilitiesRef.flySpeed.set(handle, speed);
	}

	public float getWalkSpeed() {
		return PlayerAbilitiesRef.walkSpeed.get(handle);
	}

	public void setWalkSpeed(float speed) {
		PlayerAbilitiesRef.walkSpeed.set(handle, speed);
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
