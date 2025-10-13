package com.bergerkiller.bukkit.common.cloud.audiences;

/**
 * Type Adapter. These implementations exist:
 * <ul>
 * <li>net.kyori.adventure.key.Key</li>
 * <li>net.kyori.adventure.text.ComponentLike</li>
 * <li>net.kyori.adventure.text.Component</li>
 * <li>net.kyori.adventure.audience.MessageType</li>
 * <li>net.kyori.adventure.identity.Identified</li>
 * <li>net.kyori.adventure.identity.Identity</li>
 * <li>net.kyori.adventure.chat.ChatType.Bound</li>
 * <li>net.kyori.adventure.chat.SignedMessage</li>
 * <li>net.kyori.adventure.chat.SignedMessage.Signature</li>
 * <li>net.kyori.adventure.title.Title</li>
 * <li>net.kyori.adventure.title.TitlePart</li>
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
 *
 * @param <A> Type we know
 * @param <B> Unknown type
 */
public interface AudienceTypeAdapter<A, B> {
    /**
     * Adapts the type we know into the unknown type
     *
     * @param value Known type
     * @return Unknown type
     */
    B adapt(A value);
    /**
     * Restores the unknown type back into a type we know (reverse)
     *
     * @param value Unknown type
     * @return Known type
     */
    A restore(B value);
}
