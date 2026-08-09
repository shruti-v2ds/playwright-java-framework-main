Feature: Register User Business Hosting

  As a registered user
  I want to host my business on the DialinArch platform
  So that I can create my business profile and use the platform

  Background:
  Given user is on dialinarch landing page
   When registered user is on the DialinArch landing page 2
  
  Scenario: Successfully host a new business using Excel data

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
    Then business profile should be created successfully

  @Completeflow
 Scenario: Successfully host Your business for Interior using Excel data
    When user clicks the "Profile Icon" button
    And user clicks the "Host Your Business" button
    And user clicks the "Interior" Profession
    And user proceeds to the Address Details page
    And user clicks on the interior next button
    And user enters Business and Basic Details using Excel row 2
    And user proceeds to the Address Details page
    And user enters Address, Qualification, and Success Story using Excel row 2
    And user proceeds to the Address Details page
    And user proceeds to the Category Selection page
    And user proceeds to the Category Selection page
    And user clicks the "Renovation & Remodeling" Business Category
    And user reviews the entered business details
    And user clicks the "Create Vendor Profile" button
    Then business profile should be created successfully