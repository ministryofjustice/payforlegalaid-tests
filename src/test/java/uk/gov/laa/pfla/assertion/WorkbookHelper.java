package uk.gov.laa.pfla.assertion;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class WorkbookHelper {

    public static Sheet getRequiredSheet(Workbook wb, String name) {
        Sheet s = wb.getSheet(name);
        if (s == null) throw new AssertionError("Missing sheet: " + name);
        return s;
    }

    public static Optional<CellAddress> findCellContaining(Sheet sheet, String needle) {
        String n = needle.toLowerCase(Locale.ROOT);
        return StreamSupport.stream(sheet.spliterator(), false)
                .flatMap(row -> StreamSupport.stream(row.spliterator(), false))
                .filter(cell -> cell.getCellType() == CellType.STRING)
                .filter(cell -> {
                    String s = cell.getStringCellValue();
                    return s != null && s.toLowerCase(Locale.ROOT).contains(n);
                })
                .map(Cell::getAddress)
                .findFirst();
    }

    public static Cell getCell(Sheet sheet, int rowIdx, int colIdx) {
        if (rowIdx < 0 || colIdx < 0) return null;
        Row r = sheet.getRow(rowIdx);
        return r == null ? null : r.getCell(colIdx);
    }

    public static String safeRender(Cell cell, Workbook wb) {
        if (cell == null) return "<null>";
        DataFormatter fmt = new DataFormatter(Locale.UK, true);
        return fmt.formatCellValue(cell);
    }
}