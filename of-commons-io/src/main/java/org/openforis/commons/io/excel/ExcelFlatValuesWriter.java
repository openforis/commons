package org.openforis.commons.io.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openforis.commons.io.flat.FlatDataWriter;

public class ExcelFlatValuesWriter extends FlatDataWriter {

	private Workbook workbook = new XSSFWorkbook();
	private Sheet sheet = workbook.createSheet("sheet1");
	private OutputStream os;

	public ExcelFlatValuesWriter(OutputStream output) throws UnsupportedEncodingException {
		this.os = output;
	}

	@Override
	protected void writeNextInternal(String[] values) {
		Row row = sheet.createRow(Long.valueOf(linesWritten).intValue());
		// Create cells
		for (int i = 0; i < getColumnNames().size(); i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(values[i]);
		}
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
		workbook.write(os);
		workbook.close();
	}

}
