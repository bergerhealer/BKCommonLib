package com.bergerkiller.bukkit.common.server.test;

class TestServerFactory_1_21_11 extends TestServerFactory_1_21_2 {
    @Override
    protected void init(ServerEnvironment env) throws Throwable {
        env.systemUtilsClassName = "net.minecraft.util.SystemUtils";
        super.init(env);
    }
}
