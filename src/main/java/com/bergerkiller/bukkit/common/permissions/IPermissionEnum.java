package com.bergerkiller.bukkit.common.permissions;

import org.bukkit.command.CommandSender;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

/**
 * Basic implementation of IPermissionDefault that supplies additional function
 * routines. As opposed to {@link PermissionEnum} this is an interface, so it can
 * be used with enums properly.
 */
public interface IPermissionEnum extends IPermissionDefault {

    /**
     * Gets the root name of this Permission, the node name excluding appended *
     * parts for arguments. This should be implemented.
     *
     * @return permission root name
     */
    String getRootName();

    /**
     * Gets the number of arguments appended to this permission's {@link #getRootName()}
     * to build the final permission node.
     * 
     * @return permission argument count
     */
    default int getArgumentCount() { return 0; }

    @Override
    default String getName() {
        String rootName = this.getRootName();
        int count = this.getArgumentCount();
        if (count == 0) {
            return rootName;
        } else {
            StringBuffer outputBuffer = new StringBuffer(rootName.length() + count * 2);
            outputBuffer.append(rootName);
            for (int n = 0; n < count; n++) {
                outputBuffer.append(".*");
            }
            return outputBuffer.toString();
        }
    }

    /**
     * Checks whether a CommandSender has this permission
     *
     * @param sender to check the permission for
     * @return True if the sender has permission for this node, False if not
     * @throws IllegalArgumentException If this permission requires arguments
     */
    default boolean has(CommandSender sender) {
        return has(sender, StringUtil.EMPTY_ARRAY);
    }

    /**
     * Checks whether a CommandSender has this permission
     *
     * @param sender to check the permission for
     * @param args to use for this permission node (appended to the node name)
     * @return True if the sender has permission for this node, False if not
     * @throws IllegalArgumentException If incorrect number of arguments were specified
     */
    default boolean has(CommandSender sender, String... args) {
        if (this.getArgumentCount() > args.length) {
            throw new IllegalArgumentException("This permission requires " + this.getArgumentCount()
                    + " arguments, but " + args.length + " were provided");
        }

        if (args.length == 0) {
            // No-argument version
            return CommonUtil.hasPermission(sender, this.getName());
        } else {
            String[] fragments = this.getRootName().split("\\.");
            return CommonUtil.hasPermission(sender, LogicUtil.appendArray(fragments, args));
        }
    }

    /**
     * Checks whether a CommandSender has this permission<br>
     * If the sender does not have this permission, the message specified is
     * sent
     *
     * @param sender to check the permission for
     * @param message to send if the sender has no permission
     * @return True if the sender has permission, False if not
     * @throws IllegalArgumentException If this permission requires arguments
     */
    default boolean handleMsg(CommandSender sender, String message) {
        return handleMsg(sender, message, StringUtil.EMPTY_ARRAY);
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
     * @throws IllegalArgumentException If incorrect number of arguments were specified
     */
    default boolean handleMsg(CommandSender sender, String message, String... args) {
        if (this.has(sender, args)) {
            return true;
        }
        sender.sendMessage(message);
        return false;
    }

    /**
     * Checks whether a CommandSender has this permission<br>
     * If the sender does not have this permission, a NoPermissionException is
     * thrown
     *
     * @param sender to check the permission for
     * @throws NoPermissionException If sender does not have permission
     * @throws IllegalArgumentException If this permission requires arguments
     */
    default void handle(CommandSender sender) {
        this.handle(sender, StringUtil.EMPTY_ARRAY);
    }

    /**
     * Checks whether a CommandSender has this permission<br>
     * If the sender does not have this permission, a NoPermissionException is
     * thrown
     *
     * @param sender to check the permission for
     * @param args to use for this permission node (appended to the node name)
     * @throws NoPermissionException If sender does not have permission
     * @throws IllegalArgumentException If incorrect number of arguments were specified
     */
    default void handle(CommandSender sender, String... args) {
        if (this.getArgumentCount() > args.length) {
            throw new IllegalArgumentException("This permission requires " + this.getArgumentCount()
                    + " arguments, but " + args.length + " were provided");
        }

        if (args.length == 0) {
            // No-argument version
            String rootName = this.getRootName();
            if (!CommonUtil.hasPermission(sender, rootName)) {
                throw new NoPermissionException(rootName);
            }
        } else {
            String[] fragments = this.getRootName().split("\\.");
            String[] permPath = LogicUtil.appendArray(fragments, args);
            if (!CommonUtil.hasPermission(sender, permPath)) {
                throw new NoPermissionException(String.join(".", permPath));
            }
        }
    }
}
