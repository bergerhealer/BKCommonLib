package com.bergerkiller.generated.net.minecraft.world.level.biome;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.biome.BiomeSettingsMobs</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.biome.BiomeSettingsMobs")
public abstract class BiomeSettingsMobsHandle extends Template.Handle {
    /** @see BiomeSettingsMobsClass */
    public static final BiomeSettingsMobsClass T = Template.Class.create(BiomeSettingsMobsClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static BiomeSettingsMobsHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.level.biome.BiomeSettingsMobs</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BiomeSettingsMobsClass extends Template.Class<BiomeSettingsMobsHandle> {
    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.world.level.biome.BiomeSettingsMobs.SpawnRate</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.world.level.biome.BiomeSettingsMobs.SpawnRate")
    public abstract static class SpawnRateHandle extends Template.Handle {
        /** @see SpawnRateClass */
        public static final SpawnRateClass T = Template.Class.create(SpawnRateClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static SpawnRateHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        public static final SpawnRateHandle createNew(java.lang.Class<?> entityClass, int x, int y, int z) {
            return T.constr_entityClass_x_y_z.newInstance(entityClass, x, y, z);
        }

        /* ============================================================================== */

        public abstract int getWeight();
        public abstract java.lang.Class<?> getEntityClass();
        public abstract void setEntityClass(java.lang.Class<?> value);
        public abstract int getMinSpawnCount();
        public abstract void setMinSpawnCount(int value);
        public abstract int getMaxSpawnCount();
        public abstract void setMaxSpawnCount(int value);
        /**
         * Stores class members for <b>net.minecraft.world.level.biome.BiomeSettingsMobs.SpawnRate</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class SpawnRateClass extends Template.Class<SpawnRateHandle> {
            public final Template.Constructor.Converted<SpawnRateHandle> constr_entityClass_x_y_z = new Template.Constructor.Converted<SpawnRateHandle>();

            public final Template.Field.Converted<java.lang.Class<?>> entityClass = new Template.Field.Converted<java.lang.Class<?>>();
            public final Template.Field.Integer minSpawnCount = new Template.Field.Integer();
            public final Template.Field.Integer maxSpawnCount = new Template.Field.Integer();

            public final Template.Method<Integer> getWeight = new Template.Method<Integer>();

        }

    }

}

