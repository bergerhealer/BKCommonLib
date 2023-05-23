package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.Display</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.entity.Display")
public abstract class DisplayHandle extends EntityHandle {
    /** @see DisplayClass */
    public static final DisplayClass T = Template.Class.create(DisplayClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static DisplayHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public static final Key<Integer> DATA_INTERPOLATION_START_DELTA_TICKS = Key.Type.INTEGER.createKey(T.DATA_INTERPOLATION_START_DELTA_TICKS_ID, -1);
    public static final Key<Integer> DATA_INTERPOLATION_DURATION = Key.Type.INTEGER.createKey(T.DATA_INTERPOLATION_DURATION_ID, -1);
    public static final Key<org.bukkit.util.Vector> DATA_TRANSLATION = Key.Type.JOML_VECTOR3F.createKey(T.DATA_TRANSLATION_ID, -1);
    public static final Key<org.bukkit.util.Vector> DATA_SCALE = Key.Type.JOML_VECTOR3F.createKey(T.DATA_SCALE_ID, -1);
    public static final Key<com.bergerkiller.bukkit.common.math.Quaternion> DATA_LEFT_ROTATION = Key.Type.JOML_QUATERNIONF.createKey(T.DATA_LEFT_ROTATION_ID, -1);
    public static final Key<com.bergerkiller.bukkit.common.math.Quaternion> DATA_RIGHT_ROTATION = Key.Type.JOML_QUATERNIONF.createKey(T.DATA_RIGHT_ROTATION_ID, -1);
    public static final Key<Byte> DATA_BILLBOARD_RENDER_CONSTRAINTS = Key.Type.BYTE.createKey(T.DATA_BILLBOARD_RENDER_CONSTRAINTS_ID, -1);
    public static final Key<com.bergerkiller.bukkit.common.wrappers.Brightness> DATA_BRIGHTNESS_OVERRIDE = Key.Type.DISPLAY_BRIGHTNESS.createKey(T.DATA_BRIGHTNESS_OVERRIDE_ID, -1);
    public static final Key<Float> DATA_VIEW_RANGE = Key.Type.FLOAT.createKey(T.DATA_VIEW_RANGE_ID, -1);
    public static final Key<Float> DATA_SHADOW_RADIUS = Key.Type.FLOAT.createKey(T.DATA_SHADOW_RADIUS_ID, -1);
    public static final Key<Float> DATA_SHADOW_STRENGTH = Key.Type.FLOAT.createKey(T.DATA_SHADOW_STRENGTH_ID, -1);
    public static final Key<Float> DATA_WIDTH = Key.Type.FLOAT.createKey(T.DATA_WIDTH_ID, -1);
    public static final Key<Float> DATA_HEIGHT = Key.Type.FLOAT.createKey(T.DATA_HEIGHT_ID, -1);
    public static final Key<Integer> DATA_GLOW_COLOR_OVERRIDE = Key.Type.INTEGER.createKey(T.DATA_GLOW_COLOR_OVERRIDE_ID, -1);
    /**
     * Stores class members for <b>net.minecraft.world.entity.Display</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DisplayClass extends Template.Class<DisplayHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_INTERPOLATION_START_DELTA_TICKS_ID = new Template.StaticField.Converted<Key<Integer>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_INTERPOLATION_DURATION_ID = new Template.StaticField.Converted<Key<Integer>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Object>> DATA_TRANSLATION_ID = new Template.StaticField.Converted<Key<Object>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Object>> DATA_SCALE_ID = new Template.StaticField.Converted<Key<Object>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Object>> DATA_LEFT_ROTATION_ID = new Template.StaticField.Converted<Key<Object>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Object>> DATA_RIGHT_ROTATION_ID = new Template.StaticField.Converted<Key<Object>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Byte>> DATA_BILLBOARD_RENDER_CONSTRAINTS_ID = new Template.StaticField.Converted<Key<Byte>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_BRIGHTNESS_OVERRIDE_ID = new Template.StaticField.Converted<Key<Integer>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Float>> DATA_VIEW_RANGE_ID = new Template.StaticField.Converted<Key<Float>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Float>> DATA_SHADOW_RADIUS_ID = new Template.StaticField.Converted<Key<Float>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Float>> DATA_SHADOW_STRENGTH_ID = new Template.StaticField.Converted<Key<Float>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Float>> DATA_WIDTH_ID = new Template.StaticField.Converted<Key<Float>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Float>> DATA_HEIGHT_ID = new Template.StaticField.Converted<Key<Float>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_GLOW_COLOR_OVERRIDE_ID = new Template.StaticField.Converted<Key<Integer>>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.world.entity.Display.TextDisplay</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.world.entity.Display.TextDisplay")
    public abstract static class TextDisplayHandle extends DisplayHandle {
        /** @see TextDisplayClass */
        public static final TextDisplayClass T = Template.Class.create(TextDisplayClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static TextDisplayHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */


        public static final Key<com.bergerkiller.bukkit.common.wrappers.ChatText> DATA_TEXT = Key.Type.CHAT_TEXT.createKey(T.DATA_TEXT_ID, -1);
        public static final Key<Integer> DATA_LINE_WIDTH = Key.Type.INTEGER.createKey(T.DATA_LINE_WIDTH_ID, -1);
        public static final Key<Integer> DATA_BACKGROUND_COLOR = Key.Type.INTEGER.createKey(T.DATA_BACKGROUND_COLOR_ID, -1);
        public static final Key<Byte> DATA_TEXT_OPACITY = Key.Type.BYTE.createKey(T.DATA_TEXT_OPACITY_ID, -1);
        public static final Key<Byte> DATA_STYLE_FLAGS = Key.Type.BYTE.createKey(T.DATA_STYLE_FLAGS_ID, -1);
        /**
         * Stores class members for <b>net.minecraft.world.entity.Display.TextDisplay</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class TextDisplayClass extends Template.Class<TextDisplayHandle> {
            @Template.Optional
            public final Template.StaticField.Converted<Key<Object>> DATA_TEXT_ID = new Template.StaticField.Converted<Key<Object>>();
            @Template.Optional
            public final Template.StaticField.Converted<Key<Integer>> DATA_LINE_WIDTH_ID = new Template.StaticField.Converted<Key<Integer>>();
            @Template.Optional
            public final Template.StaticField.Converted<Key<Integer>> DATA_BACKGROUND_COLOR_ID = new Template.StaticField.Converted<Key<Integer>>();
            @Template.Optional
            public final Template.StaticField.Converted<Key<Byte>> DATA_TEXT_OPACITY_ID = new Template.StaticField.Converted<Key<Byte>>();
            @Template.Optional
            public final Template.StaticField.Converted<Key<Byte>> DATA_STYLE_FLAGS_ID = new Template.StaticField.Converted<Key<Byte>>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.world.entity.Display.BlockDisplay</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.world.entity.Display.BlockDisplay")
    public abstract static class BlockDisplayHandle extends DisplayHandle {
        /** @see BlockDisplayClass */
        public static final BlockDisplayClass T = Template.Class.create(BlockDisplayClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static BlockDisplayHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */


        public static final Key<com.bergerkiller.bukkit.common.wrappers.BlockData> DATA_BLOCK_STATE = Key.Type.BLOCK_DATA.createKey(T.DATA_BLOCK_STATE_ID, -1);
        /**
         * Stores class members for <b>net.minecraft.world.entity.Display.BlockDisplay</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class BlockDisplayClass extends Template.Class<BlockDisplayHandle> {
            @Template.Optional
            public final Template.StaticField.Converted<Key<Object>> DATA_BLOCK_STATE_ID = new Template.StaticField.Converted<Key<Object>>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.world.entity.Display.ItemDisplay</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.world.entity.Display.ItemDisplay")
    public abstract static class ItemDisplayHandle extends DisplayHandle {
        /** @see ItemDisplayClass */
        public static final ItemDisplayClass T = Template.Class.create(ItemDisplayClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static ItemDisplayHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */


        public static final Key<org.bukkit.inventory.ItemStack> DATA_ITEM_STACK = Key.Type.ITEMSTACK.createKey(T.DATA_ITEM_STACK_ID, -1);
        public static final Key<com.bergerkiller.bukkit.common.wrappers.ItemDisplayMode> DATA_ITEM_DISPLAY_MODE = Key.Type.ITEM_DISPLAY_MODE.createKey(T.DATA_ITEM_DISPLAY_ID, -1);
        /**
         * Stores class members for <b>net.minecraft.world.entity.Display.ItemDisplay</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class ItemDisplayClass extends Template.Class<ItemDisplayHandle> {
            @Template.Optional
            public final Template.StaticField.Converted<Key<Object>> DATA_ITEM_STACK_ID = new Template.StaticField.Converted<Key<Object>>();
            @Template.Optional
            public final Template.StaticField.Converted<Key<Byte>> DATA_ITEM_DISPLAY_ID = new Template.StaticField.Converted<Key<Byte>>();

        }

    }

}

