package uk.gov.laa.pfla.steps;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Value;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.LoadState;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uk.gov.laa.pfla.client.AuthenticationProvider;
import uk.gov.laa.pfla.performance.PerformanceReportRegistry;
import uk.gov.laa.pfla.scenario.ScenarioContext;

public class PerformanceSteps {

    private final ScenarioContext scenarioContext;

    @Value("${gpfd.url}")
    private String baseUrl;

    private Playwright playwright;
    private BrowserContext context;
    private Page page;

    public PerformanceSteps(ScenarioContext scenarioContext, AuthenticationProvider authenticationProvider) {
        this.scenarioContext = scenarioContext;
    }

    @Before("@performance")
    public void launchBrowser() {
        playwright = Playwright.create();

        Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true)
        );

        context = browser.newContext();
        page = context.newPage();

        String sessionCookie = System.getenv("JSESSIONID");
        if (sessionCookie != null && !sessionCookie.isEmpty()) {
            Cookie cookie = new Cookie("JSESSIONID", sessionCookie)
                    .setDomain(getDomainFromUrl(baseUrl))
                    .setPath("/")
                    .setSecure(baseUrl.startsWith("https"));
            context.addCookies(List.of(cookie));
        } else {
            System.out.println("WARNING: JSESSIONID env var not set. Set JSESSIONID=<cookie-value> to authenticate.");
        }
    }

    private String getDomainFromUrl(String url) {
        try {
            return java.net.URI.create(url).getHost();
        } catch (Exception e) {
            return "localhost";
        }
    }

    @After("@performance")
    public void closeBrowser() {
        if (playwright != null) {
            playwright.close();
        }
    }

    @When("the user navigates to the reports listing page")
    public void navigateToReportListPage() {
        System.out.println("DEBUG: Active cookies: " + context.cookies());
        page.navigate(baseUrl + "/reports");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Then("the page should load within {int} milliseconds")
    public void assertPageLoadTime(int thresholdMs) {
        Object result = page.evaluate("""
        () => {
            const nav = performance.getEntriesByType('navigation')[0];
            return nav.loadEventEnd - nav.startTime;
        }
    """);

        double loadTime = ((Number) result).doubleValue();

        System.out.printf("UI page load time: %.0fms (SLA: %dms)%n", loadTime, thresholdMs);
        assertThat(loadTime)
                .describedAs("Page load time should be under %dms", thresholdMs)
                .isLessThan(thresholdMs);
    }

    @When("the user downloads the {word} {word} report via the UI")
    public void downloadReport(String size, String format) {

        String reportId = PerformanceReportRegistry
                .getReportIdBySizeAndFormat(size, format)
                .orElseThrow(() -> new IllegalStateException(
                        "No report found for size: " + size + ", format: " + format
                ));

        System.out.println("DEBUG: Downloading " + size + " " + format + " report, ID = " + reportId);
        System.out.println("DEBUG: URL = " + baseUrl + "/reports/" + reportId + "/" + format);

        long start = System.currentTimeMillis();
        scenarioContext.setAttribute("downloadStart", start);

        final int[] responseStatus = {0};
        page.onResponse(response -> {
            System.out.println("DEBUG: Response received: " + response.url() + " status=" + response.status());
            responseStatus[0] = response.status();
            if (response.url().contains("/" + format)) {
                long ttfb = System.currentTimeMillis() - start;
                scenarioContext.setAttribute("ttfb", (double) ttfb);
            }
        });

        try {
            // First try to wait for a download event
            Download download = page.waitForDownload(
                    new Page.WaitForDownloadOptions().setTimeout(5000),
                    () -> page.navigate(
                            baseUrl
                                    + "/reports/"
                                    + reportId
                                    + "/"
                                    + format
                    )
            );

            download.path(); // wait for completion
            long duration = System.currentTimeMillis() - start;
            scenarioContext.setAttribute("downloadDuration", duration);
            System.out.println("DEBUG: Download completed in " + duration + "ms");

        } catch (Exception e) {
            // If no download event, the response is returned directly
            // Just measure the time to get the response
            System.out.println("DEBUG: Direct response (no download event), measuring response time");
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            long duration = System.currentTimeMillis() - start;
            scenarioContext.setAttribute("downloadDuration", duration);
            scenarioContext.setAttribute("ttfb", (double) duration);
            System.out.println("DEBUG: Response received in " + duration + "ms");
        }

        int status = responseStatus[0];
        if (status >= 400) {
            throw new IllegalStateException(
                    "Download request failed with status " + status +
                            " for " + size + " " + format + " — check report IDs and formats"
            );
        }
    }

    @Then("the time to first byte should be within {int} milliseconds")
    public void assertTtfb(int thresholdMs) {

        double ttfb = (double) scenarioContext.getAttribute("ttfb");

        System.out.printf("TTFB: %.0fms (SLA: %dms)%n", ttfb, thresholdMs);

        assertThat(ttfb)
                .describedAs("TTFB should be under %dms", thresholdMs)
                .isLessThan(thresholdMs);
    }

    @Then("the download should complete within {int} milliseconds")
    public void assertDownloadCompletion(int thresholdMs) {

        long duration = (long) scenarioContext.getAttribute("downloadDuration");

        System.out.printf("Download duration: %dms (SLA: %dms)%n", duration, thresholdMs);

        assertThat(duration)
                .describedAs("Download should complete within %dms", thresholdMs)
                .isLessThan(thresholdMs);
    }
}