package com.bergerkiller.bukkit.common.io;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileLock;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.bergerkiller.bukkit.common.utils.StreamUtil;

/**
 * Helper class for writing text data to a file asynchronously.
 * Use {@link #write(file, inputData)} to start writing. The returned
 * completable future can be used to wait or poll for the writing to finish.
 */
public class AsyncTextWriter {
    private final AsynchronousFileChannel _file;
    private final CharsetEncoder _encoder;
    private final CharBuffer _inputData;
    private final ByteBuffer _writeBufferA;
    private final ByteBuffer _writeBufferB;
    private final CompletableFuture<Void> _future;
    private FileLock _lock;
    private volatile long _position;
    private volatile boolean _finishedReading;
    private volatile boolean _finishedEncoding;

    private AsyncTextWriter(AsynchronousFileChannel file, CharBuffer inputData, int bufferSize) {
        _file = file;
        _encoder = StandardCharsets.UTF_8.newEncoder();
        _inputData = inputData;
        _writeBufferA = ByteBuffer.allocateDirect(bufferSize);
        _writeBufferB = ByteBuffer.allocateDirect(bufferSize);
        _future = new CompletableFuture<Void>();
        _lock = null;
        _position = 0;
        _finishedReading = false;
        _finishedEncoding = false;
    }

    private synchronized void fail(Throwable ex) {
        if (!_future.isCompletedExceptionally()) {
            _future.completeExceptionally(ex);
            close();
        }
    }

    private void close() {
        try {
            if (_lock != null) {
                _lock.release();
                _lock = null;
            }
        } catch (Throwable t) {
            fail(t);
        }
        try {
            _file.close();
        } catch (Throwable t) {
            fail(t);
        }
    }

    private ByteBuffer getBufferA() {
        return this._writeBufferA;
    }

    private ByteBuffer getBufferB() {
        return this._writeBufferB;
    }

    private void start(AsyncTextWriterStep<?> step) {
        step.start(this);
    }

    private void encode(ByteBuffer buffer) {
        buffer.clear();
        CoderResult result;
        if (_finishedReading) {
            result = _encoder.flush(buffer);
        } else {
            result = _encoder.encode(_inputData, buffer, true);
            if (result.isUnderflow()) {
                _finishedReading = true;
                result = _encoder.flush(buffer);
            }
        }
        buffer.flip();
        if (result.isError()) {
            _finishedEncoding = true;
            try {
                result.throwException();
            } catch (Throwable t) {
                _future.completeExceptionally(t);
            }
        } else if (result.isUnderflow()) {
            _finishedEncoding = true;
        }
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
                // First try a newer Java's Files.move as this allows for an atomic move with overwrite
                // If this doesn't work, only then do we try our custom non-atomic methods
                try {
                    Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                    future.complete(null);
                    return;
                } catch (AtomicMoveNotSupportedException | UnsupportedOperationException unsupportedIgnored) {
                    // Efficient move using this method is not supported, use a fallback
                } catch (Throwable t) {
                    future.completeExceptionally(t);
                    return;
                }

                // More dangerous: delete target file, then move the temp file to it
                // This operation is not atomic and could fail
                if (file.delete() && tempFile.renameTo(file)) {
                    future.complete(null);
                    return;
                }

                // Even more risky: copy the data by using file streams
                // This could result in partial data in the destination file :(
                if (StreamUtil.tryCopyFile(tempFile, file)) {
                    tempFile.delete();
                    future.complete(null);
                    return;
                }

                // No idea anymore
                future.completeExceptionally(new IOException("Failed to move " + tempFile.getAbsolutePath() + " to " + file.getAbsolutePath()));
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
    public static CompletableFuture<Void> write(File file, CharBuffer inputData) {
        try {
            // Create directories of where the file is located
            file.getParentFile().mkdirs();

            // Open the file. May throw an error.
            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(file.toPath(),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

            // Pick an appropriate buffer size. We don't need a large buffer for small amounts of text.
            // Use size that is a power of 2 because it might be more performant
            int bufferSize = 512;
            int len = inputData.length();
            while (len > bufferSize && bufferSize < 16384) {
                bufferSize <<= 1;
            }

            // Create the writer and initiate it
            AsyncTextWriter writer = new AsyncTextWriter(fileChannel, inputData, bufferSize);
            writer.start(LOCK_FILE);
            return writer._future;
        } catch (Throwable t) {
            CompletableFuture<Void> future = new CompletableFuture<Void>();
            future.completeExceptionally(t);
            return future;
        }
    }

    private static final AsyncTextWriterStep<FileLock> LOCK_FILE = new AsyncTextWriterStep<FileLock>() {
        @Override
        public void start(AsyncTextWriter writer) {
            writer._file.lock(writer, this);
        }

        @Override
        public void completed(FileLock result, AsyncTextWriter writer) {
            synchronized (writer) {
                writer._lock = result;
                writer.encode(writer.getBufferA());
                writer.start(writer._finishedEncoding ? FINISH_BUFFER_A : WRITE_BUFFER_A);
            }
        }
    };

    private static final AsyncTestWriterStepWriter WRITE_BUFFER_A = new AsyncTestWriterStepWriter(AsyncTextWriter::getBufferA) {
        @Override
        public void start(AsyncTextWriter writer) {
            super.start(writer);
            writer.encode(writer.getBufferB());
        }

        @Override
        public void next(AsyncTextWriter writer) {
            writer.start(writer._finishedEncoding ? FINISH_BUFFER_B : WRITE_BUFFER_B);
        }
    };

    private static final AsyncTestWriterStepWriter WRITE_BUFFER_B = new AsyncTestWriterStepWriter(AsyncTextWriter::getBufferB) {
        @Override
        public void start(AsyncTextWriter writer) {
            super.start(writer);
            writer.encode(writer.getBufferA());
        }

        @Override
        public void next(AsyncTextWriter writer) {
            writer.start(writer._finishedEncoding ? FINISH_BUFFER_A : WRITE_BUFFER_A);
        }
    };

    private static final AsyncTestWriterStepWriter FINISH_BUFFER_A = new AsyncTestWriterStepWriter(AsyncTextWriter::getBufferA) {
        @Override
        public void next(AsyncTextWriter writer) {
            writer.close();
            writer._future.complete(null);
        }
    };

    private static final AsyncTestWriterStepWriter FINISH_BUFFER_B = new AsyncTestWriterStepWriter(AsyncTextWriter::getBufferB) {
        @Override
        public void next(AsyncTextWriter writer) {
            writer.close();
            writer._future.complete(null);
        }
    };

    private static abstract class AsyncTestWriterStepWriter extends AsyncTextWriterStep<Integer> {
        private final Function<AsyncTextWriter, ByteBuffer> _getBufferFunction;

        public AsyncTestWriterStepWriter(Function<AsyncTextWriter, ByteBuffer> getBufferFunction) {
            _getBufferFunction = getBufferFunction;
        }

        /**
         * Execute the next step
         * 
         * @param writer
         */
        protected abstract void next(AsyncTextWriter writer);

        @Override
        public void start(AsyncTextWriter writer) {
            writer._file.write(_getBufferFunction.apply(writer), writer._position, writer, this);
        }

        @Override
        public void completed(Integer result, AsyncTextWriter writer) {
            int num = result.intValue();

            // If we wrote 0 bytes or a negative amount then something fishy is going on
            // Likely the underlying I/O is overloaded
            // Stress testing showed that without this sleep the entire VM
            // can crash at random, which is obviously very bad.
            if (num <= 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
            } else {
                writer._position += num;
            }

            synchronized (writer) {
                ByteBuffer buffer = _getBufferFunction.apply(writer);
                if (buffer.remaining() > 0) {
                    // Write the remainder of the buffer
                    writer._file.write(buffer, writer._position, writer, this);
                } else {
                    this.next(writer);
                }
            }
        }
    }

    // Base class for a writer execution state
    private static abstract class AsyncTextWriterStep<T> implements CompletionHandler<T, AsyncTextWriter> {
        /**
         * Starts this writer step
         * 
         * @param writer
         */
        public abstract void start(AsyncTextWriter writer);

        @Override
        public final void failed(Throwable exc, AsyncTextWriter writer) {
            writer.fail(exc);
        }
    }
}
