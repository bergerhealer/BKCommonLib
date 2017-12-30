package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.bergerkiller.bukkit.common.internal.proxy.EntitySliceProxy_1_8_3;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntitySliceHandle;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.type.AnnotatedConverter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;

// Tests the EntitySlice <> List conversion that occurs on CraftBukkit
public class CBEntitySliceTest {

    static {
        CommonUtil.bootstrap();
    }

    @Test
    public void testSliceConverter() {
        if (!EntitySliceHandle.T.isAvailable()) {
            return;
        }

        TypeDeclaration typeSlice = TypeDeclaration.createGeneric(EntitySliceHandle.T.getType(), EntityHandle.T.getType());
        TypeDeclaration typeList = TypeDeclaration.createGeneric(List.class, Object.class);
        TypeDeclaration typeSliceArray = TypeDeclaration.createArray(typeSlice);
        TypeDeclaration typeListArray = TypeDeclaration.createArray(typeList);

        if (!(Conversion.find(typeSlice, typeList) instanceof AnnotatedConverter)) {
            System.out.println(Conversion.find(typeSlice, typeList));
            fail("Converter >> from " + typeSlice + " to " + typeList + " is not our annotated converter!");
        }
        if (!(Conversion.find(typeList, typeSlice) instanceof AnnotatedConverter)) {
            System.out.println(Conversion.find(typeList, typeSlice));
            fail("Converter << from " + typeList + " to " + typeSlice + " is not our annotated converter!");
        }

        Object[] slices = LogicUtil.createArray(EntitySliceHandle.T.getType(), 1);
        slices[0] = EntitySliceHandle.createNew(EntityHandle.T.getType()).getRaw();

        Converter<Object, Object> conv = Conversion.find(typeSliceArray, typeListArray);
        Object[] result = (Object[]) conv.convert(slices);

        assertNotNull(result);
        assertEquals(1, result.length);
        assertNotNull(result[0]);
        assertEquals(EntitySliceProxy_1_8_3.class, result[0].getClass());

        Converter<Object, Object> conv2 = Conversion.find(typeListArray, typeSliceArray);
        Object[] original = (Object[]) conv2.convert(result);

        assertNotNull(original);
        assertEquals(1, original.length);
        assertNotNull(original[0]);
        assertEquals(EntitySliceHandle.T.getType(), original[0].getClass());
    }
}
