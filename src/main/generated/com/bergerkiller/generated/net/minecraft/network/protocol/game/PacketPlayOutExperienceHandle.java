package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutExperience</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutExperience")
public abstract class PacketPlayOutExperienceHandle extends PacketHandle {
    /** @see PacketPlayOutExperienceClass */
    public static final PacketPlayOutExperienceClass T = Template.Class.create(PacketPlayOutExperienceClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutExperienceHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract float getExperienceProgress();
    public abstract void setExperienceProgress(float value);
    public abstract int getTotalExperience();
    public abstract void setTotalExperience(int value);
    public abstract int getExperienceLevel();
    public abstract void setExperienceLevel(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutExperience</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutExperienceClass extends Template.Class<PacketPlayOutExperienceHandle> {
        public final Template.Field.Float experienceProgress = new Template.Field.Float();
        public final Template.Field.Integer totalExperience = new Template.Field.Integer();
        public final Template.Field.Integer experienceLevel = new Template.Field.Integer();

    }

}

