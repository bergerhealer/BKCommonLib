package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.map.MapResourcePack;

/**
 * The default Minecraft resources are (down)loaded by this class<br>
 * <br>
 * The Minecraft client resources are owned by Mojang. These assets are installed separately from BKCommonLib.
 * BKCommonLib, nor its developers, distribute (illegal) copies of the Minecraft client.
 * We assume you have already accepted the Minecraft EULA to run the server. If you have not,
 * please read <a href="https://account.mojang.com/documents/minecraft_eula">https://account.mojang.com/documents/minecraft_eula.</a>
 */
public class VanillaResourcePack extends MapResourcePack {

    public VanillaResourcePack() {
        super((MapResourcePack) null, "default");
    }

    /**
     * If not already installed, calling this method will automatically download the
     * appropriate Vanilla Minecraft Client jar from the official servers and install them
     * so textures and models can be loaded from them.<br>
     * <br>
     * The Minecraft client resources are owned by Mojang. These assets are installed separately from BKCommonLib.
     * BKCommonLib, nor its developers, distribute (illegal) copies of the Minecraft client.
     * We assume you have already accepted the Minecraft EULA to run the server. If you have not,
     * please read <a href="https://account.mojang.com/documents/minecraft_eula">https://account.mojang.com/documents/minecraft_eula.</a>
     */
    public void load() {
        super.load();
    }

}
