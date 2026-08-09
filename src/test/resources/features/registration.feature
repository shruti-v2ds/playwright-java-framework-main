Feature: User Registration
  As a new user
  I want to register on DialinArch platform
  So that I can create my profile and use the platform

  Background:
    Given user is on dialinarch landing page

  @registration
  Scenario: User fills registration form and creates profile with details from Excel
    When user clicks on Login Signup button
    And user fills registration form with data from Excel row 1
    And user clicks Create Profile button
    And user should enter the OTP
    Then registration should be successful

  @registration
  Scenario: User fills registration form and creates profile with details from Excel row 2
    When user clicks on Login Signup button
    And user fills registration form with data from Excel row 2
    And user clicks Create Profile button
    And user should enter the OTP
    Then registration should be successful

  @registration
  Scenario: User fills registration form and creates profile with details from Excel row 3
    When user clicks on Login Signup button
    And user fills registration form with data from Excel row 3
    And user clicks Create Profile button
    And user should enter the OTP
    Then registration should be successful