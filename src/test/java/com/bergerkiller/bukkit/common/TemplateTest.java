package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.EnumDirectionHandle.EnumAxisHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

public class TemplateTest {

    static {
        CommonUtil.bootstrap();
    }

    @Test
    public void testTemplate() {
        boolean fullySuccessful = true;
        for (ClassDeclaration dec : Common.TEMPLATE_RESOLVER.all()) {
            String genClassPath = "com.bergerkiller.generated";

            for (String part : dec.type.typePath.split("\\.")) {
                if (Character.isUpperCase(part.charAt(0))) {
                    genClassPath += "." + part + "Handle";
                } else {
                    genClassPath += "." + part;
                }
            }

            genClassPath = trimAfter(genClassPath, "org.bukkit.craftbukkit.");
            genClassPath = trimAfter(genClassPath, "net.minecraft.server.");
            Class<?> genClass = CommonUtil.getClass(genClassPath, true);
            if (genClass == null) {
                fail("Failed to find generated class at " + genClassPath);
            }
            try {
                java.lang.reflect.Field f = genClass.getField("T");
                if (f == null) {
                    fail("Failed to find template field in type " + genClassPath);
                }
                Template.Class c = (Template.Class) f.get(null);
                if (c == null) {
                    fail("Failed to initialize template class " + genClassPath);
                }
                if (!c.isValid()) {
                    System.err.println("Failed to fully load template class " + genClassPath);
                    fullySuccessful = false;
                }
            } catch (Throwable t) {
                throw new RuntimeException("Failed to test class "+  genClassPath, t);
            }
        }
        if (!fullySuccessful) {
            fail("Some generated reflection template classes could not be loaded");
        }
    }

    @Test
    public void testEnum() {
        assertNotNull(EnumAxisHandle.T.X.raw.get());
        assertNotNull(EnumAxisHandle.T.X.get());
        assertNotNull(EnumAxisHandle.X);
    }

    private String trimAfter(String path, String prefix) {
        int idx = path.indexOf(prefix);
        if (idx != -1) {
            idx += prefix.length();
            int endIdx = path.indexOf('.', idx);
            if (endIdx != -1) {
                path = path.substring(0, idx) + path.substring(endIdx + 1);
            }
        }
        return path;
    }
}
