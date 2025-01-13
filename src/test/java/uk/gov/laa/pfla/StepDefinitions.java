package uk.gov.laa.pfla;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import jakarta.servlet.http.Cookie;
import uk.gov.laa.pfla.utils.ServiceUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static uk.gov.laa.pfla.utils.ServiceUtils.makeGetCall;

public class StepDefinitions {
    private Response response;
    private Cookie cookie;

    @Given("the service is running")
    public void theServiceIsRunning() throws IOException {
        if (System.getProperty("SERVICE").equals("local")) {
            assertDoesNotThrow(ServiceUtils::checkLocalServiceIsRunning);
        } else {
            Response actuatorResponse = ServiceUtils.checkServiceIsRunning();
            assertEquals(200, actuatorResponse.getStatusCode(), "Expected 200 OK response but received " + actuatorResponse.getStatusCode());
        }
    }

    @When("{string} cookie is provided for authentication")
    public void populateCookie(String cookieType) {
        cookie = new Cookie("JSESSIONID", "");
        //TODO This is not a permanent solution but allows us to update the cookie without changing the code for now!
        if (cookieType.equals("valid"))
            cookie.setValue(System.getProperty("cookie"));
    }

    @When("it calls the actuator endpoint")
    public void callActuatorApi() {
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

    @And("it calls the reports endpoint")
    public void callReportsEndpoint() {
        response = makeGetCall("reports", System.getProperty("BASE_URL"), cookie);
    }

    @Then("it should return a list of all the reports in the database")
    public void returnListOfReports() {
        List<Object> reportList = response.jsonPath().getList("reportList");
        assertFalse(reportList.isEmpty(), "Expected report details to be returned");
    }

    @And("it calls the get reports endpoint with id {string}")
    public void callReportEndpointForGivenId(String givenId) {
        response = makeGetCall("reports/" + givenId, System.getProperty("BASE_URL"));
    }

    @Then("it should return details for report with id {string}")
    public void returnReportDetails(String givenId) {
        assertEquals(givenId, response.jsonPath().getString("id"));
    }
}