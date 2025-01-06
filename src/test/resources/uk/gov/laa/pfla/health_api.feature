Feature: Call Actuator Endpoint

  @test
  Scenario: Return 200 response when not logged in
    Given the service is running
    When it calls the actuator endpoint
    Then it should return a 200 response