package org.openforis.commons.io.csv;

import java.io.IOException;
import java.io.Writer;

import au.com.bytecode.opencsv.CSVWriter;

public class CsvFlatValuesWriter implements ValuesWriter {

	private CSVWriter csvWriter;
	
	public CsvFlatValuesWriter(Writer writer, char separator, char quoteChar) {
		csvWriter = new CSVWriter(writer, separator, quoteChar);
	}
	
	@Override
	public void writeNext(String[] values) {
		csvWriter.writeNext(values);
	}

	@Override
	public void close() throws IOException {
		csvWriter.close();
	}

	@Override
	public void flush() throws IOException {
		csvWriter.flush();
	}

}
