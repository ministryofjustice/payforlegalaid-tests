Feature: Call Actuator Endpoint

  Scenario: Return 200 response when not logged in
    Given the service is running and we are not logged in
    When it calls the actuator endpoint
    Then it should return a 200 response

  Scenario: Return 200 response when logged in
    Given the service is running and we are logged in
    When it calls the actuator endpoint
    Then it should return a 200 response
