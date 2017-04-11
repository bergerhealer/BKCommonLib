package com.bergerkiller.reflection.declarations;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.ReflectionUtil;

public class MethodDeclaration {
    public final String name;
    public final Class<?> returnType;
    public final Class<?>[] parameterTypes;
    public final int modifiers;
    private final String declaration;

    public MethodDeclaration(ClassTemplate<?> template, Method m) {
        this.returnType = m.getReturnType();
        this.parameterTypes = m.getParameterTypes();
        this.name = m.getName();
        this.modifiers = m.getModifiers();

        String declaration = "";
        declaration += Modifier.toString(m.getModifiers()) + " ";
        declaration += template.resolveClassName(m.getReturnType()) + " ";
        declaration += m.getName();

        declaration += "(";
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                declaration += ", ";
            }
            declaration += template.resolveClassName(parameterTypes[i]);
        }
        declaration += ");";
        this.declaration = declaration;
    }

    public MethodDeclaration(ClassTemplate<?> template, String declaration, boolean logErrors) {
        if (declaration.endsWith(";")) {
            declaration = declaration.substring(0, declaration.length() - 1);
        }

        String declarationF = ReflectionUtil.filterGenerics(declaration);

        String methodName = null;
        Class<?> returnType = null;
        Class<?>[] parameterTypes = null;
        int methodModifiers = 0;

        // Find method body start
        int method_start = declarationF.indexOf('(');
        if (method_start != -1) {
            int method_end = declarationF.indexOf(')', method_start);
            if (method_end == -1) {
                method_end = declarationF.length();
            }

            String method_header = declarationF.substring(0, method_start).trim();
            String[] method_params = declarationF.substring(method_start+1, method_end).split(",");
            String[] parts = method_header.split(" ");

            // Figure out what the field type is from the name
            if (parts.length >= 2) {
                methodName = parts[parts.length - 1];
                returnType = template.resolveClass(parts[parts.length - 2], logErrors);
                methodModifiers = ReflectionUtil.parseModifiers(parts, parts.length - 2);

                List<Class<?>> paramTypes = new ArrayList<Class<?>>();
                boolean paramFail = false;
                for (String param : method_params) {
                    param = param.trim();
                    if (param.length() == 0)
                        continue;

                    // Ignore name of the parameter
                    int paramTypeEnd = param.indexOf(' ');
                    if (paramTypeEnd != -1) {
                        param = param.substring(0, paramTypeEnd).trim();
                    }

                    Class<?> paramType = template.resolveClass(param, logErrors);
                    if (paramType == null) {
                        paramFail = true;
                        break;
                    }
                    paramTypes.add(paramType);
                }
                if (!paramFail) {
                    parameterTypes = paramTypes.toArray(new Class<?>[0]);
                }
            }
        }

        if (methodName == null || returnType == null || parameterTypes == null)
        {
            this.name = null;
            this.returnType = null;
            this.parameterTypes = null;
            this.modifiers = 0;
            this.declaration = null;
        } else {
            this.name = methodName;
            this.returnType = returnType;
            this.parameterTypes = parameterTypes;
            this.modifiers = methodModifiers;
            this.declaration = declaration + ";";
        }
    }

    /**
     * Checks whether this declaration was properly parsed
     * 
     * @return True if valid, False if not
     */
    public boolean isValid() {
        return name != null && returnType != null;
    }

    /**
     * Matches the full method signature and method name
     * 
     * @param m method to match
     * @return True if the method matches, False if not
     */
    public boolean match(Method m) {
        return m.getName().equals(name) && matchSignature(m);
    }

    /**
     * Matches only the signature of the method (modifiers, field type)
     * This is used to find alternative candidates for a method name
     * 
     * @param m method to match
     * @return True if the signature matches, False if not
     */
    public boolean matchSignature(Method m) {
        if (m.getReturnType() != returnType)
            return false;

        if (!ReflectionUtil.compareModifiers(m.getModifiers(), modifiers)) {
            return false;
        }

        Class<?>[] m_params = m.getParameterTypes();
        if (m_params.length != parameterTypes.length) {
            return false;
        }
        for (int i = 0; i < m_params.length; i++) {
            if (m_params[i] != parameterTypes[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return declaration;
    }
}
