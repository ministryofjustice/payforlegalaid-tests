Feature: Get CSV report

  Background:
    Given the service is running

  @local @dev
  Scenario: Should create and return the CSV report
    Given csv test data is setup in database
    When "valid" cookie is provided for authentication
    And it calls the get csv endpoint
    Then it should return a 200 response
    Then it should return the csv file

  @dev
  Scenario: Should create and return the CSV report
    When "invalid" cookie is provided for authentication
    And it calls the get csv endpoint
    Then it should return a 302 response