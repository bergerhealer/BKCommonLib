package com.bergerkiller.bukkit.common.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.ModuleLogger;
import com.bergerkiller.bukkit.common.bases.CheckedFunction;
import com.bergerkiller.bukkit.common.bases.CheckedRunnable;
import com.bergerkiller.bukkit.common.bases.CheckedSupplier;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Tracks a list of potential library components that can be used,
 * and enables the first one that can be enabled. Can be used to
 * decide between multiple different handlers or API's for the same
 * functionality.
 *
 * @param <E> Input environment used to check whether a component should
 *            be enabled, and used to create a new library component.
 * @param <L> Type of library component tracked by this selector
 */
public class LibraryComponentSelector<E, L extends LibraryComponent> extends LibraryComponentHolder<E> implements LibraryComponent {
    private final List<LibraryComponent.Conditional<E, ? extends L>> registered = new ArrayList<>();
    private CheckedSupplier<? extends L> defaultComponentSupplier = () -> null;
    private int currentRegisteredComponent = -1;
    private L currentComponent = null;
    private boolean enabled = true;

    /**
     * Initializes a new library component selector
     *
     * @param environment Input environment for checking whether components should
     *        be enabled or not.
     * @param logger Logger to write errors to that occur when enabling/
     *        disabling components
     * @param identifier Unique identifier for the type of components being selected.
     *        Used when logging.
     */
    public LibraryComponentSelector(E environment, Logger logger, String identifier) {
        super(environment, logger, identifier);
    }

    /**
     * Gets whether the components of this selector is currently enabled.
     * Becomes true once {@link #update()} is first called, and false once
     * {@link #disable()} is called.<br>
     * <br>
     * Can be used by {@link #setDefaultComponent(Supplier)} to provide
     * an at-runtime hint that this component is disabled, rather than that
     * no option could be enabled.
     *
     * @return True if this selector is currently enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public LibraryComponentSelector<E, L> runFirst(CheckedRunnable runnable) {
        super.runFirst(runnable);
        return this;
    }

    /**
     * Sets a default library component to use when none of the registered
     * options can be enabled, or when this selector is disabled. This is usually a
     * version of the component which implements all methods as a No-Op or that throws
     * an UnsupportedOperationException. It should tell consumers of the component that
     * the component is not available.<br>
     * <br>
     * If this is never called, then {@link #get()} will simply return null when
     * none of the options are enabled.<br>
     * <br>
     * A function (constructor) for a new default library component can be specified.
     * If another valid component enables, then it will never be called. As input argument
     * this selector is specified, where for example {@link #getLastError()} can be used
     * for initialization.
     *
     * @param defaultLibraryComponentFunc Constructs a new instance of the library
     *        component.
     * @return this
     */
    public LibraryComponentSelector<E, L> setDefaultComponent(CheckedFunction<LibraryComponentSelector<E, L>, ? extends L> defaultLibraryComponentFunc) {
        return setDefaultComponent(() -> defaultLibraryComponentFunc.apply(LibraryComponentSelector.this));
    }

    /**
     * Sets a default library component to use when none of the registered
     * options can be enabled, or when this selector is disabled. This is usually a
     * version of the component which implements all methods as a No-Op or that throws
     * an UnsupportedOperationException. It should tell consumers of the component that
     * the component is not available.<br>
     * <br>
     * If this is never called, then {@link #get()} will simply return null when
     * none of the options are enabled.
     *
     * @param defaultLibraryComponent Instance of the library component
     * @return this
     */
    public LibraryComponentSelector<E, L> setDefaultComponent(L defaultLibraryComponent) {
        return setDefaultComponent(() -> defaultLibraryComponent);
    }

    /**
     * Sets a default library component to use when none of the registered
     * options can be enabled, or when this selector is disabled. This is usually a
     * version of the component which implements all methods as a No-Op or that throws
     * an UnsupportedOperationException. It should tell consumers of the component that
     * the component is not available.<br>
     * <br>
     * If this is never called, then {@link #get()} will simply return null when
     * none of the options are enabled.<br>
     * <br>
     * A supplier (constructor) for a new default library component can be specified.
     * If another valid component enables, then it will never be called.
     *
     * @param defaultLibraryComponentSupplier Supplies a new instance of the library
     *        component.
     * @return this
     */
    public LibraryComponentSelector<E, L> setDefaultComponent(CheckedSupplier<? extends L> defaultLibraryComponentSupplier) {
        this.defaultComponentSupplier = defaultLibraryComponentSupplier;
        return this;
    }

    /**
     * Adds another possible library component that can be used by this selector.
     * Does not yet enable the component until {@link #update()} is called.
     *
     * @param option Conditional library component
     * @return this
     */
    public LibraryComponentSelector<E, L> addOption(LibraryComponent.Conditional<E, ? extends L> option) {
        this.registered.add(option);
        return this;
    }

    /**
     * Adds a conditional library component that is only enabled when the predicate
     * tests true.
     *
     * @param identifier Unique identifier for this optional component
     * @param isSupported Predicate to test whether the component should be enabled
     * @param componentSupplier Constructor for a new instance of the library component. Is
     *                          called when the current Minecraft version is compatible.
     * @return this
     */
    public LibraryComponentSelector<E, L> addWhen(
            final String identifier,
            final Predicate<E> isSupported,
            final CheckedSupplier<L> componentSupplier
    ) {
        return this.addOption(LibraryComponent.when(identifier, isSupported, componentSupplier));
    }

    /**
     * Adds a conditional library component that is only enabled when the predicate
     * tests true.
     *
     * @param identifier Unique identifier for this optional component
     * @param isSupported Predicate to test whether the component should be enabled
     * @param componentFunction Constructor for a new instance of the library component. Is
     *                          called when the current Minecraft version is compatible, with
     *                          the environment as input.
     * @return this
     */
    public LibraryComponentSelector<E, L> addWhen(
            final String identifier,
            final Predicate<E> isSupported,
            final CheckedFunction<E, L> componentFunction
    ) {
        return this.addOption(LibraryComponent.when(identifier, isSupported, componentFunction));
    }

    /**
     * Adds a conditional library component that is only enabled when the current server
     * Minecraft version falls between the two versions specified, inclusive.
     *
     * @param minimumMinecraftVersion Minimum supported Minecraft version. Null or empty String
     *                                if there is no minimum.
     * @param maximumMinecraftVersion Maximum supported Minecraft version. Null or empty String
     *                                if there is no maximum.
     * @param componentSupplier Constructor for a new instance of the library component. Is
     *                          called when the current Minecraft version is compatible.
     * @return this
     */
    public LibraryComponentSelector<E, L> addVersionOption(
            final String minimumMinecraftVersion,
            final String maximumMinecraftVersion,
            final CheckedSupplier<L> componentSupplier
    ) {
        return this.addOption(LibraryComponent.forVersions("",
                minimumMinecraftVersion, maximumMinecraftVersion,
                componentSupplier));
    }

    /**
     * Adds a conditional library component that is only enabled when the current server
     * Minecraft version falls between the two versions specified, inclusive.
     *
     * @param minimumMinecraftVersion Minimum supported Minecraft version. Null or empty String
     *                                if there is no minimum.
     * @param maximumMinecraftVersion Maximum supported Minecraft version. Null or empty String
     *                                if there is no maximum.
     * @param componentFunction Constructor for a new instance of the library component. Is
     *                          called when the current Minecraft version is compatible, with
     *                          the environment as input.
     * @return this
     */
    public LibraryComponentSelector<E, L> addVersionOption(
            final String minimumMinecraftVersion,
            final String maximumMinecraftVersion,
            final CheckedFunction<E, L> componentFunction
    ) {
        return this.addOption(LibraryComponent.forVersions("",
                minimumMinecraftVersion, maximumMinecraftVersion,
                componentFunction));
    }

    /**
     * Gets the currently enabled component. Returns the default component if
     * none of the registered options could be enabled. Returns <i>null</i>
     * if {@link #update()} was never called, or {@link #disable()} was last called
     * and no default component was set.
     *
     * @return Currently enabled library component
     */
    public L get() {
        return this.currentComponent;
    }

    /**
     * Updates the library component that should be enabled. If another option
     * becomes possible that has a higher priority than the one currently enabled,
     * then the previous option is disabled and the new one is enabled.
     *
     * @return Currently enabled component
     */
    public synchronized L update() {
        enabled = true;

        // Before fully enabling any components, run initializers
        // If any of these fail, then abort enabling the rest
        if (this.runInitializers()) {
            // Start enabling components
            for (int i = 0; i < registered.size(); i++) {
                LibraryComponent.Conditional<E, ? extends L> conditional = registered.get(i);
                if (!checkIsSupported(conditional)) {
                    continue;
                }

                // If already enabled, nothing changed
                if (i == currentRegisteredComponent) {
                    return currentComponent;
                }

                // Disable previously enabled component first
                disableCurrentComponent();

                // Create the new component
                L component = tryCreateAndEnableComponent(conditional);
                if (component != null) {
                    // Assign and done
                    currentRegisteredComponent = i;
                    currentComponent = component;
                    return component;
                }
            }
        } else {
            // Disable anything currently enabled - updates error info
            this.disableCurrentComponent();
        }

        // Failed to find a component to enable
        // Enable the default component, if one was registered
        if (currentComponent == null) {
            enableDefaultComponent();
        }
        return currentComponent;
    }

    /**
     * Calls {@link #update()} once to initialize any components, if needed.
     */
    @Override
    public void enable() {
        this.update();
    }

    /**
     * Disables any component that is still enabled currently
     */
    public synchronized void disable() {
        disableCurrentComponent();
        enabled = false;
        enableDefaultComponent();
    }

    private void enableDefaultComponent() {
        L component;
        try {
            component = defaultComponentSupplier.get();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to instantiate the default component", t);
            return;
        }
        if (component != null) {
            try {
                component.enable();
            } catch (Throwable t) {
                logger.log(Level.SEVERE, "Failed to enable the default component", t);
                return;
            }
        }
        currentComponent = component;
    }

    private void disableCurrentComponent() {
        // Switch out the previously chosen component
        L component = currentComponent;
        int index = currentRegisteredComponent;
        currentComponent = null;
        currentRegisteredComponent = -1;
        if (component == null) {
            return;
        }

        // Try to disable the current component
        String identifier = (currentRegisteredComponent == -1)
                ? "DEFAULT" : registered.get(index).getIdentifier();
        tryDisableComponent(component, identifier);
    }

    /**
     * Creates a plugin library component selector. The environment is the plugin itself.
     *
     * @param <P> Plugin type
     * @param <L> Library component type
     * @param plugin Plugin environment
     * @return Library component selector
     */
    public static <P extends Plugin, L extends LibraryComponent> LibraryComponentSelector<P, L> forPlugin(P plugin) {
        return new LibraryComponentSelector<P, L>(plugin, plugin.getLogger(), plugin.getName());
    }

    /**
     * Creates a library component selector for a global Class component. Environment is Void (none), and not
     * available. The most appropriate logger for this class is automatically selected.
     *
     * @param <L> Library component type
     * @param owningClass Class for which this component is
     * @return Library component selector
     */
    public static <L extends LibraryComponent> LibraryComponentSelector<Void, L> forModule(Class<L> owningClass) {
        Plugin plugin = CommonUtil.getPluginByClass(owningClass);
        String identifier = owningClass.getSimpleName();
        if (plugin != null) {
            identifier = plugin.getName() + "." + identifier;
            return new LibraryComponentSelector<Void, L>(null, new ModuleLogger(plugin, owningClass.getSimpleName()), identifier);
        } else {
            return new LibraryComponentSelector<Void, L>(null, new ModuleLogger(owningClass.getSimpleName()), identifier);
        }
    }
}
