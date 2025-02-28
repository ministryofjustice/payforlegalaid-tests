package uk.gov.laa.pfla.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uk.gov.laa.pfla.scenario.ScenarioContext;
import uk.gov.laa.pfla.service.HttpProvider;

import static org.junit.jupiter.api.Assertions.assertTrue;

public record GetSingleReportSteps(HttpProvider httpProvider, ScenarioContext scenarioContext) {

    @When("a request is made to the reports endpoint with the report ID {string}")
    public void aRequestIsMadeToTheReportsEndpointWithTheReportId(String reportId) {
        var response = httpProvider.getClient().getForEntity("reports/" + reportId, String.class);
        scenarioContext.setResponse(response);
    }

    @Then("the response should include details for the report with ID {string}")
    public void theResponseShouldIncludeDetailsForTheReportWithId(String reportId) {
        var response = scenarioContext.getResponse();
        var responseBody = response.getBody();
        assertTrue(responseBody != null && responseBody.contains(reportId), "Expected response to include details for report with ID " + reportId);
    }

    @Then("the response should include the error message {string}")
    public void theResponseShouldIncludeTheErrorMessage(String errorMessage) {
        var response = scenarioContext.getResponse();
        var responseBody = response.getBody();
        assertTrue(responseBody != null && responseBody.contains(errorMessage), "Expected response to include error message: " + errorMessage);
    }
}
