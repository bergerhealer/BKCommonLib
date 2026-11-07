# Permission Examples

BKCommonLib's permission helpers let a plugin define permission nodes as reusable constants, including their Bukkit default and a description. For an enum, implement `IPermissionEnum` (the enum-friendly counterpart to the `PermissionEnum` base class):

```java
import com.bergerkiller.bukkit.common.permissions.IPermissionEnum;
import org.bukkit.permissions.PermissionDefault;

public enum PluginPermission implements IPermissionEnum {
    // Granted to server operators by default
    ADMIN("yourplugin.admin", PermissionDefault.OP,
            "Allows administrative commands"),

    // Not granted by default
    USE_FEATURE("yourplugin.feature.use", PermissionDefault.FALSE,
            "Allows use of the special feature");

    private final String node;
    private final PermissionDefault defaultValue;
    private final String description;

    PluginPermission(String node, PermissionDefault defaultValue, String description) {
        this.node = node;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    @Override
    public String getRootName() {
        return node;
    }

    @Override
    public PermissionDefault getDefault() {
        return defaultValue;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
```

If you prefer a regular constants class instead of an enum, extend `PermissionEnum` and declare each permission as a `public static final` constant. `PermissionEnum` provides the permission name, default, description, and helper methods:

```java
import com.bergerkiller.bukkit.common.permissions.PermissionEnum;
import org.bukkit.permissions.PermissionDefault;

public final class PluginPermissions extends PermissionEnum {
    public static final PluginPermissions ADMIN = new PluginPermissions(
            "yourplugin.admin",
            PermissionDefault.OP,
            "Allows administrative commands"
    );

    public static final PluginPermissions USE_FEATURE = new PluginPermissions(
            "yourplugin.feature.use",
            PermissionDefault.FALSE,
            "Allows use of the special feature");

    private PluginPermissions(String node, PermissionDefault defaultValue, String description) {
        super(node, defaultValue, description);
    }
}
```

Register all constants from the plugin lifecycle by overriding `permissions()` in the main plugin class and calling `loadPermissions()`. Pass the enum class or the constants class; BKCommonLib discovers the enum values or static permission fields, respectively:

```java
import com.bergerkiller.bukkit.common.PluginBase;

public class YourPlugin extends PluginBase {
    @Override
    public void permissions() {
        loadPermissions(PluginPermission.class); // enum-based declaration
        // Or: loadPermissions(PluginPermissions.class); // static constants
    }
}
```

`PermissionDefault.OP` grants the permission to operators by default. `PermissionDefault.FALSE` denies it by default. BKCommonLib registers each node with its configured default and description, and lets administrators customize those defaults in `plugins/Your_Plugin/PermissionDefaults.yml`. The configured defaults apply unless a permission is assigned to a player or group.

Check permissions through the constant rather than repeating the node string. Both declaration styles support the same checks:

```java
import org.bukkit.command.CommandSender;

if (PluginPermission.ADMIN.has(sender)) {
    // Perform the administrative action
}

if (PluginPermissions.ADMIN.has(sender)) {
    // Same check using the static-constants style
}
```

Enums implement `IPermissionEnum` because Java enums cannot extend `PermissionEnum`. Use the enum style when keeping permissions together in an enum is convenient, or static `PermissionEnum` fields when you prefer named constants on a regular class.
