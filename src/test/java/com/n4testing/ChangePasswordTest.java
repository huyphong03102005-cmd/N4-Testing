package com.n4testing;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ChangePasswordTest.TestResultLogger.class)
public class ChangePasswordTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:8085";

    public static class TestResultLogger implements TestWatcher {
        @Override
        public void testSuccessful(ExtensionContext context) {
            System.out.println("✅ [PASS] " + context.getDisplayName());
        }
        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            System.err.println("\n❌ [FAIL] " + context.getDisplayName());
            System.err.println("   -> Nguyên nhân: " + cause.getMessage());
        }
    }

    @BeforeEach
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        // Thay vì maximize() gây lỗi trên Chrome 147, ta đặt kích thước cố định
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(1440, 900));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        login();
        openPasswordPanel();
    }

    private void login() {
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("staff_003");
        driver.findElement(By.id("password")).sendKeys("pass_505");
        driver.findElement(By.className("btn-login")).click();
        
        // Đợi chuyển hướng và trang nạp xong
        wait.until(ExpectedConditions.urlContains("/tongquan"));
        try { Thread.sleep(1000); } catch (InterruptedException e) {} // Đợi session ổn định
    }

    private void openPasswordPanel() {
        // Mở profile
        WebElement userIcon = wait.until(ExpectedConditions.elementToBeClickable(By.className("fa-user")));
        userIcon.click();
        
        // Kiểm tra nếu có Alert "Vui lòng đăng nhập lại" xuất hiện
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String text = alert.getText();
            alert.accept();
            fail("LỖI SESSION: Hệ thống yêu cầu đăng nhập lại (" + text + "). Vui lòng restart server.");
        } catch (TimeoutException e) {
            // Không có alert, tiếp tục bình thường
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userInfoOverlay")));
        // Mở form đổi mật khẩu (TC-UP-19)
        driver.findElement(By.className("btn-edit-pw")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("changePasswordPanel")));
    }

    @Test
    @Order(1)
    @DisplayName("TC_UP_1: Kiểm tra các trường mật khẩu được ẩn")
    public void TC_UP_PasswordHidden() {
        assertEquals("password", driver.findElement(By.id("currentPassword")).getAttribute("type"));
        assertEquals("password", driver.findElement(By.id("newPassword")).getAttribute("type"));
        assertEquals("password", driver.findElement(By.id("confirmPassword")).getAttribute("type"));
    }

    @Test
    @Order(2)
    @DisplayName("TC_UP_2: Đổi mật khẩu thành công và Khôi phục")
    public void TC_UP_ChangePasswordSuccess() {
        // 1. Đổi từ pass_505 -> new_pass_123
        driver.findElement(By.id("currentPassword")).sendKeys("pass_505");
        driver.findElement(By.id("newPassword")).sendKeys("new_pass_123");
        driver.findElement(By.id("confirmPassword")).sendKeys("new_pass_123");
        driver.findElement(By.className("btn-pw-confirm")).click();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pwSuccessOverlay")));
        driver.findElement(By.className("btn-pw-done")).click();

        // 2. [QUAN TRỌNG] Đổi ngược lại từ new_pass_123 -> pass_505 để không làm hỏng các test sau
        driver.findElement(By.className("btn-edit-pw")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("changePasswordPanel")));
        
        driver.findElement(By.id("currentPassword")).sendKeys("new_pass_123");
        driver.findElement(By.id("newPassword")).sendKeys("pass_505");
        driver.findElement(By.id("confirmPassword")).sendKeys("pass_505");
        driver.findElement(By.className("btn-pw-confirm")).click();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pwSuccessOverlay")));
        driver.findElement(By.className("btn-pw-done")).click();
        
        assertFalse(driver.findElement(By.id("changePasswordPanel")).isDisplayed());
    }

    @Test
    @Order(3)
    @DisplayName("TC_UP_3: Đổi mật khẩu - Sai mật khẩu hiện tại")
    public void TC_UP_WrongCurrentPassword() {
        driver.findElement(By.id("currentPassword")).sendKeys("wrong_pass");
        driver.findElement(By.id("newPassword")).sendKeys("new_pass_123");
        driver.findElement(By.id("confirmPassword")).sendKeys("new_pass_123");
        
        driver.findElement(By.className("btn-pw-confirm")).click();
        
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertTrue(alert.getText().contains("không đúng") || alert.getText().contains("không chính xác"), "Thông báo lỗi sai mật khẩu hiện tại không đúng");
        alert.accept();
    }

    @Test
    @Order(4)
    @DisplayName("TC_UP_4: Đổi mật khẩu - Để trống các trường")
    public void TC_UP_EmptyPasswordFields() {
        // Để trống tất cả và nhấn xác nhận
        driver.findElement(By.className("btn-pw-confirm")).click();
        
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertTrue(alert.getText().contains("điền đầy đủ"), "Thông báo lỗi trống trường không đúng");
        alert.accept();
    }

    @Test
    @Order(5)
    @DisplayName("TC-UP-5: Đổi mật khẩu - Mật khẩu mới không khớp")
    public void TC_UP_PasswordNotMatch() {
        driver.findElement(By.id("currentPassword")).sendKeys("pass_505");
        driver.findElement(By.id("newPassword")).sendKeys("new_pass_123");
        driver.findElement(By.id("confirmPassword")).sendKeys("different_pass");
        
        driver.findElement(By.className("btn-pw-confirm")).click();
        
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Mật khẩu xác nhận không khớp!", alert.getText());
        alert.accept();
    }

    @Test
    @Order(6)
    @DisplayName("TC-UP-6: Hủy form đổi mật khẩu")
    public void TC_UP_CancelPassword() {
        driver.findElement(By.id("currentPassword")).sendKeys("any_data");
        driver.findElement(By.className("btn-pw-cancel")).click();
        
        // Kiểm tra panel đã ẩn
        assertFalse(driver.findElement(By.id("changePasswordPanel")).isDisplayed());
    }

    @AfterEach
    public void teardown() {
        if (driver != null) driver.quit();
    }
}
