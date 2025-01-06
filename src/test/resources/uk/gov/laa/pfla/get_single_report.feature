Feature: Get single report metadata

  @test
  Scenario: Should return report that exists
    Given the local service is running
    When it calls the get report endpoint with id "1"
    Then it should return a 200 response
    Then it should return details for report with id "1"