package uk.gov.laa.pfla.configuration;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import uk.gov.laa.gpfd.config.builders.AuthorizeHttpRequestsBuilder;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfigTest {

    private final AuthorizeHttpRequestsBuilder authorizeHttpRequestsBuilder;

    @Bean
    @Primary
    @SneakyThrows
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .authorizeHttpRequests(authorizeHttpRequestsBuilder)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/oauth2/authorization/azure")
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .userDetailsService(userDetailsService())
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            List<String> roles = RoleRegistry.getRoles();
            log.info("GEEEET Roles in Scenario: " + roles);

            return User.withUsername(username)
                    .password("{noop}test-password")
                    .roles(roles.toArray(new String[0]))
                    .build();
        };
    }

}