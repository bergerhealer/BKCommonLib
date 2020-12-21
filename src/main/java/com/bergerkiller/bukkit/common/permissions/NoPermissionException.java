package com.bergerkiller.bukkit.common.permissions;

/**
 * Thrown when someone or something has no permission
 */
public class NoPermissionException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String permission;

    /**
     * Initializes a new NoPermissionException without permission
     * node details
     */
    public NoPermissionException() {
        this("");
    }

    /**
     * Initializes a new NoPermissionException
     * 
     * @param permission Permission node that failed a check
     */
    public NoPermissionException(String permission) {
        super(permission.isEmpty() ? "No permission" : "No '" + permission + "' permission");
        this.permission = permission;
    }

    /**
     * Gets the permission node that failed, causing this exception
     * 
     * @return permission node that caused this exception. If no
     *         permission node is involved, this String is empty.
     */
    public String getPermission() {
        return this.permission;
    }
}
