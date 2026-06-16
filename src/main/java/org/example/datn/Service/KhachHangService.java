package org.example.datn.Service;

import lombok.RequiredArgsConstructor;
import org.example.datn.Entity.KhachHang;
import org.example.datn.Repository.KhachHangRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<KhachHang> timKiem(String keyword, String trangThai) {
        return repo.findAll().stream()
                .filter(kh -> {
                    if (keyword == null || keyword.isEmpty()) return true;
                    String kw = keyword.toLowerCase();
                    return (kh.getMaKh() != null && kh.getMaKh().toLowerCase().contains(kw)) ||
                            (kh.getTenKhachHang() != null && kh.getTenKhachHang().toLowerCase().contains(kw)) ||
                            (kh.getEmail() != null && kh.getEmail().toLowerCase().contains(kw));
                })
                .filter(kh -> {
                    if (trangThai == null || trangThai.isEmpty()) return true;
                    return kh.getTrangThai().toString().equals(trangThai);
                })
                .collect(Collectors.toList());
    }

    public void doiTrangThai(Integer id) {
        KhachHang kh = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        kh.setTrangThai(!kh.getTrangThai());
        repo.save(kh);
    }

    public void them(KhachHang kh) {
        kh.setTrangThai(true);
        repo.save(kh);
    }

    public void sua(KhachHang kh) {
        KhachHang old = repo.findById(kh.getIdKh())
                .orElseThrow();

        old.setMaKh(kh.getMaKh());
        old.setTenKhachHang(kh.getTenKhachHang());
        old.setSoDienThoai(kh.getSoDienThoai());
        old.setEmail(kh.getEmail());
        old.setDiaChi(kh.getDiaChi());
        old.setGioiTinh(kh.getGioiTinh());
        old.setNgaySinh(kh.getNgaySinh());
        old.setTrangThai(kh.getTrangThai());

        repo.save(old);
    }

    public void xoa(Integer id) {
        repo.deleteById(id);
    }
}