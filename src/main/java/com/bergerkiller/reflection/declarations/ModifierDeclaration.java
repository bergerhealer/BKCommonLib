package com.bergerkiller.reflection.declarations;

import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * The declaration of number of Field or Method modifiers.
 */
public class ModifierDeclaration extends Declaration {
    private static final HashMap<String, Integer> _tokens = new HashMap<String, Integer>();
    private static final int _token_mask = (Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED | Modifier.STATIC | Modifier.FINAL | Modifier.VOLATILE | Modifier.TRANSIENT);
    private final int _modifiers;
    private final String _modifiersStr;

    static {
        int[] modifiers = new int[] {
                Modifier.ABSTRACT, Modifier.FINAL, Modifier.NATIVE,
                Modifier.PRIVATE, Modifier.PROTECTED, Modifier.PUBLIC, Modifier.STATIC,
                Modifier.STRICT, Modifier.SYNCHRONIZED, Modifier.TRANSIENT, Modifier.VOLATILE
        };
        for (int modifier : modifiers) {
            _tokens.put(Modifier.toString(modifier), modifier);
        }
    }

    public ModifierDeclaration(ClassResolver resolver, int modifiers) {
        super(resolver);
        this._modifiers = modifiers;
        this._modifiersStr = Modifier.toString(modifiers);
    }

    public ModifierDeclaration(ClassResolver resolver, String declaration) {
        super(resolver, declaration);

        // Invalid declarations are forced by passing null
        if (declaration == null) {
            this._modifiers = 0;
            this._modifiersStr = "";
            this.setInvalid();
            return;
        }

        int modifiers = 0;
        String modifiersStr = "";
        String postfix = declaration;
        while (true) {
            // Trim spaces from start of String
            int startIdx = 0;
            while (startIdx < postfix.length() && postfix.charAt(startIdx) == ' ') {
                startIdx++;
            }

            // Find the first space from the current position
            int spaceIdx = postfix.indexOf(' ', startIdx);
            if (spaceIdx != -1) {
                String token = postfix.substring(startIdx, spaceIdx);
                Integer m = _tokens.get(token);
                if (m != null) {
                    modifiers |= m.intValue();
                    if (modifiersStr.length() > 0) {
                        modifiersStr += " ";
                    }
                    modifiersStr += token;
                    postfix = postfix.substring(spaceIdx + 1);
                    continue; // next modifier token
                }
            }

            // Not a modifier; update postfix
            postfix = postfix.substring(startIdx);
            break;
        }
        this._modifiers = modifiers;
        this._modifiersStr = modifiersStr;
        this.setPostfix(postfix);
    }

    /**
     * Gets whether the static modifier is set
     * 
     * @return True if static, False if not
     */
    public final boolean isStatic() {
        return Modifier.isStatic(this._modifiers);
    }

    @Override
    public final boolean match(Declaration modifier) {
        if (modifier instanceof ModifierDeclaration) {
            return (this._modifiers & _token_mask) == (((ModifierDeclaration) modifier)._modifiers & _token_mask);
        }
        return false;
    }

    @Override
    public final String toString() {
        if (!isValid()) {
            return "??[" + _initialDeclaration + "]??";
        }
        return _modifiersStr;
    }

    @Override
    public boolean isResolved() {
        return true;
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
        str.append(indent).append("Modifier {\n");
        str.append(indent).append("  declaration=").append(this._initialDeclaration).append('\n');
        str.append(indent).append("  postfix=").append(this.getPostfix()).append('\n');
        str.append(indent).append("  modifiersStr=").append(this._modifiersStr).append('\n');
        str.append(indent).append("  modifiers=").append(this._modifiers).append('\n');
        str.append(indent).append("}\n");
    }

}
