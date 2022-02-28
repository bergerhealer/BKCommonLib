package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.potion.PotionEffect;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutEntityEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutEntityEffect")
public abstract class PacketPlayOutEntityEffectHandle extends PacketHandle {
    /** @See {@link PacketPlayOutEntityEffectClass} */
    public static final PacketPlayOutEntityEffectClass T = Template.Class.create(PacketPlayOutEntityEffectClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutEntityEffectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PacketPlayOutEntityEffectHandle createNew(int entityId, PotionEffect mobeffect) {
        return T.constr_entityId_mobeffect.newInstance(entityId, mobeffect);
    }

    /* ============================================================================== */

    public abstract int getEffectId();
    public abstract void setEffectId(int id);

    public static final int FLAG_AMBIENT = 1;
    public static final int FLAG_VISIBLE = 2;
    public static final int FLAG_SHOW_ICON = 4;
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract byte getEffectAmplifier();
    public abstract void setEffectAmplifier(byte value);
    public abstract int getEffectDurationTicks();
    public abstract void setEffectDurationTicks(int value);
    public abstract byte getFlags();
    public abstract void setFlags(byte value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutEntityEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutEntityEffectClass extends Template.Class<PacketPlayOutEntityEffectHandle> {
        public final Template.Constructor.Converted<PacketPlayOutEntityEffectHandle> constr_entityId_mobeffect = new Template.Constructor.Converted<PacketPlayOutEntityEffectHandle>();

        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Byte effectAmplifier = new Template.Field.Byte();
        public final Template.Field.Integer effectDurationTicks = new Template.Field.Integer();
        public final Template.Field.Byte flags = new Template.Field.Byte();

        public final Template.Method<Integer> getEffectId = new Template.Method<Integer>();
        public final Template.Method<Void> setEffectId = new Template.Method<Void>();

    }

}

