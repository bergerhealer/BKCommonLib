package com.bergerkiller.bukkit.common.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bukkit.World;

/**
 * Async chunk loading with a runnable callback is no longer supported by the server,
 * but getChunkAt is still supported async. This class manages a thread pool to process
 * asynchronous chunk loading on.
 */
public class CommonChunkLoaderPool {
    private final ExecutorService executorService;

    public CommonChunkLoaderPool() {
        this.executorService = Executors.newFixedThreadPool(2);
    }

    public void disable() {
        this.executorService.shutdown();
        try {
            this.executorService.awaitTermination(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void queueChunkLoad(World world, int cx, int cz, Runnable runnable) {
        this.executorService.submit(new LoadChunkTask(world, cx, cz, runnable));
    }

    private static final class LoadChunkTask implements Runnable {
        private final World world;
        private final int cx, cz;
        private final Runnable runnable;

        public LoadChunkTask(World world, int cx, int cz, Runnable runnable) {
            this.world = world;
            this.cx = cx;
            this.cz = cz;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            CommonNMS.getHandle(world).getChunkProviderServer().getChunkAt(this.cx, this.cz);
            try {
                runnable.run();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
