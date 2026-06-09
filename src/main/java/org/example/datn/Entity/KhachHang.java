package org.example.datn.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "khach_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kh")
    private Integer idKh;

    @Size(max = 50)
    @Column(name = "ma_kh", unique = true)
    private String maKh;

    @NotBlank(message = "Tên khách hàng không được để trống")
    @Size(max = 100)
    @Column(name = "ten_khach_hang")
    private String tenKhachHang;

    @Size(max = 100)
    @Column(name = "ten_tai_khoan")
    private String tenTaiKhoan;

    @Pattern(regexp = "^(0[3|5|7|8|9])[0-9]{8}$",
            message = "Số điện thoại không hợp lệ")
    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Email(message = "Email không hợp lệ")
    @Column(name = "email")
    private String email;

    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "gioi_tinh")
    private String gioiTinh;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "mat_khau")
    private String matKhau;

    @Column(name = "trang_thai")
    private Boolean trangThai = true;

    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    public void prePersist() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}
