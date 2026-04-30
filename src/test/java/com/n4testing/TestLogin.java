package com.n4testing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TestLogin {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        
        // Tránh tình trạng treo vĩnh viễn ở driver.get()
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
        
        // Đợi tối đa 10 giây nếu không thấy element
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void testSuccessfulLogin() {
        driver.get("http://localhost:8085/login");

        // Đợi cho đến khi ô username xuất hiện rồi mới nhập
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameField.sendKeys("admin_hotel");

        // Nhập password
        driver.findElement(By.id("password")).sendKeys("pass_123");

        // Click nút Đăng nhập
        driver.findElement(By.className("btn-login")).click();

        // Đợi cho đến khi URL chuyển sang trang /tongquan
        wait.until(ExpectedConditions.urlContains("/tongquan"));

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("/tongquan"), "Lỗi: Không chuyển hướng đến trang tổng quan!");
    }

    @Test
    public void testLoginAndNavigatePages() {
        driver.get("http://localhost:8085/login");

        // Đăng nhập
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameField.sendKeys("admin_hotel");
        driver.findElement(By.id("password")).sendKeys("pass_123");
        driver.findElement(By.className("btn-login")).click();

        // Chờ đăng nhập thành công
        wait.until(ExpectedConditions.urlContains("/tongquan"));

        // Danh sách các trang cần chuyển hướng tới
        String[] pages = {
                "http://localhost:8085/dat-phong",
                "http://localhost:8085/ql-datphong",
                "http://localhost:8085/nhan-phong",
                "http://localhost:8085/tra-phong",
                "http://localhost:8085/service"
        };

        for (String pageUrl : pages) {
            driver.get(pageUrl);
            
            String expectedPath = pageUrl.replace("http://localhost:8085", "");
            wait.until(ExpectedConditions.urlContains(expectedPath));
            
            Assertions.assertTrue(driver.getCurrentUrl().contains(expectedPath), 
                    "Lỗi: Không thể truy cập trang hoặc bị chuyển hướng khỏi " + pageUrl);
        }
    }

    @Test
    public void testFailedLogin() {
        driver.get("http://localhost:8085/login");

        // Nhập thông tin sai
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("user_sai");
        driver.findElement(By.id("password")).sendKeys("sai_mat_khau");
        driver.findElement(By.className("btn-login")).click();

        // Đợi thông báo lỗi xuất hiện (Dựa trên ảnh của bạn: màu đỏ #de5e5e)
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'không đúng') or contains(@style, '#de5e5e')]")
        ));

        Assertions.assertTrue(errorMsg.isDisplayed(), "Lỗi: Không thấy thông báo lỗi khi đăng nhập sai!");
    }

    @Test
    public void testEmptyUsername() {
        driver.get("http://localhost:8085/login");

        // Bỏ trống username, chỉ điền password
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password"))).sendKeys("pass_123");

        // Click nút Đăng nhập
        driver.findElement(By.className("btn-login")).click();

        // Lấy thông báo lỗi mặc định của HTML5 ("Vui lòng điền vào trường này")
        WebElement usernameField = driver.findElement(By.id("username"));
        String validationMsg = usernameField.getAttribute("validationMessage");

        Assertions.assertFalse(validationMsg.isEmpty(), "Lỗi: Không có cảnh báo yêu cầu nhập username!");
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"), "Form không được submit khi thiếu username");
    }

    @Test
    public void testEmptyPassword() {
        driver.get("http://localhost:8085/login");

        // Điền username, bỏ trống password
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("admin_hotel");

        // Click nút Đăng nhập
        driver.findElement(By.className("btn-login")).click();

        // Lấy thông báo lỗi của HTML5
        WebElement passwordField = driver.findElement(By.id("password"));
        String validationMsg = passwordField.getAttribute("validationMessage");

        Assertions.assertFalse(validationMsg.isEmpty(), "Lỗi: Không có cảnh báo yêu cầu nhập password!");
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"), "Form không được submit khi thiếu password");
    }

    @Test
    public void testEmptyBoth() {
        driver.get("http://localhost:8085/login");

        // Không điền cả 2 ô, click ngay nút Đăng nhập
        wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-login"))).click();

        // Trình duyệt sẽ ưu tiên báo lỗi ở ô đầu tiên bị trống (username)
        WebElement usernameField = driver.findElement(By.id("username"));
        String validationMsg = usernameField.getAttribute("validationMessage");

        Assertions.assertFalse(validationMsg.isEmpty(), "Lỗi: Không có cảnh báo khi để trống cả 2 ô!");
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"), "Form không được submit khi thiếu thông tin");
    }

    @AfterEach
    public void teardown() {
        // Đóng comment dòng bên dưới để giữ màn hình không bị tắt tự động
        // if (driver != null) {
        //     driver.quit();
        // }
    }
}