Feature: Register User Business Hosting

  As a registered user
  I want to host my business on the DialinArch platform
  So that I can create my business profile and use the platform

  Background:
     Given user is on dialinarch landing page
    When user clicks on Login Signup button
    And user fills registration form with data from Excel row 1
    And user clicks Create Profile button
    And user should enter the OTP
  
  @hostingBusiness
  @NeedsCleanup
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

    # ===== Skill Service Registration Scenarios =====

@skillservice
  Scenario: User registers as Skill Service Provider - Plumber
    When user clicks the "Profile Icon" button
    And user clicks the "Host Your Business" button
    And user fills registration form with skill service data from Excel row 1
      | FullName | Email              | Phone      | ServiceType | Description                          | Experience |
      | John Doe | john@example.com   | 9876543210 | Plumber     | Experienced plumber with 10+ years   | 10         |
    And user clicks Create Profile button
    And user should enter the OTP
    Then registration should be successful

  @registration @skillservice
  Scenario: User registers as Skill Service Provider - Electrician
    When user clicks on Login Signup button
    And user fills registration form with skill service data from Excel row 2
      | FullName       | Email               | Phone      | ServiceType | Description                          | Experience |
      | Alice Johnson  | alice@example.com   | 9876543211 | Electrician | Licensed electrician, commercial exp | 12         |
    And user clicks Create Profile button
    And user should enter the OTP
    Then registration should be successful

  @registration @skillservice
  Scenario: User registers as Skill Service Provider - Carpenter
    When user clicks on Login Signup button
    And user fills registration form with skill service data from Excel row 3
      | FullName   | Email             | Phone      | ServiceType | Description                       | Experience |
      | Bob Smith  | bob@example.com   | 9876543212 | Carpenter   | Skilled carpenter, custom work    | 8          |
    And user clicks Create Profile button
    And user should enter the OTP
    Then registration should be successful

  @registration @skillservice
  Scenario Outline: Register multiple skill service providers with different service types
    When user clicks on Login Signup button
    And user fills basic registration information with "<fullName>", "<email>", and "<phone>"
    And user selects service type as "<serviceType>"
    And user fills service description as "<description>"
    And user fills years of experience as "<experience>"
    And user clicks Create Profile button
    And user should enter the OTP
    Then registration should be successful

    Examples:
      | fullName      | email                    | phone      | serviceType  | description                        | experience |
      | Carol White   | carol@example.com        | 9876543213 | Painter      | Professional painter               | 6          |
      | David Brown   | david@example.com        | 9876543214 | HVAC Tech    | HVAC maintenance and installation  | 7          |
      | Emma Davis    | emma@example.com         | 9876543215 | Plumber      | Expert plumber                     | 15         |

  @registration @skillservice
  Scenario: Skill Service Provider registration with all optional fields
    When user clicks on Login Signup button
    And user fills basic registration information with "Frank Miller", "frank@example.com", and "9876543216"
    And user selects service type as "General Contractor"
    And user fills service description as "General contracting with 20+ years experience in residential and commercial projects"
    And user fills years of experience as "20"
    And user fills address details with "100 Market Street", "San Francisco", "CA", "94102"
    And user clicks Create Profile button
    And user should enter the OTP
    Then registration should be successful
