package com.bergerkiller.generated.com.mojang.authlib;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.com.mojang.authlib.properties.PropertyHandle;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>com.mojang.authlib.GameProfile</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("com.mojang.authlib.GameProfile")
public abstract class GameProfileHandle extends Template.Handle {
    /** @See {@link GameProfileClass} */
    public static final GameProfileClass T = Template.Class.create(GameProfileClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static GameProfileHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final GameProfileHandle createNew(UUID uuid, String name) {
        return T.constr_uuid_name.newInstance(uuid, name);
    }

    /* ============================================================================== */

    public abstract UUID getId();
    public abstract String getName();
    public abstract void clearProperties();
    public abstract Set<String> getPropertyKeys();
    public abstract Collection<PropertyHandle> getProperties(String key);
    public abstract boolean putProperty(String key, PropertyHandle property);
    public abstract void setAllProperties(GameProfileHandle profile);

    public static GameProfileHandle getForPlayer(org.bukkit.entity.HumanEntity player) {
        Object handle = com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toEntityHandle(player);
        return com.bergerkiller.generated.net.minecraft.server.EntityHumanHandle.T.gameProfile.get(handle);
    }
    /**
     * Stores class members for <b>com.mojang.authlib.GameProfile</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class GameProfileClass extends Template.Class<GameProfileHandle> {
        public final Template.Constructor.Converted<GameProfileHandle> constr_uuid_name = new Template.Constructor.Converted<GameProfileHandle>();

        public final Template.Method<UUID> getId = new Template.Method<UUID>();
        public final Template.Method<String> getName = new Template.Method<String>();
        public final Template.Method<Void> clearProperties = new Template.Method<Void>();
        public final Template.Method<Set<String>> getPropertyKeys = new Template.Method<Set<String>>();
        public final Template.Method.Converted<Collection<PropertyHandle>> getProperties = new Template.Method.Converted<Collection<PropertyHandle>>();
        public final Template.Method.Converted<Boolean> putProperty = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Void> setAllProperties = new Template.Method.Converted<Void>();

    }

}

