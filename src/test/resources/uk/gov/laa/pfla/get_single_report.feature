Feature: Get Single Report

Background:
    Given the service is running

  @local @dev @uat
  Scenario: Should return report that exists
    When "valid" cookie is provided for authentication
    And it calls the get reports endpoint with id "1"
    Then it should return a 200 response
    Then it should return details for report with id "1"

  @dev @uat
  Scenario: Should redirect to login
    When "invalid" cookie is provided for authentication
    And it calls the get reports endpoint with id "1"
    Then it should return a 302 response