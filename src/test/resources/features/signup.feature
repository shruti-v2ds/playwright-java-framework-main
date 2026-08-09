Feature: User registration

  @signup
  Scenario: User registers with mobile OTP
    Given user is on dialinarch landing page
    When user navigates to signup page
    And user enters mobile "1234567890" for signup
    And user completes signup with OTP for mobile "1234567890"
    Then signup should be successful
