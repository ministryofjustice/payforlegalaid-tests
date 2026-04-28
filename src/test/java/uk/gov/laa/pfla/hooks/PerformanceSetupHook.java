package uk.gov.laa.pfla.hooks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.Before;
import uk.gov.laa.gpfd.model.ReportsGet200Response;
import uk.gov.laa.pfla.performance.PerformanceReportRegistry;
import static uk.gov.laa.pfla.scenario.AuthenticationState.AUTHENTICATED;
import uk.gov.laa.pfla.service.HttpProvider;

public class PerformanceSetupHook {

    private final HttpProvider httpProvider;
    private static boolean initialised = false;

    public PerformanceSetupHook(HttpProvider httpProvider) {
        this.httpProvider = httpProvider;
    }

    @Before("@performance")
    public void fetchReports() {
        if (initialised) return; // only run once per suite

        // Performance tests use Basic Auth, so set the state before making HTTP calls
        httpProvider.setAuthenticationState(AUTHENTICATED);

        var response = httpProvider.getClient().getForEntity("/reports", String.class);
        String responseBody = response.getBody();

        if (responseBody == null || responseBody.isEmpty()) {
            throw new IllegalStateException(
                    "Failed to fetch /reports — check the application is running and accessible"
            );
        }

        // Check for service unavailability error
        if (responseBody.contains("unavailable")) {
            throw new IllegalStateException(
                    "Service is currently unavailable: " + responseBody
            );
        }

        // Parse JSON response manually
        ObjectMapper mapper = new ObjectMapper();
        ReportsGet200Response reportsResponse;
        try {
            reportsResponse = mapper.readValue(responseBody, ReportsGet200Response.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse /reports response", e);
        }

        PerformanceReportRegistry.populate(reportsResponse.getReportList());
        System.out.println("Performance registry loaded: " +
                reportsResponse.getReportList().size() + " reports");

        initialised = true;
    }
}