package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutEntityHandle extends PacketHandle {
    /** @See {@link PacketPlayOutEntityClass} */
    public static final PacketPlayOutEntityClass T = new PacketPlayOutEntityClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutEntityHandle.class, "net.minecraft.server.PacketPlayOutEntity");

    /* ============================================================================== */

    public static PacketPlayOutEntityHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutEntityHandle handle = new PacketPlayOutEntityHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */


    public double getDeltaX() {
        if (T.dx_1_10_2.isAvailable()) {
            return (double) T.dx_1_10_2.getInteger(instance) / 4096.0;
        } else {
            return (double) T.dx_1_8_8.getByte(instance) / 32.0;
        }
    }

    public double getDeltaY() {
        if (T.dy_1_10_2.isAvailable()) {
            return (double) T.dy_1_10_2.getInteger(instance) / 4096.0;
        } else {
            return (double) T.dy_1_8_8.getByte(instance) / 32.0;
        }
    }

    public double getDeltaZ() {
        if (T.dz_1_10_2.isAvailable()) {
            return (double) T.dz_1_10_2.getInteger(instance) / 4096.0;
        } else {
            return (double) T.dz_1_8_8.getByte(instance) / 32.0;
        }
    }

    public void setDeltaX(double dx) {
        if (T.dx_1_10_2.isAvailable()) {
            T.dx_1_10_2.setInteger(instance, com.bergerkiller.bukkit.common.utils.MathUtil.floor(dx * 4096.0));
        } else {
            T.dx_1_8_8.setByte(instance, (byte) com.bergerkiller.bukkit.common.utils.MathUtil.floor(dx * 32.0));
        }
    }

    public void setDeltaY(double dy) {
        if (T.dy_1_10_2.isAvailable()) {
            T.dy_1_10_2.setInteger(instance, com.bergerkiller.bukkit.common.utils.MathUtil.floor(dy * 4096.0));
        } else {
            T.dy_1_8_8.setByte(instance, (byte) com.bergerkiller.bukkit.common.utils.MathUtil.floor(dy * 32.0));
        }
    }

    public void setDeltaZ(double dz) {
        if (T.dz_1_10_2.isAvailable()) {
            T.dz_1_10_2.setInteger(instance, com.bergerkiller.bukkit.common.utils.MathUtil.floor(dz * 4096.0));
        } else {
            T.dz_1_8_8.setByte(instance, (byte) com.bergerkiller.bukkit.common.utils.MathUtil.floor(dz * 32.0));
        }
    }

    public float getDeltaYaw() {
        return (float) T.dyaw_raw.getByte(instance) * 360.0f / 256.0f;
    }

    public float getDeltaPitch() {
        return (float) T.dpitch_raw.getByte(instance) * 360.0f / 256.0f;
    }

    public void setDeltaYaw(float deltaYaw) {
        T.dyaw_raw.setByte(instance, (byte) com.bergerkiller.bukkit.common.utils.MathUtil.floor(deltaYaw * 256.0f / 360.0f));
    }

    public void setDeltaPitch(float deltaPitch) {
        T.dpitch_raw.setByte(instance, (byte) com.bergerkiller.bukkit.common.utils.MathUtil.floor(deltaPitch * 256.0f / 360.0f));
    }
    public int getEntityId() {
        return T.entityId.getInteger(instance);
    }

    public void setEntityId(int value) {
        T.entityId.setInteger(instance, value);
    }

    public boolean isOnGround() {
        return T.onGround.getBoolean(instance);
    }

    public void setOnGround(boolean value) {
        T.onGround.setBoolean(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutEntityClass extends Template.Class<PacketPlayOutEntityHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Byte dx_1_8_8 = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Byte dy_1_8_8 = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Byte dz_1_8_8 = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Integer dx_1_10_2 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer dy_1_10_2 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer dz_1_10_2 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Byte dyaw_raw = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Byte dpitch_raw = new Template.Field.Byte();
        public final Template.Field.Boolean onGround = new Template.Field.Boolean();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutEntity.PacketPlayOutEntityLook</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    public static class PacketPlayOutEntityLookHandle extends PacketPlayOutEntityHandle {
        /** @See {@link PacketPlayOutEntityLookClass} */
        public static final PacketPlayOutEntityLookClass T = new PacketPlayOutEntityLookClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutEntityLookHandle.class, "net.minecraft.server.PacketPlayOutEntity.PacketPlayOutEntityLook");

        /* ============================================================================== */

        public static PacketPlayOutEntityLookHandle createHandle(Object handleInstance) {
            if (handleInstance == null) return null;
            PacketPlayOutEntityLookHandle handle = new PacketPlayOutEntityLookHandle();
            handle.instance = handleInstance;
            return handle;
        }

        public static final PacketPlayOutEntityLookHandle createNew() {
            return T.constr.newInstance();
        }

        /* ============================================================================== */


        public static PacketPlayOutEntityHandle.PacketPlayOutEntityLookHandle createNew(int entityId, float deltaYaw, float deltaPitch, boolean onGround) {
            PacketPlayOutEntityHandle.PacketPlayOutEntityLookHandle handle = createNew();
            handle.setEntityId(entityId);
            handle.setDeltaYaw(deltaYaw);
            handle.setDeltaPitch(deltaPitch);
            handle.setOnGround(onGround);
            return handle;
        }
        /**
         * Stores class members for <b>net.minecraft.server.PacketPlayOutEntity.PacketPlayOutEntityLook</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class PacketPlayOutEntityLookClass extends Template.Class<PacketPlayOutEntityLookHandle> {
            public final Template.Constructor.Converted<PacketPlayOutEntityLookHandle> constr = new Template.Constructor.Converted<PacketPlayOutEntityLookHandle>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutEntity.PacketPlayOutRelEntityMove</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    public static class PacketPlayOutRelEntityMoveHandle extends PacketPlayOutEntityHandle {
        /** @See {@link PacketPlayOutRelEntityMoveClass} */
        public static final PacketPlayOutRelEntityMoveClass T = new PacketPlayOutRelEntityMoveClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutRelEntityMoveHandle.class, "net.minecraft.server.PacketPlayOutEntity.PacketPlayOutRelEntityMove");

        /* ============================================================================== */

        public static PacketPlayOutRelEntityMoveHandle createHandle(Object handleInstance) {
            if (handleInstance == null) return null;
            PacketPlayOutRelEntityMoveHandle handle = new PacketPlayOutRelEntityMoveHandle();
            handle.instance = handleInstance;
            return handle;
        }

        public static final PacketPlayOutRelEntityMoveHandle createNew() {
            return T.constr.newInstance();
        }

        /* ============================================================================== */


        public static PacketPlayOutEntityHandle.PacketPlayOutRelEntityMoveHandle createNew(int entityId, double dx, double dy, double dz, boolean onGround) {
            PacketPlayOutEntityHandle.PacketPlayOutRelEntityMoveHandle handle = createNew();
            handle.setEntityId(entityId);
            handle.setDeltaX(dx);
            handle.setDeltaY(dy);
            handle.setDeltaZ(dz);
            handle.setOnGround(onGround);
            return handle;
        }
        /**
         * Stores class members for <b>net.minecraft.server.PacketPlayOutEntity.PacketPlayOutRelEntityMove</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class PacketPlayOutRelEntityMoveClass extends Template.Class<PacketPlayOutRelEntityMoveHandle> {
            public final Template.Constructor.Converted<PacketPlayOutRelEntityMoveHandle> constr = new Template.Constructor.Converted<PacketPlayOutRelEntityMoveHandle>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    public static class PacketPlayOutRelEntityMoveLookHandle extends PacketPlayOutEntityHandle {
        /** @See {@link PacketPlayOutRelEntityMoveLookClass} */
        public static final PacketPlayOutRelEntityMoveLookClass T = new PacketPlayOutRelEntityMoveLookClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutRelEntityMoveLookHandle.class, "net.minecraft.server.PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook");

        /* ============================================================================== */

        public static PacketPlayOutRelEntityMoveLookHandle createHandle(Object handleInstance) {
            if (handleInstance == null) return null;
            PacketPlayOutRelEntityMoveLookHandle handle = new PacketPlayOutRelEntityMoveLookHandle();
            handle.instance = handleInstance;
            return handle;
        }

        public static final PacketPlayOutRelEntityMoveLookHandle createNew() {
            return T.constr.newInstance();
        }

        /* ============================================================================== */


        public static PacketPlayOutEntityHandle.PacketPlayOutRelEntityMoveLookHandle createNew(int entityId, double dx, double dy, double dz, float deltaYaw, float deltaPitch, boolean onGround) {
            PacketPlayOutEntityHandle.PacketPlayOutRelEntityMoveLookHandle handle = createNew();
            handle.setEntityId(entityId);
            handle.setDeltaX(dx);
            handle.setDeltaY(dy);
            handle.setDeltaZ(dz);
            handle.setDeltaYaw(deltaYaw);
            handle.setDeltaPitch(deltaPitch);
            handle.setOnGround(onGround);
            return handle;
        }
        /**
         * Stores class members for <b>net.minecraft.server.PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class PacketPlayOutRelEntityMoveLookClass extends Template.Class<PacketPlayOutRelEntityMoveLookHandle> {
            public final Template.Constructor.Converted<PacketPlayOutRelEntityMoveLookHandle> constr = new Template.Constructor.Converted<PacketPlayOutRelEntityMoveLookHandle>();

        }

    }

}

