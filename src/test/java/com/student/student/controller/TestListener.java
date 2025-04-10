package com.student.student.controller;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import java.io.FileWriter;
import java.io.IOException;

public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("❌ Test Failed: " + result.getName());
        
        // Capture failure details
        try (FileWriter writer = new FileWriter("failed_tests.csv", true)) {
            writer.append(result.getName()).append(",FAILED\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("✅ Test Passed: " + result.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("Total Passed: " + context.getPassedTests().size());
        System.out.println("Total Failed: " + context.getFailedTests().size());
        System.out.println("Total Skipped: " + context.getSkippedTests().size());
    }
}
