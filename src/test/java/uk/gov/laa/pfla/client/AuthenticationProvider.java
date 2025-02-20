package uk.gov.laa.pfla.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

@FunctionalInterface
public interface AuthenticationProvider {
    static AuthenticationProvider basicAuth(String username, String password) {
        return new AuthenticationProvider() {
            @Override
            public <T> HttpEntity<T> addAuthentication(T request) {
                var headers = new HttpHeaders() {{
                    setBasicAuth(username, password);
                }};
                return new HttpEntity<>(request, headers);
            }
        };
    }

    <T> HttpEntity<T> addAuthentication(T request);
}