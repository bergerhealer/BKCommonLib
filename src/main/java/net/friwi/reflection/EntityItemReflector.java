package net.friwi.reflection;

import net.minecraft.server.v1_8_R3.EntityItem;

import java.lang.reflect.Field;

public class EntityItemReflector {

    public static int getAge(EntityItem l) {
        try {
            Field f = EntityItem.class.getDeclaredField("age");
            f.setAccessible(true);
            return f.getInt(l);
        } catch (Exception e) {
            System.out.println("Could not get age from EntityItem");
            return 0;
        }
    }

    public static void setAge(EntityItem l, int age) {
        try {
            Field f = EntityItem.class.getDeclaredField("age");
            f.setAccessible(true);
            f.set(l, age);
        } catch (Exception e) {
            System.out.println("Could not set age in EntityItem");
        }
    }
}
