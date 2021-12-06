package com.bergerkiller.bukkit.common.component;

import java.util.Deque;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.bases.CheckedFunction;
import com.bergerkiller.bukkit.common.bases.CheckedRunnable;
import com.bergerkiller.bukkit.common.bases.CheckedSupplier;

/**
 * A list of LibraryComponent elements which are all sequentially
 * enabled and disabled in order, with error handling.
 *
 * @param <E> Input environment used to check whether a component should
 *            be enabled, and used to create a new library component.
 */
public class LibraryComponentList<E> extends LibraryComponentHolder<E> {
    private final Deque<EnabledComponent> enabledComponents = new LinkedList<>();

    /**
     * Initializes a new library component list
     *
     * @param environment Input environment for checking whether components should
     *        be enabled or not.
     * @param logger Logger to write errors to that occur when enabling/
     *        disabling components
     * @param identifier Unique identifier for the type of components being selected.
     *        Used when logging.
     */
    public LibraryComponentList(E environment, Logger logger, String identifier) {
        super(environment, logger, identifier);
    }

    @Override
    public LibraryComponentList<E> runFirst(CheckedRunnable runnable) {
        super.runFirst(runnable);
        return this;
    }

    /**
     * Adds a conditional library component that is only enabled when the current server
     * Minecraft version falls between the two versions specified, inclusive.
     *
     * @param <L> Library component type
     * @param identifier Unique identifier for the component being enabled
     * @param minimumMinecraftVersion Minimum supported Minecraft version. Null or empty String
     *                                if there is no minimum.
     * @param maximumMinecraftVersion Maximum supported Minecraft version. Null or empty String
     *                                if there is no maximum.
     * @param componentSupplier Constructor for a new instance of the library component. Is
     *                          called when the current Minecraft version is compatible.
     * @return created library component, or null if not created
     */
    public <L extends LibraryComponent> L enableForVersions(
            final String identifier,
            final String minimumMinecraftVersion,
            final String maximumMinecraftVersion,
            final CheckedSupplier<L> componentSupplier
    ) {
        return this.enable(LibraryComponent.forVersions(identifier,
                minimumMinecraftVersion, maximumMinecraftVersion,
                componentSupplier));
    }

    /**
     * Creates a conditional library component that is only enabled when the current server
     * Minecraft version falls between the two versions specified, inclusive.
     *
     * @param <L> Library component type
     * @param identifier Unique identifier for the component being enabled
     * @param minimumMinecraftVersion Minimum supported Minecraft version. Null or empty String
     *                                if there is no minimum.
     * @param maximumMinecraftVersion Maximum supported Minecraft version. Null or empty String
     *                                if there is no maximum.
     * @param componentFunction Constructor for a new instance of the library component. Is
     *                          called when the current Minecraft version is compatible, with
     *                          the environment as input.
     * @return created library component, or null if not created
     */
    public <L extends LibraryComponent> L enableForVersions(
            final String identifier,
            final String minimumMinecraftVersion,
            final String maximumMinecraftVersion,
            final CheckedFunction<E, L> componentFunction
    ) {
        return this.enable(LibraryComponent.forVersions(identifier,
                minimumMinecraftVersion, maximumMinecraftVersion,
                componentFunction));
    }

    /**
     * Tries to enable a component, adding it to the list of enabled
     * components. If enabling fails with an error, then the component
     * is not added and will not be disabled again later.
     *
     * @param component
     * @return Input component, also when enabling fails
     */
    public <L extends LibraryComponent> L enable(final L component) {
        return enable(new LibraryComponent.Conditional<E, L>() {
            @Override
            public String getIdentifier() {
                return component.getClass().getSimpleName();
            }

            @Override
            public boolean isSupported(E environment) {
                return true;
            }

            @Override
            public L create(E environment) throws Throwable {
                return component;
            }
        });
    }

    /**
     * Tries to enable a conditionally supported component. If the condition indicates
     * it is supported, then the component is created and enabled. If any of that fails,
     * then the component is not enabled and not added to this list. In that case,
     * null is returned.
     *
     * @param <L> Type of library component
     * @param conditional Conditional component to evaluate
     * @return Created and enabled component, or null if any of that failed
     */
    public <L extends LibraryComponent> L enable(LibraryComponent.Conditional<E, L> conditional) {
        // Ensure (environment) is initialized first
        if (!this.runInitializers()) {
            return null;
        }

        // If not supported, fail and return null
        if (!conditional.isSupported(this.environment)) {
            return null;
        }

        // Try to create and initialize the new component
        L component = this.tryCreateAndEnableComponent(conditional);
        if (component != null) {
            enabledComponents.offer(new EnabledComponent(component, conditional.getIdentifier()));
        }
        return component;
    }

    /**
     * Disables all previously enabled components in the reverse order they were
     * enabled. The last-enabled component is disabled first.
     */
    public void disable() {
        for (EnabledComponent enabledComponent; (enabledComponent = enabledComponents.pollLast()) != null;) {
            tryDisableComponent(enabledComponent.component, enabledComponent.identifier);
        }
    }

    /**
     * Creates a plugin library component list. The environment is the plugin itself.
     *
     * @param <P> Plugin type
     * @param plugin Plugin environment
     * @return Library component list
     */
    public static <P extends Plugin> LibraryComponentList<P> forPlugin(P plugin) {
        return new LibraryComponentList<P>(plugin, plugin.getLogger(), plugin.getName());
    }

    private static class EnabledComponent {
        public final LibraryComponent component;
        public final String identifier;

        public EnabledComponent(LibraryComponent component, String identifier) {
            this.component = component;
            this.identifier = identifier;
        }
    }
}
