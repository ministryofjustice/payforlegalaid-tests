package uk.gov.laa.pfla.utils;

import io.cucumber.java.AfterAll;
import uk.gov.laa.pfla.ServiceManager;

public class CucumberHooks {

    @AfterAll
    public static void teardown() {
        ServiceManager.stopService();
    }
}
