package com.bergerkiller.generated.net.minecraft.network.protocol.common;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.common.ServerboundClientInformationPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.common.ServerboundClientInformationPacket")
public abstract class ServerboundClientInformationPacketHandle extends PacketHandle {
    /** @see ServerboundClientInformationPacketClass */
    public static final ServerboundClientInformationPacketClass T = Template.Class.create(ServerboundClientInformationPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundClientInformationPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract String getLocale();
    public abstract void setLocale(String locale);
    public abstract int getView();
    public abstract void setView(int view);
    public abstract Object getChatVisibility();
    public abstract void setChatVisibility(Object visibility);
    public abstract boolean getEnableColors();
    public abstract void setEnableColors(boolean enable);
    public abstract int getModelPartFlags();
    public abstract void setModelPartFlags(int flags);
    public abstract HumanHand getMainHand();
    public abstract void setMainHand(HumanHand mainHand);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.common.ServerboundClientInformationPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundClientInformationPacketClass extends Template.Class<ServerboundClientInformationPacketHandle> {
        public final Template.Method<String> getLocale = new Template.Method<String>();
        public final Template.Method<Void> setLocale = new Template.Method<Void>();
        public final Template.Method<Integer> getView = new Template.Method<Integer>();
        public final Template.Method<Void> setView = new Template.Method<Void>();
        public final Template.Method<Object> getChatVisibility = new Template.Method<Object>();
        public final Template.Method.Converted<Void> setChatVisibility = new Template.Method.Converted<Void>();
        public final Template.Method<Boolean> getEnableColors = new Template.Method<Boolean>();
        public final Template.Method<Void> setEnableColors = new Template.Method<Void>();
        public final Template.Method<Integer> getModelPartFlags = new Template.Method<Integer>();
        public final Template.Method<Void> setModelPartFlags = new Template.Method<Void>();
        public final Template.Method.Converted<HumanHand> getMainHand = new Template.Method.Converted<HumanHand>();
        public final Template.Method.Converted<Void> setMainHand = new Template.Method.Converted<Void>();

    }

}

