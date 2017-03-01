package org.openforis.commons.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author S. Ricci
 *
 */
public class OpenForisIOUtils {

	public static final String UTF_8 = "UTF-8";

	public static File copyToTempFile(InputStream is) {
		return copyToTempFile(is, null);
	}
	
	public static File copyToTempFile(InputStream is, String extension) {
		try {
			String suffix = StringUtils.isBlank(extension) ? "": "." + extension;
			File tempFile = File.createTempFile("openforis", suffix);
			FileUtils.copyInputStreamToFile(is, tempFile);
			return tempFile;
		} catch (IOException e) {
			throw new RuntimeException("Error copying to temp file: " + e.getMessage());
		}
	}

	public static File copyToTempFile(Reader reader) {
		return copyToTempFile(reader, null);
	}
	
	public static File copyToTempFile(Reader reader, String extension) {
		InputStream is = toInputStream(reader);
		return copyToTempFile(is, extension);
	}

	public static InputStream toInputStream(Reader reader) {
		return new ReaderInputStream(reader, UTF_8);
	}

	public static InputStreamReader toReader(InputStream is) {
		try {
			return new InputStreamReader(is, UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Reader toReader(File file) throws FileNotFoundException {
		return toReader(file, OpenForisIOUtils.UTF_8);
	}
	
	public static Reader toReader(File file, String charsetName) throws FileNotFoundException {
		try {
			return new InputStreamReader(new FileInputStream(file), charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
