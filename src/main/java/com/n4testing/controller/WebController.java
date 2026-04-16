package com.n4testing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/login")
    public String login() {
        return "dang_nhap";
    }

    @GetMapping("/tong-quan")
    public String overview(Model model) {
        model.addAttribute("currentPage", "tongquan");
        return "tongquan";
    }

    @GetMapping("/dat-phong")
    public String bookRoom(Model model) {
        model.addAttribute("currentPage", "datphong");
        return "DAT_PHONG";
    }

    @GetMapping("/quan-ly-dat-phong")
    public String manageBooking(Model model) {
        model.addAttribute("currentPage", "qldatphong");
        return "QL_DatPhong";
    }

    @GetMapping("/nhan-phong")
    public String checkIn(Model model) {
        model.addAttribute("currentPage", "nhanphong");
        return "nhan_phong";
    }

    @GetMapping("/dich-vu")
    public String services(Model model) {
        model.addAttribute("currentPage", "dichvu");
        return "Service";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
}
