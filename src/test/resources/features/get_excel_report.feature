Feature: Generate and Retrieve Excel Financial Report

  As a user of the system
  I want to generate and retrieve an Excel report
  So that I can analyze and work with financial data in a structured and familiar format

  Scenario: Successfully generate and retrieve an Excel report with valid authentication
    Given I am authenticated with a valid session
    When a request is made to the Excel endpoint with the report ID "b36f9bbb-1178-432c-8f99-8090e285f2d3"
    Then the service should respond with a status code of 200
    And the response should include the Excel file with "CCMS Invoice Analysis (CIS to CCMS)" report

  Scenario: Return an error when attempting to retrieve a report with an unrecognized ID
    Given I am authenticated with a valid session
    When a request is made to the Excel endpoint with the report ID "01010101-0101-0101-0101-010101010101"
    Then the service should respond with a status code of 404
    And the response should include the error message "Report not found for ID"

  Scenario: Return an error when report generation fails due to invalid query
    Given I am authenticated with a valid session
    When a request is made to the Excel endpoint with the report ID "0fbec75b-2d72-44f5-a0e3-2dcb29d92f79"
    Then the service should respond with a status code of 500
    And the response should include the error message "Error reading from DB"
