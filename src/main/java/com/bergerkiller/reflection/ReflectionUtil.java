package com.bergerkiller.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {
    /// removes generics from a field/method declaration
    /// example: Map<String, String> stuff -> Map stuff
    public static String filterGenerics(String input) {
        int genEnd = input.indexOf('>');
        if (genEnd == -1) {
            return input;
        }
        int genStart = input.lastIndexOf('<', genEnd);
        if (genStart == -1) {
            return input;
        }
        return filterGenerics(input.substring(0, genStart) + input.substring(genEnd + 1));
    }

    /// parses method/field modifier lists
    public static int parseModifiers(String[] parts, int count) {
        // Read modifiers
        int modifiers = 0;
        for (int i = 0; i < count; i++) {
            switch (parts[i]) {
            case "public":
                modifiers |= Modifier.PUBLIC; break;
            case "private":
                modifiers |= Modifier.PRIVATE; break;
            case "protected":
                modifiers |= Modifier.PROTECTED; break;
            case "final":
                modifiers |= Modifier.FINAL; break;
            case "static":
                modifiers |= Modifier.STATIC; break;
            case "volatile":
                modifiers |= Modifier.VOLATILE; break;
            case "abstract":
                modifiers |= Modifier.ABSTRACT; break;
            }
        }
        return modifiers;
    }

    public static boolean compareModifiers(int m1, int m2) {
        return (Modifier.isPrivate(m1) == Modifier.isPrivate(m2) &&
                Modifier.isPublic(m1) == Modifier.isPublic(m2) &&
                Modifier.isProtected(m1) == Modifier.isProtected(m2) &&
                Modifier.isStatic(m1) == Modifier.isStatic(m2) &&
                Modifier.isFinal(m1) == Modifier.isFinal(m2));
    }

    public static List<SafeField<?>> fillFields(List<SafeField<?>> fields, Class<?> clazz) {
        if (clazz == null) {
            return fields;
        }
        Field[] declared = clazz.getDeclaredFields();
        ArrayList<SafeField<?>> newFields = new ArrayList<SafeField<?>>(declared.length);
        for (Field field : declared) {
            if (!Modifier.isStatic(field.getModifiers())) {
                newFields.add(new SafeField<Object>(field));
            }
        }
        fields.addAll(0, newFields);
        return fillFields(fields, clazz.getSuperclass());
    }
}
