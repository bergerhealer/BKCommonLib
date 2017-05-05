package com.bergerkiller.bukkit.common;

import org.junit.Test;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.reflection.gen.EntityHandle;

public class TemplateTest {

    static {
        CommonUtil.bootstrap();
    }

    @Test
    public void testTemplate() {
        EntityHandle handle = new EntityHandle();
    }
}
