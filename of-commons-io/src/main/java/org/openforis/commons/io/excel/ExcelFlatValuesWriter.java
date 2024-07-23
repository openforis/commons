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
import org.openforis.commons.io.flat.Field.Type;
import org.openforis.commons.io.flat.FlatDataWriter;

public class ExcelFlatValuesWriter extends FlatDataWriter {

	private static final int IMAGE_COLUMN_WIDTH = 4000;
	private static final short IMAGE_ROW_HEIGHT = 2000;

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
		for (int i = 0; i < getFields().size() && i < values.length; i++) {
			Cell cell = row.createCell(i);
			Field field = getFields().get(i);
			switch (field.getType()) {
			case DECIMAL:
			case INTEGER:
				cell.setCellType(CellType.NUMERIC);
				break;
			default:
				cell.setCellType(CellType.STRING);
			}
			setValueIntoCell(cell, field, values[i]);
		}
	}

	protected void setValueIntoCell(Cell cell, Field field, Object value) {
		if (value == null) {
			cell.setCellValue((String) null);
		} else {
			Type fieldType = field.getType();
			if (value instanceof Number && (fieldType == Type.DECIMAL || fieldType == Type.INTEGER)) {
				cell.setCellValue(((Number) value).doubleValue());
			} else if (fieldType == Type.IMAGE_BYTE_ARRAY) {
				sheet.setColumnWidth(cell.getColumnIndex(), IMAGE_COLUMN_WIDTH);
				cell.getRow().setHeight(IMAGE_ROW_HEIGHT);
				ExcelPictures.addPicture(sheet, (byte[]) value, Workbook.PICTURE_TYPE_JPEG, cell);
			} else {
				cell.setCellValue(value.toString());
			}
		}
	}

	@Override
	public void flush() throws IOException {
		// no need to flush values
	}

	@Override
	public void close() throws IOException {
		if (workbook != null) {
			workbook.write(os);
			workbook.close();
		}
	}

}
