package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.IBlockDataHandle;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.NullInstantiator;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;

class BlockDataWrapperHook_Impl extends BlockDataWrapperHook {
    private final FastMethod<Object> getValues = new FastMethod<>();
    private final FastField<Object> values = new FastField<>();
    private NullInstantiator<Object> hookBuilder = null;
    private final FastField<Object> hookBaseField = new FastField<>();
    private final FastField<BlockData> hookBlockDataField = new FastField<>();

    @Override
    protected void baseEnable() throws Throwable {
        final Class<?> immutableMapType;
        Field valuesField;

        if (CommonBootstrap.evaluateMCVersion(">=", "1.20.5")) {
            // Reference2ObjectArrayMap after 1.20.5
            immutableMapType = getClassVerify("it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap");
            Class<?> iBlockDataHolderType = getClassVerify("net.minecraft.world.level.block.state.IBlockDataHolder");
            valuesField = Resolver.resolveAndGetDeclaredField(iBlockDataHolderType, "values");
        } else {
            // ImmutableMap before MC 1.20.5
            // Version-specific field names...
            immutableMapType = getClassVerify("com.google.common.collect.ImmutableMap");
            if (CommonBootstrap.evaluateMCVersion(">=", "1.16")) {
                // Since MC 1.16: Field is stored in the IBlockDataHolder class
                Class<?> iBlockDataHolderType = getClassVerify("net.minecraft.world.level.block.state.IBlockDataHolder");
                if (CommonBootstrap.evaluateMCVersion(">=", "1.18")) {
                    valuesField = Resolver.resolveAndGetDeclaredField(iBlockDataHolderType, "values");
                } else if (CommonBootstrap.evaluateMCVersion(">=", "1.17")) {
                    valuesField = Resolver.resolveAndGetDeclaredField(iBlockDataHolderType, "values");
                } else {
                    valuesField = Resolver.resolveAndGetDeclaredField(iBlockDataHolderType, "b");
                }
            } else if (CommonBootstrap.evaluateMCVersion(">=", "1.13")) {
                // Since MC 1.13: Field is stored in the BlockDataAbstract class
                Class<?> blockDataAbstractType = CommonUtil.getClass("net.minecraft.world.level.block.state.BlockDataAbstract");
                if (CommonBootstrap.evaluateMCVersion(">=", "1.14")) {
                    valuesField = Resolver.resolveAndGetDeclaredField(blockDataAbstractType, "d");
                } else {
                    valuesField = Resolver.resolveAndGetDeclaredField(blockDataAbstractType, "c");
                }
            } else {
                // MC 1.8 - 1.12.2: Field is stored in the BlockData class
                Class<?> blockDataType = CommonUtil.getClass("net.minecraft.world.level.block.state.BlockStateList$BlockData");
                if (SafeField.contains(blockDataType, "bAsImmutableMap", immutableMapType)) {
                    // Optimization on TacoSpigot / BurritoSpigot
                    valuesField = blockDataType.getDeclaredField("bAsImmutableMap");
                } else {
                    valuesField = Resolver.resolveAndGetDeclaredField(blockDataType, "b");
                }
            }
        }

        // Verify that the values field is of type ImmutableMap and not something else
        if (valuesField.getType() != immutableMapType) {
            throw new UnsupportedOperationException("Values field is of type " + valuesField.getType() +
                    ", expected " + immutableMapType);
        }

        values.init(valuesField);
        values.forceInitialization();

        // Initialize IBlockData getValues() method, we already refer to it in templates
        Method getValuesMethod = IBlockDataHandle.T.getStates.raw.toJavaMethod();
        getValues.init(getValuesMethod);
        getValues.forceInitialization();

        // Generate a new class that implements ImmutableMap and implements our Accessor class
        final ExtendedClassWriter<Object> cw = ExtendedClassWriter.builder(immutableMapType)
                .addInterface(Accessor.class)
                .setFlags(ClassWriter.COMPUTE_MAXS)
                .setClassLoader(BlockDataWrapperHook_Impl.class.getClassLoader())
                .build();
        final String blockDataDesc = MPLType.getDescriptor(BlockData.class);
        final String immutableMapDesc = MPLType.getDescriptor(immutableMapType);

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
        ReflectionUtil.getAllMethods(immutableMapType)
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
                    ExtendedClassWriter.visitInvoke(mv, immutableMapType, m);
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
        return getValues.invoke(nmsIBlockData);
    }

    @Override
    protected void setAccessor(Object nmsIBlockdata, Object accessor) {
        values.set(nmsIBlockdata, accessor);
    }

    @Override
    protected Object hook(Object accessor, BlockData blockData) {
        Object hook = hookBuilder.create();
        hookBaseField.set(hook, accessor);
        hookBlockDataField.set(hook, blockData);
        return hook;
    }
}
