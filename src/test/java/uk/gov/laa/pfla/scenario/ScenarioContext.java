package uk.gov.laa.pfla.scenario;

import java.util.HashMap;
import java.util.Map;
import static java.util.Optional.ofNullable;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.cucumber.spring.ScenarioScope;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import static uk.gov.laa.pfla.scenario.AuthenticationState.AUTHENTICATED;
import static uk.gov.laa.pfla.scenario.AuthenticationState.INVALID_CREDENTIALS;
import uk.gov.laa.pfla.util.JsonDeserializer;

@Data
@Component
@ScenarioScope
public class ScenarioContext {
    private AuthenticationState authenticationState;
    private ResponseEntity<?> response;
    private final JsonDeserializer jsonDeserializer;
    private final Map<String, Object> attributes = new HashMap<>();

    public <T> T deserialize(Class<T> valueType) {
        return jsonDeserializer.deserializeFromResponse(getResponseAs(String.class), valueType);
    }

    public <T> ResponseEntity<T> getResponseAs(Class<T> responseType) {
        return ofNullable(response)
                .filter(res -> responseType.isInstance(res.getBody()))
                .map(res -> ResponseEntity.ok(responseType.cast(res.getBody())))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Response is not of type " + responseType.getSimpleName()));
    }

    public void validAuthenticationCookieProvided() {
        this.authenticationState = AUTHENTICATED;
    }

    public void invalidAuthenticationCookieProvided() {
        this.authenticationState = INVALID_CREDENTIALS;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }
}