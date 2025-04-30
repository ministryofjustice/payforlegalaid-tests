package uk.gov.laa.pfla.util.workbook.pivot;

import org.apache.poi.xssf.usermodel.XSSFPivotTable;

import static uk.gov.laa.pfla.util.workbook.pivot.PivotTableValidationStrategy.CacheDefinitionValidation;
import static uk.gov.laa.pfla.util.workbook.pivot.PivotTableValidationStrategy.DataSourceValidation;
import static uk.gov.laa.pfla.util.workbook.pivot.PivotTableValidationStrategy.LocationValidation;
import static uk.gov.laa.pfla.util.workbook.pivot.PivotTableValidationStrategy.PivotFieldsValidation;

/**
 * An interface defining the contract for pivot table validation strategies.
 * <p>
 * Implementations of this interface validate specific aspects of an Excel pivot table's
 * structure and configuration. Each implementation should focus on a single validation
 * concern.
 * </p>
 */
sealed interface PivotTableValidationStrategy permits
        CacheDefinitionValidation,
        DataSourceValidation,
        LocationValidation,
        PivotFieldsValidation {

    /**
     * Validates a specific aspect of the given pivot table.
     * <p>
     * Implementations should perform focused validation on one particular aspect
     * of the pivot table's structure or configuration. The method should throw
     * {@link PivotTableValidationException} with a descriptive message if validation fails.
     * </p>
     */
    void validate(XSSFPivotTable pivotTable) throws PivotTableValidationException;

    final class PivotFieldsValidation implements PivotTableValidationStrategy {

        @Override
        public void validate(XSSFPivotTable pivotTable) throws PivotTableValidationException {
            var pivotFields = pivotTable.getCTPivotTableDefinition().getPivotFields();
            if (pivotFields == null || pivotFields.getPivotFieldList().isEmpty()) {
                throw PivotTableValidationException.noFields(pivotTable);
            }
        }

    }

    /**
     * Validates the location definition of a pivot table.
     * <p>
     * Ensures the pivot table has a defined location in the worksheet where it should be rendered.
     * Without a valid location, the pivot table cannot be properly displayed.
     * </p>
     */
    final class LocationValidation implements PivotTableValidationStrategy {

        @Override
        public void validate(XSSFPivotTable pivotTable) throws PivotTableValidationException {
            if (pivotTable.getCTPivotTableDefinition().getLocation() == null) {
                throw PivotTableValidationException.noLocation(pivotTable);
            }
        }

    }

    /**
     * Validates the data source configuration of a pivot table.
     * <p>
     * This strategy verifies that the pivot table has a properly configured data source
     * reference, including both the cache source and worksheet source definitions.
     * </p>
     */
    final class DataSourceValidation implements PivotTableValidationStrategy {

        @Override
        public void validate(XSSFPivotTable pivotTable) throws PivotTableValidationException {
            var cacheDef = pivotTable.getPivotCacheDefinition();
            var cacheSource = cacheDef.getCTPivotCacheDefinition().getCacheSource();
            if (cacheSource == null || cacheSource.getWorksheetSource() == null) {
                throw PivotTableValidationException.noDataSource(pivotTable);
            }
        }

    }

    /**
     * Validates the cache definition of a pivot table.
     * <p>
     * This strategy checks that the pivot table has a valid cache definition structure,
     * which is essential for the pivot table to function properly.
     * </p>
     */
    final class CacheDefinitionValidation implements PivotTableValidationStrategy {

        @Override
        public void validate(XSSFPivotTable pivotTable) throws PivotTableValidationException {
            var cacheDef = pivotTable.getPivotCacheDefinition();
            if (cacheDef == null || cacheDef.getCTPivotCacheDefinition() == null) {
                throw PivotTableValidationException.invalidCache(pivotTable);
            }
        }

    }
}
