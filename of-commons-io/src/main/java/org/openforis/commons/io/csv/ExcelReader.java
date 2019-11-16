/**
 * 
 */
package org.openforis.commons.io.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
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
	public void readHeaders() throws IOException {
		if (headersRead) {
			throw new IllegalStateException("Headers already read");
		}
		Row row = sheet.getRow(0);
		columnCount = row.getPhysicalNumberOfCells();

		String[] rowValues = extractValues(row);
		
		// remove last empty column values
		for (; columnCount >= 0; columnCount --) {
			String val = rowValues[columnCount - 1];
			if (StringUtils.isNotBlank(val))
				break;
		}
		rowValues = Arrays.copyOf(rowValues, columnCount);
		
		setFieldNames(rowValues);
		headersRead = true;
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
		case BLANK:
			return "";
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				Date date = cell.getDateCellValue();
				return new SimpleDateFormat(DATE_TIME_FORMAT).format(date);
			} else {
				cell.setCellType(CellType.STRING);
				return cell.getStringCellValue();
			}
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		default:
			cell.setCellType(CellType.STRING);
			return cell.getStringCellValue();
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
