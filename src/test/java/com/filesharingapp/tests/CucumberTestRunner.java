package com.filesharingapp.tests;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * CucumberTestRunner
 * ------------------
 * Hooks Cucumber features into TestNG.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.filesharingapp.tests",
        plugin = {"pretty", "html:target/cucumber-report.html"}
)
public class CucumberTestRunner extends AbstractTestNGCucumberTests {
}
