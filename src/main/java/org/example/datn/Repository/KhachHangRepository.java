package org.example.datn.Repository;

import org.example.datn.Entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KhachHangRepository extends JpaRepository<KhachHang,Integer> {
    boolean existsByMaKh(String maKh);
    boolean existsByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);
}
