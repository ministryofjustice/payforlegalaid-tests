package uk.gov.laa.pfla;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

public class Stepdefs {
    private Boolean dummy;
    private Boolean actualAnswer;

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
}