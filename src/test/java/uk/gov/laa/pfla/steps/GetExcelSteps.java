package uk.gov.laa.pfla.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uk.gov.laa.pfla.assertion.WorkbookHelper;
import uk.gov.laa.pfla.assertion.SheetLabelValidator;
import uk.gov.laa.pfla.assertion.CellValidationRule;
import uk.gov.laa.pfla.assertion.ReportLabelEnum;
import uk.gov.laa.pfla.assertion.FieldValidators;
import uk.gov.laa.pfla.assertion.WorkbookAssert;
import uk.gov.laa.pfla.scenario.ScenarioContext;
import uk.gov.laa.pfla.service.HttpProvider;
import uk.gov.laa.pfla.util.WorkbookUtil;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.ContentDisposition.attachment;
import static org.springframework.http.MediaType.parseMediaType;

public record GetExcelSteps(HttpProvider httpProvider, ScenarioContext scenarioContext, WorkbookUtil excelService) {

    private static final String APPLICATION_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @When("a request is made to the Excel endpoint with the report ID {string}")
    public void aRequestIsMadeToTheExcelEndpointWithTheReportId(String reportId) {
        scenarioContext.setResponse(httpProvider.getClient().getForEntity("/excel/" + reportId, byte[].class));
    }

    @Then("the response should include the Excel file with {string} report")
    public void verifyExcelResponse(String file) {
        var currentResult = excelService.getExcelWorkbook();
        var expectedResult = excelService.loadExcelFromResources("expected_results/{0}.xlsx", file);
        var headers = scenarioContext.getResponseAs(Object.class).getHeaders();

        assertAll("Verify Excel response",
                () -> assertThat(headers.getContentType()).isEqualTo(parseMediaType(APPLICATION_EXCEL)),
                () -> assertThat(headers.getContentDisposition()).isEqualTo(
                    attachment().filename(file + ".xlsx").build()),
                () -> WorkbookAssert.assertThat(currentResult).isEqualTo(expectedResult),
                () -> WorkbookAssert.assertThat(currentResult).hasAllFormulasEvaluated(),
                () -> WorkbookAssert.assertThat(currentResult).hasValidPivotTables()
        );
    }

    @Then("the report is not empty")
    public void theExcelReportShouldNotBeEmpty() {
        var workbook = excelService.getExcelWorkbook();

        assertThat(workbook).isNotNull();
        assertThat(workbook.getNumberOfSheets()).isGreaterThan(0);
        assertThat(workbook.getSheetAt(0)).isNotNull();
        assertThat(WorkbookHelper.getRequiredSheet(workbook, ReportLabelEnum.MAIN.getLabel())).isNotNull();

    }
    @Then("the report should contain all mandatory fields and valid values")
    public void theExcelReportShouldPassMainTabHybridChecks() {
        var workbook = excelService.getExcelWorkbook();
        var sheet = workbook.getSheet(ReportLabelEnum.MAIN.getLabel());

        var checks = List.of(
                SheetLabelValidator.of(
                        ReportLabelEnum.USER_DEFINED_PAYMENT_GROUPS.getLabel(),
                        new CellValidationRule(+1, 0, FieldValidators.STRING_NONBLANK)
                ),
                SheetLabelValidator.of(
                        ReportLabelEnum.TOTAL_PAYABLE_DEFINED.getLabel(),
                        new CellValidationRule(0, +1, FieldValidators.NUMERIC),
                        new CellValidationRule(0, +2, FieldValidators.CURRENCY_GBP)
                ),
                SheetLabelValidator.of(
                        ReportLabelEnum.TOTAL_PAYABLE_NOT_DEFINED.getLabel(),
                        new CellValidationRule(0, +1, FieldValidators.NUMERIC),
                        new CellValidationRule(0, +2, FieldValidators.CURRENCY_GBP)
                ),
                SheetLabelValidator.of(
                        ReportLabelEnum.TOTAL_UNPAID_ON_HOLD.getLabel(),
                        new CellValidationRule(0, +1, FieldValidators.NUMERIC),
                        new CellValidationRule(0, +2, FieldValidators.CURRENCY_GBP)
                )
        );

        WorkbookAssert.assertThat(workbook).hasTabChecks(sheet, checks);
    }
}
