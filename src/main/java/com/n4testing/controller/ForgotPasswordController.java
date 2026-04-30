package com.n4testing.controller;

import com.n4testing.model.TaiKhoan;
import com.n4testing.repository.TaiKhoanRepository;
import com.n4testing.service.MailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.Random;

@Controller
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final TaiKhoanRepository taiKhoanRepository;
    private final MailService mailService;

    // Bước 1: Hiển thị trang nhập Email
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot_password";
    }

    // Bước 1: Xử lý gửi OTP
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, 
                                       HttpSession session, 
                                       Model model) {
        Optional<TaiKhoan> account = taiKhoanRepository.findByEmail(email);
        
        if (account.isPresent()) {
            String otp = String.format("%06d", new Random().nextInt(999999));
            session.setAttribute("otp", otp);
            session.setAttribute("resetEmail", email);
            mailService.sendOtp(email, otp);
            
            return "redirect:/verify-otp"; // Chuyển hướng sang URL mới
        } else {
            model.addAttribute("error", "Email không tồn tại trong hệ thống!");
            return "forgot_password";
        }
    }

    // Bước 2: Hiển thị trang nhập OTP
    @GetMapping("/verify-otp")
    public String showVerifyOtpPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("resetEmail");
        if (email == null) return "redirect:/forgot-password";
        
        model.addAttribute("email", email);
        return "verify_otp";
    }

    // Bước 2: Xử lý xác thực OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") String otp, 
                            @RequestParam("email") String email,
                            HttpSession session, 
                            Model model) {
        String sessionOtp = (String) session.getAttribute("otp");
        String sessionEmail = (String) session.getAttribute("resetEmail");
        
        if (sessionOtp != null && sessionOtp.equals(otp) && email.equals(sessionEmail)) {
            return "redirect:/reset-password"; // Chuyển hướng sang bước 3
        } else {
            model.addAttribute("email", email);
            model.addAttribute("error", "Mã OTP không chính xác hoặc đã hết hạn!");
            return "verify_otp";
        }
    }

    // Bước 3: Hiển thị trang đặt lại mật khẩu
    @GetMapping("/reset-password")
    public String showResetPasswordPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("resetEmail");
        if (email == null) return "redirect:/forgot-password";
        
        model.addAttribute("email", email);
        return "reset_password";
    }

    // Bước 3: Xử lý đặt lại mật khẩu
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email,
                                @RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword,
                                HttpSession session,
                                Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("email", email);
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "reset_password";
        }

        Optional<TaiKhoan> accountOpt = taiKhoanRepository.findByEmail(email);
        if (accountOpt.isPresent()) {
            TaiKhoan account = accountOpt.get();
            account.setMatKhau(password);
            taiKhoanRepository.save(account);
            
            session.removeAttribute("otp");
            session.removeAttribute("resetEmail");
            
            return "redirect:/login?success=true";
        } else {
            model.addAttribute("error", "Đã có lỗi xảy ra. Vui lòng thử lại.");
            return "forgot_password";
        }
    }
}
