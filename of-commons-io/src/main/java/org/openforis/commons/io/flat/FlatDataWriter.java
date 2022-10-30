package org.openforis.commons.io.flat;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.openforis.commons.io.csv.CsvProcessor;
import org.openforis.commons.io.flat.Field.Type;

/**
 * @author G. Miceli
 * @author S. Ricci
 */
public abstract class FlatDataWriter extends CsvProcessor implements Closeable {
	
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
		setFields(in.getFields());
		
		while ( r != null ) {
			writeNext(r);
			r = in.nextRecord();
		}
	}

	public abstract void flush() throws IOException;
	
	@Override
	public abstract void close() throws IOException;
	
	public void writeNext(FlatRecord r) {
		Object[] line = r.toArray();
		writeNext(line);
	}

	public void writeNext(List<String> line) {
		writeNext(line.toArray(new Object[line.size()]));
	}
	
	public void writeNext(Object[] line) {
		writeNextInternal(line);
		linesWritten ++;
	}
	
	protected abstract void writeNextInternal(Object[] line);

	public void writeHeaders(List<String> headers) {
		writeHeaders(headers.toArray(new String[headers.size()]));
	}
	
	public void writeHeaders(String[] headers) {
		if ( headersWritten ) {
			throw new IllegalStateException("Headers already written");
		}
		Field[] headersFields = new Field[headers.length];
		for (int i = 0; i < headers.length; i++) {
			headersFields[i] = new Field(headers[i], Type.STRING, i);
		}
		
		setFields(headersFields);
		
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
