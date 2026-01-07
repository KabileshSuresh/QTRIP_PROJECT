package qtriptest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.FileNotFoundException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.testng.annotations.DataProvider;
public class DP {

    @DataProvider(name = "data-provider", parallel = false)
    public Object[][] dpMethod(Method m) throws IOException {
        List<List<String>> outputList = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();

        String sheetName = "";
        String methodName = m.getName();

        if (methodName.equals("TestCase01")) {
            sheetName = "TestCase01";
        } else if (methodName.equals("TestCase02")) {
            sheetName = "TestCase02";
        } else if (methodName.equals("TestCase03")) {
            sheetName = "TestCase03";
        } else if (methodName.equals("TestCase04")) {
            sheetName = "TestCase04";
        } else {
            throw new IllegalArgumentException("No matching sheet for method: " + methodName);
        }

        String excelPath = "/home/crio-user/workspace/kabileshs-joy-ME_QTRIP_QA_V2/app/src/test/resources/DatasetsforQTrip.xlsx";
        File excelFile = new File(excelPath);
        if (!excelFile.exists()) {
            throw new FileNotFoundException("Excel file not found at: " + excelPath);
        }

        FileInputStream fis = new FileInputStream(excelFile);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet(sheetName);

        if (sheet == null) {
            workbook.close();
            throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in Excel.");
        }

        Iterator<Row> rowIterator = sheet.iterator();
        boolean isFirstRow = true;

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (isFirstRow) {
                isFirstRow = false;
                continue; // Skip header
            }

            List<String> rowData = new ArrayList<>();
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String value = dataFormatter.formatCellValue(cell).trim();
                rowData.add(value);
            }

            if (!rowData.isEmpty() && rowData.stream().anyMatch(s -> !s.isEmpty())) {
                outputList.add(rowData);
            }
        }

        workbook.close();
        fis.close();

        Object[][] result = new Object[outputList.size()][];
        for (int i = 0; i < outputList.size(); i++) {
            result[i] = outputList.get(i).toArray(new String[0]);
        }

        System.out.println("Loaded " + result.length + " rows from sheet: " + sheetName);
        return result;
    }
}
