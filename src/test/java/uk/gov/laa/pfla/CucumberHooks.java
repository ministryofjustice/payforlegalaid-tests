package uk.gov.laa.pfla;

import io.cucumber.java.After;
import io.cucumber.java.Before;

import java.io.IOException;

public class CucumberHooks {
    private ServiceManager serviceManager;

    // This starts up the payforlegalaid service before every test
    // Currently each test uses a newly running instance of the service
    @Before
    public void setup() throws IOException {
        serviceManager = new ServiceManager();
        serviceManager.startService();
    }

    @After
    public void teardown() {

        if (serviceManager != null) {
            serviceManager.stopService();
        }
    }

}