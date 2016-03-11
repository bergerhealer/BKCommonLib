package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import org.bukkit.craftbukkit.v1_9_R1.chunkio.ChunkIOExecutor;

import java.util.Map;

public class ChunkIOExecutorRef {

    public static final FieldAccessor<Object> asynchronousExecutor = new SafeField<Object>(ChunkIOExecutor.class, "instance");
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final ClassTemplate<Object> TEMPLATE = new ClassTemplate(CommonUtil.getCBClass("util.AsynchronousExecutor"));
    public static final FieldAccessor<Map<?, ?>> tasks = TEMPLATE.getField("tasks");
}
