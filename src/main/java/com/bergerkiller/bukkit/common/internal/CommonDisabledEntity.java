package com.bergerkiller.bukkit.common.internal;

import java.lang.reflect.Method;

import com.bergerkiller.generated.net.minecraft.server.DataWatcherObjectHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityItemHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.Invokable;

public class CommonDisabledEntity {
    public static final EntityHandle INSTANCE;

    static {
        Object entity = EntityItemHandle.T.newInstanceNull();

        // Since Minecraft 1.14 the Pose datawatcher item is used to refresh the entity size
        // This adds unwanted overhead, so get rid of it
        if (CommonBootstrap.evaluateMCVersion(">=", "1.14")) {
            entity = new ClassInterceptor() {
                @Override
                protected Invokable getCallback(Method method) {
                    if (method.getName().equals("a") && method.getParameterCount() == 1) {
                        Class<?> argType = method.getParameters()[0].getType();
                        if (DataWatcherObjectHandle.T.isAssignableFrom(argType)) {
                            return new NullInvokable();
                        }
                    }

                    return null;
                }
            }.hook(entity);
        }

        INSTANCE = EntityHandle.createHandle(entity);
    }
}
