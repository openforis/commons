package org.openforis.commons.io.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openforis.commons.io.flat.Field;
import org.openforis.commons.io.flat.FlatDataWriter;
import org.openforis.commons.io.flat.Field.Type;

public class ExcelFlatValuesWriter extends FlatDataWriter {

	private Workbook workbook = new XSSFWorkbook();
	private Sheet sheet = workbook.createSheet("sheet1");
	private OutputStream os;

	public ExcelFlatValuesWriter(OutputStream output) throws UnsupportedEncodingException {
		this.os = output;
	}

	@Override
	protected void writeNextInternal(Object[] values) {
		Row row = sheet.createRow(Long.valueOf(linesWritten).intValue());
		// Create cells
		for (int i = 0; i < getFields().size(); i++) {
			Cell cell = row.createCell(i);
			Field field = getFields().get(i);
			Object value = values[i];
			switch(field.getType()) {
			case DECIMAL:
			case INTEGER:
				cell.setCellType(CellType.NUMERIC);
				break;
			case STRING:
			default:
				cell.setCellType(CellType.STRING);
			}
			if (value == null) {
				cell.setCellValue((String) null);
			} else if (value instanceof Number 
					&& (field.getType() == Type.DECIMAL 
						|| field.getType() == Type.INTEGER)) {
				cell.setCellValue(((Number) value).doubleValue());
			} else {
				cell.setCellValue(value.toString());
			}
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
