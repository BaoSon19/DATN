package org.example.datn.Service;

import lombok.RequiredArgsConstructor;
import org.example.datn.Entity.KhachHang;
import org.example.datn.Repository.KhachHangRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KhachHangService {

    private final KhachHangRepository repo;

    public List<KhachHang> findAll() {
        return repo.findAll();
    }

    public Optional<KhachHang> findById(Integer id) {
        return repo.findById(id);
    }

    public void them(KhachHang kh) {

        if (kh.getMatKhau() == null || kh.getMatKhau().isBlank()) {
            kh.setMatKhau("123456");
        }

        kh.setTrangThai(true);
        repo.save(kh);
    }

    public void sua(KhachHang kh) {
        KhachHang old = repo.findById(kh.getIdKh())
                .orElseThrow();

        old.setMaKh(kh.getMaKh());
        old.setTenKhachHang(kh.getTenKhachHang());
        old.setTenTaiKhoan(kh.getTenTaiKhoan());
        old.setSoDienThoai(kh.getSoDienThoai());
        old.setEmail(kh.getEmail());
        old.setDiaChi(kh.getDiaChi());
        old.setGioiTinh(kh.getGioiTinh());
        old.setNgaySinh(kh.getNgaySinh());
        old.setMatKhau(kh.getMatKhau());
        old.setTrangThai(kh.getTrangThai());

        repo.save(old);
    }

    public void xoa(Integer id) {
        repo.deleteById(id);
    }
}
