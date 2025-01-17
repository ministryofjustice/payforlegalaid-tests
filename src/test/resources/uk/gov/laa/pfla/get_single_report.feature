Feature: Get Single Report

  Background:
    Given the service is running
    Given csv test data is setup in database

  @local @dev @uat
  Scenario: Should return report that exists
    When "valid" cookie is provided for authentication
    And it calls the get reports endpoint with id "c16eb360-6f61-4588-882e-a9528429e82e"
    Then it should return a 200 response
    Then it should return details for report with id "c16eb360-6f61-4588-882e-a9528429e82e"

  @dev @uat
  Scenario: Should redirect to login
    When "invalid" cookie is provided for authentication
    And it calls the get reports endpoint with id "c16eb360-6f61-4588-882e-a9528429e82e"
    Then it should return a 302 response

  @local @dev @uat
  Scenario: Should return unrecognised id error for unknown id
    When "valid" cookie is provided for authentication
    And it calls the get reports endpoint with id "899"
    Then it should return a 404 response
    Then it should return error message "Report with unrecognised ID"
