package com.bergerkiller.bukkit.common.utils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.UnmodifiableListCollector;

/**
 * Stream and File-related utility methods
 */
public class StreamUtil {

    public static UUID readUUID(DataInputStream stream) throws IOException {
        return new UUID(stream.readLong(), stream.readLong());
    }

    public static void writeUUID(DataOutputStream stream, UUID uuid) throws IOException {
        stream.writeLong(uuid.getMostSignificantBits());
        stream.writeLong(uuid.getLeastSignificantBits());
    }

    public static void writeIndent(BufferedWriter writer, int indent) throws IOException {
        for (int i = 0; i < indent; i++) {
            writer.write(' ');
        }
    }

    /**
     * Returns an iterable that can be used to navigate all the files in a
     * directory. The listed files include sub-directories.<br>
     * <br>
     * <b>Implementers note:</b><br>
     * Might be used to optimize file listing for Java 1.7. Alternatively,
     * perhaps some native calls will do the job. The goal is providing faster
     * directory listing than the default {@link File#listFiles()} method can
     * offer.
     *
     * @param directory to list the file contents of
     * @return Iterable of Files available in the directory
     */
    public static Iterable<File> listFiles(File directory) {
        return Arrays.asList(directory.listFiles());
    }

    /**
     * Gets the amount of bytes of data stored on disk by a specific file or in
     * a specific folder
     *
     * @param file to get the size of
     * @return File/folder size in bytes
     */
    public static long getFileSize(File file) {
        if (!file.exists()) {
            return 0L;
        } else if (file.isDirectory()) {
            long size = 0L;
            for (File subfile : listFiles(file)) {
                size += getFileSize(subfile);
            }
            return size;
        } else {
            return file.length();
        }
    }

    /**
     * Deletes a file or directory. This method will attempt to delete all files
     * in a directory if a directory is specified. All files it could not delete
     * will be returned. If the returned list is empty then deletion was
     * successful. The returned list is unmodifiable.
     *
     * @param file to delete
     * @return a list of files it could not delete
     */
    public static List<File> deleteFile(File file) {
        if (file.isDirectory()) {
            List<File> failFiles = new ArrayList<File>();
            deleteFileList(file, failFiles);
            return Collections.unmodifiableList(failFiles);
        } else if (file.delete()) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(Arrays.asList(file));
        }
    }

    private static boolean deleteFileList(File file, List<File> failFiles) {
        if (file.isDirectory()) {
            boolean success = true;
            for (File subFile : listFiles(file)) {
                success &= deleteFileList(subFile, failFiles);
            }
            if (success) {
                file.delete();
            }
            return success;
        } else if (file.delete()) {
            return true;
        } else {
            failFiles.add(file);
            return false;
        }
    }

    /**
     * Creates a new FileOutputStream to a file. If the file does not yet exist
     * a new file is created. The contents of existing files will be
     * overwritten.
     *
     * @param file to open
     * @return a new FileOutputStream for the (created) file
     * @throws IOException in case opening the file failed
     * @throws SecurityException in case the Security Manager (if assigned)
     * denies access
     */
    public static FileOutputStream createOutputStream(File file) throws IOException, SecurityException {
        return createOutputStream(file, false);
    }

    /**
     * Creates a new FileOutputStream to a file. If the file does not yet exist
     * a new file is created.
     *
     * @param file to open
     * @param append whether to append to existing files or not
     * @return a new FileOutputStream for the (created) file
     * @throws IOException in case opening the file failed
     * @throws SecurityException in case the Security Manager (if assigned)
     * denies access
     */
    public static FileOutputStream createOutputStream(File file, boolean append) throws IOException, SecurityException {
        File directory = file.getAbsoluteFile().getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create the parent directory of the file");
        }
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Failed to create the new file to write to");
        }
        return new FileOutputStream(file, append);
    }

    /**
     * Tries to copy a file or directory from one place to the other. If copying
     * fails along the way the error is printed and false is returned. Note that
     * when copying directories it may result in an incomplete copy.
     *
     * @param sourceLocation
     * @param targetLocation
     * @return True if copying succeeded, False if not
     */
    public static boolean tryCopyFile(File sourceLocation, File targetLocation) {
        try {
            copyFile(sourceLocation, targetLocation);
            return true;
        } catch (IOException ex) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to copy file " + sourceLocation + " to " + targetLocation, ex);
            return false;
        }
    }

    /**
     * Copies a file or directory from one place to the other. If copying fails
     * along the way an IOException is thrown. Note that when copying
     * directories it may result in an incomplete copy.
     *
     * @param sourceLocation
     * @param targetLocation
     * @throws IOException
     */
    public static void copyFile(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdirs();
            }
            for (File subFile : listFiles(sourceLocation)) {
                String subFileName = subFile.getName();
                copyFile(new File(sourceLocation, subFileName), new File(targetLocation, subFileName));
            }
        } else {
            // Start a new stream
            FileInputStream input = null;
            FileOutputStream output = null;
            FileChannel inputChannel = null;
            FileChannel outputChannel = null;
            try {
                // Initialize file streams
                input = new FileInputStream(sourceLocation);
                inputChannel = input.getChannel();
                output = createOutputStream(targetLocation);
                outputChannel = output.getChannel();
                // Start transferring
                long transfered = 0;
                long bytes = inputChannel.size();
                while (transfered < bytes) {
                    transfered += outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                    outputChannel.position(transfered);
                }
            } finally {
                // Close input stream
                if (inputChannel != null) {
                    inputChannel.close();
                } else if (input != null) {
                    input.close();
                }
                // Close output stream
                if (outputChannel != null) {
                    outputChannel.close();
                } else if (output != null) {
                    output.close();
                }
            }
        }
    }

    /**
     * Obtains a file found in a parent directory. Child name casing is ignored.
     * If the child file is found in the parent directory with the same casing,
     * this is returned. If this is not the case, but a file with the same name
     * but different casing is available, then this is returned instead. If none
     * could be found, a file with the casing of the child is returned
     * alternatively.<br>
     * <br>
     * The returned File will point to an existing file <b>on the current
     * OS</b>, if system-independent file paths are needed or paths with
     * accurate File System casing, {@link File#getCanonicalFile()} should be
     * used on the resulting File.
     *
     * @param parent directory
     * @param child file name
     * @return File pointing to the child file found in the parent directory
     */
    public static File getFileIgnoreCase(File parent, String child) {
        if (LogicUtil.nullOrEmpty(child)) {
            return parent;
        }
        File childFile = new File(parent, child);
        if (!childFile.exists() && parent.exists()) {
            int firstFolderIdx = child.indexOf(File.separator);
            if (firstFolderIdx != -1) {
                // Process folder structure recursively
                File newParent = getFileIgnoreCase(parent, child.substring(0, firstFolderIdx));
                String newChild = child.substring(firstFolderIdx + 1);
                return getFileIgnoreCase(newParent, newChild);
            } else {
                // No folder specified in child - find the accurate file name
                for (String childName : parent.list()) {
                    if (childName.equalsIgnoreCase(child)) {
                        return new File(parent, childName);
                    }
                }
            }
        }
        return childFile;
    }

    /**
     * Version of {@link Collectors#toList()} which wraps the result up in an unmodifiable List.
     * 
     * @param <T>
     * @return unmodifiable list
     */
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, List<T>> toUnmodifiableList() {
        return UnmodifiableListCollector.INSTANCE;
    }

    /**
     * Instantiates a new DeflaterOutputStream with the default settings for writing compressed data
     * to an output stream. Sets a specific compression level
     *
     * @param stream Stream to write compressed data to
     * @param deflaterCompressionLevel Level, see Deflater static constants
     * @return new Deflater Output Stream
     * @throws IOException
     */
    public static DeflaterOutputStream createDeflaterOutputStreamWithCompressionLevel(OutputStream stream, int deflaterCompressionLevel) throws IOException {
        return new DeflaterOutputStream(stream, new Deflater(deflaterCompressionLevel), 512, false);
    }

    /**
     * Attempts to perform an atomic move operation from one file to another, overwriting the destination
     * file's contents. This attempts to use the safest method available on the underlying filesystem.
     *
     * @param fromFile File to move
     * @param toFile File to replace with the file
     * @throws IOException If the atomic move failed for some reason
     */
    public static void atomicReplace(File fromFile, File toFile) throws IOException {
        // First try a newer Java's Files.move as this allows for an atomic move with overwrite
        // If this doesn't work, only then do we try our custom non-atomic methods
        try {
            Files.move(fromFile.toPath(), toFile.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            return;
        } catch (AtomicMoveNotSupportedException | UnsupportedOperationException unsupportedIgnored) {
            // Efficient move using this method is not supported, use a fallback
        }

        // More dangerous: delete target file, then move the temp file to it
        // This operation is not atomic and could fail
        if (toFile.delete() && fromFile.renameTo(toFile)) {
            return;
        }

        // Even more risky: copy the data by using file streams
        // This could result in partial data in the destination file :(
        if (StreamUtil.tryCopyFile(fromFile, toFile)) {
            fromFile.delete();
            return;
        }

        // No idea anymore
        throw new IOException("Failed to move " + fromFile.getAbsolutePath() + " to " + toFile.getAbsolutePath());
    }
}
