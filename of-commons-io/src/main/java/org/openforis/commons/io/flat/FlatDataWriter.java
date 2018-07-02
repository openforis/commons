package org.openforis.commons.io.flat;

import java.io.IOException;
import java.util.List;

import org.openforis.commons.io.csv.CsvProcessor;
import org.openforis.commons.io.csv.ValuesWriter;

/**
 * @author G. Miceli
 * @author S. Ricci
 */
public abstract class FlatDataWriter extends CsvProcessor implements ValuesWriter {
	
	protected long linesWritten; 
	protected boolean headersWritten;
	
	public FlatDataWriter() {
		linesWritten = 0;
		headersWritten = false;
	}

	public void writeAll(FlatDataStream in) throws IOException {
		FlatRecord r = in.nextRecord();
		if ( r == null ) {
			return;
		}
    	writeHeaders(in.getFieldNames());
		
		while ( r != null ) {
			writeNext(r);
			r = in.nextRecord();
		}
	}

	@Override
	public abstract void flush() throws IOException;
	
	@Override
	public abstract void close() throws IOException;
	
	public void writeNext(FlatRecord r) {
		String[] line = r.toStringArray();
		writeNext(line);
	}

	public void writeNext(List<String> line) {
		writeNext(line.toArray(new String[line.size()]));
	}
	
	public void writeNext(String[] line) {
		writeNextInternal(line);
		linesWritten ++;
	}
	
	protected abstract void writeNextInternal(String[] line);

	public void writeHeaders(List<String> headers) {
		writeHeaders(headers.toArray(new String[headers.size()]));
	}
	
	public void writeHeaders(String[] headers) {
		if ( headersWritten ) {
			throw new IllegalStateException("Headers already written");
		}
		setColumnNames(headers);
    	writeNext(headers);
    	headersWritten = true;
	}

	public long getLinesWritten() {
		return linesWritten;
	}
	
	public boolean isHeadersWritten() {
		return headersWritten;
	}
}
