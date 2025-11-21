@ui
Feature: Be able to view UI

  As a user of the system
  I want to use the UI to see which reports are available
  So that I can access and analyse data

  Scenario: Playwright test
  Given Playwright is Setup
  When I load the Playwright homepage
  Then I get the page title