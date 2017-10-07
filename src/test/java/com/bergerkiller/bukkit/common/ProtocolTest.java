package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryHandle;

public class ProtocolTest {

    @Test
    public void testAngleProtocol() {
        for (int protAngle = -128; protAngle <= 127; protAngle++) {
            float realAngle = EntityTrackerEntryHandle.getRotationFromProtocol(protAngle);
            int protResult = EntityTrackerEntryHandle.getProtocolRotation(realAngle);
            if (protResult != protAngle) {
                fail("Angle(" + protAngle + ") = " + realAngle + " but Prot(" + realAngle + ")=" + protResult);
            }
        }
        assertEquals(0, EntityTrackerEntryHandle.getProtocolRotation(0.0f));
        assertEquals(0, EntityTrackerEntryHandle.getProtocolRotation(360.0f));
        assertEquals(0, EntityTrackerEntryHandle.getProtocolRotation(-360.0f));
        assertEquals(0, EntityTrackerEntryHandle.getProtocolRotation(720.0f));
        assertEquals(127, EntityTrackerEntryHandle.getProtocolRotation(179.0f));
        assertEquals(-128, EntityTrackerEntryHandle.getProtocolRotation(180.0f));
        assertEquals(-128, EntityTrackerEntryHandle.getProtocolRotation(181.0f));
        assertEquals(127, EntityTrackerEntryHandle.getProtocolRotation(-181.0f));
        assertEquals(-128, EntityTrackerEntryHandle.getProtocolRotation(-180.0f));
        assertEquals(-128, EntityTrackerEntryHandle.getProtocolRotation(-179.0f));
    }

}
