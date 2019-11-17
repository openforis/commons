package org.openforis.commons.io.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import org.openforis.commons.io.OpenForisIOUtils;

import au.com.bytecode.opencsv.CSVReader;

/**
 * 
 * @author G. Miceli
 * @author M. Togna
 * @author S. Ricci
 *
 */
class OpenCsvReader extends CsvReaderDelegate {

	private final CSVReader csv;
	private File file;

	public static final char DEFAULT_SEPARATOR = ',';
	public static final char DEFAULT_QUOTE_CHARACTER = '"';

	public OpenCsvReader(File file, String charsetName, char separator, char quoteChar, CsvReader csvReader)
			throws FileNotFoundException {
		this(OpenForisIOUtils.toReader(file, charsetName), separator, quoteChar, csvReader);
		this.file = file;
	}

	@Deprecated
	public OpenCsvReader(Reader reader, char separator, char quoteChar, CsvReader csvReader) {
		super(csvReader);
		this.csv = new CSVReader(reader, separator, quoteChar);
	}

	@Override
	public String[] readHeadersInternal() throws IOException {
		return csv.readNext();
	}

	@Override
	protected String[] line(long lineIdx) throws IOException {
		return csv.readNext();
	}

	@Override
	public void close() throws IOException {
		csv.close();
	}

	/**
	 * Returns the number of lines including the headers
	 * 
	 * @return
	 * @throws IOException
	 */
	public int size() throws IOException {
		if (this.file == null) {
			throw new IllegalStateException("Source file not properly initialized");
		}
		LineNumberReader lineReader = null;
		try {
			lineReader = new LineNumberReader(new FileReader(this.file));
			lineReader.skip(Long.MAX_VALUE);
			return lineReader.getLineNumber();
		} catch (IOException e) {
			throw e;
		} finally {
			lineReader.close();
		}
	}

}
