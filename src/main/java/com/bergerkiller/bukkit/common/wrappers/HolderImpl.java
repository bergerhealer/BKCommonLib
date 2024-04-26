package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

class HolderImpl<T> implements Holder<T> {
    private static final HolderLogic LOGIC = Template.Class.create(HolderLogic.class, Common.TEMPLATE_RESOLVER);

    private final Object rawHolder;
    private Supplier<T> handleSupplier;

    public static <T extends Template.Handle> HolderImpl<T> direct(T handle) {
        return new HolderImpl<T>(LOGIC.createDirect(handle.getRaw()), handle);
    }

    public static <T extends Template.Handle> HolderImpl<T> directWrap(Object value, Function<Object, T> handleCtor) {
        return new HolderImpl<T>(LOGIC.createDirect(value), handleCtor);
    }

    public HolderImpl(Object rawHolder, Function<Object, T> handleCtor) {
        this.rawHolder = rawHolder;
        this.handleSupplier = () -> {
            T result = handleCtor.apply(rawValue());
            handleSupplier = LogicUtil.constantSupplier(result);
            return result;
        };
    }

    public HolderImpl(Object rawHolder, T handle) {
        this.rawHolder = rawHolder;
        this.handleSupplier = LogicUtil.constantSupplier(handle);
    }

    @Override
    public T value() {
        return handleSupplier.get();
    }

    @Override
    public Object rawValue() {
        return LOGIC.getHolderValue(rawHolder);
    }

    @Override
    public Optional<ResourceKey<T>> key() {
        return LOGIC.getHolderKey(rawHolder).map(ResourceKey::fromResourceKeyHandle);
    }

    @Override
    public Object toRawHolder() {
        return rawHolder;
    }

    @Override
    public String toString() {
        return rawHolder.toString();
    }

    @Override
    public int hashCode() {
        return rawHolder.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Holder) {
            return rawHolder.equals(((Holder<?>) o).toRawHolder());
        } else {
            return false;
        }
    }

    // Note: Holder exists since Minecraft 1.18.2. On 1.18.1 and before we store the actual value, instead.
    @Template.Import("java.util.Optional")
    @Template.InstanceType("net.minecraft.server.MinecraftServer")
    public static abstract class HolderLogic extends Template.Class<Template.Handle> {

        /*
         * <GET_HOLDER_VALUE>
         * public static Object getHolderValue(Object holder) {
         * #if version >= 1.18.2
         *     return ((net.minecraft.core.Holder) holder).value();
         * #else
         *     return holder;
         * #endif
         * }
         */
        @Template.Generated("%GET_HOLDER_VALUE%")
        public abstract Object getHolderValue(Object nmsHolder);

        /*
         * <GET_HOLDER_KEY>
         * public static Optional<Object> getHolderKey(Object holder) {
         * #if version >= 1.18.2
         *     return ((net.minecraft.core.Holder) holder).unwrapKey();
         * #else
         *     return Optional.empty();
         * #endif
         * }
         */
        @Template.Generated("%GET_HOLDER_KEY%")
        public abstract Optional<Object> getHolderKey(Object nmsHolder);

        /*
         * <CREATE_DIRECT_HOLDER>
         * public static Object createDirect(Object value) {
         * #if version >= 1.18.2
         *     return net.minecraft.core.Holder.direct(value);
         * #else
         *     return value;
         * #endif
         * }
         */
        @Template.Generated("%CREATE_DIRECT_HOLDER%")
        public abstract Object createDirect(Object value);
    }
}
