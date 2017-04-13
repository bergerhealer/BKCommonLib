package com.bergerkiller.reflection.declarations;

import java.lang.reflect.Method;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

public class MethodDeclaration extends Declaration {
    public final Method method;
    public final ModifierDeclaration modifiers;
    public final TypeDeclaration returnType;
    public final NameDeclaration name;
    public final ParameterListDeclaration parameters;

    public MethodDeclaration(ClassResolver resolver, Method method) {
        super(resolver);
        this.method = method;
        this.modifiers = new ModifierDeclaration(resolver, method.getModifiers());
        this.returnType = new TypeDeclaration(resolver, method.getGenericReturnType());
        this.name = new NameDeclaration(resolver, method.getName(), null);
        this.parameters = new ParameterListDeclaration(resolver, method.getGenericParameterTypes());
    }

    public MethodDeclaration(ClassResolver resolver, String declaration) {
        super(resolver, declaration);
        this.method = null;
        this.modifiers = nextModifier();

        // Skip type variables, they may exist. For now do a simple replace between < > portions
        //TODO: Make this better? It makes it overly complicated.
        String postfix = getPostfix();
        if (postfix != null && postfix.length() > 0 && postfix.charAt(0) == '<') {
            boolean foundEnd = false;
            for (int cidx = 1; cidx < postfix.length(); cidx++) {
                char c = postfix.charAt(cidx);
                if (c == '>') {
                    foundEnd = true;
                } else if (foundEnd && !LogicUtil.containsChar(c, invalid_name_chars)) {
                    setPostfix(postfix.substring(cidx));
                    break;
                }
            }
        }

        this.returnType = nextType();
        this.name = nextName();
        this.parameters = nextParameterList();
    }

    @Override
    public boolean match(Declaration declaration) {
        if (declaration instanceof MethodDeclaration) {
            MethodDeclaration method = (MethodDeclaration) declaration;
            return modifiers.match(method.modifiers) &&
                    returnType.match(method.returnType) &&
                    name.match(method.name) &&
                    parameters.match(method.parameters);
        }
        return false;
    }

    /**
     * Matches this declaration with another declaration, ignoring the name of the method
     * 
     * @param declaration to check against
     * @return True if the signatures match (except for name), False if not
     */
    public boolean matchSignature(Declaration declaration) {
        if (declaration instanceof MethodDeclaration) {
            MethodDeclaration method = (MethodDeclaration) declaration;
            return modifiers.match(method.modifiers) &&
                    returnType.match(method.returnType) &&
                    parameters.match(method.parameters);
        }
        return false;
    }

    @Override
    public String toString() {
        if (!isValid()) {
            return "??[" + _initialDeclaration + "]??";
        }
        String m = modifiers.toString();
        String t = returnType.toString();
        String n = name.toString();
        String p = parameters.toString();
        if (m.length() > 0) {
            return m + " " + t + " " + n + p;
        } else {
            return t + " " + n + p;
        }
    }

    @Override
    public boolean isResolved() {
        return this.modifiers.isResolved() && this.returnType.isResolved() && 
                this.name.isResolved() && this.parameters.isResolved();
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
        str.append(indent).append("Method {\n");
        str.append(indent).append("  declaration=").append(this._initialDeclaration).append('\n');
        str.append(indent).append("  postfix=").append(this.getPostfix()).append('\n');
        this.modifiers.debugString(str, indent + "  ");
        this.returnType.debugString(str, indent + "  ");
        this.name.debugString(str, indent + "  ");
        this.parameters.debugString(str, indent + "  ");
        str.append(indent).append("}\n");
    }
}
