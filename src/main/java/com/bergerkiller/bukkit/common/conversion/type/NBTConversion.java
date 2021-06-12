package com.bergerkiller.bukkit.common.conversion.type;

import java.util.List;
import java.util.Map;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.generated.net.minecraft.nbt.NBTBaseHandle;
import com.bergerkiller.generated.net.minecraft.nbt.NBTTagCompoundHandle;
import com.bergerkiller.generated.net.minecraft.nbt.NBTTagListHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.conversion.util.ConvertingMap;

public class NBTConversion {

    @ConverterMethod(input="java.util.Map<String, net.minecraft.nbt.NBTBase>")
    public static Map<String, NBTBaseHandle> mapValuesToNBTBaseHandle(Map<String, Object> map) {
        return new ConvertingMap<String, NBTBaseHandle>(map, DuplexConversion.string_string, DuplexConversion.nbtBase_nbtBaseHandle);
    }

    @ConverterMethod(input="java.util.List<net.minecraft.nbt.NBTBase>")
    public static List<NBTBaseHandle> listValuesToNBTBaseHandle(List<Object> list) {
        return new ConvertingList<NBTBaseHandle>(list, DuplexConversion.nbtBase_nbtBaseHandle);
    }

    @SuppressWarnings("unchecked")
    @ConverterMethod(input="net.minecraft.nbt.NBTBase")
    public static <T extends CommonTag> T toCommonTag(Object nmsNBTTagHandle) {
        return (T) NBTBaseHandle.createHandleForData(nmsNBTTagHandle).toCommonTag();
    }

    @ConverterMethod(input="net.minecraft.nbt.NBTTagList")
    public static CommonTagList toCommonTagList(Object nmsNBTTagListHandle) {
        return CommonTagList.create(NBTTagListHandle.createHandle(nmsNBTTagListHandle));
    }

    @ConverterMethod(input="net.minecraft.nbt.NBTTagCompound")
    public static CommonTagCompound toCommonTagCompound(Object nmsNBTTagCompoundHandle) {
        return CommonTagCompound.create(NBTTagCompoundHandle.createHandle(nmsNBTTagCompoundHandle));
    }

    @ConverterMethod(output="net.minecraft.nbt.NBTTagCompound")
    public static Object toNBTTagHandle(CommonTagCompound commonTag) {
        return commonTag.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.nbt.NBTTagList")
    public static Object toNBTTagHandle(CommonTagList commonTag) {
        return commonTag.getRawHandle();
    }

    @ConverterMethod(output="T extends net.minecraft.nbt.NBTBase")
    public static Object toNBTTagHandle(CommonTag commonTag) {
        return commonTag.getRawHandle();
    }

    @ConverterMethod(cost = 2)
    public static CommonTagCompound createCommonTag(Map<?, ?> mapData) {
        return (CommonTagCompound) CommonTag.createForData(mapData);
    }

    @ConverterMethod(cost = 2)
    public static CommonTagList createCommonTag(List<?> listData) {
        return (CommonTagList) CommonTag.createForData(listData);
    }

    @ConverterMethod(cost = 2)
    public static CommonTag createCommonTag(Byte data) {
        return CommonTag.createForData(data);
    }

    @ConverterMethod(cost = 2)
    public static CommonTag createCommonTag(Short data) {
        return CommonTag.createForData(data);
    }

    @ConverterMethod(cost = 2)
    public static CommonTag createCommonTag(Integer data) {
        return CommonTag.createForData(data);
    }

    @ConverterMethod(cost = 2)
    public static CommonTag createCommonTag(Long data) {
        return CommonTag.createForData(data);
    }

    @ConverterMethod(cost = 2)
    public static CommonTag createCommonTag(Float data) {
        return CommonTag.createForData(data);
    }

    @ConverterMethod(cost = 2)
    public static CommonTag createCommonTag(Double data) {
        return CommonTag.createForData(data);
    }

    @ConverterMethod(cost = 2)
    public static CommonTag createCommonTag(byte[] data) {
        return CommonTag.createForData(data);
    }

    @ConverterMethod(cost = 2)
    public static CommonTag createCommonTag(int[] data) {
        return CommonTag.createForData(data);
    }

    @ConverterMethod(cost = 2)
    public static CommonTag createCommonTag(long[] data) {
        return CommonTag.createForData(data);
    }

    @ConverterMethod(cost = 2)
    public static CommonTag createCommonTag(String data) {
        return CommonTag.createForData(data);
    }
}
