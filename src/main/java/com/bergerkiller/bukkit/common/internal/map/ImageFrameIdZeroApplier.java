package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * When the ImageFrame plugin is installed and a Map Display is used for the first time,
 * makes sure map id 0 is added to the "exemptMapIdsFromDeletion" configuration.
 * This prevents map items going empty when map id 0 is deleted within that plugin.<br>
 * <br>
 * See also: https://github.com/LOOHP/ImageFrame/commit/f077708ad1e31ee1b10c47c91a7222b12cbbb748<br>
 * <br>
 * Because apply() is called only once a player views a display, which must be a tick delayed,
 * there is no worry that the plugin isn't enabled yet when run.
 */
class ImageFrameIdZeroApplier {
    private static boolean applied = false;

    public static void apply() {
        if (applied) {
            return;
        } else {
            applied = true; // Don't try again
        }

        // Do nothing if not running
        Plugin imageFramePlugin = Bukkit.getPluginManager().getPlugin("ImageFrame");
        if (imageFramePlugin == null) {
            return;
        }

        // Try to find the "exemptMapIdsFromDeletion" field
        Class<?> imageFrameClass = imageFramePlugin.getClass();
        java.util.Collection<Object> exemptIdSet;
        try {
            java.lang.reflect.Field exemptField = imageFrameClass.getField("exemptMapIdsFromDeletion");
            exemptField.setAccessible(true); // Just in case it goes private at some point
            Object exemptIdSetObj = exemptField.get(null);
            if (!(exemptIdSetObj instanceof java.util.Collection)) {
                return;
            }
            exemptIdSet = LogicUtil.unsafeCast(exemptIdSetObj);
        } catch (Throwable t) {
            return;
        }

        // Must have method "satisfies" - call it with id=0 to check if not already added
        try {
            java.lang.reflect.Method satisfiedMethod = exemptIdSet.getClass().getMethod("satisfies", int.class);
            if (satisfiedMethod.getReturnType() != boolean.class) {
                return;
            }
            if (satisfiedMethod.invoke(exemptIdSet, 0).equals(Boolean.TRUE)) {
                return; // Already added
            }
        } catch (Throwable t) {
            return;
        }

        // Try to modify the exempt id set (type: List<IntRange>)
        Object zeroMapIdIntRange;
        try {
            Class<?> intRangeType = Class.forName("com.loohp.imageframe.objectholders.IntRange");
            zeroMapIdIntRange = intRangeType.getMethod("of", int.class).invoke(null, 0);
            if (zeroMapIdIntRange == null) {
                return;
            }
        } catch (Throwable t) {
            return;
        }

        // Add it to the List
        try {
            exemptIdSet.add(zeroMapIdIntRange);
        } catch (Throwable t) {
            return;
        }

        // Tell everything worked out OK for future debugging reasons
        Logging.LOGGER_MAPDISPLAY.info("Added Map ID #0 to ImageFrame's 'exempt from deletion' set to avoid maps going empty");
    }
}
