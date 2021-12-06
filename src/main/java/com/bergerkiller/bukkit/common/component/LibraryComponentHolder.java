package com.bergerkiller.bukkit.common.component;

import java.util.Deque;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bergerkiller.bukkit.common.bases.CheckedRunnable;

/**
 * Holds multiple library components
 */
public class LibraryComponentHolder<E> {
    protected final E environment;
    protected final Logger logger;
    protected final String holderIdentifier;
    private final Deque<CheckedRunnable> initializers = new LinkedList<>();
    private Throwable lastError = null;

    /**
     * Initializes a new Library Component Holder
     *
     * @param environment Input environment for checking whether components should
     *        be enabled or not.
     * @param logger Logger to write errors to that occur when enabling/
     *        disabling components
     * @param identifier Unique identifier for the type of components being selected.
     *        Used when logging.
     */
    public LibraryComponentHolder(E environment, Logger logger, String identifier) {
        this.environment = environment;
        this.logger = logger;
        this.holderIdentifier = identifier;
    }

    /**
     * Gets the managed environment of this selector
     *
     * @return environment
     */
    public E getEnvironment() {
        return environment;
    }

    /**
     * Orders this holder to run the specified runnable first. If execution fails,
     * then the components will not be enabled. If a default component was set,
     * then that one is enabled and set instead.
     *
     * @param runnable Runnable to execute
     * @return this
     */
    public LibraryComponentHolder<E> runFirst(CheckedRunnable runnable) {
        initializers.offerLast(runnable);
        return this;
    }

    /**
     * Gets the last error that occurred when trying to enable a component.
     * Can be used by the default library component to supply additional
     * information to the callers.
     *
     * @return Last thrown error when enabling a component
     */
    public Throwable getLastError() {
        return lastError;
    }

    protected boolean runInitializers() {
        CheckedRunnable runnable;
        while ((runnable = initializers.poll()) != null) {
            try {
                runnable.run();
            } catch (Throwable t) {
                logger.log(Level.SEVERE, "Failed to run initializer for " + holderIdentifier, t);
                lastError = t;
                initializers.offerFirst(runnable); // Run again next time
                return false;
            }
        }
        return true;
    }

    protected boolean checkIsSupported(LibraryComponent.Conditional<E, ?> conditional) {
        try {
            return conditional.isSupported(environment);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to detect whether component '" +
                    conditional.getIdentifier() + "' of " + holderIdentifier +
                    " is supported", t);
            lastError = t;
            return false;
        }
    }

    protected <L extends LibraryComponent> L tryCreateAndEnableComponent(LibraryComponent.Conditional<E, L> conditional) {
        // Create the component
        L component;
        try {
            component = conditional.create(environment);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to create component '" +
                    conditional.getIdentifier() + "' of " + holderIdentifier, t);
            lastError = t;
            return null;
        }

        // Enable the new component
        try {
            component.enable();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to enable component '" +
                    conditional.getIdentifier() + "' of " + holderIdentifier, t);
            lastError = t;
            return null;
        }

        return component;
    }

    protected void tryDisableComponent(LibraryComponent component, String identifier) {
        try {
            component.disable();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to disable component '" + identifier +
                    "' of " + holderIdentifier, t);
        }
    }
}
