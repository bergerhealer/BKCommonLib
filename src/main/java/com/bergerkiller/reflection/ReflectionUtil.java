package com.bergerkiller.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

public class ReflectionUtil {
    /// removes generics from a field/method declaration
    /// example: Map<String, String> stuff -> Map stuff
    public static String filterGenerics(String input) {
        int genEnd = input.indexOf('>');
        if (genEnd == -1) {
            return input;
        }
        int genStart = input.lastIndexOf('<', genEnd);
        if (genStart == -1) {
            return input;
        }
        return filterGenerics(input.substring(0, genStart) + input.substring(genEnd + 1));
    }

    /// parses method/field modifier lists
    public static int parseModifiers(String[] parts, int count) {
        // Read modifiers
        int modifiers = 0;
        for (int i = 0; i < count; i++) {
            switch (parts[i]) {
            case "public":
                modifiers |= Modifier.PUBLIC; break;
            case "private":
                modifiers |= Modifier.PRIVATE; break;
            case "protected":
                modifiers |= Modifier.PROTECTED; break;
            case "final":
                modifiers |= Modifier.FINAL; break;
            case "static":
                modifiers |= Modifier.STATIC; break;
            case "volatile":
                modifiers |= Modifier.VOLATILE; break;
            case "abstract":
                modifiers |= Modifier.ABSTRACT; break;
            }
        }
        return modifiers;
    }

    public static boolean compareModifiers(int m1, int m2) {
        return (Modifier.isPrivate(m1) == Modifier.isPrivate(m2) &&
                Modifier.isPublic(m1) == Modifier.isPublic(m2) &&
                Modifier.isProtected(m1) == Modifier.isProtected(m2) &&
                Modifier.isStatic(m1) == Modifier.isStatic(m2) &&
                Modifier.isFinal(m1) == Modifier.isFinal(m2));
    }

    public static List<SafeField<?>> fillFields(List<SafeField<?>> fields, Class<?> clazz) {
        if (clazz == null) {
            return fields;
        }
        Field[] declared = clazz.getDeclaredFields();
        ArrayList<SafeField<?>> newFields = new ArrayList<SafeField<?>>(declared.length);
        for (Field field : declared) {
            if (!Modifier.isStatic(field.getModifiers())) {
                newFields.add(new SafeField<Object>(field));
            }
        }
        fields.addAll(0, newFields);
        return fillFields(fields, clazz.getSuperclass());
    }

    public static String stringifyType(Class<?> type) {
        return (type == null ? "[null]" : type.getSimpleName());
    }

    /**
     * Produces a human-readable version of the method signature for logging purposes
     * 
     * @param method to stringify
     * @return stringified method signature
     */
    public static String stringifyMethodSignature(Method method) {
        String str = Modifier.toString(method.getModifiers());
        str += " " + stringifyType(method.getReturnType());
        str += " " + method.getName();
        str += "(";
        boolean first = true;
        for (Class<?> param : method.getParameterTypes()) {
            if (first) {
                first = false;
            } else {
                str += ", ";
            }
            str += stringifyType(param);
        }
        str += ")";
        return str;
    }

    private static boolean hasMethod(Class<?> type, Method method) {
        try {
            return type.getMethod(method.getName(), method.getParameterTypes()) != null;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Attempts to find the base class or interface in which a particular method was originally declared.
     * 
     * @param method to find
     * @return Class where it is defined
     */
    public static Class<?> findMethodClass(Method method) {
        Class<?> type = method.getDeclaringClass();
        for (Class<?> iif : type.getInterfaces()) {
            if (hasMethod(iif, method)) {
                return iif;
            }
        }
        Class<?> lowestSubClass = type;
        while ((type = type.getSuperclass()) != null) {
            if (hasMethod(type, method)) {
                lowestSubClass = type;
            }
        }
        return lowestSubClass;
    }

    /**
     * Simplifies the exception thrown when invoking a Method and throws it. This prevents very long InvocationTargetException
     * errors that barely show what actually went wrong, and instead shows only the actual exception that occurred.
     * Parameters and instance are also type-checked in case there is an error there.
     *
     * @param method that was invoked
     * @param instance the method was invoked on
     * @param args arguments passed in
     * @param ex exception that was thrown
     * @return a suitable exception to throw
     */
    public static RuntimeException fixMethodInvokeException(Method method, Object instance, Object[] args, Throwable ex) {
        // Validate the instance object that was used to invoke the method
        RuntimeException result = null;
        if (Modifier.isStatic(method.getModifiers())) {
            if (instance != null) {
                result = new IllegalArgumentException("Method " + stringifyMethodSignature(method) +
                        " is static and can not be invoked on instance of type " + stringifyType(instance.getClass()));
            }
        } else {
            if (instance == null) {
                result = new IllegalArgumentException("Method " + stringifyMethodSignature(method) +
                        " is not static and requires an instance passed in (instance is null)");
            } else {
                Class<?> m_type = findMethodClass(method);
                if (!m_type.isInstance(instance)) {
                    result = new IllegalArgumentException("Method " + stringifyMethodSignature(method) +
                            " is declared in class " + stringifyType(m_type) +
                            " and can not be invoked on object of type " + stringifyType(instance.getClass()));
                }
            }
        }

        // Validate the parameters used to invoke the method
        if (result == null) {
            Class<?>[] m_params = method.getParameterTypes();
            if (args.length != m_params.length) {
                result = new IllegalArgumentException("Method " + stringifyMethodSignature(method) +
                        " Illegal amount of arguments provided (" + args.length + "), " + 
                        "expected " + m_params.length + " - check method signature");
            } else {
                for (int i = 0; i < m_params.length; i++) {
                    Object arg = args[i];
                    if (m_params[i].isPrimitive() && arg == null) {
                        result = new IllegalArgumentException("Method " + stringifyMethodSignature(method) +
                                " Passed in null for primitive type parameter #" + (i + 1));
                        break;
                    } else if (arg != null && !LogicUtil.tryBoxType(m_params[i]).isInstance(arg)) {
                        result = new IllegalArgumentException("Method " + stringifyMethodSignature(method) +
                                " Passed in wrong type for parameter #" + (i + 1) + " (" + stringifyType(m_params[i]) + " expected" +
                                ", but was " + stringifyType(arg.getClass()) + ")");
                        break;
                    }
                }
            }
        }

        // For exception created in here, remove traces of the function call itself
        if (result != null) {
            StackTraceElement[] elems = result.getStackTrace();
            if (elems.length > 2) {
                StackTraceElement[] newElems = new StackTraceElement[elems.length - 1];
                for (int i = 0; i < newElems.length; i++) {
                    newElems[i] = elems[i + 1];
                }
                result.setStackTrace(newElems);
            }
            return result;
        }

        // Handle other types of exceptions
        if (ex instanceof InvocationTargetException) {
            ex = ((InvocationTargetException) ex).getCause();
        }

        // Wrap the exception as a proper RuntimeException so it doesn't show a pointless 'caused by'
        if (ex instanceof RuntimeException) {
            return ((RuntimeException) ex);
        } else {
            return new RuntimeException(ex);
        }
    }
}
