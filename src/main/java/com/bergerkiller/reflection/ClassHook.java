package com.bergerkiller.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bergerkiller.bukkit.common.Logging;

import net.sf.cglib.proxy.MethodProxy;

public class ClassHook<T extends ClassHook<?>> extends ClassInterceptor {
    private static final Map<Class<?>, HookMethodList> hookMethodMap = new HashMap<Class<?>, HookMethodList>();
    public T base;
    private final HookMethodList methods;

    @SuppressWarnings("unchecked")
    public ClassHook() {
        this.methods = loadMethodList(getClass());
        this.base = (T) this.methods.baseInterceptor.hook(this);
    }

    @Override
    protected Invokable getCallback(Method method) {
        Class<?> method_class = method.getDeclaringClass();
        for (HookMethodEntry entry : methods.entries) {
            if (method.equals(entry.findMethodIn(method_class))) {
                //Logging.LOGGER_REFLECTION.info("[" + method.getDeclaringClass().getSimpleName() + "] Hooked " + getMethodName(method) + " to " + getMethodName(entry.method));
                return entry;
            }
        }
        return null;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface HookMethod {
        String value();
    }

    private static HookMethodList loadMethodList(Class<?> hookClass) {
        if (!ClassHook.class.isAssignableFrom(hookClass)) {
            return new HookMethodList();
        }

        HookMethodList list = hookMethodMap.get(hookClass);
        if (list == null) {
            list = new HookMethodList();

            // Find all methods with a @HookMethod annotation
            for (Method method : hookClass.getDeclaredMethods()) {
                HookMethod hm = method.getAnnotation(HookMethod.class);
                if (hm != null) {
                    list.entries.add(new HookMethodEntry(method, hm.value()));
                }
            }

            // Handle superclasses recursively
            list.entries.addAll(loadMethodList(hookClass.getSuperclass()).entries);
            hookMethodMap.put(hookClass, list);
        }
        return list;
    }

    private static class HookMethodList {
        public final List<HookMethodEntry> entries = new ArrayList<HookMethodEntry>();
        public final ClassInterceptor baseInterceptor = new ClassInterceptor() {
            @Override
            protected Invokable getCallback(Method method) {
                HookMethodEntry foundEntry = null;
                Iterator<HookMethodEntry> iter = entries.iterator();
                do {
                    if (!iter.hasNext()) return null;
                } while (!(foundEntry = iter.next()).method.equals(method));

                final HookMethodEntry entry = foundEntry;
                return new Invokable() {
                    @Override
                    public Object invoke(Object instance, Object... args) {
                        ClassHook<?> hook = ((ClassHook<?>) instance);

                        // Figure out what object we are currently handling and what type it is
                        Object enhancedInstance = hook.instance();
                        Class<?> enhancedType = enhancedInstance.getClass();

                        if (enhancedInstance instanceof EnhancedObject) {
                            // Find a method proxy to use for calling the super method, and cache it
                            MethodProxy proxy = entry.superMethodProxyMap.get(enhancedType);
                            if (proxy == null) {
                                Class<?> baseType = ((EnhancedObject) enhancedInstance).CI_getBaseType();
                                Method m = entry.findMethodIn(baseType);
                                if (m == null) {
                                    throw new UnsupportedOperationException("Class " + baseType.getName() + 
                                            " does not contain method " + entry.toString());
                                }

                                proxy = findMethodProxy(m, enhancedInstance);
                                entry.superMethodProxyMap.put(enhancedType, proxy);
                            }

                            // Call invokeSuper() on the MethodProxy to call the base class method
                            try {
                                return proxy.invokeSuper(enhancedInstance, args);
                            } catch (Throwable e) {
                                throw new RuntimeException("Failed to invoke super method " + proxy.getSignature().toString(), e);
                            }
                        } else {
                            // Not an enhanced instance, find the method in the class and invoke
                            Method m = entry.findMethodIn(enhancedType);
                            if (m == null) {
                                throw new UnsupportedOperationException("Class " + enhancedType.getName() + 
                                        " does not contain method " + entry.toString());
                            }

                            // Invoke the method directly
                            try {
                                return m.invoke(enhancedInstance, args);
                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                                throw new RuntimeException("Failed to invoke method " + m.toString(), e);
                            }
                        }
                    }
                };
            }
        };
    }

    private static String getMethodName(Method method) {
        String result = "";
        result += Modifier.toString(method.getModifiers()) + " ";
        result += method.getReturnType().getSimpleName() + " ";
        result += method.getName();
        result += "(";
        boolean firstParam = true;
        for (Class<?> p : method.getParameterTypes()) {
            if (firstParam) {
                firstParam = false;
            } else {
                result += ", ";
            }
            result += p.getSimpleName();
        }
        result += ")";
        return result;
    }

    private static class HookMethodEntry extends MethodInvokable {
        private final Map<Class<?>, Method> superMethodMap = new HashMap<Class<?>, Method>();
        public final Map<Class<?>, MethodProxy> superMethodProxyMap = new HashMap<Class<?>, MethodProxy>();
        public final Class<?> parameters[];
        public final Class<?> returnType;
        public final String name;

        public HookMethodEntry(Method method, String name) {
            super(method);
            this.name = name;
            this.parameters = method.getParameterTypes();
            this.returnType = method.getReturnType();
        }

        @Override
        public String toString() {
            String result =  returnType.getSimpleName() + " " + name + "(";
            boolean first = true;
            for (Class<?> param : parameters) {
                if (first) {
                    first = false;
                } else {
                    result += ", ";
                }
                result += param.getSimpleName();
            }
            return result + ")";
        }

        public Method findMethodIn(Class<?> type) {
            if (type == null) {
                return null;
            }
            Method m = superMethodMap.get(type);
            if (m == null) {
                //TODO: Use something other than ClassTemplate for this shit!
                ClassTemplate<?> template = ClassTemplate.create(type);
                m =  template.selectRawMethod(name, false);
                if (m != null) {
                    superMethodMap.put(type, m);
                }
            }
            return m;
        }
    }
}
