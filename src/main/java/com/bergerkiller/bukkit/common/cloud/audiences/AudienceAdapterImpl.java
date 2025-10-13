package com.bergerkiller.bukkit.common.cloud.audiences;

import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastConstructor;
import com.bergerkiller.mountiplex.reflection.util.UniqueHash;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import net.kyori.adventure.audience.Audience;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

/**
 * Generates an implementation type of Audience that calls all the methods on
 * another (adapted) Audience implementation. All parameters for these function
 * calls are adapted using {@link PaperAdventureAdapter}.
 */
final class AudienceAdapterImpl implements PaperAdventureAdapter.AudienceAdapter {
    private static final UniqueHash generatedClassCtr = new UniqueHash();
    private final PaperAdventureAdapter adapter;
    private final FastConstructor<Audience> ctor;

    public AudienceAdapterImpl(PaperAdventureAdapter adapter) {
        this.adapter = adapter;

        final Class<?> adaptedAudienceType = adapter.getAdventureClass(
                /* String join to avoid the gradle remapper changing it */
                String.join(".", "net", "kyori", "adventure", "audience", "Audience"));

        ExtendedClassWriter<? extends Audience> cw = ExtendedClassWriter.builder(Audience.class)
                .setFlags(ClassWriter.COMPUTE_MAXS)
                .setAccess(ACC_FINAL)
                .setExactName(AudienceAdapterImpl.class.getName() + "$AudienceImpl" + generatedClassCtr.nextHex())
                .build();

        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;

        // Add field storing the PaperAdventureAdapter
        {
            fieldVisitor = cw.visitField(ACC_PRIVATE | ACC_FINAL, "adapter",
                    MPLType.getDescriptor(PaperAdventureAdapter.class), null, null);
            fieldVisitor.visitEnd();
        }

        // Add field storing the adapted original Audience
        {
            fieldVisitor = cw.visitField(ACC_PRIVATE | ACC_FINAL, "aud",
                    MPLType.getDescriptor(adaptedAudienceType), null, null);
            fieldVisitor.visitEnd();
        }

        // Add constructor assigning both fields
        {
            String ctorDesc = "(" + MPLType.getDescriptor(PaperAdventureAdapter.class) + MPLType.getDescriptor(adaptedAudienceType) + ")V";

            methodVisitor = cw.visitMethod(ACC_PUBLIC, "<init>", ctorDesc, null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitFieldInsn(PUTFIELD, cw.getInternalName(), "adapter", MPLType.getDescriptor(PaperAdventureAdapter.class));

            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitFieldInsn(PUTFIELD, cw.getInternalName(), "aud", MPLType.getDescriptor(adaptedAudienceType));

            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }

        // Implement all methods that feature parameter types that we can adapt
        // If we cannot, leave the default implementation unless it is abstract, then throw a NotSupported exception
        List<Method> methodsToImplement = ReflectionUtil.getAllMethods(Audience.class)
                .filter(m -> !Modifier.isStatic(m.getModifiers()))
                .collect(Collectors.toList());

        for (Method method : methodsToImplement) {
            // Go by all method parameters and the return type and verify that we know how to adapt it
            boolean canAdapt = true;
            List<PaperAdventureAdapter.TypeAdapter> adaptedParamTypes = new ArrayList<>();
            PaperAdventureAdapter.TypeAdapter adaptedReturnType = null;
            for (Class<?> type : method.getParameterTypes()) {
                PaperAdventureAdapter.TypeAdapter paramTypeAdapter = adapter.getTypeAdapter(type);
                if (paramTypeAdapter == null) {
                    canAdapt = false;
                    break;
                } else {
                    adaptedParamTypes.add(paramTypeAdapter);
                }
            }
            if (canAdapt) {
                adaptedReturnType = adapter.getTypeAdapter(method.getReturnType());
                if (adaptedReturnType == null) {
                    canAdapt = false;
                }
            }

            // Find the exact same method name on the other audience implementation, with the same (adapted) args
            Method adaptedMethod = null;
            if (canAdapt) {
                try {
                    Class<?>[] params = adaptedParamTypes.stream()
                            .map(PaperAdventureAdapter.TypeAdapter::getAdaptedType)
                            .toArray(Class<?>[]::new);
                    adaptedMethod = adaptedAudienceType.getMethod(method.getName(), params);
                    if (!adaptedMethod.getReturnType().equals(adaptedReturnType.getAdaptedType())) {
                        canAdapt = false;
                    }
                } catch (NoSuchMethodException e) {
                    canAdapt = false;
                }
            }

            // Fail if we can't implement it
            if (!canAdapt) {
                System.out.println("Cannot implement: " + method);
                if (Modifier.isAbstract(method.getModifiers())) {
                    cw.visitMethodUnsupported(method, "Not implemented by this Paper Audience adapter");
                }
                continue;
            }

            System.out.println("Implementing: " + method);

            // Implement the method
            // Start by reading the adapter and aud field
            methodVisitor = cw.visitMethod(ACC_PUBLIC | ACC_FINAL, MPLType.getName(method), MPLType.getMethodDescriptor(method), null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, cw.getInternalName(), "adapter", MPLType.getDescriptor(PaperAdventureAdapter.class));
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, cw.getInternalName(), "aud", MPLType.getDescriptor(adaptedAudienceType));

            // Next, go by all parameters and process them with the parameter type mapping we got
            int register = 1;
            for (PaperAdventureAdapter.TypeAdapter parameterType : adaptedParamTypes) {
                register = parameterType.applyParameter(cw, methodVisitor, register);
            }

            // Call the same method on the adapted audience
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, MPLType.getInternalName(adaptedAudienceType),
                    method.getName(), MPLType.getMethodDescriptor(adaptedMethod), true);

            // Adapt the return type as well (could emit no instructions if void)
            {
                adaptedReturnType.applyReturn(cw, methodVisitor);
            }

            // Return the value and done
            methodVisitor.visitInsn(MPLType.getOpcode(method.getReturnType(), IRETURN));
            methodVisitor.visitMaxs(6, 5);
            methodVisitor.visitEnd();
        };

        // public MyReturnType method(long l, MyParamType type, int k)

        /*
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "method", "(JLcom/bergerkiller/mountiplex/ASMPlaygroundTest$MyParamType;I)Lcom/bergerkiller/mountiplex/ASMPlaygroundTest$MyReturnType;", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD, "com/bergerkiller/mountiplex/ASMPlaygroundTest$AdaptedAudience", "adapter", "Lcom/bergerkiller/mountiplex/ASMPlaygroundTest$Adapter;");
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD, "com/bergerkiller/mountiplex/ASMPlaygroundTest$AdaptedAudience", "aud", "Lcom/bergerkiller/mountiplex/ASMPlaygroundTest$OtherAudience;");

        methodVisitor.visitVarInsn(LLOAD, 1);

        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD, "com/bergerkiller/mountiplex/ASMPlaygroundTest$AdaptedAudience", "adapter", "Lcom/bergerkiller/mountiplex/ASMPlaygroundTest$Adapter;");
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/bergerkiller/mountiplex/ASMPlaygroundTest$Adapter", "adaptParamType", "(Lcom/bergerkiller/mountiplex/ASMPlaygroundTest$MyParamType;)Ljava/lang/Object;", false);
        methodVisitor.visitTypeInsn(CHECKCAST, "com/bergerkiller/mountiplex/ASMPlaygroundTest$OtherMyParamType");

        methodVisitor.visitVarInsn(ILOAD, 4);

        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "com/bergerkiller/mountiplex/ASMPlaygroundTest$OtherAudience", "method", "(JLcom/bergerkiller/mountiplex/ASMPlaygroundTest$OtherMyParamType;I)Lcom/bergerkiller/mountiplex/ASMPlaygroundTest$OtherMyReturnType;", true);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/bergerkiller/mountiplex/ASMPlaygroundTest$Adapter", "reverseReturnType", "(Ljava/lang/Object;)Lcom/bergerkiller/mountiplex/ASMPlaygroundTest$MyReturnType;", false);
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitMaxs(6, 5);
        methodVisitor.visitEnd();
         */

        // Generate and done
        this.ctor = new FastConstructor<>(cw.generateConstructor(PaperAdventureAdapter.class, adaptedAudienceType));
    }

    @Override
    public Audience reverseAudience(Object audience) {
        return this.ctor.newInstance(this.adapter, audience);
    }
}
