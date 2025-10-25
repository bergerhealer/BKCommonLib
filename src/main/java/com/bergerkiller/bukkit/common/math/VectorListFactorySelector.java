package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.Logging;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

/**
 * Automatically picks an optimized implementation for certain sizes of vector lists,
 * if SIMD optimizations are available. Only for immutable lists, as mutations
 * with SIMD are dog slow.
 */
class VectorListFactorySelector {

    public static VectorList.Factory initFactory() {
        // Detect that SIMD is available at all on the platform we are running
        final String packagePath;
        try {
            // Check if Vector API is available
            Class.forName("jdk.incubator.vector.DoubleVector");

            // Package where SIMD classes live
            String packagePathTmp = VectorListFactorySelector.class.getName();
            packagePath = packagePathTmp.substring(0, packagePathTmp.lastIndexOf('.'));

            // Must at least support storing two doubles, otherwise it is useless
            if (jdk.incubator.vector.DoubleVector.SPECIES_PREFERRED.length() < 2) {
                throw new UnsupportedOperationException("SIMD not supported");
            }
        } catch (Throwable ignored) {
            return initBasicFactory();
        }

        // Try to instantiate the suitable SIMD implementation
        try {
            ClassLoader classLoader = VectorListFactorySelector.class.getClassLoader();
            FactoryList factories = new FactoryList();

            // Special implementation for size-8 vector lists on platforms with 256 support (common)
            // Uses two 4-length DoubleVectors. We want this for optimized oriented bounding box maths.
            if (
                    jdk.incubator.vector.DoubleVector.SPECIES_PREFERRED.length() < 8
                            && jdk.incubator.vector.DoubleVector.SPECIES_PREFERRED.length() >= 4
            ) {
                Class<?> vectorListClass = Class.forName(packagePath + ".VectorListSIMD256DoubledImpl", true, classLoader);
                Field factoryField = vectorListClass.getDeclaredField("FACTORY");
                factoryField.setAccessible(true);
                VectorList.Factory octoFactory = (VectorList.Factory) factoryField.get(null);
                factories.testAdd(octoFactory);
            }

            // Generic any species implementation
            Class<?> genericSIMDClass = Class.forName(packagePath + ".VectorListSIMDImpl", true, classLoader);
            Method createFactoryForMethod = genericSIMDClass.getDeclaredMethod("createFactoryFor", jdk.incubator.vector.VectorSpecies.class);
            createFactoryForMethod.setAccessible(true);

            // Retrieve the generic factory version, which supports all species on the platform
            // We do want to limit the species to those that are below preferred in length, as any
            // other will be ridiculously slow.
            for (jdk.incubator.vector.VectorSpecies<?> species : new jdk.incubator.vector.VectorSpecies[] {
                    jdk.incubator.vector.DoubleVector.SPECIES_128,
                    jdk.incubator.vector.DoubleVector.SPECIES_256,
                    jdk.incubator.vector.DoubleVector.SPECIES_512
            }) {
                if (species.length() > jdk.incubator.vector.DoubleVector.SPECIES_PREFERRED.length()) {
                    continue;
                }

                VectorList.Factory factory = (VectorList.Factory) createFactoryForMethod.invoke(null, species);
                factories.testAdd(factory);
            }

            // If no factories are stored, something is amiss.
            if (factories.isEmpty()) {
                return initBasicFactory();
            }

            // Add the fallback implementation last
            factories.add(initBasicFactory());

            // Done
            return factories;
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.WARNING, "Failed to initialize SIMD", t);
            return initBasicFactory();
        }
    }

    private static VectorList.Factory initBasicFactory() {
        return new VectorList.Factory() {
            @Override
            public VectorList copyOf(VectorList vectorValues) {
                return new VectorListBasicImpl(vectorValues);
            }

            @Override
            public VectorList createWith(int size, VectorList.VectorIterator iterator) {
                return new VectorListBasicImpl(size, iterator);
            }
        };
    }

    private static class FactoryList extends ArrayList<VectorList.Factory> implements VectorList.Factory {

        private VectorList.Factory select(int size) {
            for (VectorList.Factory factory : this) {
                int required = factory.getRequiredSize();
                if (required == -1 || required == size) {
                    return factory;
                }
            }

            throw new IllegalArgumentException("No factory found for size " + size);
        }

        /**
         * Performs a quick test that the SIMD implementation is working correctly.
         * If any issues are found, the factory is not registered
         *
         * @param factory Factory
         */
        public void testAdd(VectorList.Factory factory) {
            try {
                int requiredSize = factory.getRequiredSize();
                if (requiredSize == -1) {
                    requiredSize = 32; // For testing
                }

                // Verify we can create vector lists with it
                VectorList list = factory.createWith(requiredSize,
                        VectorList.VectorIterator.iterateFilled(1.0, 2.0, 3.0));
                if (list == null) {
                    throw new IllegalStateException("Factory returned null");
                }

                // Verify all values we set are indeed stored
                for (int i = 0; i < requiredSize; i++) {
                    Vector v = list.get(i);
                    if (!new Vector(1.0, 2.0, 3.0).equals(v)) {
                        throw new IllegalStateException("Factory produced list has invalid element at " + i + ": " + v);
                    }
                }

                // All good!
                this.add(factory);
            } catch (Throwable t) {
                Logging.LOGGER.log(Level.WARNING, "Failed to make use of vector list factory [name=" +
                        factory.getClass().getName() + " req_size=" + factory.getRequiredSize() + "]", t);
            }
        }

        @Override
        public boolean isOptimizedSize(int size) {
            for (VectorList.Factory factory : this) {
                int requiredSize = factory.getRequiredSize();
                if (requiredSize == -1 || requiredSize == size) {
                    return factory.isOptimizedSize(size);
                }
            }

            return false;
        }

        @Override
        public VectorList copyOf(VectorList vectorValues) {
            return select(vectorValues.size()).copyOf(vectorValues);
        }

        @Override
        public VectorList createWith(int size, VectorList.VectorIterator iterator) {
            return select(size).createWith(size, iterator);
        }

        @Override
        public VectorList copyOf(Vector... vectorValues) {
            return select(vectorValues.length).copyOf(vectorValues);
        }

        @Override
        public VectorList copyOf(Collection<Vector> vectorValues) {
            return select(vectorValues.size()).copyOf(vectorValues);
        }
    }
}
