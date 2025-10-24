package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.Logging;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
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
        final String simdOctoImplementationClassName;
        try {
            // Check if Vector API is available
            Class.forName("jdk.incubator.vector.DoubleVector");

            // Get preferred species
            jdk.incubator.vector.VectorSpecies<Double> preferred = jdk.incubator.vector.DoubleVector.SPECIES_PREFERRED;
            int lanes = preferred.length();

            String packagePath = VectorListFactorySelector.class.getName();
            packagePath = packagePath.substring(0, packagePath.lastIndexOf('.'));

            if (lanes >= 8) {
                simdOctoImplementationClassName = packagePath + ".VectorListOctoSIMD512Impl";
            } else if (lanes >= 4) {
                simdOctoImplementationClassName = packagePath + ".VectorListOctoSIMD256Impl";
            } else {
                throw new UnsupportedOperationException("SIMD not supported");
            }
        } catch (Throwable ignored) {
            return initBasicFactory();
        }

        // Try to instantiate the suitable SIMD implementation
        try {
            ClassLoader classLoader = VectorListFactorySelector.class.getClassLoader();
            Class<?> vectorListClass = Class.forName(simdOctoImplementationClassName, true, classLoader);
            Field factoryField = vectorListClass.getDeclaredField("FACTORY");
            factoryField.setAccessible(true);
            final VectorList.Factory octoFactory = (VectorList.Factory) factoryField.get(null);

            // Test it
            if (octoFactory.createWith(8, VectorList.VectorIterator.iterateFilled(1.0, 2.0, 3.0)) == null) {
                throw new IllegalStateException("Factory returned null");
            }

            // Works - use it
            final VectorList.Factory altFactory = initBasicFactory();
            return new VectorList.Factory() {
                @Override
                public VectorList copyOf(VectorList vectorValues) {
                    return (vectorValues.size() == 8 ? octoFactory : altFactory).copyOf(vectorValues);
                }

                @Override
                public VectorList createWith(int size, VectorList.VectorIterator iterator) {
                    return (size == 8 ? octoFactory : altFactory).createWith(size, iterator);
                }

                @Override
                public VectorList copyOf(Collection<Vector> vectorValues) {
                    return (vectorValues.size() == 8 ? octoFactory : altFactory).copyOf(vectorValues);
                }
            };
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.WARNING, "Failed to initialize SIMD " + simdOctoImplementationClassName, t);
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
}
