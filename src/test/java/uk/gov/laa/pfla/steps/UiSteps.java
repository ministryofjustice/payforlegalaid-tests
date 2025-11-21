package uk.gov.laa.pfla.steps;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UiSteps {

    private Playwright playwright;
    private Browser browser;
    private Page page;

    @Before("@ui")
    public void setupUI(){
        playwright = Playwright.create();
        System.out.println("Playwright bein' created");
        browser = playwright.chromium().launch();
        page = browser.newPage();
    }

    @After("@ui")
    public void closePlaywright(){
        playwright.close();
    }

    @Given("Playwright is Setup")
    public void playwrightSetup(){
        //TODO should this happen everytime or once??
        assertTrue(browser.isConnected());
    }

    @When("I load the Playwright homepage")
    public void loadPlaywrightHomepage() {
        page.navigate("https://playwright.dev");
    }

    @Then("I get the page title")
    public void checkPageTitle(){
        assertEquals("Fast and reliable end-to-end testing for modern web apps | Playwright", page.title());
    }

}
