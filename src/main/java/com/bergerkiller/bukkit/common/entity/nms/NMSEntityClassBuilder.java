package com.bergerkiller.bukkit.common.entity.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.reflection.classes.WorldRef;

import net.sf.cglib.asm.Type;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

/**
 * Takes care of NMS Entity class creation, allowing multiple callback methods to be implemented.
 * All methods provided by a callback Class that are supposed to be inherited by the produced Entity
 * class instance, should be in a separate interface Class implemented by the callback class.<br>
 * <br>
 * For example, an 'InventoryHookImpl' class implementing 'InventoryHook', with InventoryHook
 * declaring the methods 'setItem' and 'super_setItem'.<br>
 * <br>
 * To call super methods, add methods in the interface starting with <i>super_</i>.
 * These methods are automatically redirected to the base Entity class.<br>
 * <br>
 * All callback classes must have a constructor that accepts a single CommonEntity instance.
 */
public class NMSEntityClassBuilder {
	private static final String SUPER_PREFIX = "super_";
	private static final Class<?>[] DEFAULT_CONSTRUCTOR_TYPES = {WorldRef.TEMPLATE.getType()};
	private static final Object[] DEFAULT_CONSTRUCTOR_ARGS = {null};
	private final Enhancer enhancer = new Enhancer();
	private final Map<Signature, CallbackBase> callbacks = new HashMap<Signature, CallbackBase>();
	private final List<CallbackClass> callbackClasses = new ArrayList<CallbackClass>();
	private final Map<Class<?>, Object> callbackInstancesBuffer = new HashMap<Class<?>, Object>();

	public NMSEntityClassBuilder(Class<?> superclass, Collection<Class<?>> callbackClasses) {
		try {
			// Obtain all available interfaces and callbacks from the callback classes
			Collection<Class<?>> interfaceClasses = new ArrayList<Class<?>>(callbackClasses.size());
			for (Class<?> callbackClass : callbackClasses) {
				for (Class<?> interfaceClass : callbackClass.getInterfaces()) {
					interfaceClasses.add(interfaceClass);

					// Set the initial Callbacks - this is important during the CallbackFilter accept phase
					Signature sig;
					for (Method method : interfaceClass.getDeclaredMethods()) {
						sig = getSig(method);
						callbacks.put(sig, null);
						if (sig.getName().startsWith(SUPER_PREFIX)) {
							sig = new Signature(sig.getName().substring(SUPER_PREFIX.length()), sig.getReturnType(), sig.getArgumentTypes());
							callbacks.put(sig, null);
						}
					}
				}
				this.callbackClasses.add(new CallbackClass(callbackClass));
			}

			// Initialize the base Class
			enhancer.setSuperclass(superclass);
			enhancer.setInterfaces(interfaceClasses.toArray(new Class<?>[0]));
			enhancer.setClassLoader(getClass().getClassLoader());
			enhancer.setCallbackTypes(new Class<?>[] {CallbackMethodInterceptor.class, NoOp.class});
			enhancer.setCallbacks(new Callback[] {new CallbackMethodInterceptor(), NoOp.INSTANCE});
			enhancer.setCallbackFilter(new CallbackFilter() {
				@Override
				public int accept(Method method) {
					return callbacks.containsKey(getSig(method)) ? 0 : 1;
				}
			});

			Class<?> type = enhancer.create(DEFAULT_CONSTRUCTOR_TYPES, DEFAULT_CONSTRUCTOR_ARGS).getClass();

			// Generate callback instances
			CallbackBase callback;
			for (Class<?> interfaceClass : interfaceClasses) {
				for (Method method : interfaceClass.getDeclaredMethods()) {
					// Generate method signature
					final Signature sig = getSig(method);
					callback = null;

					// Find the best suitable way of redirecting this Method
					final String name = sig.getName();
					if (name.startsWith(SUPER_PREFIX)) {
						// Automatically redirect to the super method
						final Signature superSig = new Signature(name.substring(SUPER_PREFIX.length()), sig.getReturnType(), sig.getArgumentTypes());
						try {
							callback = new SuperCallback(MethodProxy.find(type, superSig));
						} catch (IllegalArgumentException ex) {
							throw new RuntimeException("Could not access super method: " + superSig, ex);
						}
					} else {
						// Find this method in one of the callback classes
						for (CallbackClass callBackClass : this.callbackClasses) {
							if (interfaceClass.isAssignableFrom(callBackClass.type)) {
								callback = new ProxyCallback(callBackClass.type.getDeclaredMethod(method.getName(), method.getParameterTypes()));
								break;
							}
						}
					}
					// Put the callback instance if available
					if (callback == null) {
						throw new RuntimeException("Interface method is not implemented: " + sig);
					}
					this.callbacks.put(sig, callback);
				}
			}
		} catch (Throwable t) {
			throw new RuntimeException("Could not initialize Entity Class Builder for '" + superclass.getSimpleName() + "':", t);
		}
	}

	private static Signature getSig(Method method) {
		return new Signature(method.getName(), Type.getReturnType(method), Type.getArgumentTypes(method));
	}

	/**
	 * Creates a new Entity instance
	 * 
	 * @return new Entiy instance
	 */
	public synchronized Object create(CommonEntity<?> entity) {
		try {
			// Create a method interceptor, Entity handle and CommonEntity instance
			// Any methods being called inside the Entity constructor are NOT redirected
			final CallbackMethodInterceptor interceptor = new CallbackMethodInterceptor();
			enhancer.setCallbacks(new Callback[] {interceptor, NoOp.INSTANCE});
			final Object entityHandle = enhancer.create(DEFAULT_CONSTRUCTOR_TYPES, DEFAULT_CONSTRUCTOR_ARGS);

			// Set up callback Class instances
			for (CallbackClass callback : this.callbackClasses) {
				try {
					callbackInstancesBuffer.put(callback.type, callback.constructor.newInstance(entity));
				} catch (IllegalArgumentException e) {
					throw new RuntimeException("Callback Class has an unexpected constructor signature:", e);
				} catch (InstantiationException e) {
					throw new RuntimeException("An error occurred while constructing callback class:", e);
				} catch (Throwable t) {
					throw new RuntimeException("Unexpected error constructor callback class:", t);
				}
			}

			// Set up callback instances
			for (Entry<Signature, CallbackBase> callback : callbacks.entrySet()) {
				interceptor.callbacks.put(callback.getKey(), callback.getValue().newInstance(callbackInstancesBuffer));
			}
			return entityHandle;
		} finally {
			callbackInstancesBuffer.clear();
		}
	}

	private static class CallbackClass {
		public final Class<?> type;
		public final Constructor<?> constructor;

		public CallbackClass(Class<?> type) {
			this.type = type;
			try {
				this.constructor = type.getConstructor(CommonEntity.class);
			} catch (Throwable t) {
				throw new RuntimeException("Callback class '" + type.getName() + "' is invalid: No one-argument 'CommonEntity' constructor");
			}
		}
	}

	private static class CallbackMethodInterceptor implements MethodInterceptor {
		public final Map<Signature, CallbackBase> callbacks = new HashMap<Signature, CallbackBase>();

		@Override
		public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			CallbackBase callback = this.callbacks.get(proxy.getSignature());
			if (callback == null) {
				return proxy.invokeSuper(instance, args);
			} else {
				return callback.invoke(instance, args);
			}
		}
	}

	private static interface CallbackBase {
		public Object invoke(Object instance, Object[] args) throws Throwable;
		public CallbackBase newInstance(Map<Class<?>, Object> callbackInstances);
	}

	private static class SuperCallback implements CallbackBase {
		public final MethodProxy superMethodProxy;

		public SuperCallback(MethodProxy superMethodProxy) {
			this.superMethodProxy = superMethodProxy;
		}

		@Override
		public Object invoke(Object instance, Object[] args) throws Throwable {
			return superMethodProxy.invokeSuper(instance, args);
		}

		@Override
		public CallbackBase newInstance(Map<Class<?>, Object> callbackInstances) {
			return this;
		}
	}

	private static class ProxyCallback implements CallbackBase {
		public final Method callbackMethod;
		public final Object callbackInstance;

		public ProxyCallback(Method callbackMethod) {
			this(null, callbackMethod);
		}

		public ProxyCallback(Object callbackInstance, Method callbackMethod) {
			this.callbackInstance = callbackInstance;
			this.callbackMethod = callbackMethod;
		}

		@Override
		public Object invoke(Object instance, Object[] args) throws Throwable {
			return callbackMethod.invoke(callbackInstance, args);
		}

		@Override
		public CallbackBase newInstance(Map<Class<?>, Object> callbackInstances) {
			final Class<?> declaringClass = callbackMethod.getDeclaringClass();
			final Object instance = callbackInstances.get(declaringClass);
			if (instance == null) {
				throw new RuntimeException("No callback instance found for Class '" + declaringClass.getName() + "'!");
			}
			return new ProxyCallback(instance, callbackMethod);
		}
	}
}
