Feature: List All Available Reports

  Background:
    Given the service is running

    @dev @local
  Scenario: Should return all reports
      When "valid" cookie is provided for authentication
      And it calls the reports endpoint
      Then it should return a 200 response
      Then it should return a list of all the reports in the database

    @dev
  Scenario: Should redirect to login
      When "invalid" cookie is provided for authentication
      And it calls the reports endpoint
      Then it should return a 302 response
