package uk.gov.laa.pfla;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class StepDefinitions {
    private Response response;
    private Cookie cookie;
    public static final int payForLegalAidPort = 8080;
    public static final String serverName = "localhost";

    @Given("the service is running")
    public void theServiceIsRunning() throws InterruptedException {
        if (System.getProperty("SERVICE").equals("local")) {
            assertTrue(isLocalServiceIsRunning());
        } else {
            Response actuatorResponse = checkServiceIsRunning();
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
        response = makeGetCall("reports/" + givenId, System.getProperty("BASE_URL"), cookie);
    }

    @Then("it should return details for report with id {string}")
    public void returnReportDetails(String givenId) {
        assertEquals(givenId, response.jsonPath().getString("id"));
    }

    private boolean isLocalServiceIsRunning() throws InterruptedException {
        int timeout = 30; // Seconds
        while (timeout > 0) {
            try (Socket socket = new Socket(serverName, payForLegalAidPort)) {
                //Service is ready to be called
                return true;
            } catch (IOException e) {
                Thread.sleep(1000);
                timeout--;
            }
        }
        return false;
    }

    private Response checkServiceIsRunning() {
        return makeGetCall("actuator", System.getProperty("BASE_URL"));
    }

    private Response makeGetCall(String endpoint, String baseUrl) {
        return given().baseUri(baseUrl).redirects().follow(false)
                .get(endpoint);
    }

    private Response makeGetCall(String endpoint, String baseUrl, Cookie cookie) {
        return given().baseUri(baseUrl).redirects().follow(false).headers("cookie", cookie.getName() + "=" + cookie.getValue())
                .get(endpoint);
    }

}