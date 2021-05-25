package org.openforis.commons.io.csv;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openforis.commons.io.flat.Field;
import org.openforis.commons.io.flat.FlatRecord;

/**
 * 
 * @author D. Wiell
 * @author S. Ricci
 *
 */
public abstract class CsvReaderDelegate extends CsvProcessor implements Closeable {

	protected boolean headersRead;
	protected long linesRead;
	protected CsvReader csvReader;

	CsvReaderDelegate(CsvReader csvReader) {
		this.csvReader = csvReader;
		this.headersRead = false;
		this.linesRead = 0;
	}

	/**
	 * Returns the number of lines including the headers
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract int size() throws IOException;

	public abstract String[] readHeadersInternal() throws IOException;

	protected abstract String[] line(long lineIdx) throws IOException;

	public CsvLine readNextLine() throws IOException {
		if (!headersRead) {
			throw new IllegalStateException("Headers must be read first");
		}
		String[] line = line(linesRead);
		if (line == null) {
			return null;
		} else {
			linesRead++;
			return new CsvLine(csvReader, line);
		}
	}

	public void readHeaders() throws IOException {
		if (headersRead) {
			throw new IllegalStateException("Headers already read");
		}
		String[] headers = readHeadersInternal();
		headers = adjustHeaders(headers);
		setFieldNames(headers);
		headersRead = true;
	}

	private String[] adjustHeaders(String[] headers) {
		if (headers == null)
			return null;

		String[] result = null;

		// remove last empty headers
		int count = headers.length;
		while (count > 0 && StringUtils.isBlank(headers[count - 1])) {
			count--;
		}
		result = count < headers.length ? headers = ArrayUtils.subarray(headers, 0, count) : headers;
		
		// trim headers (avoid bugs with Excel adding spaces to CSV columns)
		for (int i = 0; i < count; i++) {
			result[i] = result[i].trim();
		}

		return result;
	}

	public final boolean isHeadersRead() {
		return headersRead;
	}

	public final long getLinesRead() {
		return linesRead;
	}

	public final List<Field> getFields() {
		return super.getFields();
	}

	public final List<String> getFieldNames() {
		return super.getFieldNames();
	}

	public final FlatRecord nextRecord() throws IOException {
		return readNextLine();
	}

	public void setHeadersRead(boolean headersRead) {
		this.headersRead = headersRead;
	}

}