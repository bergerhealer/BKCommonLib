package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.logging.Level;

public class FileConfiguration extends BasicConfiguration {
    private static final Map<File, CompletableFuture<Void>> ongoingSaves = new HashMap<File, CompletableFuture<Void>>();
    private final File file;

    public FileConfiguration(JavaPlugin plugin) {
        this(plugin, "config.yml");
    }

    public FileConfiguration(JavaPlugin plugin, String filepath) {
        this(new File(CommonUtil.getPluginDataFolder(plugin), filepath));
    }

    public FileConfiguration(String filepath) {
        this(new File(filepath));
    }

    public FileConfiguration(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("File is not allowed to be null!");
        }
        this.file = file;
    }

    public boolean exists() {
        return this.file.exists();
    }

    /**
     * Loads this File Configuration from file
     */
    public void load() {
        // Make sure a previous save() is completed first
        flushSaveOperation(this.file);

        // Ignore loading if file doesn't exist
        if (!file.exists()) {
            return;
        }
        try {
            if (CommonPlugin.hasInstance() && CommonPlugin.getInstance().forceSynchronousSaving()) {
                char[] data;
                {
                    // Read bytes
                    byte[] data_bytes = Files.readAllBytes(this.file.toPath());

                    // Decode as char[] using UTF-8
                    String s = new String(data_bytes, StandardCharsets.UTF_8);
                    data = s.toCharArray();
                }

                // Safety: erase any use of 0-char values in the original data
                for (int i = 0; i < data.length; i++) {
                    if (data[i] == 0) {
                        int num_nul_chars = 1;
                        for (int j = i+1; j < data.length && data[j] == 0; j++) {
                            num_nul_chars++;
                        }
                        char[] new_data = new char[data.length - num_nul_chars];
                        System.arraycopy(data, 0, new_data, 0, i);
                        System.arraycopy(data, i + num_nul_chars, new_data, i, new_data.length - i);
                        data = new_data;
                        i--;
                    }
                }

                // Load it in
                this.loadFromReader(new CharArrayReader(data));
            } else {
                this.loadFromStream(new FileInputStream(this.file));
            }
        } catch (Throwable t) {
        	Logging.LOGGER_CONFIG.log(Level.SEVERE, "An error occured while loading file '" + this.file + "'");
            try {
                File backup = new File(this.file.getPath() + ".old");
                StreamUtil.copyFile(this.file, backup);
                Logging.LOGGER_CONFIG.log(Level.SEVERE, "A backup of this (corrupted?) file named '" + backup.getName() + "' can be found in case you wish to restore", t);
            } catch (IOException ex) {
            	Logging.LOGGER_CONFIG.log(Level.SEVERE, "A backup of this (corrupted?) file could not be made and its contents may be lost (overwritten)", t);
            }
        }
    }

    /**
     * Saves this File Configuration to file and waits for writing to complete
     */
    public void saveSync() {
        save();
        flushSaveOperation(this.file);
    }

    /**
     * Saves this File Configuration to file asynchronously in the background.
     * A guarantee is made that future calls to {@link #load()} for this FileConfiguration
     * or a different instance for the same file will wait for this save to complete.<br>
     * <br>
     * All pending saves are flushed when BKCommonLib terminates.
     */
    public void save() {
        final boolean fileWasGenerated = !this.exists();

        // Wait for a previous save operation to be finished (infinitely), then schedule our own
        putSaveOperation(this.file, Long.MAX_VALUE, ()-> {
            // Once writing finishes, perform cleanup and also handle errors
            CompletableFuture<Void> future = saveToFileAsync(file);
            return future.handle(new BiFunction<Void, Throwable, Void>() {
                @Override
                public Void apply(Void ignored, Throwable error) {
                    // Attempt cleanup
                    synchronized (ongoingSaves) {
                        CompletableFuture<Void> removed = ongoingSaves.remove(file);
                        if (removed != null && removed != future) {
                            ongoingSaves.put(file, removed);
                        }
                    }

                    // Log errors
                    if (error != null) {
                        Logging.LOGGER_CONFIG.log(Level.SEVERE, "An error occured while saving to file '" + file + "':", error);
                        return null;
                    }

                    // Log 'generated' message
                    if (fileWasGenerated) {
                        Logging.LOGGER_CONFIG.log(Level.INFO, "File '" + file + "' has been generated");
                    }

                    return null;
                }
            });
        });
    }

    /**
     * Finishes any save operation that is going on for a particular file
     * 
     * @param file
     */
    public static void flushSaveOperation(File file) {
        flushSaveOperation(file, Long.MAX_VALUE);
    }

    /**
     * Finishes any save operation that is going on for a particular file
     * with a timeout in milliseconds. If the timeout is reached, then
     * false is returned.
     * 
     * @param file
     * @param timeout
     * @return Whether flushing was sucessful, False if and only if the timeout was reached
     */
    public static boolean flushSaveOperation(File file, long timeout) {
        return putSaveOperation(file, timeout, null);
    }

    /**
     * Finishes all FileConfiguration save operations that are still ongoing
     */
    public static void flushAllSaveOperations() {
        flushAllSaveOperationsInDirectory(null);
    }

    /**
     * Finishes all FileConfiguration save operations that are still ongoing inside a directory.
     * Files inside sub-directories of the directory are waited for as well.
     * If the directory is null, then all ongoing save operations are waited for.
     * 
     * @param directory
     */
    public static void flushAllSaveOperationsInDirectory(File directory) {
        for (File file : findSaveOperationsInDirectory(directory)) {
            flushSaveOperation(file);
        }
    }

    /**
     * Finishes all FileConfiguration save operations that are still ongoing inside the configuration
     * directory of a plugin.
     * 
     * @param plugin
     */
    public static void flushAllSaveOperationsForPlugin(Plugin plugin) {
        flushAllSaveOperationsInDirectory(plugin.getDataFolder());
    }

    /**
     * Gets all the files for which FileConfiguration save operations are
     * still ongoing inside a directory. If the directory specified is null,
     * then all ongoing save operations are returned. Due to the asynchronous
     * nature of saving, the saving may have already be completed by the time
     * the file is iterated.<br>
     * <br>
     * The iterable returned is generated live, which means it can be used
     * more than once and new save operations will show up.
     * 
     * @param directory
     * @return ongoing save operations
     */
    public static Iterable<File> findSaveOperationsInDirectory(File directory) {
        return ()-> {
            return new Iterator<File>() {
                private File _next = null;

                @Override
                public boolean hasNext() {
                    if (_next != null) {
                        return true;
                    } else {
                        synchronized (ongoingSaves) {
                            if (directory == null) {
                                for (File file : ongoingSaves.keySet()) {
                                    _next = file;
                                    return true;
                                }
                            } else {
                                Path directoryPath = directory.getAbsoluteFile().toPath();
                                for (File file : ongoingSaves.keySet()) {
                                    if (file.getAbsoluteFile().toPath().startsWith(directoryPath)) {
                                        _next = file;
                                        return true;
                                    }
                                }
                            }
                        }
                        return false;
                    }
                }

                @Override
                public File next() {
                    if (hasNext()) {
                        File result = _next;
                        _next = null;
                        return result;
                    } else {
                        throw new NoSuchElementException();
                    }
                }
            };
        };
    }

    // Puts an operation into the mapping. If futureSupplier is null, removes it
    // Previous operation is waited for completion
    // If timeout is 0, then this operation will fail if an operation already exists
    // If timeout is Long MAX_VALUE then it will wait for as long as needed
    private static boolean putSaveOperation(File file, long timeout, Supplier<CompletableFuture<Void>> futureSupplier) {
        while (true) {
            CompletableFuture<Void> existing;
            synchronized (ongoingSaves) {
                existing = ongoingSaves.remove(file);
                if (existing != null && !existing.isDone()) {
                    ongoingSaves.put(file, existing);
                } else if (futureSupplier != null) {
                    ongoingSaves.put(file, futureSupplier.get());
                    return true;
                } else {
                    return true;
                }
            }
            if (timeout <= 0) {
                return false;
            }
            try {
                if (timeout == Long.MAX_VALUE) {
                    existing.get();
                } else {
                    existing.get(timeout, TimeUnit.MILLISECONDS);
                }
            } catch (TimeoutException ex) {
                return false;
            } catch (Throwable t) {}
        }
    }
}
