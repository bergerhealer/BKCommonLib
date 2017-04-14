package com.bergerkiller.reflection.declarations;

import java.util.LinkedList;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * A list of package paths and imports, combined with class definitions
 */
public class SourceDeclaration extends Declaration {
    public final ClassDeclaration[] classes;

    public SourceDeclaration(String declaration) {
        super(new ClassResolver(), declaration);

        trimWhitespace(0);

        // Parse all segments
        String postfix;
        LinkedList<ClassDeclaration> classes = new LinkedList<ClassDeclaration>();
        while ((postfix = this.getPostfix()) != null && postfix.length() > 0) {
            if (postfix.startsWith("//")) {
                trimLine();
                continue;
            }

            boolean is_package = false;
            boolean is_import = false;
            if (postfix.startsWith("package ")) {
                trimWhitespace(8);
                is_package = true;
            } else if (postfix.startsWith("import ")) {
                trimWhitespace(7);
                is_import = true;
            }

            // Parse package or import name
            if (is_package || is_import) {
                String name = null;
                postfix = this.getPostfix();
                for (int cidx = 0; cidx < postfix.length(); cidx++) {
                    char c = postfix.charAt(cidx);
                    if (LogicUtil.containsChar(c, invalid_name_chars)) {
                        name = postfix.substring(0, cidx);
                        break;
                    }
                }
                if (name != null) {
                    if (is_package) {
                        this.getResolver().setPackage(name);
                    }
                    if (is_import) {
                        this.getResolver().addImport(name);
                    }
                }
                trimLine();
                continue;
            }

            // Read classes
            classes.add(nextClass());
        }
        this.classes = classes.toArray(new ClassDeclaration[classes.size()]);
    }

    @Override
    public boolean isResolved() {
        return false;
    }

    @Override
    public boolean match(Declaration declaration) {
        return false; // don't care
    }

    @Override
    public String toString() {
        String pkg = getResolver().getPackage();
        String str = "";
        if (pkg.length() > 0) {
            str += "package " + pkg + ";\n\n";
        }
        for (String imp : getResolver().getImports()) {
            str += "import " + imp + ";\n";
        }
        str += "\n";
        for (ClassDeclaration c : classes) {
            str += c.toString() + "\n";
        }
        return str;
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
    }

}
