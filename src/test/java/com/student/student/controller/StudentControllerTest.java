package com.student.student.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.student.student.model.Student;
import com.student.student.repository.StudentRepo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.responseSpecification;
import static io.restassured.RestAssured.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class StudentControllerTest {

    String baseURI = "http://localhost:8080";

    @Autowired
    private StudentRepo studentRepo;
    private static int passedCount = 0;
    private static int failedCount = 0;
    static ExtentReports extent;
    static ExtentTest test;

    @BeforeAll
    public void setUpExtent() {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
    }

    @AfterAll
    public void tearDownExtent() {
        extent.flush();  
    }

    //Posting data manually.
    @Test
    public void testAddStudent(){

        RestAssured.baseURI = "http://localhost:8080";

        String studentJson = """
                    {
                        "name":"Krishna",
                        "department":"ece",
                        "email":"santhan@gmail.com",
                        "phoneNumber":"6304349076"
                    }
                """;

        given()
        .contentType(ContentType.JSON)
        .body(studentJson)
        .when()
        .post("/student")
        .then()
        .statusCode(201)
        .contentType(ContentType.JSON)
        .body("name",equalTo("Krishna"))
        .body("department",equalTo("ece"))
        .body("email", equalTo("santhan@gmail.com"))
        .body("phoneNumber", equalTo("6304349076"));
        
    }
    

    //get all students
    @Test
    public void getAllStudents(){
        RestAssured
        .given()
        .contentType(ContentType.JSON)
        .baseUri("http://localhost:8080")
        .when()
        .get("/student")
        .then()
        .statusCode(200)
        .statusLine("HTTP/1.1 200 ");
    }

    //getting a single student manually by sending id
    @Test
    public void getStudentById(){
        RestAssured
        .given()
            .contentType(ContentType.JSON)
            .baseUri("http://localhost:8080/student")
            .pathParam("id", 291)
        .when()
            .get("{id}")
        .then()
            .assertThat()
            .statusCode(200)
            .body("name", equalTo("Santhan"));
        
    }
    

    //used wrong path for getting all students 
    @Test
    public void getAllWithWrongPath(){
        RestAssured
        .given()
        .contentType(ContentType.JSON)
        .baseUri("http://localhost:8080")
        .when()
        .get("/students")
        .then()
        .statusCode(404);

    }


    // posting data manually with invalid JSON
    @Test
    public void postWithNotValidJSON(){

        String studentJson = """
                    {
                        "name":"Krishna",
                        "department":"ece",
                        "email":"santhan@gmail.com",
                        "phoneNumber":"900000235"
                    }
                """;
        
        RestAssured
        .given()
           .contentType(ContentType.JSON)
           .body(studentJson)
           .baseUri("http://localhost:8080")
        .when()
           .post("/student")
        .then()
           .statusCode(500);


    }






    // get all students and then store in a list and tested posting them
    @Test
    public void AutomateFetchAndPostStudent(){
        RestAssured.baseURI = "http://localhost:8080";

        List<Student> students = given()
                                    .contentType(ContentType.JSON)
                                .when()
                                    .get("/student")
                                .then()
                                    .statusCode(200)
                                    .extract().body().jsonPath().getList("$", Student.class);

        for(Student student:students){
            given()
                .contentType(ContentType.JSON)
                .body(student)
            .when()
                .post("/student")
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("name", equalTo(student.getName()))
                .body("department", equalTo(student.getDepartment()))
                .body("email", equalTo(student.getEmail()))
                .body("phoneNumber", equalTo(student.getPhoneNumber()));

            System.out.println("successfully added"+student.getName());
                
        }
    }
 

    //got all the students and stored in list and posted each student to assert the data
    @Test
    public void automateGetById(){
        RestAssured.baseURI = "http://localhost:8080";

        List<Student> students = given()
                                    .contentType(ContentType.JSON)
                                .when()
                                    .get("/student")
                                .then()
                                    .statusCode(200)
                                    .extract().body().jsonPath().getList("$",Student.class);
        
        for(Student student:students){
            given()
                .contentType(ContentType.JSON)
                .body(student)
                .pathParam("id", student.getId())
            .when()
                .get("/student/{id}")
            .then()
                .statusCode(200)
                .body("name", equalTo(student.getName()))
                .body("department", equalTo(student.getDepartment()))
                .body("email", equalTo(student.getEmail()))
                .body("phoneNumber", equalTo(student.getPhoneNumber())) ;
        }
    }


   




    //Extracted data from CSV file and used it for posting students
    private static final String BASE_URL = "http://localhost:8080"; 

    @Test
    @DisplayName("Test Case: Add Student through CSV")
    public void testPostStudentUsingCSV() throws IOException, CsvException {
    CSVReader reader = new CSVReader(new FileReader("src/test/resources/editData.csv"));
    List<String[]> allData = reader.readAll();
    reader.close();

    for (int i = 1; i < allData.size(); i++) { 
        String[] row = allData.get(i);

        Student student = new Student();
        student.setName(row[3]); 
        student.setDepartment(row[1]);
        student.setEmail(row[2]);
        student.setPhoneNumber(row[4]);

        RestAssured
            .given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(student)
            .when()
                .post("/student")
            .then()
                .statusCode(201);
    }
}


    //Extracted data from CSV files and passed them through get By id  
    @Test
    @DisplayName("Test Case: GET Student By Id CSV")
    public void getAllStudentsUsingCSV() throws IOException, CsvException {

    CSVReader reader = new CSVReader(new FileReader("src/test/resources/getCSVData.csv"));
    List<String[]> allData = reader.readAll();
    reader.close(); 

    for (int i = 1; i < allData.size(); i++) { 
        String[] row = allData.get(i);

        Student student = new Student();
        student.setName(row[3]); 
        student.setDepartment(row[1]);
        student.setEmail(row[2]);
        student.setPhoneNumber(row[4]);
        Long id = Long.parseLong(row[0]);

        RestAssured
            .given()
                .baseUri("http://localhost:8080/student")
                .contentType(ContentType.JSON)
                .pathParam("id",id )
            .when()
                .get("/{id}")
            .then()
                .statusCode(200)
                .body("name",equalTo(student.getName()))
                .body("department",equalTo(student.getDepartment())) 
                .body("email",equalTo(student.getEmail()))
                    .body("phoneNumber",equalTo(student.getPhoneNumber()));           
        }
    }


    @Test
    @DisplayName("Test Case: Edit Student By CSV data")
    public void automateEditUsingCSVData() throws IOException, CsvException{
        CSVReader reader = new CSVReader(new FileReader("src/test/resources/editData.csv"));
        List<String[]> list = reader.readAll();
        reader.close();


        for(int i=1;i<list.size();i++){
            String[] row = list.get(i);
            
            Long id = Long.parseLong(row[0]);
            Student student = studentRepo.findById(id).orElse(null);

            
            student.setName(row[3]); 
            student.setDepartment(row[1]);
            student.setEmail(row[2]);
            student.setPhoneNumber(row[4]);

            studentRepo.save(student);
            System.out.println("edited student: "+student);
            RestAssured
            .given()
                .baseUri("http://localhost:8080/student")
                .contentType(ContentType.JSON)
                .pathParam("id",id )
            .when()
                .get("/{id}")
            .then()
                .statusCode(200)
                .body("name",equalTo(row[3]))
                .body("department",equalTo(row[1])) 
                .body("email",equalTo(row[2]))
                        .body("phoneNumber",equalTo(row[4]));           
            }
        }
    
//        @Test
//@DisplayName("Test Case: Delete Student by ID - CSV Data")
//public void automateDeleteUsingCSVData() throws IOException, CsvException {
//    test = extent.createTest("Delete Student by ID");
//    boolean hasFailures = false; 
//    
//    CSVReader reader = new CSVReader(new FileReader("src/test/resources/deleteData.csv"));
//    List<String[]> list = reader.readAll();
//    reader.close();
//
//    for (int i = 1; i < list.size(); i++) {
//        String[] row = list.get(i);
//        Long id = Long.parseLong(row[0]);
//
//        test.info("Attempting to delete student with ID: " + id);
//
//        int statusCode = given()
//                .baseUri(BASE_URL)
//                .pathParam("id", id)
//        .when()
//                .delete("/student/{id}")
//        .then()
//                .extract().statusCode();
//
//        if (statusCode == 200) {
//            test.pass("✅ Student with ID " + id + " deleted successfully.");
//            passedCount++;
//        } else {
//            test.fail("❌ Student with ID " + id + " not found. Status: " + statusCode);
//            failedCount++;
//            hasFailures = true; 
//        }
//    }
//
//    if (hasFailures) {
//        Assertions.fail("❌ One or more delete operations failed. Check Extent Report.");
//    }
//}

       
    

// @Test
// public void testPutData() throws IOException, CsvException{

//     test = extent.createTest("Put Transfer");

//     CSVReader reader = new CSVReader(new FileReader("src/test/java/com/student/student/resources/generated_data.csv"));
//     List<String[]> allData = reader.readAll();
//     reader.close();

//     for (int i = 1; i < allData.size(); i++) { 
//         String[] row = allData.get(i);
//         Long id1 = Long.parseLong(row[0]);
//         Long id2 = Long.parseLong(row[1]);
//         Long id3 = Long.parseLong(row[2]);
//         Transfer transfer = new Transfer(id1,id2,id3);

//         RestAssured
//         .given()
//             .baseUri("http://192.168.31.198:3100")
//             .contentType(ContentType.JSON)
//             .body(transfer)
//         .when()
//             .put("/transfer")
//         .then()
//             .statusCode(200);
          





//     }


}
 
    
    
    

