package org.openforis.commons.io.csv;

import java.io.IOException;
import java.util.List;

import org.openforis.commons.io.flat.FlatRecord;

/**
 * 
 * @author D. Wiell
 * @author S. Ricci
 *
 */
public abstract class CsvReaderDelegate extends CsvProcessor {

	protected boolean headersRead;
	protected long linesRead;
	protected CsvReader csvReader;
	
	/**
	 * Returns the number of lines including the headers
	 * @return
	 * @throws IOException
	 */
	public abstract int size() throws IOException;

	public abstract void close() throws IOException;

	public abstract void readHeaders() throws IOException;

	protected abstract String[] line(long lineIdx) throws IOException;

	CsvReaderDelegate(CsvReader csvReader) {
		this.csvReader = csvReader;
		this.headersRead = false;
		this.linesRead = 0;
	}

	public CsvLine readNextLine() throws IOException {
		if ( !headersRead ) {
			throw new IllegalStateException("Headers must be read first");
		}
		String[] line = line(linesRead);
		if ( line == null ) {
			return null;
		} else {
			linesRead ++;
			return new CsvLine(csvReader, line);
		}
	}

	public final boolean isHeadersRead() {
		return headersRead;
	}

	public final long getLinesRead() {
		return linesRead;
	}

	public final List<String> getFieldNames() {
		return getColumnNames();
	}

	public final FlatRecord nextRecord() throws IOException {
		return readNextLine();
	}

	public void setHeadersRead(boolean headersRead) {
		this.headersRead = headersRead;
	}

}