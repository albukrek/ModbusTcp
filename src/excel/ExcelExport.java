package excel;

import ModbusTcp.TotalPerTime;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;


public class ExcelExport {
    private ObservableList<TotalPerTime> data;
    private XSSFWorkbook workBook;
    private ExcelStyles excelStyles;

    public ExcelExport(ObservableList<TotalPerTime> data) {
        this.data = data;
        workBook = new XSSFWorkbook();
        excelStyles = new ExcelStyles();
    }

    public void excelTotalPerTime(String excelName, String sheetName) {
        XSSFSheet sheet = workBook.createSheet(sheetName);
        TotalPerTime totalPerTime = new TotalPerTime();
        prepareTableHeader(sheet, totalPerTime.getHeaders());
        int rowMax = prepareTableData(sheet);
        excelStyles.getSheetStyle(sheet, rowMax);
        writeExcel(excelName);
    }

    private void prepareTableHeader(XSSFSheet sheet, Object[] headers) {
        XSSFRow row;
        Cell cell;

        // Create table head style
        CellStyle headStyle = excelStyles.getHeadStyle(workBook);

        //Table Header
        row = sheet.createRow(0);
        row.setHeightInPoints(25);

        cell = row.createCell(0);
        cell.setCellStyle(headStyle);
        cell.setCellValue((String) headers[0]);
        cell = row.createCell(1);
        cell.setCellStyle(headStyle);
        cell.setCellValue((String) headers[1]);
        cell = row.createCell(2);
        cell.setCellStyle(headStyle);
        cell.setCellValue((String) headers[2]);
    }

    private int prepareTableData(XSSFSheet sheet) {
        XSSFRow row;
        Cell cell;

        // Create table body style
        CellStyle bodyStyle = excelStyles.getBodyStyle(workBook);

        //Table Data
        int i;
        for (i = 1; i <= data.size(); i++) {
            row = sheet.createRow(i);
            TotalPerTime line = data.get(i - 1);
            cell = row.createCell(0);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(line.getWeightId());
            cell = row.createCell(1);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(line.getTotal());
            cell = row.createCell(2);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(line.getTimestamp());
        }
        return i;
    }

    private void writeExcel(String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName + ".xlsx");
            workBook.write(fileOut);
            fileOut.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
} //End of class ExcelExport;
