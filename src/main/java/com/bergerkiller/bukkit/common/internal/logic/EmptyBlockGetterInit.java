package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import org.objectweb.asm.FieldVisitor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

import static org.objectweb.asm.Opcodes.*;

/**
 * Maintains a EmptyBlockGetter instance which re-implements the EmptyBlockGetter added in 1.14
 * for older versions of the server. This is used to retrieve block data properties without world access
 * for blocks that do not have a dynamic shape.
 */
public class EmptyBlockGetterInit {
    public static final String CLASS_NAME = "com.bergerkiller.bukkit.common.internal.logic.EmptyBlockGetter";
    public static final String FIELD_CODE = CLASS_NAME + ".INSTANCE";
    public static volatile boolean is_initialized = false;

    /**
     * Makes sure to initialize the EmptyBlockGetter.
     * A new class is generated and loaded with a static field storing the instance,
     * with the correct type. Inside generated code, it can be accessed
     * the following way:<br>
     * <br>
     * <pre>com.bergerkiller.bukkit.common.internal.logic.EmptyBlockGetter.INSTANCE</pre>
     */
    public static synchronized void initialize() {
        if (is_initialized) {
            return;
        } else {
            is_initialized = true;
        }

        // Only used on 1.13 and not needed after 1.14
        if (CommonBootstrap.evaluateMCVersion("<", "1.13") || CommonBootstrap.evaluateMCVersion(">", "1.14")) {
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
            Class<?> blockGetterType = Resolver.loadClass("net.minecraft.world.level.BlockGetter", false);
            if (blockGetterType == null) {
                throw new IllegalStateException("BlockGetter class not found in server");
            }

            final ExtendedClassWriter<Object> cw = ExtendedClassWriter.builder(blockGetterType).setExactName(CLASS_NAME).build();

            // Add a static INSTANCE field
            {
                FieldVisitor fv;
                fv = cw.visitField(ACC_PUBLIC | ACC_STATIC, "INSTANCE",
                        cw.getTypeDescriptor(), null, null);
                fv.visitEnd();
            }

            // Implement all methods that aren't already implemented with a default implementation
            ReflectionUtil.getAllMethods(blockGetterType)
                .filter(m -> !m.isDefault())
                .filter(m -> {
                    int modifiers = m.getModifiers();
                    return !Modifier.isStatic(modifiers)
                            && !Modifier.isPrivate(modifiers)
                            && !Modifier.isFinal(modifiers);
                })
                .forEach(m -> {
                    cw.visitMethodReturnConstant(m, BoxedType.getDefaultValue(m.getReturnType()));
                });

            // Instantiate and assign to the INSTANCE field
            Object instance = cw.generateInstanceNull();
            Field f = instance.getClass().getDeclaredField("INSTANCE");
            f.set(null, instance);
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize empty block getter", t);
        }
    }
}
