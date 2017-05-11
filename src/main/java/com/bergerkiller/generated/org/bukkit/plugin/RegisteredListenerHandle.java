package com.bergerkiller.generated.org.bukkit.plugin;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.plugin.EventExecutor;

public class RegisteredListenerHandle extends Template.Handle {
    public static final RegisteredListenerClass T = new RegisteredListenerClass();


    public EventExecutor getExecutor() {
        return T.executor.get(instance);
    }

    public void setExecutor(EventExecutor value) {
        T.executor.set(instance, value);
    }

    public static class RegisteredListenerClass extends Template.Class {

        protected RegisteredListenerClass() {
            init(RegisteredListenerClass.class, "org.bukkit.plugin.RegisteredListener");
        }

        public final Template.Field<EventExecutor> executor = new Template.Field<EventExecutor>();

    }
}
