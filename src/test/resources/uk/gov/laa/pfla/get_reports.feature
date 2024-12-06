Feature: List All Available Reports

  Scenario: Should return all reports
    Given the service is running and we are logged in
    When it calls the reports endpoint
    Then it should return a 200 response
    Then it should return a list of all the reports in the database