package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.NullInstantiator;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.ClassFieldCopier;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/**
 * Hooks the 'mapColor' field of the BlockState (BlockBehaviour.BlockStateBase) class to store
 * our own BlockData instance. Used on 26.1 and later.
 */
class BlockDataWrapperHook_Impl_26_1 extends BlockDataWrapperHook {
    private final FastField<Object> mapColor = new FastField<>();
    private ClassFieldCopier<Object> mapColorFieldCopier = null;
    private NullInstantiator<Object> hookBuilder = null;
    private final FastField<Object> hookBaseField = new FastField<>();
    private final FastField<BlockData> hookBlockDataField = new FastField<>();

    @Override
    protected void baseEnable() throws Throwable {
        final Class<?> blockStateBaseType = getClassVerify("net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase");
        final Class<?> mapColorType = getClassVerify("net.minecraft.world.level.material.MapColor");

        mapColor.init(Resolver.resolveAndGetDeclaredField(blockStateBaseType, "mapColor"));
        mapColor.forceInitialization();

        // Generate a new class that extends and hooks MapColor, adding a field to store the BlockData
        final ExtendedClassWriter<Object> cw = ExtendedClassWriter.builder(mapColorType)
                .addInterface(Accessor.class)
                .setFlags(ClassWriter.COMPUTE_MAXS)
                .setClassLoader(BlockDataWrapperHook_Impl_26_1.class.getClassLoader())
                .build();
        final String blockDataDesc = MPLType.getDescriptor(BlockData.class);
        final String mapColorDesc = MPLType.getDescriptor(mapColorType);

        // Add the fields holding the original value and the BlockData to be stored
        {
            FieldVisitor fv = cw.visitField(ACC_PRIVATE | ACC_FINAL, "base", mapColorDesc, null, null);
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
            mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "base", mapColorDesc);
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

        Class<?> type = cw.generate();

        hookBuilder = NullInstantiator.of(type);
        mapColorFieldCopier = ClassFieldCopier.of(LogicUtil.unsafeCast(mapColorType));
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
        return mapColor.get(nmsIBlockData);
    }

    @Override
    protected void setAccessor(Object nmsIBlockdata, Object accessor) {
        mapColor.set(nmsIBlockdata, accessor);
    }

    @Override
    protected Object hook(Object accessor, BlockData blockData) {
        Object hook = hookBuilder.create();
        mapColorFieldCopier.copy(accessor, hook);
        hookBaseField.set(hook, accessor);
        hookBlockDataField.set(hook, blockData);
        return hook;
    }
}