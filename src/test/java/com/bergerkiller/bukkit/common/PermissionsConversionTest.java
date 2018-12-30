package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.bukkit.permissions.PermissionDefault;
import org.junit.Test;

import com.bergerkiller.bukkit.common.conversion.Conversion;

public class PermissionsConversionTest {

    @Test
    public void testPermissionDefaultConversion() {
        for (int i = 0; i < 10; i++) {
            assertPerm(PermissionDefault.TRUE, Conversion.convert("true", PermissionDefault.class, null));
            assertPerm(PermissionDefault.FALSE, Conversion.convert("false", PermissionDefault.class, null));
            assertPerm(PermissionDefault.OP, Conversion.convert("op", PermissionDefault.class, null));
            assertPerm(PermissionDefault.NOT_OP, Conversion.convert("not_op", PermissionDefault.class, null));
            assertPerm(PermissionDefault.TRUE, Conversion.convert(Boolean.TRUE, PermissionDefault.class, null));
            assertPerm(PermissionDefault.FALSE, Conversion.convert(Boolean.FALSE, PermissionDefault.class, null));
        }
    }

    private void assertPerm(PermissionDefault expected, PermissionDefault actual) {
        if (actual == null) {
            fail("Permission default failed to parse");
        }
        if (actual != expected) {
            fail("Invalid default parsed: expected " + expected.name() + ", but got " + actual.name() + " instead");
        }
    }
}
