package org.example.datn.Service;

import lombok.RequiredArgsConstructor;
import org.example.datn.Entity.NhanVien;
import org.example.datn.Repository.NhanVienRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NhanVienService {
    private final NhanVienRepository nhanVienRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public List<NhanVien> findAll() {
        return nhanVienRepository.findAll();
    }

    public Optional<NhanVien> findById(Integer id) {
        return nhanVienRepository.findById(id);
    }

    public void validateThem(NhanVien nv, BindingResult result) {
        if (nhanVienRepository.existsByMaNv(nv.getMaNv())) {
            result.rejectValue("maNv", "duplicate", "Mã nhân viên đã tồn tại");
        }

        if (nhanVienRepository.existsByEmail(nv.getEmail())) {
            result.rejectValue("email", "duplicate", "Email đã được sử dụng");
        }

        if (nv.getCccd() != null && !nv.getCccd().isBlank()
                && nhanVienRepository.existsByCccd(nv.getCccd())) {
            result.rejectValue("cccd", "duplicate", "CCCD đã tồn tại");
        }
    }

    public void validateSua(NhanVien nv, BindingResult result) {
        if (nhanVienRepository.existsByMaNvAndIdNvNot(nv.getMaNv(), nv.getIdNv())) {
            result.rejectValue("maNv", "duplicate", "Mã nhân viên đã tồn tại");
        }

        if (nhanVienRepository.existsByEmailAndIdNvNot(nv.getEmail(), nv.getIdNv())) {
            result.rejectValue("email", "duplicate", "Email đã được sử dụng");
        }

        if (nv.getCccd() != null && !nv.getCccd().isBlank()
                && nhanVienRepository.existsByCccdAndIdNvNot(nv.getCccd(), nv.getIdNv())) {
            result.rejectValue("cccd", "duplicate", "CCCD đã tồn tại");
        }
    }

    public void them(NhanVien nv) {

        if (nv.getMatKhau() == null || nv.getMatKhau().isBlank()) {
            nv.setMatKhau("123456");
        }

        nv.setMatKhau(passwordEncoder.encode(nv.getMatKhau()));
        nv.setTrangThai(true);

        nhanVienRepository.save(nv);
    }

    public void sua(NhanVien nvMoi) {

        NhanVien nv = nhanVienRepository.findById(nvMoi.getIdNv())
                .orElseThrow();

        nv.setMaNv(nvMoi.getMaNv());
        nv.setHoTen(nvMoi.getHoTen());
        nv.setSoDienThoai(nvMoi.getSoDienThoai());
        nv.setEmail(nvMoi.getEmail());
        nv.setCccd(nvMoi.getCccd());
        nv.setGioiTinh(nvMoi.getGioiTinh());
        nv.setNgaySinh(nvMoi.getNgaySinh());
        nv.setDiaChi(nvMoi.getDiaChi());
        nv.setVaiTro(nvMoi.getVaiTro());
        nv.setNgayVaoLam(nvMoi.getNgayVaoLam());
        nv.setTrangThai(nvMoi.getTrangThai());

        if (nvMoi.getMatKhau() != null && !nvMoi.getMatKhau().isBlank()) {
            nv.setMatKhau(passwordEncoder.encode(nvMoi.getMatKhau()));
        }

        nhanVienRepository.save(nv);
    }

    public void xoaMem(Integer id) {
        nhanVienRepository.deleteById(id);
    }

    public void xoaCung(Integer id) {
        nhanVienRepository.deleteById(id);
    }
}
