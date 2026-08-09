Feature: Login functionality

  @login
  Scenario: Successful login with OTP
    Given user is on dialinarch landing page
    When user logs in with OTP
    Then user should be logged in successfully

  @login
  Scenario: Successful login with specific mobile
    Given user is on dialinarch landing page
    When user logs in with OTP using mobile "1234567890"
    Then user should be logged in successfully
