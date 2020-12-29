package com.bergerkiller.bukkit.common.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.Locale;

/**
 * Basic implementation of IPermissionDefault that supplies additional function
 * routines
 */
public abstract class PermissionEnum implements IPermissionEnum {

    private final String node;
    private final PermissionDefault def;
    private final String desc;
    private final int argCount;

    protected PermissionEnum(String node, PermissionDefault def, String description) {
        this(node, def, description, 0);
    }

    protected PermissionEnum(String node, PermissionDefault def, String description, int argCount) {
        this.node = node.toLowerCase(Locale.ENGLISH);
        this.def = def;
        this.desc = description;
        this.argCount = argCount;
    }

    /**
     * Gets the root name of this Permission, the node name excluding appended *
     * parts for arguments
     *
     * @return permission root name
     */
    @Override
    public String getRootName() {
        return this.node;
    }

    @Override
    public int getArgumentCount() {
        return this.argCount;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public PermissionDefault getDefault() {
        return this.def;
    }

    @Override
    public String getDescription() {
        return this.desc;
    }

    @Override
    public String getName() {
        return IPermissionEnum.super.getName();
    }

    /**
     * Checks whether a CommandSender has this permission<br>
     * If the sender does not have this permission, the message specified is
     * sent
     *
     * @param sender to check the permission for
     * @param message to send if the sender has no permission
     * @return True if the sender has permission, False if not
     */
    @Override
    public boolean handleMsg(CommandSender sender, String message) {
        return IPermissionEnum.super.handleMsg(sender, message);
    }

    /**
     * Checks whether a CommandSender has this permission<br>
     * If the sender does not have this permission, the message specified is
     * sent
     *
     * @param sender to check the permission for
     * @param message to send if the sender has no permission
     * @param args to use for this permission node (appended to the node name)
     * @return True if the sender has permission, False if not
     */
    @Override
    public boolean handleMsg(CommandSender sender, String message, String... args) {
        return IPermissionEnum.super.handleMsg(sender, message, args);
    }

    /**
     * Checks whether a CommandSender has this permission<br>
     * If the sender does not have this permission, a NoPermissionException is
     * thrown
     *
     * @param sender to check the permission for
     */
    @Override
    public void handle(CommandSender sender) {
        IPermissionEnum.super.handle(sender);
    }

    /**
     * Checks whether a CommandSender has this permission<br>
     * If the sender does not have this permission, a NoPermissionException is
     * thrown
     *
     * @param sender to check the permission for
     * @param args to use for this permission node (appended to the node name)
     * @throws NoPermissionException
     */
    @Override
    public void handle(CommandSender sender, String... args) {
        IPermissionEnum.super.handle(sender, args);
    }

    /**
     * Checks whether a CommandSender has this permission
     *
     * @param sender to check the permission for
     * @return True if the sender has permission for this node, False if not
     */
    @Override
    public boolean has(CommandSender sender) {
        return IPermissionEnum.super.has(sender);
    }

    /**
     * Checks whether a CommandSender has this permission
     *
     * @param sender to check the permission for
     * @param args to use for this permission node (appended to the node name)
     * @return True if the sender has permission for this node, False if not
     */
    @Override
    public boolean has(CommandSender sender, String... args) {
        return IPermissionEnum.super.has(sender, args);
    }
}
