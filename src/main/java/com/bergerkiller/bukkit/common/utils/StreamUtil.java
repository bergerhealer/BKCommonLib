package com.bergerkiller.bukkit.common.utils;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
	 * Returns an iterable that can be used to navigate all the files in a directory.
	 * The listed files include sub-directories.<br>
	 * <br>
	 * <b>Implementers note:</b><br>
	 * Might be used to optimize file listing for Java 1.7.
	 * Alternatively, perhaps some native calls will do the job.
	 * The goal is providing faster directory listing than the default
	 * {@link File#listFiles()} method can offer.
	 * 
	 * @param directory to list the file contents of
	 * @return Iterable of Files available in the directory
	 */
	public static Iterable<File> listFiles(File directory) {
		return Arrays.asList(directory.listFiles());
	}

	/**
	 * Gets the amount of bytes of data stored on disk by a specific file or in a specific folder
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
	 * Deletes a file or directory.
	 * This method will attempt to delete all files in a directory
	 * if a directory is specified.
	 * All files it could not delete will be returned.
	 * If the returned list is empty then deletion was successful.
	 * The returned list is unmodifiable.
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
	 * Creates a new FileOutputStream to a file.
	 * If the file does not yet exist a new file is created.
	 * The contents of existing files will be overwritten.
	 * 
	 * @param file to open
	 * @return a new FileOutputStream for the (created) file
	 * @throws IOException in case opening the file failed
	 * @throws SecurityException in case the Security Manager (if assigned) denies access
	 */
	public static FileOutputStream createOutputStream(File file) throws IOException, SecurityException {
		return createOutputStream(file, false);
	}

	/**
	 * Creates a new FileOutputStream to a file.
	 * If the file does not yet exist a new file is created.
	 * 
	 * @param file to open
	 * @param append whether to append to existing files or not
	 * @return a new FileOutputStream for the (created) file
	 * @throws IOException in case opening the file failed
	 * @throws SecurityException in case the Security Manager (if assigned) denies access
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
	 * Tries to copy a file or directory from one place to the other.
	 * If copying fails along the way the error is printed and false is returned.
	 * Note that when copying directories it may result in an incomplete copy.
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
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Copies a file or directory from one place to the other.
	 * If copying fails along the way an IOException is thrown.
	 * Note that when copying directories it may result in an incomplete copy.
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
}
