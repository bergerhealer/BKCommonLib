package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.material.MaterialData;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.SafeDirectField;
import com.bergerkiller.reflection.net.minecraft.server.NMSCommandBlockListenerAbstract;
import com.bergerkiller.reflection.net.minecraft.server.NMSRegistryMaterials;
import com.bergerkiller.reflection.net.minecraft.server.NMSTileEntity;
import com.mojang.authlib.GameProfile;

public class CBCraftBlockState {
    public static final ClassTemplate<?> T = ClassTemplate.create(CommonUtil.getCBClass("block.CraftBlockState"));

    private static final ClassMap<TileInstantiator> tileToInst = new ClassMap<TileInstantiator>();
    private static final ClassMap<TileInstantiator> stateToInst = new ClassMap<TileInstantiator>();
    public static final FieldAccessor<World> world  = T.selectField("private final org.bukkit.craftbukkit.CraftWorld world");
    public static final FieldAccessor<Chunk> chunk  = T.selectField("private final org.bukkit.craftbukkit.CraftChunk chunk");
    public static final FieldAccessor<Integer> x         = T.selectField("private final int x");
    public static final FieldAccessor<Integer> y         = T.selectField("private final int y");
    public static final FieldAccessor<Integer> z         = T.selectField("private final int z");
    public static final FieldAccessor<Integer> type      = T.selectField("protected int flag");
    public static final FieldAccessor<MaterialData> data = T.selectField("protected org.bukkit.material.MaterialData data");

    private static void registerInst(TileInstantiator inst) {
        tileToInst.put(inst.TILE.getType(), inst);
        stateToInst.put(inst.STATE.getType(), inst);
    }

    // Initialize some instantiators
    static {
        registerInst(new TileInstantiator("Banner") {
            private final FieldAccessor<DyeColor> base = STATE.selectField("private org.bukkit.DyeColor base");
            private final FieldAccessor<List<Pattern>> state_patterns = STATE.addImport("org.bukkit.block.banner.Pattern").selectField("private List<Pattern> patterns");
            private final FieldAccessor<CommonTagList> tile_patterns = TILE.selectField("public NBTTagList patterns").translate(DuplexConversion.commonTagList);
            private final FieldAccessor<Object> tile_color = TILE.selectField("public EnumColor color");

            private final MethodAccessor<Integer> dyecolor_getIdx = ClassTemplate.createNMS("EnumColor").selectMethod("public int getInvColorIndex()");
            private final int getDyeColorIndex(Object tile) {
                return dyecolor_getIdx.invoke(tile_color.get(tile)).intValue();
            }

            @Override
            @SuppressWarnings("deprecation")
            protected void apply(BlockState state, Object tile) {
                base.set(state, DyeColor.getByDyeData((byte) getDyeColorIndex(tile)));

                List<Pattern> newPatterns = new ArrayList<Pattern>();
                CommonTagList patterns = tile_patterns.get(tile);
                if (patterns != null) {
                    for (int i = 0; i < patterns.size(); i++) {
                        CommonTagCompound comp = (CommonTagCompound) patterns.get(i);
                        PatternType type = PatternType.getByIdentifier(comp.getValue("Pattern", ""));
                        byte colorB = comp.getValue("Color", 0).byteValue();
                        newPatterns.add(new Pattern(DyeColor.getByDyeData(colorB), type));
                    }
                }
                state_patterns.set(state, newPatterns);
            }
        });
        registerInst(new TileInstantiator("Beacon"));
        registerInst(new TileInstantiator("BrewingStand", "BrewingStand", "brewingStand"));
        registerInst(new TileInstantiator("Chest"));
        registerInst(new TileInstantiator("Command", "CommandBlock", "commandBlock") {
            private final FieldAccessor<Object> listener = TILE.selectField("private final CommandBlockListenerAbstract i");
            private final FieldAccessor<String> state_command = STATE.selectField("private String command");
            private final FieldAccessor<String> state_name = STATE.selectField("private String name");

            @Override
            protected void apply(BlockState state, Object tile) {
                Object list = listener.get(tile);
                state_command.set(state, NMSCommandBlockListenerAbstract.getCommand.invoke(list));
                state_name.set(state, NMSCommandBlockListenerAbstract.getName.invoke(list));
            }
        });
        registerInst(new TileInstantiator("Comparator"));
        registerInst(new TileInstantiator(CommonUtil.getNMSClass("ITileInventory"), "Container", "container"));
        registerInst(new TileInstantiator("MobSpawner", "CreatureSpawner", "spawner"));
        registerInst(new TileInstantiator("LightDetector", "DaylightDetector", "detector"));
        registerInst(new TileInstantiator("Dispenser"));
        registerInst(new TileInstantiator("Dropper"));
        registerInst(new TileInstantiator("EnchantTable", "EnchantingTable", "enchant"));
        registerInst(new TileInstantiator("EnderChest", "EnderChest", "chest"));
        registerInst(new TileInstantiatorNone("EnderPortal"));
        registerInst(new TileInstantiator("EndGateway", "EndGateway", "gateway"));
        registerInst(new TileInstantiator("FlowerPot", "FlowerPot", "pot"));
        registerInst(new TileInstantiator("Furnace"));
        registerInst(new TileInstantiator("Hopper"));
        registerInst(new TileInstantiator(CommonUtil.getNMSClass("BlockJukeBox.TileEntityRecordPlayer"), "Jukebox", "jukebox"));
        registerInst(new TileInstantiator("Lootable", "Lootable", "te"));
        registerInst(new TileInstantiator("Note", "NoteBlock", "note"));
        registerInst(new TileInstantiatorNone("Piston"));
        registerInst(new TileInstantiator("ShulkerBox", "ShulkerBox", "box"));
        registerInst(new TileInstantiator("Sign") {
            // Sign lines are IChatBaseComponent in n.m.s. but String in Craftbukkit
            private final FieldAccessor<String[]> state_lines = STATE.selectField("private final String[] lines");
            private final FieldAccessor<ChatText[]> tile_lines = TILE.selectField("public final IChatBaseComponent[] lines").translate(DuplexConversion.chatTextArray);

            @Override
            protected void apply(BlockState state, Object tile) {
                ChatText[] linesText = tile_lines.get(tile);
                if (linesText == null) {
                    throw new RuntimeException("Failed to read lines field into a text lines array");
                }
                String[] lines = new String[linesText.length];
                for (int i = 0; i < lines.length; i++) {
                    if (linesText[i] == null) {
                        lines[i] = "";
                    } else {
                        lines[i] = linesText[i].getMessage();
                    }
                }
                state_lines.set(state, lines);
            }
        });
        registerInst(new TileInstantiator("Skull") {
            private final FieldAccessor<GameProfile> state_profile = STATE.selectField("private com.mojang.authlib.GameProfile profile");
            private final FieldAccessor<SkullType> state_type = STATE.selectField("private org.bukkit.SkullType skullType");
            private final FieldAccessor<Byte> state_rotation = STATE.selectField("private byte rotation");
            private final MethodAccessor<SkullType> state_getSkullType = STATE.selectMethod("static org.bukkit.SkullType getSkullType(int id)");
            private final FieldAccessor<Integer> tile_rotation = TILE.selectField("public int rotation");
            private final MethodAccessor<GameProfile> tile_getProfile = TILE.selectMethod("public com.mojang.authlib.GameProfile getGameProfile()");
            private final MethodAccessor<Integer> tile_getSkullType = TILE.selectMethod("public int getSkullType()");

            @Override
            protected void apply(BlockState state, Object tile) {
                state_profile.set(state, tile_getProfile.invoke(tile));
                state_type.set(state, state_getSkullType.invoke(null, tile_getSkullType.invoke(tile)));
                state_rotation.set(state, tile_rotation.get(tile).byteValue());
            }
        });
        registerInst(new TileInstantiator("Structure", "StructureBlock", "structure"));
    }

    /* This verifies that all tile entity classes are properly registered here */
    static {
        FieldAccessor<Object> mapField = ClassTemplate.createNMS("TileEntity").selectField("private static final RegistryMaterials<MinecraftKey, Class<? extends TileEntity>> f");
        if (!mapField.isValid()) {
            Logging.LOGGER_REFLECTION.warning("Could not verify tile entities: Material Registry field not found");
        } else {
            Object registryMaterials = mapField.get(null);
            Set<?> keys = NMSRegistryMaterials.keySet.invoke(registryMaterials);
            for (Object key : keys) {
                Class<?> tileClass = CommonUtil.unsafeCast(NMSRegistryMaterials.getValue.invoke(registryMaterials, key));
                if (tileToInst.get(tileClass) == null) {
                    Logging.LOGGER_REGISTRY.warning("Tile entity [" + key.toString() + "] class " + tileClass.getSimpleName() + " is not registered");
                }
            }
        }
    }

    public static Object toTileEntity(BlockState state) {
        TileInstantiator inst = stateToInst.get(state);
        Object handle = (inst == null) ? null : inst.getTileHandle(state);
        if (handle == null) {
            handle = NMSTileEntity.getFromWorld(state.getBlock());
        }
        return handle;
    }

    public static BlockState toBlockState(Block block) {
        Object tileEntity = NMSTileEntity.getFromWorld(block);
        if (tileEntity != null) {
            TileInstantiator inst = tileToInst.get(tileEntity);
            if (inst != null) {
                return inst.newInstance(block, tileEntity);
            }
        }
        // All BlockState types REQUIRE a tile entity; fall back to a default method using Block
        // Note that this one has several glitches!
        return block.getState();
    }

    public static BlockState toBlockState(Object tileEntity) {
        if (tileEntity == null || !NMSTileEntity.hasWorld(tileEntity)) {
            throw new IllegalArgumentException("Tile Entity is null or has no world set");
        }
        TileInstantiator inst = tileToInst.get(tileEntity);
        if (inst == null) {
            return toBlockState(NMSTileEntity.getBlock(tileEntity));
        } else {
            return inst.newInstance(tileEntity);
        }
    }

    private static class TileInstantiatorNone extends TileInstantiator {
        public TileInstantiatorNone(String tileName) {
            super(tileName, "BlockState", null);
        }
    }

    private static class TileInstantiator {

        private final FieldAccessor<Object> tileField;
        private final FieldAccessor<World> secondWorld;
        protected final ClassTemplate<?> STATE;
        protected final ClassTemplate<?> TILE;

        public TileInstantiator(String name) {
            this(name, name, name.toLowerCase(Locale.ENGLISH));
        }

        public TileInstantiator(String tileName, String stateName, String tileFieldName) {
            this(CommonUtil.getNMSClass("TileEntity" + tileName), stateName, tileFieldName);
        }

        public TileInstantiator(Class<?> tileType, String stateName, String tileFieldName) {
            this.TILE = ClassTemplate.create(tileType);
            this.STATE = ClassTemplate.createCB("block.Craft" + stateName);
            if (tileFieldName != null) {
                this.tileField = this.STATE.getField(tileFieldName, this.TILE.getType());
            } else {
                this.tileField = new SafeDirectField<Object>() {
                    @Override
                    public Object get(Object instance) { return null; }
                    @Override
                    public boolean set(Object instance, Object value) { return true; }
                };
            }
            // Second world, yes, Bukkit is stupid enough to have two world fields. LOL.
            this.secondWorld = this.STATE.getField("world", CommonUtil.getCBClass("CraftWorld"));
        }

        public Object getTileHandle(Object state) {
            return tileField.get(state);
        }

        protected void apply(BlockState state, Object tile) {
        }

        public BlockState newInstance(Object tileEntity) {
            return newInstance(NMSTileEntity.getBlock(tileEntity), tileEntity);
        }

        public BlockState newInstance(Block block, Object tileEntity) {
            final BlockState state = (BlockState) STATE.newInstanceNull();
            final BlockData bdata = WorldUtil.getBlockData(block);
            tileField.set(state, tileEntity);
            world.set(state, block.getWorld());
            secondWorld.set(state, block.getWorld());
            chunk.set(state, block.getChunk());
            type.set(state, bdata.getTypeId());
            x.set(state, block.getX());
            y.set(state, block.getY());
            z.set(state, block.getZ());
            data.set(state, bdata.newMaterialData());
            this.apply(state, tileEntity);
            return state;
        }
    }
}
