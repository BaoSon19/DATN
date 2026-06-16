package org.example.datn.Service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.datn.Entity.NhanVien;
import org.example.datn.Repository.NhanVienRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        try {
            if (nv.getMatKhau() == null || nv.getMatKhau().isBlank()) {
                nv.setMatKhau("123456");
            }
            nv.setMatKhau(passwordEncoder.encode(nv.getMatKhau()));
            nv.setTrangThai(true);
            nhanVienRepository.save(nv);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm nhân viên: " + e.getMessage());
        }
    }

    public void sua(NhanVien nvMoi) {
        NhanVien nv = nhanVienRepository.findById(nvMoi.getIdNv())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

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

        // Chỉ cập nhật mật khẩu nếu có nhập mới
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

    public List<NhanVien> timKiem(String hoTen, String sdtEmail, String soGiayTo, String trangThai) {
        if ((hoTen == null || hoTen.isEmpty()) &&
                (sdtEmail == null || sdtEmail.isEmpty()) &&
                (soGiayTo == null || soGiayTo.isEmpty()) &&
                (trangThai == null || trangThai.isEmpty())) {
            return nhanVienRepository.findAll();
        }

        return nhanVienRepository.findAll().stream()
                .filter(nv -> hoTen == null || hoTen.isEmpty() || nv.getHoTen().toLowerCase().contains(hoTen.toLowerCase()))
                .filter(nv -> sdtEmail == null || sdtEmail.isEmpty() ||
                        (nv.getEmail() != null && nv.getEmail().toLowerCase().contains(sdtEmail.toLowerCase())) ||
                        (nv.getSoDienThoai() != null && nv.getSoDienThoai().contains(sdtEmail)))
                .filter(nv -> soGiayTo == null || soGiayTo.isEmpty() ||
                        (nv.getCccd() != null && nv.getCccd().contains(soGiayTo)))
                .filter(nv -> trangThai == null || trangThai.isEmpty() ||
                        nv.getTrangThai().toString().equals(trangThai))
                .collect(Collectors.toList());
    }

    public void doiTrangThai(Integer id) {
        NhanVien nv = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        nv.setTrangThai(!nv.getTrangThai());
        nhanVienRepository.save(nv);
    }

    public byte[] exportToExcel(List<NhanVien> danhSach) throws IOException {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Danh sách nhân viên");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            String[] headers = {"STT", "Mã NV", "Họ tên", "Email", "SĐT", "Địa chỉ",
                    "Chức vụ", "Giới tính", "Ngày sinh", "CCCD", "Ngày vào làm", "Trạng thái"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }

            int rowNum = 1;
            for (NhanVien nv : danhSach) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(nv.getMaNv() != null ? nv.getMaNv() : "");
                row.createCell(2).setCellValue(nv.getHoTen() != null ? nv.getHoTen() : "");
                row.createCell(3).setCellValue(nv.getEmail() != null ? nv.getEmail() : "");
                row.createCell(4).setCellValue(nv.getSoDienThoai() != null ? nv.getSoDienThoai() : "");
                row.createCell(5).setCellValue(nv.getDiaChi() != null ? nv.getDiaChi() : "");
                row.createCell(6).setCellValue(nv.getVaiTro() != null ? nv.getVaiTro() : "");
                row.createCell(7).setCellValue(nv.getGioiTinh() != null ? nv.getGioiTinh() : "");

                String ngaySinh = nv.getNgaySinh() != null ? nv.getNgaySinh().format(dateFormatter) : "";
                row.createCell(8).setCellValue(ngaySinh);

                row.createCell(9).setCellValue(nv.getCccd() != null ? nv.getCccd() : "");

                String ngayVaoLam = nv.getNgayVaoLam() != null ? nv.getNgayVaoLam().format(dateFormatter) : "";
                row.createCell(10).setCellValue(ngayVaoLam);

                row.createCell(11).setCellValue(nv.getTrangThai() != null && nv.getTrangThai() ? "Đang làm" : "Nghỉ việc");

                for (int i = 0; i < headers.length; i++) {
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellStyle(dataStyle);
                    }
                }
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}