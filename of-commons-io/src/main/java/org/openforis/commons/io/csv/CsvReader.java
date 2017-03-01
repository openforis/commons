package org.openforis.commons.io.csv;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.util.List;
import java.util.Map;

import org.openforis.commons.io.OpenForisIOUtils;
import org.openforis.commons.io.csv.ExcelReader.ExcelParseException;
import org.openforis.commons.io.flat.FlatDataStream;
import org.openforis.commons.io.flat.FlatRecord;

/**
 * 
 * @author G. Miceli
 * @author M. Togna
 * @author S. Ricci
 *
 */
public class CsvReader extends CsvProcessor implements FlatDataStream, Closeable {

	private final CsvReaderDelegate delegate;

	private IOException delegateConstructionException;
	
    public static final char DEFAULT_SEPARATOR = ',';
    public static final char DEFAULT_QUOTE_CHARACTER = '"';

	public CsvReader(String filename) throws FileNotFoundException {
		this(filename, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER);
	}
	
	public CsvReader(String fileName, char separator, char quoteChar) throws FileNotFoundException {
		this(new File(fileName), separator, quoteChar);
	}
	
	public CsvReader(File file) throws FileNotFoundException {
		this(file, OpenForisIOUtils.UTF_8, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER);
	}
	
	public CsvReader(File file, char separator, char quoteChar) throws FileNotFoundException {
		this(file, OpenForisIOUtils.UTF_8, separator, quoteChar);
	}
	
	public CsvReader(File file, String charsetName, char separator, char quoteChar) throws FileNotFoundException {
		this.delegate = createDelegate(file, charsetName, separator, quoteChar, this);
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
		this.delegate = new OpenCsvReader(reader, separator, quoteChar, this);
	}

	private static CsvReaderDelegate createDelegate(File file, String charsetName, char separator, char quoteChar, 
			CsvReader csvReader) throws FileNotFoundException {
		try {
			return new ExcelReader(file, csvReader);
		} catch(ExcelParseException e) {
			try {
				return new OpenCsvReader(file, charsetName, separator, quoteChar, csvReader);
			} catch(IOException ex) {
				csvReader.delegateConstructionException = ex;
				return null;
			}
		}
	}
	
	public void readHeaders() throws IOException {
		checkDelegate();
		delegate.readHeaders();
	}

	public CsvLine readNextLine() throws IOException {
		return delegate.readNextLine();
	}
	
	@Override
	public void close() throws IOException {
		delegate.close();
	}

	public boolean isHeadersRead() {
		return delegate.isHeadersRead();
	}
	
	public void setHeadersRead(boolean headersRead ) {
		delegate.setHeadersRead( headersRead );
	}

	public long getLinesRead() {
		return delegate.getLinesRead();
	}
	
	@Override
	public List<String> getFieldNames() {
		return delegate.getFieldNames();
	}

	@Override
	public FlatRecord nextRecord() throws IOException {
		return delegate.nextRecord();
	}
	
	@Override
	public List<String> getColumnNames() {
		return delegate.getColumnNames();
	}
	
	@Override
	public DateFormat getDateFormat() {
		return delegate.getDateFormat();
	}
	
	@Override
	Map<String, Integer> getColumnIndices() {
		return delegate.getColumnIndices();
	}
	
	/**
	 * Returns the number of lines including the headers
	 * @return
	 * @throws IOException
	 */
	public int size() throws IOException {
		return delegate.size();
	}
	
	private void checkDelegate() throws IOException {
		if (delegate == null) {
			throw delegateConstructionException;
		}
	}

}
