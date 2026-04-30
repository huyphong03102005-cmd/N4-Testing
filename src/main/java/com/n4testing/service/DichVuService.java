package com.n4testing.service;

import com.n4testing.model.*;
import com.n4testing.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DichVuService {

    private final DichVuRepository dichVuRepository;
    private final SuDungDichVuRepository suDungDichVuRepository;
    private final LuuTruRepository luuTruRepository;
    private final TaiSanRepository taiSanRepository;
    private final ThietHaiRepository thietHaiRepository;
    private final PhongRepository phongRepository;

    public List<DichVu> getAllDichVu() {
        return dichVuRepository.findAll();
    }

    @Transactional
    public DichVu createDichVu(DichVu dichVu) {
        return dichVuRepository.save(dichVu);
    }

    @Transactional
    public DichVu updateDichVu(Integer id, DichVu updated) {
        DichVu dichVu = dichVuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại"));
        dichVu.setTenDichVu(updated.getTenDichVu());
        dichVu.setDonGia(updated.getDonGia());
        return dichVuRepository.save(dichVu);
    }

    @Transactional
    public void deleteDichVu(Integer id) {
        dichVuRepository.deleteById(id);
    }

    public List<TaiSan> getAllTaiSan() {
        return taiSanRepository.findAll();
    }

    @Transactional
    public TaiSan createTaiSan(TaiSan taiSan) {
        return taiSanRepository.save(taiSan);
    }

    @Transactional
    public TaiSan updateTaiSan(Integer id, TaiSan updated) {
        TaiSan taiSan = taiSanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tài sản không tồn tại"));
        taiSan.setTenTaiSan(updated.getTenTaiSan());
        taiSan.setGiaTriBoiThuong(updated.getGiaTriBoiThuong());
        return taiSanRepository.save(taiSan);
    }

    @Transactional
    public void deleteTaiSan(Integer id) {
        taiSanRepository.deleteById(id);
    }

    public List<TaiSan> getTaiSanByPhong(Integer idPhong) {
        Phong phong = phongRepository.findById(idPhong)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));
        return taiSanRepository.findByPhong(phong);
    }

    @Transactional
    public void addSuDungDichVu(Integer idLuutru, Integer idDichvu, Integer quantity) {
        LuuTru luuTru = luuTruRepository.findById(idLuutru)
                .orElseThrow(() -> new RuntimeException("Bản ghi lưu trú không tồn tại"));
        DichVu dichVu = dichVuRepository.findById(idDichvu)
                .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại"));

        SuDungDichVu suDung = new SuDungDichVu();
        suDung.setLuuTru(luuTru);
        suDung.setDichVu(dichVu);
        suDung.setSoluong(quantity);
        suDung.setThanhTien(dichVu.getDonGia().multiply(new BigDecimal(quantity)));
        
        suDungDichVuRepository.save(suDung);
    }

    @Transactional
    public void addThietHai(Integer idLuutru, Integer idTaisan, String mucDo, Integer soLuong, BigDecimal fineAmount) {
        LuuTru luuTru = luuTruRepository.findById(idLuutru)
                .orElseThrow(() -> new RuntimeException("Bản ghi lưu trú không tồn tại"));
        TaiSan taiSan = taiSanRepository.findById(idTaisan)
                .orElseThrow(() -> new RuntimeException("Tài sản không tồn tại"));

        ThietHai thietHai = new ThietHai();
        thietHai.setLuuTru(luuTru);
        thietHai.setTaiSan(taiSan);
        thietHai.setMucDo(mucDo);
        thietHai.setSoLuong(soLuong);
        thietHai.setSoTienBoiThuong(fineAmount);
        thietHai.setTrangThai("Chưa thanh toán");

        thietHaiRepository.save(thietHai);
    }

    @Transactional
    public void updateSuDungDichVuQty(Integer idSudungDv, Integer quantity) {
        SuDungDichVu sd = suDungDichVuRepository.findById(idSudungDv)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi sử dụng dịch vụ"));
        sd.setSoluong(quantity);
        if (sd.getDichVu() != null) {
            sd.setThanhTien(sd.getDichVu().getDonGia().multiply(new BigDecimal(quantity)));
        }
        suDungDichVuRepository.save(sd);
    }

    @Transactional
    public void updateThietHaiQty(Integer idThietHai, Integer quantity) {
        ThietHai th = thietHaiRepository.findById(idThietHai)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi bồi thường"));
        th.setSoLuong(quantity);
        if (th.getTaiSan() != null && th.getTaiSan().getGiaTriBoiThuong() != null) {
            th.setSoTienBoiThuong(th.getTaiSan().getGiaTriBoiThuong().multiply(new BigDecimal(quantity)));
        }
        thietHaiRepository.save(th);
    }

    @Transactional
    public void deleteSuDungDichVu(Integer idSudungDv) {
        suDungDichVuRepository.deleteById(idSudungDv);
    }

    @Transactional
    public void deleteThietHai(Integer idThietHai) {
        thietHaiRepository.deleteById(idThietHai);
    }
}
