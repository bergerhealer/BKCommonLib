package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.io.VersionedMappingsFileIO;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import org.bukkit.Material;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Stores mappings from Mojang item/block id to Bukkit Material enum names.
 * These are tracked as a delta per data version historically to allow for proper
 * decoding of these regardless of server version.
 */
public class ItemStackDeserializerIdToMaterialMapper extends VersionedMappingsFileIO<Map<String, String>> {

    public ItemStackDeserializerIdToMaterialMapper() {
        super(Comparator.comparingInt(Integer::parseInt), m -> m.mappings);
    }

    /**
     * Loads the mappings from bundled resources file. If something is wrong or missing for this version
     * of the server, generates new mappings for this version.
     */
    public void loadMappings() {
        String idToMaterialMappingsFile = "/com/bergerkiller/bukkit/common/internal/resources/id_to_material_mappings.dat";
        try {
            try (InputStream in = ItemStackDeserializerIdToMaterialMapper.class.getResourceAsStream(idToMaterialMappingsFile)) {
                read(in);
            }
        } catch (IOException ex) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to read id-to-material mappings (corrupted jar?)", ex);
        }

        int dataVersion = CraftMagicNumbersHandle.getDataVersion();
        if (dataVersion < 4325) {
            // Before 1.21.5 this format was not used and no configurations of it will exist
            // A little redundant as getOrNewer will also block, but just to be safe.
            return;
        }

        if (!getOrNewer(Integer.toString(dataVersion)).isPresent()) {
            Logging.LOGGER.warning("Id-to-material mappings are missing for data version " + dataVersion + " and will be regenerated");
            storeCurrentDataVersion();
        }
    }

    /**
     * Stores all the id - material mappings for the current server data version.
     *
     * @return True if a new mapping was stored or was updated
     */
    public boolean storeCurrentDataVersion() {
        try {
            Method getKeyMethod = Material.class.getMethod("getKeyOrNull");
            String dataVersion = Integer.toString(CraftMagicNumbersHandle.getDataVersion());
            Map<String, String> newMappings = new HashMap<>();
            for (Material material : MaterialUtil.getAllMaterials()) {
                if (MaterialUtil.isLegacyType(material)) {
                    continue;
                }
                Object key = getKeyMethod.invoke(material);
                if (key == null) {
                    continue;
                }

                newMappings.put(key.toString(), material.name());
            }

            if (newMappings.equals(this.get(dataVersion).orElse(null))) {
                return false;
            }

            this.store(dataVersion, newMappings);
            return true;
        } catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }
}
