package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.Block</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class BlockHandle extends Template.Handle {
    /** @See {@link BlockClass} */
    public static final BlockClass T = new BlockClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BlockHandle.class, "net.minecraft.server.Block");

    @SuppressWarnings("rawtypes")
    public static final Iterable REGISTRY = T.REGISTRY.getSafe();
    @SuppressWarnings("rawtypes")
    public static final Iterable REGISTRY_ID = T.REGISTRY_ID.getSafe();
    /* ============================================================================== */

    public static BlockHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        BlockHandle handle = new BlockHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static int getCombinedId(IBlockDataHandle iblockdata) {
        return T.getCombinedId.invokeVA(iblockdata);
    }

    public static int getId(BlockHandle block) {
        return T.getId.invokeVA(block);
    }

    public static BlockHandle getById(int id) {
        return T.getById.invokeVA(id);
    }

    public static IBlockDataHandle getByCombinedId(int combinedId) {
        return T.getByCombinedId.invokeVA(combinedId);
    }

    public void entityHitVertical(WorldHandle world, EntityHandle entity) {
        T.entityHitVertical.invoke(instance, world, entity);
    }

    public int getOpacity(IBlockDataHandle iblockdata) {
        return T.getOpacity.invoke(instance, iblockdata);
    }

    public int getEmission(IBlockDataHandle iblockdata) {
        return T.getEmission.invoke(instance, iblockdata);
    }

    public boolean isOccluding(IBlockDataHandle iblockdata) {
        return T.isOccluding.invoke(instance, iblockdata);
    }

    public boolean isPowerSource(IBlockDataHandle iblockdata) {
        return T.isPowerSource.invoke(instance, iblockdata);
    }

    public float getDamageResillience(Entity entity) {
        return T.getDamageResillience.invoke(instance, entity);
    }

    public SoundEffectTypeHandle getStepSound() {
        return T.getStepSound.invoke(instance);
    }

    public void dropNaturally(World world, IntVector3 blockposition, IBlockDataHandle iblockdata, float yield, int chance) {
        T.dropNaturally.invoke(instance, world, blockposition, iblockdata, yield, chance);
    }

    public void ignite(World world, IntVector3 blockposition, ExplosionHandle explosion) {
        T.ignite.invoke(instance, world, blockposition, explosion);
    }

    public void stepOn(World world, IntVector3 blockposition, Entity entity) {
        T.stepOn.invoke(instance, world, blockposition, entity);
    }

    public IBlockDataHandle getBlockData() {
        return T.getBlockData.invoke(instance);
    }

    public IBlockDataHandle fromLegacyData(int data) {
        return T.fromLegacyData.invoke(instance, data);
    }

    public int toLegacyData(IBlockDataHandle iblockdata) {
        return T.toLegacyData.invoke(instance, iblockdata);
    }

    /**
     * Stores class members for <b>net.minecraft.server.Block</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlockClass extends Template.Class<BlockHandle> {
        @SuppressWarnings("rawtypes")
        public final Template.StaticField.Converted<Iterable> REGISTRY = new Template.StaticField.Converted<Iterable>();
        @SuppressWarnings("rawtypes")
        public final Template.StaticField.Converted<Iterable> REGISTRY_ID = new Template.StaticField.Converted<Iterable>();

        public final Template.StaticMethod.Converted<Integer> getCombinedId = new Template.StaticMethod.Converted<Integer>();
        public final Template.StaticMethod.Converted<Integer> getId = new Template.StaticMethod.Converted<Integer>();
        public final Template.StaticMethod.Converted<BlockHandle> getById = new Template.StaticMethod.Converted<BlockHandle>();
        public final Template.StaticMethod.Converted<IBlockDataHandle> getByCombinedId = new Template.StaticMethod.Converted<IBlockDataHandle>();

        public final Template.Method.Converted<Void> entityHitVertical = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Integer> getOpacity = new Template.Method.Converted<Integer>();
        public final Template.Method.Converted<Integer> getEmission = new Template.Method.Converted<Integer>();
        public final Template.Method.Converted<Boolean> isOccluding = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Boolean> isPowerSource = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Float> getDamageResillience = new Template.Method.Converted<Float>();
        public final Template.Method.Converted<SoundEffectTypeHandle> getStepSound = new Template.Method.Converted<SoundEffectTypeHandle>();
        public final Template.Method.Converted<Void> dropNaturally = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> ignite = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> stepOn = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<IBlockDataHandle> getBlockData = new Template.Method.Converted<IBlockDataHandle>();
        public final Template.Method.Converted<IBlockDataHandle> fromLegacyData = new Template.Method.Converted<IBlockDataHandle>();
        public final Template.Method.Converted<Integer> toLegacyData = new Template.Method.Converted<Integer>();

    }

}

