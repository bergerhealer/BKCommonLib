package com.bergerkiller.bukkit.common;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;
import org.junit.Test;

import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapDisplayDebugWindow;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;

public class MapDisplayTest {

    @Ignore
    @Test
    public void testDisplayDebugWindow() {
        MapDisplay display = new MapDisplay() {
            @Override
            public void onAttached() {
                for (int i = 0; i < 3; i++) {
                    addWidget(new MapWidgetButton() {
                        @Override
                        public void onActivate() {

                        }
                    }
                            .setText(randomButtonText())
                            .setBounds(9, 12 + (i * 38), 110, 20));
                }
            }
        };

        MapDisplayDebugWindow.showForeverAutoScale(display);
    }

    private static String randomButtonText() {
        return "Btn-" + Integer.toHexString(ThreadLocalRandom.current().nextInt(0x1000, 0x10000));
    }
}
