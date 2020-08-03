package com.bergerkiller.generated.red.mohist.bukkit.nms.utils;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>red.mohist.bukkit.nms.utils.RemapUtils</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("red.mohist.bukkit.nms.utils.RemapUtils")
public abstract class RemapUtilsHandle extends Template.Handle {
    /** @See {@link RemapUtilsClass} */
    public static final RemapUtilsClass T = Template.Class.create(RemapUtilsClass.class, com.bergerkiller.bukkit.common.server.MohistServer.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static RemapUtilsHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static String mapClassName(String className) {
        return T.mapClassName.invoker.invoke(null,className);
    }

    public static String inverseMapClassName(Class<?> type) {
        return T.inverseMapClassName.invoker.invoke(null,type);
    }

    public static String inverseMapMethodName(Class<?> type, String name, Class<?>[] parameterTypes) {
        return T.inverseMapMethodName.invoker.invoke(null,type, name, parameterTypes);
    }

    public static String inverseMapFieldName(Class<?> type, String fieldName) {
        return T.inverseMapFieldName.invoker.invoke(null,type, fieldName);
    }

    /**
     * Stores class members for <b>red.mohist.bukkit.nms.utils.RemapUtils</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RemapUtilsClass extends Template.Class<RemapUtilsHandle> {
        public final Template.StaticMethod<String> mapClassName = new Template.StaticMethod<String>();
        public final Template.StaticMethod<String> inverseMapClassName = new Template.StaticMethod<String>();
        public final Template.StaticMethod<String> inverseMapMethodName = new Template.StaticMethod<String>();
        public final Template.StaticMethod<String> inverseMapFieldName = new Template.StaticMethod<String>();

    }

}

