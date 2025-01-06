package uk.gov.laa.pfla;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import uk.gov.laa.pfla.utils.ServiceUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static uk.gov.laa.pfla.utils.ServiceUtils.makeGetCall;
import static uk.gov.laa.pfla.utils.ServiceUtils.makeGetCallWithAuth;

public class StepDefinitions {
    private Response response;
    private String cookie;

    @Given("the local service is running")
    public void theLocalServiceIsRunning() throws IOException {
        ServiceManager.startService();
        assertDoesNotThrow(ServiceUtils::checkLocalServiceIsRunning);
    }

    @Given("the service is running")
    public void theServiceIsRunning() {
        assertDoesNotThrow(ServiceUtils::checkServiceIsRunning);
    }

    @And("a user is logged in")
    public void userIsLoggedIn(){
        cookie = "JSESSIONID=[Insert cookie value here]";
    }

    @When("it calls the actuator endpoint")
    public void callHealthApi() {
        response = makeGetCall("actuator", System.getProperty("BASE_URL"));
    }

    @Then("it should return a 200 response")
    public void itShouldReturn200() {
        assertEquals(200, response.getStatusCode(), "Expected 200 OK response but received " + response.getStatusCode());
    }

    @Then("it should return a 302 response")
    public void itShouldReturn302() {
        assertEquals(302, response.getStatusCode(), "Expected 302 OK response but received " + response.getStatusCode());
    }

    @When("it calls the reports endpoint")
    public void callReportsEndpoint() {
        if (cookie != null) {
            response = makeGetCallWithAuth("reports?continue", System.getProperty("BASE_URL"), cookie);
        } else {
            response = makeGetCall("reports", System.getProperty("BASE_URL"));
        }
    }

    @Then("it should return a list of all the reports in the database")
    public void returnListOfReports() {
        List<Object> reportList = response.jsonPath().getList("reportList");
        assertFalse(reportList.isEmpty(), "Expected report details to be returned");
    }

    @When("it calls the get report endpoint with id {string}")
    public void callReportEndpointForGivenId(String givenId){
       response = makeGetCall("report", givenId);
    }

    @Then("it should return details for report with id {string}")
    public void returnReportDetails(String givenId){
        assertEquals(givenId, response.jsonPath().getString("id"));
    }

}