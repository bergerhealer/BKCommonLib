package com.bergerkiller.bukkit.common.map.util;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.utils.MathUtil;

/**
 * Displays live map contents in a Java window for debugging outside of the server
 */
public class MapDebugWindow {
    private double _x_dirty = 0.0, _y_dirty = 0.0;
    private int _z_dirty = 0;
    private int _xint = 0, _yint = 0, _zint = 0;
    private double _x = 0.0, _y = 0.0;
    private boolean _event = true;
    private boolean _closing = false;
    private final JLabel label;
    private final MapCanvas map;
    private final int scale;

    private MapDebugWindow(final JLabel label, MapCanvas map, int scale) {
        this.map = map;
        this.scale = scale;
        this.label = label;
        this.label.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                _x_dirty = (double) e.getX() / (double) label.getWidth();
                _y_dirty = (double) e.getY() / (double) label.getHeight();
                signal();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
        this.label.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() > 0) {
                    _z_dirty++;
                    signal();
                } else if (e.getWheelRotation() < 0) {
                    _z_dirty--;
                    signal();
                }
            }
        });
        updateImage();
    }

    private void updateImage() {
        this.label.setIcon(new ImageIcon(map.toJavaImage().getScaledInstance(map.getWidth() * scale, map.getHeight() * scale, 0)));
    }

    public synchronized boolean waitNext() {
        this.updateImage();
        _event = false;
        while (!_event) {
            try {
                this.wait();
            } catch (InterruptedException ignore) {}
        }
        _x = _x_dirty;
        _y = _y_dirty;
        _xint = (int) (this._x * this.map.getWidth());
        _yint = (int) (this._y * this.map.getHeight());
        _zint = _z_dirty;
        return !_closing;
    }

    public int x() {
        return this._xint;
    }

    public int y() {
        return this._yint;
    }

    public int z() {
        return this._zint;
    }

    public int x(int min, int max) {
        return MathUtil.clamp(this._xint, min, max);
    }

    public int y(int min, int max) {
        return MathUtil.clamp(this._yint, min, max);
    }

    public int z(int min, int max) {
        return MathUtil.clamp(this._zint, min, max);
    }

    public double fx() {
        return this._x;
    }

    public double fy() {
        return this._y;
    }

    private synchronized void signal() {
        _event = true;
        this.notifyAll();
    }

    public void waitForever() {
        while (waitNext());
    }

    public static void showMapForeverAutoScale(MapCanvas mapContents) {
        showMapAutoScale(mapContents).waitForever();
    }

    public static void showMapForever(MapCanvas mapContents) {
        showMap(mapContents).waitForever();
    }

    public static void showMapForever(MapCanvas mapContents, int scale) {
        showMap(mapContents, scale).waitForever();
    }

    public static MapDebugWindow showMapAutoScale(MapCanvas mapContents) {
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        double scale = Math.min(width / (mapContents.getWidth() + 32), height / (mapContents.getHeight() + 64));        
        return showMap(mapContents, Math.max(1, MathUtil.floor(scale)));
    }

    public static MapDebugWindow showMap(MapCanvas mapContents) {
        return showMap(mapContents, 1);
    }

    public static MapDebugWindow showMap(MapCanvas mapContents, int scale) {
        final MapDebugWindow window = new MapDebugWindow(new JLabel(), mapContents, scale);

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        f.getContentPane().setLayout(new GridLayout(1,2));
        f.getContentPane().add(window.label);
        f.pack();
        f.setLocationRelativeTo(null);
        f.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                window._closing = true;
                window.signal();
            }
        });
        f.setVisible(true);
        return window;
    }
}
