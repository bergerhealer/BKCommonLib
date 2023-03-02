package com.bergerkiller.bukkit.common.config.yaml;

/**
 * Translates absolute yaml paths to those relative to a certain node.
 * This is used to make the yaml path sent to a change listener relative
 * to the node the listener was registered on.
 */
class YamlChangeListenerRelative implements YamlChangeListener {
    private final YamlPath.Supplier rootPathSupplier;
    private final YamlChangeListener listener;

    private YamlChangeListenerRelative(YamlPath.Supplier rootPathSupplier, YamlChangeListener listener) {
        this.rootPathSupplier = rootPathSupplier;
        this.listener = listener;
    }

    @Override
    public void onNodeChanged(YamlPath path) {
        final YamlPath rootPath = this.rootPathSupplier.getYamlPath();

        // Common case of a root configuration (node) having a listener
        // In that case no extra logic is needed
        if (rootPath.isRoot()) {
            listener.onNodeChanged(path);
            return;
        }

        // Make the input path relative to rootPath
        // If this isn't possible, don't notify, it's probably being buggy
        YamlPath relativePath = path.makeRelative(rootPath);
        if (relativePath != null) {
            listener.onNodeChanged(relativePath);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof YamlChangeListenerRelative) {
            YamlChangeListenerRelative other = (YamlChangeListenerRelative) o;
            return this.listener.equals(other.listener);
        } else {
            return false;
        }
    }

    public static YamlChangeListener create(YamlPath.Supplier rootPathSupplier, YamlChangeListener listener) {
        return new YamlChangeListenerRelative(rootPathSupplier, listener);
    }
}
