package com.bergerkiller.reflection.declarations;

import java.lang.reflect.Constructor;

public class ConstructorDeclaration extends Declaration {
    public final Constructor<?> constructor;
    public final ModifierDeclaration modifiers;
    public final TypeDeclaration type;
    public final ParameterListDeclaration parameters;

    public ConstructorDeclaration(ClassResolver resolver, Constructor<?> constructor) {
        super(resolver);
        this.constructor = constructor;
        this.modifiers = new ModifierDeclaration(resolver, constructor.getModifiers());
        this.type = new TypeDeclaration(resolver, constructor.getDeclaringClass());
        this.parameters = new ParameterListDeclaration(resolver, constructor.getGenericParameterTypes());
    }

    public ConstructorDeclaration(ClassResolver resolver, String declaration) {
        super(resolver, declaration);
        this.constructor = null;
        this.modifiers = nextModifier();
        this.type = nextType();
        this.parameters = nextParameterList();
    }

    @Override
    public boolean match(Declaration declaration) {
        if (declaration instanceof ConstructorDeclaration) {
            ConstructorDeclaration constr = (ConstructorDeclaration) declaration;
            return type.match(constr.type) && parameters.match(constr.parameters); 
        }
        return false;
    }

    @Override
    public String toString() {
        if (!isValid()) {
            return "??[" + _initialDeclaration + "]??";
        }
        String m = modifiers.toString();
        if (m.length() > 0) {
            return m + " " + type.toString() + parameters.toString() + ";";
        } else {
            return type.toString() + parameters.toString() + ";";
        }
    }

    @Override
    public boolean isResolved() {
        return type.isResolved() && parameters.isResolved();
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
        str.append(indent).append("Constructor {\n");
        str.append(indent).append("  declaration=").append(this._initialDeclaration).append('\n');
        str.append(indent).append("  postfix=").append(this.getPostfix()).append('\n');
        this.modifiers.debugString(str, indent + "  ");
        this.type.debugString(str, indent + "  ");
        this.parameters.debugString(str, indent + "  ");
        str.append(indent).append("}\n");
    }
}
