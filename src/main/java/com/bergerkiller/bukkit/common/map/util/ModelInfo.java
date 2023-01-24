package com.bergerkiller.bukkit.common.map.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bergerkiller.bukkit.common.map.util.Model.ModelOverride;

/**
 * The base {@link Model} information, such as the model name, parent, author, comments
 * and defined predicates that link to other models. No actual model information is loaded,
 * so no textures, boxes or block state details are available.<br>
 * <br>
 * Primarily useful for listing purposes.
 */
public class ModelInfo {
    private transient String name = "unknown";
    protected transient boolean placeholder = false;
    protected String parent = null;
    protected List<ModelOverride> overrides = new ArrayList<ModelOverride>();

    /*
     * Some metadata fields that aren't important for functional purposes
     * BlockBench uses "credit", Cubik Studio / Some dev tools use __comment
     */
    protected String credit;
    protected String __comment;

    /**
     * Gets the name (path) of this model. Is set when loading.
     *
     * @return model name
     */
    public final String getName() {
        return name;
    }

    /**
     * Sets the name (path) of this model. Is called by the loader, and shouldn't
     * be called by anyone else.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the parent model. This is the model that this model extends.
     *
     * @return Parent model name. <i>Null</i> if this model has no parent.
     */
    public final String getParentName() {
        return this.parent;
    }

    /**
     * Gets the credit information about this model. This is a short sentence describing
     * the author(s) of the model.
     *
     * @return Model credit information. Empty string if none is available.
     */
    public final String getCredit() {
        if (credit != null) {
            return credit;
        } else if (__comment != null) {
            return __comment;
        } else {
            return "";
        }
    }

    /**
     * Gets whether this Model is a placeholder model, because the original model could
     * not be found or loaded.
     *
     * @return True if this is a placeholder model
     */
    public final boolean isPlaceholder() {
        return placeholder;
    }

    /**
     * Gets a List of Model Overrides. These are predicates that, if fulfilled, display
     * a different model than this one.
     *
     * @return Overrides
     */
    public final List<ModelOverride> getOverrides() {
        List<ModelOverride> overrides = this.overrides;
        return (overrides == null || overrides.isEmpty()) ? Collections.emptyList() : overrides;
    }

    /**
     * Creates a placeholder model info
     *
     * @param name Name (path) of the model
     * @return Model Info
     */
    public static ModelInfo createPlaceholder(String name) {
        ModelInfo m = new ModelInfo();
        m.name = name;
        m.placeholder = true;
        return m;
    }
}
