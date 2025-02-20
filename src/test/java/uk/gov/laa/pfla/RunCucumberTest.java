package uk.gov.laa.pfla;

import io.cucumber.java.BeforeAll;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.SpringApplication;

@Suite
@SelectClasspathResource("features")
public class RunCucumberTest {

    @BeforeAll
    public static void setup() {
        SpringApplication.run(Main.class);
    }
}
