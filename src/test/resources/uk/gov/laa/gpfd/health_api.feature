Feature: Call Health Endpoint

  Scenario: Return 401 response
    Given the service is running and we are not logged in
    When it calls the actuator endpoint
    Then it should return a 401 response
