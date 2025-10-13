package com.bergerkiller.bukkit.common.cloud.audiences;

import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastConstructor;
import com.bergerkiller.mountiplex.reflection.util.UniqueHash;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

/**
 * Generates implementation types for net.kyori.adventure.chat.SignedMessage.
 */
final class SignedMessageFactoryImpl<T> implements PaperAdventureAdapter.SignedMessageFactory<T> {
    private static final UniqueHash generatedClassCtr = new UniqueHash();
    private final FastConstructor<T> ctor;

    public SignedMessageFactoryImpl(Class<?> signatureClass) {
        this.ctor = new FastConstructor<>(generateConstructor(signatureClass));
    }

    @Override
    public T create(
            final java.time.Instant timestamp,
            final long salt,
            final String message,
            final Object /* net.kyori.adventure.chat.SignedMessage$Signature */ signature,
            final Object /* net.kyori.adventure.text.Component */ unsignedContent,
            final Object /* net.kyori.adventure.identity.Identity */ identity
    ) {
        return ctor.newInstanceVA(timestamp, salt, message, signature, unsignedContent, identity);
    }

    private static <T> Constructor<? extends T> generateConstructor(Class<?> signatureClass) {
        ExtendedClassWriter<? extends T> cw = ExtendedClassWriter.builder(signatureClass)
                .setFlags(ClassWriter.COMPUTE_MAXS)
                .setAccess(ACC_FINAL)
                .setExactName(SignedMessageFactoryImpl.class.getName() + "$SignedMessageImpl" + generatedClassCtr.nextHex())
                .build();

        MethodVisitor methodVisitor;

        // We need these types
        Class<?> signedMessageSignatureType = findAdventureType(signatureClass, "chat.SignedMessage$Signature");
        Class<?> componentType = findAdventureType(signatureClass, "text.Component");
        Class<?> identityType = findAdventureType(signatureClass, "identity.Identity");

        // Declare all fields in our type that will also be set as constructor parameters
        List<FieldInfo> fields = Arrays.asList(
                new FieldInfo("timestamp", "timestamp", java.time.Instant.class),
                new FieldInfo("salt", "salt", long.class),
                new FieldInfo("message", "message", String.class),
                new FieldInfo("signature", "signature", signedMessageSignatureType),
                new FieldInfo("unsignedContent", "unsignedContent", componentType),
                new FieldInfo("identity", "identity", identityType)
        );

        // Add fields as private final fields
        for (FieldInfo field : fields) {
            FieldVisitor fieldVisitor = cw.visitField(ACC_PRIVATE | ACC_FINAL, field.name,
                    MPLType.getDescriptor(field.type), null, null);
            fieldVisitor.visitEnd();
        }

        // Generate a constructor that assigns all these fields
        {
            String ctorDesc = "(" + fields.stream().map(f -> MPLType.getDescriptor(f.type)).collect(Collectors.joining()) + ")V";

            methodVisitor = cw.visitMethod(ACC_PUBLIC, "<init>", ctorDesc, null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

            int register = 1;
            for (FieldInfo field : fields) {
                methodVisitor.visitVarInsn(ALOAD, 0);
                register = MPLType.visitVarILoad(methodVisitor, register, field.type);
                methodVisitor.visitFieldInsn(PUTFIELD, cw.getInternalName(), field.name, MPLType.getDescriptor(field.type));
            }

            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }

        // Implement all the getter methods that return our fields
        for (FieldInfo field : fields) {
            methodVisitor = cw.visitMethod(ACC_PUBLIC | ACC_FINAL, field.getterName,
                    "()" + MPLType.getDescriptor(field.type), null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, cw.getInternalName(), field.name, MPLType.getDescriptor(field.type));
            methodVisitor.visitInsn(MPLType.getOpcode(field.type, IRETURN));
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }

        // Generate the class and retrieve its constructor
        return cw.generateConstructor(fields.stream().map(f -> f.type).toArray(Class<?>[]::new));
    }

    private static Class<?> findAdventureType(Class<?> signedMessageType, String adventureTypeName) {
        // Extract package from signedMessageType (net.kyori.adventure.chat.SignedMessage -> net.kyori.adventure)
        String prefix = signedMessageType.getName().substring(0, signedMessageType.getName().length() - 18);
        String fullTypeName = prefix + adventureTypeName;
        try {
            return Class.forName(fullTypeName, false, signedMessageType.getClassLoader());
        } catch (ClassNotFoundException ex) {
            throw new UnsupportedOperationException("Adventure library type not found: " + fullTypeName);
        }
    }

    private static class FieldInfo {
        public final String name;
        public final Class<?> type;
        public final String getterName;

        public FieldInfo(String name, String getterName, Class<?> type) {
            this.name = name;
            this.getterName = getterName;
            this.type = type;
        }
    }
}
