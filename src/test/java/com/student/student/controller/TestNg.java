package com.student.student.controller;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class TestNg {

    @Test(dataProvider = "CSVTestData")
    public void DataDrivenTesting(Map<String, String> testData) {
        System.out.println("Testing with Data: " + testData);
    }

    @DataProvider(name = "CSVTestData")
    public Object[][] getTestData() {
        List<Map<String, String>> testDataList = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader("src/test/java/com/student/student/resources/testData.csv"))) {
            List<String[]> allData = csvReader.readAll();

            for (String[] line : allData) {
                if (line.length < 4) continue; // Skip invalid lines

                Map<String, String> map = new HashMap<>();
                map.put("name", line[0]);
                map.put("department", line[1]);
                map.put("email", line[2]);
                map.put("phoneNumber", line[3]);

                testDataList.add(map);
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        // Convert List to Object[][] for TestNG
        Object[][] objArray = new Object[testDataList.size()][1];
        for (int i = 0; i < testDataList.size(); i++) {
            objArray[i][0] = testDataList.get(i);
        }

        return objArray;
    }
}
