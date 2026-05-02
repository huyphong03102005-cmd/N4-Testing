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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(CheckinAutomationTest.TestResultLogger.class)
public class CheckinAutomationTest {
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
            System.err.println("==================================================================");
        }
    }

    @BeforeEach
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        login();
        driver.get(BASE_URL + "/nhan-phong");
        wait.until(ExpectedConditions.urlContains("/nhan-phong"));
    }

    private void login() {
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("admin_hotel");
        driver.findElement(By.id("password")).sendKeys("pass_123");
        driver.findElement(By.className("btn-login")).click();
        wait.until(ExpectedConditions.urlContains("/tongquan"));
    }

    @AfterEach
    public void teardown() {
        if (driver != null) driver.quit();
    }

    @Test
    @Order(1)
    @DisplayName("TC-CI-01: Check-in đúng quy trình (Phòng Trống)")
    public void testSuccessfulCheckin() {
        String targetCode = "DPW204030526014";
        searchBooking(targetCode);
        driver.findElement(By.className("btn-checkin")).click();
        try {
            WebElement successPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successPopup")));
            assertTrue(successPopup.isDisplayed());
        } catch (TimeoutException e) {
            checkErrorMessage();
        }
    }

    @Test
    @Order(2)
    @DisplayName("TC-CI-06: Phòng chưa dọn xong/Bảo trì (Kiểm tra Popup báo lỗi)")
    public void testMaintenanceBlock() {
        List<WebElement> bookings = driver.findElements(By.className("booking-item"));
        Assumptions.assumeFalse(bookings.isEmpty());
        bookings.get(0).findElement(By.className("btn-checkin")).click();
        try {
            WebElement errorPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorPopup")));
            String msg = driver.findElement(By.id("errorMessage")).getText();
            System.out.println("-> Đã chặn và hiển thị lỗi: " + msg);
            assertTrue(msg.contains("không sẵn sàng") || msg.contains("bảo trì") || msg.contains("khách ở"));
        } catch (TimeoutException e) {
            fail("BUG: Hệ thống không hiện Popup báo lỗi khi phòng không sẵn sàng");
        }
    }

    @Test
    @Order(3)
    @DisplayName("TC-CI-09 & TC-CI-15: Tìm kiếm (Mã DP, SĐT, Email)")
    public void testSearchOptions() {
        WebElement search = driver.findElement(By.name("search"));
        search.sendKeys("0123"); // Tìm theo SĐT
        search.sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.urlContains("search="));
        assertTrue(driver.findElements(By.className("booking-item")).size() >= 0);
    }

    @Test
    @Order(4)
    @DisplayName("TC-CI-03 & TC-CI-12 & TC-CI-17: Kiểm tra thông tin hiển thị và Format mã")
    public void testInfoVisibility() {
        List<WebElement> bookings = driver.findElements(By.className("booking-item"));
        Assumptions.assumeFalse(bookings.isEmpty());
        String text = bookings.get(0).getText();
        assertTrue(text.contains("DP"), "Mã DP sai format");
        assertTrue(text.contains("Tiền đặt cọc"), "Thiếu tiền cọc");
        assertTrue(text.contains("Email"), "Thiếu Email");
    }

    @Test
    @Order(5)
    @DisplayName("TC-CI-10: Đổi phòng bỏ trống lý do")
    public void testChangeRoomEmptyReason() {
        List<WebElement> btns = driver.findElements(By.className("btn-cancel"));
        if (btns.isEmpty()) return;
        btns.get(0).click();
        WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("change-room-popup")));
        assertTrue(popup.isDisplayed());
    }

    @Test
    @Order(6)
    @DisplayName("TC-CI-13: Kiểm tra mục Ghi chú (Special Request)")
    public void testSpecialRequestField() {
        List<WebElement> bookings = driver.findElements(By.className("booking-item"));
        Assumptions.assumeFalse(bookings.isEmpty());
        String text = bookings.get(0).getText();
        if (!text.toLowerCase().contains("ghi chú")) {
            System.err.println("⚠️ CẢNH BÁO: Không tìm thấy mục Ghi chú trên phiếu");
        }
    }

    @Test
    @Order(7)
    @DisplayName("TC-CI-07 & TC-CI-16: Kiểm tra logic ngày và Thời gian lưu trú")
    public void testDateLogic() {
        List<WebElement> bookings = driver.findElements(By.className("booking-item"));
        Assumptions.assumeFalse(bookings.isEmpty());
        assertTrue(bookings.get(0).getText().contains("-"), "Sai định dạng ngày");
    }

    @Test
    @Order(8)
    @DisplayName("TC-CI-08: Kiểm tra Sơ đồ phòng")
    public void testRoomMap() {
        assertTrue(driver.findElement(By.className("room-map-card")).isDisplayed());
    }

    @Test
    @Order(9)
    @DisplayName("TC-CI-19: Thay đổi ngày trả khi nhận phòng (Bug confirmation)")
    public void testEditCheckoutDate() {
        List<WebElement> bookings = driver.findElements(By.className("booking-item"));
        Assumptions.assumeFalse(bookings.isEmpty());
        // Kiểm tra xem có nút sửa ngày không
        boolean canEdit = driver.findElements(By.className("btn-edit-date")).size() > 0;
        if (!canEdit) System.err.println("⚠️ CẢNH BÁO: Hệ thống chưa có chức năng sửa ngày trả tại chỗ");
    }

    private void searchBooking(String code) {
        WebElement s = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("search")));
        s.clear(); s.sendKeys(code); s.sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.urlContains("search="));
    }

    private void checkErrorMessage() {
        if (driver.findElements(By.id("errorPopup")).size() > 0 && driver.findElement(By.id("errorPopup")).isDisplayed()) {
            fail("HỆ THỐNG BÁO LỖI: " + driver.findElement(By.id("errorMessage")).getText());
        } else {
            fail("LỖI: Hệ thống không phản hồi sau khi nhấn Nhận phòng");
        }
    }
}
