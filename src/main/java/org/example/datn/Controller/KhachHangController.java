package org.example.datn.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.datn.Entity.KhachHang;
import org.example.datn.Service.KhachHangService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/khachHang")
@RequiredArgsConstructor
public class KhachHangController {

    private final KhachHangService service;

    @GetMapping("/hien-thi")
    public String hienThi(Model model,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String trangThai) {
        List<KhachHang> list = service.timKiem(keyword, trangThai);
        model.addAttribute("listKhachHang", list);
        if (!model.containsAttribute("khachHang")) {
            model.addAttribute("khachHang", new KhachHang());
        }
        model.addAttribute("activeMenu", "khachhang");
        return "khach_hang";
    }

    @GetMapping("/doi-trang-thai/{id}")
    public String doiTrangThai(@PathVariable Integer id,
                               RedirectAttributes redirectAttributes) {
        try {
            service.doiTrangThai(id);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cập nhật trạng thái thất bại: " + e.getMessage());
        }
        return "redirect:/khachHang/hien-thi";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("khachHang") KhachHang kh,
                      BindingResult result,
                      Model model,
                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("listKhachHang", service.findAll());
            model.addAttribute("activeMenu", "khachhang");
            return "khach_hang";
        }
        try {
            service.them(kh);
            redirectAttributes.addFlashAttribute("success", "Thêm khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Thêm thất bại: " + e.getMessage());
        }
        return "redirect:/khachHang/hien-thi";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        KhachHang kh = service.findById(id).orElse(new KhachHang());
        model.addAttribute("khachHang", kh);
        model.addAttribute("listKhachHang", service.findAll());
        model.addAttribute("activeMenu", "khachhang");
        return "khach_hang";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute KhachHang kh,
                         RedirectAttributes redirectAttributes) {
        try {
            kh.setIdKh(id);
            service.sua(kh);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cập nhật thất bại!");
        }
        return "redirect:/khachHang/hien-thi";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            service.xoa(id);
            redirectAttributes.addFlashAttribute("success", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Xóa thất bại!");
        }
        return "redirect:/khachHang/hien-thi";
    }

    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String trangThai) throws IOException {

        List<KhachHang> danhSach = service.timKiem(keyword, trangThai);
        byte[] excelBytes = exportToExcel(danhSach);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Danh_sach_khach_hang.xlsx");

        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }

    private byte[] exportToExcel(List<KhachHang> danhSach) throws IOException {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Danh sách khách hàng");

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

            String[] headers = {"STT", "Mã KH", "Tên khách hàng", "Email", "SĐT", "Địa chỉ", "Giới tính", "Ngày sinh", "Trạng thái"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }

            int rowNum = 1;
            for (KhachHang kh : danhSach) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(kh.getMaKh() != null ? kh.getMaKh() : "");
                row.createCell(2).setCellValue(kh.getTenKhachHang() != null ? kh.getTenKhachHang() : "");
                row.createCell(3).setCellValue(kh.getEmail() != null ? kh.getEmail() : "");
                row.createCell(4).setCellValue(kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "");
                row.createCell(5).setCellValue(kh.getDiaChi() != null ? kh.getDiaChi() : "");
                row.createCell(6).setCellValue(kh.getGioiTinh() != null ? kh.getGioiTinh() : "");

                String ngaySinh = kh.getNgaySinh() != null ? kh.getNgaySinh().format(dateFormatter) : "";
                row.createCell(7).setCellValue(ngaySinh);

                row.createCell(8).setCellValue(kh.getTrangThai() != null && kh.getTrangThai() ? "Đang hoạt động" : "Ngừng hoạt động");

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