package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.server.*;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.templates.TemplateResolver;

import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Common {
    /**
     * Used by {@link #hasCapability(String)}
     */
    private static final Set<String> CAPABILITIES = Stream.of(
            "Common:Capabilities",
            "Common:EntityController:SetBlockActivationEnabled",
            "Common:PacketPlayOutUpdateAttributes:createZeroMaxHealth",
            "Common:Yaml:ChangeListeners",
            "Common:Yaml:CloneAndSetToWithFixes",
            "Common:IPermissionEnum",
            "Common:FishingHookFixes1.16",
            "Common:Chunk:FutureProvider",
            "Common:IntCuboid",
            "Common:WorldBlockBorder",
            "Common:EntityController:FixedOnDieDuringTeleport",
            "Common:WorldUtil:getDefaultNetherPortalSearchRadius",
            "Common:Item:CreatePlayerHeadUsingGameProfile",
            "Common:VehicleMountController:Spectating",
            "Common:WorldUtil:getWorldLevelFile",
            "Common:EntityController:forceControllerInitialization",
            "Common:EntityNetworkController:HasOnPassengersChanged",
            "Common:Localization:InitDefaults",
            "Common:BlockData:EmissionBlockParameter",
            "Common:SignChangeTracker",
            "Common:EntityUtil:GetSetEquipmentSlot",
            "Common:ChatText:MultiLineSupport",
            "Common:EntityUtil:PortalWaitDelay",
            "Common:MapDisplay:BoundsChangeViewFix",
            "Common:EntitySlimeHandle",
            "Common:PlayerGameInfo",
            "Common:Yaml:BetterChangeListeners",
            "Common:DisplayEntity:Brightness",
            "Common:PacketPlayOutEntityEquipment:OwnerType",
            "Common:BlockDataStateRename",
            "Common:Advancement:RewardDisabler",
            "Common:Sound:StopSoundPacket",
            "Common:Sound:CloudParser",
            "Common:Event:PlayerAdvancementProgressEvent",
            "Common:Yaml:ChildWithLiteralName",
            "Common:ChunkUtil:getChunkViewers",
            "Common:SignChangeTracker:FormattedText",
            "Common:ConnectionResetAwaitTeleport",
            "Common:EntityController:PositionPassenger",
            "Common:CommonItemStack",
            "Common:EquipmentSlot:IsSupportedCheck",
            "Common:Attributes:RemoveAllModifiers",
            "Common:Attributes:GetAllAttributes",
            "Common:Player:SetSkinMetadata",
            "Common:PacketPlayInBlockPlace:RotationApi",
            "Common:PlayerInstancePhase",
            "Common:SignEditTextEvent"
    ).collect(Collectors.toSet());

    /**
     * BKCommonLib version number, use this to set your dependency version for
     * BKCommonLib-using plugins<br>
     * <b>Use getVersion() instead if you want the actual, current version!
     * Constants get inlined when compiling!</b>
     */
    public static final int VERSION = 12103;
    /**
     * Defines the Minecraft version that runs on the server.
     */
    public static final String MC_VERSION;
    /**
     * Defines the net.minecraft.server constant (which is not inlined or
     * relocated). Implementer note: do NOT change this to a constant or maven
     * shading will rename it.
     */
    public static final String NMS_ROOT = StringUtil.join(".", "net", "minecraft", "server");
    /**
     * Defines the org.bukkit.craftbukkit constant (which is not inlined or
     * relocated). Implementer note: do NOT change this to a constant or maven
     * shading will rename it.
     */
    public static final String CB_ROOT = StringUtil.join(".", "org", "bukkit", "craftbukkit");
    /**
     * Defines the com.bergerkiller.bukkit.common root path of this library
     */
    public static final String COMMON_ROOT = "com.bergerkiller.bukkit.common";
    /**
     * Defines the type of server BKCommonLib is currently running on and
     * provides server-specific implementations.
     */
    public static final CommonServer SERVER;
    /**
     * Resolves template Class Declarations at runtime
     */
    public static final TemplateResolver TEMPLATE_RESOLVER;
    /**
     * Gets whether the current server software used is the Spigot
     * implementation
     */
    public static final boolean IS_SPIGOT_SERVER;
    /**
     * Gets whether the current server software used is the PaperSpigot
     * implementation
     */
    public static final boolean IS_PAPERSPIGOT_SERVER;
    /**
     * Gets whether the current server software used is the Purpur
     * implementation (based on Paper Spigot)
     */
    public static final boolean IS_PURPUR_SERVER;
    /**
     * Whether BKCommonLib is compatible with the server it is currently running
     * on
     */
    public static final boolean IS_COMPATIBLE;
    /**
     * Server or test level logger that is in use
     */
    public static final ModuleLogger LOGGER;
    /**
     * Whether BKCommonLib is run under test, indicating not all server functionality is available
     */
    public static final boolean IS_TEST_MODE;

    /**
     * When under test internal classes have to be loaded in a very specific order.
     * This function makes sure the server registers are initialized.
     * 
     * @deprecated Moved to internal, use CommonBootstrap
     */
    @Deprecated
    public static void bootstrap() {
        CommonBootstrap.initCommonServerAssertCompatibility();
    }

    static {
        // Used to detect whether running in a live environment, or under test
        IS_TEST_MODE = CommonBootstrap.isTestMode();

        // Depends on whether Bukkit server is initialized
        LOGGER = Logging.LOGGER;

        // Set up the constants
        SERVER = CommonBootstrap.initCommonServer();
        MC_VERSION = SERVER.getMinecraftVersion();
        IS_SPIGOT_SERVER = CommonBootstrap.isSpigotServer();
        IS_PAPERSPIGOT_SERVER = CommonBootstrap.isPaperServer();
        IS_PURPUR_SERVER = CommonBootstrap.isPurpurServer();
        IS_COMPATIBLE = CommonBootstrap.initCommonServerCheckCompatibility();
        TEMPLATE_RESOLVER = CommonBootstrap.initTemplates();
    }

    /**
     * Gets the BKCommonLib version number, use this function to compare your
     * own version with the currently installed version
     *
     * @return BKCommonLib version number
     */
    public static int getVersion() {
        return VERSION;
    }

    /**
     * Loads one or more classes<br>
     * Use this method to pre-load certain classes before enabling your plugin
     *
     * @param classNames to load
     */
    public static void loadClasses(String... classNames) {
        for (String className : classNames) {
            try {
                loadInner(Class.forName(className));
            } catch (ExceptionInInitializerError error) {
                throw new RuntimeException("An error occurred trying to initialize class '" + className + "':", error);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException("Could not load class '" + className + "' - Update needed?");
            }
        }
    }

    private static void loadInner(Class<?> clazz) {
        for (Class<?> subclass : clazz.getDeclaredClasses()) {
            loadInner(subclass);
        }
    }

    /**
     * Handles a reflection field or method missing<br>
     * Has a special handler for fields and methods defined inside this library
     *
     * @param type of object: field or method
     * @param name of the field or method
     * @param source class for the field or method
     */
    protected static void handleReflectionMissing(String type, String name, Class<?> source) {
        String msg = type + " '" + name + "' does not exist in class file " + source.getSimpleName();
        Exception ex = new Exception();
        for (StackTraceElement elem : ex.getStackTrace()) {
            if (elem.getClassName().startsWith(COMMON_ROOT + ".reflection")) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, msg + " (Update BKCommonLib?)", ex);
                return;
            }
        }
        Logging.LOGGER_REFLECTION.log(Level.SEVERE, msg, ex);
    }

    /**
     * Checks if the Minecraft version matches a version condition
     * 
     * @param operand to evaluate with, for example ">=" and "!="
     * @param version the operand is applied to (right side)
     * @return True if the version matches, False if not
     */
    public static boolean evaluateMCVersion(String operand, String version) {
        return CommonBootstrap.evaluateMCVersion(operand, version);
    }

    /**
     * For supporting multiple BKCommonLib versions, use this function to check whether
     * a given capability is supported. Capabilities can be features, internal implementation differences,
     * or the removal of features. It is not recommended to depend on this in your plugin.
     * If you do, make sure to remove such code when the minimum supported version of BKCommonLib guarantees
     * such a feature is supported.
     * 
     * @param capability
     * @return True if supported
     */
    public static boolean hasCapability(String capability) {
        return CAPABILITIES.contains(capability);
    }
}
