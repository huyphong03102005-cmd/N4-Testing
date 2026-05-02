package com.n4testing;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDichVuPhong {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:8085/service";

    @BeforeEach
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless"); // Mở comment nếu muốn chạy ẩn danh
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(BASE_URL);
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Kịch bản 1: Luồng Nghiệp vụ Chính (TC-SV-01, 02, 03)
     */
    @Test
    @Order(1)
    public void TC_SV_01() {
        WebElement room = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".map-room-card.bg-sudung")));
        room.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("roomDetailSection")));

        // Thêm dịch vụ
        WebElement serviceItem = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//div[@id='itemList']//div[@class='item-card'])[2]")));
        serviceItem.click();

        // Tăng số lượng
        WebElement btnPlus = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'selected-item')]//span[text()='+']")));
        btnPlus.click();

        // Lưu
        driver.findElement(By.cssSelector(".f-btn-save")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successPopup")));
        driver.findElement(By.xpath("//button[text()='Xong']")).click();
    }

    /**
     * Kịch bản 2: Kiểm soát Dữ liệu (TC-SV-07)
     */
    @Test
    @Order(2)
    public void TC_SV_02() {
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'item-card') and .//div[contains(text(), 'Thêm dịch vụ mới')]]")));
        addBtn.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("crudPopup")));
        driver.findElement(By.id("crudPriceInput")).sendKeys("100000");
        driver.findElement(By.id("crudSaveBtn")).click();
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("alertPopup")));
        assertTrue(alert.getText().contains("nhập tên"));
        driver.findElement(By.xpath("//button[text()='ĐÓNG']")).click();
    }

    /**
     * Kịch bản 3: Quản lý Bồi thường và Cộng dồn (TC-SV-14, 23)
     */
    @Test
    @Order(3)
    public void TC_SV_03() {
        WebElement room = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".map-room-card.bg-sudung")));
        room.click();

        driver.findElement(By.id("btnCompensation")).click();

        // Thêm tài sản bồi thường ở vị trí số 3
        WebElement assetItem = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//div[@class='item-card'])[3]")));
        String assetName = assetItem.findElement(By.className("item-name")).getText();
        assetItem.click();
        assetItem.click();

        // Chỉ kiểm tra số lượng của ĐÚNG món đồ vừa chọn (tránh lấy nhầm đồ của testcase khác)
        String specificQtyPath = "//div[contains(@class, 'selected-item') and .//span[contains(text(), '" + assetName + "')]]//span[@class='qty-value']";
        WebElement qtyValue = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(specificQtyPath)));
        assertEquals("02", qtyValue.getText(), "Số lượng của " + assetName + " không đúng!");
    }

    /**
     * Kịch bản 4: Giảm số lượng về min boundary (TC-SV-04)
     * Luồng: Chọn phòng -> Chọn Buffet Tối -> Giảm số lượng về 1 -> Bấm '-' -> Hiện Popup
     */
    @Test
    @Order(4)
    public void TC_SV_04() throws InterruptedException {
        WebElement room = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".map-room-card.bg-sudung")));
        room.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("roomDetailSection")));

        String targetName = "Buffet Tối";
        WebElement item = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@id='itemList']//div[contains(@class, 'item-card') and .//div[contains(text(), '" + targetName + "')]]")));
        item.click();

        String selectedItemPath = "//div[contains(@class, 'selected-item') and .//span[contains(text(), '" + targetName + "')]]";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(selectedItemPath)));

        // Vòng lặp giảm số lượng (Tìm lại element trong mỗi lần lặp để tránh Stale lỗi)
        while (true) {
            WebElement qtyValue = driver.findElement(By.xpath(selectedItemPath + "//span[@class='qty-value']"));
            int currentQty = Integer.parseInt(qtyValue.getText());
            if (currentQty <= 1) break;

            driver.findElement(By.xpath(selectedItemPath + "//span[text()='-']")).click();
            Thread.sleep(300); // Đợi UI cập nhật
        }

        // Bấm '-' lần cuối để hiện Popup
        driver.findElement(By.xpath(selectedItemPath + "//span[text()='-']")).click();
        WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmPopup")));
        assertTrue(popup.isDisplayed());

        driver.findElement(By.xpath("//div[@id='confirmPopup']//button[contains(text(), 'HUỶ')]")).click();
        wait.until(ExpectedConditions.invisibilityOf(popup));
        assertTrue(driver.findElement(By.xpath(selectedItemPath)).isDisplayed());

        driver.findElement(By.xpath(selectedItemPath + "//span[text()='-']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmPopup")));
        driver.findElement(By.id("confirmYesBtn")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(selectedItemPath)));
    }

    /**
     * TC-SV-13: Tăng số lượng dịch vụ lên giá trị rất lớn
     */
    @Test
    @Order(5)
    public void TC_SV_05() throws InterruptedException {
        // 1. Chọn phòng
        WebElement room = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".map-room-card.bg-sudung")));
        room.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("roomDetailSection")));

        // 2. Chọn dịch vụ "Buffet Tối"
        String targetName = "Buffet Tối";
        WebElement catalogItem = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@id='itemList']//div[contains(@class, 'item-card') and .//div[contains(text(), '" + targetName + "')]]")));

        // Lấy đơn giá
        long unitPrice = Long.parseLong(catalogItem.findElement(By.className("item-price")).getText().replaceAll("[^0-9]", ""));
        catalogItem.click();

        // 3. Tìm phần tử đã chọn trong danh sách bên phải
        String selPath = "//div[contains(@class, 'selected-item') and .//span[contains(text(), '" + targetName + "')]]";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(selPath)));

        // 4. Nhấn "+" liên tục đến số lượng 10
        for (int i = 0; i < 20; i++) { // Loop tối đa 20 lần để an toàn
            WebElement qtyVal = driver.findElement(By.xpath(selPath + "//span[@class='qty-value']"));
            int currentQty = Integer.parseInt(qtyVal.getText().trim());

            if (currentQty >= 10) break;

            WebElement btnPlus = driver.findElement(By.xpath(selPath + "//span[text()='+']"));
            btnPlus.click();

            // Đợi số lượng thay đổi
            final int lastQty = currentQty;
            wait.until(d -> {
                String val = d.findElement(By.xpath(selPath + "//span[@class='qty-value']")).getText().trim();
                return Integer.parseInt(val) > lastQty;
            });
        }

        // 5. Kiểm tra số lượng cuối cùng là 10
        String finalQtyStr = driver.findElement(By.xpath(selPath + "//span[@class='qty-value']")).getText().trim();
        int finalQty = Integer.parseInt(finalQtyStr);
        assertEquals(10, finalQty, "Số lượng không đạt tới 10!");

        // 6. Kiểm tra Tổng tiền (Đơn giá * 10)
        long expectedTotal = unitPrice * finalQty;

        // Đợi tổng tiền cập nhật khớp với kỳ vọng (tránh trễ UI)
        wait.until(d -> {
            String txt = d.findElement(By.xpath(selPath + "//*[contains(@class, 'selected-item-price')]")).getText();
            long actual = Long.parseLong(txt.replaceAll("[^0-9]", ""));
            return actual == expectedTotal;
        });

        long actualTotal = Long.parseLong(driver.findElement(By.xpath(selPath + "//*[contains(@class, 'selected-item-price')]")).getText().replaceAll("[^0-9]", ""));
        assertEquals(expectedTotal, actualTotal, "Tổng tiền không khớp hoặc bị lỗi overflow!");
    }
}
