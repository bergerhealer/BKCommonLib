package com.bergerkiller.bukkit.common.server.test;

class TestServerFactory_1_20_2 extends TestServerFactory_1_19_3 {

    @Override
    protected void init(ServerEnvironment env) throws Throwable {
        super.init(env);

        // Normally done in CraftServer
        createFromCode(resolveClass("org.bukkit.craftbukkit.CraftServer"),
                "org.bukkit.craftbukkit.CraftRegistry.setMinecraftRegistry(arg0.registryAccess());",
                env.mc_server);
    }

    @Override
    protected Object createVanillaResourcePackRepository() throws Throwable {
        final Class<?> resourcePackRepositoryType = resolveClass("net.minecraft.server.packs.repository.ServerPacksSource");

        // Now has a handy method we can call that does it all
        return createFromCode(resourcePackRepositoryType,
                "return net.minecraft.server.packs.repository.ServerPacksSource.createVanillaTrustedRepository();");
    }
}
