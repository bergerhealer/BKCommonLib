package com.bergerkiller.bukkit.common.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.config.CompressedDataReader;
import com.bergerkiller.bukkit.common.config.CompressedDataWriter;
import com.bergerkiller.bukkit.common.map.util.MapUUID;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;

/**
 * Temporary file generated when the server is reloaded to preserve some
 * important information.
 *
 * TODO: Maybe make this into a neat API to preserve resources during a reload?
 *       For now, internal use only.
 */
public class CommonMapReloadFile {
    private static final int FORMAT_VERSION = 1;
    public final List<Integer> staticReservedIds = new ArrayList<>();
    public final List<DynamicMappedId> dynamicMappedIds = new ArrayList<>();
    public final List<ItemFrameDisplayUUID> itemFrameDisplayUUIDs = new ArrayList<>();

    private CommonMapReloadFile() {
    }

    /**
     * Attempts to load the reload file, if currently in a state of enabling
     * after a reload, and the reload file is of the same current server process run.
     *
     * @param plugin
     * @param callback Called with the loaded data, if applicable
     */
    public static void load(JavaPlugin plugin, Consumer<CommonMapReloadFile> callback) {
        File file = getSaveFile(plugin);
        if (!file.exists()) {
            return;
        }

        try {
            // If not reloading (server ticks is 0, indicating cold boot) do NOT load this file
            // Delete the file and end.
            if (MinecraftServerHandle.instance().getTicks() == 0) {
                return;
            }

            // Load the file, check valid and of current server run (by checking ticks since epoch timestamp)
            // If all good, pass to the callback
            final AtomicBoolean isValid = new AtomicBoolean(true);
            final CommonMapReloadFile reloadFile = new CommonMapReloadFile();
            if (new CompressedDataReader(file) {
                @Override
                public void read(DataInputStream stream) throws IOException {
                    int version = stream.readInt();
                    if (version != FORMAT_VERSION) {
                        isValid.set(false);
                        return;
                    }

                    if (stream.readInt() != MinecraftServerHandle.instance().getTicksSinceUnixEpoch()) {
                        isValid.set(false);
                        return;
                    }

                    int numStaticIds = stream.readInt();
                    for (int n = 0; n < numStaticIds; n++) {
                        reloadFile.staticReservedIds.add(stream.readInt());
                    }

                    int numDynamicIds = stream.readInt();
                    for (int n = 0; n < numDynamicIds; n++) {
                        reloadFile.dynamicMappedIds.add(DynamicMappedId.readFrom(stream));
                    }

                    int numItemFrames = stream.readInt();
                    for (int n = 0; n < numItemFrames; n++) {
                        reloadFile.itemFrameDisplayUUIDs.add(ItemFrameDisplayUUID.readFrom(stream));
                    }
                }
            }.read() && isValid.get()) {
                callback.accept(reloadFile);
            }
        } finally {
            // Delete the file after loading to avoid it sticking around
            file.delete();
        }
    }

    /**
     * Checks if currently reloading, and if so, asks the callback to fill a prepared
     * reload file with data, which is then saved.
     *
     * @param plugin
     * @param callback Callback called to populate the data to save
     */
    public static void save(JavaPlugin plugin, Consumer<CommonMapReloadFile> callback) {
        File file = getSaveFile(plugin);

        // If shutting down (and not reloading) delete the file and do NOT save
        if (CommonUtil.isShuttingDown()) {
            if (file.exists()) {
                file.delete();
            }
            return;
        }

        // Try to open the file and write to it, asking the callback
        // to populate the data to write
        if (!(new CompressedDataWriter(file) {
            @Override
            public void write(DataOutputStream stream) throws IOException {
                CommonMapReloadFile reloadFile = new CommonMapReloadFile();
                callback.accept(reloadFile);

                stream.writeInt(FORMAT_VERSION);
                stream.writeInt(MinecraftServerHandle.instance().getTicksSinceUnixEpoch());

                stream.writeInt(reloadFile.staticReservedIds.size());
                for (Integer id : reloadFile.staticReservedIds) {
                    stream.writeInt(id.intValue());
                }

                stream.writeInt(reloadFile.dynamicMappedIds.size());
                for (DynamicMappedId mapid : reloadFile.dynamicMappedIds) {
                    mapid.writeTo(stream);
                }

                stream.writeInt(reloadFile.itemFrameDisplayUUIDs.size());
                for (ItemFrameDisplayUUID displayUUID : reloadFile.itemFrameDisplayUUIDs) {
                    displayUUID.writeTo(stream);
                }
            }
        }.write())) {
            file.delete();
        }
    }

    private static File getSaveFile(JavaPlugin plugin) {
        return new File(plugin.getDataFolder(), "mapinfo.reload.dat");
    }

    public void addDynamicMapId(MapUUID uuid, int id) {
        dynamicMappedIds.add(new DynamicMappedId(uuid, id));
    }

    public void addItemFrameDisplayUUID(int entityId, MapUUID uuid) {
        itemFrameDisplayUUIDs.add(new ItemFrameDisplayUUID(uuid, entityId));
    }

    /**
     * Stores the information of the dynamic ID assigned
     * to a given MapUUID (UUID + spatial coordinates).
     */
    public static class DynamicMappedId {
        public final MapUUID uuid;
        public final int id;

        public DynamicMappedId(MapUUID uuid, int id) {
            this.uuid = uuid;
            this.id = id;
        }

        public static DynamicMappedId readFrom(DataInputStream stream) throws IOException {
            MapUUID uuid = readMapUUID(stream);
            int id = stream.readInt();
            return new DynamicMappedId(uuid, id);
        }

        public void writeTo(DataOutputStream stream) throws IOException {
            writeMapUUID(stream, this.uuid);
            stream.writeInt(this.id);
        }
    }

    /**
     * The Map display Map UUID (UUID + tile coords) bound to a given ItemFrame.
     * Is used to avoid having to re-send the itemstack to players after a reload.
     */
    public static class ItemFrameDisplayUUID {
        public final MapUUID uuid;
        public final int entityId;

        public ItemFrameDisplayUUID(MapUUID uuid, int entityId) {
            this.uuid = uuid;
            this.entityId = entityId;
        }

        public static ItemFrameDisplayUUID readFrom(DataInputStream stream) throws IOException {
            MapUUID uuid = readMapUUID(stream);
            int entityId = stream.readInt();
            return new ItemFrameDisplayUUID(uuid, entityId);
        }

        public void writeTo(DataOutputStream stream) throws IOException {
            writeMapUUID(stream, this.uuid);
            stream.writeInt(this.entityId);
        }
    }

    private static MapUUID readMapUUID(DataInputStream stream) throws IOException {
        long uuidMostSig = stream.readLong();
        long uuidLeastSig = stream.readLong();
        int tileX = stream.readInt();
        int tileY = stream.readInt();
        return new MapUUID(new UUID(uuidMostSig, uuidLeastSig), tileX, tileY);
    }

    private static void writeMapUUID(DataOutputStream stream, MapUUID uuid) throws IOException {
        stream.writeLong(uuid.getUUID().getMostSignificantBits());
        stream.writeLong(uuid.getUUID().getLeastSignificantBits());
        stream.writeInt(uuid.getTileX());
        stream.writeInt(uuid.getTileY());
    }
}
