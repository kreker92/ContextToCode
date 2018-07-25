@Request
Feature: Request
 
  Scenario: User logs in to profile
    Given I am on the "Company home" page on URL "www.mycomany.com"
    When I fill in "Username" with "Test"
    And I fill in "Password" with "123"
    And I click on the "Log In" button
    Then I am on the "My profile" page on URL "www.mycompany.com/myprofile"
    And I should see "Welcome to your profile" message
    And I should see the "Log out" button
    When I click on the "Edit Personal Info" button
    And I fill in "Your name" with "Thilina Ashen Gamage"
    And I click on the "Save Changes" button
    Then I should see "Your personal info has been successfully updated" message
