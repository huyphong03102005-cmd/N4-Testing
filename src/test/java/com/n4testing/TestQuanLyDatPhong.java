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
import java.util.List;
import java.util.stream.Collectors;

public class TestQuanLyDatPhong {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Đăng nhập trước khi vào quản lý đặt phòng
        driver.get("http://localhost:8085/login");
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameField.sendKeys("admin_hotel");
        driver.findElement(By.id("password")).sendKeys("pass_123");
        driver.findElement(By.className("btn-login")).click();
        wait.until(ExpectedConditions.urlContains("/tongquan"));
        
        // Chuyển sang trang Quản lý đặt phòng
        driver.get("http://localhost:8085/ql-datphong");
        wait.until(ExpectedConditions.urlContains("/ql-datphong"));
    }

    // TC-QLDP-01: Kiểm tra hiển thị tổng quan danh sách
    @Test
    public void TC_QLDP_01() {
        // Kiểm tra 3 khối thống kê hiển thị
        WebElement summaryChoCheckin = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("summary-cho-checkin-count")));
        WebElement summaryDaCheckin = driver.findElement(By.id("summary-da-checkin-count"));
        WebElement summaryDaHuy = driver.findElement(By.id("summary-da-huy-count"));

        Assertions.assertTrue(summaryChoCheckin.isDisplayed());
        Assertions.assertTrue(summaryDaCheckin.isDisplayed());
        Assertions.assertTrue(summaryDaHuy.isDisplayed());

        // Đếm số lượng card thực tế trong các danh sách
        int choCheckinCardsCount = driver.findElements(By.cssSelector("#grid-cho-checkin .booking-card")).size();
        int daCheckinCardsCount = driver.findElements(By.cssSelector("#grid-da-checkin .booking-card")).size();
        int daHuyCardsCount = driver.findElements(By.cssSelector("#grid-da-huy .booking-card")).size();

        // Kiểm tra số lượng trên khối thống kê khớp với card thực tế
        Assertions.assertEquals(String.valueOf(choCheckinCardsCount), summaryChoCheckin.getText());
        Assertions.assertEquals(String.valueOf(daCheckinCardsCount), summaryDaCheckin.getText());
        Assertions.assertEquals(String.valueOf(daHuyCardsCount), summaryDaHuy.getText());
        
        // Kiểm tra số lượng trên badge (số nhỏ cạnh tiêu đề)
        String badgeChoCheckin = driver.findElement(By.id("badge-cho-checkin")).getText();
        Assertions.assertEquals(String.valueOf(choCheckinCardsCount), badgeChoCheckin);
    }

    // TC-QLDP-02: Kiểm tra tính toán Tổng tiền cho đơn đặt phòng nhiều đêm
    @Test
    public void TC_QLDP_02() {
        // Lấy thẻ giá tiền của một booking
        List<WebElement> prices = driver.findElements(By.className("price"));
        if (!prices.isEmpty()) {
            String tongTien = prices.get(0).getText();
            Assertions.assertFalse(tongTien.isEmpty(), "Tổng tiền không được để trống");
            Assertions.assertTrue(tongTien.contains("₫"), "Phải có ký hiệu tiền tệ ₫");
            // Kiểm tra có dấu phẩy/chấm cách nghìn (VD: 1,500,000 hoặc 1.500.000)
            Assertions.assertTrue(tongTien.matches(".*\\d{1,3}([,.]\\d{3})*.*"), "Định dạng số phải có phân cách nghìn");
        }
    }

    // TC-QLDP-03: Kiểm tra tính Tổng tiền cho đơn đặt phòng 1 đêm
    @Test
    public void TC_QLDP_03() {
        List<WebElement> prices = driver.findElements(By.className("price"));
        if (prices.size() > 1) {
            String tongTien = prices.get(1).getText();
            Assertions.assertFalse(tongTien.isEmpty());
            Assertions.assertTrue(tongTien.contains("₫"));
        }
    }

    // TC-QLDP-04: Kiểm tra tìm kiếm theo tên khách hàng
    @Test
    public void TC_QLDP_04() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bookingSearchInput")));
        searchInput.sendKeys("nam");
        
        try { Thread.sleep(500); } catch(InterruptedException e){} // Đợi JS filter chạy

        List<WebElement> visibleCards = driver.findElements(By.className("booking-card")).stream()
                .filter(WebElement::isDisplayed)
                .collect(Collectors.toList());
                
        for (WebElement card : visibleCards) {
            Assertions.assertTrue(card.getText().toLowerCase().contains("nam"), "Thẻ hiển thị phải chứa từ khóa 'nam'");
        }
    }

    // TC-QLDP-05: Kiểm tra tìm kiếm theo số phòng
    @Test
    public void TC_QLDP_05() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bookingSearchInput")));
        searchInput.sendKeys("302");
        
        try { Thread.sleep(500); } catch(InterruptedException e){}

        List<WebElement> visibleCards = driver.findElements(By.className("booking-card")).stream()
                .filter(WebElement::isDisplayed)
                .collect(Collectors.toList());
                
        for (WebElement card : visibleCards) {
            Assertions.assertTrue(card.getText().contains("302"));
        }
    }

    // TC-QLDP-06: Kiểm tra tìm kiếm theo SĐT
    @Test
    public void TC_QLDP_06() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bookingSearchInput")));
        searchInput.sendKeys("0988");
        
        try { Thread.sleep(500); } catch(InterruptedException e){}

        List<WebElement> visibleCards = driver.findElements(By.className("booking-card")).stream()
                .filter(WebElement::isDisplayed)
                .collect(Collectors.toList());
                
        for (WebElement card : visibleCards) {
            Assertions.assertTrue(card.getText().contains("0988"));
        }
    }

    // TC-QLDP-07: Kiểm tra tìm kiếm theo Mã đặt phòng
    @Test
    public void TC_QLDP_07() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bookingSearchInput")));
        searchInput.sendKeys("DPW"); // Dựa theo format mã DPW... trong HTML
        
        try { Thread.sleep(500); } catch(InterruptedException e){}

        List<WebElement> visibleCards = driver.findElements(By.className("booking-card")).stream()
                .filter(WebElement::isDisplayed)
                .collect(Collectors.toList());
                
        for (WebElement card : visibleCards) {
            Assertions.assertTrue(card.getText().contains("DPW"));
        }
    }

    // TC-QLDP-08: Kiểm tra chức năng Tìm kiếm khi không có kết quả
    @Test
    public void TC_QLDP_08() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bookingSearchInput")));
        searchInput.sendKeys("xyz123");
        
        try { Thread.sleep(500); } catch(InterruptedException e){}

        List<WebElement> visibleCards = driver.findElements(By.className("booking-card")).stream()
                .filter(WebElement::isDisplayed)
                .collect(Collectors.toList());
                
        Assertions.assertEquals(0, visibleCards.size(), "Danh sách phải trống khi tìm kiếm không có kết quả");
    }

    // Fail - TC-QLDP-09: Kiểm tra màu nút Hủy phòng (Trước giờ check-in)
    @Test
    public void TC_QLDP_09() {
        List<WebElement> disabledBtns = driver.findElements(By.cssSelector(".btn-cancel:disabled"));
        if (!disabledBtns.isEmpty()) {
            WebElement btnCancel = disabledBtns.get(0);
            Assertions.assertFalse(btnCancel.isEnabled(), "Nút phải bị disabled");
            String bg = btnCancel.getCssValue("background-color");
            // rgba(209, 213, 219, 1) tương đương #d1d5db (màu xám)
            Assertions.assertTrue(bg.contains("209, 213, 219") || btnCancel.getAttribute("style").contains("#d1d5db"));
        }
    }

    // TC-QLDP-10: Kiểm tra màu nút Hủy phòng (Quá 2h giờ check-in)
    @Test
    public void TC_QLDP_10() {
        List<WebElement> activeBtns = driver.findElements(By.cssSelector(".btn-cancel.btn-cancel-active"));
        if (!activeBtns.isEmpty()) {
            WebElement btnCancel = activeBtns.get(0);
            Assertions.assertTrue(btnCancel.isEnabled(), "Nút hủy phải bấm được");
            // Kiểm tra class có hiệu ứng nhấp nháy
            Assertions.assertTrue(btnCancel.getAttribute("class").contains("btn-cancel-active"));
        }
    }

    // TC-QLDP-11: Kiểm tra chức năng Hủy phòng
    @Test
    public void TC_QLDP_11() {
        // Tìm một thẻ chờ checkin có thể hủy
        List<WebElement> activeBtns = driver.findElements(By.cssSelector("#grid-cho-checkin .btn-cancel.btn-cancel-active"));
        if (!activeBtns.isEmpty()) {
            WebElement btnCancel = activeBtns.get(0);
            btnCancel.click();

            // Đợi modal xác nhận và click
            WebElement btnConfirm = wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-confirm-cancel")));
            btnConfirm.click();

            // Đợi modal thành công
            WebElement successModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("success-modal")));
            Assertions.assertTrue(successModal.isDisplayed());
            
            WebElement btnDone = driver.findElement(By.id("btn-done-success"));
            btnDone.click();
        }
    }

    // TC-QLDP-12: Kiểm tra xem chi tiết đặt phòng
    @Test
    public void TC_QLDP_12() {
        List<WebElement> cards = driver.findElements(By.className("booking-card"));
        if (!cards.isEmpty()) {
            cards.get(0).click();

            // Đợi modal chi tiết mở lên
            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("details-modal")));
            
            Assertions.assertTrue(modal.isDisplayed(), "Modal chi tiết phải hiển thị");
            Assertions.assertFalse(modal.findElement(By.id("detail-code")).getText().isEmpty());
            Assertions.assertFalse(modal.findElement(By.id("detail-name")).getText().isEmpty());
            Assertions.assertFalse(modal.findElement(By.id("detail-phone")).getText().isEmpty());
            
            // Đóng modal
            driver.findElement(By.id("btn-close-details")).click();
        }
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
