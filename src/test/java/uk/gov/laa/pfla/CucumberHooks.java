package uk.gov.laa.pfla;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import uk.gov.laa.pfla.ServiceManager;

import java.io.IOException;

public class CucumberHooks {

    @BeforeAll
    public static void setup() throws IOException {
        ServiceManager.startService();
    }

    @AfterAll
    public static void teardown() {
        ServiceManager.stopService();
    }
}