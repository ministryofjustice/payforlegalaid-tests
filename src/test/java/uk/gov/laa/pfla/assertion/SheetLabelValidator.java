package uk.gov.laa.pfla.assertion;

import java.util.List;

public record SheetLabelValidator(String label, List<CellValidationRule> cells) {
    public static SheetLabelValidator of(String label, CellValidationRule... cells) {
        return new SheetLabelValidator(label, List.of(cells));
    }
}