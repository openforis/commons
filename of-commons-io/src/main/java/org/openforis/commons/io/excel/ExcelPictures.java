package org.openforis.commons.io.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class ExcelPictures {

	public static void addPicture(Sheet sheet, byte[] content, int pictureType, Cell cell) {
		addPicture(sheet, content, pictureType, cell.getColumnIndex(), cell.getRowIndex(), 1);
	}
	
	public static void addPicture(Sheet sheet, byte[] content, int pictureType, int column, int startRow, int rowSpan) {
		Workbook wb = sheet.getWorkbook();

		int pictureIdx = wb.addPicture(content, pictureType);

		// create an anchor with upper left cell column/startRow, only one cell anchor
		// since bottom right depends on resizing
		CreationHelper helper = wb.getCreationHelper();
		ClientAnchor anchor = helper.createClientAnchor();
		anchor.setCol1(column);
		anchor.setRow1(startRow);

		// create a picture anchored to Col1 and Row1
		Drawing<?> drawing = sheet.createDrawingPatriarch();
		Picture pict = drawing.createPicture(anchor, pictureIdx);

		// get the picture width in px
		int pictWidthPx = pict.getImageDimension().width;
		// get the picture height in px
		int pictHeightPx = pict.getImageDimension().height;

		// get column width of column in px
		float columnWidthPx = sheet.getColumnWidthInPixels(column);

		// get the heights of all merged rows in px
		float[] rowHeightsPx = new float[startRow + rowSpan];
		float rowsHeightPx = 0f;
		for (int r = startRow; r < startRow + rowSpan; r++) {
			Row row = sheet.getRow(r);
			float rowHeightPt = row.getHeightInPoints();
			rowHeightsPx[r - startRow] = rowHeightPt * Units.PIXEL_DPI / Units.POINT_DPI;
			rowsHeightPx += rowHeightsPx[r - startRow];
		}

		float scale = calculateScale(pictWidthPx, pictHeightPx, columnWidthPx, rowsHeightPx);

		// calculate the horizontal center position
		int horCenterPosPx = Math.round(columnWidthPx / 2f - pictWidthPx * scale / 2f);
		// set the horizontal center position as Dx1 of anchor
		if (wb instanceof XSSFWorkbook) {
			anchor.setDx1(horCenterPosPx * Units.EMU_PER_PIXEL); // in unit EMU for XSSF
		} else if (wb instanceof HSSFWorkbook) {
			// see
			// https://stackoverflow.com/questions/48567203/apache-poi-xssfclientanchor-not-positioning-picture-with-respect-to-dx1-dy1-dx/48607117#48607117
			// for HSSF
			int DEFAULT_COL_WIDTH = 10 * 256;
			anchor.setDx1(Math.round(horCenterPosPx * Units.DEFAULT_CHARACTER_WIDTH / 256f * 14.75f * DEFAULT_COL_WIDTH
					/ columnWidthPx));
		}

		// calculate the vertical center position
		int vertCenterPosPx = Math.round(rowsHeightPx / 2f - pictHeightPx * scale / 2f);
		// get Row1
		Integer row1 = null;
		rowsHeightPx = 0f;
		for (int r = 0; r < rowHeightsPx.length; r++) {
			float rowHeightPx = rowHeightsPx[r];
			if (rowsHeightPx + rowHeightPx > vertCenterPosPx) {
				row1 = r + startRow;
				break;
			}
			rowsHeightPx += rowHeightPx;
		}
		// set the vertical center position as Row1 plus Dy1 of anchor
		if (row1 != null) {
			anchor.setRow1(row1);
			if (wb instanceof XSSFWorkbook) {
				anchor.setDy1(Math.round(vertCenterPosPx - rowsHeightPx) * Units.EMU_PER_PIXEL); // in unit EMU for XSSF
			} else if (wb instanceof HSSFWorkbook) {
				// see
				// https://stackoverflow.com/questions/48567203/apache-poi-xssfclientanchor-not-positioning-picture-with-respect-to-dx1-dy1-dx/48607117#48607117
				// for HSSF
				float DEFAULT_ROW_HEIGHT = 12.75f;
				anchor.setDy1(Math.round((vertCenterPosPx - rowsHeightPx) * Units.PIXEL_DPI / Units.POINT_DPI * 14.75f
						* DEFAULT_ROW_HEIGHT / rowHeightsPx[row1]));
			}
		}

		// resize the picture to it's native size
		pict.resize();
		// if it must scaled down, then scale
		if (scale < 1) {
			pict.resize(scale);
		}
	}

	private static float calculateScale(int pictWidthPx, int pictHeightPx, float columnWidthPx, float rowsHeightPx) {
		float scale = 1;
		if (pictHeightPx > rowsHeightPx) {
			float tmpscale = rowsHeightPx / (float) pictHeightPx;
			if (tmpscale < scale)
				scale = tmpscale;
		}
		if (pictWidthPx > columnWidthPx) {
			float tmpscale = columnWidthPx / (float) pictWidthPx;
			if (tmpscale < scale)
				scale = tmpscale;
		}
		return scale;
	}
}
