package com.bergerkiller.reflection.declarations;

import java.lang.reflect.Type;

/**
 * A single named parameter in a parameter list for a method or constructor.
 * Matching only matches the parameter type, not the name, as that is unimportant.
 */
public class ParameterDeclaration extends Declaration {
    public final TypeDeclaration type;
    public final NameDeclaration name;

    public ParameterDeclaration(ClassResolver resolver, Type type, String name) {
        super(resolver);
        this.type = new TypeDeclaration(resolver, type);
        this.name = new NameDeclaration(resolver, name, null);
    }

    public ParameterDeclaration(ClassResolver resolver, String declaration, int paramIdx) {
        super(resolver, declaration);
        this.type = nextType();
        this.name = nextName(paramIdx);
    }

    @Override
    public boolean match(Declaration declaration) {
        if (declaration instanceof ParameterDeclaration) {
            return type.match(((ParameterDeclaration) declaration).type);
        }
        return false;
    }

    @Override
    public String toString() {
        if (!isValid()) {
            return "??[" + _initialDeclaration + "]??";
        }
        return type.toString() + " " + name.toString();
    }

    @Override
    public boolean isResolved() {
        return this.type.isResolved() && this.name.isResolved();
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
        str.append(indent).append("Parameter {\n");
        str.append(indent).append("  declaration=").append(this._initialDeclaration).append('\n');
        str.append(indent).append("  postfix=").append(this.getPostfix()).append('\n');
        this.name.debugString(str, indent + "  ");
        this.type.debugString(str, indent + "  ");
        str.append(indent).append("}\n");
    }

}
