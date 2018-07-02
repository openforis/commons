package org.openforis.commons.io.csv;

import java.io.Closeable;
import java.io.IOException;

public interface ValuesWriter extends Closeable {

	public void writeNext(String[] values);
	
	public void flush() throws IOException;
}
