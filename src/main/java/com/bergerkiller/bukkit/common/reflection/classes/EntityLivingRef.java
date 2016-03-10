package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.*;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class EntityLivingRef extends EntityRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("EntityLiving");
    public static final FieldAccessor<Object> attributeMap = TEMPLATE.getField("bp");
    //  No longer supported
//	public static final MethodAccessor<Void> resetAttributes = TEMPLATE.getMethod("aC");
    public static final MethodAccessor<Object> getAttributesMap = TEMPLATE.getMethod("getAttributeMap");
    public static final MethodAccessor<Object> getNavigation = new SafeMethod<Object>(CommonUtil.getNMSClass("EntityInsentient"), "getNavigation");
    public static final FieldAccessor<Float> forwardMovement = TEMPLATE.getField("bB");
}
