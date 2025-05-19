@NotReady
Feature: Generate and Retrieve CSV Report

  As a user of the system
  I want to generate and retrieve a CSV report
  So that I can analyze and work with the data in a structured format

  Scenario: Successfully generate and retrieve a CSV report with valid authentication
    Given I am authenticated with a valid session
    And a request is made to the reports endpoint
    When a user request csv for a randomly selected report
    Then the service should respond with a status code of 200
    And the response should include the CSV file

  Scenario: Return an error when attempting to retrieve a report with an unrecognized ID
    Given I am authenticated with a valid session
    When a request is made to the CSV endpoint with the report ID "01010101-0101-0101-0101-010101010101"
    Then the service should respond with a status code of 404
    And the response should include the error message "Report with unrecognised ID"

#  Scenario: Redirect to login when attempting to retrieve a CSV report with invalid authentication
#    When an invalid authentication cookie is provided
#    And a request is made to the CSV endpoint with the report ID "c16eb360-6f61-4588-882e-a9528429e82e"
#    Then the service should respond with a status code of 302
#    And the user should be redirected to the login page
