package com.bergerkiller.bukkit.common.conversion.type;

import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;

/**
 * Handles type conversion for Minecraft 1.17 and later
 */
public class MC1_17_Conversion {
    private static final FastMethod<Object> spcGetEntityPlayerMethod = new FastMethod<Object>();
    private static final FastField<Object> epConnectionField = new FastField<Object>();

    public static void init() {
        Class<?> entityPlayerType = Resolver.loadClass("net.minecraft.server.level.EntityPlayer", false);
        Class<?> spcType = Resolver.loadClass("net.minecraft.server.network.ServerPlayerConnection", false);

        try {
            String methodName = "d"; // 1.17
            if (CommonBootstrap.evaluateMCVersion(">=", "1.18")) {
                methodName = Resolver.resolveMethodName(spcType, "getPlayer", new Class[0]);
            }

            if (spcType == null) {
                throw new IllegalStateException("ServerPlayerConnection class not found");
            }
            spcGetEntityPlayerMethod.init(spcType.getDeclaredMethod(methodName));
            if (spcGetEntityPlayerMethod.getMethod().getReturnType() != entityPlayerType) {
                throw new IllegalStateException("Method does not return EntityPlayer, method not found");
            }
        } catch (Throwable t) {
            Logging.LOGGER_CONVERSION.log(Level.SEVERE, "Failed to initialize getEntityPlayer()", t);
            spcGetEntityPlayerMethod.initUnavailable("ServerPlayerConnection getEntityPlayer() is not available");
        }

        try {
            String fieldName = "connection"; // 1.17
            fieldName = Resolver.resolveFieldName(entityPlayerType, fieldName);

            if (spcType == null) {
                throw new IllegalStateException("ServerPlayerConnection class not found");
            }
            epConnectionField.init(MPLType.getDeclaredField(entityPlayerType, fieldName));
            if (!spcType.isAssignableFrom(epConnectionField.getType())) {
                throw new IllegalStateException("Field not assignable to ServerPlayerConnection");
            }
        } catch (Throwable t) {
            Logging.LOGGER_CONVERSION.log(Level.SEVERE, "Failed to initialize EntityPlayer.connection field", t);
            epConnectionField.initUnavailable("EntityPlayer connection field is not available");
        }
    }

    @ConverterMethod(input="net.minecraft.server.network.ServerPlayerConnection", output="net.minecraft.server.level.EntityPlayer")
    public static Object serverConnectionToEntityPlayer(Object nmsServerPlayerConnection) {
        return spcGetEntityPlayerMethod.invoke(nmsServerPlayerConnection);
    }

    @ConverterMethod(input="net.minecraft.server.level.EntityPlayer", output="net.minecraft.server.network.ServerPlayerConnection")
    public static Object entityPlayerToServerConnection(Object nmsEntityPlayer) {
        return epConnectionField.get(nmsEntityPlayer);
    }

    @ConverterMethod(input="net.minecraft.server.network.ServerPlayerConnection")
    public static Player serverConnectionToBukkitPlayer(Object nmsServerPlayerConnection) {
        return (Player) WrapperConversion.toEntity(serverConnectionToEntityPlayer(nmsServerPlayerConnection));
    }

    @ConverterMethod(output="net.minecraft.server.network.ServerPlayerConnection")
    public static Object bukkitPlayerToServerConnection(Player player) {
        return entityPlayerToServerConnection(HandleConversion.toEntityHandle(player));
    }
}
