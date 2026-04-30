package com.n4testing;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestDichVuPhong {
    WebDriver driver;

    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testAddServiceToRoom() throws InterruptedException {
        // 1. Mở trang Dịch vụ phòng
        driver.get("http://localhost:8085/service");
        Thread.sleep(2000); // Đợi load data (Nên dùng WebDriverWait thực tế)

        // 2. Click chọn một phòng trên Sơ đồ (Giả sử phòng có class hoặc ID cụ thể)
        WebElement room204 = driver.findElement(By.xpath("//div[contains(text(), '202')]"));
        room204.click();
        Thread.sleep(1000);

        // 3. Click vào 1 dịch vụ bên menu trái (VD: Ăn trưa)
        WebElement serviceItem = driver.findElement(By.xpath("//span[contains(text(), 'Bar')]"));
        serviceItem.click();
        Thread.sleep(1000);

        // 4. Click nút Tăng số lượng (+) của dịch vụ vừa thêm
        WebElement btnPlus = driver.findElement(By.xpath("//div[contains(@class, 'selected-item') and .//span[contains(text(), 'Ăn trưa')]]//span[text()='+']"));
        btnPlus.click();

        // 5. Kiểm tra logic giao diện (Số lượng có lên 02 không)
        WebElement qtyValue = driver.findElement(By.xpath("//div[contains(@class, 'selected-item') and .//span[contains(text(), 'Ăn trưa')]]//span[@class='qty-value']"));
        Assertions.assertEquals("02", qtyValue.getText(), "Số lượng dịch vụ không tăng đúng!");

        // 6. Bấm nút LƯU
        WebElement btnSave = driver.findElement(By.xpath("//button[text()='LƯU']"));
        btnSave.click();

        // 7. Kiểm tra Popup thành công xuất hiện
        WebElement successPopup = driver.findElement(By.id("successPopup"));
        Assertions.assertTrue(successPopup.isDisplayed(), "Không hiện thông báo lưu thành công!");
    }

    @AfterEach
    public void teardown() {
        // Đóng comment dòng bên dưới để giữ màn hình không bị tắt tự động
        // if (driver != null) {
        //     driver.quit();
        // }
    }
}
