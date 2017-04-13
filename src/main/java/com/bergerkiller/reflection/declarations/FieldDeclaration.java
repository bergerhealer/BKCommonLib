package com.bergerkiller.reflection.declarations;

import java.lang.reflect.Field;

public class FieldDeclaration extends Declaration {
    public final ModifierDeclaration modifiers;
    public final NameDeclaration name;
    public final TypeDeclaration type;
    public final Field field;

    public FieldDeclaration(ClassResolver resolver, Field field) {
        super(resolver);
        this.field = field;
        this.modifiers = new ModifierDeclaration(resolver, field.getModifiers());
        this.type = new TypeDeclaration(resolver, field.getGenericType());
        this.name = new NameDeclaration(resolver, field.getName(), null);
    }

    public FieldDeclaration(ClassResolver resolver, String declaration) {
        super(resolver, declaration);
        this.field = null;
        this.modifiers = nextModifier();
        this.type = nextType();
        this.name = nextName();
    }

    @Override
    public boolean match(Declaration declaration) {
        if (declaration instanceof FieldDeclaration) {
            FieldDeclaration field = (FieldDeclaration) declaration;
            return modifiers.match(field.modifiers) &&
                    name.match(field.name) &&
                    type.match(field.type);
        }
        return false;
    }

    /**
     * Matches this declaration with another declaration, ignoring the name of the field
     * 
     * @param declaration to check against
     * @return True if the signatures match (except for name), False if not
     */
    public boolean matchSignature(Declaration declaration) {
        if (declaration instanceof FieldDeclaration) {
            FieldDeclaration field = (FieldDeclaration) declaration;
            return modifiers.match(field.modifiers) &&
                    type.match(field.type);
        }
        return false;
    }

    @Override
    public String toString() {
        if (!isValid()) {
            return "??[" + _initialDeclaration + "]??";
        }
        String m = modifiers.toString();
        String t = type.toString();
        String n = name.toString();
        if (m.length() > 0) {
            return m + " " + t + " " + n;
        } else {
            return t + " " + n;
        }
    }

    @Override
    public boolean isResolved() {
        return this.modifiers.isResolved() && this.type.isResolved() && this.name.isResolved();
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
        str.append(indent).append("Field {\n");
        str.append(indent).append("  declaration=").append(this._initialDeclaration).append('\n');
        str.append(indent).append("  postfix=").append(this.getPostfix()).append('\n');
        this.modifiers.debugString(str, indent + "  ");
        this.type.debugString(str, indent + "  ");
        this.name.debugString(str, indent + "  ");
        str.append(indent).append("}\n");
    }
}
