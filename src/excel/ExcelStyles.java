package excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelStyles {

    CellStyle getHeadStyle(XSSFWorkbook workBook) {
        CellStyle headStyle = workBook.createCellStyle();
        Font headFont = workBook.createFont();

     /* ---- Set Font style ----- */
        headFont.setBold(true);
        headFont.setFontName("Arial");
        headFont.setColor(IndexedColors.WHITE.getIndex());
        headFont.setFontHeightInPoints((short) 13);

     /* ---- Set Cell style ----- */
        headStyle.setFont(headFont);
        // Set Cell alignment
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // Set Cell Color
        headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //Set Borders
        headStyle.setBorderLeft(BorderStyle.THIN);
        headStyle.setBorderBottom(BorderStyle.THICK);

        return headStyle;
    }

    CellStyle getBodyStyle(XSSFWorkbook workBook){
        CellStyle bodyStyle = workBook.createCellStyle();
        Font bodyFont = workBook.createFont();

    /* ---- Set Font style ----- */
        bodyFont.setBold(false);
        bodyFont.setFontName("Arial");
        bodyFont.setFontHeightInPoints((short) 13);

    /* ---- Set Cell style ----- */
        bodyStyle.setFont(bodyFont);
        //Set Borders
        bodyStyle.setBorderLeft(BorderStyle.THIN);
        bodyStyle.setBorderBottom(BorderStyle.THIN);

        return bodyStyle;
    }

    void getSheetStyle(XSSFSheet sheet, int rowMax) {
     /* ----- Set Column length ------ */
        sheet.setColumnWidth(0, 4800);
        sheet.setColumnWidth(1, 3500);
        sheet.setColumnWidth(2, 7000);

     /* ----- Create table range ------ */
        CellRangeAddress tableRange = new CellRangeAddress(0, rowMax-1, 0, 2);

     /* ----- Set table borders ------ */
        RegionUtil.setBorderBottom(BorderStyle.THICK, tableRange, sheet);
        RegionUtil.setBorderTop(BorderStyle.THICK, tableRange, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THICK, tableRange, sheet);
        RegionUtil.setBorderRight(BorderStyle.THICK, tableRange, sheet);

        sheet.setAutoFilter(tableRange);
        sheet.createFreezePane( 0, 1, 0, 1 );
        sheet.setDisplayGridlines(false);
    }
}
