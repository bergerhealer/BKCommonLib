package com.bergerkiller.bukkit.common.server.test;

class TestServerFactory_1_20_2 extends TestServerFactory_1_19_3 {

    @Override
    protected Object createVanillaResourcePackRepository() throws Throwable {
        final String repopath = "net.minecraft.server.packs.repository.";
        final Class<?> resourcePackRepositoryType = Class.forName(repopath + "ResourcePackSourceVanilla");

        // Now has a handy method we can call that does it all
        return createFromCode(resourcePackRepositoryType,
                "return ResourcePackSourceVanilla.createVanillaTrustedRepository();");
    }
}
