package com.bergerkiller.bukkit.common.reflection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.asm.Type;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.reflection.gen.CallbackMethod;
import com.bergerkiller.bukkit.common.reflection.gen.CallbackSignature;
import com.bergerkiller.bukkit.common.reflection.gen.ProxyCallbackSignature;
import com.bergerkiller.bukkit.common.reflection.gen.SuperCallbackSignature;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * A simple implementation builder that allows the creation of extensions of
 * classes. This is done by specifying interfaces and implementations for these
 * interfaces.<br><br>
 *
 * To construct new extensions of classes, do the following:<br>
 * - Make new interface(s) containing the methods to override<br>
 * - Implement these interface(s) in your own implementation class(es)<br>
 * - Construct a new Class Builder using the superclass and
 * implementation<br><br>
 *
 * To call super methods in the superclass, add interface methods starting with
 * 'super_'. These methods are never called in the implemented version, you can
 * leave them empty there.
 */
public class ClassBuilder {

    private static final String SUPER_PREFIX = "super_";
    private final Class<?> superClass;
    private final Enhancer enhancer = new Enhancer();
    private final Map<Class<?>, Object> callbackInstancesBuffer = new HashMap<Class<?>, Object>();
    private final Map<Signature, CallbackSignature> callbackSignatures = new HashMap<Signature, CallbackSignature>();
    private final List<Class<?>> callbackClasses;

    @SuppressWarnings("rawtypes")
    public ClassBuilder(Class<?> superclass, Class... callbackClasses) {
        this(superclass, Arrays.asList((Class<?>[]) callbackClasses));
    }

    public ClassBuilder(Class<?> superclass, Collection<Class<?>> callbackClasses) {
        if (LogicUtil.nullOrEmpty(callbackClasses)) {
            throw new IllegalArgumentException("At least one Callback Class is needed to use a Class Builder");
        }
        this.callbackClasses = Collections.unmodifiableList(new ArrayList<Class<?>>(callbackClasses));
        try {
            this.superClass = superclass;

            // Obtain all available interfaces and callbacks from the callback classes
            Collection<Class<?>> interfaceClasses = new ArrayList<Class<?>>(callbackClasses.size());
            for (Class<?> callbackClass : callbackClasses) {
                for (Class<?> interfaceClass : callbackClass.getInterfaces()) {
                    interfaceClasses.add(interfaceClass);

                    // Set the initial Callbacks - this is important during the CallbackFilter accept phase
                    for (Method method : interfaceClass.getDeclaredMethods()) {
                        addCallback(method, null);
                    }
                }
            }

            // Initialize the base Class
            enhancer.setSuperclass(superclass);
            enhancer.setInterfaces(interfaceClasses.toArray(new Class<?>[0]));
            enhancer.setClassLoader(getClass().getClassLoader());
            enhancer.setCallbackTypes(new Class<?>[]{NoOp.class, CallbackMethodInterceptor.class});
            enhancer.setCallbackFilter(new CallbackFilter() {
                @Override
                public int accept(Method method) {
                    return callbackSignatures.containsKey(getSig(method)) ? 1 : 0;
                }
            });

            // Generate callback instances
            CallbackSignature callback;
            for (Class<?> interfaceClass : interfaceClasses) {
                for (Method method : interfaceClass.getDeclaredMethods()) {
                    // Find the best suitable way of redirecting this Method
                    callback = null;
                    final String name = method.getName();
                    if (name.startsWith(SUPER_PREFIX)) {
                        // Automatically redirect to the super method
                        final String methodName = Common.SERVER.getMethodName(superclass, name.substring(SUPER_PREFIX.length()), method.getParameterTypes());
                        final Signature superSig = getSig(method, methodName);
                        try {
                            // Try to get the method in the superclass - prior
                            if (!SafeMethod.contains(superClass, methodName, method.getParameterTypes())) {
                                throw new RuntimeException("Could not find super method: " + superSig);
                            }
                            callback = new SuperCallbackSignature(superSig);
                        } catch (IllegalArgumentException ex) {
                            throw new RuntimeException("Could not access super method: " + superSig, ex);
                        }
                    } else {
                        // Find this method in one of the callback classes
                        for (Class<?> callBackClass : callbackClasses) {
                            if (interfaceClass.isAssignableFrom(callBackClass)) {
                                callback = new ProxyCallbackSignature(callBackClass.getDeclaredMethod(method.getName(), method.getParameterTypes()));
                                break;
                            }
                        }
                    }
                    // Put the callback instance if available
                    if (callback == null) {
                        throw new RuntimeException("Interface method is not implemented: " + method.getName());
                    }
                    addCallback(method, callback);
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("Could not initialize Entity Class Builder for '" + superclass.getSimpleName() + "':", t);
        }
    }

    /**
     * Creates a new instance using the constructor that matches the
     * argumentTypes, using the arguments. The callbacks are applied to the
     * newly created instance.
     *
     * @param argumentTypes of the superclass constructor to use
     * @param arguments to pass along the superclass constructor
     * @param callbacks to use for the newly created instance
     * @return a new instance created by this ClassBuilder
     */
    public synchronized Object create(Class<?>[] argumentTypes, Object[] arguments, Collection<Object> callbacks) {
        try {
            // Prepare the instances buffer: add all callback instances
            for (Object callbackInstance : callbacks) {
                callbackInstancesBuffer.put(callbackInstance.getClass(), callbackInstance);
            }

            // Create a method interceptor, assign it and then create a new instance
            final CallbackMethodInterceptor interceptor = new CallbackMethodInterceptor();
            enhancer.setCallbacks(new Callback[]{NoOp.INSTANCE, interceptor});
            final Object instance = enhancer.create(argumentTypes, arguments);

            // Prepare for clearing the callback instances buffer
            interceptor.createAllCallbacks(instance);

            return instance;
        } finally {
            callbackInstancesBuffer.clear();
        }
    }

    /**
     * Gets an unmodifiable List of Callback Classes used by this Class Builder
     *
     * @return Unmodifiable List of Callback Classes
     */
    public List<Class<?>> getCallbackClasses() {
        return this.callbackClasses;
    }

    /**
     * Gets the superclass that new Instances created by this builder extend
     *
     * @return superclass
     */
    public Class<?> getSuperClass() {
        return this.superClass;
    }

    private void addCallback(Method superMethod, CallbackSignature callback) {
        Signature sig = getSig(superMethod);
        this.callbackSignatures.put(sig, callback);
        String sigName = sig.getName();
        if (sigName.startsWith(SUPER_PREFIX)) {
            return;
        }
        String fixedName = Common.SERVER.getMethodName(this.superClass, sigName, superMethod.getParameterTypes());
        if (!fixedName.equals(sigName)) {
            Signature fixedSig = getSig(superMethod, fixedName);
            this.callbackSignatures.put(fixedSig, callback);
        }
    }

    private static Signature getSig(Method method) {
        return getSig(method, method.getName());
    }

    private static Signature getSig(Method method, String name) {
        return new Signature(name, Type.getReturnType(method), Type.getArgumentTypes(method));
    }

    private final class CallbackMethodInterceptor implements MethodInterceptor {

        private final Map<Signature, Object> callbackMethods;

        public CallbackMethodInterceptor() {
            this.callbackMethods = new HashMap<Signature, Object>(callbackSignatures);
        }

        /**
         * Converts all (any remaining) Callback Signatures to Callback Methods.
         * After this method is called, the callback instances buffer can be
         * cleared and re-used.
         *
         * @param instance that was created
         */
        public void createAllCallbacks(Object instance) {
            for (Map.Entry<Signature, Object> callbackEntry : this.callbackMethods.entrySet()) {
                Object callback = callbackEntry.getValue();
                if (callback instanceof CallbackSignature) {
                    callbackEntry.setValue(createCallback(callback, instance));
                }
            }
        }

        @Override
        public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            Object callback = this.callbackMethods.get(proxy.getSignature());
            if (callback instanceof CallbackSignature) {
                // Initialize this callback method (occurred in the Class constructor)
                callback = createCallback(callback, instance);
                this.callbackMethods.put(proxy.getSignature(), callback);
            }
            if (callback instanceof CallbackMethod) {
                // Execute the method
                return ((CallbackMethod) callback).invoke(instance, args);
            } else {
                // This should never occur because we have a filter, but it's here for safety purposes
                return proxy.invokeSuper(instance, args);
            }
        }

        private CallbackMethod createCallback(Object callbackSignature, Object instance) {
            return ((CallbackSignature) callbackSignature).createCallback(instance, callbackInstancesBuffer);
        }
    }
}
