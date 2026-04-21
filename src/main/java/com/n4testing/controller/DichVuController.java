package com.n4testing.controller;

import com.n4testing.model.DichVu;
import com.n4testing.model.TaiSan;
import com.n4testing.service.DichVuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DichVuController {

    private final DichVuService dichVuService;

    @GetMapping("/dich-vu")
    public List<DichVu> getAllDichVu() {
        return dichVuService.getAllDichVu();
    }

    @GetMapping("/tai-san")
    public List<TaiSan> getAllTaiSan() {
        return dichVuService.getAllTaiSan();
    }

    @GetMapping("/phong/{idPhong}/tai-san")
    public List<TaiSan> getTaiSanByPhong(@PathVariable Integer idPhong) {
        return dichVuService.getTaiSanByPhong(idPhong);
    }

    @PostMapping("/su-dung-dich-vu")
    public ResponseEntity<?> addSuDung(@RequestBody Map<String, Object> payload) {
        try {
            Integer idLuutru = (Integer) payload.get("idLuutru");
            Integer idDichvu = (Integer) payload.get("idDichvu");
            Integer quantity = (Integer) payload.get("quantity");
            dichVuService.addSuDungDichVu(idLuutru, idDichvu, quantity);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/thiet-hai")
    public ResponseEntity<?> addThietHai(@RequestBody Map<String, Object> payload) {
        try {
            Integer idLuutru = (Integer) payload.get("idLuutru");
            Integer idTaisan = (Integer) payload.get("idTaisan");
            String mucDo = (String) payload.get("mucDo");
            BigDecimal fineAmount = new BigDecimal(payload.get("fineAmount").toString());
            dichVuService.addThietHai(idLuutru, idTaisan, mucDo, fineAmount);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/su-dung-dich-vu/{id}")
    public ResponseEntity<?> updateSuDung(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        try {
            Integer quantity = (Integer) payload.get("quantity");
            dichVuService.updateSuDungDichVuQty(id, quantity);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/su-dung-dich-vu/{id}")
    public ResponseEntity<?> deleteSuDung(@PathVariable Integer id) {
        try {
            dichVuService.deleteSuDungDichVu(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/thiet-hai/{id}")
    public ResponseEntity<?> deleteThietHai(@PathVariable Integer id) {
        try {
            dichVuService.deleteThietHai(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
