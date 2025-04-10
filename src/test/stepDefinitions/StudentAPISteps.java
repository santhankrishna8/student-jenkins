package stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.student.student.model.Student;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class StudentAPISteps {
    private static final String BASE_URL = "http://localhost:8080";
    private List<String[]> studentData;
    private Response response;

    @Given("I have student data from {string}")
    public void iHaveStudentDataFromCSV(String filePath) throws IOException, CsvException {
        File file = new File("src/test/resources/" + filePath);
    if (!file.exists()) {
        throw new IOException("File not found: " + file.getAbsolutePath());
    }
    CSVReader reader = new CSVReader(new FileReader(file));
    studentData = reader.readAll();
    reader.close();
    }

    @When("I send a POST request to add students")
    public void iSendPostRequest() {
        for (int i = 1; i < studentData.size(); i++) {
            String[] row = studentData.get(i);
            Student student = new Student(row[3], row[1], row[2], row[4]);

            response = RestAssured
                .given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .body(student)
                .when()
                    .post("/student");
        }
    }

    @Then("The students should be added successfully")
    public void theStudentsShouldBeAddedSuccessfully() {
        response.then().statusCode(201);
    }

    @Given("I have student IDs and details from {string}")
    public void iHaveStudentIDsAndDetails(String filePath) throws IOException, CsvException {
        File file = new File("src/test/resources/" + filePath);
    if (!file.exists()) {
        throw new IOException("File not found: " + file.getAbsolutePath());
    }
    CSVReader reader = new CSVReader(new FileReader(file));
    studentData = reader.readAll();
    reader.close();
    }

    @When("I send a GET request for each student by ID")
    public void iSendGetRequestById() {
        for (int i = 1; i < studentData.size(); i++) {
            String[] row = studentData.get(i);
            Long id = Long.parseLong(row[0]);
            Student student = new Student(row[3], row[1], row[2], row[4]);

            response = RestAssured
                .given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .pathParam("id", id)
                .when()
                    .get("/student/{id}");

            response.then()
                .statusCode(200)
                .body("name", equalTo(student.getName()))
                .body("department", equalTo(student.getDepartment()))
                .body("email", equalTo(student.getEmail()))
                .body("phoneNumber", equalTo(student.getPhoneNumber()));
        }
    }
    @Then("The response should match the student details")
    public void the_response_should_match_the_student_details() {
        for (int i = 1; i < studentData.size(); i++) {
            String[] row = studentData.get(i);
            Long id = Long.parseLong(row[0]);
            Student expectedStudent = new Student(row[3], row[1], row[2], row[4]);

            response = RestAssured
                .given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .pathParam("id", id)
                .when()
                    .get("/student/{id}");

            response.then()
                .statusCode(200)
                .body("name", equalTo(expectedStudent.getName()))
                .body("department", equalTo(expectedStudent.getDepartment()))
                .body("email", equalTo(expectedStudent.getEmail()))
                .body("phoneNumber", equalTo(expectedStudent.getPhoneNumber()));
        }
    }

    List<String[]> list;
    @Given("I have student details from {string}")
public void i_have_student_details_from(String filePath)throws IOException, CsvException {
    File file = new File("src/test/resources/" + filePath);
    if (!file.exists()) {
        throw new IOException("File not found: " + file.getAbsolutePath());
    }
    CSVReader reader = new CSVReader(new FileReader(file));
    List<String[]> list = reader.readAll();
    reader.close();
}
@When("I send a delete request for each student")
public void i_send_a_delete_request_for_each_student() {
    for (int i = 1; i < list.size(); i++) {
        String[] row = list.get(i);
        Long id = Long.parseLong(row[0]);
        int status = given()
                .baseUri(BASE_URL)
                .pathParam("id", id)
        .when()
                .delete("/student/{id}")
        .then()
                .extract().statusCode();
    }
   
}
@Then("The response should be same as the details i sent")
public void the_response_should_be_same_as_the_details_i_sent() {
    for (int i = 1; i < list.size(); i++) {
        String[] row = list.get(i);
        Long id = Long.parseLong(row[0]);
        RestAssured.given()
                .baseUri(BASE_URL)
                .pathParam("id", id)
        .when()
                .delete("/student/{id}")
        .then()
                .statusCode(200);
    }
}


}
