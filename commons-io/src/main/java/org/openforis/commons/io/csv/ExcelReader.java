/**
 * 
 */
package org.openforis.commons.io.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
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

	private static final String DATE_TIME_FORMAT 	= "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	
	private Sheet sheet;
	private int columnCount;
	private Workbook wb;
	
	public ExcelReader(File file, CsvReader csvReader) throws FileNotFoundException {
		super(csvReader);
		try {
			wb = WorkbookFactory.create(file);
			sheet = wb.getSheetAt(0);
		} catch(Exception e) {
			throw new ExcelParseException("Failed to parse " + file.getAbsolutePath() + " as Excel", e);
		}
	}

	@Override
	public void readHeaders() throws IOException {
		if ( headersRead ) {
			throw new IllegalStateException("Headers already read");
		}
		Row row = sheet.getRow(0);
	    columnCount = row.getPhysicalNumberOfCells();
	    
	    String[] rowValues = extractValues(row);
		setColumnNames(rowValues);
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
		int cellType = cell.getCellType();
		String value;
		if (cellType == Cell.CELL_TYPE_BLANK) {
			value = "";
		} else if (cellType == Cell.CELL_TYPE_STRING) {
			value = cell.getStringCellValue();
		} else if (cellType == Cell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date date = cell.getDateCellValue();
				value = new SimpleDateFormat(DATE_TIME_FORMAT).format(date);
			} else {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				value = cell.getStringCellValue();
			}
		} else if(cellType == Cell.CELL_TYPE_BOOLEAN) {
			value = String.valueOf(cell.getBooleanCellValue());
		} else {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			value = cell.getStringCellValue();
		}
		return value;
	}

	@Override
	protected String[] line(long lineIdx) {
		Row row = sheet.getRow((int) lineIdx + 1);
		if (row == null) {
			return null;
		}
		String[] line = extractValues(row);
		return line;
	}
	
	@Override
	public void close() throws IOException {
		wb.close();
	}

	/**
	 * Returns the number of lines including the headers
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
