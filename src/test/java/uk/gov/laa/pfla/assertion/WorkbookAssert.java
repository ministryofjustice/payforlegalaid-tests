package uk.gov.laa.pfla.assertion;

import org.assertj.core.api.AbstractAssert;
import org.apache.poi.ss.usermodel.Workbook;
import uk.gov.laa.pfla.comparator.WorkbookComparator;

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
            failWithMessage("Response body should not be empty");
        }
    }

}