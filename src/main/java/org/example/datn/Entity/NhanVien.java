package org.example.datn.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhan_vien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nv")
    private Integer idNv;

    @NotBlank(message = "Mã nhân viên không được để trống")
    @Size(max = 50, message = "Mã nhân viên tối đa 50 ký tự")
    @Column(name = "ma_nv", unique = true)
    private String maNv;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    @Column(name = "ho_ten")
    private String hoTen;

    @Pattern(regexp = "^(0[3|5|7|8|9])+([0-9]{8})$", message = "Số điện thoại không hợp lệ")
    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100)
    @Column(name = "email")
    private String email;

    @Size(max = 255)
    @Column(name = "mat_khau")
    private String matKhau;

    @Pattern(regexp = "^[0-9]{12}$", message = "CCCD phải có đúng 12 chữ số")
    @Column(name = "cccd")
    private String cccd;

    @Column(name = "gioi_tinh")
    private String gioiTinh;

    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Size(max = 255)
    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "vai_tro")
    private String vaiTro;

    @Column(name = "trang_thai")
    private Boolean trangThai = true;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_vao_lam")
    private LocalDate ngayVaoLam;

    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}
