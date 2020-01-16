/**
 * 
 */
package org.openforis.commons.io.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * @author D. Wiell
 * @author S. Ricci
 *
 */
class ExcelReader extends CsvReaderDelegate {

	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	private Sheet sheet;
	private int columnCount;
	private Workbook wb;

	public ExcelReader(File file, CsvReader csvReader) throws FileNotFoundException {
		super(csvReader);
		try {
			wb = WorkbookFactory.create(file);
			sheet = wb.getSheetAt(0);
		} catch (Exception e) {
			throw new ExcelParseException("Failed to parse " + file.getAbsolutePath() + " as Excel", e);
		}
	}

	@Override
	public String[] readHeadersInternal() throws IOException {
		Row row = sheet.getRow(0);
		columnCount = row.getPhysicalNumberOfCells();
		return extractValues(row);
	}

	@Override
	public void readHeaders() throws IOException {
		super.readHeaders();
		columnCount = getFieldNames() == null ? 0 : getFieldNames().size();
	}

	private String[] extractValues(Row row) {
		String[] rowValues = new String[columnCount];
		for (int colIdx = 0; colIdx < columnCount; colIdx++) {
			rowValues[colIdx] = getCellStringValue(row, colIdx);
		}
		return rowValues;
	}

	private String getCellStringValue(Row row, int colIdx) {
		Cell cell = row.getCell(colIdx);
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
		case NUMERIC:
			return getNumericStringValue(cell);
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		default:
			cell.setCellType(CellType.STRING);
			return cell.getStringCellValue();
		}
	}

	private String getNumericStringValue(Cell cell) {
		if (DateUtil.isCellDateFormatted(cell)) {
			Date date = cell.getDateCellValue();
			return new SimpleDateFormat(DATE_TIME_FORMAT).format(date);
		} else {
			double doubleVal = cell.getNumericCellValue();
			Double doubleValObj = Double.valueOf(doubleVal);
			if (doubleVal % 1 == 0 && doubleVal < Integer.MAX_VALUE) {
				// values like 1.0 will be considered as integers
				return String.valueOf(doubleValObj.intValue());
			} else {
				return String.valueOf(doubleVal);
			}
		}
	}

	@Override
	protected String[] line(long lineIdx) {
		Row row = sheet.getRow((int) lineIdx + 1);
		if (row == null) {
			return null;
		}
		return extractValues(row);
	}

	@Override
	public void close() throws IOException {
		wb.close();
	}

	/**
	 * Returns the number of lines including the headers
	 * 
	 * @return
	 * @throws IOException
	 */
	@Override
	public int size() throws IOException {
		return sheet.getPhysicalNumberOfRows();
	}

	static class ExcelParseException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public ExcelParseException(String message, Throwable cause) {
			super(message, cause);
		}

	}
}
