package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.JsonSerializer;
import com.bergerkiller.bukkit.common.inventory.CommonItemMaterials;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.com.mojang.authlib.properties.PropertyHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class JsonSerializerTest {
    final int dataVersion = CraftMagicNumbersHandle.getDataVersion();

    @Test
    public void testEmptyMapToJson() {
        JsonSerializer serializer = new JsonSerializer();
        Map<String, Object> jsonMap = new LinkedHashMap<>();
        jsonMap.put("key", Collections.emptyMap());
        String json = serializer.toJson(jsonMap);
        assertEquals("{\"key\":{}}", json);
    }

    @Test
    public void testItemStackToJsonStick() throws JsonSerializer.JsonSyntaxException {
        JsonSerializer serializer = new JsonSerializer();
        String json = serializer.itemStackToJson(CommonItemStack.create(CommonItemMaterials.STICK, 1).toBukkit());
        Map<String, Object> jsonMap = serializer.jsonToMap(json);
        assertEquals(dataVersion, ((Number) jsonMap.get("v")).intValue());
        assertEquals("STICK", jsonMap.get("type"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testItemStackToJsonPlayerHeadYa() throws JsonSerializer.JsonSyntaxException {
        JsonSerializer serializer = new JsonSerializer();

        GameProfileHandle profile = GameProfileHandle.createNew(
                UUID.fromString("04049c90-d3e9-4621-9caf-0000aaa58540"),
                "HeadDatabase");
        profile.putProperty("textures", PropertyHandle.createNew("textures",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjllNzVlMDQxNTFlN2NkZThlN2YxNDlkYWU5MmYwYzE0ZWY1ZjNmZjQ1Y2QzMjM5NTc4NzZiMGFkZDJjNDk0OSJ9fX0="));

        ConfigurationNode node = new ConfigurationNode();
        node.set("item", CommonItemStack.createPlayerSkull(profile)
                        .toBukkit());

        ConfigurationNode two = new ConfigurationNode();
        two.loadFromString(node.toString());
        System.out.println(two);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testItemStackToJsonPlayerHead() throws JsonSerializer.JsonSyntaxException {
        JsonSerializer serializer = new JsonSerializer();

        GameProfileHandle profile = GameProfileHandle.createNew(
                UUID.fromString("04049c90-d3e9-4621-9caf-0000aaa58540"),
                "HeadDatabase");
        profile.putProperty("textures", PropertyHandle.createNew("textures",
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjllNzVlMDQxNTFlN2NkZThlN2YxNDlkYWU5MmYwYzE0ZWY1ZjNmZjQ1Y2QzMjM5NTc4NzZiMGFkZDJjNDk0OSJ9fX0="));

        String json = serializer.itemStackToJson(
                CommonItemStack.createPlayerSkull(profile)
                        .toBukkit());

        //System.out.println(json);

        Map<String, Object> jsonMap = serializer.jsonToMap(json);

        // Just a rough check all the info is in there
        assertEquals(dataVersion, ((Number) jsonMap.get("v")).intValue());
        assertEquals("PLAYER_HEAD", jsonMap.get("type"));
        Map<String, Object> meta = (Map<String, Object>) jsonMap.get("meta");
        assertEquals("SKULL", meta.get("meta-type"));
        Map<String, Object> skullOwner = (Map<String, Object>) meta.get("skull-owner");
        assertEquals("04049c90-d3e9-4621-9caf-0000aaa58540", skullOwner.get("uniqueId"));
    }

    @Test
    public void testItemStackFromJsonStick() throws JsonSerializer.JsonSyntaxException {
        JsonSerializer serializer = new JsonSerializer();
        String json = "{\"v\":3953,\"type\":\"STICK\"}";
        CommonItemStack itemStack = CommonItemStack.of(serializer.fromJsonToItemStack(json));

        assertEquals(CommonItemMaterials.STICK, itemStack.getType());
        assertEquals(1, itemStack.getAmount());
    }

    @Test
    public void testItemStackFromJsonPlayerHead() throws JsonSerializer.JsonSyntaxException {
        JsonSerializer serializer = new JsonSerializer();

        String json = "{\"v\":3953,\"type\":\"PLAYER_HEAD\",\"meta\":{\"meta-type\":\"SKULL\",\"skull-owner\":{\"uniqueId\":\"04049c90-d3e9-4621-9caf-0000aaa58540\",\"name\":\"HeadDatabase\",\"properties\":[{\"name\":\"textures\",\"value\":\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjllNzVlMDQxNTFlN2NkZThlN2YxNDlkYWU5MmYwYzE0ZWY1ZjNmZjQ1Y2QzMjM5NTc4NzZiMGFkZDJjNDk0OSJ9fX0=\"}],\"==\":\"PlayerProfile\"},\"==\":\"ItemMeta\"}}";
        CommonItemStack itemStack = CommonItemStack.of(serializer.fromJsonToItemStack(json));

        assertEquals(CommonItemMaterials.SKULL, itemStack.getType());
        assertEquals(1, itemStack.getAmount());

        GameProfileHandle profile = itemStack.getSkullProfile();
        assertNotNull(profile);
        assertEquals(UUID.fromString("04049c90-d3e9-4621-9caf-0000aaa58540"), profile.getId());
        for (PropertyHandle prop : profile.getProperties("textures")) {
            assertEquals("textures", prop.getName());
            assertTrue(prop.getValue().length() > 10);
        }
    }
}
