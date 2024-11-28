package uk.gov.laa.pfla.utils;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import uk.gov.laa.pfla.ServiceManager;

import java.io.IOException;

public class CucumberHooks {

    // This is a Cucucmber BeforeAll
    // We can also use a Junit one if we need to, they behave slightly differently.
    @BeforeAll
    public static void setup() throws IOException {
        ServiceManager.startService();
    }

    @AfterAll
    public static void teardown() {
        ServiceManager.stopService();
    }
}
