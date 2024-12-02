package uk.gov.laa.pfla;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import uk.gov.laa.pfla.utils.ServiceUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StepDefinitions {
    private HttpResponse response;

    @Given("the service is running and we are not logged in")
    public void the_service_is_running() {
        //The service should be set running by the Cucumber @Before hook.
        assertDoesNotThrow(ServiceUtils::checkServiceIsRunning);
    }

    @When("it calls the actuator endpoint")
    public void call_health_api() {
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000).build(); // 30 seconds

        assertDoesNotThrow(() -> {
                    try (CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build()) {
                        HttpGet request = new HttpGet(ServiceUtils.getUrl("actuator"));
                        response = httpClient.execute(request);
                    }
                }
        );

    }

    @Then("it should return a 200 response")
    public void it_should_return_200() {
        assertEquals(200, response.getStatusLine().getStatusCode(), "Expected 200 OK response but received " + response.getStatusLine().getStatusCode());
    }
}