package com.bergerkiller.reflection.declarations;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.ReflectionUtil;

public class FieldDeclaration {
    public final String name;
    public final Class<?> type;
    public final int modifiers;
    private final String declaration;

    public FieldDeclaration(ClassTemplate<?> template, Field f) {
        this.type = f.getType();
        this.name = f.getName();
        this.modifiers = f.getModifiers();

        String declaration = "";

        declaration += Modifier.toString(f.getModifiers()) + " ";
        declaration += template.resolveClassName(f.getType()) + " ";
        declaration += f.getName() + ";";
        this.declaration = declaration;
    }

    public FieldDeclaration(ClassTemplate<?> template, String declaration) {
        if (declaration.endsWith(";")) {
            declaration = declaration.substring(0, declaration.length() - 1);
        }

        String declarationF = ReflectionUtil.filterGenerics(declaration);

        String[] parts = declarationF.split(" ");

        // Figure out what the field type is from the name
        String fieldName = null;
        Class<?> fieldType = null;
        int fieldModifiers = 0;
        if (parts.length >= 2) {
            fieldName = parts[parts.length - 1];
            fieldType = template.resolveClass(parts[parts.length - 2], true);
            fieldModifiers = ReflectionUtil.parseModifiers(parts, parts.length - 2);
        }

        if (fieldName == null || fieldType == null)
        {
            this.name = null;
            this.type = null;
            this.modifiers = 0;
            this.declaration = null;
        } else {
            this.name = fieldName;
            this.type = fieldType;
            this.modifiers = fieldModifiers;
            this.declaration = declaration + ";";
        }
    }

    /**
     * Checks whether this declaration was properly parsed
     * 
     * @return True if valid, False if not
     */
    public boolean isValid() {
        return name != null && type != null;
    }

    /**
     * Matches the full field signature and field name
     * 
     * @param f field to match
     * @return True if the field matches, False if not
     */
    public boolean match(Field f) {
        return f.getName().equals(name) && matchSignature(f);
    }

    /**
     * Matches only the signature of the method (modifiers, field type)
     * This is used to find alternative candidates for a field name
     * 
     * @param f field to match
     * @return True if the signature matches, False if not
     */
    public boolean matchSignature(Field f) {
        return (f.getType() == type) && ReflectionUtil.compareModifiers(f.getModifiers(), modifiers);
    }

    @Override
    public String toString() {
        return declaration;
    }
}
