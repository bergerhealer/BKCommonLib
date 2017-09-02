package com.bergerkiller.bukkit.common.conversion.type;

import java.util.List;
import java.util.Map;

import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;

public class NBTConversion {

    @SuppressWarnings("unchecked")
    @ConverterMethod(input="net.minecraft.server.NBTBase")
    public static <T extends CommonTag> T toCommonTag(Object nmsNBTTagHandle) {
        return (T) CommonTag.create(nmsNBTTagHandle);
    }

    @ConverterMethod(input="net.minecraft.server.NBTTagList")
    public static CommonTagList toCommonTagList(Object nmsNBTTagListHandle) {
        return CommonTagList.create(nmsNBTTagListHandle);
    }

    @ConverterMethod(input="net.minecraft.server.NBTTagCompound")
    public static CommonTagCompound toCommonTagCompound(Object nmsNBTTagCompoundHandle) {
        return CommonTagCompound.create(nmsNBTTagCompoundHandle);
    }

    @ConverterMethod(output="net.minecraft.server.NBTTagCompound")
    public static Object toNBTTagHandle(CommonTagCompound commonTag) {
        return commonTag.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.server.NBTTagList")
    public static Object toNBTTagHandle(CommonTagList commonTag) {
        return commonTag.getRawHandle();
    }

    @ConverterMethod(output="T extends net.minecraft.server.NBTBase")
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
    public static CommonTag createCommonTag(String data) {
        return CommonTag.createForData(data);
    }
}
