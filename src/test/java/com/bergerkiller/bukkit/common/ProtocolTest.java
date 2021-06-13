package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;

public class ProtocolTest {

    @Test
    public void testAngleProtocol() {
        for (int protAngle = -128; protAngle <= 127; protAngle++) {
            float realAngle = EntityTrackerEntryStateHandle.getRotationFromProtocol(protAngle);
            int protResult = EntityTrackerEntryStateHandle.getProtocolRotation(realAngle);
            if (protResult != protAngle) {
                fail("Angle(" + protAngle + ") = " + realAngle + " but Prot(" + realAngle + ")=" + protResult);
            }
        }
        assertEquals(0, EntityTrackerEntryStateHandle.getProtocolRotation(0.0f));
        assertEquals(0, EntityTrackerEntryStateHandle.getProtocolRotation(360.0f));
        assertEquals(0, EntityTrackerEntryStateHandle.getProtocolRotation(-360.0f));
        assertEquals(0, EntityTrackerEntryStateHandle.getProtocolRotation(720.0f));
        assertEquals(127, EntityTrackerEntryStateHandle.getProtocolRotation(179.0f));
        assertEquals(-128, EntityTrackerEntryStateHandle.getProtocolRotation(180.0f));
        assertEquals(-128, EntityTrackerEntryStateHandle.getProtocolRotation(181.0f));
        assertEquals(127, EntityTrackerEntryStateHandle.getProtocolRotation(-181.0f));
        assertEquals(-128, EntityTrackerEntryStateHandle.getProtocolRotation(-180.0f));
        assertEquals(-128, EntityTrackerEntryStateHandle.getProtocolRotation(-179.0f));
    }

    @Test
    public void testAngleChangedPerformance() {
        // Create a pool of random angles
        float[] pool = new float[1000000];
        boolean[] result_a = new boolean[pool.length];
        boolean[] result_b = new boolean[pool.length];
        Random r = new Random();
        for (int i = 0; i < pool.length; i++) {
            pool[i] = -360.0f + (720.0f * r.nextFloat());
        }

        // Method A + measure
        long ma_start = System.nanoTime();
        for (int i = 1; i < pool.length; i++) {
            result_a[i] = EntityTrackerEntryStateHandle.getProtocolRotation(pool[i-1]) !=
                    EntityTrackerEntryStateHandle.getProtocolRotation(pool[i]);
        }
        long ma_end = System.nanoTime();
        
        // Method B + measure
        long mb_start = System.nanoTime();
        for (int i = 1; i < pool.length; i++) {
            result_b[i] = EntityTrackerEntryStateHandle.hasProtocolRotationChanged(pool[i-1], pool[i]);
        }
        long mb_end = System.nanoTime();

        // Correctness check
        for (int i = 1; i < pool.length; i++) {
            assertEquals(result_a[i], result_b[i]);
        }

        // Display performance difference
        System.out.println("getProtocolRotation: " + ((double) (ma_end - ma_start) / 1000000.0) + " ms");
        System.out.println("hasProtocolRotationChanged: " + ((double) (mb_end - mb_start) / 1000000.0) + " ms");
    }
}
