Feature: List All Available Reports

  @test
  Scenario: Should return all reports
    Given the local service is running
    When it calls the reports endpoint
    Then it should return a 200 response
    Then it should return a list of all the reports in the database

  @dev
  Scenario: Should redirect to login
    Given the service is running
    When it calls the reports endpoint
    Then it should return a 302 response

  @dev
  Scenario: Should return all reports
    Given the service is running
    And a user is logged in
    When it calls the reports endpoint with auth token
    Then it should return a 200 response
    Then it should return a list of all the reports in the database
