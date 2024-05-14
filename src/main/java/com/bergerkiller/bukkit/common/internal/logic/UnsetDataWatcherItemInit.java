package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

import static org.objectweb.asm.Opcodes.*;

/**
 * Maintains a DataWatcher.Item instance which has an unset value, and attempting to
 * set a new value throws an exception. This exception can be handled to properly
 * initialize the entry afterwards.
 */
public class UnsetDataWatcherItemInit {
    public static final String CLASS_NAME = "com.bergerkiller.bukkit.common.internal.logic.UnsetDataWatcherItem";
    public static final String FIELD_CODE = CLASS_NAME + ".INSTANCE";
    public static volatile boolean is_initialized = false;

    /**
     * Constant value assigned to DataWatcher Items that have no initial value set.
     * As a result, it will always view the value as set to non-default and include
     * it in packets.
     * The actual value is not used in serialization and such.
     */
    public static final Object UNSET_MARKER_VALUE = new Object();

    /**
     * Makes sure to initialize the UnsetDataWatcherItem.
     * A new class is generated and loaded with a static field storing the instance,
     * with the correct type. Inside generated code, it can be accessed
     * the following way:<br>
     * <br>
     * <pre>com.bergerkiller.bukkit.common.internal.logic.UnsetDataWatcherItem.INSTANCE</pre>
     */
    public static synchronized void initialize() {
        if (is_initialized) {
            return;
        } else {
            is_initialized = true;
        }

        // Only used on 1.20.5 and later
        if (CommonBootstrap.evaluateMCVersion("<", "1.20.5")) {
            return;
        }

        // If class already exists, the library was probably reloaded. The generated class doesn't touch
        // any of this library's code, so it's fine to keep using that one.
        try {
            Class.forName(CLASS_NAME);
            return;
        } catch (ClassNotFoundException ex) { /* expected */ }

        try {
            // Implement 1.20.5 RegistryFriendlyByteBuf if it exists
            Class<?> dataWatcherItemType = Resolver.loadClass("net.minecraft.network.syncher.DataWatcher$Item", false);
            if (dataWatcherItemType == null) {
                throw new IllegalStateException("DataWatcher.Item class not found in server");
            }

            Class<?> dataWatcherObjectType = Resolver.loadClass("net.minecraft.network.syncher.DataWatcherObject", false);
            if (dataWatcherObjectType == null) {
                throw new IllegalStateException("DataWatcherObject class not found in server");
            }

            final ExtendedClassWriter<Object> cw = ExtendedClassWriter.builder(dataWatcherItemType).setExactName(CLASS_NAME).build();

            // Add a static INSTANCE field
            {
                FieldVisitor fv;
                fv = cw.visitField(ACC_PUBLIC | ACC_STATIC, "INSTANCE",
                        cw.getTypeDescriptor(), null, null);
                fv.visitEnd();
            }

            // Add an empty constructor that sets the initialValue/value to the UNSET_MARKER_VALUE
            {
                /*
                MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitInsn(ACONST_NULL);
                mv.visitFieldInsn(GETSTATIC, MPLType.getInternalName(DataWatcherHandle.class), "UNSET_MARKER_VALUE", "Ljava/lang/Object;");
                mv.visitMethodInsn(INVOKESPECIAL, MPLType.getInternalName(dataWatcherItemType), "<init>",
                        "(" + MPLType.getDescriptor(dataWatcherObjectType) + "Ljava/lang/Object;)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(3, 3);
                mv.visitEnd();
                */

                MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                        "(" + MPLType.getDescriptor(dataWatcherObjectType) + "Ljava/lang/Object;)V",
                        null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitMethodInsn(INVOKESPECIAL, MPLType.getInternalName(dataWatcherItemType), "<init>",
                        "(" + MPLType.getDescriptor(dataWatcherObjectType) + "Ljava/lang/Object;)V",
                        false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(3, 3);
                mv.visitEnd();
            }

            // Find the setValue() method
            final java.lang.reflect.Method setValueMethod = Resolver.resolveAndGetDeclaredMethod(dataWatcherItemType,
            "setValue", Object.class);

            // Override all non-final non-private member methods of PacketDataSerializer
            ReflectionUtil.getAllMethods(dataWatcherItemType)
                    .filter(m -> {
                        int modifiers = m.getModifiers();
                        return !Modifier.isStatic(modifiers)
                                && !Modifier.isPrivate(modifiers)
                                && !Modifier.isFinal(modifiers);
                    })
                    .forEach(m -> {
                        if (m.equals(setValueMethod)) {
                            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, MPLType.getName(setValueMethod), MPLType.getMethodDescriptor(setValueMethod),
                                    "(TT;)V", null);
                            mv.visitCode();
                            mv.visitFieldInsn(GETSTATIC, MPLType.getInternalName(UnsetDataWatcherItemException.class),
                                    "INSTANCE", MPLType.getDescriptor(UnsetDataWatcherItemException.class));
                            mv.visitInsn(ATHROW);
                            mv.visitMaxs(1, 2);
                            mv.visitEnd();
                        } else {

                        }
                    });

            // Instantiate and assign to the INSTANCE field
            Object instance = cw.generateInstance(new Class<?>[] { dataWatcherObjectType, Object.class },
                                                  new Object[] { null, UNSET_MARKER_VALUE });
            //Object instance = cw.generateInstance();
            Field f = instance.getClass().getDeclaredField("INSTANCE");
            f.set(null, instance);
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize null packet data serializer", t);
        }
    }
}
