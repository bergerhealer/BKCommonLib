package com.bergerkiller.reflection.declarations;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Base class for Declaration implementations
 */
public abstract class Declaration {
    protected static final char[] invalid_name_chars;
    protected static final char[] space_chars;
    private String _postfix;
    protected final String _initialDeclaration;
    private final ClassResolver _resolver;

    static {
        invalid_name_chars = new char[] {
                ' ', '\n', '\r', '<', '>', ',', '(', ')', '{', '}', ';', '='
        };
        space_chars = new char[] {
                ' ', '\n', '\r'
        };
    }

    public Declaration(ClassResolver resolver) {
        this._resolver = resolver;
        this._initialDeclaration = "";
        this._postfix = "";
    }

    public Declaration(ClassResolver resolver, String initialDeclaration) {
        this._resolver = resolver;
        this._initialDeclaration = initialDeclaration;
        this._postfix = initialDeclaration;
    }

    /**
     * Gets the {@link ClassResolver} used to resolve Class types from name
     * 
     * @return class resolver
     */
    public final ClassResolver getResolver() {
        return this._resolver;
    }

    protected final ModifierDeclaration nextModifier() {
        return updatePostfix(new ModifierDeclaration(this._resolver, this._postfix));
    }

    protected final NameDeclaration nextName() {
        return updatePostfix(new NameDeclaration(this._resolver, this._postfix));
    }

    protected final NameDeclaration nextName(int optionalIdx) {
        return updatePostfix(new NameDeclaration(this._resolver, this._postfix, optionalIdx));
    }

    protected final TypeDeclaration nextType() {
        return updatePostfix(new TypeDeclaration(this._resolver, this._postfix));
    }

    protected final ParameterDeclaration nextParameter(int paramIdx) {
        return updatePostfix(new ParameterDeclaration(this._resolver, this._postfix, paramIdx));
    }

    protected final ParameterListDeclaration nextParameterList() {
        return updatePostfix(new ParameterListDeclaration(this._resolver, this._postfix));
    }

    protected final ClassDeclaration nextClass() {
        return updatePostfix(new ClassDeclaration(this._resolver, this._postfix));
    }

    /**
     * Updates the text that exists after this declaration, by taking
     * over the information from the last child declaration.
     * 
     * @param lastDeclaration
     */
    protected final <T extends Declaration> T updatePostfix(T lastDeclaration) {
        this._postfix = lastDeclaration.getPostfix();
        return lastDeclaration;
    }

    /**
     * Gets the text put after this declaration.
     * If this declaration invalid according to {@link #isValid()} this function returns null.
     * 
     * @return declaration postfix
     */
    public final String getPostfix() {
        return _postfix;
    }

    /**
     * Checks whether the declaration is in a valid syntax
     * 
     * @return True if the syntax is valid, False if not
     */
    public final boolean isValid() {
        return _postfix != null;
    }

    /**
     * Sets the text that exists after this declaration.
     * To mark this declaration as invalid, pass a null postfix.
     * Implementation use only.
     * 
     * @param postfix to set to
     */
    protected final void setPostfix(String postfix) {
        this._postfix = postfix;
    }

    /**
     * Marks this declaration as invalid because of a syntax error
     */
    protected final void setInvalid() {
        this._postfix = null;
    }

    /**
     * Removes all whitespace characters from the start of the current postfix
     * 
     * @param start index
     */
    protected final void trimWhitespace(int start) {
        if (this._postfix == null) {
            return;
        }
        for (int cidx = start; cidx < this._postfix.length(); cidx++) {
            char c = this._postfix.charAt(cidx);
            if (LogicUtil.containsChar(c, space_chars)) {
                continue;
            }
            this._postfix = this._postfix.substring(cidx);
            return;
        }
        this._postfix = "";
    }

    /**
     * Removes everything up until the next newline
     */
    protected final void trimLine() {
        if (this._postfix == null) {
            return;
        }
        boolean foundNewline = false;
        for (int cidx = 0; cidx < this._postfix.length(); cidx++) {
            char c = this._postfix.charAt(cidx);
            if (c == '\r' || c == '\n') {
                foundNewline = true;
                continue;
            }
            if (c != ' ' && foundNewline) {
                this._postfix = this._postfix.substring(cidx);
                return;
            }
        }
        this._postfix = "";
    }

    /**
     * Checks whether all the Class types could be resolved for this Declaration
     * 
     * @return True if this declaration was fully resolved, False if not
     */
    public abstract boolean isResolved();

    /**
     * Checks if the declaration specified matches this declaration.
     * 
     * @param declaration to check against
     * @return True if matching, False if not
     */
    public abstract boolean match(Declaration declaration);

    /**
     * Gets a human-readable String representation of this Declaration
     * 
     * @return declaration String
     */
    @Override
    public abstract String toString(); // must implement

    /**
     * Gets a debug String showing deep nested information about this parsed declaration.
     * If generic types are showing hidden differences, this method can show this difference.
     * 
     * @return debug string
     */
    public String debugString() {
        StringBuilder str = new StringBuilder();
        debugString(str, "");
        return str.toString();
    }

    protected abstract void debugString(StringBuilder str, String indent); // must implement
}
