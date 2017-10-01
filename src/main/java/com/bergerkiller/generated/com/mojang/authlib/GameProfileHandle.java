package com.bergerkiller.generated.com.mojang.authlib;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
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
public class GameProfileHandle extends Template.Handle {
    /** @See {@link GameProfileClass} */
    public static final GameProfileClass T = new GameProfileClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(GameProfileHandle.class, "com.mojang.authlib.GameProfile");

    /* ============================================================================== */

    public static GameProfileHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        GameProfileHandle handle = new GameProfileHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final GameProfileHandle createNew(UUID uuid, String name) {
        return T.constr_uuid_name.newInstance(uuid, name);
    }

    /* ============================================================================== */

    public UUID getId() {
        return T.getId.invoke(instance);
    }

    public String getName() {
        return T.getName.invoke(instance);
    }

    public void clearProperties() {
        T.clearProperties.invoke(instance);
    }

    public Set<String> getPropertyKeys() {
        return T.getPropertyKeys.invoke(instance);
    }

    public Collection<PropertyHandle> getProperties(String key) {
        return T.getProperties.invoke(instance, key);
    }

    public boolean putProperty(String key, PropertyHandle property) {
        return T.putProperty.invoke(instance, key, property);
    }

    public void setAllProperties(GameProfileHandle profile) {
        T.setAllProperties.invoke(instance, profile);
    }


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

