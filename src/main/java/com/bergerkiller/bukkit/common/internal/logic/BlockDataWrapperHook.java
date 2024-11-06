package com.bergerkiller.bukkit.common.internal.logic;

import java.util.logging.Level;

import org.bukkit.Material;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;

/**
 * Hooks fields inside IBlockData to store a BlockData wrapper
 */
public abstract class BlockDataWrapperHook implements LibraryComponent {
    public static BlockDataWrapperHook INSTANCE = LibraryComponentSelector.forModule(BlockDataWrapperHook.class)
            .runFirst(CommonBootstrap::initServer)
            .setDefaultComponent(BlockDataWrapperHook_Fallback::new)
            .addWhen("Paper 1.21.2",
                    a -> CommonBootstrap.evaluateMCVersion(">=", "1.21.2") && CommonBootstrap.isPaperServer(),
                    BlockDataWrapperHook_Impl_Paper_1_21_2::new)
            .addVersionOption("1.8", null, BlockDataWrapperHook_Impl_Default::new)
            .update();

    /**
     * Implemented by hooks at runtime
     */
    public static interface Accessor {
        Object bkcGetOriginalValue();
        BlockData bkcGetBlockData();
    }

    public static void init() {
        // cinit
    }

    /**
     * Disables the hook, so that future get()s don't result in further hooks being registered
     */
    public static void disableHook() {
        try {
            INSTANCE.disable();
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to disable BlockData hook", t);
        }
        INSTANCE = new BlockDataWrapperHook_Fallback();
    }

    /**
     * Gets the wrapper BlockData installed for a given IBlockData instance.
     * If none is hooked, will use the fallback routine to find the BlockData, instead.
     * It will then, if supported, hook the BlockData inside the IBlockData.
     *
     * @param nmsIBlockData
     * @param accessor Gotten from {@link #getAccessor(Object)}
     * @param blockData BlockData wrapper of IBlockData
     */
    public void hook(Object nmsIBlockData, Object accessor, BlockData blockData) {
        setAccessor(nmsIBlockData, hook(accessor, blockData));
    }

    /**
     * Unregisters a hook previously installed using {@link #hook(Object, BlockData)}
     *
     * @param nmsIBlockData
     */
    public final void unhook(Object nmsIBlockData) {
        Object accessor;
        try {
            accessor = getAccessor(nmsIBlockData);
            if (!(accessor instanceof Accessor)) {
                return;
            }
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to read BlockData accessor field", t);
            return;
        }

        try {
            setAccessor(nmsIBlockData, ((Accessor) accessor).bkcGetOriginalValue());
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to unhook BlockData accessor", t);
        }
    }

    @Override
    public void disable() {
    }

    @Override
    public final void enable() throws Throwable {
        this.baseEnable();

        // Perform a little dry run to verify all the above initialized logic actually works
        // Operate on BlockData 'AIR' for this
        // As we're modifying a final non-volatile field, it might not update right away, try with yield()
        Object airBlockData = BlockHandle.createHandle(CraftMagicNumbersHandle.getBlockFromMaterial(Material.AIR)).getBlockData().getRaw();
        Object currentAccessor = this.getAccessor(airBlockData);
        try {
            // Hook the accessor
            Object hooked = this.hook(currentAccessor, BlockData.AIR);

            // Verify accessor was updated
            this.setAccessor(airBlockData, hooked);
            if (this.getAccessor(airBlockData) != hooked) {
                Thread.yield();
                if (this.getAccessor(airBlockData) != hooked) {
                    throw new IllegalStateException("Output of getAccessor() did not change after setting accessor");
                }
            }
        } finally {
            // Restore
            this.setAccessor(airBlockData, currentAccessor);
        }
    }

    /**
     * Enables this implementation. After this all other methods should work.
     *
     * @throws Throwable
     */
    protected abstract void baseEnable() throws Throwable;

    /**
     * Gets the Accessor instance, if the object is hooked it should implement {@link Accessor}
     *
     * @param nmsIBlockData
     * @return accessor instance
     */
    public abstract Object getAccessor(Object nmsIBlockData);

    /**
     * Sets the Accessor instance
     *
     * @param nmsIBlockdata
     * @param accessor instance to set
     */
    protected abstract void setAccessor(Object nmsIBlockdata, Object accessor);

    /**
     * Generates a new Accessor instance with the BlockData field installed
     *
     * @param accessor
     * @param blockData
     * @return hooked accessor
     */
    protected abstract Object hook(Object accessor, BlockData blockData);
}
