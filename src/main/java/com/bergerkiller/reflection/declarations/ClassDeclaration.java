package com.bergerkiller.reflection.declarations;

import java.util.LinkedList;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Declares the full contents of a Class
 */
public class ClassDeclaration extends Declaration {
    public final ModifierDeclaration modifiers;
    public final TypeDeclaration type;
    public final ConstructorDeclaration[] constructors;
    public final MethodDeclaration[] methods;
    public final FieldDeclaration[] fields;
    public final boolean is_interface;

    public ClassDeclaration(ClassResolver resolver, String declaration) {
        super(resolver.clone(), declaration);

        // Modifiers, stop when invalid
        this.modifiers = nextModifier();
        if (!this.isValid()) {
            this.type = nextType();
            this.constructors = new ConstructorDeclaration[0];
            this.methods = new MethodDeclaration[0];
            this.fields = new FieldDeclaration[0];
            this.is_interface = false;
            return;
        }

        // Class or interface? Then parse class/interface type
        String postfix = this.getPostfix();
        this.is_interface = postfix.startsWith("interface ");
        if (!this.is_interface && !postfix.startsWith("class ")) {
            this.type = nextType();
            this.constructors = new ConstructorDeclaration[0];
            this.methods = new MethodDeclaration[0];
            this.fields = new FieldDeclaration[0];
            this.setInvalid();
            return;
        }
        setPostfix(postfix.substring(this.is_interface ? 10 : 6));
        this.type = nextType();
        if (!this.isValid()) {
            this.constructors = new ConstructorDeclaration[0];
            this.methods = new MethodDeclaration[0];
            this.fields = new FieldDeclaration[0];
            return;
        }

        // Find start of class definitions {
        postfix = getPostfix();
        boolean foundClassStart = false;
        int startIdx = -1;
        for (int cidx = 0; cidx < postfix.length(); cidx++) {
            char c = postfix.charAt(cidx);
            if (c == '{') {
                foundClassStart = true;
            } else if (foundClassStart && !LogicUtil.containsChar(c, space_chars)) {
                startIdx = cidx;
                break;
            }
        }
        if (startIdx == -1) {
            this.constructors = new ConstructorDeclaration[0];
            this.methods = new MethodDeclaration[0];
            this.fields = new FieldDeclaration[0];
            this.setInvalid();
            return;
        }
        this.setPostfix(postfix.substring(startIdx));

        LinkedList<ConstructorDeclaration> constructors = new LinkedList<ConstructorDeclaration>();
        LinkedList<MethodDeclaration> methods = new LinkedList<MethodDeclaration>();
        LinkedList<FieldDeclaration> fields = new LinkedList<FieldDeclaration>();
        while ((postfix = getPostfix()) != null && postfix.length() > 0) {
            if (postfix.charAt(0) == '}') {
                trimWhitespace(1);
                break;
            }

            MethodDeclaration mdec = new MethodDeclaration(getResolver(), postfix);
            if (mdec.isValid()) {
                methods.add(mdec);
                setPostfix(mdec.getPostfix());
                trimLine();
                continue;
            }
            ConstructorDeclaration cdec = new ConstructorDeclaration(getResolver(), postfix);
            if (cdec.isValid()) {
                constructors.add(cdec);
                setPostfix(cdec.getPostfix());
                trimLine();
                continue;
            }
            FieldDeclaration fdec = new FieldDeclaration(getResolver(), postfix);
            if (fdec.isValid()) {
                fields.add(fdec);
                setPostfix(fdec.getPostfix());
                trimLine();
                continue;
            }
            break;
        }
        this.constructors = constructors.toArray(new ConstructorDeclaration[constructors.size()]);
        this.methods = methods.toArray(new MethodDeclaration[methods.size()]);
        this.fields = fields.toArray(new FieldDeclaration[fields.size()]);
    }

    @Override
    public boolean isResolved() {
        return false;
    }

    @Override
    public boolean match(Declaration declaration) {
        return false; // don't even bother
    }

    @Override
    public String toString() {
        String str = this.modifiers.toString();
        if (str.length() > 0) {
            str += " ";
        }
        str += this.is_interface ? "interface " : "class ";
        str += this.type.toString();
        str += " {\n";
        for (FieldDeclaration fdec : this.fields) str += "    " + fdec.toString() + "\n";
        for (ConstructorDeclaration cdec : this.constructors) str += "    " + cdec.toString() + "\n";
        for (MethodDeclaration mdec : this.methods) str += "    " + mdec.toString() + "\n";
        str += "}";
        return str;
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
        
    }

}
