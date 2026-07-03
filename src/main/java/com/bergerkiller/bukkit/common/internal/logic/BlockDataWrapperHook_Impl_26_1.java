package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.NullInstantiator;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/**
 * Hooks the 'isSuffocating' StatePredicate field of the BlockState (BlockBehaviour.BlockStateBase) class to store
 * our own BlockData instance. Used on 26.1 and later. We implement the interface instead of extending the
 * original type, as we do not know the (lambda) type at runtime. Instead, simply call the original lambda
 * from our custom one.
 */
class BlockDataWrapperHook_Impl_26_1 extends BlockDataWrapperHook {
    private final FastField<Object> statePredicateField = new FastField<>();
    private NullInstantiator<Object> hookBuilder = null;
    private final FastField<Object> hookBaseField = new FastField<>();
    private final FastField<BlockData> hookBlockDataField = new FastField<>();

    @Override
    protected void baseEnable() throws Throwable {
        final Class<?> blockStateBaseType = getClassVerify("net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase");
        final Class<?> statePredicateType = getClassVerify("net.minecraft.world.level.block.state.BlockBehaviour$StatePredicate");

        // Hook the field that is likely to be called less. All it adds is a field redirection but it's still good to be mindful.
        statePredicateField.init(Resolver.resolveAndGetDeclaredField(blockStateBaseType, "isSuffocating"));
        statePredicateField.forceInitialization();

        // Generate a new class that implements the StatePredicate interface and calls the original method on the original value
        Class<?> type;
        {
            final ExtendedClassWriter<Object> cw = ExtendedClassWriter.builder(statePredicateType)
                    .addInterface(Accessor.class)
                    .setFlags(ClassWriter.COMPUTE_MAXS)
                    .setClassLoader(BlockDataWrapperHook_Impl_26_1.class.getClassLoader())
                    .build();
            final String blockDataDesc = MPLType.getDescriptor(BlockData.class);
            final String statePredicateDesc = MPLType.getDescriptor(statePredicateType);
            final String nmsBlockStateDesc = MPLType.getDescriptor(getClassVerify("net.minecraft.world.level.block.state.BlockState"));
            final String nmsBlockGetterDesc = MPLType.getDescriptor(getClassVerify("net.minecraft.world.level.BlockGetter"));
            final String nmsBlockPosDesc = MPLType.getDescriptor(getClassVerify("net.minecraft.core.BlockPos"));

            // Add the fields holding the original value and the BlockData to be stored
            {
                FieldVisitor fv = cw.visitField(ACC_PRIVATE | ACC_FINAL, "base", statePredicateDesc, null, null);
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
                mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "base", statePredicateDesc);
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

            //boolean test(BlockState state, BlockGetter level, BlockPos pos);
            {
                final String testMethodDesc =  "(" + nmsBlockStateDesc + nmsBlockGetterDesc + nmsBlockPosDesc + ")Z";

                MethodVisitor mv =  cw.visitMethod(ACC_PUBLIC, "test", testMethodDesc, null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "base", statePredicateDesc);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitMethodInsn(INVOKEINTERFACE, MPLType.getInternalName(statePredicateType), "test", testMethodDesc, true);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(4, 4);
                mv.visitEnd();
            }

            type = cw.generate();
        }

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
        return statePredicateField.get(nmsIBlockData);
    }

    @Override
    protected void setAccessor(Object nmsIBlockdata, Object accessor) {
        statePredicateField.set(nmsIBlockdata, accessor);
    }

    @Override
    protected Object hook(Object accessor, BlockData blockData) {
        Object hook = hookBuilder.create();
        hookBaseField.set(hook, accessor);
        hookBlockDataField.set(hook, blockData);
        return hook;
    }
}