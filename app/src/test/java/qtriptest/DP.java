package qtriptest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.testng.annotations.DataProvider;


public class DP {

    @DataProvider(name = "data-provider")
    public Object[][] dpMethod(Method m) throws IOException {
        List<List<String>> outputList = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();

        FileInputStream excelFile = new FileInputStream(new File(
            "/home/crio-user/workspace/kabileshs-joy-ME_QTRIP_QA_V2/app/src/test/resources/DatasetsforQTrip.xlsx"
        ));
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheet(m.getName()); 
        Iterator<Row> rowIterator = sheet.iterator();

        boolean isFirstRow = true;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (isFirstRow) {
                isFirstRow = false; 
                continue;
            }

            List<String> rowData = new ArrayList<>();

            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String value;

                if (cell.getCellType() == CellType.NUMERIC && i == 2) {
                    value = String.valueOf((int) cell.getNumericCellValue());
                } else {
                    value = dataFormatter.formatCellValue(cell);
                }

                rowData.add(value);
            }

            if (!rowData.isEmpty()) {
                outputList.add(rowData);
            }
        }

        workbook.close();
        excelFile.close();

        Object[][] result = new Object[outputList.size()][];
        for (int i = 0; i < outputList.size(); i++) {
            result[i] = outputList.get(i).toArray(new String[0]);
        }

        return result;
    }
}
