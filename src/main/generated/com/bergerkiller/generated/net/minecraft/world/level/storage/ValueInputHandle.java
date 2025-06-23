package com.bergerkiller.generated.net.minecraft.world.level.storage;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import org.bukkit.World;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.storage.ValueInput</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.storage.ValueInput")
public abstract class ValueInputHandle extends Template.Handle {
    /** @see ValueInputClass */
    public static final ValueInputClass T = Template.Class.create(ValueInputClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ValueInputHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ValueInputHandle forNBTOnWorld(Object problemReporter, World world, CommonTagCompound nbttagcompound) {
        return T.forNBTOnWorld.invoke(problemReporter, world, nbttagcompound);
    }

    public static ValueInputHandle forNBT(Object problemReporter, Object holderLookup, CommonTagCompound nbttagcompound) {
        return T.forNBT.invoke(problemReporter, holderLookup, nbttagcompound);
    }

    public abstract CommonTagCompound asNBT();
    /**
     * Stores class members for <b>net.minecraft.world.level.storage.ValueInput</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ValueInputClass extends Template.Class<ValueInputHandle> {
        public final Template.StaticMethod.Converted<ValueInputHandle> forNBTOnWorld = new Template.StaticMethod.Converted<ValueInputHandle>();
        public final Template.StaticMethod.Converted<ValueInputHandle> forNBT = new Template.StaticMethod.Converted<ValueInputHandle>();

        public final Template.Method.Converted<CommonTagCompound> asNBT = new Template.Method.Converted<CommonTagCompound>();

    }

}

