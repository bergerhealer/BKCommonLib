package com.bergerkiller.bukkit.common.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;

/**
 * Helper class for writing text data to a file asynchronously.
 * Use {@link #write(file, inputData)} to start writing. The returned
 * completable future can be used to wait or poll for the writing to finish.
 */
public class AsyncTextWriter {

    /**
     * Starts writing the text data to a temporary file, then replaces the file with this file.
     * This is more resilient to a sudden software shutdown or write errors. The original file is
     * not modified if anything goes wrong.
     * If the parent directory of the file does not exist, it is created if possible.
     * 
     * @param file       File to write to
     * @param inputData  Text data to write
     * @return Completable future completed when writing finishes. May complete exceptionally if I/O errors occur.
     */
    public static CompletableFuture<Void> writeSafe(File file, String inputData) {
        return writeSafe(file, CharBuffer.wrap(inputData));
    }

    /**
     * Starts writing the text data to a temporary file, then replaces the file with this file.
     * This is more resilient to a sudden software shutdown or write errors. The original file is
     * not modified if anything goes wrong.
     * If the parent directory of the file does not exist, it is created if possible.
     * 
     * @param file       File to write to
     * @param inputData  Text data to write
     * @return Completable future completed when writing finishes. May complete exceptionally if I/O errors occur.
     */
    public static CompletableFuture<Void> writeSafe(File file, CharBuffer inputData) {
        final File tempFile = new File(file.getAbsoluteFile().getParentFile(), file.getName() + "." + System.currentTimeMillis() + ".tmp");
        CompletableFuture<Void> tempWrite = write(tempFile, inputData);
        final CompletableFuture<Void> future = new CompletableFuture<Void>();
        tempWrite.handle(new BiFunction<Void, Throwable, Void>() {
            @Override
            public Void apply(Void ignored, Throwable baseThrown) {
                // Handle errors when trying to write to the temp file
                if (baseThrown != null) {
                    future.completeExceptionally(baseThrown);
                    return null;
                }

                // Try to move the temporary file to the destination file
                this.move();
                return null;
            }

            public void move() {
                try {
                    StreamUtil.atomicReplace(tempFile, file);
                    future.complete(null);
                } catch (Throwable t) {
                    future.completeExceptionally(t);
                }
            }
        });
        return future;
    }

    /**
     * Starts writing the text data to a file. The original contents of the file are replaced.
     * If the parent directory of the file does not exist, it is created if possible.
     * 
     * @param file       File to write to
     * @param inputData  Text data to write
     * @return Completable future completed when writing finishes. May complete exceptionally if I/O errors occur.
     */
    public static CompletableFuture<Void> write(File file, String inputData) {
        return write(file, CharBuffer.wrap(inputData));
    }

    /**
     * Starts writing the text data to a file. The original contents of the file are replaced.
     * If the parent directory of the file does not exist, it is created if possible.
     * 
     * @param file       File to write to
     * @param inputData  Text data to write
     * @return Completable future completed when writing finishes. May complete exceptionally if I/O errors occur.
     */
    public static CompletableFuture<Void> write(final File file, final CharBuffer inputData) {
        try {
            if (CommonPlugin.hasInstance() && CommonPlugin.getInstance().forceSynchronousSaving()) {
                // Perform all the writing right here, rather than asynchronously on another thread
                // Do it in chunks
                try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                    char[] tmp = new char[4096];
                    int remaining;
                    while ((remaining = Math.min(tmp.length, inputData.remaining())) > 0) {
                        inputData.get(tmp, 0, remaining);
                        writer.write(tmp, 0, remaining);
                    }
                }

                // Done!
                return CompletableFuture.completedFuture(null);
            } else {
                return CommonUtil.runCheckedAsync(() -> {
                    // Pick an appropriate buffer size. We don't need a large buffer for small amounts of text.
                    // Use size that is a power of 2 because it might be more performant
                    int bufferSize = 512;
                    int len = inputData.length();
                    while (len > bufferSize && bufferSize < 16384) {
                        bufferSize <<= 1;
                    }
                    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufferSize);
                    CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();

                    // Create directories of where the file is located
                    file.getAbsoluteFile().getParentFile().mkdirs();

                    try (FileOutputStream stream = new FileOutputStream(file, false); FileChannel channel = stream.getChannel()) {
                        boolean finished = false;
                        while (true) {
                            // Encode or flush to the byte buffer starting at position 0
                            ((java.nio.Buffer) byteBuffer).clear();
                            CoderResult result;
                            if (finished) {
                                result = encoder.flush(byteBuffer);
                            } else {
                                result = encoder.encode(inputData, byteBuffer, true);
                            }

                            // If errors happen, throw them, which will be handled by the completable future
                            // The finally block will clean up the file
                            if (result.isError()) {
                                result.throwException();
                            }

                            // Re-read from the byte buffer and write to file
                            // If we wrote 0 bytes or a negative amount then something fishy is going on
                            // Likely the underlying I/O is overloaded
                            // Stress testing showed that without this sleep the entire VM
                            // can crash at random, which is obviously very bad.
                            ((java.nio.Buffer) byteBuffer).flip();
                            while (byteBuffer.hasRemaining()) {
                                if (channel.write(byteBuffer) <= 0) {
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {}
                                }
                            }

                            if (finished) {
                                break;
                            } else if (result.isUnderflow()) {
                                finished = true; // flush and done
                            }
                        }

                        // Flush to disk, important for 'safe' writing
                        channel.force(true);
                    }
                });
            }
        } catch (Throwable t) {
            CompletableFuture<Void> future = new CompletableFuture<Void>();
            future.completeExceptionally(t);
            return future;
        }
    }
}
