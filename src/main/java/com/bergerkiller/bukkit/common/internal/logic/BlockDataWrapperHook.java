package com.bergerkiller.bukkit.common.internal.logic;

import java.util.logging.Level;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * Hooks fields inside IBlockData to store a BlockData wrapper
 */
public abstract class BlockDataWrapperHook implements LibraryComponent {
    public static BlockDataWrapperHook INSTANCE = LibraryComponentSelector.forModule(BlockDataWrapperHook.class)
            .runFirst(CommonBootstrap::initServer)
            .setDefaultComponent(BlockDataWrapperHook_Fallback::new)
            .addVersionOption("1.8", null, BlockDataWrapperHook_Impl::new)
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
        Object hook = hook(accessor, blockData);
        setAccessor(nmsIBlockData, hook);
        if (getAccessor(nmsIBlockData) != hook) {
            setAccessor(nmsIBlockData, accessor);
            throw new IllegalStateException("Output of getAccessor() did not change after setting accessor");
        }
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
