package com.bergerkiller.bukkit.common.permissions;

import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

/**
 * Basic implementation of IPermissionDefault that supplies additional function routines
 */
public abstract class PermissionEnum implements IPermissionDefault {
	private final String node;
	private final String[] nodeArr;
	private final String name;
	private final PermissionDefault def;
	private final String desc;

	protected PermissionEnum(String node, PermissionDefault def, String description) {
		this(node, def, description, 0);
	}

	protected PermissionEnum(String node, PermissionDefault def, String description, int argCount) {
		this.node = node.toLowerCase(Locale.ENGLISH);
		this.nodeArr = this.node.split("\\.");
		this.def = def;
		this.desc = description;
		this.name = this.node + StringUtil.getFilledString(".*", argCount);
	}

	/**
	 * Gets the root name of this Permission, the node name excluding appended * parts for arguments
	 * 
	 * @return permission root name
	 */
	public String getRootName() {
		return this.node;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public PermissionDefault getDefault() {
		return this.def;
	}

	@Override
	public String getDescription() {
		return this.desc;
	}

	/**
	 * Checks whether a CommandSender has this permission<br>
	 * If the sender does not have this permission, the message specified is sent
	 * 
	 * @param sender to check the permission for
	 * @param message to send if the sender has no permission
	 * @return True if the sender has permission, False if not
	 */
	public boolean handleMsg(CommandSender sender, String message) {
		return handleMsg(sender, message, StringUtil.EMPTY_ARRAY);
	}

	/**
	 * Checks whether a CommandSender has this permission<br>
	 * If the sender does not have this permission, the message specified is sent
	 * 
	 * @param sender to check the permission for
	 * @param message to send if the sender has no permission
	 * @param args to use for this permission node (appended to the node name)
	 * @return True if the sender has permission, False if not
	 */
	public boolean handleMsg(CommandSender sender, String message, String... args) {
		if (this.has(sender, args)) {
			return true;
		}
		sender.sendMessage(message);
		return false;
	}

	/**
	 * Checks whether a CommandSender has this permission<br>
	 * If the sender does not have this permission, a NoPermissionException is thrown
	 * 
	 * @param sender to check the permission for
	 */
	public void handle(CommandSender sender) {
		handle(sender, StringUtil.EMPTY_ARRAY);
	}

	/**
	 * Checks whether a CommandSender has this permission<br>
	 * If the sender does not have this permission, a NoPermissionException is thrown
	 * 
	 * @param sender to check the permission for
	 * @param args to use for this permission node (appended to the node name)
	 * @throws NoPermissionException
	 */
	public void handle(CommandSender sender, String... args) {
		if (!has(sender, args)) {
			throw new NoPermissionException();
		}
	}

	/**
	 * Checks whether a CommandSender has this permission
	 * 
	 * @param sender to check the permission for
	 * @return True if the sender has permission for this node, False if not
	 */
	public boolean has(CommandSender sender) {
		return has(sender, StringUtil.EMPTY_ARRAY);
	}

	/**
	 * Checks whether a CommandSender has this permission
	 * 
	 * @param sender to check the permission for
	 * @param args to use for this permission node (appended to the node name)
	 * @return True if the sender has permission for this node, False if not
	 */
	public boolean has(CommandSender sender, String... args) {
		if (args.length == 0) {
			// No-argument version
			return CommonUtil.hasPermission(sender, this.node);
		} else {
			return CommonUtil.hasPermission(sender, LogicUtil.appendArray(this.nodeArr, args));
		}
	}
}
