package com.n4testing.controller;

import com.n4testing.model.Phong;
import com.n4testing.model.TaiKhoan;
import com.n4testing.repository.TaiKhoanRepository;
import com.n4testing.service.NhanPhongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import com.n4testing.model.ChiTietDatPhong;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final NhanPhongService nhanPhongService;
    private final TaiKhoanRepository taiKhoanRepository;

    // 1. / → dang_nhap
    @GetMapping("/")
    public String index() {
        return "dang_nhap";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "dang_nhap";
    }

    // Xử lý đăng nhập từ Database
    @PostMapping("/login")
    public String handleLogin(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        Optional<TaiKhoan> account = taiKhoanRepository.findByTenDangNhap(username);
        
        if (account.isPresent() && account.get().getMatKhau().equals(password)) {
            // Đăng nhập thành công
            return "redirect:/tongquan";
        } else {
            // Đăng nhập thất bại
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            return "dang_nhap";
        }
    }

    // 2. /tongquan → tongquan
    @GetMapping("/tongquan")
    public String overview(Model model) {
        List<Phong> rooms = nhanPhongService.getAllPhongs();
        Map<Integer, ChiTietDatPhong> roomBookings = nhanPhongService.getActiveBookingMap();

        // 1. Nhóm phòng theo tầng và sắp xếp
        Map<String, List<Phong>> floors = rooms.stream()
                .filter(p -> p.getTenPhong() != null && !p.getTenPhong().isEmpty())
                .sorted(Comparator.comparing(Phong::getTenPhong, (s1, s2) -> {
                    try {
                        return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
                    } catch (NumberFormatException e) {
                        return s1.compareTo(s2);
                    }
                }))
                .collect(Collectors.groupingBy(
                        p -> "Tầng " + p.getTenPhong().substring(0, 1),
                        TreeMap::new,
                        Collectors.toList()));

        // 2. Tính toán thống kê
        long total = rooms.size();
        long occupied = rooms.stream().filter(p -> "Bận".equals(p.getTrangThai())).count();
        long maintenance = rooms.stream().filter(p -> "Sửa chữa".equals(p.getTrangThai())).count();
        
        // "Đã đặt" là những phòng Trống nhưng đã có ChiTietDatPhong (Chờ check-in)
        long reserved = rooms.stream()
                .filter(p -> "Trống".equals(p.getTrangThai()) && 
                             roomBookings.containsKey(p.getIdPhong()) && 
                             "Chờ check-in".equals(roomBookings.get(p.getIdPhong()).getDatPhong().getTrangThai()))
                .count();
        
        long available = total - occupied - maintenance - reserved;

        model.addAttribute("floors", floors);
        model.addAttribute("roomBookings", roomBookings);
        model.addAttribute("stats", Map.of(
                "total", total,
                "available", available,
                "occupied", occupied,
                "reserved", reserved,
                "maintenance", maintenance
        ));
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

    // 7. /tra-phong → tra_phong (Có sắp xếp sơ đồ phòng)
    @GetMapping("/tra-phong")
    public String checkOut(Model model) {
        List<Phong> rooms = nhanPhongService.getAllPhongs();
        Map<String, List<Phong>> floors = rooms.stream()
                .filter(p -> p.getTenPhong() != null && !p.getTenPhong().isEmpty())
                .sorted((p1, p2) -> p1.getTenPhong().compareTo(p2.getTenPhong()))
                .collect(Collectors.groupingBy(
                        p -> "Tầng " + p.getTenPhong().substring(0, 1),
                        TreeMap::new,
                        Collectors.toList()
                ));

        model.addAttribute("floors", floors);
        model.addAttribute("currentPage", "traphong");
        return "tra_phong";
    }
}
