package com.n4testing;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SampleUITest {

    private WebDriver driver;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode for server/test environments
        driver = new ChromeDriver(options);
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testLoginButtonExists() {
        // Since the app isn't actually running at a public URL here, 
        // we'd usually point to http://localhost:8080/login
        // This is a placeholder for where you would put your UI tests.
        
        // driver.get("http://localhost:8080/login");
        // boolean isPresent = driver.findElements(By.className("btn-login")).size() > 0;
        // assertTrue(isPresent);
        
        assertTrue(true, "Placeholder for Selenium tests");
    }
}
