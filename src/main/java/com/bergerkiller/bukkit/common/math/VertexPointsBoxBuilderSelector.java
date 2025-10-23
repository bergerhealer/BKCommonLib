package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.mountiplex.reflection.util.FastConstructor;
import org.bukkit.util.Vector;

import java.util.function.Supplier;
import java.util.logging.Level;

final class VertexPointsBoxBuilderSelector {
    public static final Supplier<VertexPoints.BoxBuilder> BUILDER_IMPL = selectBuilderImpl();

    @SuppressWarnings("unchecked")
    private static Supplier<VertexPoints.BoxBuilder> selectBuilderImpl() {
        // Detect that SIMD is available at all on the platform we are running
        final String simdImplementationClassName;
        try {
            // Check if Vector API is available
            Class.forName("jdk.incubator.vector.DoubleVector");

            // Get preferred species
            jdk.incubator.vector.VectorSpecies<Double> preferred = jdk.incubator.vector.DoubleVector.SPECIES_PREFERRED;
            int lanes = preferred.length();

            String packagePath = VertexPointsBoxBuilderSelector.class.getName();
            packagePath = packagePath.substring(0, packagePath.lastIndexOf('.'));

            if (lanes >= 8) {
                simdImplementationClassName = packagePath + ".VertexPointsSIMD512Impl$BoxBuilder";
            } else if (lanes >= 4) {
                simdImplementationClassName = packagePath + ".VertexPointsSIMD256Impl$BoxBuilder";
            } else {
                throw new UnsupportedOperationException("SIMD not supported");
            }
        } catch (Throwable ignored) {
            return VertexPointsBasicImpl.BoxBuilder::new;
        }

        // Try to instantiate the suitable SIMD implementation
        try {
            ClassLoader classLoader = VertexPointsBoxBuilderSelector.class.getClassLoader();
            Class<? extends VertexPoints.BoxBuilder> boxBuilderClass = (Class<? extends VertexPoints.BoxBuilder>) Class.forName(
                    simdImplementationClassName, true, classLoader);
            final FastConstructor<? extends VertexPoints.BoxBuilder> ctor = new FastConstructor<>(boxBuilderClass.getConstructor());

            // Test it
            VertexPoints pts = ctor.newInstance()
                    .halfSize(new Vector(1, 1, 1))
                    .rotate(Quaternion.fromYawPitchRoll(45, 90, 180))
                    .translate(new Vector(10, 20, 30))
                    .build();
            if (pts == null) {
                throw new IllegalStateException("Builder returned null");
            }

            // Works - use it
            return ctor::newInstance;
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.WARNING, "Failed to initialize SIMD " + simdImplementationClassName, t);
            return VertexPointsBasicImpl.BoxBuilder::new;
        }
    }
}
