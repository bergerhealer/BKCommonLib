package com.bergerkiller.bukkit.common.map.util;

import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.bergerkiller.bukkit.common.AsyncTask;
import com.bergerkiller.bukkit.common.map.MapCanvas;

/**
 * Some utility methods for testing map displays outside the server
 */
public class MapDebugWindow {

    public static void waitForever() {
        for (;;) {
            AsyncTask.sleep(10000);
        }
    }

    public static void showMapForever(MapCanvas mapContents) {
        showMap(mapContents);
        waitForever();
    }

    public static void showMapForever(MapCanvas mapContents, int scale) {
        showMap(mapContents, scale);
        waitForever();
    }

    public static void showMap(MapCanvas mapContents) {
        showMap(mapContents, 1);
    }

    public static void showMap(MapCanvas mapContents, int scale) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(new GridLayout(1,2));
        f.getContentPane().add(new JLabel(new ImageIcon(mapContents.toJavaImage().getScaledInstance(mapContents.getWidth() * scale, mapContents.getHeight() * scale, 0))));
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
