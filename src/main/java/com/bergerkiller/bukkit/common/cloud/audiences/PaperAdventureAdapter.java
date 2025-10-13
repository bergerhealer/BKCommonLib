package com.bergerkiller.bukkit.common.cloud.audiences;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * Converts types from Adventure between the BKCommonLib shaded types and the
 * types declared by Paper in the server.<br>
 * <br>
 * These Adventure types can be adapted:
 * <ul>
 *     <li>net.kyori.adventure.key.audience.Audience</li>
 *     <li>net.kyori.adventure.key.Key</li>
 *     <li>net.kyori.adventure.text.Component</li>
 *     <li>net.kyori.adventure.audience.MessageType</li>
 *     <li>net.kyori.adventure.identity.Identity</li>
 *     <li>net.kyori.adventure.chat.ChatType.Bound</li>
 *     <li>net.kyori.adventure.chat.SignedMessage</li>
 *     <li>net.kyori.adventure.title.TitlePart</li>
 * </ul>
 *
 * These are not implemented and these functionalities will not work as a result:
 * <ul>
 *     <li>net.kyori.adventure.bossbar.BossBar</li>
 *     <li>net.kyori.adventure.sound.Sound</li>
 *     <li>net.kyori.adventure.sound.SoundStop</li>
 *     <li>net.kyori.adventure.sound.Sound.Emitter</li>
 *     <li>net.kyori.adventure.inventory.Book.Builder</li>
 *     <li>net.kyori.adventure.inventory.Book</li>
 *     <li>net.kyori.adventure.resource.ResourcePackInfoLike</li>
 *     <li>net.kyori.adventure.resource.ResourcePackRequestLike</li>
 *     <li>net.kyori.adventure.resource.ResourcePackRequest</li>
 *     <li>net.kyori.adventure.dialog.DialogLike</li>
 * </ul>
 */
@Template.Import("com.bergerkiller.bukkit.common.cloud.audiences.PaperAdventureAdapter")
@Template.InstanceType("net.kyori.adventure.audience.Audience")
public abstract class PaperAdventureAdapter extends Template.Class<Template.Handle> {
    private final Function<String, Class<?>> adventureClassLoader = getAdventureClassLoader();
    private final Map<Class<?>, TypeAdapter> typeAdapters = new HashMap<>();

    public final SignedMessageFactory<Object> adaptSignedMessageFactory =
            new SignedMessageFactoryImpl<>(getAdventureClass(
                    /* String join to avoid the gradle remapper changing it */
                    String.join(".", "net", "kyori", "adventure", "chat", "SignedMessage")));
    public final SignedMessageFactory<net.kyori.adventure.chat.SignedMessage> reverseSignedMessageFactory =
            new SignedMessageFactoryImpl<>(net.kyori.adventure.chat.SignedMessage.class);
    public final AudienceAdapter adaptAudience = new AudienceAdapterImpl(this);
    public final AudienceProviderAdapter adaptAudienceProvider = new AudienceProviderAdapterImpl(this);

    public PaperAdventureAdapter() {
        // Register all the pass-through (no adapting) types that can be safely used
        // When these types are encountered, it is assumed the adapted method will use the same type
        BoxedType.getUnboxedTypes().forEach(this::registerPassThroughAdapter);
        BoxedType.getBoxedTypes().forEach(this::registerPassThroughAdapter);
        registerPassThroughAdapter(UUID.class);
        registerPassThroughAdapter(String.class);
        registerPassThroughAdapter(Predicate.class);
        registerPassThroughAdapter(Object.class);

        // Register all the type adapters that this class supports
        registerStandardAdapter(
                net.kyori.adventure.key.Key.class,
                String.join(".", "net", "kyori", "adventure", "key", "Key"),
                "adaptKey", "reverseKey");
        registerStandardAdapter(
                net.kyori.adventure.identity.Identity.class,
                String.join(".", "net", "kyori", "adventure", "identity", "Identity"),
                "adaptIdentity", "reverseIdentity");
        registerStandardAdapter(
                net.kyori.adventure.text.Component.class,
                String.join(".", "net", "kyori", "adventure", "text", "Component"),
                "adaptComponent", "reverseComponent");
        registerStandardAdapter(
                net.kyori.adventure.audience.Audience.class,
                String.join(".", "net", "kyori", "adventure", "audience", "Audience"),
                "adaptAudience", "reverseAudience");
        registerStandardAdapter(
                net.kyori.adventure.platform.AudienceProvider.class,
                String.join(".", "net", "kyori", "adventure", "platform", "AudienceProvider"),
                "adaptAudienceProvider", "reverseAudienceProvider");
    }

    /**
     * Retrieves a (foreign) Adventure library class by name
     *
     * @param name Name of the class
     * @return Class
     * @throws UnsupportedOperationException If this type does not exist (somehow?)
     */
    public final Class<?> getAdventureClass(String name) {
        return adventureClassLoader.apply(name);
    }

    /**
     * Gets type adapter information for an API Type.
     * Returns null if the type is unknown and not supported for adapting.
     *
     * @param type API Type
     * @return TypeAdapter
     */
    public final TypeAdapter getTypeAdapter(Class<?> type) {
        return typeAdapters.get(type);
    }

    /*
     * <ADAPT_KEY>
     * public static net.kyori.adventure.key.Key adaptKey(com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key key) {
     *     return net.kyori.adventure.key.Key.key(key.namespace(), key.value());
     * }
     */
    @Template.Generated("%ADAPT_KEY%")
    public abstract Object adaptKey(net.kyori.adventure.key.Key key);

    /*
     * <REVERSE_KEY>
     * public static com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key reverseKey(net.kyori.adventure.key.Key key) {
     *     return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key.key(key.namespace(), key.value());
     * }
     */
    @Template.Generated("%REVERSE_KEY%")
    public abstract net.kyori.adventure.key.Key reverseKey(Object key);

    /*
     * <ADAPT_MESSAGE_TYPE>
     * public static net.kyori.adventure.audience.MessageType adaptMessageType(com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.MessageType messageType) {
     *     if (messageType == com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.MessageType.SYSTEM) {
     *         return net.kyori.adventure.audience.MessageType.SYSTEM;
     *     } else {
     *         return net.kyori.adventure.audience.MessageType.CHAT;
     *     }
     * }
     */
    @Template.Generated("%ADAPT_MESSAGE_TYPE%")
    public abstract Object adaptMessageType(net.kyori.adventure.key.Key messageType);

    /*
     * <REVERSE_MESSAGE_TYPE>
     * public static com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.MessageType reverseMessageType(net.kyori.adventure.audience.MessageType messageType) {
     *     if (messageType == net.kyori.adventure.audience.MessageType.SYSTEM) {
     *         return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.MessageType.SYSTEM;
     *     } else {
     *         return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.MessageType.CHAT;
     *     }
     * }
     */
    @Template.Generated("%REVERSE_MESSAGE_TYPE%")
    public abstract net.kyori.adventure.key.Key reverseMessageType(Object messageType);

    /*
     * <ADAPT_IDENTITY>
     * public static net.kyori.adventure.identity.Identity adaptIdentity(com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identity identity) {
     *     if (identity == null) {
     *         return null;
     *     } else if (identity == com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identity.nil()) {
     *         return net.kyori.adventure.identity.Identity.nil();
     *     } else {
     *         return net.kyori.adventure.identity.Identity.identity(identity.uuid());
     *     }
     * }
     */
    @Template.Generated("%ADAPT_IDENTITY%")
    public abstract Object adaptIdentity(net.kyori.adventure.identity.Identity identity);

    /*
     * <REVERSE_IDENTITY>
     * public static com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identity reverseIdentity(net.kyori.adventure.identity.Identity identity) {
     *     if (identity == null) {
     *         return null;
     *     } else if (identity == net.kyori.adventure.identity.Identity.nil()) {
     *         return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identity.nil();
     *     } else {
     *         return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identity.identity(identity.uuid());
     *     }
     * }
     */
    @Template.Generated("%REVERSE_IDENTITY%")
    public abstract net.kyori.adventure.identity.Identity reverseIdentity(Object identity);

    /*
     * <ADAPT_COMPONENT>
     * public static net.kyori.adventure.text.Component adaptComponent(com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component component) {
     *     if (component == null)
     *         return null;
     *     if (component == com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component.empty())
     *         return net.kyori.adventure.text.Component.empty();
     *
     *     String json = (String) com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(component);
     *
     *     return (net.kyori.adventure.text.Component)
     *         net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().deserialize(json);
     * }
     */
    @Template.Generated("%ADAPT_COMPONENT%")
    public abstract Object adaptComponent(net.kyori.adventure.text.Component component);

    /*
     * <REVERSE_COMPONENT>
     * public static com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component reverseComponent(net.kyori.adventure.text.Component component) {
     *     if (component == null)
     *         return null;
     *     if (component == net.kyori.adventure.text.Component.empty())
     *         return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component.empty();
     *
     *     String json = (String) net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(component);
     *
     *     return (com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component)
     *         com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().deserialize(json);
     * }
     */
    @Template.Generated("%REVERSE_COMPONENT%")
    public abstract net.kyori.adventure.text.Component reverseComponent(Object component);

    /*
     * <ADAPT_CHAT_TYPE>
     * public static net.kyori.adventure.chat.ChatType adaptChatType(com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType chatType) {
     * #if version >= 1.19
     *     if (chatType == com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.CHAT) {
     *         return net.kyori.adventure.chat.ChatType.CHAT;
     *     } else if (chatType == com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.SAY_COMMAND) {
     *         return net.kyori.adventure.chat.ChatType.SAY_COMMAND;
     *     } else if (chatType == com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.MSG_COMMAND_INCOMING) {
     *         return net.kyori.adventure.chat.ChatType.MSG_COMMAND_INCOMING;
     *     } else if (chatType == com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.MSG_COMMAND_OUTGOING) {
     *         return net.kyori.adventure.chat.ChatType.MSG_COMMAND_OUTGOING;
     *     } else if (chatType == com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.TEAM_MSG_COMMAND_INCOMING) {
     *         return net.kyori.adventure.chat.ChatType.TEAM_MSG_COMMAND_INCOMING;
     *     } else if (chatType == com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.TEAM_MSG_COMMAND_OUTGOING) {
     *         return net.kyori.adventure.chat.ChatType.TEAM_MSG_COMMAND_OUTGOING;
     *     } else if (chatType == com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.EMOTE_COMMAND) {
     *         return net.kyori.adventure.chat.ChatType.EMOTE_COMMAND;
     *     } else {
     *         com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key key = chatType.key();
     *         net.kyori.adventure.key.Key adaptedKey =
     *                     net.kyori.adventure.key.Key.key(key.namespace(), key.value());
     *         return net.kyori.adventure.chat.ChatType.chatType(adaptedKey);
     *     }
     * #else
     *     return null;
     * #endif
     * }
     */
    @Template.Generated("%ADAPT_CHAT_TYPE%")
    public abstract Object adaptChatType(net.kyori.adventure.chat.ChatType chatType);

    /*
     * <REVERSE_CHAT_TYPE>
     * public static com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType reverseChatType(net.kyori.adventure.chat.ChatType chatType) {
     * #if version >= 1.19
     *     if (chatType == net.kyori.adventure.chat.ChatType.CHAT) {
     *         return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.CHAT;
     *     } else if (chatType == net.kyori.adventure.chat.ChatType.SAY_COMMAND) {
     *         return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.SAY_COMMAND;
     *     } else if (chatType == net.kyori.adventure.chat.ChatType.MSG_COMMAND_INCOMING) {
     *         return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.MSG_COMMAND_INCOMING;
     *     } else if (chatType == net.kyori.adventure.chat.ChatType.MSG_COMMAND_OUTGOING) {
     *         return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.MSG_COMMAND_OUTGOING;
     *     } else if (chatType == net.kyori.adventure.chat.ChatType.TEAM_MSG_COMMAND_INCOMING) {
     *         return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.TEAM_MSG_COMMAND_INCOMING;
     *     } else if (chatType == net.kyori.adventure.chat.ChatType.TEAM_MSG_COMMAND_OUTGOING) {
     *         return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.TEAM_MSG_COMMAND_OUTGOING;
     *     } else if (chatType == net.kyori.adventure.chat.ChatType.EMOTE_COMMAND) {
     *         return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.EMOTE_COMMAND;
     *     } else {
     *         net.kyori.adventure.key.Key key = chatType.key();
     *         com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key reversedKey =
     *                     com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key.key(key.namespace(), key.value());
     *         return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.chatType(reversedKey);
     *     }
     * #else
     *     return null;
     * #endif
     * }
     */
    @Template.Generated("%REVERSE_CHAT_TYPE%")
    public abstract net.kyori.adventure.chat.ChatType reverseChatType(Object chatType);

    /*
     * <ADAPT_BOUND_CHAT_TYPE>
     * public static net.kyori.adventure.chat.ChatType.Bound reverseChatType(PaperAdventureAdapter adapter, com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.Bound boundChatType) {
     * #if version >= 1.19
     *     net.kyori.adventure.chat.ChatType type = (net.kyori.adventure.chat.ChatType) adapter.adaptChatType(boundChatType.type());
     *     net.kyori.adventure.text.Component name = (net.kyori.adventure.text.Component) adapter.adaptComponent(boundChatType.name());
     *     net.kyori.adventure.text.Component target = (net.kyori.adventure.text.Component) adapter.adaptComponent(boundChatType.target());
     *     return type.bind(name, target);
     * #else
     *     return null;
     * #endif
     * }
     */
    @Template.Generated("%ADAPT_BOUND_CHAT_TYPE%")
    public abstract Object adaptBoundChatType(PaperAdventureAdapter adapter, net.kyori.adventure.chat.ChatType.Bound boundChatType);

    public final Object adaptBoundChatType(net.kyori.adventure.chat.ChatType.Bound boundChatType) {
        return adaptBoundChatType(this, boundChatType);
    }

    /*
     * <REVERSE_BOUND_CHAT_TYPE>
     * public static com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType.Bound reverseChatType(PaperAdventureAdapter adapter, net.kyori.adventure.chat.ChatType.Bound boundChatType) {
     * #if version >= 1.19
     *     com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType type = adapter.reverseChatType(boundChatType.type());
     *     com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component name = adapter.reverseComponent(boundChatType.name());
     *     com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component target = adapter.reverseComponent(boundChatType.target());
     *     return type.bind(name, target);
     * #else
     *     return null;
     * #endif
     * }
     */
    @Template.Generated("%REVERSE_BOUND_CHAT_TYPE%")
    public abstract net.kyori.adventure.chat.ChatType.Bound reverseBoundChatType(PaperAdventureAdapter adapter, Object boundChatType);

    public final net.kyori.adventure.chat.ChatType.Bound reverseBoundChatType(Object boundChatType) {
        return reverseBoundChatType(this, boundChatType);
    }

    /*
     * <ADAPT_SIGNED_MESSAGE_SIGNATURE>
     * public static net.kyori.adventure.chat.SignedMessage.Signature adaptSignedMessageSignature(com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.SignedMessage.Signature signature) {
     *     if (signature == null)
     *         return null;
     *     return net.kyori.adventure.chat.SignedMessage.signature(signature.bytes());
     * }
     */
    @Template.Generated("%ADAPT_SIGNED_MESSAGE_SIGNATURE%")
    public abstract Object adaptSignedMessageSignature(net.kyori.adventure.chat.SignedMessage.Signature signature);

    /*
     * <REVERSE_SIGNED_MESSAGE_SIGNATURE>
     * public static com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.SignedMessage.Signature reverseSignedMessageSignature(net.kyori.adventure.chat.SignedMessage.Signature signature) {
     *     if (signature == null)
     *         return null;
     *     return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.SignedMessage.signature(signature.bytes());
     * }
     */
    @Template.Generated("%REVERSE_SIGNED_MESSAGE_SIGNATURE%")
    public abstract net.kyori.adventure.chat.SignedMessage.Signature reverseSignedMessageSignature(Object signature);

    /*
     * <ADAPT_SIGNED_MESSAGE>
     * public static net.kyori.adventure.chat.SignedMessage adaptSignedMessage(PaperAdventureAdapter adapter, com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.SignedMessage signedMessage) {
     *     if (signedMessage == null)
     *         return null;
     *
     *     java.time.Instant timestamp = signedMessage.timestamp();
     *     long salt = signedMessage.salt();
     *     String message = signedMessage.message();
     *
     *     net.kyori.adventure.chat.SignedMessage$Signature signature =
     *                     (net.kyori.adventure.chat.SignedMessage$Signature) adapter.adaptSignedMessageSignature(signedMessage.signature());
     *     net.kyori.adventure.text.Component unsignedContent =
     *                     (net.kyori.adventure.text.Component) adapter.adaptComponent(signedMessage.unsignedContent());
     *     net.kyori.adventure.identity.Identity identity =
     *                     (net.kyori.adventure.identity.Identity) adapter.adaptIdentity(signedMessage.identity());
     *
     *     return (net.kyori.adventure.chat.SignedMessage) adapter.adaptSignedMessageFactory.create(
     *             timestamp, salt, message, signature, unsignedContent, identity );
     * }
     */
    @Template.Generated("%ADAPT_SIGNED_MESSAGE%")
    public abstract Object adaptSignedMessage(PaperAdventureAdapter adapter, net.kyori.adventure.chat.SignedMessage signedMessage);

    public final Object adaptSignedMessage(net.kyori.adventure.chat.SignedMessage signedMessage) {
        return adaptSignedMessage(this, signedMessage);
    }

    /*
     * <REVERSE_SIGNED_MESSAGE>
     * public static com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.SignedMessage reverseSignedMessage(PaperAdventureAdapter adapter, net.kyori.adventure.chat.SignedMessage signedMessage) {
     *     if (signedMessage == null)
     *         return null;
     *
     *     java.time.Instant timestamp = signedMessage.timestamp();
     *     long salt = signedMessage.salt();
     *     String message = signedMessage.message();
     *
     *     com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.SignedMessage$Signature signature =
     *                     adapter.reverseSignedMessageSignature(signedMessage.signature());
     *     com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component unsignedContent =
     *                     adapter.reverseComponent(signedMessage.unsignedContent());
     *     com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identity identity =
     *                     adapter.reverseIdentity(signedMessage.identity());
     *
     *     return (com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.SignedMessage) adapter.reverseSignedMessageFactory.create(
     *             timestamp, salt, message, signature, unsignedContent, identity );
     * }
     */
    @Template.Generated("%REVERSE_SIGNED_MESSAGE%")
    public abstract net.kyori.adventure.chat.SignedMessage reverseSignedMessage(PaperAdventureAdapter adapter, Object signedMessage);

    public final net.kyori.adventure.chat.SignedMessage reverseSignedMessage(Object signedMessage) {
        return reverseSignedMessage(this, signedMessage);
    }

    /*
     * <ADAPT_TITLE>
     * public static net.kyori.adventure.title.Title adaptTitle(PaperAdventureAdapter adapter, com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.Title title) {
     *     if (title == null)
     *         return null;
     *
     *     // Adapt times
     *     com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.Title$Times origTimes = title.times();
     *     net.kyori.adventure.title.Title$Times times;
     *     if (origTimes == null) {
     *         times = null;
     *     } else if (origTimes == com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.Title.DEFAULT_TIMES) {
     *         times = net.kyori.adventure.title.Title.DEFAULT_TIMES;
     *     } else {
     *         times = net.kyori.adventure.title.Title$Times.times(
     *                         origTimes.fadeIn(), origTimes.stay(), origTimes.fadeOut());
     *     }
     *
     *     // Adapt the other elements
     *     net.kyori.adventure.text.Component titleText =
     *                     (net.kyori.adventure.text.Component) adapter.adaptComponent(title.title());
     *     net.kyori.adventure.text.Component subTitleText =
     *                     (net.kyori.adventure.text.Component) adapter.adaptComponent(title.subtitle());
     *
     *     // Build
     *     return net.kyori.adventure.title.Title.title(titleText, subTitleText, times);
     * }
     */
    @Template.Generated("%ADAPT_TITLE%")
    public abstract Object adaptTitle(PaperAdventureAdapter adapter, net.kyori.adventure.title.Title title);

    public final Object adaptTitle(net.kyori.adventure.title.Title title) {
        return adaptTitle(this, title);
    }

    /*
     * <REVERSE_TITLE>
     * public static com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.Title reverseTitle(PaperAdventureAdapter adapter, net.kyori.adventure.title.Title title) {
     *     if (title == null)
     *         return null;
     *
     *     // Reverse times
     *     net.kyori.adventure.title.Title$Times origTimes = title.times();
     *     com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.Title$Times times;
     *     if (origTimes == null) {
     *         times = null;
     *     } else if (origTimes == net.kyori.adventure.title.Title.DEFAULT_TIMES) {
     *         times = com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.Title.DEFAULT_TIMES;
     *     } else {
     *         times = com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.Title$Times.times(
     *                         origTimes.fadeIn(), origTimes.stay(), origTimes.fadeOut());
     *     }
     *
     *     // Reverse the other elements
     *     com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component titleText =
     *                     adapter.reverseComponent(title.title());
     *     com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component subTitleText =
     *                     adapter.reverseComponent(title.subtitle());
     *
     *     // Build
     *     return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.Title.title(titleText, subTitleText, times);
     * }
     */
    @Template.Generated("%REVERSE_TITLE%")
    public abstract net.kyori.adventure.title.Title reverseTitle(PaperAdventureAdapter adapter, Object title);

    public final net.kyori.adventure.title.Title reverseTitle(Object title) {
        return reverseTitle(this, title);
    }

    /*
     * <ADAPT_TITLE_PART>
     * public static net.kyori.adventure.title.TitlePart<?> adaptMessageType(com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.TitlePart<?> titlePart) {
     *     if (titlePart == null) {
     *         return null;
     *     } else if (titlePart == com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.TitlePart.TITLE) {
     *         return net.kyori.adventure.title.TitlePart.TITLE;
     *     } else if (titlePart == com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.TitlePart.SUBTITLE) {
     *         return net.kyori.adventure.title.TitlePart.SUBTITLE;
     *     } else if (titlePart == com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.TitlePart.TIMES) {
     *         return net.kyori.adventure.title.TitlePart.TIMES;
     *     } else {
     *         throw new IllegalArgumentException("Unknown title part: " + titlePart);
     *     }
     * }
     */
    @Template.Generated("%ADAPT_TITLE_PART%")
    public abstract Object adaptTitlePart(net.kyori.adventure.title.TitlePart<?> titlePart);

    /*
     * <REVERSE_TITLE_PART>
     * public static com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.TitlePart<?> reverseMessageType(net.kyori.adventure.title.TitlePart<?> titlePart) {
     *     if (titlePart == null) {
     *         return null;
     *     } else if (titlePart == net.kyori.adventure.title.TitlePart.TITLE) {
     *         return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.TitlePart.TITLE;
     *     } else if (titlePart == net.kyori.adventure.title.TitlePart.SUBTITLE) {
     *         return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.TitlePart.SUBTITLE;
     *     } else if (titlePart == net.kyori.adventure.title.TitlePart.TIMES) {
     *         return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.TitlePart.TIMES;
     *     } else {
     *         throw new IllegalArgumentException("Unknown title part: " + titlePart);
     *     }
     * }
     */
    @Template.Generated("%REVERSE_TITLE_PART%")
    public abstract <T> net.kyori.adventure.title.TitlePart<T> reverseTitlePart(Object titlePart);

    public final net.kyori.adventure.audience.Audience reverseAudience(Object audience) {
        return adaptAudience.reverseAudience(audience);
    }

    public final net.kyori.adventure.platform.AudienceProvider reverseAudienceProvider(Object audienceProvider) {
        return adaptAudienceProvider.reverseAudienceProvider(audienceProvider);
    }

    /**
     * Registers a Type Adapter that performs no adapting at all, passing it through.
     *
     * @param type Type
     */
    private void registerPassThroughAdapter(
            final Class<?> type
    ) {
        registerTypeAdapter(new TypeAdapter() {
            @Override
            public Class<?> getType() {
                return type;
            }

            @Override
            public Class<?> getAdaptedType() {
                return type;
            }

            @Override
            public int applyParameter(ExtendedClassWriter<?> cw, MethodVisitor mv, int register) {
                return MPLType.visitVarILoad(mv, register, type);
            }

            @Override
            public void applyReturn(ExtendedClassWriter<?> cw, MethodVisitor mv) {
            }
        });
    }

    private void registerStandardAdapter(
            final Class<?> type, /* For example: net.kyori.adventure.title.TitlePart.class */
            final String adaptedTypeName, /* For example: "net.kyori.adventure.title.TitlePart" */
            final String adaptMethodName, /* For example: "adaptTitlePart" */
            final String reverseMethodName /* For example: "reverseTitlePart" */
    ) {
        final Class<?> adaptedType = getAdventureClass(adaptedTypeName);
        registerTypeAdapter(new TypeAdapter() {
            @Override
            public Class<?> getType() {
                return type;
            }

            @Override
            public Class<?> getAdaptedType() {
                return adaptedType;
            }

            @Override
            public int applyParameter(ExtendedClassWriter<?> cw, MethodVisitor mv, int register) {
                // Access 'adapter' field of the adapter class we generate
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "adapter", MPLType.getDescriptor(PaperAdventureAdapter.class));

                // Load the parameter from the stack, and adapt it
                int nextRegister = MPLType.visitVarILoad(mv, register, type);
                mv.visitMethodInsn(INVOKEVIRTUAL, MPLType.getInternalName(PaperAdventureAdapter.class),
                        adaptMethodName, "(" + MPLType.getDescriptor(type) + ")Ljava/lang/Object;", false);
                mv.visitTypeInsn(CHECKCAST, MPLType.getInternalName(adaptedType));
                return nextRegister;
            }

            @Override
            public void applyReturn(ExtendedClassWriter<?> cw, MethodVisitor mv) {
                mv.visitMethodInsn(INVOKEVIRTUAL, MPLType.getInternalName(PaperAdventureAdapter.class), reverseMethodName,
                        "(Ljava/lang/Object;)" + MPLType.getDescriptor(type), false);
            }
        });
    }

    private void registerTypeAdapter(TypeAdapter typeAdapter) {
        typeAdapters.put(typeAdapter.getType(), typeAdapter);
    }

    /**
     * Looks up the (foreign) Adventure library Class and then initializes a loader for other
     * types in the Adventure library.
     *
     * @return Loader
     */
    private static Function<String, Class<?>> getAdventureClassLoader() {
        final String prefix;
        final ClassLoader classLoader;
        try {
            String audienceName = String.join(".", "net", "kyori", "adventure", "audience", "Audience");
            Class<?> audienceType = Class.forName(audienceName);
            prefix = audienceType.getName().endsWith(audienceName)
                    ? audienceType.getName().substring(0, audienceType.getName().length() - audienceName.length())
                    : "";
            classLoader = audienceType.getClassLoader();
        } catch (ClassNotFoundException ex) {
            throw new UnsupportedOperationException("Native Adventure library could not be found on the server");
        }

        return name -> {
            try {
                return Class.forName(prefix + name, false, classLoader);
            } catch (ClassNotFoundException ex) {
                throw new UnsupportedOperationException("Adventure library type not found: " + name);
            }
        };
    }

    public interface SignedMessageFactory<T> {
        T create(
                final java.time.Instant timestamp,
                final long salt,
                final String message,
                final Object /* net.kyori.adventure.chat.SignedMessage$Signature */ signature,
                final Object /* net.kyori.adventure.text.Component */ unsignedContent,
                final Object /* net.kyori.adventure.identity.Identity */ identity
        );
    }

    public interface AudienceAdapter {
        net.kyori.adventure.audience.Audience reverseAudience(
                final Object /* net.kyori.adventure.audience.Audience */ audience
        );
    }

    public interface AudienceProviderAdapter {
        net.kyori.adventure.platform.AudienceProvider reverseAudienceProvider(
                final Object /* net.kyori.adventure.platform.AudienceProvider */ audienceProvider
        );
    }

    /**
     * A single type adapter responsible for converting from a public API Type to an adapted
     * (foreign) Adventure type. Handles generating the required ASM for converting
     * parameter types before calling the foreign method and converting the method result
     * back into API types.
     */
    public interface TypeAdapter {
        /**
         * Gets the API type (known to BKCommonLib at compile time)
         *
         * @return API Type
         */
        Class<?> getType();

        /**
         * Gets the adapted type (not known to BKCommonLib at compile time)
         *
         * @return Adapted Type
         */
        Class<?> getAdaptedType();

        /**
         * Adapts the type from API type to Adapted Type for a method parameter
         *
         * @param cw ExtendedClassWriter
         * @param mv MethodVisitor
         * @param register Parameter register position
         * @return Parameter register position for the next parameter (register + size)
         */
        int applyParameter(ExtendedClassWriter<?> cw, MethodVisitor mv, int register);

        /**
         * Adapts the type from Adapted Type to API type for a return value
         *
         * @param cw ExtendedClassWriter
         * @param mv MethodVisitor
         */
        void applyReturn(ExtendedClassWriter<?> cw, MethodVisitor mv);
    }
}
