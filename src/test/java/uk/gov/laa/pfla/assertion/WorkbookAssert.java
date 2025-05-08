package uk.gov.laa.pfla.assertion;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.api.AbstractAssert;
import uk.gov.laa.pfla.comparator.WorkbookComparator;
import uk.gov.laa.pfla.util.workbook.SheetCellIterator;
import uk.gov.laa.pfla.util.workbook.pivot.PivotTableValidator;

import static org.apache.poi.ss.usermodel.CellType.ERROR;
import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.xssf.usermodel.XSSFRelation.PIVOT_TABLE;

public abstract class WorkbookAssert extends AbstractAssert<WorkbookAssert, Workbook> {

    private final WorkbookComparator workbookComparator;

    public WorkbookAssert(Workbook actual) {
        super(actual, WorkbookAssert.class);
        this.workbookComparator = new WorkbookComparator() {};
    }

    public static WorkbookAssert assertThat(Workbook actual) {
        return new WorkbookAssert(actual) {};
    }

    public void isEqualTo(Workbook expected) {
        isNotNull();

        if (!workbookComparator.areWorkbooksEqual(actual, expected)) {
            failWithMessage("Workbook does not match the expected result");
        }
    }

    public void hasAllFormulasEvaluated() {
        SheetCellIterator.ofWorkbook(actual).stream()
                .filter((Cell cell) -> FORMULA == cell.getCellType())
                .filter((Cell cell) -> ERROR == cell.getCachedFormulaResultType())
                .forEach((Cell cell) -> {
                    var sheet = ((XSSFCell)cell).getSheet();
                    failWithMessage("Formula error at %s.%s - Formula: '%s' resulted in error",
                            sheet.getSheetName(),
                            cell.getAddress().formatAsString(),
                            cell.getCellFormula());
                });
    }

    public void hasValidPivotTables() {
        var xssfWorkbook = (XSSFWorkbook) actual;
        var validator = new PivotTableValidator();

        xssfWorkbook.getRelationParts().stream()
                .filter(part -> part.getRelationship().getRelationshipType().contains(PIVOT_TABLE.getRelation()))
                .map(part -> (XSSFPivotTable) xssfWorkbook.getRelationById(part.getRelationship().getId()))
                .forEach(validator::validate);
    }

}