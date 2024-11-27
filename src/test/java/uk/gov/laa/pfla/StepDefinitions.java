package uk.gov.laa.pfla;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import uk.gov.laa.pfla.utils.ServiceUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StepDefinitions {
    private HttpResponse response;

    @Given("the service is running and we are not logged in")
    public void the_service_is_running() {
        //The service should be set running by the Cucumber Before hook.
        assertDoesNotThrow(ServiceUtils::checkServiceIsRunning);
    }

    @When("it calls the actuator endpoint")
    public void call_health_api() {

        assertDoesNotThrow(() -> {
                    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                        HttpGet request = new HttpGet(ServiceUtils.getUrl("actuator"));
                        response = httpClient.execute(request);
                    }
                }
        );

    }

    @Then("it should return a 401 response")
    public void it_should_return_401() {
        assertEquals(401, response.getStatusLine().getStatusCode());
    }
}