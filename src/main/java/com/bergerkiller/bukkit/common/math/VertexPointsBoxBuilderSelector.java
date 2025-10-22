package com.bergerkiller.bukkit.common.math;

import java.util.function.Supplier;

final class VertexPointsBoxBuilderSelector {
    public static final Supplier<VertexPoints.BoxBuilder> BUILDER_IMPL = selectBuilderImpl();

    private static Supplier<VertexPoints.BoxBuilder> selectBuilderImpl() {
        return VertexPointsBasicImpl.BoxBuilder::new;

        //TODO: Dynamic load
        /*
        try {
            // Check if Vector API is available
            Class.forName("jdk.incubator.vector.DoubleVector");

            // Get preferred species
            jdk.incubator.vector.VectorSpecies<Double> preferred = jdk.incubator.vector.DoubleVector.SPECIES_PREFERRED;
            int lanes = preferred.length();

            if (lanes >= 8) {
                return VertexPointsSIMD512Impl.BoxBuilder::new;
            } else if (lanes >= 4) {
                return VertexPointsSIMD256Impl.BoxBuilder::new;
            }
        } catch (Throwable ignored) {
            // Fall through to default
        }

        return VertexPointsBasicImpl.BoxBuilder::new;
         */
    }
}
