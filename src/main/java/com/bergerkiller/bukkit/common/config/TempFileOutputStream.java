package com.bergerkiller.bukkit.common.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.bergerkiller.bukkit.common.utils.StreamUtil;

/**
 * A {@link FileOutputStream} wrapper that writes to a temporary file prior to
 * closing the stream and flushing the data to the official output file.
 * It can be used to safely write data to disk without resulting
 * in partially-written data when writing is interrupted.<br><br>
 * 
 * Flushing this stream does <b>NOT</b> flush data to the output file, only to the
 * temporary file. If the temporary file stream needs to be closed without replacing
 * the original output file, use {@link #close(boolean) close(false)}.
 */
public class TempFileOutputStream extends OutputStream {
	private final FileOutputStream baseStream;
	private final File outputFile;
	private final File tempFile;
	private boolean closed;

	/**
	 * Constructs a new TempFileOutputStream with the output file specified.
	 * The temporary file is the output file with <i>.tmp</i> appended.
	 * 
	 * @param name of the file to eventually flush the data to
	 * @throws IOException if preparing the temporary file failed
	 */
	public TempFileOutputStream(String name) throws IOException {
		this(new File(name), new File(name + ".tmp"));
	}

	/**
	 * Constructs a new TempFileOutputStream with the output file specified.
	 * The temporary file is the output file with <i>.tmp</i> appended.
	 * 
	 * @param file to eventually flush the data to
	 * @throws IOException if preparing the temporary file failed
	 */
	public TempFileOutputStream(File file) throws IOException {
		this(file, new File(file.getPath() + ".tmp"));
	}

	/**
	 * Constructs a new TempFileOutputStream with the output file and temporary file specified.
	 * 
	 * @param outputFile to eventually flush the data to
	 * @param tempFile to write data to before flushing
	 * @throws IOException if preparing the temporary file failed
	 */
	public TempFileOutputStream(File outputFile, File tempFile) throws IOException {
		this(outputFile, tempFile, StreamUtil.createOutputStream(tempFile));
	}

	/**
	 * Constructs a new TempFileOutputStream with the output file, temporary file and temporary base
	 * stream specified. The temporary base stream should be a {@link FileOutputStream} pointing to the
	 * <i>tempFile</i> parameter.
	 * 
	 * @param outputFile to eventually flush the data to
	 * @param tempFile the tempBaseStream is accessing
	 * @param tempBaseStream to write data to before flushing
	 */
	public TempFileOutputStream(File outputFile, File tempFile, FileOutputStream tempBaseStream) {
		super();
		this.baseStream = tempBaseStream;
		this.tempFile = tempFile;
		this.outputFile = outputFile;
		this.closed = false;
	}

	@Override
	public void write(int b) throws IOException {
		baseStream.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		baseStream.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException  {
		baseStream.write(b, off, len);
	}

	@Override
	public void close() throws IOException {
		close(true);
	}

	/**
	 * Closes the temporary file stream, writing any pending data to it. If completeTransfer is set,
	 * the output file is replaced with the temporary file. Typically, this parameter should specify
	 * whether previous writing was successful.
	 * 
	 * @param completeTransfer - True to replace the output file, False to preserve it
	 * @throws IOException - if an I/O error occurs.
	 */
	public void close(boolean completeTransfer) throws IOException {
		if (this.closed) {
			return;
		}
		this.closed = true;

		// Close the base streams
		try {
			super.close();
		} finally {
			baseStream.close();
		}
		if (!completeTransfer) {
			return;
		}

		// Try to delete the output file first
		if (!outputFile.delete() && outputFile.exists()) {
			throw new IOException("Failed to delete existing output file prior to transfer");
		}

		// Move the temp file to the output file
		if (!tempFile.renameTo(outputFile)) {
			throw new IOException("Failed to transfer temporary file to output file");
		}
	}
}