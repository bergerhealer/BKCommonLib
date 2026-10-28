package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.players.PlayerListHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import org.junit.Assume;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerDataMigrationTest {
    private PlayerListHandle paperPlayerList() throws Exception {
        Assume.assumeTrue(Common.evaluateMCVersion(">=", "1.20.5"));
        Assume.assumeTrue(CommonUtil.getClass("ca.spottedleaf.dataconverter.minecraft.MCDataConverter") != null);
        CommonUtil.getClass("net.minecraft.SharedConstants").getMethod("tryDetectVersion").invoke(null);
        // Paper's converter does not require the PlayerDataStorage's vanilla DataFixer.
        // Leaving it unset also detects accidentally routing back through vanilla DFU.
        return PlayerListHandle.createHandle(ClassTemplate.create("net.minecraft.server.dedicated.DedicatedPlayerList").newInstanceNull());
    }

    @Test
    public void testPaperMigratesLegacyInventoryAndPreservesPluginMetadata() throws Exception {
        PlayerListHandle playerList = paperPlayerList();
        CommonTagCompound data = new CommonTagCompound();
        data.putValue("DataVersion", 3700); // Minecraft 1.20.4, before item data components
        CommonTagCompound item = new CommonTagCompound();
        item.putValue("id", "minecraft:stone");
        item.putValue("Count", (byte) 3);
        item.putValue("Slot", (byte) 0);
        data.createList("Inventory").add(item);
        data.createCompound("bukkit").putValue("lastKnownName", "MigrationTest");
        data.createCompound("MyWorlds").putValue("customMetadata", "preserved");

        CommonTagCompound result = playerList.migratePlayerData(data);
        CommonTagList inventory = result.get("Inventory", CommonTagList.class);
        assertNotNull(inventory);
        assertEquals(1, inventory.size());
        CommonTagCompound migratedItem = (CommonTagCompound) inventory.get(0);
        assertEquals("minecraft:stone", migratedItem.getValue("id", String.class));
        assertEquals(Integer.valueOf(3), migratedItem.getValue("count", Integer.class));
        assertEquals(Byte.valueOf((byte) 0), migratedItem.getValue("Slot", Byte.class));
        assertEquals("MigrationTest", result.getCompoundOrEmpty("bukkit").getValue("lastKnownName", String.class));
        assertEquals("preserved", result.getCompoundOrEmpty("MyWorlds").getValue("customMetadata", String.class));
    }

    @Test
    public void testPaperPreservesCurrentProfile() throws Exception {
        PlayerListHandle playerList = paperPlayerList();
        CommonTagCompound data = new CommonTagCompound();
        int currentVersion = (Integer) CommonUtil.getClass("ca.spottedleaf.dataconverter.minecraft.util.Version")
                .getMethod("getCurrentVersion").invoke(null);
        data.putValue("DataVersion", currentVersion);
        data.createCompound("bukkit").putValue("lastPlayed", 123456789L);
        CommonTagCompound expected = data.clone();

        assertEquals(expected, playerList.migratePlayerData(data));
    }
}
