Feature: Return Hello World

  Scenario: Return Hello World
    Given this is a dummy test
    When it checks the test type
    Then it should return Hello World

  Scenario: Do not return Hello World
    Given this is a real test
    When it checks the test type
    Then it should not return Hello World