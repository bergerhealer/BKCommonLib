package com.bergerkiller.bukkit.common.cloud.audiences;

import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastConstructor;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import net.kyori.adventure.audience.Audience;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/**
 * Because we shade in the Adventure library at a path different than Paper, we cannot
 * make use of Paper's own Audience implementation. This class generates an adapter
 * class that calls into Paper's Audience methods from (remapped) our own.<br>
 * <br>
 * To avoid making this a nightmare to maintain, it generates most of the adapter code.
 */
public class PaperAudienceAdapterFactoryOld {
    private final FastConstructor<Audience> audienceAdapterCtor = new FastConstructor<>();

    public PaperAudienceAdapterFactoryOld() throws Throwable {


    }

    /*
     *     <li>net.kyori.adventure.key.Key</li>
     *     <li>net.kyori.adventure.text.ComponentLike</li> Don't implement, use default implementation
     *     <li>net.kyori.adventure.text.Component</li> Implement, with GSON String as intermediate
     *     <li>net.kyori.adventure.audience.MessageType</li> Implement, just CHAT or SYSTEM
     *     <li>net.kyori.adventure.identity.Identified</li> Don't implement, use default implementation
     *     <li>net.kyori.adventure.identity.Identity</li> Implement, just has UUID field
     *     <li>net.kyori.adventure.chat.ChatType.Bound</li> Implement, and ChatType too
     *     <li>net.kyori.adventure.chat.SignedMessage</li> Implement, has simple types (+Component)
     *     <li>net.kyori.adventure.chat.SignedMessage.Signature</li> Implement, is just byte[]
     *     <li>net.kyori.adventure.title.Title</li> Implement, has Times (Duration) and some components
     *     <li>net.kyori.adventure.title.TitlePart</li> Implements, it's like an enum
     */

    private static Class<? extends Audience> createAudienceAdapterClass(Class<?> paperAudienceType) {
        ExtendedClassWriter<? extends Audience> cw = ExtendedClassWriter.builder(Audience.class)
                .setFlags(ClassWriter.COMPUTE_MAXS)
                .setAccess(ACC_FINAL)
                .setExactName("com.bergerkiller.bukkit.common.cloud.audiences.PaperAudienceAdapterFactoryOld$AudienceAdapter")
                .build();

        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;

        // Add field storing the Paper audience implementation type
        {
            fieldVisitor = cw.visitField(ACC_PRIVATE | ACC_FINAL, "aud",
                    MPLType.getDescriptor(paperAudienceType), null, null);
            fieldVisitor.visitEnd();
        }

        // Generate a constructor that assigns this Paper audience field
        {
            methodVisitor = cw.visitMethod(ACC_PUBLIC, "<init>", "(" + MPLType.getDescriptor(paperAudienceType) + ")V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitFieldInsn(PUTFIELD, cw.getInternalName(), "aud", MPLType.getDescriptor(paperAudienceType));
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }

        // Go by all methods that exist in the Audience class, and implement them by calling
        // the same method on the 'aud' field. If the method is not available, and the method
        // is abstract (no default implementation), implements it to throw an UnsupportedOperationException



        {
            methodVisitor = cw.visitMethod(ACC_PUBLIC, "doThing", "()V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitLdcInsn("Not Supported");
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/String;)V", false);
            methodVisitor.visitInsn(ATHROW);
            methodVisitor.visitMaxs(3, 1);
            methodVisitor.visitEnd();
        }
        cw.visitEnd();

        //return cw.toByteArray();
        return null;
    }

    /**
     * Type Adapter. These implementations exist:
     * <ul>
     *     <li>net.kyori.adventure.key.Key</li>
     *     <li>net.kyori.adventure.text.ComponentLike</li>
     *     <li>net.kyori.adventure.text.Component</li>
     *     <li>net.kyori.adventure.audience.MessageType</li>
     *     <li>net.kyori.adventure.identity.Identified</li>
     *     <li>net.kyori.adventure.identity.Identity</li>
     *     <li>net.kyori.adventure.chat.ChatType.Bound</li>
     *     <li>net.kyori.adventure.chat.SignedMessage</li>
     *     <li>net.kyori.adventure.chat.SignedMessage.Signature</li>
     *     <li>net.kyori.adventure.title.Title</li>
     *     <li>net.kyori.adventure.title.TitlePart</li>
     * </ul>
     *
     * These are not implemented and these functionalities will not work as a result:
     * <ul>
     *     <li>net.kyori.adventure.bossbar.BossBar</li>
     *     <li>net.kyori.adventure.sound.Sound</li>
     *     <li>net.kyori.adventure.sound.SoundStop</li>
     *     <li>net.kyori.adventure.sound.Sound.Emitter</li>
     *     <li>net.kyori.adventure.inventory.Book.Builder</li>
     *     <li>net.kyori.adventure.inventory.Book</li>
     *     <li>net.kyori.adventure.resource.ResourcePackInfoLike</li>
     *     <li>net.kyori.adventure.resource.ResourcePackRequestLike</li>
     *     <li>net.kyori.adventure.resource.ResourcePackRequest</li>
     *     <li>net.kyori.adventure.dialog.DialogLike</li>
     * </ul>
     *
     * @param <A> Type we know
     * @param <B> Unknown type
     */
    public interface TypeAdapter<A, B> {
        /** Adapts the type we know into the unknown type */
        B adapt(A value);
        /** Restores the unknown type back into a type we know (reverse) */
        A restore(B value);
    }
}
