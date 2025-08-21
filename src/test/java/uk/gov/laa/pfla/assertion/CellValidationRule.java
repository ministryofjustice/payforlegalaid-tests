package uk.gov.laa.pfla.assertion;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.function.BiPredicate;

public record CellValidationRule(int dRow, int dCol, BiPredicate<Cell, Workbook> validator, String validatorName) {
    public CellValidationRule(int dRow, int dCol, NamedValidator nv) {
        this(dRow, dCol, nv.predicate(), nv.name());
    }
}