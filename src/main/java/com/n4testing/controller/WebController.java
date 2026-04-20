package com.n4testing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    // 1. / → dang_nhap
    @GetMapping("/")
    public String login() {
        return "dang_nhap";
    }

    // 2. /tongquan → tongquan
    @GetMapping("/tongquan")
    public String overview(Model model) {
        model.addAttribute("currentPage", "tongquan");
        return "tongquan";
    }

    // 3. /dat-phong → DAT_PHONG
    @GetMapping("/dat-phong")
    public String bookRoom(Model model) {
        model.addAttribute("currentPage", "datphong");
        return "DAT_PHONG";
    }


    // 5. /ql-datphong → QL_DatPhong
    @GetMapping("/ql-datphong")
    public String manageBooking(Model model) {
        model.addAttribute("currentPage", "qldatphong");
        return "QL_DatPhong";
    }

    // 6. /service → Service
    @GetMapping("/service")
    public String services(Model model) {
        model.addAttribute("currentPage", "dichvu");
        return "Service";
    }

    // 7. /tra-phong → tra_phong
    @GetMapping("/tra-phong")
    public String checkOut(Model model) {
        model.addAttribute("currentPage", "traphong");
        return "tra_phong";
    }

    // Route legacy /login for convenience
    @GetMapping("/login")
    public String loginPage() {
        return "dang_nhap";
    }
}
