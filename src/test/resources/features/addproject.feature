Feature:Add project for Register User and hosted Business

  As a registered user and hosted business
  I want to add my projects on the DialinArch platform
  So that I can added projects is visible on the platform

  Background:

  Given user is on dialinarch landing page
    When user clicks on Login Signup button
    And user fills registration form with data from Excel row 1
    And user clicks Create Profile button
    And user should enter the OTP
    When user clicks the "Profile Icon" button
    And user clicks the "Host Your Business" button
    And user proceeds to the Business Details page
    And user enters Business and Basic Details using Excel row 1
    And user proceeds to the Address Details page
    And user enters Address, Qualification, and Success Story using Excel row 1
    And user proceeds to the Address Details page
    And user proceeds to the Category Selection page
    And user proceeds to the Category Selection page
    And user selects Business Categories and accepts Terms & Conditions
    And user reviews the entered business details
    And user clicks the "Create Vendor Profile" button
  

  Scenario: Successfully register, host a business, and add a project using Excel data
    When the user clicks the "Add Project" button
    And the user enters project details using Excel row 1
    And the user uploads project images using Excel row 1
    And the user clicks the "Save Project" button
    Then the project should be added successfully
