Feature: Home page and architect search

  @home
  Scenario: User can view landing page
    Given user is on dialinarch landing page
    Then the landing page should be displayed correctly

  @search
  Scenario: User can search for architects
    Given user is on dialinarch landing page
    When user searches for architects in "New York"
    Then search results should be displayed
