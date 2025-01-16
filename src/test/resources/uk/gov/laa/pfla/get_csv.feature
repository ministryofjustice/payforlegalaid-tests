Feature: Get CSV report

  Background:
    Given the service is running
    Given csv test data is setup in database

  @local @dev @uat
  Scenario: Should create and return the CSV report
    When "valid" cookie is provided for authentication
    And it calls the get csv endpoint with id "c16eb360-6f61-4588-882e-a9528429e82e"
    Then it should return a 200 response
    Then it should return the csv file

  @dev @uat
  Scenario: Should create and return the CSV report
    When "invalid" cookie is provided for authentication
    And it calls the get csv endpoint with id "c16eb360-6f61-4588-882e-a9528429e82e"
    Then it should return a 302 response
