package com.bergerkiller.generated.net.minecraft.world.item.component;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.item.component.CustomModelData</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.item.component.CustomModelData")
public abstract class CustomModelDataHandle extends Template.Handle {
    /** @see CustomModelDataClass */
    public static final CustomModelDataClass T = Template.Class.create(CustomModelDataClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CustomModelDataHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static CustomModelDataHandle empty() {
        return T.empty.invoke();
    }

    public static CustomModelDataHandle createNew(List<Float> floats, List<Boolean> flags, List<String> strings, List<Integer> colors) {
        return T.createNew.invoke(floats, flags, strings, colors);
    }

    public static CustomModelDataHandle createNewLegacy(int value) {
        return T.createNewLegacy.invoke(value);
    }

    public abstract List<Float> floats();
    public abstract List<Boolean> flags();
    public abstract List<String> strings();
    public abstract List<Integer> colors();
    /**
     * Stores class members for <b>net.minecraft.world.item.component.CustomModelData</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CustomModelDataClass extends Template.Class<CustomModelDataHandle> {
        public final Template.StaticMethod.Converted<CustomModelDataHandle> empty = new Template.StaticMethod.Converted<CustomModelDataHandle>();
        public final Template.StaticMethod.Converted<CustomModelDataHandle> createNew = new Template.StaticMethod.Converted<CustomModelDataHandle>();
        public final Template.StaticMethod.Converted<CustomModelDataHandle> createNewLegacy = new Template.StaticMethod.Converted<CustomModelDataHandle>();

        public final Template.Method<List<Float>> floats = new Template.Method<List<Float>>();
        public final Template.Method<List<Boolean>> flags = new Template.Method<List<Boolean>>();
        public final Template.Method<List<String>> strings = new Template.Method<List<String>>();
        public final Template.Method<List<Integer>> colors = new Template.Method<List<Integer>>();

    }

}

