Feature: Call Health Endpoint

  Scenario: Return 200 response
    Given the service is running
    When it calls the health AI
    Then it should return a 200 response
