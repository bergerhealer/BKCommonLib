package com.bergerkiller.bukkit.common.internal.logic;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;

public class CreaturePreSpawnHandler_Paper extends CreaturePreSpawnHandler {
    private final PreCreatureSpawnEventHandle handle;
    private final Listener listener;

    public CreaturePreSpawnHandler_Paper() throws Throwable {
        handle = Template.Class.create(PreCreatureSpawnEventHandle.class);
        handle.forceInitialization();

        // Since we don't know about paper's events at compile time (maybe fix this?), generate
        // a handler class that we shall use to listen for the PreCreatureSpawnEvent
        ExtendedClassWriter<Listener> cw = ExtendedClassWriter.builder(Listener.class)
                .setExactName(CreaturePreSpawnHandler_Paper.class.getName() + "$Listener")
                .build();
        FieldVisitor fv;
        MethodVisitor mv;

        // Add a 'main' field storing this CreaturePreSpawnHandler_Paper instance
        String preSpawnHandlerPaperDesc = MPLType.getDescriptor(CreaturePreSpawnHandler_Paper.class);
        {
            fv = cw.visitField(ACC_PRIVATE | ACC_FINAL, "main", preSpawnHandlerPaperDesc, null, null);
            fv.visitEnd();
        }

        // Add a constructor initializing the 'main' field
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(" + preSpawnHandlerPaperDesc + ")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, cw.getInternalName(), "main", preSpawnHandlerPaperDesc);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }

        // Add a method to handle the CreaturePreSpawnEvent
        Class<?> preSpawnEventType = Class.forName("com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent");
        String preSpawnEventDesc = MPLType.getDescriptor(preSpawnEventType);
        {
            mv = cw.visitMethod(ACC_PUBLIC, "onCreaturePreSpawnEvent", "(" + preSpawnEventDesc + ")V", null, null);
            {
                AnnotationVisitor av0 = mv.visitAnnotation(MPLType.getDescriptor(EventHandler.class), true);
                av0.visitEnd();
            }
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "main", preSpawnHandlerPaperDesc);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, MPLType.getInternalName(CreaturePreSpawnHandler_Paper.class), "onCreaturePreSpawnEvent", "(Ljava/lang/Object;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }

        listener = cw.generateInstance(new Class[] { CreaturePreSpawnHandler_Paper.class }, new Object[] { this });
        if (CommonPlugin.hasInstance()) {
            CommonPlugin.getInstance().register(listener);
        }
    }

    /**
     * This callback is called by a generated event listener to handle the event.
     * In future, please just add a dependency for paper api...
     * 
     * TODO: FIXME!
     *
     * @param event
     */
    public void onCreaturePreSpawnEvent(Object event) {
        if (!handle.isCancelled(event)) {
            // Only handle natural/spawner for performance reasons.
            // Others should be handled by the CreatureSpawnEvent, because
            // our pre spawn event lacks a reason historically. May in the future
            // be removed, where people need to handle event.getReason() appropriately.
            CreatureSpawnEvent.SpawnReason reason = handle.getReason(event);
            if (reason != CreatureSpawnEvent.SpawnReason.NATURAL &&
                reason != CreatureSpawnEvent.SpawnReason.SPAWNER)
            {
                return;
            }

            Location at = handle.getLocation(event);
            EntityType entityType = handle.getEntityType(event);
            if (!CommonPlugin.getInstance().getEventFactory().handleCreaturePreSpawn(at, entityType, reason)) {
                handle.abort(event);
            }
        }
    }

    @Override
    public void onEventHasHandlers() {
    }

    @Override
    public void onWorldEnabled(World world) {
    }

    @Override
    public void onWorldDisabled(World world) {
    }

    @Template.Import("org.bukkit.Location")
    @Template.Import("org.bukkit.entity.EntityType")
    @Template.InstanceType("com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent")
    public static abstract class PreCreatureSpawnEventHandle extends Template.Class<Template.Handle> {

        /*
         * <PRESPAWN_IS_CANCELLED>
         * public static boolean isCancelled(PreCreatureSpawnEvent event) {
         * #if exists com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent public boolean shouldAbortSpawn();
         *     if (event.shouldAbortSpawn()) {
         *         return true;
         *     }
         * #endif
         *     return event.isCancelled();
         * }
         */
        @Template.Generated("%PRESPAWN_IS_CANCELLED%")
        public abstract boolean isCancelled(Object event);

        /*
         * <PRESPAWN_ABORT>
         * public static void abort(PreCreatureSpawnEvent event) {
         * #if exists com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent public void setShouldAbortSpawn(boolean shouldAbortSpawn);
         *     event.setShouldAbortSpawn(true);
         * #endif
         *     event.setCancelled(true);
         * }
         */
        @Template.Generated("%PRESPAWN_ABORT%")
        public abstract void abort(Object event);

        /*
         * <PRESPAWN_GET_LOCATION>
         * public static Location getLocation(PreCreatureSpawnEvent event) {
         *     return event.getSpawnLocation();
         * }
         */
        @Template.Generated("%PRESPAWN_GET_LOCATION%")
        public abstract Location getLocation(Object event);

        /*
         * <PRESPAWN_GET_ENTITYTYPE>
         * public static EntityType getEntityType(PreCreatureSpawnEvent event) {
         *     return event.getType();
         * }
         */
        @Template.Generated("%PRESPAWN_GET_ENTITYTYPE%")
        public abstract EntityType getEntityType(Object event);

        /*
         * <PRESPAWN_GET_REASON>
         * public static org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason getReason(PreCreatureSpawnEvent event) {
         *     return event.getReason();
         * }
         */
        @Template.Generated("%PRESPAWN_GET_REASON%")
        public abstract CreatureSpawnEvent.SpawnReason getReason(Object event);
    }
}
