package uk.gov.laa.pfla.assertion;

public enum ReportLabelEnum {
    MAIN("MAIN"),
    USER_DEFINED_PAYMENT_GROUPS("User defined payment groups"),
    TOTAL_PAYABLE_DEFINED("Total payable under payment groups define above"),
    TOTAL_PAYABLE_NOT_DEFINED("Total payable under payment groups not define above"),
    TOTAL_UNPAID_ON_HOLD("Total unpaid due to office on hold");


    private final String label;

    ReportLabelEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}