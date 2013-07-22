package com.bergerkiller.bukkit.common.utils;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
				targetLocation.mkdir();
			}
			for (String subFileName : sourceLocation.list()) {
				copyFile(new File(sourceLocation, subFileName), new File(targetLocation, subFileName));
			}
		} else {
			// Create file
			if (!targetLocation.exists()) {
				targetLocation.createNewFile();
			}
			// Start a new stream
			FileInputStream input = null;
			FileOutputStream output = null;
			FileChannel inputChannel = null;
			FileChannel outputChannel = null;
			try {
				// Initialize file streams
    			input = new FileInputStream(sourceLocation);
    			inputChannel = input.getChannel();
    			output = new FileOutputStream(targetLocation);
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
