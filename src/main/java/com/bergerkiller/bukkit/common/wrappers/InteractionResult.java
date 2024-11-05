package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;

/**
 * Wrapper class for EnumInteractionResult introduced in Minecraft 1.9.
 * Is officially used since Minecraft 1.16 when handling entity interactions.
 * Before that, the constant is simply turned into a truthy value.
 */
public enum InteractionResult {
    /**
     * The interaction succeeded
     */
    SUCCESS(true),
    /**
     * The item in the hand of the player was consumed.
     * Same behavior as SUCCESS before Minecraft 1.15.
     */
    CONSUME(true),
    /**
     * Nothing happened
     */
    PASS(false),
    /**
     * A problem occurred trying to interact.
     * Signals something is wrong in client-server synchronization.
     */
    FAIL(false);

    private final Object _handle;
    private final boolean _truthy;

    private InteractionResult(boolean truthy) {
        this._truthy = truthy;
        if (CommonBootstrap.evaluateMCVersion(">=", "1.21.2")) {
            // Interface with constants
            String name = this.name();
            try {
                this._handle = Resolver.resolveAndGetDeclaredField(
                        CommonUtil.getClass("net.minecraft.world.EnumInteractionResult"),
                        name).get(null);
            } catch (Throwable t) {
                throw new IllegalStateException("EnumInteractionResult missing constant: " + name, t);
            }
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.9")) {
            // Enum
            String name = this.name();
            if (name.equals("CONSUME") && !CommonBootstrap.evaluateMCVersion(">=", "1.15")) {
                name = "SUCCESS";
            }
            for (Enum<?> enumConstant : (Enum[]) CommonUtil.getClass("net.minecraft.world.EnumInteractionResult").getEnumConstants()) {
                if (enumConstant.name().equals(name)) {
                    this._handle = enumConstant;
                    return;
                }
            }
            throw new IllegalStateException("EnumInteractionResult missing constant: " + name);
        } else {
            // True/False
            this._handle = null;
        }
    }

    /**
     * Gets the raw underlying EnumInteractionResult handle
     * 
     * @return handle
     */
    public Object getRawHandle() {
        return this._handle;
    }

    /**
     * Gets whether this interaction result is truthy.
     * Before Minecraft 1.16, this is the result passed on when interacting with entities.
     * 
     * @return truthy
     */
    public boolean isTruthy() {
        return this._truthy;
    }

    /**
     * Retrieves the InteractionResult value matching a handle.
     * If input is null, null is returned.
     * 
     * @param nmsEnumInteractionResult
     * @return best-matching interaction result
     */
    public static InteractionResult fromHandle(Object nmsEnumInteractionResult) {
        if (nmsEnumInteractionResult == null) {
            return null;
        }
        for (InteractionResult result : values()) {
            if (result._handle == nmsEnumInteractionResult) {
                return result;
            }
        }
        throw new IllegalArgumentException("Unsupported enum value: " + nmsEnumInteractionResult);
    }

    /**
     * Converts a truthy value back to a SUCCESS or PASS
     * 
     * @param truth
     * @return SUCCESS or PASS based on truth
     */
    public static InteractionResult fromTruthy(boolean truth) {
        return truth ? SUCCESS : PASS;
    }
}
