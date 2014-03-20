package org.openforis.commons.io.csv;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.List;

import org.openforis.commons.io.flat.FlatDataStream;
import org.openforis.commons.io.flat.FlatRecord;

import au.com.bytecode.opencsv.CSVReader;

/**
 * 
 * @author G. Miceli
 * @author M. Togna
 * @author S. Ricci
 *
 */
public class CsvReader extends CsvProcessor implements FlatDataStream, Closeable {

	private CSVReader csv;
	private long linesRead;
	private boolean headersRead;
	private File file;
	
    public static final char DEFAULT_SEPARATOR = ',';
    public static final char DEFAULT_QUOTE_CHARACTER = '"';

	public CsvReader(String filename) throws FileNotFoundException {
		this(filename, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER);
	}
	
	public CsvReader(String fileName, char separator, char quoteChar) throws FileNotFoundException {
		this(new File(fileName), separator, quoteChar);
	}
	
	public CsvReader(File file) throws FileNotFoundException {
		this(file, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER);
	}
	
	public CsvReader(File file, char separator, char quoteChar) throws FileNotFoundException {
		this(new FileReader(file), separator, quoteChar);
		this.file = file;
	}
	
	/**
	 * @deprecated Call {@link CsvReader#CsvReader(File)} instead.
	 */
	@Deprecated
	public CsvReader(Reader reader) {
		this(reader, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER);
	}
	
	/**
	 * @deprecated Call {@link CsvReader#CsvReader(File, char, char)} instead.
	 */
	@Deprecated
	public CsvReader(Reader reader, char separator, char quoteChar) {
		csv = new CSVReader(reader, separator, quoteChar);
		headersRead = false;
		linesRead = 0;
	}

	public void readHeaders() throws IOException {
		if ( headersRead ) {
			throw new IllegalStateException("Headers already read");
		}
		String[] headers = csv.readNext();
		setColumnNames(headers);
		headersRead = true;
	}

	public CsvLine readNextLine() throws IOException {
		if ( !headersRead ) {
			throw new IllegalStateException("Headers must be read first");
		}
		String[] line = csv.readNext();
		if ( line == null ) {
			return null;
		} else {
			linesRead ++;
			return new CsvLine(this, line);
		}
	}
	
	@Override
	public void close() throws IOException {
		csv.close();
	}

	public boolean isHeadersRead() {
		return headersRead;
	}

	public long getLinesRead() {
		return linesRead;
	}
	
	@Override
	public List<String> getFieldNames() {
		return getColumnNames();
	}

	@Override
	public FlatRecord nextRecord() throws IOException {
		return readNextLine();
	}
	
	/**
	 * Returns the number of lines including the headers
	 * @return
	 * @throws IOException
	 */
	public int size() throws IOException {
		if ( this.file == null ) {
			throw new IllegalStateException("Source file not properly initialized");
		}
		LineNumberReader lineReader = null;
		try {
			lineReader = new LineNumberReader(new FileReader(this.file));
			lineReader.skip(Long.MAX_VALUE);
			return lineReader.getLineNumber();
		} catch(IOException e) {
			throw e;
		} finally {
			lineReader.close();
		}
	}
	
}
