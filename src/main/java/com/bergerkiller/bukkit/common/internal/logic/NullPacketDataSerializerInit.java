package com.bergerkiller.bukkit.common.internal.logic;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import org.objectweb.asm.FieldVisitor;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import org.objectweb.asm.MethodVisitor;

/**
 * Maintains a PacketDataSerializer instance which reads indefinitely, and always
 * returns the default value type for all methods.
 */
public class NullPacketDataSerializerInit {
    public static final String CLASS_NAME = "com.bergerkiller.bukkit.common.internal.logic.NullPacketDataSerializer";
    public static final String FIELD_CODE = CLASS_NAME + ".INSTANCE";
    public static volatile boolean is_initialized = false;

    /**
     * Makes sure to initialize the NullPacketDataSerializer.
     * A new class is generated and loaded with a static field storing the instance,
     * with the correct type. Inside generated code, it can be accessed
     * the following way:<br>
     * <br>
     * <pre>com.bergerkiller.bukkit.common.internal.logic.NullPacketDataSerializer.INSTANCE</pre>
     */
    public static synchronized void initialize() {
        if (is_initialized) {
            return;
        } else {
            is_initialized = true;
        }

        // Only used on 1.17 and later
        if (CommonBootstrap.evaluateMCVersion("<", "1.17")) {
            return;
        }

        // If class already exists, the library was probably reloaded. The generated class doesn't touch
        // any of this library's code, so it's fine to keep using that one.
        try {
            Resolver.getClassByExactName(CLASS_NAME);
            return;
        } catch (ClassNotFoundException ex) { /* expected */ }

        try {
            // Implement 1.20.5 RegistryFriendlyByteBuf if it exists
            Class<?> dataSerializerType = Resolver.loadClass("net.minecraft.network.RegistryFriendlyByteBuf", false);
            if (dataSerializerType == null) {
                dataSerializerType = Resolver.loadClass("net.minecraft.network.PacketDataSerializer", false);
            }
            if (dataSerializerType == null) {
                throw new IllegalStateException("PacketDataSerializer class not found in server");
            }

            final ExtendedClassWriter<Object> cw = ExtendedClassWriter.builder(dataSerializerType).setExactName(CLASS_NAME).build();

            // Add a static INSTANCE field
            {
                FieldVisitor fv;
                fv = cw.visitField(ACC_PUBLIC | ACC_STATIC, "INSTANCE",
                        cw.getTypeDescriptor(), null, null);
                fv.visitEnd();
            }

            // On 1.20.5+ it also may have a method returning the custom registry, where we return the server one
            final Class<?> customRegistryType;
            if (CommonBootstrap.evaluateMCVersion(">=", "1.20.5")) {
                customRegistryType = CommonUtil.getClass("net.minecraft.core.IRegistryCustom");
            } else {
                customRegistryType = null;
            }

            // Override all non-final non-private member methods of PacketDataSerializer
            ReflectionUtil.getAllMethods(dataSerializerType)
                .filter(m -> {
                    int modifiers = m.getModifiers();
                    return !Modifier.isStatic(modifiers)
                            && !Modifier.isPrivate(modifiers)
                            && !Modifier.isFinal(modifiers);
                })
                .forEach(m -> {
                    if (m.getReturnType().equals(customRegistryType)) {
                        MethodVisitor mv = cw.visitMethod(1, MPLType.getName(m), MPLType.getMethodDescriptor(m), (String)null, (String[])null);
                        mv.visitCode();

                        if (CommonBootstrap.evaluateMCVersion(">=", "1.21.5")) {
                            Class<?> craftRegistryClass = CommonUtil.getClass("org.bukkit.craftbukkit.CraftRegistry", false);
                            String methodName = Resolver.resolveMethodName(craftRegistryClass, "getMinecraftRegistry", new Class<?>[0]);
                            mv.visitMethodInsn(INVOKESTATIC, MPLType.getInternalName(craftRegistryClass), methodName,
                                    "()" + MPLType.getDescriptor(customRegistryType), false);
                        } else {
                            Class<?> minecraftServerClass = CommonUtil.getClass("net.minecraft.server.MinecraftServer");
                            String methodName = Resolver.resolveMethodName(minecraftServerClass, "getDefaultRegistryAccess", new Class<?>[0]);
                            mv.visitMethodInsn(INVOKESTATIC, MPLType.getInternalName(minecraftServerClass), methodName,
                                    "()" + MPLType.getDescriptor(customRegistryType), false);
                        }

                        mv.visitInsn(ARETURN);
                        mv.visitMaxs(MPLType.getType(customRegistryType).getSize(), 1);
                        mv.visitEnd();
                    } else {
                        cw.visitMethodReturnConstant(m, BoxedType.getDefaultValue(m.getReturnType()));
                    }
                });

            // Instantiate and assign to the INSTANCE field
            Object instance = cw.generateInstanceNull();
            Field f = instance.getClass().getDeclaredField("INSTANCE");
            f.set(null, instance);
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize null packet data serializer", t);
        }
    }
}
