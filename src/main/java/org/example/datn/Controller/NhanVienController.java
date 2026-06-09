package org.example.datn.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datn.Entity.NhanVien;
import org.example.datn.Service.NhanVienService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/nhanVien")
@RequiredArgsConstructor
public class NhanVienController {

    private final NhanVienService nhanVienService;

    @GetMapping("/hien-thi")
    public String hienThi(Model model) {
        model.addAttribute("listNhanVien", nhanVienService.findAll());
        model.addAttribute("nhanVien", new NhanVien());
        return "nhanVien";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("nhanVien") NhanVien nv,
                      BindingResult result,
                      Model model) {

        if (result.hasErrors()) {
            model.addAttribute("listNhanVien", nhanVienService.findAll());
            return "nhanVien";
        }

        nhanVienService.them(nv);
        return "redirect:/nhanVien/hien-thi";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("nhanVien",
                nhanVienService.findById(id).orElse(new NhanVien()));
        model.addAttribute("listNhanVien", nhanVienService.findAll());
        return "nhanVien";
    }

    @PostMapping("/update/{id}")
    public String update(@ModelAttribute("nhanVien") NhanVien nv) {

        nhanVienService.sua(nv);
        return "redirect:/nhanVien/hien-thi";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        nhanVienService.xoaMem(id);
        return "redirect:/nhanVien/hien-thi";
    }
}
