Feature: Student Management API Testing using CSV

  Scenario: Add students from CSV file
    Given I have student data from "editData.csv"
    When I send a POST request to add students
    Then The students should be added successfully

  Scenario: Get student details by ID from CSV file
    Given I have student IDs and details from "getCSVData.csv"
    When I send a GET request for each student by ID
    Then The response should match the student details

  Scenario: Edit student details by ID from CSV file
    Given I have student details from "editData.csv"
    When I send a delete request for each student
    Then The response should be same as the details i sent