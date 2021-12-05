package com.bergerkiller.bukkit.common.component;

import com.bergerkiller.bukkit.common.bases.CheckedFunction;
import com.bergerkiller.bukkit.common.bases.CheckedSupplier;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;

/**
 * Defines a library or program component that can be individually enabled or disabled.
 * Depending on the environment or configuration, the component is either enabled or left
 * disabled.
 */
public interface LibraryComponent {

    /**
     * Enables this component
     */
    void enable() throws Throwable;

    /**
     * Disables this component
     */
    void disable() throws Throwable;

    /**
     * A conditional library component. Has a method to detect whether the component
     * should be enabled, and a method to create a new instance of the library component
     * if it should be enabled.
     *
     * @param <E> Input environment
     * @param <L> Type of library component
     */
    public static interface Conditional<E, L extends LibraryComponent> {

        /**
         * Gets a unique identifier of this conditional library component.
         * Is used when errors occur for logging purposes.
         * Please make sure this method cannot throw any exceptions. Ideally,
         * just return a String constant.
         *
         * @return Conditional library component identifier (name)
         */
        String getIdentifier();

        /**
         * Gets whether this conditional library component can be enabled
         *
         * @param environment Input environment
         * @return True if the library component should be enabled
         */
        boolean isSupported(E environment);

        /**
         * Creates a new instance of the LibraryComponent
         *
         * @param environment Input environment
         * @return Created library component, is not yet enabled
         */
        L create(E environment) throws Throwable;
    }

    /**
     * Creates a conditional library component that is only enabled when the current server
     * Minecraft version falls between the two versions specified, inclusive.
     *
     * @param <E> Environment type, ignored
     * @param <L> Library component type
     * @param identifier Unique identifier for the library component being added. The version
     *                   conditions are appended.
     * @param minimumMinecraftVersion Minimum supported Minecraft version. Null or empty String
     *                                if there is no minimum.
     * @param maximumMinecraftVersion Maximum supported Minecraft version. Null or empty String
     *                                if there is no maximum.
     * @param componentSupplier Constructor for a new instance of the library component. Is
     *                          called when the current Minecraft version is compatible.
     * @return Conditional library component
     */
    public static <E, L extends LibraryComponent> Conditional<E, L> forVersions(
            final String identifier,
            final String minimumMinecraftVersion,
            final String maximumMinecraftVersion,
            final CheckedSupplier<L> componentSupplier
    ) {
        return forVersions(identifier, minimumMinecraftVersion, maximumMinecraftVersion,
                e -> componentSupplier.get());
    }

    /**
     * Creates a conditional library component that is only enabled when the current server
     * Minecraft version falls between the two versions specified, inclusive.
     *
     * @param <E> Environment type, ignored
     * @param <L> Library component type
     * @param identifier Unique identifier for the library component being added. The version
     *                   conditions are appended.
     * @param minimumMinecraftVersion Minimum supported Minecraft version. Null or empty String
     *                                if there is no minimum.
     * @param maximumMinecraftVersion Maximum supported Minecraft version. Null or empty String
     *                                if there is no maximum.
     * @param componentFunction Constructor for a new instance of the library component. Is
     *                          called when the current Minecraft version is compatible, with
     *                          the environment as input.
     * @return Conditional library component
     */
    public static <E, L extends LibraryComponent> Conditional<E, L> forVersions(
            final String identifier,
            final String minimumMinecraftVersion,
            final String maximumMinecraftVersion,
            final CheckedFunction<E, L> componentFunction
    ) {
        // Create an identifier that includes the version details
        final String identifierWithVersion;
        {
            boolean hasMinimum = (minimumMinecraftVersion != null && !minimumMinecraftVersion.isEmpty());
            boolean hasMaximum = (maximumMinecraftVersion != null && !maximumMinecraftVersion.isEmpty());
            if (hasMinimum && hasMaximum) {
                identifierWithVersion = identifier + "[" + minimumMinecraftVersion + " - " + maximumMinecraftVersion + "]";
            } else if (hasMinimum) {
                identifierWithVersion = identifier + "[" + minimumMinecraftVersion + " AND LATER]";
            } else if (hasMaximum) {
                identifierWithVersion = identifier + "[" + maximumMinecraftVersion + " AND BEFORE]";
            } else {
                identifierWithVersion = identifier + "[ALWAYS]"; // Odd
            }
        }

        return new Conditional<E, L>() {
            @Override
            public String getIdentifier() {
                return identifierWithVersion;
            }

            @Override
            public boolean isSupported(E environment) {
                // Minimum
                if (minimumMinecraftVersion != null &&
                    !minimumMinecraftVersion.isEmpty() &&
                    !CommonBootstrap.evaluateMCVersion(">=", minimumMinecraftVersion)
                ) {
                    return false;
                }

                // Maximum
                if (maximumMinecraftVersion != null &&
                    !maximumMinecraftVersion.isEmpty() &&
                    !CommonBootstrap.evaluateMCVersion("<=", maximumMinecraftVersion)
                ) {
                    return false;
                }

                return true;
            }

            @Override
            public L create(E environment) throws Throwable {
                return componentFunction.apply(environment);
            }
        };
    }
}
