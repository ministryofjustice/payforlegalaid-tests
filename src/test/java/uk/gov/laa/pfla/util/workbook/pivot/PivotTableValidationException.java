package uk.gov.laa.pfla.util.workbook.pivot;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ooxml.POIXMLDocumentPart;

import static uk.gov.laa.pfla.util.workbook.pivot.PivotTableValidationException.ErrorCase.INVALID_CACHE;
import static uk.gov.laa.pfla.util.workbook.pivot.PivotTableValidationException.ErrorCase.NO_DATA_SOURCE;
import static uk.gov.laa.pfla.util.workbook.pivot.PivotTableValidationException.ErrorCase.NO_FIELDS;
import static uk.gov.laa.pfla.util.workbook.pivot.PivotTableValidationException.ErrorCase.NO_LOCATION;

/**
 * Exception thrown when a pivot table fails validation checks.
 * <p>
 * This exception indicates that a pivot table did not meet one or more validation requirements
 * as defined by the {@link PivotTableValidationStrategy} implementations. The exception message
 * contains specific details about which validation check failed.
 * </p>
 */
@EqualsAndHashCode(callSuper = true)
public abstract class PivotTableValidationException extends RuntimeException {

    ErrorCase errorCase;
    POIXMLDocumentPart pivotTable;

    /**
     * Constructs a new validation exception for a specific error case.
     *
     * @param errorCase  The specific validation failure case
     * @param pivotTable The pivot table that failed validation
     */
    private PivotTableValidationException(ErrorCase errorCase, POIXMLDocumentPart pivotTable) {
        super(errorCase.formatMessage(pivotTable));
        this.errorCase = errorCase;
        this.pivotTable = pivotTable;
    }

    /**
     * Creates a validation exception for a pivot table with an invalid cache definition.
     */
    public static PivotTableValidationException invalidCache(POIXMLDocumentPart pivotTable) {
        return new PivotTableValidationException(INVALID_CACHE, pivotTable) {};
    }

    /**
     * Creates a validation exception for a pivot table missing a data source reference.
     */
    public static PivotTableValidationException noDataSource(POIXMLDocumentPart pivotTable) {
        return new PivotTableValidationException(NO_DATA_SOURCE, pivotTable) {};
    }

    /**
     * Creates a validation exception for a pivot table with no defined location.
     */
    public static PivotTableValidationException noLocation(POIXMLDocumentPart pivotTable) {
        return new PivotTableValidationException(NO_LOCATION, pivotTable) {};
    }

    /**
     * Creates a validation exception for a pivot table with no configured fields.
     */
    public static PivotTableValidationException noFields(POIXMLDocumentPart pivotTable) {
        return new PivotTableValidationException(NO_FIELDS, pivotTable) {};
    }

    /**
     * Defines the specific validation failure cases for pivot tables.
     */
    @RequiredArgsConstructor
    enum ErrorCase {
        INVALID_CACHE("Pivot table at %s has invalid cache definition"),
        NO_DATA_SOURCE("Pivot table at %s has no data source reference"),
        NO_LOCATION("Pivot table at %s has no defined location"),
        NO_FIELDS("Pivot table at %s has no configured fields");

        private final String messageFormat;

        String formatMessage(POIXMLDocumentPart pivotTable) {
            return messageFormat.formatted(pivotTable);
        }
    }
}