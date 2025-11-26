package uk.gov.laa.pfla.steps;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.laa.pfla.client.AuthenticationProvider;
import uk.gov.laa.pfla.configuration.PlaywrightManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UiSteps {

    private Page page;

    @Before("@ui")
    public void setupUI(){
        PlaywrightManager.getPlaywright();
        Browser browser = PlaywrightManager.getBrowser();
        System.out.println("Creating a page to test with");
        BrowserContext browserContext = browser.newContext(new Browser.NewContextOptions()
                .setExtraHTTPHeaders(authenticationProvider.setAuthHeader().toSingleValueMap()));
        page = browserContext.newPage();
    }

    @When("I load the GLAD page")
    public void loadGladPage() {
        var response = page.navigate("http://localhost:8080/");
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

}
