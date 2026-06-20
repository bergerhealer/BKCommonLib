package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import org.bukkit.ChatColor;

/**
 * Converts Bukkit ChatColor &lt;&gt; and net.minecraft.world.scores.TeamColor
 */
public class TeamColorConversion {
    static {
        CommonBootstrap.initCommonServer();
    }
    private static final Class<?> teamColorType = CommonUtil.getClass("net.minecraft.world.scores.TeamColor");
    private static final Enum<?>[] teamColorValues = (teamColorType == null) ? null : (Enum<?>[]) teamColorType.getEnumConstants();
    private static final ChatColor[] bukkitChatColors = ChatColor.values();

    public static boolean isInitialized() {
        return teamColorValues != null; // Anything else will have errored
    }

    @ConverterMethod(output="net.minecraft.world.scores.TeamColor")
    public static Object fromBukkit(org.bukkit.ChatColor color) {
        int ord = color.ordinal();
        if (ord < teamColorValues.length) {
            return teamColorValues[ord];
        } else {
            throw new IllegalArgumentException("Invalid team color: " + color.name());
        }
    }

    @ConverterMethod(input="net.minecraft.world.scores.TeamColor")
    public static org.bukkit.ChatColor toBukkit(Object teamColor) {
        if (teamColor == null) {
            return null;
        } else if (!teamColorType.isInstance(teamColor)) {
            throw new IllegalArgumentException("Not a TeamColor: " + teamColor.getClass().getName());
        } else {
            int ord = ((Enum<?>) teamColor).ordinal();
            return bukkitChatColors[ord];
        }
    }
}
