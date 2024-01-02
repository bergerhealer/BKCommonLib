package com.bergerkiller.generated.net.minecraft.advancements;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.advancement.Advancement;

/**
 * Instance wrapper handle for type <b>net.minecraft.advancements.Advancement</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.advancements.Advancement")
public abstract class AdvancementHandle extends Template.Handle {
    /** @see AdvancementClass */
    public static final AdvancementClass T = Template.Class.create(AdvancementClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static AdvancementHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Advancement toBukkit(Object advancement_or_holder) {
        return T.toBukkit.invoker.invoke(null,advancement_or_holder);
    }

    /**
     * Stores class members for <b>net.minecraft.advancements.Advancement</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class AdvancementClass extends Template.Class<AdvancementHandle> {
        public final Template.StaticMethod<Advancement> toBukkit = new Template.StaticMethod<Advancement>();

    }

}

