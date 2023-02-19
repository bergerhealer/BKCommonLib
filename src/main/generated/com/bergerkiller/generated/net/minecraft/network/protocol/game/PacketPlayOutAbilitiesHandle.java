package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutAbilities</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutAbilities")
public abstract class PacketPlayOutAbilitiesHandle extends PacketHandle {
    /** @See {@link PacketPlayOutAbilitiesClass} */
    public static final PacketPlayOutAbilitiesClass T = Template.Class.create(PacketPlayOutAbilitiesClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutAbilitiesHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PacketPlayOutAbilitiesHandle createNew(PlayerAbilities abilities) {
        return T.constr_abilities.newInstance(abilities);
    }

    /* ============================================================================== */

    public abstract boolean isInvulnerable();
    public abstract void setInvulnerable(boolean value);
    public abstract boolean isFlying();
    public abstract void setIsFlying(boolean value);
    public abstract boolean isCanFly();
    public abstract void setCanFly(boolean value);
    public abstract boolean isInstabuild();
    public abstract void setInstabuild(boolean value);
    public abstract float getFlyingSpeed();
    public abstract void setFlyingSpeed(float value);
    public abstract float getWalkingSpeed();
    public abstract void setWalkingSpeed(float value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutAbilities</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutAbilitiesClass extends Template.Class<PacketPlayOutAbilitiesHandle> {
        public final Template.Constructor.Converted<PacketPlayOutAbilitiesHandle> constr_abilities = new Template.Constructor.Converted<PacketPlayOutAbilitiesHandle>();

        public final Template.Field.Boolean invulnerable = new Template.Field.Boolean();
        public final Template.Field.Boolean isFlying = new Template.Field.Boolean();
        public final Template.Field.Boolean canFly = new Template.Field.Boolean();
        public final Template.Field.Boolean instabuild = new Template.Field.Boolean();
        public final Template.Field.Float flyingSpeed = new Template.Field.Float();
        public final Template.Field.Float walkingSpeed = new Template.Field.Float();

    }

}

