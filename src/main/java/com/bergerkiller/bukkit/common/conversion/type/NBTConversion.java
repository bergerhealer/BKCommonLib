package com.bergerkiller.bukkit.common.conversion.type;

import java.util.List;
import java.util.Map;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.generated.net.minecraft.nbt.TagHandle;
import com.bergerkiller.generated.net.minecraft.nbt.CompoundTagHandle;
import com.bergerkiller.generated.net.minecraft.nbt.ListTagHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.conversion.util.ConvertingMap;

public class NBTConversion {

    @ConverterMethod(input="java.util.Map<String, net.minecraft.nbt.Tag>")
    public static Map<String, TagHandle> mapValuesToTagHandle(Map<String, Object> map) {
        return new ConvertingMap<String, TagHandle>(map, DuplexConversion.string_string, DuplexConversion.nbtBase_nbtBaseHandle);
    }

    @ConverterMethod(input="java.util.List<net.minecraft.nbt.Tag>")
    public static List<TagHandle> listValuesToTagHandle(List<Object> list) {
        return new ConvertingList<TagHandle>(list, DuplexConversion.nbtBase_nbtBaseHandle);
    }

    @SuppressWarnings("unchecked")
    @ConverterMethod(input="net.minecraft.nbt.Tag")
    public static <T extends CommonTag> T toCommonTag(Object nmsNBTTagHandle) {
        return (T) TagHandle.createHandleForData(nmsNBTTagHandle).toCommonTag();
    }

    @ConverterMethod(input="net.minecraft.nbt.ListTag")
    public static CommonTagList toCommonTagList(Object nmsListTagHandle) {
        return CommonTagList.create(ListTagHandle.createHandle(nmsListTagHandle));
    }

    @ConverterMethod(input="net.minecraft.nbt.CompoundTag")
    public static CommonTagCompound toCommonTagCompound(Object nmsCompoundTagHandle) {
        return CommonTagCompound.create(CompoundTagHandle.createHandle(nmsCompoundTagHandle));
    }

    @ConverterMethod(output="net.minecraft.nbt.CompoundTag")
    public static Object toNBTTagHandle(CommonTagCompound commonTag) {
        return commonTag.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.nbt.ListTag")
    public static Object toNBTTagHandle(CommonTagList commonTag) {
        return commonTag.getRawHandle();
    }

    @ConverterMethod(output="T extends net.minecraft.nbt.Tag")
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
