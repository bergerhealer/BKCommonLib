package com.bergerkiller.bukkit.common.server;

import java.util.Map;

import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;

/**
 * Special Spigot for Minecraft 1.8.8 kept up to date with the latest security fixes.<br>
 * <br>
 * https://github.com/CobbleSword/NachoSpigot
 */
public class NachoSpigotServer extends SpigotServer {

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }

        try {
            MPLType.getClassByName("dev.cobblesword.nachospigot.Nacho");
        } catch (ClassNotFoundException ex) {
            return false;
        }

        return true;
    }

    @Override
    public String getServerName() {
        return "NachoSpigot";
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("nachospigot", "true");
    }
}
