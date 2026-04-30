package com.n4testing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtp(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Mã OTP xác thực quên mật khẩu");
        message.setText("Chào bạn,\n\nMã OTP của bạn là: " + otp + "\n\nVui lòng không chia sẻ mã này với bất kỳ ai.\n\nTrân trọng.");
        
        try {
            mailSender.send(message);
            System.out.println("OTP sent to " + to + ": " + otp);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }

        // Ghi OTP vào file để phục vụ Test Automation (Dùng đường dẫn tuyệt đối)
        try {
            String path = System.getProperty("user.dir") + "/otp.txt";
            java.nio.file.Files.writeString(java.nio.file.Paths.get(path), otp);
            System.out.println("OTP saved to: " + path);
        } catch (java.io.IOException e) {
            System.err.println("Error writing OTP to file: " + e.getMessage());
        }
    }
}
