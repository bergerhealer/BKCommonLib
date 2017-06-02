package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class WeightedRandomHandle extends Template.Handle {
    public static final WeightedRandomClass T = new WeightedRandomClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WeightedRandomHandle.class, "net.minecraft.server.WeightedRandom");

    /* ============================================================================== */

    public static WeightedRandomHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        WeightedRandomHandle handle = new WeightedRandomHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class WeightedRandomClass extends Template.Class<WeightedRandomHandle> {
    }


    public static class WeightedRandomChoiceHandle extends Template.Handle {
        public static final WeightedRandomChoiceClass T = new WeightedRandomChoiceClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(WeightedRandomChoiceHandle.class, "net.minecraft.server.WeightedRandom.WeightedRandomChoice");

        /* ============================================================================== */

        public static WeightedRandomChoiceHandle createHandle(Object handleInstance) {
            if (handleInstance == null) return null;
            WeightedRandomChoiceHandle handle = new WeightedRandomChoiceHandle();
            handle.instance = handleInstance;
            return handle;
        }

        /* ============================================================================== */

        public int getChance() {
            return T.chance.getInteger(instance);
        }

        public void setChance(int value) {
            T.chance.setInteger(instance, value);
        }

        public static final class WeightedRandomChoiceClass extends Template.Class<WeightedRandomChoiceHandle> {
            public final Template.Field.Integer chance = new Template.Field.Integer();

        }

    }

}

