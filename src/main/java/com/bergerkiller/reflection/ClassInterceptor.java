package com.bergerkiller.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.objenesis.ObjenesisHelper;
import org.objenesis.instantiator.ObjectInstantiator;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

import net.sf.cglib.asm.Type;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

/**
 * Base implementation for hooking other classes and intercepting method calls.
 * A single {@link ClassInterceptor} can be used to hook multiple objects in a single session.
 * Every hooked object stores a reference to the ClassInterceptor instance, which can be retrieved
 * using {@link #get(Object, Class)}.
 * <br><br>
 * It is required that an implementation of ClassInterceptor never changes the behavior
 * of {@link #getCallback(Method)}. In other words, for the given Method parameter, it should
 * consistently return the same {@link CallbackDelegate} across all instances.
 */
public abstract class ClassInterceptor {
    private static final Map<Class<?>, Map<Method, Invokable>> globalMethodDelegatesMap = new HashMap<Class<?>, Map<Method, Invokable>>();
    private static final Map<ClassPair, EnhancedClass> enhancedTypes = new HashMap<ClassPair, EnhancedClass>();
    private boolean useGlobalCallbacks = true;
    private final Map<Method, Invokable> globalMethodDelegates;
    private final InstanceHolder lastHookedObject = new InstanceHolder();
    private final ThreadLocal<StackInformation> stackInfo = new ThreadLocal<StackInformation>() {
        @Override
        protected StackInformation initialValue() { return new StackInformation(); }
    };

    public ClassInterceptor() {
        synchronized (globalMethodDelegatesMap) {
            Map<Method, Invokable> globalMethodDelegates = globalMethodDelegatesMap.get(getClass());
            if (globalMethodDelegates == null) {
                globalMethodDelegates = new HashMap<Method, Invokable>();
                globalMethodDelegatesMap.put(getClass(), globalMethodDelegates);
            }
            this.globalMethodDelegates = globalMethodDelegates;
        }
    }

    /**
     * Retrieves the callback delegate for handling a certain Method call in the base object.
     * To not intercept the Method, return NULL.
     * 
     * @param method
     * @return Callback delegate to execute, or NULL to not intercept the Method
     */
    protected abstract Invokable getCallback(Method method);

    /**
     * Creates a new hooked instance of the type specified, without constructing it
     * 
     * @param type to create a new hook of
     * @return new instance of the type, hooked by this ClassInterceptor
     */
    public <T> T createInstance(Class<T> type) {
        return createEnhancedClass(this, type, null, null, null);
    }

    /**
     * Creates a new hooked instance of the type specified by calling a constructor
     * 
     * @param type to create a new hook of
     * @param paramTypes of the constructor
     * @param params for when calling the constructor
     * @return newly constructed instance of the type, hooked by this ClassInterceptor
     */
    public <T> T constructInstance(Class<T> type, Class<?>[] paramTypes, Object[] params) {
        return createEnhancedClass(this, type, null, paramTypes, params);
    }

    /**
     * Creates an extension of the object intercepting the callbacks as specified by @getCallback.
     * The object state (fields) are copied over from the old object to the new one.
     * 
     * @param object to hook
     * @return hooked object
     */
    public <T> T hook(T object) {
        return createEnhancedClass(this, object.getClass(), object, null, null);
    }

    /**
     * Initializes the interceptor so that it is aware of the object and callbacks it has to call
     * for a particular object type, but does not hook. Mocking an object will not intercept its
     * method calls, but will enable proper function of the callback functions on the object.
     * <br><br>
     * Only one object can be mocked by an interceptor at one time. Hooking a new object will
     * replace the original mocked object.
     * 
     * @param object to mock
     */
    public void mock(Object object) {
        this.lastHookedObject.value = object;
    }

    /**
     * Removes all extensions added to an object, ending the method intercepting.
     * The object state (fields) are copied over from the old object to the new one.
     * 
     * @param object to unhook
     * @return unhooked base object
     */
    public static <T> T unhook(T object) {
        // If this object was last-hooked, clear the state so this object is no more returned
        if (object instanceof EnhancedObject) {
            EnhancedObject enhancedObject = (EnhancedObject) object;
            ClassInterceptor ci = enhancedObject.CI_getInterceptor();
            EnhancedClass eh = enhancedObject.CI_getEnhancedClass();

            if (ci.lastHookedObject.value == object) {
                ci.lastHookedObject.value = null;
            }

            return eh.createBase(object);
        } else {
            return object; // not enhanced; ignore
        }
    }

    /**
     * Retrieves the ClassInterceptor owner of a hooked object
     * 
     * @param object to query
     * @param interceptorClass type
     * @return hook owner, or NULL if the object is not hooked by this ClassInterceptor type.
     */
    @SuppressWarnings("unchecked")
    public static <T extends ClassInterceptor> T get(Object object, Class<T> interceptorClass) {
        ClassInterceptor interceptor = get(object);
        return interceptorClass.isInstance(interceptor) ? (T) interceptor : null;
    }

    /**
     * Retrieves the ClassInterceptor owner of a hooked object, without checking for type
     * 
     * @param object to query
     * @return interceptor
     */
    protected static ClassInterceptor get(Object object) {
        return (object instanceof EnhancedObject) ? ((EnhancedObject) object).CI_getInterceptor() : null;
    }

    /**
     * Special callback delegate optimized for invoking a method in the ClassInterceptor instance
     */
    public static class MethodInvokable implements Invokable {
        public final Method method;

        public MethodInvokable(Method method) {
            this.method = method;
            if (method == null) {
                throw new IllegalArgumentException("Method can not be null");
            }
        }

        @Override
        public final Object invoke(Object instance, Object... args) {
            try {
                return method.invoke(((EnhancedObject) instance).CI_getInterceptor(), args);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException("Failed to invoke method " + this.method.toString(), e);
            }
        }
    }

    /**
     * Special callback delegate that always returns null, void, false, or 0 depending what type is expected.
     * It fulfills the role of an "ignore".
     */
    public static class NullInvokable implements Invokable {
        private final Object nullObject;

        public NullInvokable() {
            this.nullObject = null;
        }

        public NullInvokable(Object nullObject) {
            this.nullObject = nullObject;
        }

        public NullInvokable(Method signature) {
            Class<?> t = signature.getReturnType();
            if (boolean.class.isAssignableFrom(t)) {
                this.nullObject = Boolean.valueOf(false);
            } else if (char.class.isAssignableFrom(t)) {
                this.nullObject = Character.valueOf('\0');
            } else if (byte.class.isAssignableFrom(t)) {
                this.nullObject = Byte.valueOf((byte) 0);
            } else if (short.class.isAssignableFrom(t)) {
                this.nullObject = Short.valueOf((short) 0);
            } else if (int.class.isAssignableFrom(t)) {
                this.nullObject = Integer.valueOf(0);
            } else if (long.class.isAssignableFrom(t)) {
                this.nullObject = Long.valueOf(0L);
            } else if (float.class.isAssignableFrom(t)) {
                this.nullObject = Float.valueOf(0.0F);
            } else if (double.class.isAssignableFrom(t)) {
                this.nullObject = Double.valueOf(0.0);
            } else {
                this.nullObject = null;
            }
        }

        @Override
        public Object invoke(Object instance, Object... args) {
            return this.nullObject;
        }
    }

    /**
     * When all method callbacks are global:
     * <ul>
     * <li>getCallback() is only called once per method (faster!)
     * <li>it is impossible to have instance-specific callbacks
     * <li>this is the default setting
     * </ul>
     * When methods are only kept local to this ClassInterceptor:
     * <ul>
     * <li>getCallback() is called for every method, for every thread, for every interceptor instance (slower!)
     * <li>it is possible to have instance-specific callbacks
     * </ul>
     * 
     * @param global whether to cache callback methods globally
     */
    public final void setUseGlobalCallbacks(boolean global) {
        useGlobalCallbacks = global;
    }

    /**
     * Gets the Object that is currently being invoked, or was last hooked
     * 
     * @return Underlying Object
     */
    protected final Object instance() {
        StackInformation stack = this.stackInfo.get();
        if (stack.current.instance != null) {
            return stack.current.instance;
        } else if (lastHookedObject.value == null) {
            throw new IllegalStateException("No object is handled right now");
        } else {
            return lastHookedObject.value;
        }
    }

    /**
     * Invokes the method on the superclass. This bypasses the callbacks and enables the original base class
     * to handle the method.
     * 
     * @param method to invoke
     * @param instance to invoke the method on
     * @param args arguments for the method
     * @return The response from executing
     * @throws Throwable
     */
    public final Object invokeSuperMethod(Method method, Object instance, Object[] args) {
        try {
            if (instance instanceof EnhancedObject) {
                return findMethodProxy(method, instance).invokeSuper(instance, args);
            } else {
                return method.invoke(instance, args);
            }
        } catch (Throwable t) {
            throw new RuntimeException("Failed to invoke super method " + method.toString());
        }
    }

    /**
     * Finds the CGLib method proxy that can be used to invoke a super method in the enhanced instance.
     * No caching is done, so it may be preferable to cache the proxy when repeatedly used.
     * Please note that the MethodProxy returned is only valid for objects hooked by this
     * ClassInterceptor and Object instance type passed in.
     * 
     * @param method to find
     * @param instance to find the method in
     * @return MethodProxy
     * @throws UnsupportedOperationException when the method can not be proxied
     */
    protected final MethodProxy findMethodProxy(Method method, Object instance) {
        // Fast access: check if the last-called proxy matches the method
        // This is a common case where a handler calls a base method
        // Doing it this way avoids a map get/put call
        StackFrame frame = this.stackInfo.get().current;
        if (instance == frame.instance && method.equals(frame.method)) {
            return frame.proxy;
        }

        // Slower way of instantiating a new MethodProxy for this type
        Signature sig = new Signature(method.getName(), Type.getReturnType(method), Type.getArgumentTypes(method));
        MethodProxy proxy = null;
        try {
            proxy = MethodProxy.find(instance.getClass(), sig);
        } catch (Throwable t) {
        }

        // Dont allow this!
        if (proxy == null) {
            throw new UnsupportedOperationException("Proxy for super method " + method.toGenericString() + 
                    " does not exist in class " + instance.getClass().getName());
        }
        return proxy;
    }

    /* ====================================================================================== */
    /* ================================== Implementation Code =============================== */
    /* ====================================================================================== */

    private static synchronized <T> T createEnhancedClass(final ClassInterceptor interceptor, 
                                                          Class<?> objectType, T object,
                                                          Class<?>[] paramTypes, Object[] params)
    {
        // The key used to access the EnhancedClass instance for creating this instance
        final ClassPair key = new ClassPair(interceptor.getClass(), objectType);

        // A list of fixed values returned by the EnhancedObject interface
        final Callback[] callbacks = new Callback[] {
                new EnhancedObjectProperty("CI_getInterceptor", interceptor),
                new EnhancedObjectProperty("CI_getBaseType", objectType),
                new EnhancedObjectProperty("CI_getEnhancedClass", null),
                new CallbackMethodInterceptor(interceptor),
                NoOp.INSTANCE
        };

        // Try to find the CGLib-generated enhanced class that provides the needed callbacks
        // If none exists yet, generate a new one and put it into the table for future re-use
        EnhancedClass enhanced = enhancedTypes.get(key);
        if (enhanced == null) {
            final StackInformation stackInfo = interceptor.stackInfo.get();
            Enhancer enhancer = new Enhancer();
            enhancer.setClassLoader(key.hookClass.getClassLoader());

            // When its an interface, we have to implement it as opposed to extending it
            if (objectType.isInterface()) {
                enhancer.setSuperclass(Object.class);
                enhancer.setInterfaces(new Class<?>[] { EnhancedObject.class, objectType } );
            } else {
                enhancer.setSuperclass(objectType);
                enhancer.setInterfaces(new Class<?>[] { EnhancedObject.class } );
            }

            // Initialize the callback types and callback mapping
            enhancer.setCallbackTypes(LogicUtil.getTypes(callbacks));
            enhancer.setCallbackFilter(new CallbackFilter() {
                @Override
                public int accept(Method method) {
                    // Properties are returned as Fixed Value types for quick access
                    for (int i = 0; i < callbacks.length; i++) {
                        if (callbacks[i] instanceof EnhancedObjectProperty &&
                                ((EnhancedObjectProperty) callbacks[i]).getName().equals(method.getName())) {
                            return i;
                        }
                    }

                    // Handle callbacks/no-op
                    Invokable callback = stackInfo.methodDelegates.get(method);
                    if (callback == null) {
                        callback = interceptor.getCallback(method);
                        if (callback == null) {
                            return (callbacks.length - 1); /* No callback, redirect to No Operation handler */
                        }
                        stackInfo.methodDelegates.put(method, callback);
                    }
                    return (callbacks.length - 2); /* Has callback, redirect to CallbackMethodInterceptor */
                }
            });

            // Finally create the enhanced class type and store it in the mapping for later use
            enhanced = new EnhancedClass(objectType, enhancer.createClass());
            enhancedTypes.put(key, enhanced);
        }

        // Update EnhancedClass property
        callbacks[2] = new EnhancedObjectProperty("CI_getEnhancedClass", enhanced);

        // Register the method callbacks, and then create the enhanced object instance using Objenesis
        // Note that since we don't call any constructors, CGLib does not update the object callback list
        // We force an explicit internal update by calling the CI_getInterceptor() interface function
        // After this is done, we must delete the callbacks again to prevent a memory leak
        Enhancer.registerCallbacks(enhanced.enhancedType, callbacks);
        T enhancedObject = enhanced.createEnhanced(object, paramTypes, params);
        interceptor.lastHookedObject.value = enhancedObject;
        ((EnhancedObject) enhancedObject).CI_getInterceptor();
        Enhancer.registerCallbacks(enhanced.enhancedType, null);
        return enhancedObject;
    }

    private static final class EnhancedObjectProperty implements FixedValue {
        private final String name;
        private final Object value;

        public EnhancedObjectProperty(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public final String getName() {
            return name;
        }

        @Override
        public final Object loadObject() throws Exception {
            return this.value;
        }
    }

    private static final class ClassPair {
        public final Class<?> hookClass;
        public final Class<?> instanceClass;
        private final int hashcode;

        public ClassPair(Class<?> hookClass, Class<?> instanceClass) {
            this.hookClass = hookClass;
            this.instanceClass = instanceClass;
            this.hashcode = (hookClass.hashCode() >> 1) + (instanceClass.hashCode() >> 1);
        }

        @Override
        public int hashCode() {
            return this.hashcode;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof ClassPair) {
                ClassPair p = (ClassPair) other;
                return hookClass.equals(p.hookClass) && instanceClass.equals(p.instanceClass);
            }
            return false;
        }
    }

    /**
     * Maintains metadata information about a particular CGLib-enhanced class.
     * Also handles the construction of new objects during hooking/unhooking.
     */
    public static final class EnhancedClass {
        public final ClassTemplate<?> baseTemplate;
        public final ObjectInstantiator<?> enhancedInstantiator;
        public final ObjectInstantiator<?> baseInstantiator;
        public final Class<?> enhancedType;

        public EnhancedClass(Class<?> baseType, Class<?> enhancedType) {
            this.baseTemplate = ClassTemplate.create(baseType);
            this.enhancedType = enhancedType;
            this.baseInstantiator = ObjenesisHelper.getInstantiatorOf(baseType);
            this.enhancedInstantiator = ObjenesisHelper.getInstantiatorOf(enhancedType);
            if (this.baseInstantiator == null)
                throw new RuntimeException("Base Class " + baseType.getName() + " has no instantiator");
            if (this.enhancedInstantiator == null)
                throw new RuntimeException("Enhanced Class " + enhancedType.getName() + " has no instantiator");
        }

        @SuppressWarnings("unchecked")
        public <T> T createBase(T enhanced) {
            Object base = this.baseInstantiator.newInstance();
            if (base == null)
                throw new RuntimeException("Class " + baseTemplate.getType().getName() + " could not be instantiated (newInstance failed)");

            baseTemplate.transfer(enhanced, base);
            return (T) base;
        }

        @SuppressWarnings("unchecked")
        public <T> T createEnhanced(T base, Class<?>[] paramTypes, Object[] params) {
            Object enhanced = null;
            if (paramTypes == null) {
                // Null parameter array: use Objenesis to create an instance without calling a constructor
                enhanced = this.enhancedInstantiator.newInstance();
            } else {
                // Find the constructor in the base class and call it
                Constructor<?> constructor = null;
                try {
                    constructor = enhancedType.getConstructor(paramTypes);
                    enhanced = constructor.newInstance(params);
                } catch (Throwable t) {
                    Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to construct " + enhancedType.getName(), t);
                }
            }

            if (enhanced == null)
                throw new RuntimeException("Class " + enhancedType.getName() + " could not be instantiated (newInstance failed)");

            if (base != null) {
                baseTemplate.transfer(base, enhanced);
            }
            return (T) enhanced;
        }
    }

    /**
     * The MethodInterceptor is the first to receive method execution notification from CGLib.
     * In here the right callback to use is found and executed. It also updates the call stack.
     * The call stack is important for the correct workings of:
     * <ul>
     * <li>{@link ClassInterceptor#findMethodProxy(method, instance)}
     * <li>{@link ClassInterceptor#instance()}
     * </ul>
     */
    private static final class CallbackMethodInterceptor implements MethodInterceptor {
        private final ClassInterceptor interceptor;

        public CallbackMethodInterceptor(ClassInterceptor interceptor) {
            this.interceptor = interceptor;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            StackInformation stack = this.interceptor.stackInfo.get();
            try {
                // Push stack element
                // Inlined linked list is much faster than using a LinkedList with method calls
                if (stack.current.next == null) {
                    stack.current.next = new StackFrame();
                    stack.current.next.previous = stack.current;
                }
                stack.current = stack.current.next;
                stack.current.instance = obj;
                stack.current.proxy = proxy;
                stack.current.method = method;

                // Find method callback delegate if we don't know yet
                Invokable callback = stack.methodDelegates.get(method);
                if (callback == null) {
                    synchronized (interceptor.globalMethodDelegates) {
                        callback = interceptor.globalMethodDelegates.get(method);
                    }
                    if (callback == null) {
                        callback = interceptor.getCallback(method);
                        if (callback == null) {
                            callback = new SuperClassInvokable(proxy);
                        }
                        stack.methodDelegates.put(method, callback);

                        // Register globally if needed
                        if (interceptor.useGlobalCallbacks){
                            synchronized (interceptor.globalMethodDelegates) {
                                interceptor.globalMethodDelegates.put(method, callback);
                            }
                        }
                    }
                }

                // Make sure to inline the MethodCallbackDelegate to avoid a stack frame
                if (callback instanceof MethodInvokable) {
                    try {
                        return ((MethodInvokable) callback).method.invoke(this.interceptor, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                }

                // Execute the callback
                return callback.invoke(obj, args);
            } finally {
                // Pop stack element
                stack.current.instance = null; // invalidate
                stack.current = stack.current.previous;
            }
        }
    }

    /**
     * This will never be used in reality and is strictly here to deal with unexpected NULL callbacks
     */
    private static final class SuperClassInvokable implements Invokable {
        private final MethodProxy proxy;

        public SuperClassInvokable(MethodProxy proxy) {
            this.proxy = proxy;
        }

        @Override
        public Object invoke(Object instance, Object... args) {
            try {
                return proxy.invokeSuper(instance, args);
            } catch (Throwable e) {
                throw new RuntimeException("Failed to invoke super method " + proxy.getSignature().toString(), e);
            }
        }
    };

    /**
     * We have to track the 'current' object this interceptor is handling.
     * When only a single instance is ever used, it will always be the same.
     * But with multiple instances, we must guarantee that the handler is made
     * aware of the current object when invoking from callback delegates.
     * 
     * This is also important when handling a callback -> super invocation, which
     * can re-use the method called to avoid expensive map get/put operations.
     * 
     * In addition, it stores a mapping of methods to callback delegates for use by the interceptor.
     * These are also stored here so they are thread-local, preventing cross-thread Map access.
     */
    private static final class StackInformation {
        public final Map<Method, Invokable> methodDelegates = new HashMap<Method, Invokable>();

        public StackFrame frames = new StackFrame();
        public StackFrame current = frames;
    }

    /**
     * A single execution stack frame as handled by the method interceptor
     */
    private static final class StackFrame {
        public Object instance = null;
        public MethodProxy proxy = null;
        public Method method = null;
        public StackFrame next = null;
        public StackFrame previous = null;
    }

    /**
     * Sometimes ClassInterceptors can be copied (ClassHook!) and so we must wrap the object
     */
    private static class InstanceHolder {
        public Object value = null;
    }

    /**
     * All hooked objects implement this interface to retrieve the interceptor owner and base type
     */
    protected static interface EnhancedObject {
        public ClassInterceptor CI_getInterceptor();
        public Class<?> CI_getBaseType();
        public EnhancedClass CI_getEnhancedClass();
    }
}
