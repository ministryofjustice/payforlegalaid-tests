package uk.gov.laa.pfla.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import uk.gov.laa.pfla.configuration.RoleRegistry;

import java.util.List;

@Slf4j
public class RoleSetupHook {

    @Before
    public void beforeScenario(Scenario scenario) {
        log.info("SCCCE : " + scenario.getName());
        List<String> roles = scenario.getSourceTagNames().stream()
                .filter(t -> t.startsWith("@Role="))
                .map(t -> t.replace("@Role=", ""))
                .toList();

        log.info("Roles in Scenario: " + roles);

        //set Default role = Financial
        if (roles.isEmpty()) {
            roles = List.of("Financial");
        }
        RoleRegistry.setRoles(roles);
    }

    @After
    public void afterScenario() {
        RoleRegistry.clear();
    }
}
