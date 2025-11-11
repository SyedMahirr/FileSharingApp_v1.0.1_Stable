Feature: HTTP transfer basic contract
  Scenario: Handshake is available
    Given the file sharing server is running
    Then the handshake endpoint returns READY
