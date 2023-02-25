package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInSettings</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInSettings")
public abstract class PacketPlayInSettingsHandle extends PacketHandle {
    /** @see PacketPlayInSettingsClass */
    public static final PacketPlayInSettingsClass T = Template.Class.create(PacketPlayInSettingsClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInSettingsHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public HumanHand getMainHand() {
        if (T.mainHand.isAvailable()) {
            return T.mainHand.get(getRaw());
        } else {
            return HumanHand.RIGHT;
        }
    }

    public void setMainHand(HumanHand mainHand) {
        if (T.mainHand.isAvailable()) {
            T.mainHand.set(getRaw(), mainHand);
        }
    }
    public abstract String getLocale();
    public abstract void setLocale(String value);
    public abstract int getView();
    public abstract void setView(int value);
    public abstract Object getChatVisibility();
    public abstract void setChatVisibility(Object value);
    public abstract boolean isEnableColors();
    public abstract void setEnableColors(boolean value);
    public abstract int getModelPartFlags();
    public abstract void setModelPartFlags(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInSettings</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInSettingsClass extends Template.Class<PacketPlayInSettingsHandle> {
        public final Template.Field<String> locale = new Template.Field<String>();
        public final Template.Field.Integer view = new Template.Field.Integer();
        public final Template.Field.Converted<Object> chatVisibility = new Template.Field.Converted<Object>();
        public final Template.Field.Boolean enableColors = new Template.Field.Boolean();
        public final Template.Field.Integer modelPartFlags = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Converted<HumanHand> mainHand = new Template.Field.Converted<HumanHand>();

    }

}

