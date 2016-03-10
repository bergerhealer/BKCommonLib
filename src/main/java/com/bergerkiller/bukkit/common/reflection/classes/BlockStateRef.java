package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_9_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.block.CraftBlockState;
import org.bukkit.material.MaterialData;

import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.reflection.CBClassTemplate;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_9_R1.CommandBlockListenerAbstract;
import net.minecraft.server.v1_9_R1.IChatBaseComponent;
import net.minecraft.server.v1_9_R1.TileEntitySkull;

public class BlockStateRef {

    public static final ClassTemplate<?> TEMPLATE = ClassTemplate.create(CommonUtil.getCBClass("block.CraftBlockState"));
    private static final ClassMap<TileInstantiator> tileToInst = new ClassMap<TileInstantiator>();
    private static final ClassMap<TileInstantiator> stateToInst = new ClassMap<TileInstantiator>();
    public static final FieldAccessor<CraftWorld> world = TEMPLATE.getField("world");
    public static final FieldAccessor<CraftChunk> chunk = TEMPLATE.getField("chunk");
    public static final FieldAccessor<Integer> x = TEMPLATE.getField("x");
    public static final FieldAccessor<Integer> y = TEMPLATE.getField("y");
    public static final FieldAccessor<Integer> z = TEMPLATE.getField("z");
    public static final FieldAccessor<Integer> type = TEMPLATE.getField("flag");
    public static final FieldAccessor<MaterialData> data = TEMPLATE.getField("data");
    public static final FieldAccessor<Byte> light = TEMPLATE.getField("light");

    private static void registerInst(TileInstantiator inst) {
        tileToInst.put(inst.TILE.getType(), inst);
        stateToInst.put(inst.STATE.getType(), inst);
    }

    static {
        // Initialize some instantiators
        registerInst(new TileInstantiator("Sign") {
            // Sign lines are IChatBaseComponent in n.m.s. but String in Craftbukkit
            private final FieldAccessor<String[]> state_lines = STATE.getField("lines");
            private final FieldAccessor<IChatBaseComponent[]> tile_lines = TILE.getField("lines");

            @Override
            protected void apply(BlockState state, Object tile) {
                IChatBaseComponent[] tileLineArray = tile_lines.get(tile);
                List<String> signLines = new ArrayList<String>(tileLineArray.length);
                for (IChatBaseComponent line : tileLineArray) {
                    if (line == null) { // Avoid NPE later calling string functions
                        signLines.add("");
                    } else {
                        signLines.add(line.getText());
                    }
                }
                state_lines.set(state, signLines.toArray(new String[signLines.size()]));
            }
        });
        registerInst(new TileInstantiator("Skull") {
            private final FieldAccessor<GameProfile> state_profile = STATE.getField("profile");
            private final FieldAccessor<SkullType> state_type = STATE.getField("skullType");
            private final FieldAccessor<Byte> state_rotation = STATE.getField("rotation");
            private final MethodAccessor<SkullType> state_getSkullType = STATE.getMethod("getSkullType", int.class);

            @Override
            protected void apply(BlockState state, Object tile) {
                TileEntitySkull t = (TileEntitySkull) tile;
                state_profile.set(state, t.getGameProfile());
                state_type.set(state, state_getSkullType.invoke(null, t.getSkullType()));
                state_rotation.set(state, (byte) t.getRotation());
            }
        });
        registerInst(new TileInstantiator("Command", "CommandBlock", "commandBlock") {
            private final FieldAccessor<CommandBlockListenerAbstract> listener = TILE.getField("a");
            private final FieldAccessor<String> state_command = STATE.getField("command");
            private final FieldAccessor<String> state_name = STATE.getField("name");

            @Override
            protected void apply(BlockState state, Object tile) {
                CommandBlockListenerAbstract list = listener.get(tile);
                state_command.set(state, list.getCommand());
                state_name.set(state, list.getName());
            }
        });
        registerInst(new TileInstantiator("Furnace"));
        registerInst(new TileInstantiator("Dispenser"));
        registerInst(new TileInstantiator("Chest"));
        registerInst(new TileInstantiator("Dropper"));
        registerInst(new TileInstantiator("Beacon"));
        registerInst(new TileInstantiator("Hopper"));
        registerInst(new TileInstantiator("Chest"));
        registerInst(new TileInstantiator("MobSpawner", "CreatureSpawner", "spawner"));
        registerInst(new TileInstantiator("Note", "NoteBlock", "note"));
//		registerInst(new TileInstantiator("RecordPlayer", "Jukebox", "jukebox"));
    }

    public static Object toTileEntity(BlockState state) {
        TileInstantiator inst = stateToInst.get(state);
        if (inst == null) {
            return TileEntityRef.getFromWorld(state.getBlock());
        } else {
            return inst.getTileHandle(state);
        }
    }

    public static BlockState toBlockState(Block block) {
		Object tileEntity = TileEntityRef.getFromWorld(block);
		if (tileEntity != null) {
			TileInstantiator inst = tileToInst.get(tileEntity);
			if (inst != null) {
				return inst.newInstance(block, tileEntity);
			}
		}
		// All BlockState types REQUIRE a tile entity, just return the default BlockState here
		return new CraftBlockState(block);
    }

    public static BlockState toBlockState(Object tileEntity) {
        if (tileEntity == null || !TileEntityRef.hasWorld(tileEntity)) {
            throw new IllegalArgumentException("Tile Entity is null or has no world set");
        }
        TileInstantiator inst = tileToInst.get(tileEntity);
        if (inst == null) {
            return toBlockState(TileEntityRef.getBlock(tileEntity));
        } else {
            return inst.newInstance(tileEntity);
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
            this.TILE = NMSClassTemplate.create("TileEntity" + tileName);
            this.STATE = CBClassTemplate.create("block.Craft" + stateName);
            this.tileField = this.STATE.getField(tileFieldName);
            // Second world, yes, Bukkit is stupid enough to have two world fields. LOL.
            this.secondWorld = this.STATE.getField("world");
        }

        public Object getTileHandle(Object state) {
            return tileField.get(state);
        }

        protected void apply(BlockState state, Object tile) {
        }

        public BlockState newInstance(Object tileEntity) {
            return newInstance(TileEntityRef.getBlock(tileEntity), tileEntity);
        }

        public BlockState newInstance(Block block, Object tileEntity) {
            final BlockState state = (BlockState) STATE.newInstanceNull();
            final int typeId = MaterialUtil.getTypeId(block);
            tileField.set(state, tileEntity);
            world.set(state, (CraftWorld)block.getWorld());
            secondWorld.set(state, block.getWorld());
            chunk.set(state, (CraftChunk)block.getChunk());
            type.set(state, typeId);
            light.set(state, block.getLightLevel());
            x.set(state, block.getX());
            y.set(state, block.getY());
            z.set(state, block.getZ());
            data.set(state, BlockUtil.getData(typeId, MaterialUtil.getRawData(block)));
            this.apply(state, tileEntity);
            return state;
        }
    }
}
