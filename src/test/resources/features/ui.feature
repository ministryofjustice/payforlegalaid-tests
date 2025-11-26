@ui
Feature: Be able to view UI

  As a user of the system
  I want to use the UI to see which reports are available
  So that I can access and analyse data

  Scenario: Load the UI
    Given I am authenticated with a valid session
    When I load the GLAD page
    Then the page title is correct
    And the heading is set
    And there is at least one report row
    And there are download links