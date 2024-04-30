package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;

import java.util.function.Consumer;

/**
 * Acts as an NBTTagCompound consumer, wrapping the compound and passing it
 * to the API consumer.
 */
public class NBTTagCompoundConsumer implements Consumer<Object> {
    private final Consumer<CommonTagCompound> consumer;

    public NBTTagCompoundConsumer(Consumer<CommonTagCompound> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void accept(Object o) {
        consumer.accept(CommonTagCompound.create(o));
    }
}
