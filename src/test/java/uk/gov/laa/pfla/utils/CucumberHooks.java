package uk.gov.laa.pfla.utils;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import uk.gov.laa.pfla.ServiceManager;

import java.io.IOException;

public class CucumberHooks {

    @AfterAll
    public static void teardown() {
        ServiceManager.stopService();
    }
}
