package com.bergerkiller.generated.com.mojang.authlib;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.com.mojang.authlib.properties.PropertyHandle;
import com.google.common.collect.Multimap;
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
    /** @see GameProfileClass */
    public static final GameProfileClass T = Template.Class.create(GameProfileClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static GameProfileHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static GameProfileHandle createNew(UUID uuid, String name, Multimap<String, PropertyHandle> properties) {
        return T.createNew.invoke(uuid, name, properties);
    }

    public abstract UUID getId();
    public abstract String getName();
    public abstract Set<String> getPropertyKeys();
    public abstract Collection<PropertyHandle> getProperties(String key);
    public abstract Multimap<String, PropertyHandle> getMutableProperties();
    public static GameProfileHandle createNew(UUID uuid, String name) {
        return createNew(uuid, name, com.google.common.collect.ImmutableMultimap.of());
    }

    public GameProfileHandle withProperties(Multimap<String, PropertyHandle> properties) {
        return createNew(getId(), getName(), properties);
    }

    public GameProfileHandle withPropertiesOf(GameProfileHandle profile) {
        return withProperties(profile.getMutableProperties());
    }

    public GameProfileHandle withPropertiesChanged(java.util.function.Consumer<Multimap<String, PropertyHandle>> mutator) {
        Multimap<String, PropertyHandle> properties = getMutableProperties();
        mutator.accept(properties);
        return withProperties(properties);
    }

    public GameProfileHandle withPropertyPut(String key, PropertyHandle property) {
        return withPropertiesChanged(p -> p.put(key, property));
    }

    public static GameProfileHandle getForPlayer(org.bukkit.entity.HumanEntity player) {
        Object handle = com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toEntityHandle(player);
        return com.bergerkiller.generated.net.minecraft.world.entity.player.EntityHumanHandle.T.gameProfile.get(handle);
    }
    /**
     * Stores class members for <b>com.mojang.authlib.GameProfile</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class GameProfileClass extends Template.Class<GameProfileHandle> {
        public final Template.StaticMethod.Converted<GameProfileHandle> createNew = new Template.StaticMethod.Converted<GameProfileHandle>();

        public final Template.Method<UUID> getId = new Template.Method<UUID>();
        public final Template.Method<String> getName = new Template.Method<String>();
        public final Template.Method<Set<String>> getPropertyKeys = new Template.Method<Set<String>>();
        public final Template.Method.Converted<Collection<PropertyHandle>> getProperties = new Template.Method.Converted<Collection<PropertyHandle>>();
        public final Template.Method<Multimap<String, PropertyHandle>> getMutableProperties = new Template.Method<Multimap<String, PropertyHandle>>();

    }

}

