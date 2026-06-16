package org.example.datn.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.datn.Entity.NhanVien;
import org.example.datn.Service.NhanVienService;
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
@RequestMapping("/nhanVien")
@RequiredArgsConstructor
public class NhanVienController {

    private final NhanVienService nhanVienService;

    @GetMapping("/khach-hang")
    public String khachHang(Model model) {
        model.addAttribute("activeMenu", "khachhang");
        return "nhanVien";
    }

    @GetMapping("/doi-trang-thai/{id}")
    public String doiTrangThai(@PathVariable Integer id,
                               RedirectAttributes redirectAttributes) {
        try {
            nhanVienService.doiTrangThai(id);
            redirectAttributes.addFlashAttribute("success",
                    "Cập nhật trạng thái thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Cập nhật trạng thái thất bại");
        }
        return "redirect:/nhanVien/hien-thi";
    }

    @GetMapping("/hien-thi")
    public String hienThi(Model model,
                          @RequestParam(required = false) String hoTen,
                          @RequestParam(required = false) String sdtEmail,
                          @RequestParam(required = false) String loaiGiayTo,
                          @RequestParam(required = false) String soGiayTo,
                          @RequestParam(required = false) String trangThai) {
        List<NhanVien> danhSach = nhanVienService.timKiem(hoTen, sdtEmail, soGiayTo, trangThai);
        model.addAttribute("listNhanVien", danhSach);
        if (!model.containsAttribute("nhanVien")) {
            model.addAttribute("nhanVien", new NhanVien());
        }
        model.addAttribute("activeMenu", "nhanvien");
        return "nhanVien";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("nhanVien") NhanVien nv,
                      BindingResult result,
                      Model model,
                      RedirectAttributes redirectAttributes) {
        nhanVienService.validateThem(nv, result);
        if (result.hasErrors()) {
            model.addAttribute("listNhanVien", nhanVienService.findAll());
            return "nhanVien";
        }
        try {
            nhanVienService.them(nv);
            redirectAttributes.addFlashAttribute("success", "Thêm nhân viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Thêm thất bại: " + e.getMessage());
        }
        return "redirect:/nhanVien/hien-thi";
    }

    // ĐÃ BỎ METHOD detail cũ

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute NhanVien nv,
                         RedirectAttributes redirectAttributes) {
        try {
            nv.setIdNv(id);
            nhanVienService.sua(nv);
            redirectAttributes.addFlashAttribute("success", "Cập nhật nhân viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cập nhật thất bại: " + e.getMessage());
        }
        return "redirect:/nhanVien/hien-thi";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            nhanVienService.xoaMem(id);
            redirectAttributes.addFlashAttribute("success", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Xóa thất bại!");
        }
        return "redirect:/nhanVien/hien-thi";
    }

    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) String hoTen,
            @RequestParam(required = false) String trangThai) throws IOException {

        List<NhanVien> danhSach = nhanVienService.timKiem(hoTen, null, null, trangThai);
        byte[] excelBytes = nhanVienService.exportToExcel(danhSach);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Danh_sach_nhan_vien.xlsx");
        headers.setContentLength(excelBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }
}