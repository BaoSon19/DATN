package org.example.datn.Repository;

import org.example.datn.Entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien,Integer> {
    boolean existsByMaNv(String maNv);

    boolean existsByEmail(String email);

    boolean existsByCccd(String cccd);

    boolean existsByMaNvAndIdNvNot(String maNv, Integer idNv);

    boolean existsByEmailAndIdNvNot(String email, Integer idNv);

    boolean existsByCccdAndIdNvNot(String cccd, Integer idNv);

    Optional<NhanVien> findByEmail(String email);
    List<NhanVien> findByTrangThaiTrue();
}
