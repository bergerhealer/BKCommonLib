package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.generated.net.minecraft.world.item.component.CustomModelDataHandle;

import java.util.List;

/**
 * Wrapper class for the 1.21.4+ CustomModelData component fields.
 * On versions before that, proxies the single 'int' custom_model_data
 * field.
 */
public class CustomModelData extends BasicWrapper<CustomModelDataHandle> {

    public CustomModelData() {
        this.setHandle(CustomModelDataHandle.empty());
    }

    public CustomModelData(CustomModelDataHandle handle) {
        this.setHandle(handle);
    }

    public CustomModelData(List<Float> floats, List<Boolean> flags, List<String> strings, List<Integer> colors) {
        this.setHandle(CustomModelDataHandle.createNew(floats, flags, strings, colors));
    }

    public CustomModelData(int legacyCustomModelDataValue) {
        this.setHandle(CustomModelDataHandle.createNewLegacy(legacyCustomModelDataValue));
    }

    public List<Float> floats() {
        return handle.floats();
    }

    public List<Boolean> flags() {
        return handle.flags();
    }

    public List<String> strings() {
        return handle.strings();
    }

    public List<Integer> colors() {
        return handle.colors();
    }

    public CustomModelData withFloats(List<Float> floats) {
        return new CustomModelData(floats, this.flags(), this.strings(), this.colors());
    }

    public CustomModelData withFlags(List<Boolean> flags) {
        return new CustomModelData(this.floats(), flags, this.strings(), this.colors());
    }

    public CustomModelData withStrings(List<String> strings) {
        return new CustomModelData(this.floats(), this.flags(), strings, this.colors());
    }

    public CustomModelData withColors(List<Integer> colors) {
        return new CustomModelData(this.floats(), this.flags(), this.strings(), colors);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("CustomModelData {\n");
        listToString(str, "floats", floats());
        listToString(str, "flags", flags());
        listToString(str, "strings", strings());
        listToString(str, "colors", colors());
        str.append("}");
        return str.toString();
    }

    @Override
    public int hashCode() {
        return handle.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CustomModelData && handle.equals(((CustomModelData) o).handle);
    }

    private void listToString(StringBuilder str, String title, List<?> list) {
        str.append("  ").append(title).append(": [");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                str.append(", ");
            }
            str.append(list.get(i));
        }
        str.append("]\n");
    }
}
