package uk.gov.laa.pfla;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import uk.gov.laa.pfla.utils.ServiceUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static uk.gov.laa.pfla.utils.ServiceUtils.makeGetCall;

public class StepDefinitions {
    private Response response;

    @Given("the service is running and we are not logged in")
    public void the_service_is_running_and_not_logged_in() {
        //The service should be set running by the Cucumber @Before hook.
        assertDoesNotThrow(ServiceUtils::checkServiceIsRunning);
    }

    @Given("the service is running and we are logged in")
    public void the_service_is_running_and_logged_in() {

        // On local profile there is no need to login
        // When running on dev/uat profiles in future we need to put some auth code here

        //The service should be set running by the Cucumber @Before hook.
        assertDoesNotThrow(ServiceUtils::checkServiceIsRunning);
    }

    @When("it calls the actuator endpoint")
    public void call_health_api() {
        response = makeGetCall("actuator");
    }

    @Then("it should return a 200 response")
    public void it_should_return_200() {
        assertEquals(200, response.getStatusCode(), "Expected 200 OK response but received " + response.getStatusCode());
    }

    @When("it calls the reports endpoint")
    public void call_reports_endpoint() {
        response = makeGetCall("reports");
    }

    @Then("it should return a list of all the reports in the database")
    public void return_list_of_reports() {

        List<Object> blah = response.jsonPath().getList("reportList");
        assertFalse(blah.isEmpty(), "Expected report details to be returned");
    }


}