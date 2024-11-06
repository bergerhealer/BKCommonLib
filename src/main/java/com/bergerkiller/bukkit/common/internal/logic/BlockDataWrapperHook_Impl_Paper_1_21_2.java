package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.NullInstantiator;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;

/**
 * Hooks the "propertiesCodec" Codec field to store our own BlockData.
 * We cannot use the mapping, as on Paper they optimized this out to use a shared map instance.
 * Hooking this would cause chaos. Unfortunately this means we must use reflection every
 * time to access it, and this overhead cannot be eliminated.
 */
class BlockDataWrapperHook_Impl_Paper_1_21_2 extends BlockDataWrapperHook {
    private final FastField<Object> propertiesCodec = new FastField<>();
    private NullInstantiator<Object> hookBuilder = null;
    private final FastField<Object> hookBaseField = new FastField<>();
    private final FastField<BlockData> hookBlockDataField = new FastField<>();

    @Override
    protected void baseEnable() throws Throwable {
        final Class<?> mapCodecType;
        Field propertiesCodecField;

        // MapCodec type instance (from mojang serialization)
        mapCodecType = getClassVerify("com.mojang.serialization.MapCodec");
        Class<?> iBlockDataHolderType = getClassVerify("net.minecraft.world.level.block.state.IBlockDataHolder");
        propertiesCodecField = Resolver.resolveAndGetDeclaredField(iBlockDataHolderType, "propertiesCodec");

        // Verify that the values field is of type MapCodec and not something else
        if (propertiesCodecField.getType() != mapCodecType) {
            throw new UnsupportedOperationException("Values field is of type " + propertiesCodecField.getType() +
                    ", expected " + mapCodecType);
        }

        propertiesCodec.init(propertiesCodecField);
        propertiesCodec.forceInitialization();

        // Generate a new class that implements ImmutableMap and implements our Accessor class
        final ExtendedClassWriter<Object> cw = ExtendedClassWriter.builder(mapCodecType)
                .addInterface(Accessor.class)
                .setFlags(ClassWriter.COMPUTE_MAXS)
                .setClassLoader(BlockDataWrapperHook_Impl_Paper_1_21_2.class.getClassLoader())
                .build();
        final String blockDataDesc = MPLType.getDescriptor(BlockData.class);
        final String immutableMapDesc = MPLType.getDescriptor(mapCodecType);

        // Add the fields holding the original value and the BlockData to be stored
        {
            FieldVisitor fv = cw.visitField(ACC_PRIVATE | ACC_FINAL, "base", immutableMapDesc, null, null);
            fv.visitEnd();
        }
        {
            FieldVisitor fv = cw.visitField(ACC_PRIVATE | ACC_FINAL, "blockData", blockDataDesc, null, null);
            fv.visitEnd();
        }

        // Implement the Accessor interface
        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "bkcGetOriginalValue", "()Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "base", immutableMapDesc);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "bkcGetBlockData", "()" + blockDataDesc, null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "blockData", blockDataDesc);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        // Implement all methods of the immutable map and call it on the proxy field, instead
        ReflectionUtil.getAllMethods(mapCodecType)
                .filter(m -> {
                    // This causes problems...
                    if (m.getName().equals("finalize") && m.getParameterCount() == 0) {
                        return false;
                    }
                    if (m.getName().equals("clone") && m.getParameterCount() == 0) {
                        return false;
                    }

                    int modifiers = m.getModifiers();
                    return !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers) && !Modifier.isPrivate(modifiers);
                })
                .forEachOrdered(m -> {
                    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, MPLType.getName(m), MPLType.getMethodDescriptor(m), null, null);
                    mv.visitCode();
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "base", immutableMapDesc);
                    int registerInitial = 1;
                    for (Class<?> param : m.getParameterTypes()) {
                        registerInitial = MPLType.visitVarILoad(mv, registerInitial, param);
                    }
                    ExtendedClassWriter.visitInvoke(mv, mapCodecType, m);
                    mv.visitInsn(MPLType.getReturnType(m).getOpcode(IRETURN));
                    mv.visitMaxs(0, 0); // Computed! TODO: Maybe don't?
                    mv.visitEnd();
                });

        Class<?> type = cw.generate();
        hookBuilder = NullInstantiator.of(type);
        hookBaseField.init(type.getDeclaredField("base"));
        hookBlockDataField.init(type.getDeclaredField("blockData"));
    }

    private Class<?> getClassVerify(String name) {
        Class<?> type = CommonUtil.getClass(name);
        if (type == null) {
            throw new UnsupportedOperationException("Class not found: " + name);
        }
        return type;
    }

    @Override
    public Object getAccessor(Object nmsIBlockData) {
        return propertiesCodec.get(nmsIBlockData);
    }

    @Override
    protected void setAccessor(Object nmsIBlockdata, Object accessor) {
        propertiesCodec.set(nmsIBlockdata, accessor);
    }

    @Override
    protected Object hook(Object accessor, BlockData blockData) {
        Object hook = hookBuilder.create();
        hookBaseField.set(hook, accessor);
        hookBlockDataField.set(hook, blockData);
        return hook;
    }
}
