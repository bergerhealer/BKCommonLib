package com.bergerkiller.reflection.declarations;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Declaration for a method or field name
 */
public class NameDeclaration extends Declaration {
    private final String _name;
    private final String _alias;

    public NameDeclaration(ClassResolver resolver, String name, String alias) {
        super(resolver);
        this._name = name;
        this._alias = alias;
    }

    public NameDeclaration(ClassResolver resolver, String declaration) {
        this(resolver, declaration, -1);
    }

    public NameDeclaration(ClassResolver resolver, String declaration, int optionalIdx) {
        super(resolver, declaration);

        // Invalid declarations are forced by passing null
        if (declaration == null) {
            this._name = "";
            this._alias = null;
            this.setInvalid();
            return;
        }

        // Locate the name
        int startIdx = -1;
        String name = null;
        String alias = null;
        for (int cidx = 0; cidx < declaration.length(); cidx++) {
            char c = declaration.charAt(cidx);

            // Ignore spaces at the start
            if (startIdx == -1 && c == ' ') {
                continue;
            }

            boolean validNameChar = !LogicUtil.containsChar(c, invalid_name_chars);

            // Verify the first character of the name is valid, and set it
            if (startIdx == -1) {
                if (validNameChar) {
                    startIdx = cidx; 
                } else {
                    break; // not a valid start of the name
                }
            }

            // The first invalid character finishes the name declaration
            if (!validNameChar && name == null) {
                name = declaration.substring(startIdx, cidx);
            }

            // The first non-space after the name starts the next postfix part
            if (name != null && c != ' ') {
                this.setPostfix(declaration.substring(cidx));
                break;
            }
        }

        // Start index not found means the name is invalid
        if (startIdx == -1) {
            // When an optional index is set and no name is available, allow for a fallback name
            if (optionalIdx != -1) {
                this._name = "arg" + optionalIdx;
                this._alias = null;
            } else {
                this._name = "";
                this._alias = null;
                this.setInvalid();
            }
            return;
        }

        // Fallback if no end delimiter found
        if (name == null) {
            name = declaration.substring(startIdx);
            this.setPostfix("");
        }

        // Check for alias (:)
        int alias_idx = name.indexOf(':');
        if (alias_idx != -1) {
            alias = name.substring(0, alias_idx);
            name = name.substring(alias_idx + 1);
        }

        this._name = name;
        this._alias = alias;
    }

    /**
     * Gets the name value
     * 
     * @return name
     */
    public final String value() {
        return _name;
    }

    /**
     * Gets the alias used for this name. Is null if no alias is used.
     * 
     * @return name alias
     */
    public final String alias() {
        return _alias;
    }

    /**
     * Gets whether this Name Declaration has an alias defined
     * 
     * @return True if an alias is set, False if not
     */
    public final boolean hasAlias() {
        return _alias != null;
    }

    @Override
    public boolean match(Declaration declaration) {
        if (declaration instanceof NameDeclaration) {
            return ((NameDeclaration) declaration)._name.equals(this._name);
        }
        return false;
    }

    @Override
    public String toString() {
        if (!isValid()) {
            return "??[" + _initialDeclaration + "]??";
        }
        if (_alias == null) {
            return _name;
        } else {
            return _alias + ":" + _name;
        }
    }

    @Override
    public boolean isResolved() {
        return true; // no types to resolve
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
        str.append(indent).append("Name {\n");
        str.append(indent).append("  declaration=").append(this._initialDeclaration).append('\n');
        str.append(indent).append("  postfix=").append(this.getPostfix()).append('\n');
        str.append(indent).append("  name=").append(this._name).append('\n');
        str.append(indent).append("  alias=").append(this._alias).append('\n');
        str.append(indent).append("}\n");
    }

}
