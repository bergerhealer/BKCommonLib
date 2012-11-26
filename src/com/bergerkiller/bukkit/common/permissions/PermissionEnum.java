package com.bergerkiller.bukkit.common.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class PermissionEnum implements IPermissionDefault {
	private final String node;
	private final String name;
	private final PermissionDefault def;
	private final String desc;

	protected PermissionEnum(String node, PermissionDefault def, String description) {
		this(node, def, description, 1);
	}

	protected PermissionEnum(String node, PermissionDefault def, String description, int argCount) {
		this.node = node;
		this.def = def;
		this.desc = description;
		StringBuilder builder = new StringBuilder(this.node);
		for (int i = 0; i < argCount; i++) {
			builder.append(".*");
		}
		this.name = builder.toString();
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

	public boolean hasGlobal(Player player, String... args) {
		return has(player, name) || has(player, "*");
	}

	public boolean hasGlobal(CommandSender sender, String name1, String name2) {
		return has(sender, name1, name2) || has(sender, name1, "*") || has(sender, "*", name2) || has(sender, "*", "*");
	}

	public boolean has(CommandSender sender) {
		return has(sender, new String[0]);
	}

	public boolean has(CommandSender sender, String... args) {
		String node = this.node;
		if (args.length > 0) {
			StringBuilder builder = new StringBuilder(node);
			for (String arg : args) {
				builder.append('.').append(arg);
			}
			node = builder.toString();
		}
		return CommonUtil.hasPermission(sender, node);
	}
}
