package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.world.phys.shapes.VoxelShapeHandle;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.NullInstantiator;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;

/**
 * Hooks the 'occlusionShape' field of the BlockState (BlockBehaviour.BlockStateBase) class to store
 * our own BlockData instance. Used on 26.1 and later.
 */
class BlockDataWrapperHook_Impl_26_1 extends BlockDataWrapperHook {
    private final FastField<Object> occlusionShape = new FastField<>();
    private final FastMethod<Object> getOcclusionShape = new FastMethod<>();
    private NullInstantiator<Object> hookBuilder = null;
    private final FastField<Object> hookBaseField = new FastField<>();
    private final FastField<BlockData> hookBlockDataField = new FastField<>();

    @Override
    protected void baseEnable() throws Throwable {
        final Class<?> blockStateBaseType = getClassVerify("net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase");
        final Class<?> voxelShapeType = VoxelShapeHandle.T.getType();

        occlusionShape.init(Resolver.resolveAndGetDeclaredField(blockStateBaseType, "occlusionShape"));
        occlusionShape.forceInitialization();

        getOcclusionShape.init(Resolver.resolveAndGetDeclaredMethod(blockStateBaseType, "getOcclusionShape"));
        getOcclusionShape.forceInitialization();

        // Generate a new class that extends/implements VoxelShape and implements our Accessor class
        final ExtendedClassWriter<Object> cw = ExtendedClassWriter.builder(voxelShapeType)
                .addInterface(Accessor.class)
                .setFlags(ClassWriter.COMPUTE_MAXS)
                .setClassLoader(BlockDataWrapperHook_Impl_26_1.class.getClassLoader())
                .build();
        final String blockDataDesc = MPLType.getDescriptor(BlockData.class);
        final String voxelShapeDesc = MPLType.getDescriptor(voxelShapeType);

        // Add the fields holding the original value and the BlockData to be stored
        {
            FieldVisitor fv = cw.visitField(ACC_PRIVATE | ACC_FINAL, "base", voxelShapeDesc, null, null);
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
            mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "base", voxelShapeDesc);
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
        ReflectionUtil.getAllMethods(voxelShapeType)
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
                    mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "base", voxelShapeDesc);
                    int registerInitial = 1;
                    for (Class<?> param : m.getParameterTypes()) {
                        registerInitial = MPLType.visitVarILoad(mv, registerInitial, param);
                    }
                    ExtendedClassWriter.visitInvoke(mv, voxelShapeType, m);
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
        return getOcclusionShape.invoke(nmsIBlockData);
    }

    @Override
    protected void setAccessor(Object nmsIBlockdata, Object accessor) {
        occlusionShape.set(nmsIBlockdata, accessor);
    }

    @Override
    protected Object hook(Object accessor, BlockData blockData) {
        Object hook = hookBuilder.create();
        hookBaseField.set(hook, accessor);
        hookBlockDataField.set(hook, blockData);
        return hook;
    }
}