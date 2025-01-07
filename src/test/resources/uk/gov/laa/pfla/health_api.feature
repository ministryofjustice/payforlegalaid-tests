Feature: Call Actuator Endpoint

  @local @dev
  Scenario: Return 200 response
    Given the service is running
    When it calls the actuator endpoint
    Then it should return a 200 response