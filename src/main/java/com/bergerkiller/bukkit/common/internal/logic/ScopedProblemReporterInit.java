package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.logging.Level;

import static org.objectweb.asm.Opcodes.*;

/**
 * Initializes the <code>com.bergerkiller.bukkit.common.internal.logic.ScopedProblemReporter</code>
 * which is made available inside templates as a <b>scoped</b> problem reporter. Calling
 * close() on it will log any problems to (BKCommonLib's) logger.
 */
public class ScopedProblemReporterInit {
    public static final String CLASS_NAME = "com.bergerkiller.bukkit.common.internal.logic.ScopedProblemReporter";
    public static volatile boolean is_initialized = false;

    /**
     * Makes sure to initialize the ScopedProblemReporter as a class, making it available on
     * BKCommonLib's class path.
     */
    public static synchronized void initialize() {
        if (is_initialized) {
            return;
        } else {
            is_initialized = true;
        }

        // Only used on 1.21.6 and later
        if (CommonBootstrap.evaluateMCVersion("<", "1.21.6")) {
            return;
        }

        // If class already exists, the library was probably reloaded. The generated class doesn't touch
        // any of this library's code, so it's fine to keep using that one.
        try {
            Resolver.getClassByExactName(CLASS_NAME);
            return;
        } catch (ClassNotFoundException ex) { /* expected */ }

        try {
            // Find the ProblemReporter type
            Class<?> problemReporterBaseType = Resolver.loadClass("net.minecraft.util.ProblemReporter$a", false);

            final ExtendedClassWriter<Object> cw = ExtendedClassWriter.builder(problemReporterBaseType)
                    .addInterface(AutoCloseable.class)
                    .setExactName(CLASS_NAME)
                    .build();

            // Add a private final logger field
            {
                FieldVisitor fv = cw.visitField(ACC_PRIVATE | ACC_FINAL, "logger", "Ljava/util/logging/Logger;", null, null);
                fv.visitEnd();
            }

            // Add a constructor that calls super() and initializes this field
            {
                MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/util/logging/Logger;)V", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, MPLType.getInternalName(problemReporterBaseType), "<init>", "()V", false);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(PUTFIELD, cw.getInternalName(), "logger", "Ljava/util/logging/Logger;");
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
            }

            // We need these obfuscated method names to work properly
            String isEmptyMethodName = Resolver.resolveMethodName(problemReporterBaseType, "isEmpty", new Class[0]);
            String getTreeReportMethodName = Resolver.resolveMethodName(problemReporterBaseType, "getTreeReport", new Class[0]);

            // Implement AutoCloseable close() that logs getTreeReport() to the logger
            {
                MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "close", "()V", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, cw.getInternalName(), isEmptyMethodName, "()Z", false);
                Label label0 = new Label();
                mv.visitJumpInsn(IFNE, label0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "logger", "Ljava/util/logging/Logger;");
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                mv.visitLdcInsn("Serialization errors\n: ");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, cw.getInternalName(), getTreeReportMethodName, "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/logging/Logger", "warning", "(Ljava/lang/String;)V", false);
                mv.visitLabel(label0);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                mv.visitInsn(RETURN);
                mv.visitMaxs(3, 1);
                mv.visitEnd();
            }

            cw.generate();
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize scoped problem reporter", t);
        }
    }
}
