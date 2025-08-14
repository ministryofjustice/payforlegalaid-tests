package uk.gov.laa.pfla.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.laa.pfla.assertion.WorkbookAssert;
import uk.gov.laa.pfla.scenario.ScenarioContext;
import uk.gov.laa.pfla.service.HttpProvider;
import uk.gov.laa.pfla.util.WorkbookUtil;

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
}
