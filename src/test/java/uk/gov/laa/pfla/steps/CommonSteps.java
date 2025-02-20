package uk.gov.laa.pfla.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import uk.gov.laa.pfla.scenario.ScenarioContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

public record CommonSteps(ScenarioContext scenarioContext) {

    @Given("I am authenticated with a valid session")
    public void iAmAuthenticatedWithAValidSession() {
        scenarioContext.validAuthenticationCookieProvided();
    }

    @Given("I am authenticated with an invalid session")
    public void iAmAuthenticatedWithAnInvalidSession() {
        scenarioContext.invalidAuthenticationCookieProvided();
    }

    @Then("the service should respond with a status code of {int}")
    public void theServiceShouldRespondWithAStatusCodeOf(int expectedStatusCode) {
        var response = scenarioContext.getResponse();
        assertEquals(expectedStatusCode, response.getStatusCodeValue(), "Expected status code " + expectedStatusCode);
    }

}
