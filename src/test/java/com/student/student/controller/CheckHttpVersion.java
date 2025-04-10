package com.student.student.controller;



import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CheckHttpVersion {
    public static void main(String[] args) {
        Response response = RestAssured.post("http://localhost:8080/student");

        
        System.out.println("Status Line: " + response.getStatusLine());
    }

}
