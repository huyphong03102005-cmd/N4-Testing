package com.n4testing.controller;

import com.n4testing.model.DatPhong;
import com.n4testing.model.Phong;
import com.n4testing.service.NhanPhongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/nhan-phong")
@RequiredArgsConstructor
public class NhanPhongController {

    private final NhanPhongService nhanPhongService;

    @GetMapping
    public String viewNhanPhong(@RequestParam(value = "search", required = false) String search, Model model) {
        // 1. Lấy danh sách đặt phòng
        List<DatPhong> bookings = nhanPhongService.searchBookings(search);
        
        // 2. Lấy danh sách phòng và nhóm theo tầng
        List<Phong> rooms = nhanPhongService.getAllPhongs();
        
        // Nhóm phòng theo tầng dựa trên chữ số đầu tiên của tên phòng (ví dụ 101 -> Tầng 1)
        Map<String, List<Phong>> floors = rooms.stream()
                .collect(Collectors.groupingBy(
                        p -> "Tầng " + p.getTenPhong().substring(0, 1),
                        TreeMap::new, // Để giữ thứ tự tầng
                        Collectors.toList()
                ));

        model.addAttribute("bookings", bookings);
        model.addAttribute("floors", floors);
        model.addAttribute("searchKeyword", search);
        model.addAttribute("currentPage", "nhanphong");
        
        return "nhan_phong";
    }

    @PostMapping("/checkin/{id}")
    public String processCheckin(@PathVariable("id") Integer maDatPhong, RedirectAttributes redirectAttributes) {
        try {
            nhanPhongService.thucHienNhanPhong(maDatPhong);
            redirectAttributes.addFlashAttribute("successMessage", "Mở phòng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/nhan-phong";
    }
}
