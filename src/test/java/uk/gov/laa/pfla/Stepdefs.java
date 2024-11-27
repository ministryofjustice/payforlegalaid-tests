package uk.gov.laa.pfla;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class Stepdefs {
    private Boolean dummy;
    private Boolean actualAnswer;
    private HttpResponse response;

    private ServiceManager serviceManager;

    public void waitForService(int port) throws InterruptedException {
        int timeout = 30; // seconds
        while (timeout > 0) {
            try (Socket socket = new Socket("localhost", port)) {
                return; // Service is ready
            } catch (IOException e) {
                Thread.sleep(1000);
                timeout--;
            }
        }
        throw new RuntimeException("Service did not start in time");
    }

    @Before
    public void setup() throws IOException {
        serviceManager = new ServiceManager();
        serviceManager.startService();
    }
    @After
    public void teardown() {
        if (serviceManager != null) {
            serviceManager.stopService();
        }
    }

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

    @Given("the service is running and we are not logged in")
    public void run_the_service() {
        // Will spin up service
        try {
            waitForService(8080);
        } catch (InterruptedException e) {
            //TODO
        }
    }

    @When("it calls the actuator endpoint")
    public void call_health_api() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/actuator");
            response = httpClient.execute(request);
        } catch (Exception ex) {
            // TODO
        }

    }

    @Then("it should return a 401 response")
    public void it_should_return_401() {
        assertEquals(401, response.getStatusLine().getStatusCode());
    }
}