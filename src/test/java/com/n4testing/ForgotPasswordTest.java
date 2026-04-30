package com.n4testing;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ForgotPasswordTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String baseUrl = "http://localhost:8085";

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless"); // Uncomment for headless execution
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test navigating to Forgot Password page")
    void testNavigateToForgotPassword() {
        driver.get(baseUrl + "/login");

        WebElement forgotPwLink = driver.findElement(By.className("forgot-pw"));
        forgotPwLink.click();

        wait.until(ExpectedConditions.urlContains("/forgot-password"));
        String title = driver.findElement(By.className("login-title")).getText();
        assertEquals("Quên mật khẩu", title);
    }

    @Test
    @Order(2)
    @DisplayName("Test submitting non-existent email")
    void testNonExistentEmail() {
        driver.get(baseUrl + "/forgot-password");

        WebElement emailInput = driver.findElement(By.id("email"));
        emailInput.sendKeys("nonexistent@example.com");

        WebElement submitBtn = driver.findElement(By.className("btn-submit"));
        submitBtn.click();

        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("error-msg")));
        assertTrue(errorMsg.getText().contains("Email không tồn tại"));
    }

    @Test
    @Order(3)
    @DisplayName("Test submitting valid email and reaching OTP page")
    void testValidEmailSubmission() {
        driver.get(baseUrl + "/forgot-password");

        // Use a known email in the system. From Application.java: admin@n4hotel.com
        WebElement emailInput = driver.findElement(By.id("email"));
        emailInput.sendKeys("test@gmail.com");

        WebElement submitBtn = driver.findElement(By.className("btn-submit"));
        submitBtn.click();

        // Wait for redirection to verify-otp
        wait.until(ExpectedConditions.urlContains("/verify-otp"));

        String title = driver.findElement(By.className("login-title")).getText();
        assertEquals("Nhập mã OTP", title);

        WebElement emailDisplay = driver.findElement(By.tagName("strong"));
        assertEquals("test@gmail.com", emailDisplay.getText());
    }

    @Test
    @Order(4)
    @DisplayName("Test submitting incorrect OTP")
    void testIncorrectOtp() {
        // We first need to reach the OTP page
        driver.get(baseUrl + "/forgot-password");
        driver.findElement(By.id("email")).sendKeys("test@gmail.com");
        driver.findElement(By.className("btn-submit")).click();

        wait.until(ExpectedConditions.urlContains("/verify-otp"));

        // Enter 6 wrong digits
        List<WebElement> otpInputs = driver.findElements(By.className("otp-input"));
        for (WebElement input : otpInputs) {
            input.sendKeys("0");
        }

        driver.findElement(By.className("btn-submit")).click();

        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("error-msg")));
        assertTrue(errorMsg.getText().contains("Mã OTP không chính xác"));
    }

    @Test
    @Order(5)
    @DisplayName("Test full password reset success flow")
    void testFullPasswordResetSuccess() throws java.io.IOException, InterruptedException {
        driver.get(baseUrl + "/forgot-password");

        // Bước 1: Nhập email
        driver.findElement(By.id("email")).sendKeys("test@gmail.com");
        driver.findElement(By.className("btn-submit")).click();

        wait.until(ExpectedConditions.urlContains("/verify-otp"));

        // Bước 2: Đọc mã OTP từ file (dùng đường dẫn tuyệt đối)
        String path = System.getProperty("user.dir") + "/otp.txt";
        java.io.File file = new java.io.File(path);
        String otp = "";
        for (int i = 0; i < 10; i++) {
            if (file.exists() && file.length() >= 6) {
                otp = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path))).trim();
                if (otp.length() == 6) break;
            }
            Thread.sleep(500);
        }
        System.out.println("DEBUG: OTP đọc được là [" + otp + "]");

        // Điền OTP vào các ô input (hệ thống có 6 ô)
        List<WebElement> otpInputs = driver.findElements(By.className("otp-input"));
        for (int i = 0; i < 6; i++) {
            otpInputs.get(i).sendKeys(String.valueOf(otp.charAt(i)));
        }

        driver.findElement(By.className("btn-submit")).click();

        wait.until(ExpectedConditions.urlContains("/reset-password"));

        // Bước 3: Đặt mật khẩu mới
        driver.findElement(By.id("password")).sendKeys("123123");
        driver.findElement(By.id("confirm-password")).sendKeys("123123");
        driver.findElement(By.className("btn-submit")).click();

        // Kiểm tra xem có quay về trang đăng nhập và báo thành công không
        try {
            wait.until(ExpectedConditions.urlContains("/login"));
            
            // Tìm thông báo thành công bằng class name (Cực kỳ ổn định)
            WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("success-msg")
            ));
            
            // Kiểm tra chính xác tuyệt đối chữ hoa, chữ thường và dấu câu
            assertEquals("Đặt lại mật khẩu thành công!", successMsg.getText().trim());
        } catch (TimeoutException e) {
            System.out.println("❌ KHÔNG TÌM THẤY THÔNG BÁO THÀNH CÔNG. URL HIỆN TẠI: " + driver.getCurrentUrl());
            throw e;
        }
    }

    @Test
    @Order(6)
    @DisplayName("Test back to login link")
    void testBackToLoginLink() {
        driver.get(baseUrl + "/forgot-password");

        WebElement backLink = driver.findElement(By.className("back-to-login"));
        backLink.click();

        wait.until(ExpectedConditions.urlContains("/login"));
        String title = driver.findElement(By.className("login-title")).getText();
        assertEquals("Đăng nhập", title);
    }
}
