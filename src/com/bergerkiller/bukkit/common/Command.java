package com.bergerkiller.bukkit.common;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.permissions.IPermissionDefault;
import com.bergerkiller.bukkit.common.permissions.NoPermissionException;

public abstract class Command {
	
	private ArrayList<Command> subCommands = new ArrayList<Command>();
	public List<String> args;
	public CommandSender sender;
	
	public final boolean isArg(int index, String... values) {
		if (index < args.size()) {
			String arg = args.get(index);
			for (String value : values) {
				if (arg.equalsIgnoreCase(value)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public final Double parseDouble(int index, Double def) {
		try {
			if (index < this.args.size()) {
				return Double.parseDouble(this.args.get(index));
			}
		} catch (Exception ex) {}
		return def;
	}
	
	public final Integer parseInt(int index, Integer def) {
		try {
			if (index < this.args.size()) {
				return Integer.parseInt(this.args.get(index));
			}
		} catch (Exception ex) {}
		return def;
	}
		
	public final boolean execute(CommandSender sender, List<String> args) throws NoPermissionException {
		this.args = args;
		this.sender = sender;
		if (this.match()) {
			if (sender instanceof Player) {
				String perm = this.getPermissionNode();
				if (perm != null && !sender.hasPermission(perm)) {
					throw new NoPermissionException();
				}
			} else if (!isConsoleAllowed()) {
				throw new NoPermissionException();
			}
			if (this.handle()) {
				for (Command command : this.subCommands) {
					command.execute(sender, args);
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean isConsoleAllowed() {
		return true;
	}
	
	public String getPermissionNode() {
		IPermissionDefault def = this.getPermission();
		return def == null ? null : def.getName();
	}
	
	public IPermissionDefault getPermission() {
		return null;
	}
	
	public abstract boolean match();
	
	public abstract boolean handle() throws NoPermissionException;
	
	public final void register(Command command) {
		this.subCommands.add(command);
	}

}
