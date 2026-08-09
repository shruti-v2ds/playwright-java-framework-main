Feature: Register User Business Hosting - Data-Driven Multi-User & Multi-Business

  As a registered user
  I want to host multiple businesses on the DialinArch platform
  So that I can manage multiple business profiles in a single session

  Background:
    Given user is on dialinarch landing page

  @DataDriven @MultiUser @MultiBusinessPerUser
  Scenario: Host multiple businesses for multiple users - Data-Driven
    
    # This scenario will be executed once per Excel data row in HostYourBusiness sheet
    # Each row contains: UserID, Mobile, Profession, BusinessName, AboutBusiness, 
    #                    Address, Pincode, Qualification, Experience, SuccessStory, 
    #                    Category, SubCategory, ProjectDone
    
    # The step definitions will:
    # 1. Read all users from Excel (grouped by UserID)
    # 2. For each user, create a new session (if first business) or reuse (if subsequent)
    # 3. Log in with Mobile from Excel
    # 4. For each business of that user:
    #    - Navigate to Host Your Business
    #    - Select profession from Excel data
    #    - Enter business details from Excel
    #    - Complete the flow
    #    - Navigate back to dashboard
    # 5. After all businesses for user are created, move to next user
    # 6. Each user gets isolated browser session
    
    When data-driven test reads all users from Excel
    And for each user:
      | action                          |
      | Create new user session         |
      | Log in with mobile from Excel   |
      | Create all businesses for user  |
      | Log out and close session       |
    Then all businesses should be created successfully
    And each user should have isolated session
    And no data should leak between users

  @DataDriven @SingleUserMultiBusinessInSession
  Scenario: Host multiple businesses in single user session - Data-Driven
    
    # This scenario demonstrates one user creating multiple businesses
    # The same user session is reused for all businesses
    # When user navigates back to dashboard, they stay logged in
    
    # Execution flow:
    # 1. Read all rows from Excel for the test user
    # 2. Log in once with mobile number
    # 3. Create first business with first profession
    # 4. Navigate back to dashboard (session continues)
    # 5. Create second business with second profession
    # 6. Continue until all businesses created
    # 7. Final logout
    
    When data-driven test reads businesses for a single user from Excel
    And user logs in once with the mobile number
    And for each business in user's data:
      | step                                                |
      | Navigate to Host Your Business                      |
      | Select profession from business data row           |
      | Enter business details from data row               |
      | Complete the business creation flow                |
      | Navigate back to dashboard (session continues)    |
    Then all businesses should be created in single session
    And user should still be logged in after each business
    And session should not be reused across different users

  @DataDriven @SpecificUserMultipleBusiness
  Scenario: Host businesses for a specific user - Data-Driven
    
    # This scenario allows testing a specific user with all their businesses
    # Useful for debugging or testing a particular user's data set
    
    # Example: Test USER001 with all their businesses
    When test retrieves all businesses for user "USER001"
    And user logs in with USER001 mobile number from Excel
    Then user should be able to create all USER001 businesses in sequence
    And each business creation should succeed
    And user session should be maintained throughout

  @DataDriven @FirstBusinessOnly
  Scenario: Host first business for each user - Data-Driven Subset
    
    # This scenario creates only the first business for each user
    # Useful for quick validation or initial setup
    
    When test retrieves first business for each unique user
    And for each user and their first business:
      | action                                    |
      | Create user session                       |
      | Log in with mobile                        |
      | Create business with profession data      |
      | Verify business created                   |
      | Close user session                        |
    Then all first businesses should be created
    And test should complete in minimum time

  @DataDriven @ExcelValidation
  Scenario: Validate Excel test data structure - Data-Driven
    
    # This scenario validates the Excel file structure before running tests
    # Checks for required columns: UserID, Mobile, Profession, BusinessName, etc.
    
    When test validates Excel file structure
    Then Excel should have columns: UserID, Mobile, Profession, BusinessName
    And Excel should have data rows
    And all required fields should be populated
    And no UserID or Mobile should be empty
