Feature: Smoke
  Scenario: Server health
    Given the file sharing server is running
    Then the health endpoint returns OK
