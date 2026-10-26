package com.bergerkiller.bukkit.common.map;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;

/**
 * Standalone 128x128 debug window for {@link MapDisplay} instances.
 * Runs the display at 20 ticks per second, forwards keyboard input
 * (W/A/S/D, Space and Shift) and continuously repaints map contents.
 */
public final class MapDisplayDebugWindow {
    private static final int WIDTH = MapDisplayTile.RESOLUTION;
    private static final int HEIGHT = MapDisplayTile.RESOLUTION;
    private static final int TPS = 20;

    private final MapDisplay display;
    private final MapDisplay.DebugController controller;
    private final MapDisplay.UpdateDispatcher previousDispatcher;
    private final int scale;
    private final JFrame frame;
    private final DebugPanel panel;
    private final CountDownLatch closedLatch = new CountDownLatch(1);
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "MapDisplayDebugWindow");
        t.setDaemon(true);
        return t;
    });
    private final Set<MapPlayerInput.Key> pressedKeys = Collections.newSetFromMap(new ConcurrentHashMap<MapPlayerInput.Key, Boolean>());
    private EnumSet<MapPlayerInput.Key> previousKeys = EnumSet.noneOf(MapPlayerInput.Key.class);
    private int keyRepeat = 0;
    private volatile boolean closed;

    private final byte[] imageBuffer = new byte[WIDTH * HEIGHT];
    private final BufferedImage image;

    private MapDisplayDebugWindow(MapDisplay display, int scale) {
        this.display = display;
        this.controller = display.createDebugController();
        this.previousDispatcher = display.getUpdateDispatcher();
        display.setUpdateDispatcher(new DebugUpdateDispatcher());
        this.scale = Math.max(1, scale);

        DataBufferByte dataBuffer = new DataBufferByte(imageBuffer, imageBuffer.length);
        WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, WIDTH, HEIGHT, WIDTH, 1, new int[] {0}, null);
        this.image = new BufferedImage(MapColorPalette.getIndexColorModel(), raster, false, null);

        this.panel = new DebugPanel();
        Dimension renderSize = new Dimension(WIDTH * this.scale, HEIGHT * this.scale);
        this.panel.setPreferredSize(renderSize);
        this.panel.setMinimumSize(renderSize);
        this.panel.setMaximumSize(renderSize);
        this.panel.setSize(renderSize);

        this.frame = new JFrame("Map Display Debug Window - " + display.getClass().getSimpleName());
        this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        this.frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                MapPlayerInput.Key key = fromKeyCode(e.getKeyCode());
                if (key != null) {
                    pressedKeys.add(key);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                MapPlayerInput.Key key = fromKeyCode(e.getKeyCode());
                if (key != null) {
                    pressedKeys.remove(key);
                }
            }
        });
        this.frame.setContentPane(panel);
        this.frame.pack();
        Insets insets = this.frame.getInsets();
        this.frame.setSize(renderSize.width + insets.left + insets.right,
                           renderSize.height + insets.top + insets.bottom);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
        this.frame.requestFocus();
    }

    public static MapDisplayDebugWindow show(MapDisplay display) {
        return show(display, calculateAutoScale());
    }

    public static MapDisplayDebugWindow show(MapDisplay display, int scale) {
        if (display == null) {
            throw new IllegalArgumentException("Display can not be null");
        }

        MapDisplayDebugWindow window = createWindowOnEdt(display, scale);
        window.startTicking();
        return window;
    }

    private static MapDisplayDebugWindow createWindowOnEdt(MapDisplay display, int scale) {
        if (SwingUtilities.isEventDispatchThread()) {
            return new MapDisplayDebugWindow(display, scale);
        }

        AtomicReference<MapDisplayDebugWindow> result = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    result.set(new MapDisplayDebugWindow(display, scale));
                } catch (Throwable t) {
                    error.set(t);
                }
            });
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while creating debug window", ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException("Failed to create debug window", ex.getCause());
        }

        if (error.get() != null) {
            throw new RuntimeException("Failed to create debug window", error.get());
        }
        return result.get();
    }

    public static void showForever(MapDisplay display) {
        showForever(display, 1);
    }

    public static void showForever(MapDisplay display, int scale) {
        MapDisplayDebugWindow window = show(display, scale);
        window.waitUntilClosed();
    }

    public static MapDisplayDebugWindow showAutoScale(MapDisplay display) {
        return show(display, calculateAutoScale());
    }

    public static void showForeverAutoScale(MapDisplay display) {
        showForever(display, calculateAutoScale());
    }

    public void waitUntilClosed() {
        try {
            closedLatch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public void close() {
        if (closed) {
            return;
        }
        closed = true;

        executor.shutdownNow();
        try {
            controller.stop();
        } catch (Throwable t) {
            Logging.LOGGER_MAPDISPLAY.warning("Failed to stop map display debug controller: " + t.getMessage());
        } finally {
            display.setUpdateDispatcher(previousDispatcher);
        }

        SwingUtilities.invokeLater(() -> {
            frame.dispose();
            closedLatch.countDown();
        });
    }

    private void startTicking() {
        controller.start(WIDTH, HEIGHT);

        long tickTimeNanos = TimeUnit.SECONDS.toNanos(1) / TPS;
        executor.scheduleAtFixedRate(() -> {
            if (closed) {
                return;
            }

            try {
                processInputTick();
                controller.tick();
                renderTick();
            } catch (Throwable t) {
                Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Map display debug window tick failed", t);
                close();
            }
        }, 0, tickTimeNanos, TimeUnit.NANOSECONDS);
    }

    private void processInputTick() {
        EnumSet<MapPlayerInput.Key> currentKeys = pressedKeys.isEmpty()
                ? EnumSet.noneOf(MapPlayerInput.Key.class)
                : EnumSet.copyOf(pressedKeys);

        if (currentKeys.isEmpty()) {
            keyRepeat = 0;
        } else {
            keyRepeat++;
        }

        for (MapPlayerInput.Key key : MapPlayerInput.Key.values()) {
            boolean wasPressed = previousKeys.contains(key);
            boolean isPressed = currentKeys.contains(key);

            if (isPressed && (!wasPressed || keyRepeat > 15)) {
                MapKeyEvent event = MapKeyEvent.createTestEvent(display, null, key, keyRepeat);
                display.onKeyPressed(event);
                display.getRootWidget().onKeyPressed(event);
            }

            if (isPressed) {
                MapKeyEvent event = MapKeyEvent.createTestEvent(display, null, key, keyRepeat);
                display.onKey(event);
                display.getRootWidget().onKey(event);
            } else if (wasPressed) {
                MapKeyEvent event = MapKeyEvent.createTestEvent(display, null, key, keyRepeat);
                display.onKeyReleased(event);
                display.getRootWidget().onKeyReleased(event);
            }
        }

        previousKeys = currentKeys;
    }

    private void renderTick() {
        byte[] liveBuffer = display.getLiveBuffer();
        if (liveBuffer != null && liveBuffer.length == imageBuffer.length) {
            System.arraycopy(liveBuffer, 0, imageBuffer, 0, imageBuffer.length);
            panel.repaint();
        }
    }

    private static MapPlayerInput.Key fromKeyCode(int keyCode) {
        switch (keyCode) {
        case KeyEvent.VK_W:
            return MapPlayerInput.Key.UP;
        case KeyEvent.VK_S:
            return MapPlayerInput.Key.DOWN;
        case KeyEvent.VK_A:
            return MapPlayerInput.Key.LEFT;
        case KeyEvent.VK_D:
            return MapPlayerInput.Key.RIGHT;
        case KeyEvent.VK_SPACE:
            return MapPlayerInput.Key.ENTER;
        case KeyEvent.VK_SHIFT:
            return MapPlayerInput.Key.BACK;
        default:
            return null;
        }
    }

    private static int calculateAutoScale() {
        int width;
        int height;
        try {
            java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
            java.awt.GraphicsDevice gd = ge.getDefaultScreenDevice();
            java.awt.GraphicsConfiguration gc = gd.getDefaultConfiguration();
            java.awt.Rectangle bounds = gc.getBounds();
            java.awt.geom.AffineTransform transform = gc.getDefaultTransform();
            width = (int) Math.round(bounds.width * transform.getScaleX());
            height = (int) Math.round(bounds.height * transform.getScaleY());
        } catch (Throwable t) {
            java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            width = screenSize.width;
            height = screenSize.height;
        }

        width = Math.max(1, width);
        height = Math.max(1, height);

        int fitScaleX = (int) Math.floor((width * 0.92) / (double) WIDTH);
        int fitScaleY = (int) Math.floor((height * 0.82) / (double) HEIGHT);
        int fitScale = Math.min(fitScaleX, fitScaleY);

        int fullScale = Math.min(width / WIDTH, height / HEIGHT);
        int preferredMinimum = Math.min(8, Math.max(1, fullScale));
        return Math.max(1, Math.min(fullScale, Math.max(fitScale, preferredMinimum)));
    }

    private final class DebugPanel extends JComponent {
        private static final long serialVersionUID = 1L;

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(WIDTH * scale, HEIGHT * scale);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                    java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        }
    }

    private static final class DebugUpdateDispatcher implements MapDisplay.UpdateDispatcher {
        @Override
        public void sendMapUpdate(MapDisplay display, Player viewer, MapDisplayTile.Update mapUpdate) {
            // In debug mode all rendering is read directly from display.getLiveBuffer()
        }
    }
}
