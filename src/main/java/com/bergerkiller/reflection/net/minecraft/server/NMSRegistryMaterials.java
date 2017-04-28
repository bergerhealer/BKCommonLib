package com.bergerkiller.reflection.net.minecraft.server;

import java.util.Set;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

public class NMSRegistryMaterials {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("RegistryMaterials");
    public static final MethodAccessor<Set<?>> keySet = T.selectMethod("public Set<K> keySet()");
    public static final MethodAccessor<Object> getValue = T.selectMethod("public V get(K paramK)");
}
