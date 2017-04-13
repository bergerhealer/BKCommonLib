package com.bergerkiller.reflection.declarations;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.LinkedList;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Represents a (generic) Type declaration and allows for type matching.
 * The {@link ClassResolver} passed in will be used to parse type declarations into runtime types.
 * Examples of declarations supported by this class:
 * <ul>
 * <li>Player</li>
 * <li>List&lt;String&gt;</li>
 * <li>Map&lt;Integer, Object&gt;</li>
 * </ul>
 */
public class TypeDeclaration extends Declaration {
    private final Class<?> _type;
    private final boolean _isWildcard;
    private final String _typeName;
    private final TypeDeclaration[] _genericTypes;

    /**
     * Turns a {@link Type} into a TypeDeclaration
     * 
     * @param resolver that is used with this type
     * @param type to read the declaration from
     */
    public TypeDeclaration(ClassResolver resolver, Type type) {
        super(resolver);

        // Handle wildcard types, only support one upper bound for now ( ? extends <type> )
        this._isWildcard = (type instanceof WildcardType);
        if (this._isWildcard) {
            type = ((WildcardType) type).getUpperBounds()[0];
        }

        // Arrays
        int arrayLevels = 0;
        while (type instanceof GenericArrayType) {
            type = ((GenericArrayType) type).getGenericComponentType();
            arrayLevels++;
        }

        // Process the type itself into a raw type + optional generic types
        if (type instanceof ParameterizedType) {
            // Example: Map<K, V>
            ParameterizedType ptype = (ParameterizedType) type;
            Type[] params = ptype.getActualTypeArguments();
            this._type = LogicUtil.getArrayType((Class<?>) ptype.getRawType(), arrayLevels);
            this._typeName = resolver.resolveName(this._type);
            this._genericTypes = new TypeDeclaration[params.length];
            for (int i = 0; i < params.length; i++) {
                this._genericTypes[i] = new TypeDeclaration(resolver, params[i]);
            }
        } else if (type instanceof Class) {
            // Example: Entity
            this._type = LogicUtil.getArrayType((Class<?>) type, arrayLevels);
            this._typeName = resolver.resolveName(this._type);
            this._genericTypes = new TypeDeclaration[0];
        } else if (type instanceof TypeVariable) {
            // Example: T
            TypeVariable<?> vtype = (TypeVariable<?>) type;
            this._type = LogicUtil.getArrayType(Object.class, arrayLevels);
            this._typeName = vtype.getName();
            this._genericTypes = new TypeDeclaration[0];
        } else {
            // ???
            Logging.LOGGER_REFLECTION.warning("Unsupported type in TypeDeclaration: " + type.getClass());
            this._type = null;
            this._typeName = "";
            this._genericTypes = new TypeDeclaration[0];
            this.setInvalid();
        }
    }

    public TypeDeclaration(ClassResolver resolver, String declaration) {
        super(resolver, declaration);

        // Invalid declarations are forced by passing null
        if (declaration == null) {
            this._typeName = "";
            this._type = null;
            this._genericTypes = new TypeDeclaration[0];
            this._isWildcard = false;
            this.setInvalid();
            return;
        }

        // Find the end of the raw Class type in the declaration
        // This is the first '<' we find, or otherwise the first open space
        // We also allow types like List <String>, where a space preceeds the <
        String rawType = null;
        String postfix = "";
        int startIdx = -1;
        boolean anyType = false;
        boolean foundExtends = false;
        for (int cidx = 0; cidx < declaration.length(); cidx++) {
            char c = declaration.charAt(cidx);

            // Ignore spaces and anytype (?) at the start
            if (startIdx == -1) {
                if (c == ' ') {
                    continue;
                }
                if (c == '?') {
                    anyType = true;
                    continue;
                }
            }

            boolean validNameChar = !LogicUtil.containsChar(c, invalid_name_chars);

            // Verify the first character of the name is valid, and set it
            if (startIdx == -1) {
                if (validNameChar) {
                    startIdx = cidx; 
                } else {
                    postfix = declaration.substring(cidx);
                    break; // not a valid start of the name
                }
            }

            // The first invalid character finishes the raw type declaration
            if (!validNameChar && rawType == null) {
                rawType = declaration.substring(startIdx, cidx);
                if (anyType && !foundExtends) {
                    startIdx = -1;
                    if (rawType.equals("extends")) {
                        foundExtends = true;
                        rawType = null;
                    } else {
                        // Invalid!
                        break;
                    }
                }
            }

            // The first non-space starts the postfix part of this type declaration
            if (rawType != null && c != ' ') {
                postfix = declaration.substring(cidx);
                break;
            }
        }

        // Types that start with [? extends] are 'any types'
        this._isWildcard = anyType;

        // Raw type name not found? Invalid!
        if (startIdx == -1) {
            if (this._isWildcard) {
                this.setPostfix(postfix);
                this._typeName = "";
                this._type = Object.class;
                this._genericTypes = new TypeDeclaration[0];
            } else {
                this.setInvalid();
                this._typeName = "";
                this._type = null;
                this._genericTypes = new TypeDeclaration[0];
            }
            return;
        }

        // No contents after raw type?
        if (rawType == null) {
            rawType = declaration.substring(startIdx);
            postfix = "";
        }

        if (postfix.length() > 0 && postfix.charAt(0) == '<') {

            // Go down the list of generic types, parsing them as TypeDeclaration recursively
            LinkedList<TypeDeclaration> types = new LinkedList<TypeDeclaration>();
            do {
                TypeDeclaration gen = new TypeDeclaration(resolver, postfix.substring(1));

                // If one of the generic types is invalid, set the entire type declaration invalid
                if (!gen.isValid()) {
                    this.setInvalid();
                    this._typeName = "";
                    this._type = null;
                    this._genericTypes = new TypeDeclaration[0];
                    return;
                }

                types.add(gen);
                postfix = gen.getPostfix();
            } while (postfix.length() > 0 && postfix.charAt(0) == ',');

            // Trim starting spaces and single > from postfix
            for (int cidx = 0; cidx < postfix.length(); cidx++) {
                char c = postfix.charAt(cidx);
                if (c != ' ') {
                    if (c == '>') {
                        postfix = postfix.substring(cidx + 1);
                    } else {
                        postfix = postfix.substring(cidx);
                    }
                    break;
                }
            }

            // To array
            this._genericTypes = types.toArray(new TypeDeclaration[types.size()]);
        } else {
            // No generic types
            this._genericTypes = new TypeDeclaration[0];
        }

        // Check for array type declarations (put after the <> or type name)
        int arrayEnd = -1;
        for (int cidx = 0; cidx < postfix.length(); cidx++) {
            char c = postfix.charAt(cidx);
            if (c == '[' || c == ']') {
                rawType += c;
            } else if (c != ' ') {
                arrayEnd = cidx;
                break;
            }
        }
        if (arrayEnd == -1) {
            this.setPostfix("");
        } else {
            this.setPostfix(postfix.substring(arrayEnd));
        }

        // Resolve the raw type
        this._type = resolver.resolveClass(rawType);
        if (this._type == null) {
            this._typeName = "??" + rawType + "??";
        } else {
            this._typeName = rawType;
        }
    }

    @Override
    public final boolean match(Declaration declaration) {
        if (!(declaration instanceof TypeDeclaration)) {
            return false;
        }
        TypeDeclaration type = (TypeDeclaration) declaration;
        if (this._type == null || type._type == null) return false;
        if (this._isWildcard != type._isWildcard) return false;
        if (!this._type.equals(type._type)) return false;
        if (this._genericTypes.length != type._genericTypes.length) return false;
        for (int i = 0; i < this._genericTypes.length; i++) {
            if (!this._genericTypes[i].match(type._genericTypes[i])) return false;
        }
        return true;
    }

    /**
     * Creates a String representation of this Type Declaration
     * 
     * @return simplified human-readable declaration String
     */
    @Override
    public final String toString() {
        if (!isValid()) {
            return "??[" + _initialDeclaration + "]??";
        }
        int arrIdx = _typeName.indexOf('[');
        String typeName = _typeName;
        String arrPart = "";
        if (arrIdx != -1) {
            typeName = this._typeName.substring(0, arrIdx);
            arrPart = this._typeName.substring(arrIdx);
        }
        
        String str;
        if (this._isWildcard) {
            if (typeName.length() == 0) {
                str = "?";
            } else {
                str = "? extends " + typeName;
            }
        } else {
            str = typeName;
        }        
        if (this._genericTypes.length > 0) {
            str += "<";
            boolean first = true;
            for (TypeDeclaration genericType : _genericTypes) {
                if (first) {
                    first = false;
                } else {
                    str += ", ";
                }
                str += genericType.toString();
            }
            str += ">";
        }
        str += arrPart;
        return str;
    }

    @Override
    public boolean isResolved() {
        if (this._type == null) {
            return false;
        }
        for (int i = 0; i < _genericTypes.length; i++) {
            if (!_genericTypes[i].isResolved()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
        str.append(indent).append("Type {\n");
        str.append(indent).append("  declaration=").append(this._initialDeclaration).append('\n');
        str.append(indent).append("  postfix=").append(this.getPostfix()).append('\n');
        str.append(indent).append("  typeName=").append(this._typeName).append('\n');
        str.append(indent).append("  type=").append(this._type).append('\n');
        str.append(indent).append("  isWildcard=").append(this._isWildcard).append('\n');
        for (TypeDeclaration t : this._genericTypes) {
            t.debugString(str, indent + "  ");
        }
        str.append(indent).append("}\n");
    }

}
