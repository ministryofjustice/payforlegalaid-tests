package uk.gov.laa.pfla;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import static org.junit.jupiter.api.Assertions.*;

public class Stepdefs {
    private Boolean dummy;
    private Boolean actualAnswer;
    private HttpResponse response;

    @Given("this is a dummy test")
    public void this_is_a_dummy_test() {
        dummy = true;
    }

    @When("it checks the test type")
    public void it_checks_the_test_type() {
        actualAnswer = dummy;
    }

    @Then("it should return Hello World")
    public void it_should_return() {
        assertTrue(actualAnswer);
    }

    @Given("this is a real test")
    public void this_is_a_real_test() {
        dummy = false;
    }

    @Then("it should not return Hello World")
    public void it_should_not_return() {
        assertFalse(actualAnswer);
    }

    @Given("the service is running")
    public void run_the_service() {
        // Will spin up service
    }

    @When("it calls the health API")
    public void call_health_api() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/health");
            response = httpClient.execute(request);
        } catch (Exception ex) {
            // TODO
        }

    }

    @Then("it should return a 200 response")
    public void it_should_return_200() {
        assertEquals(200, response.getStatusLine().getStatusCode());
    }
}