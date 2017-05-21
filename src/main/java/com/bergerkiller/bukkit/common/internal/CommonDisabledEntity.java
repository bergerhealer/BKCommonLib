package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.generated.net.minecraft.server.EntityHandle;

public class CommonDisabledEntity {
    public static final Object INSTANCE;

    static {
        INSTANCE = EntityHandle.createNew(null).getRaw();
    }

    //TODO: Really needed to override the below methods?
    // If so, generate an extended type at runtime.

    /*
    //public static final CommonDisabledEntity INSTANCE = ClassTemplate.create(CommonDisabledEntity.class).newInstanceNull();

    private CommonDisabledEntity() {
        super(null);
    }

    @Override
    protected void a(NBTTagCompound arg0) {
    }

    @Override
    protected void b(NBTTagCompound arg0) {
    }

	@Override
	public MinecraftServer B_() {
		return null;
	}

	@Override
	protected void i() {
	}
	*/
}
