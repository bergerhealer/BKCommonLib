package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.map.MapMarker;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Checks all map marker related functions work
 */
public class MapMarkerTest {

    @Test
    public void testProperties() {
        assertEquals("WHITE_POINTER", MapMarker.Type.WHITE_POINTER.name());
        assertEquals("pointer (white)", MapMarker.Type.WHITE_POINTER.displayName());
        assertEquals(MapMarker.Color.WHITE, MapMarker.Type.WHITE_POINTER.color());

        assertEquals("WHITE_CIRCLE", MapMarker.Type.WHITE_CIRCLE.name());
        assertEquals("ball (white)", MapMarker.Type.WHITE_CIRCLE.displayName());
        assertEquals(MapMarker.Color.WHITE, MapMarker.Type.WHITE_CIRCLE.color());

        assertEquals("BANNER_GREEN", MapMarker.Type.BANNER_GREEN.name());
        assertEquals("banner (green)", MapMarker.Type.BANNER_GREEN.displayName());
        assertEquals(MapMarker.Color.GREEN, MapMarker.Type.BANNER_GREEN.color());
    }

    @Test
    public void testByName() {
        assertEquals(MapMarker.Type.WHITE_POINTER, MapMarker.Type.byName("WHITE_POINTER"));
        assertEquals(MapMarker.Type.BANNER_GREEN, MapMarker.Type.byName("BANNER_GREEN"));
    }

    @Test
    public void testValues() {
        // Check that at least the types known as of 1.8 exist
        List<MapMarker.Type> values = MapMarker.Type.values();
        assertTrue(values.contains(MapMarker.Type.WHITE_POINTER));
        assertTrue(values.contains(MapMarker.Type.WHITE_CIRCLE));
    }

    @Test
    public void testValuesIncludingUnavailable() {
        // Check that at least the types known as of 1.8 exist
        List<MapMarker.Type> values = MapMarker.Type.values_including_unavailable();
        assertTrue(values.contains(MapMarker.Type.WHITE_POINTER));
        assertTrue(values.contains(MapMarker.Type.WHITE_CIRCLE));
        assertTrue(values.contains(MapMarker.Type.BANNER_GREEN));
    }
}
