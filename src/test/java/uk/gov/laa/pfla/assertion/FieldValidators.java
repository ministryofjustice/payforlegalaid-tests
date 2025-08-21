package uk.gov.laa.pfla.assertion;

import org.apache.poi.ss.usermodel.*;
import java.util.Locale;

public class FieldValidators {

    public static final NamedValidator NUMERIC = new NamedValidator(
            FieldTypeEnum.NUMERIC.name(),
        (cell, wb) -> cell != null && switch (cell.getCellType()) {
            case NUMERIC -> true;
            case FORMULA -> isFormulaNumeric(cell);
            default -> false;
        }
    );

    public static final NamedValidator CURRENCY_GBP = new NamedValidator(
            FieldTypeEnum.CURRENCY_GBP.name(),
            (cell, wb) -> {
                if (cell == null) return false;
                CellType type = cell.getCellType();
                if (!(type == CellType.NUMERIC || type == CellType.FORMULA)) return false;

                DataFormatter fmt = new DataFormatter(Locale.UK, true);
                FormulaEvaluator eval = wb.getCreationHelper().createFormulaEvaluator();

                String rendered = fmt.formatCellValue(cell, eval);
                if (rendered != null && rendered.trim().startsWith("£")) return true;

                if (type == CellType.FORMULA) {
                    CellType cached = cell.getCachedFormulaResultType();
                    if (cached == CellType.NUMERIC) {
                        CellStyle style = cell.getCellStyle();
                        String fmtStr = style != null ? style.getDataFormatString() : null;
                        if (fmtStr != null) {
                            String fs = fmtStr.toLowerCase(Locale.ROOT);
                            if (fs.contains("£")) {
                                return true;
                            }
                        }
                        return true;
                    }
                    if (cached == CellType.STRING) {
                        String s = fmt.formatCellValue(cell, eval);
                        return s != null && s.trim().startsWith("£");
                    }
                    return false;
                }

                if (type == CellType.NUMERIC) {
                    CellStyle style = cell.getCellStyle();
                    String fmtStr = style != null ? style.getDataFormatString() : null;
                    if (fmtStr != null && fmtStr.contains("£")) return true;
                    String renderedNoEval = fmt.formatCellValue(cell);
                    return renderedNoEval != null && renderedNoEval.trim().startsWith("£");
                }

                return false;
            }
    );

    public static final NamedValidator STRING_NONBLANK = new NamedValidator(
            FieldTypeEnum.STRING_NONBLANK.name(),
            (cell, wb) -> {
                if (cell == null) return false;
                return switch (cell.getCellType()) {
                    case STRING -> {
                        String s = cell.getStringCellValue();
                        yield s != null && !s.trim().isEmpty();
                    }
                    case FORMULA -> {
                        DataFormatter fmt = new DataFormatter(Locale.UK, true);
                        String s = fmt.formatCellValue(cell);
                        yield s != null && !s.trim().isEmpty();
                    }
                    default -> false;
                };
            }
    );

    private static boolean isFormulaNumeric(Cell c) {
        try {
            c.getNumericCellValue();
            return true;
        } catch (IllegalStateException ex) {
            return false;
        }
    }
}