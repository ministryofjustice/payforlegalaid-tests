Feature: List All Available Reports

  Background:
    Given the service is running

    @dev @local @uat
  Scenario: Should return all reports
      When it calls the reports endpoint
      Then it should return a 200 response
      Then it should return a list of all the reports in the database

    @dev @uat
  Scenario: Should redirect to login
      When it calls the reports endpoint
      Then it should return a 302 response
