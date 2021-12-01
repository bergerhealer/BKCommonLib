package com.bergerkiller.bukkit.common.server;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.asm.ASMUtil;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;

class MinecraftVersionDiscovery {
    private final String NMS_ROOT_VERSIONED;
    private final String CB_ROOT_VERSIONED;
    private Class<?> typeMinecraftServer;
    private Class<?> typeMinecraftVersion;
    private Class<?> typeSharedConstants;
    private Class<?> typeGameVersion;
    private Method getVersionMethod;
    private Method getMinecraftVersionMethodBukkitServer;

    public MinecraftVersionDiscovery(String packageVersion) {
        if (packageVersion.isEmpty()) {
            NMS_ROOT_VERSIONED = "net.minecraft.server";
            CB_ROOT_VERSIONED = "org.bukkit.craftbukkit";
        } else {
            NMS_ROOT_VERSIONED = "net.minecraft.server." + packageVersion;
            CB_ROOT_VERSIONED = "org.bukkit.craftbukkit." + packageVersion;
        }

        try {
            typeMinecraftServer = loadClass("net.minecraft.server.MinecraftServer");
        } catch (ClassNotFoundException ex) {
            typeMinecraftServer = tryLoadClass(NMS_ROOT_VERSIONED + ".MinecraftServer");
        }

        try {
            typeMinecraftVersion = loadClass("net.minecraft.MinecraftVersion");
        } catch (ClassNotFoundException ex) {
            typeMinecraftVersion = tryLoadClass(NMS_ROOT_VERSIONED + ".MinecraftVersion");
        }

        try {
            typeSharedConstants = loadClass("net.minecraft.SharedConstants");
        } catch (ClassNotFoundException ex) {
            typeSharedConstants = tryLoadClass(NMS_ROOT_VERSIONED + ".SharedConstants");
        }

        if (typeMinecraftServer != null) {
            try {
                getVersionMethod = getDeclaredMethod(typeMinecraftServer, "getVersion");
                if (!getVersionMethod.getReturnType().equals(String.class)) {
                    getVersionMethod = null; // Invalid return type
                }
            } catch (NoSuchMethodException ex) {
                /* 1.18 and later, or an unsupported server */
                getVersionMethod = null;
            }
        } else {
            getVersionMethod = null;
        }

        typeGameVersion = tryLoadClass("com.mojang.bridge.game.GameVersion");

        // Paper only
        try {
            getMinecraftVersionMethodBukkitServer = Server.class.getDeclaredMethod("getMinecraftVersion");
            if (!getMinecraftVersionMethodBukkitServer.getReturnType().equals(String.class)) {
                getMinecraftVersionMethodBukkitServer = null;
            }
        } catch (NoSuchMethodException ex) {
            /* Not paper or too old */
            getMinecraftVersionMethodBukkitServer = null;
        }
    }

    public String detect() throws VersionIdentificationFailureException {
        // If a server instance exists that happens to be a Paper server which
        // has a simple getter for the version, prefer that over any other method
        // we dream up here.
        if (Bukkit.getServer() != null && getMinecraftVersionMethodBukkitServer != null) {
            try {
                return (String) getMinecraftVersionMethodBukkitServer.invoke(Bukkit.getServer());
            } catch (Throwable t) {
                /* ignore, try fallback methods */
                Logging.LOGGER.log(Level.WARNING, "An error occurred calling Server::getMinecraftVersion()", t);
            }
        }

        // Check if a server instance exists. If so, this is not under test,
        // and it becomes much easier to identify the exact minecraft version.
        // No mock server needs to be setup for this to work. We just retrieve the
        // MinecraftServer handle instance and call getVersion() on it. Won't work
        // on 1.18 and later where getVersion() is obfuscated and unsafe to be called.
        if (Bukkit.getServer() != null && getVersionMethod != null) {
            return detectUsingBukkitServerHandleGetVersion();
        }

        // If getVersion() does exist, but MinecraftVersion class doesn't, then this is
        // very likely an old server where the method returned a String constant. In that case,
        // use ASM to analyze the method bytecode and obtain the String constant that way.
        // This avoids initializing the MinecraftServer class, which is very slow.
        if (typeMinecraftVersion == null && typeSharedConstants == null && getVersionMethod != null) {
            String fromASM = ASMUtil.findStringConstantReturnedByMethod(getVersionMethod);
            if (fromASM != null) {
                return fromASM;
            } else {
                Logging.LOGGER.log(Level.WARNING, "Failed to detect version using ASM analysis of MinecraftServer::getVersion()");
            }
        }

        // If SharedConstants class exists, and the server is initialized, try to read the version from there
        // We don't reliably know the name of the method to call, since on 1.18 and later this is obfuscated
        // But we do know what the return type is, so it's pretty easy to find.
        // If there is no server instance it cannot be used, because then the shared constants class static
        // fields aren't initialized yet (all null) and this will just fail.
        if (Bukkit.getServer() != null && typeSharedConstants != null && typeGameVersion != null) {
            Method m = findStaticGetGameVersionMethod(typeSharedConstants); // getGameVersion()
            if (m != null) {
                Bukkit.getVersion(); // This might initialize some stuff, just in case!
                Object gameVersion = null;
                try {
                    gameVersion = m.invoke(null);
                } catch (Throwable t) {
                    /* failed */
                    Logging.LOGGER.log(Level.WARNING, "An error occurred calling SharedConstants getGameVersion()", t);
                }
                if (gameVersion != null) {
                    return getGameVersionName(gameVersion);
                }
            } else {
                Logging.LOGGER.log(Level.WARNING, "Failed to find SharedConstants::getGameVersion()");
            }
        }

        // If the MinecraftVersion class exists, try to see if we can call the static method on it
        // This method, called 'tryDetectVersion', does all the work of reading from a stored json
        // file and decode it into a GameVersion. If it succeeds, all is well. This method will also
        // work if no server instance is initialized yet (under test), but it is slower.
        if (typeMinecraftVersion != null && typeGameVersion != null) {
            Method m = findStaticGetGameVersionMethod(typeMinecraftVersion); // tryDetectVersion()
            if (m != null) {
                Object gameVersion = null;
                try {
                    gameVersion = m.invoke(null);
                } catch (Throwable t) {
                    /* failed */
                    Logging.LOGGER.log(Level.WARNING, "An error occurred calling MinecraftVersion tryDetectVersion()", t);
                }
                if (gameVersion != null) {
                    return getGameVersionName(gameVersion);
                }
            } else {
                Logging.LOGGER.log(Level.WARNING, "Failed to find MinecraftVersion::tryDetectVersion()");
            }
        }

        // Last-ditch effort of initializing a new DedicatedServer instance and calling the method on that
        // This will likely fail, but we've tried.
        if (getVersionMethod != null) {
            // Find DedicatedServer object, differs on 1.17 and later
            Class<?> dedicatedServerClass;
            try {
                dedicatedServerClass = loadClass("net.minecraft.server.dedicated.DedicatedServer");
            } catch (ClassNotFoundException ex) {
                try {
                    dedicatedServerClass = loadClass(NMS_ROOT_VERSIONED + ".DedicatedServer");
                } catch (ClassNotFoundException ex2) {
                    throw new VersionIdentificationFailureException("DedicatedServer class not found");
                }
            }

            // Create an instance of Minecraft Server without calling any constructors
            // This is a bit slower, but works as a reliable fallback
            Logging.LOGGER.warning("Failed to find Minecraft Version efficiently, falling back to slower null-constructing");
            ClassTemplate<?> nms_server_tpl = ClassTemplate.create(dedicatedServerClass);
            Object minecraftServerInstance = nms_server_tpl.newInstanceNull();
            try {
                return (String) getVersionMethod.invoke(minecraftServerInstance);
            } catch (Throwable t) {
                throw new VersionIdentificationFailureException(t);
            }
        }

        throw new VersionIdentificationFailureException("Failed to detect a way to retrieve the Minecraft Version information");
    }

    private String detectUsingBukkitServerHandleGetVersion() {
        Server server = Bukkit.getServer();

        // Locate CraftServer class
        Class<?> typeCraftServer;
        try {
            typeCraftServer = loadClass(CB_ROOT_VERSIONED + ".CraftServer");
        } catch (ClassNotFoundException ex) {
            typeCraftServer = server.getClass(); // Fallback that'll probably work
        }

        // Try to find the server's getServer() method. If the found typeCraftServer
        // is actually an extension of another class, we might need to dig further
        // and check superclasses as well.
        java.lang.reflect.Method getServerMethod = null;
        for (Class<?> c = typeCraftServer; c != Object.class; c = c.getSuperclass()) {
            try {
                getServerMethod = getDeclaredMethod(c, "getServer");
            } catch (NoSuchMethodException ex) { /* not found */ }
        }
        if (getServerMethod == null) {
            throw new VersionIdentificationFailureException("CraftServer method getServer() was not found");
        }

        // Obtain MinecraftServer instance from CraftServer::getServer()
        // Then call getVersion() on it
        try {
            Object minecraftServerInstance = getServerMethod.invoke(Bukkit.getServer());
            return (String) getVersionMethod.invoke(minecraftServerInstance);
        } catch (Throwable t) {
            throw new VersionIdentificationFailureException(t);
        }
    }

    private Method findStaticGetGameVersionMethod(Class<?> type) {
        for (Method m : type.getDeclaredMethods()) {
            if (m.getParameterCount() == 0 &&
                    typeGameVersion.isAssignableFrom(m.getReturnType()) &&
                Modifier.isStatic(m.getModifiers())
            ) {
                return m;
            }
        }
        return null;
    }

    private String getGameVersionName(Object gameVersion) throws VersionIdentificationFailureException {
        Method getNameMethod = null;
        try {
            getNameMethod = getDeclaredMethod(this.typeGameVersion, "getName");
            if (!getNameMethod.getReturnType().equals(String.class)) {
                getNameMethod = null;
            }
        } catch (NoSuchMethodException ex) {
            /* Failed */
        }
        if (getNameMethod == null) {
            throw new VersionIdentificationFailureException("Method String GameVersion::getName() does not exist");
        }

        try {
            return (String) getNameMethod.invoke(gameVersion);
        } catch (Throwable t) {
            throw new VersionIdentificationFailureException(t);
        }
    }

    private static Class<?> tryLoadClass(String name) {
        try {
            return loadClass(name);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    private static Class<?> loadClass(String name) throws ClassNotFoundException {
        return MPLType.getClassByName(Resolver.resolveClassPath(name));
    }

    private static Method getDeclaredMethod(Class<?> declaringClass, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        methodName = Resolver.resolveMethodName(declaringClass, methodName, parameterTypes);
        return MPLType.getDeclaredMethod(declaringClass, methodName, parameterTypes);
    }
}
