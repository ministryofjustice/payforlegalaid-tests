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

public class StepDefinitions {
    private Response response;

    @Given("the local service is running")
    public void the_local_service_is_running() throws IOException {
        ServiceManager.startService();
        assertDoesNotThrow(ServiceUtils::checkLocalServiceIsRunning);
    }

    @Given("the service is running")
    public void the_service_is_running() {
        assertDoesNotThrow(ServiceUtils::checkServiceIsRunning);
    }

    @And("a user is logged in")
    public void user_is_logged_in(){


    }

    @When("it calls the actuator endpoint")
    public void call_health_api() {
        response = makeGetCall("actuator", "");
    }

    @Then("it should return a 200 response")
    public void it_should_return_200() {
        assertEquals(200, response.getStatusCode(), "Expected 200 OK response but received " + response.getStatusCode());
    }

    @Then("it should return a 302 response")
    public void it_should_return_302() {
        assertEquals(302, response.getStatusCode(), "Expected 302 OK response but received " + response.getStatusCode());
    }

    @When("it calls the reports endpoint")
    public void call_reports_endpoint() {
        System.out.println("Base url: " + System.getProperty("BASE_URL"));
        response = makeGetCall("reports", System.getProperty("BASE_URL"));
    }

    @Then("it should return a list of all the reports in the database")
    public void return_list_of_reports() {
        List<Object> reportList = response.jsonPath().getList("reportList");
        assertFalse(reportList.isEmpty(), "Expected report details to be returned");
    }

    @When("it calls the get report endpoint with id {string}")
    public void call_report_endpoint_for_given_id(String givenId){
       response = makeGetCall("report", givenId);
    }

    @Then("it should return details for report with id {string}")
    public void return_report_details(String givenId){
        assertEquals(givenId, response.jsonPath().getString("id"));
    }

}