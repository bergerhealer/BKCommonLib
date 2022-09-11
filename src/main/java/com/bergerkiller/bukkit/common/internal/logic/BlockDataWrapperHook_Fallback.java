package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.wrappers.BlockData;

class BlockDataWrapperHook_Fallback extends BlockDataWrapperHook {

    @Override
    public void enable() {
    }

    @Override
    public void hook(Object nmsIBlockData, Object accessor, BlockData blockData) {
    }

    @Override
    public Object getAccessor(Object nmsIBlockData) {
        return null;
    }

    @Override
    protected void setAccessor(Object nmsIBlockdata, Object accessor) {
    }

    @Override
    protected Object hook(Object accessor, BlockData blockData) {
        return accessor;
    }
}
