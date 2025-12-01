package uk.gov.laa.pfla.steps;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.laa.pfla.client.AuthenticationProvider;
import uk.gov.laa.pfla.configuration.PlaywrightManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UiSteps {

    private Page page;
    private int reportCount;
    private Response response;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Before("@ui")
    public void setupUI(){
        System.out.println("Ensuring Playwright is setup");
        PlaywrightManager.getPlaywright();
    }

    @After("@ui")
    public void cleanupPage(){
        System.out.println("Cleaning up page from last test");
        page.close();
        response = null;
    }

    @Given("The user is authorised in the UI")
    public void uiAuthSetup(){
        System.out.println("Creating an authorised test page");
        Browser browser = PlaywrightManager.getBrowser();
        BrowserContext browserContext = browser.newContext(new Browser.NewContextOptions()
                .setExtraHTTPHeaders(authenticationProvider.setAuthHeader().toSingleValueMap()));
        page = browserContext.newPage();
    }

    @Given("The user is not authorised in the UI")
    public void uiNoAuthSetup(){
        System.out.println("Creating an unauthorised test page");
        Browser browser = PlaywrightManager.getBrowser();
        page = browser.newPage();
    }

    @When("I load the GLAD page")
    public void loadGladPage() {
        response = page.navigate("http://localhost:8080/");
    }

    @Then("the page loads successfully")
    public void pageReturns200(){
        assertEquals(200, response.status());
    }

    @Then("the page title is correct")
    public void checkPageTitle(){
        assertEquals("Get Legal Aid Data", page.title());
    }

    @Then("the heading is set")
    public void checkPageHeading(){
        var locator = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setLevel(1));
        assertEquals("Get Legal Aid Data", locator.textContent());
    }

    @Then("there is at least one report row")
    public void checkReportsExist(){
        var rows = page.locator(".govuk-grid-row");
        // -1 as header row
        reportCount = rows.count() - 1;
        assertTrue(reportCount > 0);
    }

    @Then("there are download links")
    public void checkDownloadLinks(){
        var links = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("download"));
        var linkCount = links.count();
        // Should be as many download links as report rows
        assertEquals(reportCount, linkCount);
        assertTrue(links.first().getAttribute("href").matches(".*(csv|excel|file).*"));
    }

    @Then("return 401 unauthorised")
    public void returnUnauthorised() {
        assertEquals(401, response.status());
    }

}
