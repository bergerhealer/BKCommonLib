package com.bergerkiller.bukkit.common.internal.hooks;

import com.bergerkiller.mountiplex.reflection.ClassHook;

/**
 * Special hook class used on < MC 1.9, to 'catch' the missing Prepare Anvil Event
 */
public class LegacyContainerAnvilHook extends ClassHook<LegacyContainerAnvilHook> {
    public Runnable textChangeCallback = null;

    @HookMethod("public void a(String s)")
    public void onTextChange(String newText) {
        base.onTextChange(newText);
        if (textChangeCallback != null) {
            textChangeCallback.run();
        }
    }
}
