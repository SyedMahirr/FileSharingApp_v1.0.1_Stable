Feature: API handshake

  Scenario: Handshake readiness
    Given the FileSharing server is running
    When I send GET request to "/handshake"
    Then the response code should be 200
    And the response should contain "HANDSHAKE-OK"
