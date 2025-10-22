package com.bergerkiller.bukkit.common.math;

import java.util.function.Supplier;

class VertexPointsBoxBuilderSelector {
    public static final Supplier<VertexPoints.BoxBuilder> BUILDER_IMPL = VertexPointsBasicImpl.BoxBuilder::new;
}
