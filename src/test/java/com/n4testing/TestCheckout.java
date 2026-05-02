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
public class TestCheckout {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:8085";

    @BeforeEach
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        // options.addArguments("--headless"); // Mở trình duyệt để xem quá trình test
        driver = new ChromeDriver(options);
        // Thay maximize() bằng setSize để tránh lỗi Chrome 147
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(1440, 900));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        login();
        driver.get(BASE_URL + "/tra-phong");
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
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * TC-CO-01: Xem chi tiết thông tin trả phòng
     */
    @Test
    @Order(1)
    @DisplayName("TC-CO-01: Xem chi tiết thông tin trả phòng")
    public void TC_CO_01() {
        WebElement room = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".map-room-card.bg-sudung")));
        String roomName = room.findElement(By.className("map-room-name")).getText();
        room.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("coDetailSection")));
        
        // Kiểm tra tiêu đề phòng trong chi tiết
        WebElement detailTitle = driver.findElement(By.className("room-title"));
        assertTrue(detailTitle.getText().contains(roomName));
        
        // Kiểm tra hiển thị tiền phòng
        WebElement roomTotal = driver.findElement(By.className("room-total"));
        assertTrue(roomTotal.getText().contains("Tiền phòng:"));
    }

    /**
     * TC-CO-02: Kiểm tra tính đúng đắn của Tổng tiền
     */
    @Test
    @Order(2)
    @DisplayName("TC-CO-02: Kiểm tra tính đúng đắn của Tổng tiền")
    public void TC_CO_02() {
        WebElement room = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".map-room-card.bg-sudung")));
        room.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("coDetailSection")));

        // Lấy tiền phòng
        WebElement roomPriceElem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'room-total')]/span")));
        long roomPrice = Long.parseLong(roomPriceElem.getText().replaceAll("[^0-9]", ""));

        // Lấy tổng dịch vụ phòng (ở panel bên phải)
        WebElement serviceTotalElem = driver.findElement(By.xpath("//div[@id='coRightPanel']//span[text()='Tổng dịch vụ phòng:']/following-sibling::span"));
        long serviceTotal = Long.parseLong(serviceTotalElem.getText().replaceAll("[^0-9]", ""));

        // Lấy tổng tiền hiển thị
        WebElement finalTotalElem = driver.findElement(By.xpath("//div[@id='coRightPanel']//span[text()='Tổng tiền']/following-sibling::span"));
        long finalTotal = Long.parseLong(finalTotalElem.getText().replaceAll("[^0-9]", ""));

        assertEquals(roomPrice + serviceTotal, finalTotal, "Tổng tiền không khớp công thức: Phòng + Dịch vụ");
    }

    /**
     * TC-CO-06: Thanh toán không chọn phương thức
     */
    @Test
    @Order(3)
    @DisplayName("TC-CO-06: Thanh toán không chọn phương thức")
    public void TC_CO_06() {
        WebElement room = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".map-room-card.bg-sudung")));
        room.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("coDetailSection")));

        // Nhấn THANH TOÁN ngay mà không chọn checkbox
        driver.findElement(By.xpath("//button[text()='THANH TOÁN']")).click();

        // Kiểm tra thông báo alert
        WebElement alertMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("alertMessage")));
        assertEquals("Vui lòng chọn 1 phương thức thanh toán!", alertMsg.getText());
        
        WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='alertPopup']//button[text()='ĐÓNG']")));
        closeBtn.click();
    }

    /**
     * TC-CO-07: Xác nhận hóa đơn và thanh toán (Tiền mặt)
     */
    @Test
    @Order(4)
    @DisplayName("TC-CO-07: Xác nhận hóa đơn và thanh toán (Tiền mặt)")
    public void TC_CO_07() {
        WebElement room = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".map-room-card.bg-sudung")));
        room.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("coDetailSection")));

        // Chọn Tiền mặt
        driver.findElement(By.id("cbCash")).click();
        
        // Nhấn THANH TOÁN
        driver.findElement(By.xpath("//button[text()='THANH TOÁN']")).click();

        // Kiểm tra Popup Hóa đơn
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("invoicePopup")));
        assertTrue(driver.findElement(By.id("invTotal")).isDisplayed());

        // Nhấn IN HÓA ĐƠN
        WebElement printBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[onclick='printInvoice()']")));
        printBtn.click();

        // Đợi popup thành công
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("printSuccessPopup")));
        assertEquals("Đã gửi lệnh in hóa đơn!", driver.findElement(By.cssSelector("#printSuccessPopup .popup-title")).getText());

        // Nhấn Xong
        driver.findElement(By.xpath("//button[text()='Xong']")).click();

        // Kiểm tra quay về sơ đồ phòng
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("coMapSection")));
    }

    /**
     * TC-CO-09: Hủy popup hóa đơn (không in)
     */
    @Test
    @Order(5)
    @DisplayName("TC-CO-09: Hủy popup hóa đơn (không in)")
    public void TC_CO_09() {
        WebElement room = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".map-room-card.bg-sudung")));
        room.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("coDetailSection")));

        driver.findElement(By.id("cbCash")).click();
        driver.findElement(By.xpath("//button[text()='THANH TOÁN']")).click();

        // Popup mở ra
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("invoicePopup")));

        // Nhấn Đóng
        WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='invoicePopup']//button[text()='ĐÓNG']")));
        closeBtn.click();

        // Kiểm tra popup biến mất nhưng vẫn ở trang chi tiết (không bị chuyển trang)
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("invoicePopup")));
        assertTrue(driver.findElement(By.id("coDetailSection")).isDisplayed());
    }

    /**
     * TC-CO-11: Kiểm tra trạng thái phòng sau trả phòng
     */
    @Test
    @Order(6)
    @DisplayName("TC-CO-11: Kiểm tra trạng thái phòng sau trả phòng")
    public void TC_CO_11() {
        // Tìm phòng đang sử dụng, lấy tên
        WebElement room = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".map-room-card.bg-sudung")));
        String roomName = room.findElement(By.className("map-room-name")).getText();
        room.click();

        // Thực hiện thanh toán nhanh
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("coDetailSection")));
        driver.findElement(By.id("cbCash")).click();
        driver.findElement(By.xpath("//button[text()='THANH TOÁN']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("invoicePopup")));
        WebElement printBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(normalize-space(.), 'IN HÓA ĐƠN')]")));
        printBtn.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("printSuccessPopup")));
        driver.findElement(By.xpath("//button[text()='Xong']")).click();

        // Sau khi reload (mặc định app gọi reload), kiểm tra phòng đó không còn màu đỏ (bg-sudung)
        // Lưu ý: Có thể trang sẽ reload, nên cần đợi map container xuất hiện lại
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("coMapSection")));
        
        // Kiểm tra phòng đó không còn class bg-sudung (vì trang trả phòng chỉ hiện phòng đang sử dụng)
        List<WebElement> redRooms = driver.findElements(By.xpath("//div[contains(@class, 'map-room-card')]//div[text()='" + roomName + "']"));
        assertTrue(redRooms.isEmpty(), "Phòng " + roomName + " vẫn còn trong danh sách 'Đang sử dụng' sau khi trả phòng!");
    }

    /**
     * TC-CO-13: Check-out phòng không có dịch vụ
     */
    @Test
    @Order(7)
    @DisplayName("TC-CO-13: Check-out phòng không có dịch vụ")
    public void TC_CO_13() {
        // Tìm phòng đang sử dụng
        WebElement room = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".map-room-card.bg-sudung")));
        room.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("coDetailSection")));

        // Kiểm tra nếu không có dịch vụ (xem trong panel trái)
        // Nếu có dịch vụ, ta bỏ qua test này hoặc tìm phòng khác. 
        // Nhưng ở đây ta cứ kiểm tra logic tính tiền.
        
        long roomPrice = Long.parseLong(driver.findElement(By.xpath("//div[contains(@class, 'room-total')]/span")).getText().replaceAll("[^0-9]", ""));
        
        // Lấy tổng dịch vụ phòng
        long serviceTotal = Long.parseLong(driver.findElement(By.xpath("//div[@id='coRightPanel']//span[text()='Tổng dịch vụ phòng:']/following-sibling::span")).getText().replaceAll("[^0-9]", ""));

        // Nếu serviceTotal == 0, kiểm tra tổng tiền
        if (serviceTotal == 0) {
            long finalTotal = Long.parseLong(driver.findElement(By.xpath("//div[@id='coRightPanel']//span[text()='Tổng tiền']/following-sibling::span")).getText().replaceAll("[^0-9]", ""));
            assertEquals(roomPrice, finalTotal, "Phòng không dịch vụ thì Tổng tiền phải bằng Tiền phòng");
        } else {
            System.out.println("SKIP TC-CO-13: Phòng này có dịch vụ, không phù hợp để test case này.");
        }
    }
}
